package org.fao.geonet.kernel.harvest.harvester.ogcwxs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fao.geonet.constants.Geonet;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.Text;
import org.jdom.xpath.XPath;

import jeeves.utils.Xml;

public class OnlineResourceUtils {

	/**
	 * Removes the online resources from the provided document, given a match passed
	 * as arguments.
	 * 
	 * @param urlPattern removes the gmd:onLine block if the host url matches
	 *
	 * @return the MD with the matched online resources removed
	 * @throws JDOMException 
	 */
	public static Element removeOnlineResources(Element md, String urlPattern) throws JDOMException {
		Pattern pat = Pattern.compile(urlPattern);
		List<Element> onlineRes = getOnlineResources(md);
		for (Element ol : onlineRes) {
			String url = getOnlineResourcesUrl(ol);
			Matcher m = pat.matcher(url);
			if (m.matches()) {
				ol.detach();
				md.removeContent(ol);
			}
		}
		return md;
	}
	
	/**
	 * Returns every gmd:onLine blocks that can be found in the given metadata.
	 *
	 * @param md the original metadata.
	 * @return a list of elements.
	 * @throws JDOMException
	 */
	public static List<Element> getOnlineResources(Element md) throws JDOMException {
		XPath x = XPath.newInstance("./gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine");
		x.addNamespace(Geonet.Namespaces.GMD);
		return x.selectNodes(md);
	}
	/**
	 * Given a gmd:onLine element, traverses the XML element to get and return the string representing the URL.
	 *
	 * @param the Element block gmd:onLine
	 * @return the string describing the URL
	 * @throws JDOMException
	 */
	public static String getOnlineResourcesUrl(Element ol) throws JDOMException {
		XPath pathUrl = XPath.newInstance("./gmd:CI_OnlineResource/gmd:linkage/gmd:URL");
		pathUrl.addNamespace(Geonet.Namespaces.GMD);
		Element urlElem = (Element) pathUrl.selectSingleNode(ol);
		return urlElem.getText();
	}

	/**
	 * Given a GetCapabilities response, returns the allowed values supported for a given operation. 
	 * @param getcap The WFS GetCapabilities response
	 * @param operation the Operation
	 * @throws JDOMException 
	 * 
	 */
	public static List<String> getWfsGetSupportedOutputFormats(Element getcap, String operation) throws JDOMException {
		List<String> of = new ArrayList<String>();
		// /wfs:WFS_Capabilities/ows:OperationsMetadata/ows:Operation/ows:Parameter/ows:AllowedValues/ows:Value
		// or
		// /wfs:WFS_Capabilities/ows:OperationsMetadata/ows:Operation/ows:Parameter
		// depending on the version
		XPath xp = XPath.newInstance("./ows:OperationsMetadata/ows:Operation[@name='"+ operation +"']/ows:Parameter[@name='outputFormat']//ows:Value/text()");
		xp.addNamespace(getcap.getNamespace("ows"));
		xp.addNamespace(getcap.getNamespace());
		List<Text> ls = xp.selectNodes(getcap);
		for(Text t: ls) {
			of.add(t.getValue());
		}
		return of;		
	}

	/**
	 * Given a URL, a protocol, a layer name and a description, generates a new gmd:onLine XML block
	 * to be inserted in a iso19139 metadata.
	 *
	 * @param url
	 * @param protocol
	 * @param layername
	 * @param layerdesc
	 *
	 * @return an Element block representing the <gmd:onLine /> block.
	 */
	public static Element createOnLineResourceBlock(String url, String protocol, String layername, String layerdesc) {
		Element el = new Element("onLine", Geonet.Namespaces.GMD);
		el.addContent(new Element("CI_OnlineResource", Geonet.Namespaces.GMD)
				.addContent(new Element("linkage", Geonet.Namespaces.GMD)
						.addContent(new Element("URL", Geonet.Namespaces.GMD).setText(url)))
				.addContent(new Element("protocol", Geonet.Namespaces.GMD)
						.addContent(new Element("CharacterString", Geonet.Namespaces.GCO).setText(protocol)))
				.addContent(new Element("name", Geonet.Namespaces.GMD)
						.addContent(new Element("CharacterString", Geonet.Namespaces.GCO).setText(layername)))
				.addContent(new Element("description", Geonet.Namespaces.GMD)
						.addContent(new Element("CharacterString", Geonet.Namespaces.GCO).setText(layerdesc)))

		);
		return el;
	}
	/**
	 * Given a metadata and an gmd:onLine resource block, inserts the block into the metadata.
	 * @throws JDOMException 
	 */
	public static Element insertOnLineResourceBlock(Element md, Element block) throws JDOMException {
		XPath x = XPath.newInstance("./gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions");
		x.addNamespace(Geonet.Namespaces.GMD);
		
		Element parent = (Element) x.selectSingleNode(md);
		parent.addContent(block);
		return md;
	}

	/**
	 * Given a layer and an element name, returns the value of the element name in the layer block.
	 *
	 * @param layer
	 * @param elementName
	 * @return
	 * @throws JDOMException
	 */
	public static String extractLayerInfo(Element layer, String elementName) throws JDOMException {
		String dummyNsPrefix = "";
		if (!layer.getNamespace().equals(Namespace.NO_NAMESPACE)) {
			dummyNsPrefix = "x:";
		}
		XPath nameXp = XPath.newInstance(String.format("./%s%s/text()", dummyNsPrefix, elementName));
		if (!layer.getNamespace().equals(Namespace.NO_NAMESPACE)) {
			nameXp.addNamespace("x", layer.getNamespace().getURI());
		}
		Text t = (Text) nameXp.selectSingleNode(layer);
		return t.getValue();
	}
}

