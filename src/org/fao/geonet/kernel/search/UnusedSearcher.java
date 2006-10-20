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

package org.fao.geonet.kernel.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import jeeves.resources.dbms.Dbms;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;
import jeeves.utils.Util;
import jeeves.utils.Xml;
import org.fao.geonet.GeonetContext;
import org.fao.geonet.constants.Geonet;
import org.fao.geonet.services.util.MainUtil;
import org.fao.geonet.util.ISODate;
import org.jdom.Element;

//==============================================================================

class UnusedSearcher extends MetaSearcher
{
	private ArrayList alResult;
	private Element   elSummary;

	//--------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//--------------------------------------------------------------------------

	public UnusedSearcher() {}

	//--------------------------------------------------------------------------
	//---
	//--- MetaSearcher Interface
	//---
	//--------------------------------------------------------------------------

	public void search(ServiceContext context, Element request,
							 ServiceConfig config) throws Exception
	{
		GeonetContext gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);

		gc.getSiteId();

		alResult = new ArrayList();

		//--- get maximun delta in minutes

		int maxDiff = Integer.parseInt(Util.getParam(request, "maxDiff", "5"));

		context.info("UnusedSearcher : using maxDiff="+maxDiff);

		//--- proper search

		String query =	"SELECT DISTINCT id, createDate, changeDate "+
							"FROM   Metadata "+
							"WHERE  isTemplate='n' AND isHarvested='n' AND source='"+gc.getSiteId()+"'";

		Dbms dbms = (Dbms) context.getResourceManager().open(Geonet.Res.MAIN_DB);
		List list = dbms.select(query).getChildren();

		for(int i=0; i<list.size(); i++)
		{
			Element rec = (Element) list.get(i);

			String id = rec.getChildText("id");

			ISODate createDate = new ISODate(rec.getChildText("createdate"));
			ISODate changeDate = new ISODate(rec.getChildText("changedate"));

			if (changeDate.sub(createDate) / 60 < maxDiff)
				if (!hasInternetGroup(dbms, id))
					alResult.add(id);
		}

		//--- build summary

		makeSummary();

		initSearchRange(context);
	}

	//--------------------------------------------------------------------------

	public Element present(ServiceContext srvContext, Element request,
								  ServiceConfig config) throws Exception
	{
		updateSearchRange(request);

		GeonetContext gc = (GeonetContext) srvContext.getHandlerContext(Geonet.CONTEXT_NAME);

		//--- build response

		Element response =  new Element("response");
		response.setAttribute("from",  getFrom()+"");
		response.setAttribute("to",    getTo()+"");

		response.addContent((Element) elSummary.clone());

		if (getTo() > 0)
		{
			for(int i = getFrom() -1; i < getTo(); i++)
			{
				String  id = (String) alResult.get(i);
				Element md = gc.getDataManager().getMetadata(srvContext, id, false);

				response.addContent(md);
			}
		}

		return response;
	}

	//--------------------------------------------------------------------------

	public int getSize()
	{
		return alResult.size();
	}

	//--------------------------------------------------------------------------

	public Element getSummary() throws Exception
	{
		Element response =  new Element("response");
		response.addContent((Element) elSummary.clone());

		return response;
	}

	//--------------------------------------------------------------------------

	public void close() {}

	//--------------------------------------------------------------------------
	//---
	//--- Private methods
	//---
	//--------------------------------------------------------------------------

	private boolean hasInternetGroup(Dbms dbms, String id) throws SQLException
	{
		String query ="SELECT COUNT(*) AS result FROM OperationAllowed WHERE groupId=1 AND metadataId="+id;

		List list = dbms.select(query).getChildren();

		Element record = (Element) list.get(0);

		int result = Integer.parseInt(record.getChildText("result"));

		return (result > 0);
	}

	//--------------------------------------------------------------------------

	private void makeSummary() throws Exception
	{
		elSummary = new Element("summary");

		elSummary.setAttribute("count", getSize()+"");
		elSummary.setAttribute("type", "local");

		Element elKeywords = new Element("keywords");
		elSummary.addContent(elKeywords);

		Element elCategories = new Element("categories");
		elSummary.addContent(elCategories);
	}
}

//==============================================================================


