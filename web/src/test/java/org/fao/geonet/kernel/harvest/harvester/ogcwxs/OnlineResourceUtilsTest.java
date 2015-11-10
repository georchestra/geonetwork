package org.fao.geonet.kernel.harvest.harvester.ogcwxs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.net.URL;
import java.util.List;

import org.jdom.Element;
import org.junit.Test;

import jeeves.utils.Xml;

public class OnlineResourceUtilsTest {

	@Test
	public void removeOnlineResourcesTest() throws Exception {
		URL fixture = this.getClass().getResource("mdd-online-resources.xml");
		assumeTrue(fixture != null);
		Element md = Xml.loadFile(fixture);
		
		int before = OnlineResourceUtils.getOnlineResources(md).size();
		md = OnlineResourceUtils.removeOnlineResources(md, "(?i).*www.geopicardie.fr.*geoserver.*request=getfeature.*");
		int after =  OnlineResourceUtils.getOnlineResources(md).size();

		assertTrue(before > after);
	}
	
	@Test
	public void getSupportedOutputFormatTest() throws Exception {
		URL fixture = this.getClass().getResource("wfs-get-capabilities.xml");
		assumeTrue(fixture != null);
		Element getcap = Xml.loadFile(fixture);	
		
		List<String> of = OnlineResourceUtils.getWfsGetSupportedOutputFormats(getcap, "GetFeature");
		assertTrue(of.size() == 14);
	}
	
	@Test
	public void createOnLineResourceBlockTest() throws Exception {
		
		Element el = OnlineResourceUtils.createOnLineResourceBlock("http://geonetwork.com/geoserver/wfs?", "OGC:WFS", "layername", "layerdesc");
		String toStr = Xml.getString(el);

		assertTrue(toStr.startsWith("<gmd:onLine") &&
				toStr.contains("geonetwork.com/geoserver/wfs?</gmd:URL>") &&
				toStr.contains("OGC:WFS</gco:CharacterString>") &&
				toStr.contains("layername</gco:CharacterString>") &&
				toStr.contains("layerdesc</gco:CharacterString>"));
	}
	
	@Test
	public void extractLayerInfoTest() throws Exception {
		URL fixture = this.getClass().getResource("wfs-get-capabilities.xml");
		assumeTrue(fixture != null);
		Element getcap = Xml.loadFile(fixture);

		// Gets the first feature type
		Element ftl = getcap.getChild("FeatureTypeList", getcap.getNamespace());
		Element ft = (Element) ftl.getChildren().get(0);

		assertTrue(ft != null);
		assertTrue(OnlineResourceUtils.extractLayerInfo(ft, "Name").equals("prefix:layer"));
		assertTrue(OnlineResourceUtils.extractLayerInfo(ft, "Title").equals("My layer title"));
		assertTrue(OnlineResourceUtils.extractLayerInfo(ft, "Abstract").equals("My layer abstract"));
	}
	/**
	 * This test actually tests the whole chain: Getting a MD from the
	 * catalogue, removing the OL given a pattern, then readds the ones
	 * relevant, in regards to the WFS GetCapabilities.
	 * 
	 * @throws Exception
	 */
	@Test
	public void addOnlineResourceBlockTest() throws Exception {
		// need all the supported outputFormats
		URL fixture = this.getClass().getResource("wfs-get-capabilities.xml");
		assumeTrue(fixture != null);
		Element getcap = Xml.loadFile(fixture);	
		List<String> of = OnlineResourceUtils.getWfsGetSupportedOutputFormats(getcap, "GetFeature");

		// need a MD for testing
		URL myMdUrl = this.getClass().getResource("mdd-online-resources.xml");
		assumeTrue(myMdUrl != null);
		Element md = Xml.loadFile(myMdUrl);
		int numolbefore = OnlineResourceUtils.getOnlineResources(md).size();
		// Removes the OnlineResources related to WFS download
		md = OnlineResourceUtils.removeOnlineResources(md, "(?i).*www.geopicardie.fr.*geoserver.*request=getfeature.*");

		// iterates on each outputformats to add an onlineresource
		for (String format: of) {
			Element newb = OnlineResourceUtils.createOnLineResourceBlock("www.geopicardie.fr/geoserver?service=WFS&request=GetFeature&outputFormat="+format,
					"WWW:LINK-1.0-http--link", "myLayer", "My test layer description");
			md = OnlineResourceUtils.insertOnLineResourceBlock(md, newb);
		}
		int numolafter = OnlineResourceUtils.getOnlineResources(md).size();

		assertTrue(numolbefore == 4 && numolafter == 17);
	}
}
