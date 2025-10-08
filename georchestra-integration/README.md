# Integrating GeoNetwork as a native Georchestra service

## GeoNetwork spring configuration approach

GeoNetwork's approach to setting up the Spring application context is rather
involved. Here's a recap of how it's set up:

Traditional servlet/spring integration starts with setting up `springSecurityFilterChain`
servlet filter in `WEB-INF/web.xml`.

In this case it's a `jeeves.config.springutil.JeevesDelegatingFilterProxy`,
which will set up the application context to be loaded from a number of xml files
with different look up strategies:

* `src/main/webResources/WEB-INF/config-spring-geonetwork.xml`
* `src/main/webapp/WEB-INF/config-spring-geonetwork-parent.xml`

Instead of scattering georchestra specific changes all over the place inside 
`web/src/main/webapp/WEB-INF/` and/or `web/src/main/webResources/WEB-INF/`,
we should ideally be able to make almost no changes to those files (and hence
keep the web app module as vanilla as possible), since there are over 30 spring config
files over there already and it's quite confusing.

### Infrastructure configuration

Internal application business spring beans will be configured from a 
`config-spring-geonetwork.xml` config file at the classpath root, requiring
no further changes to any original geonetwork config file.

### Authentication entry point configuration

Authentication specific configuratio is to be placed in a `config-security-georchestra.xml`
file, and should contain no application business bean definitions.

This requires the following customization to
`webapp/WEB-INF/config-security/config-security.xml` though:

```xml
<import resource="classpath:config-security-georchestra.xml"/>
```
