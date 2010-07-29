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

package org.fao.gast;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.fao.gast.boot.Config;
import org.fao.gast.boot.Util;
import org.fao.gast.localization.Messages;

import javax.swing.*;

//=============================================================================

public class Gast
{
    //---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public static void main(String argStrings[]) throws Exception
	{
        Args args = Args.parseArgs(argStrings);

		String starter = (args.cliArgs.length == 0)
									? "org.fao.gast.gui.MainFrame"
									: "org.fao.gast.cli.Cli";

		Util.boot(starter, args.cliArgs);
        
		// Shouldn't need the following - slf4j and log4j cause the cli to
		// never terminate?
		if (starter.equals("org.fao.gast.cli.Cli")) System.exit(0);
	}

	//---------------------------------------------------------------------------

    private static class Args {
        public final String[] cliArgs;
        private static final String CONFIG_ARG = "-config=";

        private Args(String[] cliArgs) {
            this.cliArgs = cliArgs;
        }

        public static Args parseArgs(String[] args) throws IOException {
            String configParam = loadConfig(args);
            if (configParam != null) {
                InputStream inputStream = toInputStream(configParam);
                if(inputStream == null ) {
                    // a param was declared that was not a valid param so force
                    // user to configure the config object
                    Config.queryForWebapp();
                } else {
                    Config.load(inputStream);
                }
            } else {
                // use the defaults
            }
            return new Args(findCliArgs(args));
        }

        private static String[] findCliArgs(String[] args) {
            List<String> cliArgs = new ArrayList<String>();
            for (String arg : args) {
                if (isCliArg(arg)) continue;

                cliArgs.add(arg);
            }
            return cliArgs.toArray(new String[cliArgs.size()]);
        }

        private static String loadConfig(String[] args) throws IOException {
            for (String arg : args) {
                if(isCliArg(arg)) {
                    String config = arg.substring(CONFIG_ARG.length());

                    return config;
                }
            }

            return null;
        }

        private static InputStream toInputStream(String config) throws IOException {
            if(new File(config).exists()) {
                return new FileInputStream(config);
            }
            try {
                return new URL(config).openStream();
            } catch (MalformedURLException e) {
            }
            return null;

        }

        private static boolean isCliArg(String arg) {
            return arg.toLowerCase().startsWith(CONFIG_ARG);
        }


    }
}

//=============================================================================

