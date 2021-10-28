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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.configuration.ProfileMappingProperties;
import org.geonetwork.security.external.integration.AccountsReconcilingService;
import org.geonetwork.security.external.integration.ScheduledAccountsSynchronizationService;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.georchestra.geonetwork.security.AbstractGeorchestraIntegrationTest;
import org.georchestra.security.model.GeorchestraUser;
import org.georchestra.security.model.Organization;
import org.georchestra.security.model.Role;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;

public class GeorchestraSecurityIntegrationIT extends AbstractGeorchestraIntegrationTest {

    private @Autowired ExternalizedSecurityProperties configProps;
    private @Autowired AccountsReconcilingService synchronizationService;
    private @Autowired ScheduledAccountsSynchronizationService scheduledSynchronizationService;

    public @Test void testConsoleAccountsRepository_Users() {
        Map<String, GeorchestraUser> expected = toIdMap(support.loadExpectedGeorchestraUsers(),
                GeorchestraUser::getUsername);

        CanonicalAccountsRepository repo = this.consoleAccountsRepository;
        Map<String, CanonicalUser> bridged = toIdMap(repo.findAllUsers(), CanonicalUser::getUsername);
        assertEquals(expected.keySet(), bridged.keySet());
        expected.values().forEach(georchestraUser -> {
            String username = georchestraUser.getUsername();
            support.assertUser(georchestraUser, bridged.get(username));
            Optional<CanonicalUser> byName = consoleAccountsRepository.findUserByUsername(username);
            assertTrue(byName.isPresent());
            support.assertUser(georchestraUser, byName.get());
        });
    }

    public @Test void testConsoleAccountsRepository_Groups() {
        Map<String, Organization> expected = toIdMap(support.loadExpectedGeorchestraOrgs(), Organization::getShortName);

        Map<String, CanonicalGroup> bridged = toIdMap(consoleAccountsRepository.findAllOrganizations(),
                CanonicalGroup::getName);
        assertEquals(expected.keySet(), bridged.keySet());
        expected.values().forEach(org -> {
            support.assertGroup(org, bridged.get(org.getShortName()));
            Optional<CanonicalGroup> byName = consoleAccountsRepository.findOrganizationByName(org.getShortName());
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
        assertEquals("EL_(.*)", config.getSyncRolesFilter().toString());

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
