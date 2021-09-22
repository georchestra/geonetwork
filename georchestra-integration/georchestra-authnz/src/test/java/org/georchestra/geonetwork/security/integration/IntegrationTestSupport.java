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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.fao.geonet.kernel.datamanager.base.BaseMetadataUtils;
import org.fao.geonet.repository.GroupRepository;
import org.fao.geonet.repository.UserGroupRepository;
import org.fao.geonet.repository.UserRepository;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.configuration.ProfileMappingProperties;
import org.geonetwork.security.external.integration.AccountsReconcilingService;
import org.geonetwork.security.external.integration.GroupSynchronizer;
import org.geonetwork.security.external.integration.UserSynchronizer;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.model.UserLink;
import org.geonetwork.security.external.repository.GroupLinkRepository;
import org.geonetwork.security.external.repository.UserLinkRepository;
import org.geonetwork.security.external.repository.jpa.ExternalGroupLinkRepository;
import org.geonetwork.security.external.repository.jpa.ExternalUserLinkRepository;
import org.georchestra.security.api.OrganizationsApi;
import org.georchestra.security.api.RolesApi;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.model.GeorchestraUser;
import org.georchestra.security.model.Organization;
import org.georchestra.security.model.Role;
import org.junit.rules.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

public class IntegrationTestSupport extends ExternalResource {

    // spring configuration to mock up beans that are either unnecessary for these
    // tests but required for the app context to load, or that we need to mock up to
    // interact with external systems like georchestra's console REST API
    static @Configuration class Config {
        // Mocked up beans unrelated to this test suite, but necessary for the app
        // context to load
        public @MockBean BaseMetadataUtils baseMetadataUtils;
//        public @MockBean BaseMetadataIndexer baseMetadataIndexer;
//        public @MockBean EsSearchManager esSearchManager;
//        public @MockBean GeonetworkDataDirectory geonetworkDataDirectory;
//        public @MockBean SchemaManager schemaManager;

        ////
        public @MockBean @Qualifier("georchestraConsoleUsersApiClient") UsersApi consoleUsersApiClient;
        public @MockBean @Qualifier("georchestraConsoleOrgsApiClient") OrganizationsApi consoleOrgsApiClient;
        public @MockBean @Qualifier("georchestraConsoleRolesApiClient") RolesApi consoleRolesApiClient;
    }

    public @Autowired UserRepository gnUserRepository;
    public @Autowired GroupRepository gnGroupRepository;
    public @Autowired UserGroupRepository gnUserGroupRepository;

    private @Autowired ExternalUserLinkRepository jpaUserLinkRepository;
    private @Autowired ExternalGroupLinkRepository jpaGroupLinkRepository;

    public @Autowired UserLinkRepository userLinkRepository;
    public @Autowired GroupLinkRepository groupLinkRepository;

    public @Autowired UsersApi usersApiMock;
    public @Autowired OrganizationsApi orgsApiMock;
    public @Autowired RolesApi rolesApiMock;

    public @Autowired AccountsReconcilingService service;
    private @Autowired ExternalizedSecurityProperties configProps;
    public @Autowired UserSynchronizer userSynchronizer;
    public @Autowired GroupSynchronizer groupSynchronizer;

    protected @Override void before() throws Throwable {
        when(usersApiMock.findAll()).thenReturn(loadDefaultGeorchestraUsers());
        when(orgsApiMock.findAll()).thenReturn(loadDefaultGeorchestraOrgs());
        when(rolesApiMock.findAll()).thenReturn(loadDefaultGeorchestraRoles());
    }

    protected @Override void after() {
        // do nothing
    }

    public void setOrgsSyncMode() {
        configProps.setSyncMode(GroupSyncMode.orgs);
    }

    public void setRolesSyncMode() {
        configProps.setSyncMode(GroupSyncMode.roles);
    }

    public List<GeorchestraUser> loadDefaultGeorchestraUsers() {
        return loadJson("defaultUsers.json", GeorchestraUser.class);
    }

    public List<Role> loadDefaultGeorchestraRoles() {
        return loadJson("defaultRoles.json", Role.class);
    }

    public List<Organization> loadDefaultGeorchestraOrgs() {
        return loadJson("defaultOrganizations.json", Organization.class);
    }

    private <T> List<T> loadJson(String resource, Class<T> type) {
        final URL url = getClass().getResource(resource);
        assertNotNull(url);
        return loadJson(type, url);
    }

    private <T> List<T> loadJson(Class<T> type, final URL url) {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, type);
        try {
            return mapper.readValue(url, collectionType);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void setUpDefaultUsersAndGroups() {
        setUpDefaultGroups();
        setUpDefaultUsers();
    }

    public List<UserLink> setUpDefaultUsers() {
        List<CanonicalUser> canonical = this.userSynchronizer.findCanonicalUsers();
        this.userSynchronizer.synchronizeAll(canonical);
        return this.userSynchronizer.getSynchronizedUsers();
    }

    public List<GroupLink> setUpDefaultGroups() {
        List<CanonicalGroup> canonical = this.groupSynchronizer.findCanonicalGroups();
        this.groupSynchronizer.synchronizeAll(canonical);
        return this.groupSynchronizer.getSynchronizedGroups();
    }

    public void assertUser(CanonicalUser expected, User user) {
        assertNotNull(user);
        assertEquals(expected.getUsername(), user.getUsername());
        assertEquals(expected.getOrganization(), user.getOrganisation());
        assertEquals(expected.getFirstName(), user.getName());
        assertEquals(expected.getLastName(), user.getSurname());
        assertEquals(expected.getEmail(), user.getEmail());
        assertEquals(expected.getPostalAddress(), user.getPrimaryAddress().getAddress());
        assertEquals(expected.getTitle(), user.getKind());

        ProfileMappingProperties profileMappings = configProps.getProfiles();
        Profile expectedProfile = profileMappings.resolveHighestProfileFromRoleNames(expected.getRoles());
        assertEquals(expectedProfile, user.getProfile());

        Set<String> expectedRoles = new HashSet<>(expected.getRoles());
        Set<String> actualRoles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        assertEquals(expectedRoles, actualRoles);
        assertGroupsAndProfiles(expected, user);
    }

    public void assertGroupsAndProfiles(CanonicalUser expected, User user) {
        final GroupSyncMode syncMode = this.configProps.getSyncMode();
        if (syncMode == GroupSyncMode.roles) {
            throw new UnsupportedOperationException("implement");
        } else {
            assertEquals(GroupSyncMode.orgs, syncMode);
            final String orgShortName = expected.getOrganization();
            List<Group> syncedGroups = this.groupSynchronizer.findGroupsFor(user);
            assertEquals(1, syncedGroups.size());
            assertEquals(orgShortName, syncedGroups.get(0).getName());
        }
    }
}
