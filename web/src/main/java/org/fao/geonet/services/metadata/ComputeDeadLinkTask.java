package org.fao.geonet.services.metadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
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
            Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

            FileWriter fstream = new FileWriter(outputXmlPath + "failingUrls-temp.xml");
            try {
                fstream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
                fstream.write("<response>\n");

                GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);

                String regex = "(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                Pattern patt = null;

                patt = Pattern.compile(regex);

                String query = "SELECT DISTINCT                " +
                               "                 id            " +
                               "FROM                           " +
                               "                 Metadata      " +
                               "WHERE                          " +
                               "                 istemplate = 'n'";
                
                Element result = dbms.select(query);

                for (Iterator iter = result.getChildren().iterator(); iter.hasNext();) {
                    Element rec = (Element) iter.next();
                    String id = rec.getChildText("id");

                    Element curMd = XmlSerializer.selectNoXLinkResolver(dbms, "Metadata", id);

                    // check parent / child validity
                    checkParentChildValidity(dbms, fstream, gc, id, curMd);
                    
                    curMd = Xml.transform(curMd, context.getAppPath() + "xsl" + File.separator + "alltext.xsl");
                    String texterizedMd = curMd.getText();

                    testTransferOptionURLs(fstream, patt, id, texterizedMd);
                } // for ()

                // -- explicitly close Dbms resource to avoid exhausting Dbms
                // pool
                fstream.write("</response>");
            } finally {
                context.getResourceManager().close();
                IOUtils.closeQuietly(fstream);
            }

            FileUtils.copyFile(new File(outputXmlPath + "failingUrls-temp.xml"), new File(outputXmlPath + "failingUrls.xml"));

            // and delete the temp file
            new File(outputXmlPath + "failingUrls-temp.xml").delete();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    private void testTransferOptionURLs(FileWriter fstream, Pattern patt, String id, String texterizedMd) throws IOException {
        Matcher matcher = patt.matcher(texterizedMd);

        while (matcher.find()) {
            String text = matcher.group(0);
            try {
                URL testUrl = new URL(text);
                try {
                    HttpURLConnection c = (HttpURLConnection) testUrl.openConnection();
                    try {
                        if (c.getResponseCode() >= 400) {
                            fstream.write("\t<badUrl mdId=\"" + id + "\"><![CDATA[" + text + "]]></badUrl>\n");
                        }
                    } finally {
                        c.disconnect();
                    }
                } catch (IOException e) {
                    fstream.write("\t<badUrl mdId=\"" + id + "\"><![CDATA[" + text + "]]></badUrl>\n");
                }
            } catch (MalformedURLException e) {

                fstream.write("\t<badUrl mdId=\"" + id + "\"><![CDATA[" + text + "]]></badUrl>\n");
            }
        } // while()
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