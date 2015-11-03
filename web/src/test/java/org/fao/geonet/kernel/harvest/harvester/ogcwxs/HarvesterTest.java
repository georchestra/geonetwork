package org.fao.geonet.kernel.harvest.harvester.ogcwxs;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.jdom.Element;
import org.jdom.filter.Filter;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.util.ReflectionUtils;



public class HarvesterTest {

	private Element prepareFeatureTypeFragment(URL fixture, String rebasedUrl) throws Exception {
		Element featureType = Xml.loadFile(fixture);
		// Adapt the MetadataURL to point to the test resources
		Filter filter = new Filter() {
			private static final long serialVersionUID = 3236424199969896193L;
			@Override
			public boolean matches(Object obj) {
				if (obj instanceof Element) {
					Element elem = (Element) obj;
					return elem.getName().equals("MetadataURL");
				}
				return false;
			}
		};
		// Need 2 passes, because of ConcurrentModificationException
		List<Element> subList = new ArrayList<Element>();		
		Iterator<Object> metadataUrlsElem = featureType.getDescendants(filter);
		while (metadataUrlsElem.hasNext()) {
			subList.add((Element) metadataUrlsElem.next());
		}
		for (Element e: subList) {
			if (rebasedUrl != null) {
				e.setText(rebasedUrl);
			} else
				// detach the element
				e.getParentElement().removeContent(e);
		}
		return featureType;
	}
	
	@Test
	public void extractWfs110MdUrlTest() throws Exception {
		URL fixture = this.getClass().getResource("wfs-featuretype-fragment.xml");
		assumeNotNull(fixture);
		URL mdUrl  = this.getClass().getResource("md.xml");
		assumeNotNull(mdUrl);
		Element featureType = prepareFeatureTypeFragment(fixture, mdUrl.toString());
		ServiceContext ctx = Mockito.mock(ServiceContext.class);
		Mockito.when(ctx.getHandlerContext(Mockito.anyString())).thenReturn(Mockito.mock(GeonetContext.class));
		Harvester h = new Harvester(null, ctx, null, null);
		Method m = ReflectionUtils.findMethod(h.getClass(), "getWfsMdFromMetadataUrl", Element.class);
		m.setAccessible(true);
		
		Element xml = (Element) ReflectionUtils.invokeMethod(m, h, featureType);

		String titlePattern = "Activités économiques de Picardie - sites (2012)";
		assertTrue(String.format(
				"Expected '%s' as MD title, not found in the remote document",
				titlePattern), Xml.getString(xml).contains(titlePattern));
	}
	
	@Test
	public void extractWfs110NoMdUrlTest() throws Exception {
		URL fixture = this.getClass().getResource("wfs-featuretype-fragment.xml");
		assumeNotNull(fixture);
		Element featureType = prepareFeatureTypeFragment(fixture, null);
		ServiceContext ctx = Mockito.mock(ServiceContext.class);
		Mockito.when(ctx.getHandlerContext(Mockito.anyString())).thenReturn(Mockito.mock(GeonetContext.class));
		Harvester h = new Harvester(null, ctx, null, null);
		Method m = ReflectionUtils.findMethod(h.getClass(), "getWfsMdFromMetadataUrl", Element.class);
		m.setAccessible(true);

		boolean noMdUrlFoundCaught = false;
		try {
			Element xml = (Element) ReflectionUtils.invokeMethod(m, h, featureType);
		} catch (Throwable e) {
			Throwable e1 = e.getCause();
			noMdUrlFoundCaught = e1.getMessage().contains("not found in the WFS XML fragment");
		}
		assertTrue("No exception caught, expected one (no MdUrl found)", noMdUrlFoundCaught);
	}
	
	@Test
	public void extractWfs110MdUrlFileNotFoundTest() throws Exception {
		URL fixture = this.getClass().getResource("wfs-featuretype-fragment.xml");
		URL fileNotfound = new URL("file:/this/file/does/not/exist.xml");
		assumeNotNull(fixture);
		assumeTrue(! new File(fileNotfound.toURI()).exists());

		Element featureType = prepareFeatureTypeFragment(fixture, fileNotfound.toString());
		ServiceContext ctx = Mockito.mock(ServiceContext.class);
		Mockito.when(ctx.getHandlerContext(Mockito.anyString())).thenReturn(Mockito.mock(GeonetContext.class));
		Harvester h = new Harvester(null, ctx, null, null);
		Method m = ReflectionUtils.findMethod(h.getClass(), "getWfsMdFromMetadataUrl", Element.class);
		m.setAccessible(true);

		boolean fileNotFoundExCaught = false;
		try {
			Element xml = (Element) ReflectionUtils.invokeMethod(m, h, featureType);
		} catch (Throwable e) {
			Throwable e1 = e.getCause();
			fileNotFoundExCaught = e1 instanceof FileNotFoundException;
		}
		assertTrue("No exception caught, expected one (no MdUrl found)", fileNotFoundExCaught);
	}	
}
