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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.fao.gast.boot.Config;

//=============================================================================

public class LogLib
{
	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public LogLib() throws IOException
	{
		Properties props = new Properties();

		InputStream is = LOG4J_CFG.openStream();
		props.load(is);
		is.close();

		props.setProperty("log4j.appender.gast.file", LOG4J_FILE);

		PropertyConfigurator.configure(props);
	}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void debug(String message)
	{
		Logger.getLogger(MODULE).debug(message);
	}

	//---------------------------------------------------------------------------

	public void info(String message)
	{
		Logger.getLogger(MODULE).info(message);
	}

	//---------------------------------------------------------------------------

	public void warning(String message)
	{
		Logger.getLogger(MODULE).warn(message);
	}

	//---------------------------------------------------------------------------

	public void error(String message)
	{
		Logger.getLogger(MODULE).error(message);
	}

	//---------------------------------------------------------------------------

	public void fatal(String message)
	{
		Logger.getLogger(MODULE).fatal(message);
	}

	//---------------------------------------------------------------------------
	//---
	//--- Variables
	//---
	//---------------------------------------------------------------------------

	private static final String MODULE     = "gast";
	private static final URL LOG4J_CFG  = Config.getResource("log4j.cfg");
	private static final String LOG4J_FILE = Config.getConfig().getLogOutputDir()+"/gast.log";
}

//=============================================================================

