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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.fao.geonet.domain.UserGroup;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.UserLink;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

@Transactional // run each test on a TestTransaction so no manual db cleanup is needed
@DirtiesContext // reset the app context for each test
public class OrgBasedSynchronizationIT extends AbstractAccountsReconcilingServiceIntegrationTest {

    public @Before void setUp_SetSyncModeToOrgs() {
        support.setOrgsSyncMode();
    }

    /**
     * When upgrading from a GeoNetwork version that didn't use this external
     * accounts synchronization subsystem, we want to make sure that existing
     * GeoNetwork users whose username match the canonical users username are kept
     * and linked.
     */
    public @Test void System_version_upgrade_reconciles_existing_users() {
        List<CanonicalUser> users = super.defaultUsers;
        List<CanonicalGroup> orgGroups = super.defaultGroups;
        createOnlyGeonetworkUsersAndGroups(users, orgGroups);

        Map<String, User> existingUsers = getExistingUsers(users);
        Map<String, Group> existingGroups = getExistingGroups(orgGroups);

        // current state is that users and groups exist in GN db but no links to
        // canonical versions exist
        service.synchronize();

        // now, verify the synchronization created the links to external entities,
        // reusing the existing geonetwork users and groups (hence the version upgrade
        // succeeded)
        orgGroups.forEach(gu -> {
            Optional<GroupLink> link = support.groupLinkRepository.findById(gu.getId());
            assertTrue(link.isPresent());
            Group actual = link.get().getGeonetworkGroup();
            Group expected = existingGroups.get(actual.getName());
            assertEquals(expected.getId(), actual.getId());
        });

        users.forEach(cu -> {
            Optional<UserLink> link = support.userLinkRepository.findById(cu.getId());
            assertTrue(link.isPresent());
            User actual = link.get().getInternalUser();
            User expected = existingUsers.get(actual.getUsername());
            assertEquals(expected.getId(), actual.getId());
        });
    }

    private Map<String, Group> getExistingGroups(List<CanonicalGroup> groups) {
        Map<String, Group> existingGroups = new HashMap<>();
        groups.forEach(cg -> assertFalse(support.groupLinkRepository.findById(cg.getId()).isPresent()));
        groups.forEach(cg -> {
            Group g = support.gnGroupRepository.findByName(cg.getName());
            assertNotNull(g);
            existingGroups.put(g.getName(), g);
        });
        return existingGroups;
    }

    private Map<String, User> getExistingUsers(List<CanonicalUser> users) {
        Map<String, User> existingUsers = new HashMap<>();
        users.forEach(cu -> assertFalse(support.userLinkRepository.findById(cu.getId()).isPresent()));
        users.forEach(cu -> {
            User u = support.gnUserRepository.findOneByUsername(cu.getUsername());
            assertNotNull(u);
            existingUsers.put(u.getUsername(), u);
        });
        return existingUsers;
    }

    private void createOnlyGeonetworkUsersAndGroups(List<CanonicalUser> users, List<CanonicalGroup> orgGroups) {
        Map<String, CanonicalGroup> groups = orgGroups.stream()
                .collect(Collectors.toMap(CanonicalGroup::getName, Function.identity()));

        for (CanonicalGroup g : groups.values()) {
            support.addGeonetworkGroup(g);
        }
        for (CanonicalUser u : users) {
            CanonicalGroup group = groups.get(u.getOrganization());
            support.addGeonetworkUser(u, group);
        }
    }

    public @Test void Synchronize_on_empty_geonetwork_db_creates_all_users_and_groups() {
        List<CanonicalUser> users = super.defaultUsers;
        List<CanonicalGroup> orgs = super.defaultGroups;

        assertEquals(0, support.gnUserRepository.count());
        assertEquals(0, support.gnGroupRepository.count());

        service.synchronize();

        users.forEach(cu -> assertTrue(support.userLinkRepository.findById(cu.getId()).isPresent()));
        orgs.forEach(cg -> assertTrue(support.groupLinkRepository.findById(cg.getId()).isPresent()));
    }

    public @Test void Synchronize_updates_group_members_when_organization_members_changed() {
        support.setUpDefaultUsersAndGroups();

        List<CanonicalUser> origUsers = super.defaultUsers;
        List<CanonicalUser> usersRelocatedToOtherOrgs = origUsers.stream().map(this::swapOrg)
                .collect(Collectors.toList());

        when(canonicalAccountsRepositoryMock.findAllUsers()).thenReturn(usersRelocatedToOtherOrgs);

        service.synchronize();

        for (CanonicalUser expected : usersRelocatedToOtherOrgs) {
            UserLink link = support.assertUserLink(expected);
            User actual = link.getInternalUser();
            String orgName = expected.getOrganization();
            CanonicalGroup expectedOrg = canonicalAccountsRepositoryMock.findGroupByName(orgName).get();
            support.assertGroup(actual, expectedOrg);
        }
    }

    private CanonicalUser swapOrg(CanonicalUser user) {
        CanonicalGroup newOrg;
        if (orgC2c.getName().equals(user.getOrganization())) {
            newOrg = orgPsc;
        } else {
            newOrg = orgC2c;
        }
        return super.withOrganization(user, newOrg.getName());
    }

    public @Test void Synchronize_creates_updates_and_deletes_users_and_groups() {
        support.setUpDefaultUsersAndGroups();

        List<CanonicalGroup> groups = new ArrayList<>(super.defaultGroups);

        CanonicalGroup neworg1 = super.createOrg("neworg1");
        CanonicalGroup neworg2 = super.createOrg("neworg2");
        groups.add(neworg1);
        groups.add(neworg2);

        CanonicalGroup removedOrg = super.orgC2c;
        groups.remove(removedOrg);

        final CanonicalGroup changedOrgOrig = super.orgC2c;
        CanonicalGroup changedOrg = super.withName(changedOrgOrig, changedOrgOrig.getName() + "Modified");
        groups.remove(changedOrgOrig);
        groups.add(changedOrg);
        when(canonicalAccountsRepositoryMock.findGroupByName(changedOrgOrig.getName())).thenReturn(Optional.empty());
        when(canonicalAccountsRepositoryMock.findGroupByName(changedOrg.getName())).thenReturn(Optional.of(changedOrg));

        List<CanonicalUser> users = new ArrayList<>(super.defaultUsers);
        CanonicalUser newuser1 = super.createUser("newuser1", changedOrg, roleOrgAdmin);
        CanonicalUser newuser2 = super.createUser("newuser2", neworg1, roleUser);
        users.add(newuser1);
        users.add(newuser2);

        CanonicalUser removedUser = super.testeditor;// the only one belonging to C2C org originally
        users.remove(removedUser);

        CanonicalUser changedUser = super.withRoles(super.testuser, roleAdministrator, roleGnAdmin, roleGnEditor);
        users.remove(super.testuser);
        users.add(changedUser);
        // just to be sure..
        users.forEach(u -> assertNotEquals(u.toString(), removedOrg.getName(), u.getOrganization()));
        users.forEach(u -> assertNotEquals(u.toString(), changedOrgOrig.getName(), u.getOrganization()));

        when(canonicalAccountsRepositoryMock.findAllGroups()).thenReturn(groups);
        when(canonicalAccountsRepositoryMock.findAllUsers()).thenReturn(users);

        service.synchronize();
        verify(users, groups);
    }

    /**
     * When using {@code groups} {@link ExternalizedSecurityProperties#getSyncMode()
     * syncMode}, a GeoNetwork {@link Group} exists for each external system
     * {@link CanonicalAccountsRepository#findAllGroups() group}, and a single
     * {@link UserGroup} exists for each user/group, with its
     * {@link UserGroup#getProfile() profile} set to the highest one matching the
     * user roles.
     */
    public @Test void Synchronized_users_are_bound_to_highest_profile_mapped_from_its_roles() {
        support.setUpDefaultUsersAndGroups();
        for (CanonicalUser user : super.defaultUsers) {
            List<String> roles = user.getRoles();
            Profile expected = support.getProfileMappings().resolveHighestProfileFromRoleNames(roles);
            assertNotNull(expected);
            Optional<UserLink> link = support.userLinkRepository.findById(user.getId());
            assertTrue(link.isPresent());
            User internalUser = link.get().getInternalUser();
            assertEquals(expected, internalUser.getProfile());
        }
    }

    private void verify(List<CanonicalUser> expectedUsers, List<CanonicalGroup> expectedGroups) {
        assertEquals(expectedGroups.size(), support.groupLinkRepository.findAll().size());
        assertEquals(expectedUsers.size(), support.userLinkRepository.findAll().size());

        for (CanonicalGroup expected : expectedGroups) {
            support.assertGroupLink(expected);
        }
        for (CanonicalUser expected : expectedUsers) {
            support.assertUserLink(expected);
        }
    }
}
