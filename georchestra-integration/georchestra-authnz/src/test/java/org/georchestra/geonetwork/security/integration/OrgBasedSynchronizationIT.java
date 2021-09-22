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

package org.georchestra.geonetwork.security.integration;

import static org.assertj.core.api.Assertions.fail;

import javax.transaction.Transactional;
import javax.xml.registry.infomodel.Organization;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.UserGroup;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.integration.AbstractAccountsReconcilingServiceIntegrationTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

@Transactional
public class OrgBasedSynchronizationIT extends AbstractAccountsReconcilingServiceIntegrationTest {

    private @Autowired Environment env;

    public @Before void setUp_SetSyncModeToOrgs() {
        support.setOrgsSyncMode();
    }

    public @Test void Synchronize_on_empty_geonetwork_db_creates_all_users_and_groups_from_georchestra_orgs() {
        fail("Not yet implemented");
    }

    public @Test void Synchronize_updates_group_info_when_organization_info_changed_in_georchestra() {
        fail("Not yet implemented");
    }

    public @Test void Synchronize_updates_group_members_when_organization_members_changed() {
        fail("Not yet implemented");
    }

    public @Test void Synchronize_creates_updates_and_deletes_groups() {
        fail("Not yet implemented");
    }

    /**
     * Given the following roles to profiles mappings in datdir's
     * {@code geonetwork.properties}:
     * <p>
     * 
     * <pre>
     * <code>
     * geonetwork.profiles.default=RegisteredUser
     * geonetwork.profiles.rolemappings.[ADMIN]=Administrator
     * geonetwork.profiles.rolemappings.[REVIEWER]=Reviewer
     * geonetwork.profiles.rolemappings.[EDITOR]=Editor
     * geonetwork.profiles.rolemappings.[USER]=RegisteredUser
     * </code>
     * </pre>
     * 
     * When using {@code groups}
     * {@link ExternalizedSecurityProperties#getSyncMode() syncMode}, a
     * GeoNetwork {@link Group} exists for each Georchestra {@link Organization},
     * and a single {@link UserGroup} exists for each user/group, with its
     * {@link UserGroup#getProfile() profile} set to the highest one matching the
     * user roles.
     */
    public @Test void Synchronized_users_are_bound_to_highest_profile_mapped_from_its_roles() {
        fail("Not yet implemented");
    }
}
