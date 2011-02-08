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
 * Instances of this class represent GeoServer coverage stores. This
 * class uses GeoServer's management HTTP APIs for creating, updating
 * and deleting a coverage store.
 *
 * @author Éric Lemoine, Camptocamp France SAS
 */
public class GeoServerCoverageStore {

    private final String url;
    private final HttpClientFactory httpClientFactory;

    /**
     * Constructs a representation of a coverage store.
     *
     * @param httpClientFactory         the HTTP client factory to use for
     *                                  constructing HTTP clients
     * @param baseURL                   the URL to the GeoServer management
     *                                  service
     * @param cs                        the name of the coverage store
     * @param ws                        the namespace
     */
    GeoServerCoverageStore(final HttpClientFactory httpClientFactory,
                           final String baseURL,
                           final String cs,
                           final String ws) {
        this.httpClientFactory = httpClientFactory;
        this.url = baseURL +
            "/workspaces/" + ws + "/coveragestores/" + cs;
    }

    /**
     * Creates a coverage store in GeoServer from a GeoTIFF file.
     *
     * @param f                         the GeoTIFF file
     * @throws IOException              thrown if the status code of the GeoServer
     *                                  response is not 201
     */
    public void create(File f) throws IOException {
        final HttpClient c = httpClientFactory.newHttpClient();
        String uri = this.url + "/file.geotiff";
        final PutMethod m = new PutMethod(uri);
        m.setRequestEntity(new InputStreamRequestEntity(new FileInputStream(f)));
        m.setRequestHeader("Content-type", "image/tiff");
        m.setDoAuthentication(true);
        int status = c.executeMethod(m);
        if (status > 201) {
            throw new IOException("got status code " + status);
        }
    }

    /**
     * Gets the XML representation of the coverage store from GeoServer.
     *
     * @return                          the XML representation
     * @throws IOException              thrown if the status code of the GeoServer
     *                                  response is not 200
     */
    public String read() throws IOException {
        final HttpClient c = httpClientFactory.newHttpClient();
        final GetMethod m = new GetMethod(this.url + ".xml");
        m.setDoAuthentication(true);
        int status = c.executeMethod(m);
        if (status != 200) {
            throw new IOException("got status code " + status);
        }
        return m.getResponseBodyAsString();
    }

    /**
     * Updates a coverage store into GeoServer from a GeoTIFF file.
     *
     * @param f                         the GeoTIFF file
     * @throws IOException              thrown if the status code of the GeoServer
     *                                  response is not 201
     */
    public void update(File f) throws IOException {
        create(f);
    }

    /**
     * Deletes a coverage store from GeoServer, the method deletes the coverage
     * first and then the coverage store.
     *
     * @param cov                       the name of the coverage store to delete
     * @throws IOException              throws if the status code of the GeoServer
     *                                  response is not 200 when deleting the
     *                                  coverage or the coverage store
     */
    public void delete(String cov) throws IOException {
        int status;
        HttpClient c;
        DeleteMethod m;
        // delete feature type
        c = httpClientFactory.newHttpClient();
        m = new DeleteMethod(this.url + "/coverages/" + cov);
        m.setDoAuthentication(true);
        status = c.executeMethod(m);
        if (status != 200) {
            throw new IOException("got status code " + status);
        }
        // delete data store
        c = httpClientFactory.newHttpClient();
        m = new DeleteMethod(this.url);
        m.setDoAuthentication(true);
        status = c.executeMethod(m);
        if (status != 200) {
            throw new IOException("got status code " + status);
        }
    }

    /**
     * Deletes a coverage store from GeoServer, the method deletes the coverage
     * first and then the coverage store.
     *
     * @param f                         the file representing the coverage store
     *                                  to delete.
     * @throws IOException              throws if the status code of the GeoServer
     *                                  response is not 200 when deleting the
     *                                  coverage or the coverage store
     */
    public void delete(File f) throws IOException {
        String cov = f.getName().substring(0, f.getName().lastIndexOf("."));
        delete(cov);
    }

}
