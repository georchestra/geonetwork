# GeoNetwork integration with Georchestra pre-authentication and authorization

This module integrates *GeoNetwork* user details and authorization into *geOrchestra*.

* A periodic asynchronous job takes care of keeping all GeoNetwork users in sync with
geOrchestra users, and all GeoNetwork groups in sync with geOrchestra's
Organizations or Roles, depending on configuration.

* Authentication is provided by geOrchestra's security proxy, as a fully pre-authenticated
and pre-authorized `GeorchestraUser` object, through the `sec-user` HTTP request
header, in the form of a base-64 encoded JSON object.

* The `GeorchestraPreAuthenticationFilter` servlet filter ensures GeoNetwork's
internal `User` object is up to date with the provided pre-authorized user details,
regardless of the time-window between scheduled synchronization job runs.

* The actual store for users, organizations, and roles, is hidden from this module,
and hence from GeoNetwork as a whole. Whether authorization details come from an LDAP
database or anything else is completely transparent to this module. Instead, it integrates
with geOrchestra by calling its `console` application REST API, through the
`GeorchestraAccountsRepository` implementation of `CanonicalAccountsRepository`.

* The recent extension of geOrchestra's accounts object model to incorporate stable
UUID identifiers to users, organizations, and roles, solves the issue of dangling
GeoNetwork accounts, may names change over time (e.g. through georchestra's console web UI).

## Internal working details

This module relies on `org.geonetwork-opensource:gn-externalized-accounts`
plugin. 

Please refer to that project's [README](../externalized-accounts/README.md)
for more details.


## Installation

GeoNetwork's `web/pom.xml` is modified to add this module as a dependency:

```xml
    <dependency>
      <groupId>${project.groupId}</groupId>
      <artifactId>gn-georchestra-authnz</artifactId>
      <version>${project.version}</version>
    </dependency>
```

## Configuration

Configuration is performed using traditional georchestra's "datadirectory"
mechanism, using the `<datadir>/geonetwork/geonetwork.properties` file,
with the following properties available:

```
# Base URL for the console application REST API
georchestra.console.url=http://console:8080

# Group synchronization mode.
# Defines whether to synchronize GeoNetwork Groups with Georchestra Organizations or Roles.
# Allowed values are 'orgs' and 'roles'. Defaults to 'orgs', meaning each synchronized
# User will be matched to one GeoNetwork Group, which in turn matches the user's organization.
# A value of 'roles' means GeoNetwork Groups will be synchronized with Georchestra roles instead
# of organizations, and Users will be synchronized so that they belong to all the Groups that match
# its roles
geonetwork.syncMode=orgs

# If using 'roles' sync mode, a Java regular expression can be used to filter
# which Georchestra roles are to be mapped to GeoNetwork groups. Only those role names
# that march the regular expression will be mapped.
geonetwork.syncRolesFilter=GN_(.*)

# Map geOrchestra user role names to GeoNetwork user profiles.
# Available GN profile names are:
# Administrator, Reviewer, Editor, RegisteredUser, Guest, Monitor
geonetwork.profiles.default=RegisteredUser
geonetwork.profiles.rolemappings.[GN_ADMIN]=Administrator
geonetwork.profiles.rolemappings.[GN_REVIEWER]=Reviewer
geonetwork.profiles.rolemappings.[GN_EDITOR]=Editor
geonetwork.profiles.rolemappings.[GN_USER]=RegisteredUser

geonetwork.scheduled.enabled=true
# MILLISECONDS/SECONDS/MINUTES/HOURS
geonetwork.scheduled.timeUnit = SECONDS
geonetwork.scheduled.retryOnFailure = true
geonetwork.scheduled.initialDelay = 10
geonetwork.scheduled.retryDelay = 10
geonetwork.scheduled.delayBetweenRuns = 60

```
