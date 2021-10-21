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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Optional;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.fao.geonet.domain.UserGroup;
import org.fao.geonet.repository.GroupRepository;
import org.fao.geonet.repository.UserGroupRepository;
import org.fao.geonet.repository.UserRepository;
import org.fao.geonet.repository.specification.UserGroupSpecs;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.configuration.ProfileMappingProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.model.UserLink;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.geonetwork.security.external.repository.GroupLinkRepository;
import org.geonetwork.security.external.repository.UserLinkRepository;
import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

public class IntegrationTestSupport extends ExternalResource {

    public @Autowired UserRepository gnUserRepository;
    public @Autowired GroupRepository gnGroupRepository;
    public @Autowired UserGroupRepository gnUserGroupRepository;

    public @Autowired UserLinkRepository userLinkRepository;
    public @Autowired GroupLinkRepository groupLinkRepository;

    public @Autowired CanonicalAccountsRepository canonicalAccounts;

    public @Autowired AccountsReconcilingService service;
    private @Autowired ExternalizedSecurityProperties configProps;

    public @Autowired UserSynchronizer userSynchronizer;
    public @Autowired GroupSynchronizer groupSynchronizer;

    protected @Override void after() {
        // do nothing
    }

    public void disableScheduledSynchronization() {
        this.configProps.getScheduled().setEnabled(false);
    }

    public void enableScheduledSynchronization() {
        this.configProps.getScheduled().setEnabled(true);
    }

    public void setOrgsSyncMode() {
        configProps.setSyncMode(GroupSyncMode.orgs);
    }

    public void setRolesSyncMode() {
        configProps.setSyncMode(GroupSyncMode.roles);
    }

    public ProfileMappingProperties getProfileMappings() {
        return configProps.getProfiles();
    }

    public ExternalizedSecurityProperties getConfig() {
        return configProps;
    }

    public List<CanonicalUser> loadCanonicalUsers() {
        return canonicalAccounts.findAllUsers();
    }

    public List<CanonicalGroup> loadCanonicalRoles() {
        return canonicalAccounts.findAllRoles();
    }

    public List<CanonicalGroup> loadCanonicalGroups() {
        return canonicalAccounts.findAllOrganizations();
    }

    public void synchronizeDefaultUsersAndGroups() {
        synchronizeGroups();
        synchronizeUsers();
    }

    public List<UserLink> synchronizeUsers() {
        this.userSynchronizer.synchronizeAll();
        return this.userSynchronizer.getSynchronizedUsers();
    }

    public List<GroupLink> synchronizeGroups() {
        this.groupSynchronizer.synchronizeAll();
        return this.groupSynchronizer.getSynchronizedGroups();
    }

    public void assertUser(CanonicalUser expected, User user) {
        assertNotNull(user);
        assertEquals(expected.getUsername(), user.getUsername());
        assertEquals(expected.getOrganization(), user.getOrganisation());
        assertEquals(expected.getFirstName(), user.getName());
        assertEquals(expected.getLastName(), user.getSurname());
        assertEquals(expected.getEmail(), user.getEmail());
        String expectedTitle = expected.getTitle();
        if (null != expectedTitle && expectedTitle.length() > 16) {
            expectedTitle = expectedTitle.substring(0, 16);
        }
        assertEquals(expectedTitle, user.getKind());

        ProfileMappingProperties profileMappings = configProps.getProfiles();
        Profile expectedProfile = profileMappings.resolveHighestProfileFromRoleNames(expected.getRoles());
        assertEquals(expectedProfile, user.getProfile());
    }

//    public void assertGroupsAndProfiles(CanonicalUser expected, User user) {
//        final GroupSyncMode syncMode = this.configProps.getSyncMode();
//        if (syncMode == GroupSyncMode.roles) {
//            throw new UnsupportedOperationException("implement");
//        } else {
//            assertEquals(GroupSyncMode.orgs, syncMode);
//            final String orgShortName = expected.getOrganization();
//            List<Group> syncedGroups = this.groupSynchronizer.findGroupsFor(user);
//            assertEquals(1, syncedGroups.size());
//            assertEquals(orgShortName, syncedGroups.get(0).getName());
//        }
//    }

    public void assertGroup(User user, CanonicalGroup belongsTo) {
        GroupLink link = assertGroupLink(belongsTo);
        Group group = link.getGeonetworkGroup();
        Specification<UserGroup> query = UserGroupSpecs.hasGroupId(group.getId())
                .and(UserGroupSpecs.hasUserId(user.getId()));
        Optional<UserGroup> internalUserToGroupLink = gnUserGroupRepository.findOne(query);
        assertTrue(internalUserToGroupLink.isPresent());
    }

    public UserLink assertUserLink(CanonicalUser expected) {
        User user = this.gnUserRepository.findOneByUsername(expected.getUsername());
        Optional<UserLink> link = this.userLinkRepository.findById(expected.getId());
        if (user == null) {
            fail("User does not exist: " + expected.getUsername());
        }
        UserLink userLink = link.get();
        assertEquals(expected.getLastUpdated(), userLink.getLastUpdated());
        assertUser(expected, userLink.getInternalUser());
        return userLink;
    }

    public GroupLink assertGroupLink(CanonicalGroup expected) {
        final Group group = this.gnGroupRepository.findByName(expected.getName());
        final Optional<GroupLink> link = groupLinkRepository.findById(expected.getId());
        if (group == null) {
            String msg = String.format("GN group '%s' does not exist", expected.getName());
            fail(msg);
        }
        assertTrue("grouplink not found for " + expected, link.isPresent());

        GroupLink externalGroupLink = link.get();
        assertEquals(group, externalGroupLink.getGeonetworkGroup());
        assertGroup(expected, externalGroupLink.getGeonetworkGroup());
        assertEquals(expected, externalGroupLink.getCanonical());
        return externalGroupLink;
    }

    public void assertGroup(CanonicalGroup expected, Group actual) {
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(expected.getLinkage(), actual.getWebsite());
    }

    public void addGeonetworkGroup(CanonicalGroup g) {
        Group group = new Group();
        group.setName(g.getName());
        group.setDescription(g.getDescription());
        this.gnGroupRepository.save(group);
    }

    public void addGeonetworkUser(CanonicalUser u, CanonicalGroup... groups) {
        User user = new User();
        user.setUsername(u.getUsername());
        user.setName(u.getFirstName());
        user.setSurname(u.getLastName());
        user.setOrganisation(u.getOrganization());
        Profile profile = configProps.getProfiles().resolveHighestProfileFromRoleNames(u.getRoles());
        user.setProfile(profile);
        this.gnUserRepository.save(user);
    }

}
