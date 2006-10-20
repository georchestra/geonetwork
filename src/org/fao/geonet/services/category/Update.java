//=============================================================================
//===	Copyright (C) 2001-2005 Food and Agriculture Organization of the
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
//===	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: GeoNetwork@fao.org
//==============================================================================

package org.fao.geonet.services.category;

import java.util.Set;
import java.util.Vector;
import jeeves.constants.Jeeves;
import jeeves.interfaces.Service;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.constants.Params;
import org.fao.geonet.lib.Lib;
import org.jdom.Element;

//=============================================================================

/** Update the information of a category
  */

public class Update implements Service
{
	public void init(String appPath, ServiceConfig params) throws Exception {}

	//--------------------------------------------------------------------------
	//---
	//--- Service
	//---
	//--------------------------------------------------------------------------

	public Element exec(Element params, ServiceContext context) throws Exception
	{
		String id   = params.getChildText(Params.ID);
		String name = Util.getParam(params, Params.NAME);

		Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);

		Vector  vArgs = new Vector ();
		Element elRes = new Element(Jeeves.Elem.RESPONSE);

		if (id == null)	// For Adding new category
		{
			Set<String> langs = Lib.local.getLanguages(dbms).keySet();

			int newId = context.getSerialFactory().getSerial(dbms, "Categories");
			vArgs.add(newId);
			vArgs.add(name);

			dbms.execute("INSERT INTO Categories(id, name) VALUES (?, ?)", vArgs);
			Lib.local.insert(dbms, "Categories", newId, name, langs);
			elRes.addContent(new Element(Jeeves.Elem.OPERATION).setText(Jeeves.Text.ADDED));
		}
		else 	//--- For Update
		{
			vArgs.add(name);
			vArgs.add(new Integer(id));

			dbms.execute("UPDATE Categories SET name=? WHERE id=?", vArgs);

			elRes.addContent(new Element(Jeeves.Elem.OPERATION).setText(Jeeves.Text.UPDATED));
		}

		return elRes;
	}
}

//=============================================================================

