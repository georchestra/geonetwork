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

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.UserGroup;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.integration.AbstractAccountsReconcilingServiceIntegrationTest;
import org.georchestra.security.model.Role;
import org.junit.Before;
import org.junit.Test;

public class RoleBasedSynchronizationIT extends AbstractAccountsReconcilingServiceIntegrationTest {

    public @Before void setUp_SetSyncModeToRoles() {
        support.setRolesSyncMode();
    }

    public @Test void testForceMatchingGeonetworkUser() {
//        service.synchronize();
//        List<CanonicalUser> expected = support.loadDefaultGeorchestraUsers();
//        List<User> actual = support.gnUserRepository.findAll();
//        assertEquals(expected.size(), actual.size());
        fail("Not yet implemented");
    }

    /**
     * In {@code datadir/geonetwork/geonetwork.properties}, it is possible to set a
     * Java regular expression to filter which role names are to be mapped to
     * Geonetwork {@link Group groups}
     * <p>
     * 
     */
    public @Test void Role_based_synchronization_respects_regex_filter_from_config() {
        fail("Not yet implemented");
    }

    public @Test void Synchronize_on_empty_geonetwork_db_creates_all_users_and_groups_from_georchestra_roles() {
        fail("Not yet implemented");
    }

    public @Test void Synchronize_updates_group_info_when_role_info_changed_in_georchestra() {
        fail("Not yet implemented");
    }

    public @Test void Synchronize_updates_group_members_when_role_members_changed() {
        service.synchronize();
        // fail("Not yet implemented");
    }

    public @Test void Synchronize_creates_updates_and_deletes_groups_based_on_roles() {
//        List<User> before = support.gnUserRepository.findAll();
//        assertEquals(0, before.size());
//        service.synchronize();
//        List<CanonicalUser> expected = support.loadDefaultGeorchestraUsers();
//        List<User> actual = support.gnUserRepository.findAll();
//        assertEquals(expected.size(), actual.size());
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
     * When using {@code roles}
     * {@link ExternalizedSecurityProperties#getSyncMode() syncMode}, a
     * GeoNetwork {@link Group} exists for each Georchestra {@link Role}, and a
     * {@link UserGroup} exists for each user/group/profile combination.
     */
    public @Test void Synchronized_users_are_bound_to_all_profiles_mapped_from_its_roles() {
        fail("Not yet implemented");
    }
}
