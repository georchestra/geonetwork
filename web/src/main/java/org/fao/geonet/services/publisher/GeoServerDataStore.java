package org.fao.geonet.services.publisher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PutMethod;

/**
 * Instances of this class represent GeoServer data stores. This
 * class uses GeoServer's management HTTP APIs for creating, updating
 * and deleting a data store.
 *
 * @author Éric Lemoine, Camptocamp France SAS
 */
public class GeoServerDataStore {

    private final String dataStoreUrl;
    private String featureStoreUrl;
    private final HttpClientFactory httpClientFactory;


    /**
     * Constructs a representation of a data store.
     *
     * @param httpClientFactory         the HTTP client factory to use for
     *                                  constructing HTTP clients
     * @param baseURL                   the URL to the GeoServer management
     *                                  service
     * @param ds                        the name of the data store
     * @param ws                        the namespace
     */
    GeoServerDataStore(final HttpClientFactory httpClientFactory,
                       final String baseURL,
                       final String ds,
                       final String ws) {
        this.httpClientFactory = httpClientFactory;
        this.dataStoreUrl = baseURL + "/workspaces/" + ws + "/datastores/" + ds;
        this.featureStoreUrl = baseURL + "/workspaces/" + ws + "/datastores/" + ds + "/featuretypes/" + ds + ".xml";
    }

    /**
     * Creates a data store in GeoServer from a Shapefile.
     *
     * @param f                         the Shapefile
     * @throws IOException              thrown if the status code of the GeoServer
     *                                  response is not 201
     */
    public void create(File f) throws IOException {
        GeoFile geoFile = new GeoFile(f);
        geoFile.validateZip(f);
        String format = geoFile.getVectorLayers().iterator().next().format();

        final HttpClient c = httpClientFactory.newHttpClient();
        String uri = this.dataStoreUrl + "/file."+format;
        final PutMethod m = new PutMethod(uri);
        m.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(f)));
        m.setRequestHeader("Content-type", "application/zip");
        m.setDoAuthentication(true);
        int status = c.executeMethod(m);

        if (status > 201) {
            // hack to work around occasional problem where the first upload does not correctly rename the file
            status = c.executeMethod(m);
            if (status > 201) {
                throw new IOException("got status code " + status);
            }
        }
    }

    /**
     * Gets the XML representation of the data store from GeoServer.
     *
     * @return                          the XML representation
     * @throws IOException              thrown if the status code of the GeoServer
     *                                  response is not 200
     */
    public String read() throws IOException {
        final HttpClient c = httpClientFactory.newHttpClient();
        final GetMethod m = new GetMethod(this.dataStoreUrl + ".xml");
        m.setDoAuthentication(true);
        int status = c.executeMethod(m);
        if (status != 200) {
            throw new IOException("got status code " + status);    
        }
        return m.getResponseBodyAsString();
    }

    /**
     * Updates a data store in GeoServer from a Shapefile.
     *
     * @param f                         the Shapefile
     * @throws IOException              thrown if the status code of the GeoServer
     *                                  response is not 201
     */
    public void update(File f) throws IOException {
        create(f);
    }

    /**
     * Deletes a data store from GeoServer, the method deletes the feature type
     * first and then the data store.
     *
     * @param ft                        the name of the data store to delete
     * @throws IOException              throws if the status code of the GeoServer
     *                                  response is not 200 when deleting the
     *                                  coverage or the coverage store
     */
    public void delete(String ft) throws IOException {
        int status;
        HttpClient c;
        DeleteMethod m;
        // delete feature type
        c = httpClientFactory.newHttpClient();
        m = new DeleteMethod(this.dataStoreUrl + "/featuretypes/" + ft);
        m.setDoAuthentication(true);
        status = c.executeMethod(m);
        System.out.println(m.getURI().toString());

        // delete data store
        c = httpClientFactory.newHttpClient();
        m = new DeleteMethod(this.dataStoreUrl);
        m.setDoAuthentication(true);
        status = c.executeMethod(m);
        if (status != 200) {
            throw new IOException("got status code " + status);
        }
    }

    /**
     * Deletes a data store from GeoServer, the method deletes the feature type
     * first and then the data store.
     *
     * @param f                         the file representing the data store
     * @throws IOException              throws if the status code of the GeoServer
     *                                  response is not 200 when deleting the
     *                                  coverage or the coverage store
     */
    public void delete(File f) throws IOException {
        String ft = f.getName().substring(0, f.getName().lastIndexOf("."));
        delete(ft);
    }

}
