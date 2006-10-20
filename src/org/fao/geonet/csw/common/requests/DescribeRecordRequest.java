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

package org.fao.geonet.csw.common.requests;

import java.util.ArrayList;
import org.fao.geonet.csw.common.Csw;
import org.fao.geonet.csw.common.Csw.TypeName;
import org.fao.geonet.csw.common.http.HttpRequest;
import org.jdom.Element;

//=============================================================================

/** Params:
  *  - outputFormat   (0..1) default is 'application/xml'
  *  - schemaLanguage (0..1) default is 'XMLSCHEMA'
  *  - typeName       (0..n)
  */

public class DescribeRecordRequest extends CatalogRequest
{
	private String outputFormat;
	private String schemaLang;

	private ArrayList<TypeName> alTypeNames = new ArrayList<TypeName>();

	//---------------------------------------------------------------------------
	//---
	//--- Constructor
	//---
	//---------------------------------------------------------------------------

	public DescribeRecordRequest() {}

	//---------------------------------------------------------------------------
	//---
	//--- API methods
	//---
	//---------------------------------------------------------------------------

	public void addTypeName(TypeName name)
	{
		alTypeNames.add(name);
	}

	//---------------------------------------------------------------------------

	public void setOutputFormat(String format)
	{
		outputFormat = format;
	}

	//---------------------------------------------------------------------------

	public void setSchemaLanguage(String language)
	{
		schemaLang = language;
	}

	//---------------------------------------------------------------------------
	//---
	//--- Protected methods
	//---
	//---------------------------------------------------------------------------

	protected String getRequestName() { return "DescribeRecord"; }

	//---------------------------------------------------------------------------

	protected void setupGetParams(HttpRequest request)
	{
		request.addParam("request", getRequestName());
		request.addParam("service", Csw.SERVICE);
		request.addParam("version", Csw.CSW_VERSION);

		if (outputFormat != null)
			request.addParam("outputFormat", outputFormat);

		if (schemaLang != null)
			request.addParam("schemaLanguage", schemaLang);

		fill(request, "typeName", alTypeNames, Csw.NAMESPACE_CSW.getPrefix() + ":");

		request.addParam("namespace", Csw.NAMESPACE_CSW.getPrefix() +":"+ Csw.NAMESPACE_CSW.getURI());
	}

	//---------------------------------------------------------------------------

	protected void setupPostParams(HttpRequest request)
	{
		Element params  = new Element(getRequestName(), Csw.NAMESPACE_CSW);

		//--- 'service' and 'version' are common mandatory attributes
		params.setAttribute("service", Csw.SERVICE);
		params.setAttribute("version", Csw.CSW_VERSION);

		if (outputFormat != null)
			params.setAttribute("outputFormat", outputFormat);

		if (schemaLang != null)
			params.setAttribute("schemaLanguage", schemaLang);

		//------------------------------------------------------------------------
		//--- add 'TypeName' elements

		for(TypeName typeName : alTypeNames)
		{
			Element el = new Element("TypeName", Csw.NAMESPACE_CSW);
			el.setText(typeName.toString());
			el.setAttribute("targetNamespace", Csw.NAMESPACE_CSW.getURI());

			params.addContent(el);
		}

		request.setParams(params);
	}
}

//=============================================================================

