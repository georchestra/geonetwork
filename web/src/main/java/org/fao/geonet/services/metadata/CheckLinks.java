package org.fao.geonet.services.metadata;


import java.io.File;
import java.io.IOException;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

//=============================================================================

/** Check links (i.e. URLs) from metadata ; access is reserved to the administrator
*/

public class CheckLinks implements Service
{
	
	public void init(String appPath, ServiceConfig params) throws Exception
	{
		
	}


	public Element exec(Element params, ServiceContext context) throws Exception
	{
		  SAXBuilder builder = new SAXBuilder();

		    try {
		      Document d = builder.build(context.getAppPath() + "xml" + File.separator 
		    		  							+ "urlcheck" + File.separator + "failingUrls.xml");
		      System.out.println("XML is well-formed.");
		    
		      Element ret = d.getRootElement();
		      ret.detach();
		      
		      return ret;
		    }
		    catch (JDOMException e) { 
		      System.out.println("XML deadlinks file is not well-formed.");
		      System.out.println(e.getMessage());
		    }  
		    catch (IOException e) { 
		      System.out.println("XML deadlinks file is not accessible.");
		    }  
	    	return new Element("response");
	}

}

//=============================================================================