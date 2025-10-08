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

### Future geonetwork migrations

When a new version of geonetwork is released, 
- create a branch on newest tag, e.g. `4.4.9` -> `georchestra-gn4.4.9`.
- Then cherry-pick the commits from the latest georchestra branch.
- `pom.xml` files must be updated. E.g. for this version `4.4.9-georchestra`.
- Update `web-ui/pom.xml` file to add the new version to `gn-web-ui` package. E.g: from `<version>4.4.8-georchestra</version>` to `<version>4.4.9-georchestra</version>`
- Update latest stable georchestra verison in ` georchestra-integration/pom.xml` -> `<georchestra.version>25.0.0</georchestra.version>`

### Legacy list

This is the list of files/folders that have been modified in georchestra's geonetwork fork.

All versions in `pom.xml` files must be updated. E.g. for this version `4.4.8-georchestra`.
All italic folder just have the `pom.xml` change.

- .github
  - workflows `linux.yml` `mvn-dep-tree.yml` `sonarcloud.yml` `dependabot.yml` are deleted. Keep `backport.yml`, `build-java-11.yml`, `scorecard.yaml` is used
- *cachingxslt*
- *common*
- core
  - `XslUtil.java`: Implement georchestra header specific code
  - `config-spring-geonetwork.xml` : Implement `context:property-placeholder` for georchestra's datadir
  - `src/test/resources/config-spring-geonetwork.xml`: Add GeonetworkDataDirectory bean
  - `cleanoutdatabase.sql`: Delete from settings-ui at the end of the file
- csw-server
  - `CswFilter2Es.java` : Keep `{@}` instead of `%s` until it is fixed upstream (not supporting some CSW request)
  - `CswFilter2EsTest.java` : Keep `{@}` instead of `%s`
  - `SearchController.java` : Keep `{@}` instead of `%s` with StringUtils.replace, try/catch around Element resultMD, to avoid csw server to crash [issue core-gn 6940](https://github.com/geonetwork/core-geonetwork/issues/6940)
- **docker**
  - Mandatory, get everything from geOrchestra
- *docs*
- *doi*
- domain
  - `ExternalGroupLink.java` : get file from geOrchestra
  - `ExternalUserLink.java` : get file from geOrchestra
  - `Group.java` : set column definition to `TEXT` (which is PostGreSQL specific) on `description` and `logo`
- *es*
- *estest*
- *events*
- **georchestra-integration**
  - Mandatory, get everything from geOrchestra
- *harvesters*
- *healthmonitor*
- *index*
- *inspire-atom*
- *jmeter*
- *listeners*
- *messaging*
- *oaipmh*
- *release*
- schemas
  - `iso19139/layout/config-editor.xml`: xlinks for contacts reenabled by default: see [about-xlinks.md](./about-xlinks.md)
- *schemas-test*
- *sde*
- services
  - `config-spring-geonetwork.xml`: Implement `context:property-placeholder` for georchestra's datadir
  - `MetadataExtentApiTest.java` : Update image signatures if necessary (tests may fail see [MetadataExtentApiTest-reference](resources%2FMetadataExtentApiTest-reference) for image reference)
  - `BatchOpsMetadatReindexerTest.java` : Add PowerMockIgnore
  - `pom.xml`: version to update **and to add to gn-services**
  - `LogUtils.java`: Keep custom configuration in `refreshLogConfiguration()` method.
- *slave*
- web
  - `src/docker`: Mandatory, get everything from geOrchestra
  - `main/filters/prod.properties`: Session timeout variable updated
  - `main/filters/java/org/fao/geonet/proxy`: Mandatory, get everything from geOrchestra
  - `data-db-default.sql` : Some data to retrieve: setting-ui, inspire activated, georchestra name, (backported value) sitemapLinkUrl, xlinkresolver enabled by default
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
  - `pom.xml`: Keep exclusion of groovy package to avoid two versions of it.
- web-ui
  - `pom.xml`: version to update **and to add to gn-web-ui**
  - `CatController.js`: Menu bar accessible
  - `menu-signin.html`: remove the `authenticated` from the `ng-if` in first ul tag.
  - `src/main/resources/catalog/locales/`
    - Get `en-georchestra.json` and `fr-georchestra.json` from georchestra
  - `src/main/resources/catalog/style/gn-less`: remove position fixed from `.gn-top-bar` to get gn header after georchestra's header.
  - `src/main/WebResources/WEB-INF/data/config/index/records.json`: Keep field `mappings.dynamic_templates.semsearch` from georchestra. It is used by chatbot for semantic search.
  - `gn_admin_default.less`: Same (header position)
  - `gn_navbar_default.less`: Same (header position)
  - `src/main/resources/catalog/views/georchestra/`: Get files from georchestra
- *workers*
- *wro4j*
- .gitignore
  - add idea and settings to it
- pom.xml  
  - Add georchestra-integration module, set db-type
- Makefile: keep it
- 
## Process used

### 4.4.8 -> 4.4.9

```bash
 git diff --binary 4.4.8..georchestra-gn4.4.x > fork-changes.patch
 //checkout origin tag 4.4.9
 git am -3 < fork-changes.patch
```

### 4.2.4 -> 4.2.7

A branch has been created from core-geonetwork 4.2.7 tag and merged into `georchestra-gn4.2.x`.
All conflicts files have been resolved with taking gn changes and not geOrchestra's one.
Then changes have been reimplemented.


