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
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.fao.geonet.domain.User;
import org.geonetwork.security.external.model.CanonicalUser;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;

/**
 * Concurrency related test cases that can't run on
 * {@link AuthenticatedUserSynchronizationIT} because it sets
 * {@code @Transactional} at class level and makes concurrent calls to
 * transcational methods hang.
 */
public class AuthenticatedUserConcurrencyIT extends AbstractAccountsReconcilingServiceIntegrationTest {

    protected @SpyBean UserSynchronizer userSynchronizer;

    static final int nThreads = 8;
    static ExecutorService executor;

    public static @BeforeClass void setUpThradPool() {
        executor = Executors.newFixedThreadPool(nThreads);
    }

    public static @AfterClass void shutDownThradPool() {
        executor.shutdownNow();
    }

    public @Test void concurrent_requests_for_non_existing_geonetwork_user_create_it_ony_once() throws Exception {
        final CanonicalUser newUser = createUser("newUser", orgC2c, roleUser, roleOrgAdmin);

        final Callable<User> task = () -> service.forceMatchingGeonetworkUser(newUser);

        final int nTasks = 4 * nThreads;
        List<User> results = invokeAllAndGet(task, nTasks);

        verify(userSynchronizer, times(1)).synchronize(eq(newUser));
        results.forEach(user -> assertEquals(results.get(0).getId(), user.getId()));
        results.forEach(returned -> support.assertUser(newUser, returned));
    }

    public @Test void concurrent_requests_for_outdated_geonetwork_user_update_it_ony_once() throws Exception {
        support.setUpDefaultUsersAndGroups();

        final CanonicalUser currentUser = super.testuser;
        final CanonicalUser updated = super.withOrganization(currentUser, orgC2c.getName());
        assertNotEquals(currentUser.getOrganization(), updated.getOrganization());

        final User expected = service.findUpToDateUser(currentUser).get();
        final Callable<User> task = () -> service.forceMatchingGeonetworkUser(updated);

        final int nTasks = 4 * nThreads;
        List<User> results = invokeAllAndGet(task, nTasks);

        verify(userSynchronizer, times(1)).synchronize(eq(updated));
        results.forEach(user -> assertEquals(expected.getId(), user.getId()));
        results.forEach(returned -> {
            support.assertUser(updated, returned);
            support.assertGroup(returned, orgC2c);
        });
    }

    private <T> List<T> invokeAllAndGet(final Callable<T> task, final int nCopies) throws InterruptedException {
        return executor.invokeAll(Collections.nCopies(nCopies, task)).stream().map(t -> {
            try {
                return t.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
