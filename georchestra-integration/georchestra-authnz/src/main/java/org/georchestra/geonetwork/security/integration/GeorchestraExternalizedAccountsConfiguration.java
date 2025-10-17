package org.georchestra.geonetwork.security.integration;

import java.net.URI;

import org.geonetwork.security.external.configuration.ExternalizedSecurityConfiguration;
import org.georchestra.security.api.OrganizationsApi;
import org.georchestra.security.api.RolesApi;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.client.console.GeorchestraConsoleOrganizationsApiImpl;
import org.georchestra.security.client.console.GeorchestraConsoleRolesApiImpl;
import org.georchestra.security.client.console.GeorchestraConsoleUsersApiImpl;
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

    private @Value("${georchestra.console.url:http://console:8080}") URI consoleBaseURL;

    public @Bean CanonicalModelMapper georchestraCanonicalAccountsModelMapper() {
        return new CanonicalModelMapper();
    }

    public @Bean GeorchestraAccountsRepository georchestraAccountsRepository() {
        return new GeorchestraAccountsRepository();
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
