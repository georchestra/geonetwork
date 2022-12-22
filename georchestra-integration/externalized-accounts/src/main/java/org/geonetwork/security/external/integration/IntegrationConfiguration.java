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
package org.geonetwork.security.external.integration;

import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.geonetwork.security.external.repository.RepositoryConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

@Configuration
@Import(RepositoryConfiguration.class)
public class IntegrationConfiguration {

    /**
     * Must be provided by the external system integration configuration
     */
    private @Autowired CanonicalAccountsRepository canonicalAccountsRepository;

    public @Bean UserSynchronizer userSynchronizer() {
        return new UserSynchronizer(canonicalAccountsRepository);
    }

    public @Primary @Bean GroupSynchronizer groupSynchronizer() {
        return new GroupSynchronizerProxy();
    }

    protected @Bean RolesBasedGroupSynchronizer rolesGroupSynchronizer() {
        return new RolesBasedGroupSynchronizer(canonicalAccountsRepository);
    }

    protected @Bean OrgsBasedGroupSynchronizer orgsGroupSynchronizer() {
        return new OrgsBasedGroupSynchronizer(canonicalAccountsRepository);
    }

    protected @Bean LogoUpdater logoUpdater() {
        return new LogoUpdater(canonicalAccountsRepository);
    }

}
