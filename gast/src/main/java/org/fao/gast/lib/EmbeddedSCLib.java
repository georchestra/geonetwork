//=============================================================================
//===	Copyright (C) 2001-2007 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================

package org.fao.gast.lib;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fao.gast.boot.Config;
import org.fao.gast.boot.Util;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.xml.sax.SAXException;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.resource.Resource;
import org.mortbay.xml.XmlConfiguration;


//=============================================================================
/** Embedded Servlet Container lib */

public class EmbeddedSCLib
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public EmbeddedSCLib() throws Exception
	{
		webXml = Lib.xml.load(WEBXML_FILE);

		System.out.println("Loading Jetty with "+Config.getConfig().getWebapp());

        final Server server = new Server(Config.getConfig().getJettyPort());

        server.setHandler(new WebAppContext(Config.getConfig().getWebapp(), "/geonetwork"));

		Connector[] conns = server.getConnectors();

		//--- assume connector we want is the first one?
        host = conns[0].getHost();
        System.out.println("Jetty on Host: "+host);
        port = conns[0].getPort()+"";
        System.out.println("Jetty on Port: "+port);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public String getHost()
	{
		return host;
	}

	//---------------------------------------------------------------------------

	public String getPort()
	{
		return port;
	}

	//---------------------------------------------------------------------------

	public String getServlet()
	{
		return "geonetwork";
	}

	//---------------------------------------------------------------------------

	public void setHost(String host)
	{
		// Disabled for Jetty 6.x
	}

	//---------------------------------------------------------------------------

	public void setPort(String port)
	{
		// Disabled for Jetty 6.x
	}

	//---------------------------------------------------------------------------

	public void setServlet(String name)
	{
		// Disabled for Jetty 6.x
		//             if (servletElem != null)
		//                     servletElem.setText("/"+name);
		//
		//             for (Object e : webXml.getRootElement().getChildren())
		//             {
		//                     Element elem = (Element) e;
		//
		//                     if (elem.getName().equals("display-name"))
		//                     {
		//                             elem.setText(name);
		//                             return;
		//                     }
		//             }
		//
	}

	//---------------------------------------------------------------------------

	public void save() throws FileNotFoundException, IOException
	{
		Lib.xml.save(WEBXML_FILE, webXml);

		//--- create proper index.html file to point to correct servlet

		Map<String, String> vars = new HashMap<String, String>();
		vars.put("$SERVLET", getServlet());

        final InputStream inputStream = INDEX_SRC_FILE.openStream();
        try {
            List<String> lines = Lib.text.load(inputStream);
            Lib.text.replace(lines, vars);
            Lib.text.save(INDEX_DES_FILE, lines);
        }finally {
            inputStream.close();
        }
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	private String   host;
	private String   port;
	private Document webXml;

	private static final String WEBXML_FILE = Config.getConfig().getWebXml();

	private static final URL INDEX_SRC_FILE = Config.getResource("data/index.html");
	private static final String INDEX_DES_FILE = Config.getConfig().getWebapp()+"/index.html";
}

//=============================================================================

