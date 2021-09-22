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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.geonetwork.security.external.integration.AbstractAccountsReconcilingServiceIntegrationTest;
import org.geonetwork.security.external.integration.AccountsReconcilingService;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.UserLink;
import org.georchestra.geonetwork.security.authentication.GeorchestraPreAuthenticationFilter;
import org.junit.Test;

/**
 * {@link AccountsReconcilingService} integration tests suite for
 * {@link GeorchestraPreAuthenticationFilter} usage scenarios, where a
 * {@link CanonicalUser} is received as JSON payload of each HTTP request, and
 * {@link AccountsReconcilingService#findUpToDateUserLink
 * findUpToDateUserLink(GeorchestraUser)} and
 * {@link AccountsReconcilingService#forceMatchingGeonetworkUser
 * forceMatchingGeonetworkUser(GeorchestraUser)} are used to ensure the
 * GeoNetwork account is up to date with the provided canonical representation
 * before proceeding to serve the request.
 * <p>
 * This is important because the only source of truth for authorization is the
 * users, organizations, and roles defined in GeorChestra, and we need to
 * prevent out of date GeoNetwork internal model objects for users and groups to
 * grant unwanted privileges, or fail because a recently created georchestra
 * user is not yet synchronized to geonetwork's internal database.
 */
public class AuthenticatedUserSynchronizationIT extends AbstractAccountsReconcilingServiceIntegrationTest {

    public @Test void up_to_date_user_info_returned_from_link_orgs_group_syncmode() {
        support.setOrgsSyncMode();
        testUptodateAuthenticatedUserSync();
    }

    public @Test void up_to_date_user_info_returned_from_link_roles_group_syncmode() {
        support.setRolesSyncMode();
        testUptodateAuthenticatedUserSync();
    }

    private void testUptodateAuthenticatedUserSync() {
        support.setUpDefaultUsersAndGroups();

        CanonicalUser authenticatedUser = support.loadCanonicalUsers().get(0);
        UserLink link = service.findUpToDateUser(authenticatedUser).orElseThrow(NoSuchElementException::new);
        assertTrue(link.isUpToDateWith(authenticatedUser));

        assertEquals(authenticatedUser.getId(), link.getCanonicalUserId());
        assertEquals(authenticatedUser.getLastUpdated(), link.getLastUpdated());
        support.assertUser(authenticatedUser, link.getInternalUser());
    }

    public @Test void Geonetwork_user_is_created_from_the_authenticated_user() {
        fail("Not yet implemented");
    }

    public @Test void Geonetwork_group_is_created_from_auth_user_org_if_it_doesnt_exist_and_org_syncmode_is_set() {
        fail("Not yet implemented");
    }

    public @Test void Geonetwork_groups_are_created_from_auth_user_roles_dont_exist_and_role_syncmode_is_set() {
        fail("Not yet implemented");
    }

    public @Test void Both_user_and_group_are_created_when_using_group_based_synchronization() {
        support.setOrgsSyncMode();
        fail("Not yet implemented");
    }

    public @Test void Both_user_and_groups_are_created_when_using_role_based_synchronization() {
        support.setRolesSyncMode();
        fail("Not yet implemented");
    }

    public @Test void concurrent_requests_for_non_existing_geonetwork_user_create_it_ony_once() {
        fail("Not yet implemented");
    }

    public @Test void concurrent_requests_for_outdated_geonetwork_user_update_it_ony_once() {
        fail("Not yet implemented");
    }

}
