# Geonetwork migration in Georchestra

This document aims to help simplify future migration for geonetwork in Georchestra. 

## Upgrade Process

### Simplified guide

This is an ultra-simplified guide to be able to build an upgraded version of georchestra/geonetwork
- Merge gn-core tag into georchestra/gn wanted branch. Resolve coflicts and get georchestra custom implementions (see below).
- Copy folders `config` and `data` from `web/src/main/webapp/WEB-INF/data/` into the repo [geonetwork_minimal_datadir](https://github.com/georchestra/geonetwork_minimal_datadir) and create associated branch with new version name.
- Build geonetwork (war, docker ...)
- Delete wro4j cache if necessary (use ?debug can be sometimes useful)
- Reindex records if necessary

A more detailed guide is available in [upgrade_geonetwork.md](upgrade_geonetwork.md).

## Georchestra custom implementations

### Georchestra 4.2.7 and Gn 4.2.7

All versions in `pom.xml` files must be updated. E.g. for this version `4.2.7-georchestra`.
All italic folder just have the `pom.xml` change.

- .github
  - workflows `linux.yml` `mvn-dep-tree.yml` `sonarcloud.yml` `dependabot.yml` are deleted. Only `georchestra-gn4.yml` is used
- *cachingxslt* 
- common
  - `ZipUtilTest.java` : In `assertExampleZip` method, assertions which are supposed check folders must check with trailing slash too.
- core
  - `JeevesContextLoaderListener.java` : remove java 8 runtime exception as we use java 11.
  - `XslUtil.java`: Implement georchestra header specific code
  - `config-spring-geonetwork.xml` : Implement `context:property-placeholder` for georchestra's datadir
  - `src/test/resources/config-spring-geonetwork.xml`: Add GeonetworkDataDirectory bean
  - `cleanoutdatabase.sql`: Delete from settings-ui at the end of the file
- *csw-server*
- **docker** 
  - Mandatory, get everything from geOrchestra
- *docs*
- *doi*
- domain
  - ExternalGroupLink.java : get file from geOrchestra
  - ExternalUserLink.java : get file from geOrchestra
- *es*
- *estest*
- *events*
- **georchestra-integration** 
  - Mandatory, get everything from geOrchestra
- harvesters
  - `Harvester.java`: Use parseJDK11 method 
  - `HarvesterTest.java` : Ignore and assume true Java 8
- *healthmonitor*
- *index*
- *inspire-atom*
- *jmeter*
- *listeners*
- *messaging*
- *oaipmh*
- *release*
- schemas
  - `src/main/plugin/iso19115-3.2018`
    - OGC API features and 3Dtiles added. Differences in files: 
      - `config/associated-panel/default.json`
      - `loc/eng/labels.xml`
      - `loc/fre/labels.xml`
      - `test/resources/metadata-for-editing.xml`
      - `test/resources/metadata-for-editing-light.xml`
      - `test/resources/metadata-iso19139-for-editing.xml`
  - `src/main/plugin/iso19139/loc` : 3Dtiles added in labels.xml files.
- *schemas-test*
- *sde*
- services
  - `config-spring-geonetwork.xml`: Implement `context:property-placeholder` for georchestra's datadir
  - `MetadataExtentApiTest.java` : Update image signatures if necessary (tests may fail see [MetadataExtentApiTest-reference](resources%2FMetadataExtentApiTest-reference) for image reference)
  - `BatchOpsMetadatReindexerTest.java` : Add PowerMockIgnore
  - `pom.xml`: version to update **and to add to gn-services**
- *slave*
- web
  - `src/docker`: Mandatory, get everything from geOrchestra
  - `main/filters/prod.properties`: Session timeout variable updated
  - `main/filters/java/org/fao/geonet/proxy`: Mandatory, get everything from geOrchestra
  - `data-db-default.sql` : Some data to retrieve: setting-ui, inspire activated, georchestra name and (backported value) sitemapLinkUrl
  - `UpdateMetadataStatus.java`: Some fixes
  - `config-security.xml`: Remove config security add start of file, get end of the file from georchestra.
  - `config-geonetwork-georchestra.properties`, `config-georchestra-geonetwork-datadirs.xml`, `DKAN-to-ISO19115-3-2018.xsl`, `GeoIDE-services-OGC.xsl`, `udata-to-ISO19115-3-2018.xsl`
    - Get files from georchestra
  - `base-variables.xsl` and `base-layout.xsl`
    - Get header from georchestra
  - `defaultJdbcDataSource.xml`: Implement `context:property-placeholder` for georchestra's db
  - `config-spring-geonetwork.xml`
    - Implement `context:property-placeholder` for georchestra's datadir
    - Don't forget to import config resource and logging bean
  - `spring-servlet.xml`: Implement `context:property-placeholder` for georchestra's datadir
  - `postgres-postgis.xml` Keep `context:property-placeholder` for georchestra's datadir
  - `pom.xml`: Update `dockerGnDatadirScmVersion` variable accordingly and remove `font-awesome/css/` from `packagingExclude`
- web-ui
  - `pom.xml`: version to update **and to add to gn-web-ui**
  - `RelatedResourcesService.js`: Add 3DTiles
  - `CatController.js`: Menu bar accessible and 3DTiles
  - `menu-signin.html`: remove the `authenticated` from the `ng-if` in firstul tag.
  - `src/main/resources/catalog/locales/`
    - Add OGC API - Features to i18n files
    - Get `en-georchestra.json` and `fr-georchestra.json` from georchestra
  - `src/main/resources/catalog/style/gn-less`: remove position fixed from `.gn-top-bar` to get gn header after georchestra's header.
  - `gn_admin_default.less`: Same (header position)
  - `gn_navbar_default.less`: Same (header position)
  - `src/main/resources/catalog/views/module.js`: Add 3DTiles
  - `src/main/resources/catalog/views/georchestra/`: Get files from georchestra
- *workers*
- *wro4j*
- .gitignore 
  - add idea and settings to it
- pom.xml  
  - Implement JDK 11 specific things, add georchestra-integration module, set db-type

## Process used

### 4.2.4 -> 4.2.7

A branch has been created from core-geonetwork 4.2.7 tag and merged into `georchestra-gn4.2.x`.
All conflicts files have been resolved with taking gn changes and not grochestra's one.
Then changes have been reimplemented.

