/*
 * Copyright (C) 2011  Camptocamp
 *
 * This file is part of GeOrchestra
 *
 * GeOrchestra is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GeOrchestra is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GeoBretagne.  If not, see <http://www.gnu.org/licenses/>.
 */


/*
 * This file aims to export some configuration variables from the main
 *  GeoNetwork JS application.
 *  
 * @author pmauduit
 * 
 */

/*
 * Note : this file is included after the Env object definition, but before
 * JS libraries inclusion. It is then possible to use the Env object (as
 * shown further with the wmsUrl definition).
 * 
 * FYI, the Env object defines the current variables (see header.xsl) :
 * 
 * 			Env.host = "http://<xsl:value-of select="/root/gui/env/server/host"/>:<xsl:value-of select="/root/gui/env/server/port"/>";
 *			Env.locService= "<xsl:value-of select="/root/gui/locService"/>";
 *			Env.locUrl    = "<xsl:value-of select="/root/gui/locUrl"/>";
 *			Env.url       = "<xsl:value-of select="/root/gui/url"/>";
 *			Env.lang      = "<xsl:value-of select="/root/gui/language"/>";
 *          Env.proxy     = "<xsl:value-of select="/root/gui/config/proxy-url"/>";
 */

var GeOrchestra = {

		CONFIG : {

			// Configuration variables for the GeoPublisher
	
			GeoPublisher: {

				// configuration for the base map used in the GeoPublisher interface
				baseMapOptions : {
	
					scales:     [200, 500, 1000, 2000, 5000, 10000, 25000, 50000, 100000, 250000, 500000, 1000000, 2000000],
					projection: 'EPSG:2154',
					units:      "m",
					minLon:     83000,
					minLat:     6700000,
					maxLon:     420000,
					maxLat:     6900000,
					theme:      null,
					wmsUrl:     Env.host + "/geoserver/wms",
					layers:     "geob:SC1000_0050_7130_L93"
				}
			}

		}
};


