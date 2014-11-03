package org.fao.geonet.services.mef;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import java.io.File;
import java.lang.reflect.Field;

import javax.xml.transform.TransformerFactory;

import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.TransformerFactoryFactory;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;



public class ImportWmcTest {

    private ImportWmc importWmcService;
    private ServiceConfig serviceConfig = Mockito.mock(ServiceConfig.class);
    private ServiceContext serviceContext = Mockito.mock(ServiceContext.class);

    private static String testWmcString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ViewContext xmlns=\"http://www.opengis.net/context\" version=\"1.1.0\" id=\"e77dfc89\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/context http://schemas.opengis.net/context/1.1.0/context.xsd\"><General><Window width=\"1373\" height=\"810\"/><BoundingBox minx=\"-1363723.31702789990\" miny=\"4981331.59563689958\" maxx=\"1994613.95770959998\" maxy=\"6962579.36878869962\" SRS=\"EPSG:3857\"/><Title>aaa</Title><Abstract>ddd</Abstract><Extension><ol:maxExtent xmlns:ol=\"http://openlayers.org/context\" minx=\"-20037508.3399999999\" miny=\"-20037508.3399999999\" maxx=\"20037508.3399999999\" maxy=\"20037508.3399999999\"/></Extension></General><LayerList><Layer queryable=\"0\" hidden=\"0\"><Server service=\"OGC:WMS\" version=\"1.1.1\"><OnlineResource xlink:type=\"simple\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"http://osm.geobretagne.fr/service/wms\"/></Server><Name>osm:google</Name><Title>OpenStreetMap</Title><MetadataURL><OnlineResource xlink:type=\"simple\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"http://wiki.openstreetmap.org/wiki/FR:OpenStreetMap_License\"/></MetadataURL><sld:MinScaleDenominator xmlns:sld=\"http://www.opengis.net/sld\">266.5911979812228</sld:MinScaleDenominator><sld:MaxScaleDenominator xmlns:sld=\"http://www.opengis.net/sld\">559082264.0287180</sld:MaxScaleDenominator><FormatList><Format current=\"1\">image/png</Format></FormatList><StyleList><Style><Name/><Title>Default</Title></Style></StyleList><Extension><ol:maxExtent xmlns:ol=\"http://openlayers.org/context\" minx=\"-20037508.3399999999\" miny=\"-20037508.3399999999\" maxx=\"20037508.3399999999\" maxy=\"20037508.3399999999\"/><ol:tileSize xmlns:ol=\"http://openlayers.org/context\" width=\"256\" height=\"256\"/><ol:numZoomLevels xmlns:ol=\"http://openlayers.org/context\">22</ol:numZoomLevels><ol:units xmlns:ol=\"http://openlayers.org/context\">m</ol:units><ol:isBaseLayer xmlns:ol=\"http://openlayers.org/context\">false</ol:isBaseLayer><ol:displayInLayerSwitcher xmlns:ol=\"http://openlayers.org/context\">true</ol:displayInLayerSwitcher><ol:singleTile xmlns:ol=\"http://openlayers.org/context\">false</ol:singleTile><ol:transitionEffect xmlns:ol=\"http://openlayers.org/context\">resize</ol:transitionEffect><ol:attribution xmlns:ol=\"http://openlayers.org/context\"><Title>GéoBretagne / OSM</Title><OnlineResource xlink:type=\"simple\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"http://www.openstreetmap.org/\"/><LogoURL width=\"100\" height=\"100\" format=\"image/png\"><OnlineResource xlink:type=\"simple\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:href=\"http://upload.wikimedia.org/wikipedia/commons/thumb/b/b0/Openstreetmap_logo.svg/100px-Openstreetmap_logo.svg.png\"/></LogoURL></ol:attribution></Extension></Layer></LayerList></ViewContext>";
    private static String testWmcUrl = "http://sdi.georchestra.org/mapfishapp/ws/wmc/geodoc939c9df8121e7953b23a39c22f5b2bdb.wmc";
    private static String testWmcViewerUrl = "http://sdi.georchestra.org/mapfishapp/?wmc=ws/wmc/geodoc939c9df8121e7953b23a39c22f5b2bdb.wmc";

    private boolean testEnabled = false;

    @Before
    public void setUp() throws Exception {
        if(System.getenv("GN_HOME") == null) {
            return;
        }
        testEnabled = true;
        importWmcService = new ImportWmc();
        importWmcService.init(System.getenv("GN_HOME"), serviceConfig);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testImportWmc() throws Exception {
        // test is enabled only if a GN_HOME with the path is defined
        assumeTrue(testEnabled);

        // The stylesheet should exist in the filesystem
        Field styleSheet = ImportWmc.class.getDeclaredField("styleSheetWmc");
        styleSheet.setAccessible(true);
        String xslPath =  (String) styleSheet.get(importWmcService);
        assertTrue(new File(xslPath).exists());

        Document doc = new Document();
        Element reqElem = new Element("request");
        reqElem.addContent(new Element("wmc_string").setText(testWmcString));
        reqElem.addContent(new Element("wmc_url").setText(testWmcUrl));
        reqElem.addContent(new Element("viewer_url").setText(testWmcViewerUrl));
        doc.addContent(reqElem);

        // Eclipse embedded JUnit will select an incompatible transformer factory
        // We force it to use net.sf.saxon
        TransformerFactoryFactory.setTransformerFactory(TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",null));

        importWmcService.serviceSpecificExec(reqElem, serviceContext);

    }

}
