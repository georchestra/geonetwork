/*
 * Copyright (C) 2009-2025 by the geOrchestra PSC
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

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.assertj.core.util.Sets;
import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.User;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.UserLink;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

@Transactional // run each test on a TestTransaction so no manual db cleanup is needed
@DirtiesContext // reset the app context for each test
public class RoleBasedSynchronizationIT extends AbstractAccountsReconcilingServiceIntegrationTest {

    public @Before void setUp_SetSyncModeToRoles() {
        support.setRolesSyncMode();
    }

    /**
     * When upgrading from a GeoNetwork version that didn't use this external
     * accounts synchronization subsystem, we want to make sure that existing
     * GeoNetwork users whose username match the canonical users username are kept
     * and linked.
     */
    public @Test void System_version_upgrade_reconciles_existing_users() {
        List<CanonicalUser> users = super.defaultUsers;
        List<CanonicalGroup> roles = super.defaultRoles;
        createOnlyGeonetworkUsersAndGroupsFromRoles(users, roles);

        Map<String, User> existingUsers = getExistingUsers(users);

        // current state is that users and groups exist in GN db but no links to
        // canonical versions exist
        service.synchronize();

        // now, verify the synchronization created the links to external entities,
        // reusing the existing geonetwork users and groups (hence the version upgrade
        // succeeded)
        roles.forEach(gu -> {
            Optional<GroupLink> link = support.groupLinkRepository.findById(gu.getId());
            assertTrue(link.isEmpty());
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

    private void createOnlyGeonetworkUsersAndGroupsFromRoles(List<CanonicalUser> users,
            List<CanonicalGroup> roleGroups) {
        Map<String, CanonicalGroup> groups = roleGroups.stream().collect(toMap(CanonicalGroup::getName, identity()));

        for (CanonicalGroup g : groups.values()) {
            support.addGeonetworkGroup(g);
        }
        for (CanonicalUser u : users) {
            CanonicalGroup group = groups.get(u.getOrganization());
            support.addGeonetworkUser(u, group);
        }
    }

    public @Test void Synchronize_on_empty_geonetwork_db_creates_all_users_and_groups_from_roles() {
        List<CanonicalUser> users = super.defaultUsers;
        //Roles are empty due to removing default roles
        List<CanonicalGroup> roles = new ArrayList<>();

        assertEquals(0, support.gnUserRepository.count());
        assertEquals(0, support.gnGroupRepository.count());

        service.synchronize();
        verify(users, roles);
    }

    private CanonicalUser swapRoles(CanonicalUser user, List<CanonicalGroup> newRoles) {
        return super.withRoles(user, newRoles.toArray(new CanonicalGroup[newRoles.size()]));
    }

    /**
     * In
     * {@link ExternalizedSecurityProperties#setSyncRolesFilter(java.util.regex.Pattern)},
     * it is possible to set a Java regular expression to filter which role names
     * are to be mapped to Geonetwork {@link Group groups}.
     * <p>
     * Also, if the configured pattern contains groups, the {@link CanonicalGroup}s
     * used will be renamed to match the groups, usually to filter out prefixes from
     * the role names, like in {@literal GN_(.*)} will strip off the {@literal GN_}
     * prefix.
     */
    public @Test void Role_based_synchronization_respects_regex_filter_from_config_and_applies_pattern_group_filter() {
        ExternalizedSecurityProperties config = support.getConfig();
        config.setSyncRolesFilter(Pattern.compile("PSC_(.*)"));

        CanonicalGroup psc1 = super.createRole("PSC_COMMUNITY");
        CanonicalGroup psc2 = super.createRole("PSC_GEOCOM");
        List<CanonicalGroup> roles = super.defaultRoles;
        roles.add(super.createRole("NOPSC_BUILDINGS"));
        roles.add(psc1);
        roles.add(psc2);


        service.synchronize();

        Set<CanonicalGroup> origGroups = rolesMatchingPattern(config, Set.of(psc1, psc2));
        Set<CanonicalGroup> syncedGroups = getSavedCanonicalGroups();
        assertEquals(origGroups.size(), syncedGroups.size());
        assertNotEquals("group names should differ due to pattern grouping", origGroups, syncedGroups);

        Set<CanonicalGroup> expectedGroups = stripOffRolePrefixFromGroupNames(origGroups);
        assertEquals(expectedGroups, syncedGroups);

        List<CanonicalUser> expectedUsers = super.defaultUsers;
        List<UserLink> syncedUsers = support.userLinkRepository.findAll();
        assertEquals(expectedUsers.size(), syncedUsers.size());

        Map<String, CanonicalGroup> expectedGroupsByName = expectedGroups.stream()
                .collect(toMap(CanonicalGroup::getName, identity()));
        for (CanonicalUser expectedUser : expectedUsers) {
            UserLink link = support.assertUserLink(expectedUser);

            List<String> expectedUserGroupNames = expectedUser.getRoles().stream().filter(config::matchesRoleNameFilter)
                    .collect(toList());
            for (String roleGroupName : expectedUserGroupNames) {
                roleGroupName = roleGroupName.replace("GN_", "");
                CanonicalGroup canonicalGroup = expectedGroupsByName.get(roleGroupName);
                support.assertGroup(link.getInternalUser(), canonicalGroup);
            }
        }
    }

    public @Test void Role_correctly_added_with_editor_privilege() {
        support.synchronizeDefaultUsersAndGroups();
        ExternalizedSecurityProperties config = support.getConfig();
        config.setSyncRolesFilter(Pattern.compile("(.*)"));

        List<CanonicalGroup> roles = new ArrayList<>();
        CanonicalGroup newrole1;
        roles.add(newrole1 = super.createRole("MySuperNewRole"));


        List<CanonicalUser> users = new ArrayList<>(super.defaultUsers);
        users.add(super.setUpNewUser("newuser1", orgPsc,  newrole1, roleGnEditor));

        when(canonicalAccountsRepositoryMock.findAllRoles()).thenReturn(roles);
        when(canonicalAccountsRepositoryMock.findAllUsers()).thenReturn(users);

        service.synchronize();
        verify(users, roles);
        //TODO find a way to read privileges
    }

    private Set<CanonicalGroup> getSavedCanonicalGroups() {
        Set<CanonicalGroup> syncedGroups = support.groupLinkRepository.findAll().stream().map(GroupLink::getCanonical)
                .collect(toSet());
        return syncedGroups;
    }

    private Set<CanonicalGroup> stripOffRolePrefixFromGroupNames(Set<CanonicalGroup> origGroups) {
        Set<CanonicalGroup> expectedGroups = origGroups.stream()
                .map(g -> CanonicalGroup.builder().init(g).withName(g.getName().replace("PSC_", "")).build())
                .collect(toSet());
        return expectedGroups;
    }

    private Set<CanonicalGroup> rolesMatchingPattern(ExternalizedSecurityProperties config, Set<CanonicalGroup> expectedGroups) {
        Set<CanonicalGroup> origGroups = super.defaultRoles.stream().filter(r -> !RolesBasedGroupSynchronizer.georchestraDefaultRoleNames.contains(r.getName()))
                .filter(r -> config.matchesRoleNameFilter(r.getName())).collect(toSet());
        assertEquals(10, super.defaultRoles.size());
        assertEquals(2, origGroups.size());
        assertEquals("preflight check failed", expectedGroups, origGroups);
        return origGroups;
    }

    private void verify(List<CanonicalUser> expectedUsers, List<CanonicalGroup> expectedGroupsFromRoles) {
        assertEquals(expectedGroupsFromRoles.size(), support.groupLinkRepository.findAll().size());
        assertEquals(expectedUsers.size(), support.userLinkRepository.findAll().size());

        for (CanonicalGroup expected : expectedGroupsFromRoles) {
            support.assertGroupLink(expected);
        }
        for (CanonicalUser expected : expectedUsers) {
            support.assertUserLink(expected);
        }
    }
}
