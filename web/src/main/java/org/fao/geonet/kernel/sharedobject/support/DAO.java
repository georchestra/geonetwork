package org.fao.geonet.kernel.sharedobject.support;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import jeeves.resources.dbms.Dbms;
import jeeves.utils.Log;
import jeeves.utils.Xml;

import org.fao.geonet.constants.Geonet;
import org.geotools.data.*;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.factory.GeoTools;
import org.geotools.feature.AttributeTypeBuilder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.Or;
import org.opengis.filter.expression.Function;

public class DAO {

    private static Normalizer DEFAULT_SEARCH_NORMALIZER = new DefaultNormalizer();
    public static final String CONFIG_FILE = "/WEB-INF/config-sharedobject.xml";
    private static final Map<String, DAO> daoMapping = new HashMap<String, DAO>();
    
    private DataStore reusableStore;
    private Map<String, Mapper> mappers = new HashMap<String, Mapper>();
    private Dbms dbms;
    private String appPath;

    public DAO(String appPath, Dbms dbms) throws IOException, JDOMException, SQLException {
        this.dbms = dbms;
        this.appPath = appPath;
        Element config = Xml.loadFile(appPath+ CONFIG_FILE);
        reusableStore = init(appPath, config.getChild("sharedObjectConfig"));
    }
    
    @SuppressWarnings("unchecked")
    private DataStore init(String appPath, Element config) throws IOException, SQLException, JDOMException {
        DataStore store = initDataStore(config.getChild("connectionParams"));

        Map<String, Element> templates = setupTemplates(appPath);

        createSharedTypes(config, store, templates);
        
        return store;
    }

    private Map<String, Element> setupTemplates(String appPath) throws JDOMException, IOException {
        Map<String, Element> templates = new HashMap<String, Element>();

        File templateDir = new File(appPath + "/xml/sharedobject/template");
        for(File templateFile : templateDir.listFiles()) {
            Element template = Xml.loadFile(templateFile);
            String name = templateFile.getName();
            if(name.lastIndexOf('.') > 0) {
                name = name.substring(0,name.lastIndexOf('.'));
            }
            templates.put(name, template);
        }
        return templates;
    }

    private void createSharedTypes(Element config, DataStore store, Map<String, Element> templates) throws IOException, SQLException {
        java.util.List<Element> types = config.getChildren("type");
        Set<String> typeNames = new HashSet<String>(Arrays.asList(store.getTypeNames()));

        for (Element element : types ) {
            String name = Constants.TYPE_PREFIX+element.getAttributeValue("name");

            if(!typeNames.contains(name)) {
                store.createSchema(featureType(name));
                // TODO should use mapper and be based on JDBC driver.  perhaps entire method should be part of mapper's abstract superclass
                dbms.execute(String.format(SqlConstants.ALTER_TABLE_QUERY, name, "data"));
                dbms.execute(String.format(SqlConstants.ALTER_TABLE_QUERY, name, "search"));
                String templateRef = element.getChild("template").getAttributeValue("id");
                if(!templates.containsKey(templateRef)) {
                    throw new AssertionError("Template: "+templateRef+" does not exist.  It is referenced by type: "+name);
                }
                setTemplate(name, templates.get(templateRef));
            }

            Mapper mapper;
            if(element.getChild("mapper") != null) {
                if(element.getChild("normalizer") != null) {
                    Log.warning(Geonet.SHARED_OBJECT, "Only one of mapper or normalizer can be used.  The mapper takes precendence so normalizer is being ignored");
                }
                mapper = (Mapper) newInstance(element, "mapper");
            } else {
                Normalizer normalizer = DEFAULT_SEARCH_NORMALIZER ;
                if(element.getChild("normalizer") != null) {
                    normalizer = (Normalizer) newInstance(element, "normalizer");
                }
                mapper = new DefaultMapper(normalizer);
            }
            mapper.getNormalizer().init(appPath);
            mappers.put(name, mapper);
        }
    }

    private SimpleFeatureType featureType(String name) {
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();

        AttributeTypeBuilder attTypeBuilder = new AttributeTypeBuilder();

        attTypeBuilder.setCRS(DefaultGeographicCRS.WGS84);
        attTypeBuilder.setBinding(Geometry.class);
        builder.add(attTypeBuilder.buildDescriptor("the_geom"));

        builder.add("id",Integer.class);

        attTypeBuilder = new AttributeTypeBuilder();
        attTypeBuilder.setBinding(String.class);
        attTypeBuilder.setLength(10000);
        builder.add(attTypeBuilder.buildDescriptor("data"));

        attTypeBuilder = new AttributeTypeBuilder();
        attTypeBuilder.setBinding(String.class);
        attTypeBuilder.setLength(10000);
        builder.add(attTypeBuilder.buildDescriptor("search"));

        builder.setName(name);
        return builder.buildFeatureType();
    }
    
    private Object newInstance(Element element, String elemName) {
        try {
            return Class.forName(element.getChildTextNormalize(elemName)).newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private DataStore initDataStore(Element paramConfig) throws IOException {
        Map<String,String> params = new HashMap<String,String>();
        
        @SuppressWarnings("unchecked")
        Collection<Element> values = paramConfig.getChildren();
        
        for (Element element : values) {
            params.put(element.getName(), element.getTextNormalize());
        }
        
        return DataStoreFinder.getDataStore(params);
    }

    public void setTemplate(String typeName, Element element) throws SQLException {
        dbms.execute(SqlConstants.SET_TEMPLATE_QUERY,typeName,Xml.getString(element));
    }

    public Collection<String> listTypes() throws IOException {
        ArrayList<String> types = new ArrayList<String>();
        for (String type : reusableStore.getTypeNames()) {
            if(type.startsWith(Constants.TYPE_PREFIX)) {
                types.add(type.substring(Constants.TYPE_PREFIX.length()));
            }
        }
        return types;
    }
    
    public List<Element> listTemplates(String typename) throws SQLException, JDOMException, IOException {
        Element response = dbms.select(SqlConstants.LIST_TEMPLATES_QUERY, typename);

        List<Element> types = new ArrayList<Element>();
        for(Element record : (List<Element>) response.getChildren("record")) {
            String type = record.getChildTextNormalize(SqlConstants.TEMPLATE_COL);
            if (type != null) {
                types.add(Xml.loadString(type, false));
            }
        }

        return types;
    }

    public static synchronized DAO get(String appPath, Dbms dbms) throws JDOMException, IOException, SQLException {
        DAO dao = daoMapping.get(appPath);
        if(dao == null) {
            dao = new DAO(appPath,dbms);
            daoMapping.put(appPath, dao);
        }
        return dao;
    }

    public void update(String typename, int id, Element value) throws Exception {
        FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) this.reusableStore.getFeatureSource(typename);

        Mapper mapper = mappers.get(typename);
        final Filter findFeatureFilter = mapper.lookupFilter(id);
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = store.getFeatures(findFeatureFilter);

        SimpleFeature feature;
        boolean add = false;
        FeatureIterator<SimpleFeature> iter = features.features();
        try {
            if(iter.hasNext()) {
                feature = iter.next();
            } else {
                add = true;
                feature = SimpleFeatureBuilder.template(store.getSchema(), ""+id);
            }
        } finally {
            iter.close();
        }

        String searchData = mapper.getNormalizer().normalize(value);
        mapper.setData(id, value, searchData, feature);

        if(add) {
            FeatureCollection<SimpleFeatureType, SimpleFeature> toAdd = FeatureCollections.newCollection();
            toAdd.add(feature);
            store.addFeatures(toAdd);
        } else {
            final List<AttributeDescriptor> descriptors = new ArrayList<AttributeDescriptor>(feature.getFeatureType().getAttributeDescriptors());

            List<Object> attributes = new ArrayList<Object>();

            for (Iterator<AttributeDescriptor> i = descriptors.iterator(); i.hasNext();) {
                AttributeDescriptor descriptor = i.next();
                final Object attValue = feature.getAttribute(descriptor.getName());
                
                if(attValue != null) {
                    attributes.add(attValue);
                } else {
                    i.remove();
                }
            }
            AttributeDescriptor[] descriptorArray = descriptors.toArray(new AttributeDescriptor[descriptors.size()]);
            store.modifyFeatures(descriptorArray, attributes.toArray(), findFeatureFilter);
        }
    }

    public Element get(String typename, int id) throws JDOMException, IOException, SQLException {
        FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) this.reusableStore.getFeatureSource(typename);

        Mapper mapper = mappers.get(typename);
        final Filter findFeatureFilter = mapper.lookupFilter(id);
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = store.getFeatures(findFeatureFilter);

        FeatureIterator<SimpleFeature> iter = features.features();
        try {
            if(iter.hasNext()) {
                SimpleFeature feature = iter.next();
                final String data = (String) feature.getAttribute(Constants.DATA_ATTRIBUTE_NAME);
                Element elt = Xml.loadString(data, false);
                elt.removeAttribute("id");
                return elt;
            }
        } finally {
            iter.close();
        }

        // unable to find a feature so return first template
        List<Element> templates = listTemplates(typename);

        if(templates.isEmpty()) return null;
        else return templates.get(0);
    }

    public List<Element> list(String typename, String search, org.fao.geonet.kernel.sharedobject.List.Results resultType) throws Exception {

        ArrayList<Element> results = new ArrayList<Element>();
        FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) this.reusableStore.getFeatureSource(typename);

        Mapper mapper = mappers.get(typename);
        final Filter findFeatureFilter;
        if(search != null) {
            findFeatureFilter = mapper.searchFilter(search);
        } else {
            findFeatureFilter = Filter.INCLUDE;
        }

        String[] attributes = resultType.attributes(store);
        Query query = new DefaultQuery(typename, findFeatureFilter, attributes);
        FeatureCollection<SimpleFeatureType, SimpleFeature> features = store.getFeatures(query);

        FeatureIterator<SimpleFeature> iter = features.features();
        try {
            while(iter.hasNext()) {
                SimpleFeature feature = iter.next();
                results.add(resultType.toElem(feature));
            }
        } finally {
            iter.close();
        }
        
        return results;
    }

    public void delete(String typename, String[] ids) throws IOException {
        FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) this.reusableStore.getFeatureSource(typename);

        Mapper mapper = mappers.get(typename);

        List<Filter> filters = new ArrayList<Filter>();
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(GeoTools.getDefaultHints());

        for (String id : ids) {
            filters.add(mapper.lookupFilter(Integer.valueOf(id)));
        }
        Or filter = ff.or(filters);

        store.removeFeatures(filter);
    }

    public int nextId(String typename) throws IOException {
        FeatureStore<SimpleFeatureType, SimpleFeature> store = (FeatureStore<SimpleFeatureType, SimpleFeature>) this.reusableStore.getFeatureSource(typename);

        Mapper mapper = mappers.get(typename);

        return mapper.nextId(store);
    }
}
