package org.fao.geonet.services.publisher;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.Before;
import org.fao.geonet.test.TestConfig;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.ZipFile;


public class GeoFileTest extends TestCase {

    @Override
    @Before
    protected void setUp() throws Exception {
        TestConfig.init();
    }

    @Test
    public void test_getVectorLayers_ZIPWithOneShape() throws IOException {
        String zipName = "zipwithoneshape.zip";
        String zipPath = TestConfig.getResourcesPath() + "/" + zipName;
        GeoFile f = new GeoFile(new File(zipPath));

        // test
        Collection<String> layers = f.getVectorLayers();
        assertEquals(1, layers.size());
        assertEquals("tasmania_cities", layers.iterator().next());
    }

    @Test
    public void test_getVectorLayers_ZIPWithTwoShapes() throws IOException {
        String zipName = "zipwithtwoshapes.zip";
        String zipPath = TestConfig.getResourcesPath() + "/" + zipName;
        GeoFile f = new GeoFile(new File(zipPath));

        // test
        Collection<String> layers = f.getVectorLayers();
        assertEquals(2, layers.size());
        Iterator<String> i = layers.iterator();
        assertEquals("tasmania_cities", i.next());
        assertEquals("tasmania_roads", i.next());
    }

    @Test
    public void test_getLayerFile_ZIPWithOneShape() throws IOException {
        String zipName = "zipwithoneshape.zip";
        String zipPath = TestConfig.getResourcesPath() + "/" + zipName;
        GeoFile f = new GeoFile(new File(zipPath));
        Collection<String> layers = f.getVectorLayers();
        String layer = layers.iterator().next();

        // test
        File zip = f.getLayerFile(layer);
        assertTrue(zip.getName().startsWith("layer_"));
        assertTrue(zip.getName().endsWith(".zip"));
        Boolean gotException = false;
        try {
            new ZipFile(zip);
        } catch(IOException e) {
            gotException = true;
        }
        assertFalse(gotException);
    }

    @Test
    public void test_getLayerFile_ZIPWithTwoShapes() throws IOException {
        String zipName = "zipwithtwoshapes.zip";
        String zipPath = TestConfig.getResourcesPath() + "/" + zipName;
        GeoFile f = new GeoFile(new File(zipPath));
        Collection<String> layers = f.getVectorLayers();
        Iterator<String> i;
        int cnt = 0;

        // test
        for (i = layers.iterator(); i.hasNext();) {
            String layer = i.next();
            File zip = f.getLayerFile(layer);
            assertTrue(zip.getName().startsWith("layer_"));
            assertTrue(zip.getName().endsWith(".zip"));
            Boolean gotException = false;
            try {
                new ZipFile(zip);
            } catch(IOException e) {
                gotException = true;
            }
            assertFalse(gotException);
            cnt++;
        }
        assertEquals(2, cnt);
    }

    @Test
    public void test_getRasterLayers() throws IOException {
        String tifName = "geotif1.tif";
        String tifPath = TestConfig.getResourcesPath() + "/" + tifName;
        GeoFile f = new GeoFile(new File(tifPath));

        // test
        Collection<String> layers = f.getRasterLayers();
        assertEquals(1, layers.size());
        Iterator<String> i = layers.iterator();
        assertEquals("geotif1", i.next());
    }

    @Test
    public void test_getLayerFile_singleGeotif() throws IOException {
        String tifName = "geotif1.tif";
        String tifPath = TestConfig.getResourcesPath() + "/" + tifName;
        GeoFile f = new GeoFile(new File(tifPath));
        Collection<String> layers = f.getRasterLayers();
        String layer = layers.iterator().next();

        // test;
        File tif = f.getLayerFile(layer);
        assertEquals(tifName, tif.getName());     
    }

    @Test
    public void test_getRasterLayers_ZIPWithTwoGeotifs() throws IOException {
        String tifName = "zipwithtwogeotifs.zip";
        String tifPath = TestConfig.getResourcesPath() + "/" + tifName;
        GeoFile f = new GeoFile(new File(tifPath));

        // test
        Collection<String> layers = f.getRasterLayers();
        assertEquals(2, layers.size());
        Iterator<String> i = layers.iterator();
        assertEquals("geotif1", i.next());
        assertEquals("geotif2", i.next());
    }
}
