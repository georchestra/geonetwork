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

package org.fao.geonet.guiservices.search;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.kernel.search.SearchManager;
import org.fao.geonet.services.util.SearchDefaults;
import org.hibernate.validator.Min;
import org.jdom.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

//=============================================================================

/** Returns the top values for the lucene fields.
 *
 * Params are name of field with value the number of fields to return.
  */

public class GetSearchTerms implements Service
{
    private ArrayList<Field> fields;

    private static final class Field {
        public final String name;
        public final int maxItems;

        private Field(String name, int maxItems) {
            this.name = name;
            this.maxItems = maxItems;
        }

        @Override
        public String toString() {
            return name+" ["+maxItems+"]";
        }
    }
	//--------------------------------------------------------------------------
	//---
	//--- Init
	//---
	//--------------------------------------------------------------------------

	public void init(String appPath, ServiceConfig params) throws Exception {
        Iterator<Element> fieldElems = params.getChildren("terms");
        fields = new ArrayList<Field>();
        while(fieldElems.hasNext()) {
            Element next = fieldElems.next();

            int maxItems;
            try {
                maxItems = Integer.parseInt(next.getTextNormalize());
            } catch (Exception e) {
                maxItems = Integer.MAX_VALUE;
            }

            fields.add(new Field(next.getName(),maxItems));
        }
    }

	//--------------------------------------------------------------------------
	//---
	//--- Service
	//---
	//--------------------------------------------------------------------------

	public Element exec(Element params, ServiceContext context) throws Exception
	{
        GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
        SearchManager searchManager = gc.getSearchmanager();

        Element result = new Element("indexTerms");
        for (Field field : fields) {
            Element fieldElem = new Element(field.name);
            result.addContent(fieldElem);

            Vector<Term> terms = new Vector<Term>();

            IndexReader reader = searchManager.getIndexReader();

            try {
                TermEnum enu = reader.terms();
                while (enu.next())
                {
                    Term term = enu.term();
                    terms.add(term);
                    if (term.field().equals(field.name))
                        fieldElem.addContent(new Element("term").setText(term.text()));
                }
            } finally {
                searchManager.releaseIndexReader(reader);
            }
        }

        return result;
	}
}


//=============================================================================

