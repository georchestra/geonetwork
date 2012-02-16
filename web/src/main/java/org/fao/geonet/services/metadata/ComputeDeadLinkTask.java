package org.fao.geonet.services.metadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.XmlSerializer;
import org.jdom.Element;
import org.jdom.xpath.XPath;


public class ComputeDeadLinkTask extends TimerTask {
    ServiceContext context;
    Dbms dbms;
    String outputXmlPath ;

    public ComputeDeadLinkTask(ServiceContext context) 
    {
        this.context = context;
        this.outputXmlPath = context.getAppPath() + "xml" + File.separator + "urlcheck";
    	new File(outputXmlPath).mkdir();
        // then concatenates with a separator
        this.outputXmlPath += File.separator;
    }

    public void run() {
   
        try {

            FileWriter fstream = new FileWriter(outputXmlPath + "failingUrls-temp.xml");
            try {
                fstream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                fstream.write("<response>\n");

                GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);

                String regex = "(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                Pattern patt = null;

                patt = Pattern.compile(regex);

                // note, we are adding strings as url because performing hashcode on a url requires dns resolution and we don't want to do that
                // until after database connection is closed.
                HashMap<String /* URL */, String/* ID */> urls = new HashMap<String, String>();

                String query = "SELECT DISTINCT                " +
                               "                 id            " +
                               "FROM                           " +
                               "                 Metadata      " +
                               "WHERE                          " +
                               "                 istemplate = 'n'";
                
                Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
                try {
                Element result = dbms.select(query);
                

                for (Iterator iter = result.getChildren().iterator(); iter.hasNext();) {
                    Element rec = (Element) iter.next();
                    String id = rec.getChildText("id");

                    Element curMd = XmlSerializer.selectNoXLinkResolver(dbms, "Metadata", id);

                    // check parent / child validity
                    checkParentChildValidity(dbms, fstream, gc, id, curMd);
                    
                    curMd = Xml.transform(curMd, context.getAppPath() + "xsl" + File.separator + "alltext.xsl");
                    String texterizedMd = curMd.getText();

                    testTransferOptionURLs(fstream, patt, id, texterizedMd, urls);
                } // for ()
                } finally {
                    // close resource manager so process of checking links
                    // doesn't keep metadata table locked.
                    // this can hang other processes
                    // -- explicitly close Dbms resource to avoid exhausting
                    // Dbms
                    context.getResourceManager().close();
                }
                
                // now that database is free we can take our time to check deadlinks
                for(Map.Entry<String, String> entry: urls.entrySet()) {
                    String id = entry.getValue();
                    String urlText = entry.getKey();
                    testURL(fstream, id, urlText);
                }
                
                // pool
                fstream.write("</response>");
            } finally {
                IOUtils.closeQuietly(fstream);
            }

            FileUtils.copyFile(new File(outputXmlPath + "failingUrls-temp.xml"), new File(outputXmlPath + "failingUrls.xml"));

            // and delete the temp file
            new File(outputXmlPath + "failingUrls-temp.xml").delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testTransferOptionURLs(FileWriter fstream, 
            Pattern patt, String id, String texterizedMd, HashMap<String, String> urls) throws IOException {
        Matcher matcher = patt.matcher(texterizedMd);

        while (matcher.find()) {
            String text = matcher.group(0);
            urls.put(text, id);
        } // while()
    }

    private void testURL(FileWriter fstream, String id, String urlText) throws MalformedURLException, IOException {
        try {
            URL testUrl = new URL(urlText);
            try {
                HttpURLConnection c = (HttpURLConnection) testUrl.openConnection();
                c.setConnectTimeout(10000);
                c.setReadTimeout(10000);
                try {
                    if (c.getResponseCode() >= 400) {
                        fstream.write("\t<badUrl mdId=\"" + id + "\"><![CDATA[" + testUrl + "]]></badUrl>\n");
                    }
                } finally {
                    c.disconnect();
                }
            } catch (IOException e) {
                fstream.write("\t<badUrl mdId=\"" + id + "\"><![CDATA[" + testUrl + "]]></badUrl>\n");
            }
        } catch (MalformedURLException e) {

            fstream.write("\t<badUrl mdId=\"" + id + "\"><![CDATA[" + urlText + "]]></badUrl>\n");
        }

    }

    private void checkParentChildValidity(Dbms dbms, FileWriter fstream, GeonetContext gc, String id, Element curMd) {
        try {
            XPath pTh = XPath.newInstance("gmd:parentIdentifier/gco:CharacterString");
            List<Element> lstChild = pTh.selectNodes(curMd);

            for (Iterator<Element> lstChildIt = lstChild.iterator(); lstChildIt.hasNext();) {
                Element current = lstChildIt.next();
                String uuidP = current.getText();
                if ((uuidP == null) || ("".equals(uuidP))) {
                    continue;
                }
                try {
                    boolean parentValid = gc.getDataManager().existsMetadataUuid(dbms, uuidP);
                    if (!parentValid) {
                        fstream.write("\t<badMd mdId=\"" + id + "\"><![CDATA[" + uuidP + "]]></badMd>\n");
                    }
                } catch (Exception e) {
                    // error occured, notifying it into the xml file
                    fstream.write("\t<badMd mdId=\"" + id + "\"><![CDATA[" + uuidP + "]]></badMd>\n");
                }
            }
        } catch (Exception e) {
            // metadata is probably invalid (JDOM exception)
        }
    }
}