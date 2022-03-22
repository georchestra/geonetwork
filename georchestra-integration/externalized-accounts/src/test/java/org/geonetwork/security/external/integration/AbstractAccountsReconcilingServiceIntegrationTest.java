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
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.domain.Profile;
import org.geonetwork.security.external.configuration.ProfileMappingProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.geonetwork.testcontainers.postgres.GeonetworkPostgresContainer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import com.google.common.collect.Lists;

@RunWith(SpringRunner.class)
//@TestPropertySource(locations = "classpath:geonetwork-test.properties")
@ContextConfiguration(locations = { //
        "classpath:config-spring-geonetwork.xml", //
        "classpath:domain-repository-test-context.xml" })
@DirtiesContext // reset the app context for each test
public abstract class AbstractAccountsReconcilingServiceIntegrationTest {

    static final Logger log = LoggerFactory
            .getLogger(AbstractAccountsReconcilingServiceIntegrationTest.class.getPackage().getName());

    /**
     * Set up container as a classrule so its ready when the tests run and we don't
     * need to implement a wait-for-db strategy for entityManager
     */
    public static @ClassRule GeonetworkPostgresContainer postgresContainer = new GeonetworkPostgresContainer();

    public @MockBean CanonicalAccountsRepository canonicalAccountsRepositoryMock;

    public @Autowired @Rule IntegrationTestSupport support;

    protected @Autowired AccountsReconcilingService service;

    protected List<CanonicalUser> defaultUsers;

    protected List<CanonicalGroup> defaultGroups;

    protected List<CanonicalGroup> defaultRoles;

    protected CanonicalUser testuser;
    protected CanonicalUser testeditor;
    protected CanonicalUser testreviewer;// has no org
    protected CanonicalUser testadmin;

    protected CanonicalGroup orgPsc;
    protected CanonicalGroup orgC2c;

    protected CanonicalGroup roleOrgAdmin;
    protected CanonicalGroup roleSuperUser;
    protected CanonicalGroup roleAdministrator;
    protected CanonicalGroup roleGnReviewer;
    protected CanonicalGroup roleGnEditor;
    protected CanonicalGroup roleGnAdmin;
    protected CanonicalGroup roleUser;

    public static @BeforeClass void logContainer() {
        Integer localPort = postgresContainer.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT);
        System.setProperty("jdbc.port", localPort.toString());
        log.info("set system property jdbc.port={}, as reported by test container {}", localPort,
                postgresContainer.getContainerName());
    }

    public @Before void clearApplicationContextHolder() {
        ApplicationContextHolder.clear();
    }

    public @Before void disableScheduledSynchronization() {
        support.disableScheduledSynchronization();
    }

    public @Before void setUpCanonicalAccountsRepositoryMock() {
        setUpDefaultProfileMappings();
        defaultUsers = createDefaultUsers();
        defaultGroups = createDefaultOrgs();
        defaultRoles = createDefaultRoles();
        when(canonicalAccountsRepositoryMock.findAllUsers()).thenReturn(defaultUsers);
        when(canonicalAccountsRepositoryMock.findAllOrganizations()).thenReturn(defaultGroups);
        when(canonicalAccountsRepositoryMock.findAllRoles()).thenReturn(defaultRoles);

        mockUserLookups(defaultUsers);
        mockRoleLookups(defaultRoles);
        mockOrgLookups(defaultGroups);
    }

    private void mockUserLookups(List<CanonicalUser> users) {
        for (CanonicalUser user : users) {
            when(canonicalAccountsRepositoryMock.findUserByUsername(user.getUsername())).thenReturn(Optional.of(user));
        }
    }

    private void mockOrgLookups(List<CanonicalGroup> orgs) {
        for (CanonicalGroup groupFromOrg : orgs) {
            when(canonicalAccountsRepositoryMock.findOrganizationByName(groupFromOrg.getName()))
                    .thenReturn(Optional.of(groupFromOrg));
        }
    }

    private void mockRoleLookups(List<CanonicalGroup> roles) {
        for (CanonicalGroup groupFromRole : roles) {
            when(canonicalAccountsRepositoryMock.findRoleByName(groupFromRole.getName()))
                    .thenReturn(Optional.of(groupFromRole));
        }
    }

    protected void setUpDefaultProfileMappings() {
        ProfileMappingProperties profileMappings = support.getProfileMappings();
        profileMappings.setDefault(Profile.Guest);

        Map<String, Profile> rolemappings = profileMappings.getRolemappings();
        rolemappings.put("ADMINISTRATOR", Profile.Administrator);
        rolemappings.put("GN_ADMIN", Profile.Administrator);
        rolemappings.put("GN_EDITOR", Profile.Editor);
        rolemappings.put("GN_REVIEWER", Profile.Reviewer);
        rolemappings.put("USER", Profile.RegisteredUser);
    }

    private List<CanonicalGroup> createDefaultRoles() {
        return Lists.newArrayList(//
                roleOrgAdmin = createRole("ORGADMIN"), //
                roleSuperUser = createRole("SUPERUSER"), //
                roleAdministrator = createRole("ADMINISTRATOR"), //
                roleGnEditor = createRole("GN_EDITOR"), //
                roleGnReviewer = createRole("GN_REVIEWER"), //
                roleGnAdmin = createRole("GN_ADMIN"), //
                roleUser = createRole("USER")//
        );
    }

    private List<CanonicalGroup> createDefaultOrgs() {
        return Lists.newArrayList(//
                orgPsc = createOrg("PSC"), //
                orgC2c = createOrg("C2C"));
    }

    private List<CanonicalUser> createDefaultUsers() {
        return Lists.newArrayList(//
                testuser = createUser("testuser", "PSC", "USER"), //
                testeditor = createUser("testeditor", "C2C", "GN_EDITOR", "USER"), //
                testreviewer = createUser("testreviewer", null/* no org */, "GN_REVIEWER"), //
                testadmin = createUser("testadmin", "PSC", //
                        "ADMINISTRATOR", //
                        "SUPERUSER", //
                        "GN_ADMIN", //
                        "USER")//
        );
    }

    protected CanonicalUser setUpNewUser(String username, CanonicalGroup organization, CanonicalGroup... roles) {
        CanonicalUser user = createUser(username, organization, roles);
        when(canonicalAccountsRepositoryMock.findUserByUsername(username)).thenReturn(Optional.of(user));
        this.defaultUsers.add(user);
        return user;
    }

    protected CanonicalUser createUser(String username, CanonicalGroup organization, CanonicalGroup... roles) {
        String[] roleNames;
        if (null == roles) {
            roleNames = new String[0];
        } else {
            List<CanonicalGroup> r = Arrays.asList(roles);
            r.forEach(role -> assertEquals(GroupSyncMode.roles, role.getOrigin()));
            roleNames = r.stream().map(CanonicalGroup::getName).toArray(String[]::new);
        }

        return createUser(username, organization.getName(), roleNames);
    }

    protected CanonicalUser createUser(String username, String organization, String... roles) {
        String id = UUID.randomUUID().toString();
        return CanonicalUser.builder()//
                .withId(id)//
                .withLastUpdated(id.replaceAll("-", "")).withUsername(username)//
                .withFirstName(username + "FirstName")//
                .withLastName(username + " LastName")//
                .withOrganization(organization)//
                .withEmail(username + "@test.com")//
                .withTitle(username + " Title")//
                .withRoles(Arrays.asList(roles))//
                .build();
    }

    protected CanonicalGroup createOrg(String name) {
        return createGroup(name, GroupSyncMode.orgs);
    }

    protected CanonicalGroup createRole(String name) {
        return createGroup(name, GroupSyncMode.roles);
    }

    protected CanonicalGroup createGroup(String name, GroupSyncMode origin) {
        String id = UUID.randomUUID().toString();
        return CanonicalGroup.builder()//
                .withId(id)//
                .withLastUpdated(id.replaceAll("-", "")).withName(name)//
                .withDescription(name + " description")//
                .withLinkage("http://test.com/" + name)//
                .withOrigin(origin)//
                .build();
    }

    CanonicalUser withOrganization(CanonicalUser user, final String newOrgName) {
        final CanonicalUser existingUserWithRenamedOrg = //
                CanonicalUser.builder().init(user)//
                        .withOrganization(newOrgName)//
                        .withLastUpdated(UUID.randomUUID().toString())//
                        .build();
        return existingUserWithRenamedOrg;
    }

    CanonicalGroup withName(final CanonicalGroup group, final String newName) {
        return CanonicalGroup.builder().init(group)//
                .withName(newName)//
                .withLastUpdated(UUID.randomUUID().toString())//
                .build();
    }

    CanonicalUser withRoles(final CanonicalUser user, final CanonicalGroup... roles) {
        List<String> roleNames = Arrays.stream(roles).map(CanonicalGroup::getName).collect(Collectors.toList());
        CanonicalUser existingUserWithUnsyncRoles = //
                CanonicalUser.builder().init(user)//
                        .withRoles(roleNames)//
                        .withLastUpdated(UUID.randomUUID().toString())//
                        .build();
        return existingUserWithUnsyncRoles;
    }

}
