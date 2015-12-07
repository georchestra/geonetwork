The web module contains the static resources and configuration file for building the final web application WAR.
# Web Application Configuration

# geOrchestra specific setup

Using the default georchestra overrides files (see https://github.com/georchestra/config/tree/master/geonetwork) should suffice to reconfigure a GeoNetwork to suit geOrchestra's needs, but it requires some extra JAVA variables to be defined.

If you are using the geOrchestra datadir (via generic WAR archive, or debian/yum packages), ensure the following JAVA options are set before launching your application server:

```
  -Dgeorchestra.datadir=/etc/georchestra
  -Dgeonetwork.dir=/srv/tomcat/georchestra/work/gn_data
  -Dgeonetwork.schema.dir=/srv/tomcat/georchestra/work/gn_data/config/schema_plugins
  -Dgeonetwork.jeeves.configuration.overrides.file=/etc/georchestra/geonetwork/config/config-overrides-georchestra.xml
```
Do not forget to adapt the previous paths accordingly to suit your environment. 

