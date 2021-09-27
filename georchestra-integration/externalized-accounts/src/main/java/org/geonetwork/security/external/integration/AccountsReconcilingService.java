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

import static java.util.Objects.requireNonNull;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.transaction.Transactional;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.User;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.UserLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

/**
 * Service that
 */
public class AccountsReconcilingService {

    static final Logger log = LoggerFactory.getLogger(AccountsReconcilingService.class.getPackage().getName());

    private @Autowired GroupSynchronizer groupSynchronizer;
    private @Autowired UserSynchronizer userSynchronizer;

    private final UserLocks locks = new UserLocks();

    public Optional<User> findUpToDateUser(@NonNull CanonicalUser canonical) {
        requireNonNull(canonical);
        return this.userSynchronizer//
                .findUserLink(canonical.getId())//
                .filter(l -> l.isUpToDateWith(canonical))//
                .map(UserLink::getInternalUser);
    }

    public Optional<Group> findUpToDateGroup(@NonNull CanonicalGroup canonical) {
        requireNonNull(canonical);
        return this.groupSynchronizer//
                .findGroupLink(canonical.getId())//
                .filter(l -> l.isUpToDateWith(canonical))//
                .map(GroupLink::getGeonetworkGroup);
    }

    /**
     * Takes {@code CanonicalUser} as the canonical representation of a given user,
     * and returns the GeoNetwork {@link User user} that's linked to it, possibly
     * reconciling (i.e. creating or updating) the GeoNetwork user properties.
     * <p>
     * If the GN user does not exist, one will be created. If the GN user properties
     * are outdated with regard to the canonical user (rather, the relevant ones for
     * the sake of keeping the credentials in synch with the canonical user), the GN
     * user will be updated to match the canonical information provided by the
     * external system (or whatever other means the canonical user representation
     * was obtained from).
     * <p>
     * When this method returns, it is assured that the returned GeoNetwork user
     * matches the credentials of the provided canonical user info.
     */
    public @NonNull User forceMatchingGeonetworkUser(@NonNull CanonicalUser canonicalUser) {
        final String userId = canonicalUser.getId();
        log.debug("Forcing up-to-date user {} ({})...", canonicalUser.getUsername(), userId);
        // avoid concurrent requests updating/creating the same User
        final Lock lock = locks.getLock(userId);
        lock.lock();
        try {
            return findUpToDateUser(canonicalUser)
                    .orElseGet(() -> userSynchronizer.synchronize(canonicalUser).getInternalUser());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Synchronizes all canonical users and organizations/roles with GeoNetwork
     * users and groups.
     */
    @Transactional
    public void synchronize() {
        groupSynchronizer.synchronizeAll();
        userSynchronizer.synchronizeAll();
    }

    private static class UserLocks {
        private static final int MAX_LOCKS = 4 * Runtime.getRuntime().availableProcessors();
        private final ConcurrentMap<Integer, Lock> locks = new ConcurrentHashMap<>();

        public Lock getLock(@NonNull String id) {
            HashCode hashCode = Hashing.goodFastHash(64).hashString(id, StandardCharsets.UTF_8);
            int bucket = Hashing.consistentHash(hashCode, MAX_LOCKS);
            return locks.computeIfAbsent(bucket, b -> new ReentrantLock());
        }

    }
}
