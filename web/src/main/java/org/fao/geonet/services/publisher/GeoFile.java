package org.fao.geonet.services.publisher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import jeeves.utils.Log;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Instances of this class represent geographic files. A geographic file
 * can be a ZIP file including ESRI Shapefiles and GeoTIFF files, or an
 * individual GeoTIFF file.
 *
 * @author Eric Lemoine, Camptocamp France SAS
 */
public class GeoFile
{
    private ZipFile zipFile = null;
    private File file = null;



    /**
     * Constructs a <code>GeoFile</code> object from a <code>File</code>
     * object.
     *
     * @param f             the file from wich the <code>GeoFile</code>
     *                      object is constructed
     * @throws IOException  if an input/output exception occurs while
     *                      opening a ZIP file
     */
    public GeoFile(File f) throws IOException {
        file = f;
        try {
            zipFile = new ZipFile(file);
        } catch(ZipException e) {
            zipFile = null;
        }
    }

    /**
     * Returns the names of the vector layers (Shapefiles) in the
     * geographic file.
     *
     * @return              a collection of layer names
     */
    public Collection<VectorLayer> getVectorLayers() {
        Set<VectorLayer> layers = new HashSet<VectorLayer>();
        if (zipFile != null) {
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry ze = e.nextElement();
                String fileName = ze.getName();
                VectorLayer layer = create(fileName);
                if(layer!=null) layers.add(layer);
            }
        }
        return layers;
    }
    private static Boolean fileIsShp(String fileName) {
        String extension = getExtension(fileName);
        return extension.equalsIgnoreCase("shp");
    }
    private static Boolean fileIsMif(String fileName) {
        String extension = getExtension(fileName);
        return extension.equalsIgnoreCase("mif");
    }

    public  VectorLayer create(String fileName) {
        if (fileIsShp(fileName)) {
            return new ShpLayer(getBase(fileName));
        } else if (fileIsMif(fileName)) {
            return new MifLayer(getBase(fileName));
        }
        return null;
    }

    /**
     * Returns the names of the raster layers (GeoTIFFs) in the
     * geographic file.
     *
     * @return              a collection of layer names
     */
    public Collection<String> getRasterLayers() {
        Set<String> layers = new HashSet<String>();
        if (zipFile != null) {
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry ze =  e.nextElement();
                String fileName = ze.getName();
                if (fileIsGeotif(fileName)) {
                    layers.add(getBase(fileName));
                }
            }
        } else {
            String fileName = file.getName();
            if (fileIsGeotif(fileName)) {
                layers.add(getBase(fileName));
            }
        }
        return layers;
    }

    /**
     * Returns a file for a given layer, a ZIP file if the layer is a
     * Shapefile, a GeoTIFF file if the layer is a GeoTIFF.
     *
     * @param id            the name of the layer, as returned by the
     *                      getVectorLayer and getRasterLayers methods
     * @return              the file
     * @throws IOException  if an input/output exception occurs while
     *                      constructing a ZIP file
     */
    public File getLayerFile(String id) throws IOException {
        File f = null;
        if (zipFile != null) {
            ZipOutputStream out = null;
            byte[] buf = new byte[1024];
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry ze = e.nextElement();
                String baseName = getBase(ze.getName());
                if (baseName.equals(id)) {
                    if (out == null) {
                        f = File.createTempFile("layer_", ".zip");
                        out = new ZipOutputStream(new FileOutputStream(f));
                    }
                    InputStream in = zipFile.getInputStream(ze);
                    out.putNextEntry(new ZipEntry(ze.getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                    in.close();
                }
            }
            if (out != null) {
                out.close();
            }
        } else {
            f = file;
        }
        return f;
    }

    private static String getExtension(String fileName) {
        return fileName.substring(
            fileName.lastIndexOf(".") + 1, fileName.length());
    }

    private static String getBase(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private Boolean fileIsGeotif(String fileName) {
        String extension = getExtension(fileName);
        return
            extension.equalsIgnoreCase("tif") ||
                extension.equalsIgnoreCase("tiff") ||
                extension.equalsIgnoreCase("geotif") ||
                extension.equalsIgnoreCase("geotiff");
    }

    public void checkZipName() throws IOException {
        Collection<VectorLayer> layers = getVectorLayers();

        String fileName = getBase(file.getName());

        for (VectorLayer layer : layers) {
            if(layer.getName().equals(fileName)) {
                return;
            }
        }

        throw new IOException("wrongName");
    }

    public Integer validateZip(File f) throws IOException {
        Integer epsgCode = null;
        if (zipFile != null && !getVectorLayers().isEmpty()) {
            epsgCode = validatePrj(f);

            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            // verify that there is only one
            String base = getBase(f.getName());
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if(extraFile(base, entry)) {
                    rewriteZip(base);
                    break;
                }
            }

        }

        return epsgCode;
    }

    private Integer validatePrj(File f) throws IOException {
        Integer code;
        String prjFileName = getBase(f.getName())+".prj";

        ZipEntry prjFile = zipFile.getEntry(prjFileName);

        if(prjFile == null) {
            throw new IOException("epsgCodeLookupFailure");
        } else {
            String wkt = readAsString(prjFile);
            try {
                CoordinateReferenceSystem crs = CRS.parseWKT(wkt);

                // shortcut incase we have already a good CRS
                code = CRS.lookupEpsgCode(crs, false);
                if(code != null && !wkt.contains("PARAMETER[\"scale_factor\"")) {
                    return code;
                }

                code = compareToStdCRSs(crs);

                if(code == null) {
                    code = CRS.lookupEpsgCode(crs, true);
                }

                if(code == null) {
                    throw new IOException("epsgCodeLookupFailure");
                }

                CoordinateReferenceSystem newCrs = CRS.decode("EPSG:"+code, true);
                if (!fixWKT(newCrs).equalsIgnoreCase(wkt))
                    writeOutNewPrjFile(getBase(f.getName()), newCrs);
                return code;
            } catch (FactoryException e) {
                throw new IOException(prjFileName+" cannot be parsed: "+e.getMessage());
            }
        }
    }

    private Integer compareToStdCRSs(CoordinateReferenceSystem crs) throws FactoryException {
        int[] epsgCodes = {27562,27572,27572,3948,3948,4326};

        for (int code : epsgCodes) {
            CoordinateReferenceSystem otherCrs = getCRS(code);

            if(CRS.equalsIgnoreMetadata(crs,otherCrs)) {
                return code;
            }
        }

        return null;
    }

    private void rewriteZip(String base) throws IOException {
        writeOutNewPrjFile(base,null);
    }
    private void writeOutNewPrjFile(String base, CoordinateReferenceSystem crsWithEpsgIdentifier) throws IOException {
        String prjFileName = base +".prj";
        File f = File.createTempFile("layer_", ".zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));

        byte[] buf = new byte[1024*1024];
        try {
            for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
                ZipEntry ze = e.nextElement();
                if (extraFile(base, ze) ||
                        (crsWithEpsgIdentifier != null && prjFileName.equalsIgnoreCase(ze.getName()))) {
                    // only allowing 1 shp file for now
                    continue;
                }
                out.putNextEntry(new ZipEntry(ze.getName()));

                InputStream in = zipFile.getInputStream(ze);
                try {
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                } finally {
                    in.close();
                }
                out.closeEntry();
            }

            if(crsWithEpsgIdentifier!=null) {
                out.putNextEntry(new ZipEntry(prjFileName));
                PrintStream printer = new PrintStream(out);
                String wkt = fixWKT(crsWithEpsgIdentifier);

                printer.print(wkt);
                printer.flush();
                out.closeEntry();
            }
        } finally {
            out.close();
        }
        file.delete();
        zipFile.close();
        
        f.renameTo(file);
        zipFile = new ZipFile(file);
    }

    private boolean extraFile(String base, ZipEntry ze) {
        String[] extensions = {".shx", ".dbf", ".prj", ".shp",".qix",".sbx",".sbn",".fix",".mif",".mid"};

        for (String extension : extensions) {
            if(ze.getName().endsWith(extension) && !(base + extension).equalsIgnoreCase(ze.getName())){
                return true;
            }
        }

        return false;
    }

    private String fixWKT(CoordinateReferenceSystem crsWithEpsgIdentifier) {
        String wkt = crsWithEpsgIdentifier.toWKT();

        // The geoserver 1.7.x has an older version of geotools which can't handle scale_factor
        // so I need to remove it from the WKT
        wkt = wkt.replaceAll("PARAMETER\\[\\\"scale_factor\\\", .+?\\],?","");
        return wkt;
    }

    private String readAsString(ZipEntry prjFile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(prjFile)));
        StringBuilder all = new StringBuilder();
        
        String line = reader.readLine();
        while(line != null) {
            all.append(line);
            all.append("\n");
            line = reader.readLine();
        }

        return all.toString();
    }

    private final static Map<Integer,CoordinateReferenceSystem> CRS_CACHE = new HashMap<Integer,CoordinateReferenceSystem>();

    private static synchronized CoordinateReferenceSystem getCRS(int code) throws FactoryException {
        CoordinateReferenceSystem crs = CRS_CACHE.get(code);

        if(crs == null) {
            crs = CRS.decode("EPSG:"+code);
            CRS_CACHE.put(code,crs);
        }
        return crs;
    }


    public void setCRS(File f, CoordinateReferenceSystem crs) throws IOException {
        writeOutNewPrjFile(getBase(f.getName()),crs);
    }

    public void close() {
        try {
            if(zipFile != null)
                zipFile.close();
        } catch (IOException e) {
            Log.error(Log.SERVICE + ".GeoPublish", "Error closing zipfile");
            System.err.println("Error closing zipfile");
            e.printStackTrace();
        }
    }

    abstract class VectorLayer {
         private String name;

        private VectorLayer(String name) {
            this.name = name;
        }

        public String getName() {
            return name;  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            VectorLayer that = (VectorLayer) o;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return name != null ? name.hashCode() : 0;
        }

        public abstract String format();
    }
    private class MifLayer extends VectorLayer {

        private MifLayer(String name) {
            super(name);
        }

        @Override
        public String format() {
            return "mif";
        }
    }
    private class ShpLayer extends VectorLayer {
        private ShpLayer(String name) {
            super(name);
        }

        @Override
        public String format() {
            return "shp";
        }
    }
}
