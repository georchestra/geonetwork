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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.fao.geonet.kernel.datamanager.base.BaseMetadataUtils;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.configuration.ProfileMappingProperties;
import org.geonetwork.security.external.integration.AccountsReconcilingService;
import org.geonetwork.security.external.integration.ScheduledAccountsSynchronizationService;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.geonetwork.testcontainers.postgres.GeorchestraDatabaseContainer;
import org.georchestra.geonetwork.security.authentication.GeorchestraPreAuthenticationFilter;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.model.GeorchestraUser;
import org.georchestra.security.model.Organization;
import org.georchestra.security.model.Role;
import org.georchestra.testcontainers.console.GeorchestraConsoleContainer;
import org.georchestra.testcontainers.ldap.GeorchestraLdapContainer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

@RunWith(SpringRunner.class)
@TestPropertySource(properties = "georchestra.datadir=src/test/resources/data_directory")
@ContextConfiguration(locations = { //
        "classpath*:config-spring-geonetwork-parent.xml", //
        "classpath:config-security-georchestra-authzn.xml", //
        "classpath:domain-repository-test-context.xml" })
@DirtiesContext // reset the app context for each test
public class GeorchestraSecurityIntegrationIT {

    static final Logger log = LoggerFactory.getLogger(GeorchestraSecurityIntegrationIT.class.getPackage().getName());

    public static @ClassRule GeorchestraLdapContainer ldap = new GeorchestraLdapContainer();
    public static @ClassRule GeorchestraDatabaseContainer db = new GeorchestraDatabaseContainer();
    public static GeorchestraConsoleContainer console;

    public @MockBean BaseMetadataUtils baseMetadataUtils;

    // it'll be too much of a pita to actually do CRUD operations on the console
    // app, so spy the repo to tweak its responses when needed
    private @SpyBean CanonicalAccountsRepository consoleAccountsRepository;

    private @Autowired UsersApi consoleUsersApiClient;

    private @Autowired ExternalizedSecurityProperties configProps;
    private @Autowired AccountsReconcilingService synchronizationService;
    private @Autowired ScheduledAccountsSynchronizationService scheduledSynchronizationService;
    private @Autowired CanonicalModelMapper mapper;

    private @Autowired GeorchestraPreAuthenticationFilter authFilter;
    private @MockBean(name = "authenticationManager") AuthenticationManager authenticationManager;

    public @Rule IntegrationTestSupport support = new IntegrationTestSupport();

    @SuppressWarnings("resource")
    public static @BeforeClass void startUpConsoleContainer() {

        Testcontainers.exposeHostPorts(ldap.getMappedLdapPort(), db.getMappedDatabasePort());

        console = new GeorchestraConsoleContainer()//
                .withFileSystemBind(resolveHostDataDirectory(), "/etc/georchestra")//
                .withEnv("pgsqlHost", "host.testcontainers.internal")//
                .withEnv("pgsqlPort", String.valueOf(db.getMappedDatabasePort()))//
                .withEnv("ldapHost", "host.testcontainers.internal")//
                .withEnv("ldapPort", String.valueOf(ldap.getMappedLdapPort()))//
                .withLogToStdOut();

        console.start();
        System.setProperty("georchestra.console.url",
                String.format("http://localhost:%d", console.getMappedConsolePort()));
    }

    private static String resolveHostDataDirectory() {
        File dataDir = new File("src/test/resources/data_directory").getAbsoluteFile();
        assertTrue(dataDir.isDirectory());
        return dataDir.getAbsolutePath();
    }

    public static @AfterClass void shutDownConsoleContainer() {
        console.stop();
    }

    public @Before void before() {
        ApplicationContextHolder.clear();
        configProps.getScheduled().setEnabled(false);
        when(consoleAccountsRepository.findAllUsers()).thenCallRealMethod();
        when(consoleAccountsRepository.findAllGroups()).thenCallRealMethod();
        when(consoleAccountsRepository.findAllRoles()).thenCallRealMethod();
    }

    public @Test void testConsoleAccountsRepository_Users() {
        Map<String, GeorchestraUser> expected = toIdMap(support.loadExpectedGeorchestraUsers(),
                GeorchestraUser::getUsername);

        CanonicalAccountsRepository repo = this.consoleAccountsRepository;
        Map<String, CanonicalUser> bridged = toIdMap(repo.findAllUsers(), CanonicalUser::getUsername);
        assertEquals(expected.keySet(), bridged.keySet());
        expected.values().forEach(u -> {
            String username = u.getUsername();
            support.assertUser(u, bridged.get(username));
        });
    }

    public @Test void testConsoleAccountsRepository_Groups() {
        Map<String, Organization> expected = toIdMap(support.loadExpectedGeorchestraOrgs(), Organization::getShortName);

        Map<String, CanonicalGroup> bridged = toIdMap(consoleAccountsRepository.findAllGroups(),
                CanonicalGroup::getName);
        assertEquals(expected.keySet(), bridged.keySet());
        expected.values().forEach(org -> {
            support.assertGroup(org, bridged.get(org.getShortName()));
            Optional<CanonicalGroup> byName = consoleAccountsRepository.findGroupByName(org.getShortName());
            assertTrue(byName.isPresent());
            support.assertGroup(org, byName.get());
        });
    }

    public @Test void testConsoleAccountsRepository_Roles() {
        Map<String, Role> expected = toIdMap(support.loadExpectedGeorchestraRoles(), Role::getName);
        Map<String, CanonicalGroup> bridged = toIdMap(consoleAccountsRepository.findAllRoles(),
                CanonicalGroup::getName);
        assertEquals(expected.keySet(), bridged.keySet());
        expected.values().forEach(role -> {
            support.assertRole(role, bridged.get(role.getName()));
            Optional<CanonicalGroup> byName = consoleAccountsRepository.findRoleByName(role.getName());
            assertTrue(byName.isPresent());
            support.assertRole(role, byName.get());
        });
    }

    public @Test void testGeorchestraPreAuthenticationFilter_synchronizes_user_before_proceeding() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        GeorchestraUser preAuthUser = consoleUsersApiClient.findByUsername("testadmin")
                .orElseThrow(NoSuchElementException::new);
        //make it a non default user to avoid race conditions due to test execution order
        preAuthUser.setUsername("testadmin2");
        preAuthUser.setId(UUID.randomUUID().toString());
        preAuthUser.setLastUpdated(UUID.randomUUID().toString());
        
        String preAuthPayload = support.jsonEncode(preAuthUser);

        request.addHeader("sec-proxy", "true");
        request.addHeader("sec-user", preAuthPayload);

        CanonicalUser canonical = mapper.toCanonical(preAuthUser);
        assertFalse(synchronizationService.findUpToDateUser(canonical).isPresent());

        User createdUponAuthentication = authFilter.getPreAuthenticatedPrincipal(request);
        assertNotNull(createdUponAuthentication);
        assertEquals(canonical.getUsername(), createdUponAuthentication.getUsername());

        User found = synchronizationService.findUpToDateUser(canonical)
                .orElseThrow(() -> new IllegalStateException("user should have been synchronized"));

        assertEquals(canonical.getUsername(), found.getUsername());
    }

    /**
     * From
     * {@code src/test/resources/data_directory/geonetwork/geonetwork.properties}:
     * 
     * <pre>
     * <code>
     * geonetwork.syncMode=orgs
     * geonetwork.syncRolesFilter=GN_(.*)
     * geonetwork.profiles.default=RegisteredUser
     * geonetwork.profiles.rolemappings.[GN_ADMIN]=Administrator
     * geonetwork.profiles.rolemappings.[GN_REVIEWER]=Reviewer
     * geonetwork.profiles.rolemappings.[GN_EDITOR]=Editor
     * geonetwork.profiles.rolemappings.[GN_USER]=RegisteredUser
     * geonetwork.scheduled.enabled=true
     * geonetwork.scheduled.timeUnit = SECONDS
     * geonetwork.scheduled.retryOnFailure = true
     * geonetwork.scheduled.initialDelay = 5
     * geonetwork.scheduled.retryDelay = 5
     * geonetwork.scheduled.delayBetweenRuns = 30
     * </code>
     * </pre>
     */
    public @Test void verify_configuration_loaded_from_data_directory_geonetwork_dot_properties() {
        ExternalizedSecurityProperties config = this.configProps;
        assertEquals(GroupSyncMode.orgs, config.getSyncMode());
        assertEquals("GN_(.*)", config.getSyncRolesFilter().toString());

        ProfileMappingProperties profiles = config.getProfiles();
        assertEquals(Profile.RegisteredUser, profiles.getDefault());
        assertEquals(4, profiles.getRolemappings().size());
        assertEquals(Profile.Administrator, profiles.getRolemappings().get("GN_ADMIN"));
        assertEquals(Profile.Reviewer, profiles.getRolemappings().get("GN_REVIEWER"));
        assertEquals(Profile.Editor, profiles.getRolemappings().get("GN_EDITOR"));
        assertEquals(Profile.RegisteredUser, profiles.getRolemappings().get("GN_USER"));
    }

    public @Test void testScheduledSynchronization() throws Exception {
        // ok, so far there's either no users/groups, or just testadmin and its org,
        // depending on the order the tests were executed. If more tests are to be
        // added, be careful not to rely on the default ones too much either being or
        // not being synchronized, this is a live test suite, can't re-create the test
        // data so easily.

        // enable synchronization, was disabled at @Before
        configProps.getScheduled().setTimeUnit(TimeUnit.SECONDS);
        configProps.getScheduled().setInitialDelay(1);

        configProps.getScheduled().setEnabled(true);
        // make it think the app context just started up
        scheduledSynchronizationService.afterPropertiesSet();
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(this.scheduledSynchronizationService::isScheduled);
        Awaitility.await().atMost(5, TimeUnit.SECONDS).until(this.scheduledSynchronizationService::isRunning);
        Awaitility.await().atMost(10, TimeUnit.SECONDS).until(this.scheduledSynchronizationService::isScheduled);

        configProps.getScheduled().setEnabled(false);

        List<GeorchestraUser> users = support.loadExpectedGeorchestraUsers();
        users.forEach(user -> {
            CanonicalUser canonical = mapper.toCanonical(user);
            Optional<User> synced = this.synchronizationService.findUpToDateUser(canonical);
            assertTrue(synced.isPresent());
        });

        List<Organization> orgs = support.loadExpectedGeorchestraOrgs();
        orgs.forEach(org -> {
            CanonicalGroup canonical = mapper.toCanonical(org);
            Optional<Group> synced = this.synchronizationService.findUpToDateGroup(canonical);
            assertTrue(synced.isPresent());
        });
    }

    private <T> Map<String, T> toIdMap(List<T> list, Function<T, String> idExtractor) {
        final Map<String, T> actual = list.stream().collect(Collectors.toMap(idExtractor, Function.identity()));
        return actual;
    }
}
