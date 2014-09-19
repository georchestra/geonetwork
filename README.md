Features
--------

* Immediate search access to local and distributed geospatial catalogues
* Up- and downloading of data, graphics, documents, pdf files and any other content type
* An interactive Web Map Viewer to combine Web Map Services from distributed servers around the world
* Online editing of metadata with a powerful template system
* Scheduled harvesting and synchronization of metadata between distributed catalogs
* Support for OGC-CSW 2.0.2 ISO Profile, OAI-PMH, Z39.50 protocols
* Fine-grained access control with group and user management
* Multi-lingual user interface


Setup
-----

Be sure to include those options in your tomcat setup:

    CATALINA_OPTS="
    -Dgeonetwork.dir=/path/to/geonetwork-data-dir \
    -Dgeonetwork.schema.dir=/path/to/tomcat/webapps/geonetwork/WEB-INF/data/config/schema_plugins \
    -Dgeonetwork.jeeves.configuration.overrides.file=/path/to/tomcat/webapps/geonetwork/WEB-INF/config-overrides-georchestra.xml
    "
