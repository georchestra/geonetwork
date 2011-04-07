package org.fao.geonet.services.metadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jeeves.resources.dbms.Dbms;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.DataManager;
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
            fstream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            fstream.write("<response>\n");
            
            GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
            DataManager dm = gc.getDataManager();
            
            String regex = "(https?|ftps?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
            Pattern patt = null ;
            
          
            patt = Pattern.compile(regex);

            String query = "SELECT DISTINCT                " +
            		       "				 id            " +
            			   "FROM                           " +
            			   "                 Metadata      " +
            			   "WHERE                          " +
            			   "                 istemplate = 'n'";
            
            Element result = dbms.select(query);

			for (Iterator iter = result.getChildren().iterator(); iter.hasNext(); )
			{
				Element rec = (Element)iter.next();
				String  id = rec.getChildText("id");

				Element curMd = XmlSerializer.selectNoXLinkResolver(dbms, "Metadata", id);
				
				// check parent / child validity
				try
				{
					XPath pTh    = XPath.newInstance("gmd:parentIdentifier/gco:CharacterString");
					List<Element> lstChild = pTh.selectNodes(curMd);

					for (Iterator<Element> lstChildIt = lstChild.iterator() ; lstChildIt.hasNext() ; )
					{
						Element current = lstChildIt.next();
						String uuidP = current.getText();
						if ((uuidP == null) || ("".equals(uuidP)))
						{
							continue ;
						}
						try
						{
							boolean parentValid = gc.getDataManager().existsMetadataUuid(dbms, uuidP);
							if (! parentValid)
							{
								fstream.write("\t<badMd mdId=\""+id+"\"><![CDATA[" + uuidP +"]]></badMd>\n"); 
							}
						}
						catch (Exception e)
						{
							// error occured, notifying it into the xml file
							fstream.write("\t<badMd mdId=\""+id+"\"><![CDATA[" + uuidP +"]]></badMd>\n"); 	
						}
					}
				} catch (Exception e)
				{
					// metadata is probably invalid (JDOM exception)
				}
				curMd = Xml.transform(curMd, context.getAppPath() + "xsl" + File.separator + "alltext.xsl");
				String texterizedMd = curMd.getText();

				Matcher matcher = patt.matcher(texterizedMd);
				 	     
	             while (matcher.find()) 
	             {
	                 String text = matcher.group(0);
	                 try {
	                	 URL testUrl = new URL(text);
	                	 try
	                	 {
	                		 URLConnection c = testUrl.openConnection();
	                		 c.getInputStream().close();
	                	 } catch (IOException e)
	                	 {
	                		 // consider printing it into the xml temp file
	                		 //e.printStackTrace();
	                		 fstream.write("\t<badUrl mdId=\""+id+"\"><![CDATA[" + text +"]]></badUrl>\n"); 
	                	 }
	                 } catch (MalformedURLException e)
	                 {
	                	 //e.printStackTrace();
                		 
	            		 fstream.write("\t<badUrl mdId=\""+id+"\"><![CDATA[" + text +"]]></badUrl>\n");
	                 }
	             } // while()
			} // for ()

            //-- explicitly close Dbms resource to avoid exhausting Dbms pool
            context.getResourceManager().close();
			fstream.write("</response>");
			fstream.close();
			
			// now move the temp file to the official one
			InputStream in = new FileInputStream(outputXmlPath + "failingUrls-temp.xml");		
			OutputStream out = new FileOutputStream(outputXmlPath + "failingUrls.xml"); 
			// Transfer bytes from in to out 
			byte[] buf = new byte[1024]; 
			int len; 
			while ((len = in.read(buf)) > 0) 
			{ 
				out.write(buf, 0, len); 
			} 
			in.close(); 
			out.close(); 
			
			// and delete the temp file
			new File(outputXmlPath + "failingUrls-temp.xml").delete(); 
			
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}