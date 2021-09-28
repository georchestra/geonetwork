# GeoNetwork externalized authorization framework

This module implements a GeoNetwork plugin that allows to externalize the
source of authoritative accounts, so that GeoNetwork `User`, `Group` domain objects,
and their relationships, are governed by an external provider in a reliable way.

No assumption about the origin of such authorities is made by this module, 
and hence it provides the services to keep internal GeoNetwork `User`s and `Group`s
synchronized with the authoritative source through a single abstraction that
need to be implemented for each particular case: a `CanonicalAccountsRepository`.

The premises under which these services work are the following:

- The external source of authorization MUST be able of handling the identity/identifier
dichotomy for `User` and `Group` entities. Whereas the identity may change (e.g. `User`
and `Group` name), the identifier may not (`User` and `Group` id).

- The external source of authorization MUST also provide, for each canonical `User` and
`Group`, a version-like property, that's used as a quick way to check whether GeoNetwork's
internal version of a given entity is up to date with the canonical, external system's.
The implementation details for this property are left to the provider implementation,
and can be given in the form of a hash string (e.g. hex-encoded SHA1), a timestamp, etc.

- Since GeoNetwork `User` and `Group` ids are internal and assigned by Hibernate,
this module implements the necessary means to keep internal entities in sync with
canonical `User`s and `Group`s.

## Implementing an external Authorization provider

In order to implement a GeoNetwork plugin that yields handling of users and groups
to an external authoritative source, the only requirement is to provide an implementation
of the following interface:

`org.geonetwork.security.external.repository.CanonicalAccountsRepository`

Optionally, several aspects of the internal workings can be controlled by providing
an instance of the `ExternalizedSecurityConfiguration` class, which controls
whether the groups are synchronized with "Organization" or authorization "Roles",
among other things.

### Extension point

This is the signature of the domain repository interface that needs to be implemented:

```java
public interface CanonicalAccountsRepository {
    List<CanonicalUser> findAllUsers();
    List<CanonicalGroup> findAllGroups();
    List<CanonicalGroup> findAllRoles();
    Optional<CanonicalUser> findUserByUsername(String username);
    Optional<CanonicalGroup> findGroupByName(String name);
    Optional<CanonicalGroup> findRoleByName(String name);
}
```

Note both "groups" and "roles" are represented as `CanonicalGroup`. Whether
`findAllGroups()` or `findAllRoles()` will be called by the synchronization
process is a matter of configuration, and defaults to "organizations" mode.

The `CanonicalUser` and `CanonicalGroup` abstractions are used as the authoritative
representations GeoNetwork internal `User` and `Group` object model instances
will be synchronized against.

```
public interface CanonicalUser {
    String getId(); // identity (immutable)
    String getUsername(); //identifier (mutable)
    String getFirstName();
    String getLastName();
    String getOrganization();
    String getLastUpdated();
    String getEmail();
    String getTitle();
    List<String> getRoles();
}
public interface CanonicalGroup {
    String getId(); // identity (immutable)
    String getName(); // identifier (mutable)
    GroupSyncMode getOrigin();
    String getDescription();
    String getLinkage();
    String getLastUpdated();
}
```

### Synchronization mechanism

An `org.geonetwork.security.external.integration.AccountsReconcilingService` 
instance will be contributed to the Spring application context, which has 
the following signature:

```java
public class AccountsReconcilingService {
    public Optional<User> findUpToDateUser(CanonicalUser canonical);
    public User forceMatchingGeonetworkUser(CanonicalUser canonicalUser);
    public void synchronize();
}
```

The `synchronize()` method performs the synchronization of internal GeoNetwork
users and groups with the authoritative versions provided by
`CanonicalAccountsRepository`.

Although it can be triggered manually if needed, it's primary client is
the `ScheduledAccountsSynchronizationService` service, which will
periodically call it based on its configuration (defaults to one minute
after each run, with a 10 seconds initial delay once the application started up).

## A word on Authentication

This module does not care about authentication. 

Authentication is a separate, orthogonal concern to Authorization.

That said, what this module provides is a prescribed way to obtain the user
details once the authentication has been performed, and to ensure the user
information is up to date with its canonical form at the same time.

Whatever the authentication mechanism, the process is to first obtain the
authenticated user's `CanonicalUser`, and then call `findUpToDateUser()`.

This method will return the GeoNetwork `User` only if it exists in the PostgreSQL
database, AND it's information is up to date with the canonical version (by means
of checking both representations' `lastUpdated` property for equality).

In the event that no up-to-date GeoNetwork `User` is returned, then
`forceMatchingGeonetworkUser()` will ensure to return an up-to-date `User`,
synchronizing it with the canonical representation if need be.

### Configuration

The following class contains all the configuration options for this subsystem,
so that the implementor can easily bind it to a `.properties` configuration file:

```
org.geonetwork.security.external.configuration.ExternalizedSecurityConfiguration
```

```
syncMode=orgs|roles
syncRolesFilter=.*
profiles.default=RegisteredUser
profiles.rolemappings.[GN_ADMIN]=Administrator
profiles.rolemappings.[GN_REVIEWER]=Reviewer
profiles.rolemappings.[GN_EDITOR]=Editor
profiles.rolemappings.[GN_USER]=RegisteredUser
scheduled.enabled=true
geonetwork.scheduled.timeUnit = SECONDS # MILLISECONDS/SECONDS/MINUTES/HOURS
scheduled.retryOnFailure = true
scheduled.initialDelay = 5
scheduled.retryDelay = 5
scheduled.delayBetweenRuns = 30
```
