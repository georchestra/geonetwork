package org.fao.geonet.services.metadata;


import jeeves.exceptions.MissingParameterEx;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.UserSession;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Log;
import jeeves.utils.Util;
import jeeves.utils.Xml;

import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.exceptions.MetadataNotFoundEx;
import org.fao.geonet.kernel.AccessManager;
import org.fao.geonet.kernel.DataManager;
import org.fao.geonet.lib.Lib;
import org.fao.geonet.services.Utils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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