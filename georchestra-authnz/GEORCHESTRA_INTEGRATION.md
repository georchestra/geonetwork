# GeoNetwork integration with Georchestra pre-authentication and authorization

This module integrates *GeoNetwork* user details and authorization into *geOrchestra*,
with *geOrchestra* providing all the necessary pre-authentication and authorization
credentials on each HTTP request, through `security-proxy` application gateway's
HTTP request headers.

That means there's no need to configure other GeoNetwork components to match or
synchronize the User and Organization properties and credentials, with
geOrchestra acting as the one source of truth for user management.

## Background

*GeoNetwork* **requires** its users to be defined in the same PostgreSQL database
where it stores the metadata records and other stuff.

*geOrchestra* maintains its own user database in LDAP, and is to be taken as the
canonical representation of user details.

Traditionally, *GeoNetwork* is customized to fetch user details from LDAP, and
periodically synchronize the users in Postgres from LDAP. This comes with some
caveats, as the synchronization process is performed in a `cron-like` fashion,
and hence there's a time window where Postgres users are potentially out of sync
with LDAP users.

Also, *geOrchestra* users are identified by their username, and GeoNetwork users
by a unique integer id. Since a username may be changed, the synchronization process
will leave Postgres users dangling and create new ones?

Finally, the proliferation of direct access to LDAP to get user details
among the different *geOrchestra* applications only makes the whole echosystem
harder to maintain, whereas ideally it should be an implementation detail managed
by a single component/application (`console`?), and have a system boundary
for the rest of applications (interface and common object model, for example).

Within geOrchestra's environment, all HTTP requests to GeoNetwork come from the 
`security-proxy` application gateway, which now has the ability to send an 
arbitrary (configurable) set of user properties as HTTP headers. This opens up
the possibility for backend services to be completely decoupled from LDAP and
be given all the information they need about users/groups.

The component that integrates geOrchestra authZN with GeoNetwork can automate
the link between GN User entities and pre-authenticated users through a provided
unique identifier. 

The link from "geOrchestra user id" to "GeoNetwork user id" can be managed as a
JPA entity, and the current-ness of the Postgres user entity with regard to the
provided user details be quickly checked on-demand (e.g. through a "last-updated"
property, or heuristically by comparing relevant user properties), hence eliminating
the need for cron-like sychronization processes and the attached out-of-sync time
window.

## Risks

- `security-proxy` **must** be able to provide a unique user identifier,
`sec-username` is not good engouh as it can be changed. We're not sure yet
whether a stable UUID will be provided by LDAP? Mitigation strategy: by getting
rid of LDAP in GeoNetwork and using only the provided pre-auth credentials, we can
do whatever we want in geOrchestra's `console` app to have stable user ids, without
affecting other systems.
- The component integrates geOrchestra authZN with GeoNetwork can automate
the link

## Feature set



## Architecture

With `org.georchestra.geonetwork.security` as base package:

````
authentication
     |
     | <uses>
     V
 integration
     |
     | <uses>
     V
 repository ----> org.fao.geonet.domain.georchestra
````


## Pre-authentication

- `security-proxy` sends the necessary HTTP request headers (`sec-*`) to
handle the pre-authenticated user's identity and privileges.

- GeoNetwork's `security-proxy` headsers **MUST** be configured to send
a user's unique identifier (`sec-userid:string`) among the authN/authZ headers, 
which is used to map GeoNetwork users in its PostgreSQL database 1:1 
with geOrchestra users.

- The `security-proxy-spring-integration` dependency provides the 
pre-authentication Servlet filter.


# User and Group synchronization



## Roles and Groups

*geOrchestra* provides user privileges through user roles in the `sec-roles` header.
For example `sec-roles: ADMINISTRATOR; GN_ADMIN`.

These roles need to be mapped to a pair of GeoNetwork "Group" and "Profile".
The Group is identified by its `name`, and the profile to one of the constants 
`Administrator`, `UserAdmin`, `Reviewer`, `Editor`, `RegisteredUser`, 
`Guest`, `Monitor`. "Profiles" are organized hierarchically by it's `parents` 
property as follows:

```
   Administrator
        ^
     ___|_____
    |         |
UserAdmin  Monitor
   ^
   |
Reviewer
   ^
   |
Editor
   ^
   |
RegisteredUser
   ^
   |
 Guest
```

Mapping geOr roles to Profiles in 3.0.x is done through the `ldapUserContextMapper:LDAPUserDetailsContextMapperWithProfileSearch` bean
defined in `config/config-security-georchestra.xml`:

```
ADMIN -> Administrator
REVIEWER -> Reviewer
EDITOR -> Editor
USER -> RegisteredUser
```

---

TODO:

* Remove all ldap related configuration. From `web/src/main/webapp/WEB-INF/config-security/config-security-georchestra.xml`:
    * `ldapAuthenticationProviderPostProcessor`
    * `ldapUtils`
    * `contextSource`
    * `ldapAuthProvider`
    * `ldapUserContextMapper`
    * `ldapManager`/`ldapUserDetailsService`
    * `ldapSynchronizer`
    * `userMapper`
    * `ldapUserSearch`
* Configure georchestra's pre-auth filter and on-demand user synchronizer:
  * From `web/src/main/webapp/WEB-INF/config-security/config-security-georchestra.xml`, replace `preAuthenticationFilter` and `preFilterAuthenticationProvider` by the ones that take full AuthN/AuthZ credentials from request headers and forces matching the geonetwork user info with the georchestra user credentials.
  
* Replace all ldap related configuration in geonetwork's data directory (from georchestra), it would only require sending the
appropriate sec-* headers (like with the datafeeder app).

    