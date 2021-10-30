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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.fao.geonet.domain.UserGroup;
import org.fao.geonet.repository.UserGroupRepository;
import org.fao.geonet.repository.specification.UserGroupSpecs;
import org.geonetwork.security.external.configuration.ProfileMappingProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

/**
 * {@link AccountsReconcilingService} integration tests suite for
 * pre-authentication usage scenarios, where a {@link CanonicalUser} is received
 * as a pre-authenticated payload, and
 * {@link AccountsReconcilingService#findUpToDateUserLink findUpToDateUserLink}
 * and
 * {@link AccountsReconcilingService#forceMatchingGeonetworkUser(CanonicalUser)
 * forceMatchingGeonetworkUser} are used to ensure the GeoNetwork account is up
 * to date with the provided canonical representation before proceeding to serve
 * the request.
 * <p>
 * This is important because the only source of truth for authorization is the
 * users, organizations, and roles defined by the external system, and we need
 * to prevent out of date GeoNetwork internal model objects for users and groups
 * to grant unwanted privileges, or fail because a recently created user is not
 * yet synchronized to geonetwork's internal database.
 */
@Transactional // run each test on a TestTransaction so no manual db cleanup is needed
@DirtiesContext // reset the app context for each test
public class AuthenticatedFullUserSynchronizationIT extends AbstractAccountsReconcilingServiceIntegrationTest {

    public @Test void up_to_date_user_info_returned_from_link() {
        CanonicalUser existingUser = super.testadmin;
        assertFalse(service.findUpToDateUser(existingUser).isPresent());

        support.synchronizeDefaultUsersAndGroups();

        Optional<User> user = service.findUpToDateUser(existingUser);
        assertTrue(user.isPresent());
        support.assertUser(existingUser, user.get());
    }

    /**
     * If any canonical user property changed, it's the
     * {@link CanonicalAccountsRepository provider}'s responsibility to set a
     * different {@link CanonicalUser#getLastUpdated() lastUpdated} value.
     */
    public @Test void up_to_date_user_not_found_if_existing_user_lastupdated_property_changed() {
        support.synchronizeDefaultUsersAndGroups();

        CanonicalUser existingUserUnchanged = super.testadmin;
        assertTrue(service.findUpToDateUser(existingUserUnchanged).isPresent());

        CanonicalUser existingUserChanged = CanonicalUser.builder()//
                .init(existingUserUnchanged)//
                .withLastUpdated(UUID.randomUUID().toString()).build();

        assertFalse(service.findUpToDateUser(existingUserChanged).isPresent());
    }

    public @Test void Geonetwork_user_is_created_from_the_authenticated_user() {
        support.synchronizeDefaultUsersAndGroups();

        CanonicalUser stillNotSyncedUser = super.setUpNewUser("newuser", orgC2c, roleGnEditor);

        assertFalse(service.findUpToDateUser(stillNotSyncedUser).isPresent());

        User user = service.forceMatchingGeonetworkUser(stillNotSyncedUser);
        support.assertUser(stillNotSyncedUser, user);

        assertEquals(user, service.findUpToDateUser(stillNotSyncedUser).orElseThrow(NoSuchElementException::new));
    }

    public @Test void Geonetwork_group_is_created_from_auth_user_org_if_it_doesnt_exist_and_org_syncmode_is_set() {
        support.setOrgsSyncMode();
        support.synchronizeDefaultUsersAndGroups();

        CanonicalUser existingUserUnchanged = super.testadmin;

        CanonicalGroup newOrg = super.createOrg("neworg");
        CanonicalUser existingUserWithChangedOrg = withOrganization(existingUserUnchanged, newOrg.getName());

        assertTrue(service.findUpToDateUser(existingUserUnchanged).isPresent());
        assertFalse(service.findUpToDateUser(existingUserWithChangedOrg).isPresent());

        // make the synchronizer find the new org that was not yet synchronized
        when(canonicalAccountsRepositoryMock.findOrganizationByName(newOrg.getName())).thenReturn(Optional.of(newOrg));

        User user = service.forceMatchingGeonetworkUser(existingUserWithChangedOrg);
        support.assertUser(existingUserWithChangedOrg, user);
        support.assertGroup(user, newOrg);
    }

    public @Test void Geonetwork_groups_are_created_from_auth_user_roles_dont_exist_and_role_syncmode_is_set() {
        support.setRolesSyncMode();
        support.synchronizeDefaultUsersAndGroups();

        final CanonicalUser existingUserUnchanged = super.testuser;
        final CanonicalGroup newRole1 = super.createRole("NEW_ROLE_1");
        final CanonicalGroup newRole2 = super.createRole("NEW_ROLE_2");

        CanonicalUser existingUserWithUnsyncRoles = withRoles(existingUserUnchanged, newRole1, newRole2);

        assertTrue(service.findUpToDateUser(existingUserUnchanged).isPresent());
        assertFalse(service.findUpToDateUser(existingUserWithUnsyncRoles).isPresent());

        // make the synchronizer find the new roles that were not yet synchronized at
        // the time the authenticated user hit the app
        when(canonicalAccountsRepositoryMock.findRoleByName(newRole1.getName())).thenReturn(Optional.of(newRole1));
        when(canonicalAccountsRepositoryMock.findRoleByName(newRole2.getName())).thenReturn(Optional.of(newRole2));

        User user = service.forceMatchingGeonetworkUser(existingUserWithUnsyncRoles);
        support.assertUser(existingUserWithUnsyncRoles, user);
        support.assertGroup(user, newRole1);
        support.assertGroup(user, newRole2);
    }

    public @Test void an_organization_name_may_have_changed_but_it_must_refer_to_the_same_group() {
        support.setOrgsSyncMode();
        support.synchronizeDefaultUsersAndGroups();

        CanonicalUser existingUserUnchanged = super.testadmin;
        final String oldOrgName = existingUserUnchanged.getOrganization();
        final CanonicalGroup orgWithOldName = super.defaultGroups.stream().filter(g -> g.getName().equals(oldOrgName))
                .findFirst().orElseThrow(IllegalStateException::new);

        final String newOrgName = oldOrgName + "_MODIFIED";
        final CanonicalGroup orgRenamed = withName(orgWithOldName, newOrgName);
        final CanonicalUser existingUserWithRenamedOrg = withOrganization(existingUserUnchanged, newOrgName);

        assertTrue(service.findUpToDateUser(existingUserUnchanged).isPresent());
        assertFalse(service.findUpToDateUser(existingUserWithRenamedOrg).isPresent());

        // make the synchronizer find the new org that was not yet synchronized
        when(canonicalAccountsRepositoryMock.findOrganizationByName(oldOrgName)).thenReturn(Optional.empty());
        when(canonicalAccountsRepositoryMock.findOrganizationByName(orgRenamed.getName()))
                .thenReturn(Optional.of(orgRenamed));

        User user = service.forceMatchingGeonetworkUser(existingUserWithRenamedOrg);
        support.assertUser(existingUserWithRenamedOrg, user);
        support.assertGroup(user, orgRenamed);
    }

    public @Test void role_names_may_have_changed_but_them_must_refer_to_the_same_groups() {
        support.setRolesSyncMode();
        support.synchronizeDefaultUsersAndGroups();

        final CanonicalUser userUnchanged = super.testadmin;
        final CanonicalGroup[] renamedRoles = userUnchanged.getRoles().stream()
                .map(canonicalAccountsRepositoryMock::findRoleByName).map(Optional::get)
                .map(g -> withName(g, g.getName() + "_CHANGED")).toArray(CanonicalGroup[]::new);
        assertTrue(renamedRoles.length > 0);

        CanonicalUser userWithRenamedRoles = withRoles(userUnchanged, renamedRoles);

        assertTrue(service.findUpToDateUser(userUnchanged).isPresent());
        assertFalse(service.findUpToDateUser(userWithRenamedRoles).isPresent());

        // make the synchronizer find the new roles that were not yet synchronized at
        // the time the authenticated user hit the app
        for (CanonicalGroup renamedRole : renamedRoles) {
            when(canonicalAccountsRepositoryMock.findRoleByName(renamedRole.getName()))
                    .thenReturn(Optional.of(renamedRole));
        }

        User user = service.forceMatchingGeonetworkUser(userWithRenamedRoles);
        support.assertUser(userWithRenamedRoles, user);
        for (CanonicalGroup renamedRole : renamedRoles) {
            support.assertGroup(user, renamedRole);
        }
    }

    public @Test void Both_user_and_group_are_created_when_using_group_based_synchronization() {
        support.setOrgsSyncMode();
        support.synchronizeDefaultUsersAndGroups();

        CanonicalGroup newOrg = super.createOrg("neworg");
        CanonicalUser newUser = super.setUpNewUser("newuser", newOrg, super.roleUser);

        assertFalse(service.findUpToDateUser(newUser).isPresent());

        // make the synchronizer find the new org that was not yet synchronized
        when(canonicalAccountsRepositoryMock.findOrganizationByName(newOrg.getName())).thenReturn(Optional.of(newOrg));

        User user = service.forceMatchingGeonetworkUser(newUser);
        support.assertUser(newUser, user);
        support.assertGroup(user, newOrg);
    }

    public @Test void Both_user_and_groups_are_created_when_using_role_based_synchronization() {
        support.setRolesSyncMode();

        support.synchronizeDefaultUsersAndGroups();

        CanonicalGroup newRole1 = super.createRole("newrole1");
        CanonicalGroup newRole2 = super.createRole("newrole2");
        CanonicalUser newUser = super.setUpNewUser("newuser", super.orgC2c, newRole1, newRole2, super.roleGnReviewer);

        assertFalse(service.findUpToDateUser(newUser).isPresent());

        // make the synchronizer find the new roles that were not yet synchronized
        when(canonicalAccountsRepositoryMock.findRoleByName(newRole1.getName())).thenReturn(Optional.of(newRole1));
        when(canonicalAccountsRepositoryMock.findRoleByName(newRole2.getName())).thenReturn(Optional.of(newRole2));

        User user = service.forceMatchingGeonetworkUser(newUser);
        support.assertUser(newUser, user);
        support.assertGroup(user, newRole1);
        support.assertGroup(user, newRole2);
    }

    public @Test void User_with_no_organization_is_valid_and_its_default_role_is_mapped_from_config() {
        final CanonicalUser userWithNoOrg = super.testreviewer;
        assertEquals(Collections.singletonList("GN_REVIEWER"), userWithNoOrg.getRoles());
        ProfileMappingProperties profiles = support.getConfig().getProfiles();
        profiles.getRolemappings().put("GN_REVIEWER", Profile.Reviewer);

        support.setOrgsSyncMode();

        assertFalse(service.findUpToDateUser(userWithNoOrg).isPresent());

        User user = service.forceMatchingGeonetworkUser(userWithNoOrg);
        support.assertUser(userWithNoOrg, user);
        assertEquals(Profile.Reviewer, user.getProfile());

        UserGroupRepository userGroupRepo = support.gnUserGroupRepository;
        List<UserGroup> userGroups = userGroupRepo.findAll(UserGroupSpecs.hasUserId(user.getId()));
        assertTrue(userGroups.isEmpty());
    }
}
