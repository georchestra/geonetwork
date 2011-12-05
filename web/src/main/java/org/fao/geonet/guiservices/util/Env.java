//=============================================================================
//===	Copyright (C) 2001-2007 Food and Agriculture Organization of the
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

package org.fao.geonet.guiservices.util;

import java.util.Map;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.setting.SettingManager;
import org.jdom.Element;

//=============================================================================

/** This service returns some usefull information about GeoNetwork
  */

public class Env implements Service
{
	private boolean downloadFormEnabled = false;
	private String  downloadFormPdfUrl = "";
	
	public void init(String appPath, ServiceConfig params) throws Exception {
		downloadFormEnabled = params.getValue("dlform.activated").equalsIgnoreCase("true");
		downloadFormPdfUrl = params.getValue("dlform.pdf_url");
	}

	//--------------------------------------------------------------------------
	//---
	//--- Service
	//---
	//--------------------------------------------------------------------------

	public Element exec(Element params, ServiceContext context) throws Exception
	{
		GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);

        String  xslPath = context.getAppPath() + Geonet.Path.STYLESHEETS+ "/xml";
		Element system  = gc.getSettingManager().get("system", -1);

		try {
			Element spHeaders = new Element("security-proxy");
			Map<String, String> headers = context.getHeaders();
			for (String h : headers.keySet()) {
				if (h.toLowerCase().startsWith("sec-")) {
					spHeaders.addContent(new Element(h).setText(headers.get(h)));
				}
			}
			
			system.addContent(spHeaders);
			
			Element dlform = new Element("downloadform");
			dlform.addContent(new Element("activated").setText(downloadFormEnabled == true ? "true" : "false"));
			dlform.addContent(new Element("pdf_url").setText(downloadFormPdfUrl));
			
			system.addContent(dlform);
			
			
		} catch (Exception e) {}
		
		return Xml.transform(system, xslPath +"/env.xsl");
	}
}

//=============================================================================

