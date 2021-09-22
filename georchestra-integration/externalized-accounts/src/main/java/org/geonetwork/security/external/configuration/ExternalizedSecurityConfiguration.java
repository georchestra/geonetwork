/*
 * Copyright (C) 2021 by the geOrchestra PSC
 *
 * This file is part of geOrchestra.
 *
 * geOrchestra is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * geOrchestra is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * geOrchestra.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.geonetwork.security.external.configuration;

import org.geonetwork.security.external.integration.AccountsReconcilingService;
import org.geonetwork.security.external.integration.IntegrationConfiguration;
import org.geonetwork.security.external.integration.ScheduledAccountsSynchronizationService;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.PropertyResolver;

/**
 * Spring-framework {@link Configuration @Configuration} to create the service
 * beans to subjugate GeoNetwork users, groups, and profile assignment, to some
 * external system.
 * <p>
 * Namely, the following service beans will be provided:
 * {@link AccountsReconcilingService} and
 * {@link ScheduledAccountsSynchronizationService}.
 * <p>
 * This configuration is incomplete by design. In order to complete the
 * integration with an external authority, such as a REST API, an LDAP database,
 * etc., the integrator must contribute the following additional beans to the
 * spring application context:
 * <p>
 * <ul>
 * <li>{@link CanonicalAccountsRepository} to fetch canonical user, group, and
 * role objects from the external system
 * <li>Optionally (but recommended), an {@link ExternalizedSecurityProperties}
 * config bean set up with the synchronization preferences with the external
 * system. This class can be constructed from an external {@code .properties}
 * file or directly from Spring-framework's {@link PropertyResolver} (the app
 * context).
 * </ul>
 */
@Configuration
@Import({ IntegrationConfiguration.class })
public class ExternalizedSecurityConfiguration {

    /**
     * Default {@link ExternalizedSecurityProperties} to use if no other,
     * external-system specific bean of the same type has been provided.
     */
    @Order(Ordered.LOWEST_PRECEDENCE)
    public @Bean ExternalizedSecurityProperties defaultExternalizedSecurityConfigProperties() {
        return new ExternalizedSecurityProperties();
    }

    public @Bean AccountsReconcilingService accountsReconcilingService() {
        return new AccountsReconcilingService();
    }

    public @Bean ScheduledAccountsSynchronizationService scheduledAccountsSynchronizationService() {
        return new ScheduledAccountsSynchronizationService();
    }
}
