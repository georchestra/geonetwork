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

package org.fao.geonet.services.main;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import jeeves.interfaces.Service;
import jeeves.server.ServiceConfig;
import jeeves.server.context.ServiceContext;

import org.geotools.data.DefaultQuery;
import org.geotools.data.FeatureSource;
import org.geotools.data.wfs.WFSDataStore;
import org.geotools.data.wfs.WFSDataStoreFactory;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.jdom.Element;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.expression.PropertyName;
import org.opengis.filter.identity.FeatureId;
import org.opengis.geometry.BoundingBox;

//=============================================================================

/**
 * Called for the main.search service. Returns the regions and region categories
 */

public class RegionMatches implements Service {
    private final FilterFactory  filterFac = CommonFactoryFinder.getFilterFactory(GeoTools.getDefaultHints());
    private Map<String, Element> _typenames;
    private WFSDataStore         _ds;
    private String               _url;
    private String               _user;
    private String               _pass;

    // --------------------------------------------------------------------------
    // ---
    // --- Init
    // ---
    // --------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void init(String appPath, ServiceConfig params) throws Exception {
        _url = params.getValue("wfsURL");
        _user = params.getValue("user");
        _pass = params.getValue("pass");

        _typenames = new LinkedHashMap<String, Element>();

        for (Iterator<Element> iter = params.getChildren("typenames"); iter.hasNext();) {
            Element e = iter.next();
            _typenames.put(e.getAttributeValue("typename"), e);
        }
    }

    // --------------------------------------------------------------------------
    // ---
    // --- Service
    // ---
    // --------------------------------------------------------------------------

    public Element exec(Element params, ServiceContext context) throws Exception {
        if (params.getChild("categories") != null) {
            return categories();
        } else if (params.getChild("bboxId") != null) {
            String id = params.getChildText("bboxId");
            String typename = params.getChildText("typename");
            return bbox(id, typename);
        } else {
            String pattern = params.getChildText("pattern");
            String typename = params.getChildText("typename");
            return matches(typename, pattern);
        }
    }

    private Element matches(String typename, String pattern) throws IOException {
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = getDS().getFeatureSource(typename);

        Element type = _typenames.get(typename);
        String nameAttributeName = null;
        if(type == null) {
            // use some reflection of type to guess the correct query

            SortedSet<String> possibleNameAttributes = new TreeSet<String>(new AttributeMatchComparator());
            for(AttributeDescriptor desc: featureSource.getSchema().getAttributeDescriptors()) {
                if(String.class.isAssignableFrom(desc.getType().getBinding())) {
                    possibleNameAttributes.add(desc.getLocalName());
                }
            }
            
            if(!possibleNameAttributes.isEmpty()) {
                nameAttributeName = possibleNameAttributes.first();
            }
        } else {
            nameAttributeName = type.getAttributeValue("nameAtt");
        }
        
        if(nameAttributeName == null) {
            throw new AssertionError("Unable to find a name attribute for typename: "+typename+".  THis is probably a configuration issue.  Check the configuration of the xml.region.list service in config.xml");
        }
        
        PropertyName attExpr = filterFac.property(nameAttributeName);
        Filter filter = filterFac.like(attExpr, pattern.toUpperCase(), "*", "?", "\\", false);
        String[] properties = { nameAttributeName };
        // String[] properties = {nameAttributeName,
        // featureSource.getSchema().getGeometryDescriptor().getLocalName()};
        DefaultQuery q = new DefaultQuery(featureSource.getSchema().getTypeName(), filter, properties);
        // q.setCoordinateSystemReproject(DefaultGeographicCRS.WGS84);
        // q.setMaxFeatures(10);

        FeatureIterator<SimpleFeature> features = featureSource.getFeatures(q).features();
        try {
            Element r = new Element("records");
            while (features.hasNext()) {
                try {
                    SimpleFeature next = features.next();
                    Element e = new Element("record");
                    Object name = next.getAttribute(nameAttributeName);
                    if (name != null) {
                        e.setAttribute("id", next.getID());
    
                        Element nameElem = new Element("name");
                        nameElem.setText(name.toString());
    
                        e.addContent(nameElem);
                        // e.setText(bbox(next));
                        r.addContent(e);
                    }
                } catch (NoSuchElementException e) {
                    // convert a strange geotools bug where if a WFS exception occurred the hasNext says there is a feature but next explodes
                    break;
                }
            }
            return r;
        } finally {
            features.close();
        }
    }

    private Element bbox(String id, String typename) throws IOException {
        FeatureSource<SimpleFeatureType, SimpleFeature> featureSource = getDS().getFeatureSource(typename);
        
        
        Set<FeatureId> featureIdSet = Collections.singleton(filterFac.featureId(id));
        Filter filter = filterFac.id(featureIdSet);
        
        String[] properties = {featureSource.getSchema().getGeometryDescriptor().getLocalName()};
        
        DefaultQuery q = new DefaultQuery(featureSource.getSchema().getTypeName(), filter, properties);
        q.setCoordinateSystemReproject(DefaultGeographicCRS.WGS84);
        q.setMaxFeatures(1);

        FeatureIterator<SimpleFeature> features = featureSource.getFeatures(q).features();
        try {
            Element r = new Element("response");
            while (features.hasNext()) {
                SimpleFeature next = features.next();
                Element e = new Element("record");
                BoundingBox env = next.getBounds();
                double minx = env.getMinX();
                double miny = env.getMinY();
                double maxx = env.getMaxX();
                double maxy = env.getMaxY();
                
                
                Element northElem = new Element("north").setText(""+maxy);
                Element southElem = new Element("south").setText(""+miny);
                Element westElem = new Element("west").setText(""+minx);
                Element eastElem = new Element("east").setText(""+maxx);

                e.addContent(northElem);
                e.addContent(southElem);
                e.addContent(westElem);
                e.addContent(eastElem);
                r.addContent(e);
            }
            return r;
        } finally {
            features.close();
        }
    }

    private Element categories() {
        Element r = new Element("typenames");
        r.setAttribute("count", "" + _typenames.size());
        for (String typename : _typenames.keySet()) {
            Element e = new Element("type");
            Element id = new Element("id");
            id.setText(typename);

            Element name = new Element("name");
            name.setText(typename.split(":", 2)[1]);

            e.addContent(id);
            e.addContent(name);
            r.addContent(e);
        }
        return r;
    }

    private synchronized WFSDataStore getDS() throws IOException {
        if (_ds == null) {
            WFSDataStoreFactory fac = new WFSDataStoreFactory();
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            map.put(WFSDataStoreFactory.URL.key, _url);
            if(_user!=null && _user.trim().length() > 0) {
                map.put(WFSDataStoreFactory.USERNAME.key, _user);
                map.put(WFSDataStoreFactory.PASSWORD.key, _pass);
            }
            map.put(WFSDataStoreFactory.LENIENT.key, true);
            this._ds = fac.createDataStore(map);
        }
        return this._ds;
    }
    
    private static class AttributeMatchComparator implements Comparator<String> {
        ArrayList<String> namesToLookFor = new ArrayList<String>();
        {
            namesToLookFor.add("name");
            namesToLookFor.add("nom");
            namesToLookFor.add("title");
            namesToLookFor.add("titre");
            namesToLookFor.add("id");
        }
        
        @Override
        public int compare(String o1, String o2) {
            int v1 = valueOf(o1);
            int v2 = valueOf(o2);
            return v1 - v2;
        }
        
        private int valueOf(String s) {
            if(isEqualToName(s)) return 2;
            else if(containsName(s)) return 1;
            else return 0;
            
        }

        private boolean isEqualToName(String s) {
            for (String name : namesToLookFor) {
                if(name.equalsIgnoreCase(s)) return true;
            }
            return false;
        }
        private boolean containsName(String s) {
            for (String name : namesToLookFor) {
                if(s.toLowerCase().contains(name.toLowerCase())) return true;
            }
            return false;
        }
        
    }
}

// =============================================================================
