//=============================================================================
//===	Copyright (C) 2009 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.geonet.services.publisher;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.fao.geonet.lib.Lib;
import org.jdom.Element;

/** 
  */
public class Do implements Service {
	/**
	 * Module name
	 */
	private static String module = "GeoServerPublisher";

	/**
	 * XML document containing Geoserver node configuration defined in
	 * geoserver-nodes.xml
	 */
	private Element geoserverConfig;

	/**
	 * List of current known nodes
	 */
	private HashMap<String, GeoserverNode> geoserverNodes = new HashMap<String, GeoserverNode>();

	/**
	 * Error code received when publishing
	 */
	private String errorCode = "";

	/**
	 * Report return by a read action
	 */
	private String report = "";

	private String getReport() {
		return report;
	}

	private void setReport(String report) {
		this.report = report;
	}

	private String getErrorCode() {
		return errorCode;
	}

	private void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Load configuration file and register remote nodes. In order to register
	 * new nodes, restart is needed.
	 * 
	 */
	public void init(String appPath, ServiceConfig params) throws Exception {
		Log.createLogger(module);

		// Load configuration
		String geoserverConfigFile = appPath
				+ params.getValue("configFile", "");

		Log.info(module, "Using configuration: " + geoserverConfigFile);

		geoserverConfig = Xml.loadFile(geoserverConfigFile);
		if (geoserverConfig == null) {
			Log.error(module, "Failed to load geoserver configuration file "
					+ geoserverConfigFile);
			return;
		}

		// Read configuration and register node
		Log.debug(module, "Start node registration");
		Collection<Element> nodes = geoserverConfig.getChildren("node");
		for (Element node : nodes) {
			// TODO : check mandatory values and reject node when relevant
			String id = node.getChildText("id");
			String name = node.getChildText("name");
			Log.debug(module, "  Register node:" + name);
			String url = node.getChildText("adminurl");
			String namespacePrefix = node.getChildText("namespacePrefix");
			String namespaceUrl = node.getChildText("namespaceUrl");
			String user = node.getChildText("user");
			String password = node.getChildText("password");

			GeoserverNode g = new GeoserverNode(id, name, url, namespacePrefix,
					namespaceUrl, user, password);

			if (g != null)
				geoserverNodes.put(id, g);
		}
		Log.debug(module, "End node registration.");
	}

	/**
	 * List of action valid for publisher service
	 */
	private enum ACTION {
		/**
		 * Return list of nodes
		 */
		LIST, CREATE, UPDATE, DELETE, GET
	};

	/**
	 * Publish a dataset to a remote GeoServer node. Dataset could be a ZIP
	 * composed of Shapefile(s) or GeoTiff.
	 * 
	 * updataMetadataRecord, add or delete a online source link.
	 */
	public Element exec(Element params, ServiceContext context)
			throws Exception {
	    
		ACTION action = ACTION.valueOf(Util.getParam(params, "action"));
		if (action.equals(ACTION.LIST)) {
			return list();
		} else if (action.equals(ACTION.CREATE) || action.equals(ACTION.UPDATE)
				|| action.equals(ACTION.DELETE) || action.equals(ACTION.GET)) {

			// Check parameters
			String nodeId = Util.getParam(params, "nodeId");
			String metadataId = Util.getParam(params, "metadataId");
			GeoserverNode g = geoserverNodes.get(nodeId);
			if (g == null)
				throw new IllegalArgumentException(
						"Invalid node id "
								+ nodeId
								+ ". Can't find node id in current registered nodes. Use action=LIST parameter to retrieve the list of valid nodes.");

			String zipFile = Util.getParam(params, "zip");
			String access = Util.getParam(params, "access");
			String updateMetadataRecord = Util.getParam(params,
					"updateMetadata", "n");

			// Get ZIP file from data directory
			File dir = new File(Lib.resource
					.getDir(context, access, metadataId));
			File f = new File(dir, zipFile);
			if (f == null)
				throw new IllegalArgumentException(
						"Could not find dataset file. Invalid zip file parameters: "
								+ zipFile + ".");

			// TODO Handle multiple geofile.  Not at all tested or supported really
			GeoFile gf = new GeoFile(f);
            try {
            if(gf.getRasterLayers().isEmpty() && gf.getVectorLayers().isEmpty()) {
                throw new IOException("noLayers");
            }
			Collection<GeoFile.VectorLayer> vectorLayers = gf.getVectorLayers();
			if (vectorLayers.size() > 0) {
			    boolean update = ("n".equals(updateMetadataRecord) ? false : true);
				if (publishVector(gf,f, g, action, update)) {
					Element report = new Element("Success");
					report.setText(getReport());
					return report;
				} else {
					Element report = new Element("Exception");
					report.setAttribute("status", getErrorCode());
					return report;
				}
			}

			Collection<String> rasterLayers = gf.getRasterLayers();
			if (rasterLayers.size() > 0) {
			    boolean update = ("n".equals(updateMetadataRecord) ? false : true);
				if (publishRaster(f, g, action, update) ) {
					Element report = new Element("Success");
					report.setText(getReport());
					return report;
				} else {
					Element report = new Element("Exception");
					report.setAttribute("status", getErrorCode());
					return report;
				}
			}

        } finally {
            gf.close();
        }

			// FIXME : GeoFile create one archive with temp name
			// for each file contained in a zip file.
			// Then dataset published to GeoServer node will have
			// this name and could not be retrieve from initial filename.
			// Could we use Shapefile or GeoTiff filename instead ?
			//			
			//			
			// // Search and publish vector layers
			// Collection<String> vectorLayers = gf.getVectorLayers();
			// for (String vectorLayer : vectorLayers) {
			// File vf = gf.getLayerFile(vectorLayer);
			// if (publishVector(vf, g, action, ("n"
			// .equals(updateMetadataRecord) ? false : true))) {
			// Element report = new Element("Success");
			// report.setText(getReport());
			// return report;
			// } else {
			// Element report = new Element("Exception");
			// report.setAttribute("status", getErrorCode());
			// return report;
			// }
			// }
			//
			// // Then raster
			// Collection<String> rasterLayers = gf.getRasterLayers();
			// for (String rasterLayer : rasterLayers) {
			// File rf = gf.getLayerFile(rasterLayer);
			// if (publishVector(rf, g, action, ("n"
			// .equals(updateMetadataRecord) ? false : true))) {
			// Element report = new Element("Success");
			// report.setText(getReport());
			// return report;
			// } else {
			// Element report = new Element("Exception");
			// report.setAttribute("status", getErrorCode());
			// return report;
			// }
			// }

		}
		return null;
	}

	/**
	 * 
	 *
     * @param gf
     * @param f
     *            File to publish
     * @param g
*            Publish the content of the zip file to that node
     * @param action
*            Type of action CREATE/UPDATE/DELETE
     */
	private boolean publishVector(GeoFile gf, File f, GeoserverNode g, ACTION action,
                                  boolean updateMetadataRecord) {
		Log.debug(module, "Publish: " + action.toString() + " " + f.getName()
				+ " in " + g.getId());

		HttpClientFactory httpClientFactory = new HttpClientFactory(g
				.getUsername(), g.getUserpassword());

		String ds = f.getName();
		GeoServerDataStore p = new GeoServerDataStore(httpClientFactory, g
				.getUrl(), ds.substring(0, ds.indexOf(".")), g
				.getNamespacePrefix());
		try {
            if(action.equals(ACTION.CREATE) || action.equals(ACTION.UPDATE)) {
                gf.checkZipName();
            }
			if (action.equals(ACTION.CREATE))
				p.create(f);
			else if (action.equals(ACTION.UPDATE))
				p.update(f);
			else if (action.equals(ACTION.DELETE))
				p.delete(f);

			setReport(p.read());
			return true;

		} catch (IOException e) {
			setErrorCode(e.getMessage());
			System.out.println("IOException " + e.getMessage());
		}
		return false;
	}

	private boolean publishRaster(File f, GeoserverNode g, ACTION action,
			boolean updateMetadataRecord) {
		Log.debug(module, "Publish: " + action.toString() + " " + f.getName()
				+ " in " + g.getId());

		HttpClientFactory httpClientFactory = new HttpClientFactory(g
				.getUsername(), g.getUserpassword());

		String ds = f.getName();
		String dsName = ds.substring(0, ds.indexOf("."));
		GeoServerCoverageStore p = new GeoServerCoverageStore(
				httpClientFactory, g.getUrl(), dsName, g.getNamespacePrefix());
		try {
			if (action.equals(ACTION.CREATE)) {
				p.create(f);
			} else if (action.equals(ACTION.UPDATE)) {
				p.update(f);
			} else if (action.equals(ACTION.DELETE)) {
				p.delete(ds.substring(0, ds.indexOf(".")));
			}
			setReport(p.read());
			return true;

		} catch (IOException e) {
			setErrorCode(e.getMessage());
			System.out.println("IOException " + e.getMessage());
		}
		return false;
	}

	/**
	 * Return list of registered node
	 * 
	 * @return
	 */
	private Element list() {
		return geoserverConfig;
	}
}