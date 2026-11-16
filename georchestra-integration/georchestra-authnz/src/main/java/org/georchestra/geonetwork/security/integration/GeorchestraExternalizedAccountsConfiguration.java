package org.georchestra.geonetwork.security.integration;

import java.net.URI;

import org.geonetwork.security.external.configuration.ExternalizedSecurityConfiguration;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.georchestra.geonetwork.security.integration.keycloak.KeycloakAccountsRepository;
import org.georchestra.geonetwork.security.integration.keycloak.KeycloakAdminClient;
import org.georchestra.geonetwork.security.integration.keycloak.KeycloakProperties;
import org.georchestra.security.api.OrganizationsApi;
import org.georchestra.security.api.RolesApi;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.client.console.GeorchestraConsoleOrganizationsApiImpl;
import org.georchestra.security.client.console.GeorchestraConsoleRolesApiImpl;
import org.georchestra.security.client.console.GeorchestraConsoleUsersApiImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Import({ //
        ExternalizedSecurityConfiguration.class, //
        ExternalizedSecurityPropertiesConfiguration.class//
})
@Lazy
@PropertySource(value = "file:${georchestra.datadir}/geonetwork/geonetwork.properties", ignoreResourceNotFound = false)
public class GeorchestraExternalizedAccountsConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GeorchestraExternalizedAccountsConfiguration.class);

    private @Value("${georchestra.console.url:http://console:8080}") URI consoleBaseURL;

    /**
     * Source of the users, organizations and roles synchronized into GeoNetwork:
     * {@code console} (default) or {@code keycloak}
     */
    private @Value("${georchestra.accounts.source:console}") String accountsSource;

    private @Value("${georchestra.keycloak.url:http://keycloak:8080}") String keycloakUrl;
    private @Value("${georchestra.keycloak.realm:georchestra}") String keycloakRealm;
    private @Value("${georchestra.keycloak.clientId:geonetwork}") String keycloakClientId;
    private @Value("${georchestra.keycloak.clientSecret:}") String keycloakClientSecret;
    private @Value("${georchestra.keycloak.orgGroupPath:}") String keycloakOrgGroupPath;
    private @Value("${georchestra.keycloak.orgCodeAttribute:}") String keycloakOrgCodeAttribute;
    private @Value("${georchestra.keycloak.orgFlagAttribute:}") String keycloakOrgFlagAttribute;
    private @Value("${georchestra.keycloak.syncAllGroups:false}") boolean keycloakSyncAllGroups;
    private @Value("${georchestra.keycloak.orgNameAttribute:}") String keycloakOrgNameAttribute;
    private @Value("${georchestra.keycloak.orgMailAttribute:mail}") String keycloakOrgMailAttribute;
    private @Value("${georchestra.keycloak.orgDescriptionAttribute:description}") String keycloakOrgDescriptionAttribute;
    private @Value("${georchestra.keycloak.orgLinkageAttribute:labeledURI}") String keycloakOrgLinkageAttribute;
    private @Value("${georchestra.keycloak.userTitleAttribute:title}") String keycloakUserTitleAttribute;

    public @Bean CanonicalModelMapper georchestraCanonicalAccountsModelMapper() {
        return new CanonicalModelMapper();
    }

    public @Bean CanonicalAccountsRepository georchestraAccountsRepository() {
        if ("keycloak".equalsIgnoreCase(accountsSource.trim())) {
            KeycloakProperties config = keycloakProperties();
            log.info("Synchronizing GeoNetwork accounts from Keycloak {} (realm {})", config.getUrl(),
                    config.getRealm());
            return new KeycloakAccountsRepository(new KeycloakAdminClient(config.getUrl(), config.getRealm(),
                    config.getClientId(), config.getClientSecret()), config);
        }
        log.info("Synchronizing GeoNetwork accounts from the geOrchestra console {}", consoleBaseURL);
        return new GeorchestraAccountsRepository();
    }

    KeycloakProperties keycloakProperties() {
        KeycloakProperties config = new KeycloakProperties();
        config.setUrl(keycloakUrl);
        config.setRealm(keycloakRealm);
        config.setClientId(keycloakClientId);
        config.setClientSecret(keycloakClientSecret);
        config.setOrgGroupPath(keycloakOrgGroupPath);
        config.setOrgCodeAttribute(keycloakOrgCodeAttribute);
        config.setOrgFlagAttribute(keycloakOrgFlagAttribute);
        config.setSyncAllGroups(keycloakSyncAllGroups);
        config.setOrgNameAttribute(keycloakOrgNameAttribute);
        config.setOrgMailAttribute(keycloakOrgMailAttribute);
        config.setOrgDescriptionAttribute(keycloakOrgDescriptionAttribute);
        config.setOrgLinkageAttribute(keycloakOrgLinkageAttribute);
        config.setUserTitleAttribute(keycloakUserTitleAttribute);
        return config;
    }

//       console app clients, implements OrgsApi, UsersApi, RolesApi, with which 
//       GeorchestraAccountsRepository bridges from the externalized accounts subsystem
//       to georchestra's 
    public @Bean org.georchestra.security.client.console.RestClient georchestraConsoleRestClient() {
        return new org.georchestra.security.client.console.RestClient(consoleBaseURL);
    }

    public @Bean UsersApi georchestraConsoleUsersApiClient() {
        return new GeorchestraConsoleUsersApiImpl(georchestraConsoleRestClient());
    }

    public @Bean OrganizationsApi georchestraConsoleOrgsApiClient() {
        return new GeorchestraConsoleOrganizationsApiImpl(georchestraConsoleRestClient());
    }

    public @Bean RolesApi georchestraRolesApiClient() {
        return new GeorchestraConsoleRolesApiImpl(georchestraConsoleRestClient());
    }

}
