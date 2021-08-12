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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.fao.geonet.domain.User;
import org.fao.geonet.repository.UserRepository;
import org.georchestra.config.security.GeorchestraUserDetails;
import org.georchestra.geonetwork.logging.Logging;
import org.georchestra.geonetwork.security.repository.UserLink;
import org.georchestra.geonetwork.security.repository.UserLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * Service that
 */
@Service
public class GeorchestraToGeonetworkUserReconcilingService {

    private static final Logging log = Logging.getLogger("org.georchestra.geonetwork.security.integration");

    private final UserRepository userRepository;
    private final UserLinkRepository userLinkRepository;

    private final UserLocks locks = new UserLocks();

    private final UserMapperService mapper = new UserMapperService();

    public @Autowired GeorchestraToGeonetworkUserReconcilingService(UserRepository userRepository,
            UserLinkRepository userLinkRepository) {
        this.userRepository = userRepository;
        this.userLinkRepository = userLinkRepository;
    }

    /**
     * Finds the {@link org.fao.geonet.domain.User} that's the internal surrogate
     * representation of the canonical {@link GeorchestraUserDetails}, or
     * {@link Optional#empty() emtpy} if none exists.
     * 
     * @see UserLinkRepository
     */
    public Optional<User> findGeonetworkUser(@NonNull String georchestraUserId) {
        Optional<User> user = findLink(georchestraUserId).map(UserLink::getGeonetworkUser);
        log.debug("GN User %s for georchestra user id %s", user.isPresent() ? "found" : "NOT found", georchestraUserId);
        return user;
    }

    public Optional<User> findUpToDateUser(@NonNull GeorchestraUserDetails canonical) {
        return findLink(canonical.getUserId())//
                .filter(link -> gnUserIsUpToDate(canonical, link))//
                .map(UserLink::getGeonetworkUser);
    }

    /**
     * Takes {@code georchestraUser} as the canonical representation of a given
     * user, and returns the GeoNetwork {@link User user} that's linked to it,
     * possibly reconciling (i.e. creating or updating) the GeoNetwork user
     * properties.
     * <p>
     * If the GN user does not exist, one will be created. If the GN user properties
     * are outdated with regard to the geOrchestra user (rather, the relevant ones
     * for the sake of keeping the credentials in synch with the geOrchestra user),
     * the GN user will be updated to match the canonical information provided by
     * geOrchestra's security proxy (or whatever other means the canonical user
     * representation was obtained from).
     * <p>
     * When this method returns, it is assured that the returned GeoNetwork user
     * matches the credentials of the provided canonical user info.
     */
    @Transactional
    public @NonNull User forceMatchingGeonetworkUser(@NonNull GeorchestraUserDetails georchestraUser) {
        final String userId = georchestraUser.getUserId();
        log.info("Forcing up-to-date user from geOrchestra user %s (%s)...", userId, georchestraUser.getUsername());
        // avoid concurrent requests updating/creating the same User
        final Lock lock = locks.getUserLock(userId);
        lock.lock();
        try {
            UserLink link = findLink(userId).orElse(null);
            if (link == null || link.getGeonetworkUser() == null) {
                return createGeonetworkUser(georchestraUser);
            }
            return reconcile(georchestraUser, link);
        } finally {
            lock.unlock();
        }
    }

    private @NonNull User createGeonetworkUser(@NonNull GeorchestraUserDetails georUser) {
        final String userId = georUser.getUserId();
        log.info("Creating GN User %s (%s)...", userId, georUser.getUsername());
        User newUser = userRepository.save(mapper.toGeonetorkUser(georUser));
        UserLink link = findLink(userId).orElseGet(() -> newLink(georUser, newUser));
        userLinkRepository.save(link);
        log.info("Created GN User %s (%s)...", userId, georUser.getUsername());
        return newUser;
    }

    /**
     * Evaluates whether the GeoNetwork {@link User user} information is current
     * with the canonical geOrchestra user.
     * 
     * @return {@code true} if the users match according to the criteria to keep
     *         them in synch, {@code false} otherwise, meaning the GeoNetwork user
     *         properties must be updated in the database to match the geOrchestra
     *         user.
     */
    private boolean gnUserIsUpToDate(final @NonNull GeorchestraUserDetails preAuthUser, final @NonNull UserLink link) {
        final String expected = preAuthUser.getLastUpdated();
        final String actual = link.getLastUpdated();
        final boolean userExists = link.getGeonetworkUser() != null;
        return Objects.equals(expected, actual) && userExists;
    }

    private @NonNull User reconcile(@NonNull GeorchestraUserDetails canonical, @NonNull UserLink link) {
        if (gnUserIsUpToDate(canonical, link)) {
            log.debug("GN user is up to date. Id: %s, version: %s", canonical.getUserId(), link.getLastUpdated());
            return link.getGeonetworkUser();
        }
        final User user = link.getGeonetworkUser();
        log.info("GN user %s (version '%s') is outdated, reconciling to version '%s'", //
                link.getGeorchestraUserId(), link.getLastUpdated(), canonical.getLastUpdated());

        User updated = userRepository.update(user.getId(), persistedVersion -> {
            Map<String, Pair<?, ?>> changes = mapper.updateGeonetworkUser(canonical, persistedVersion);
            logChanges(changes);
        });

        link.setLastUpdated(canonical.getLastUpdated());
        link.setGeonetworkUser(updated);
        userLinkRepository.save(link);

        return updated;
    }

    private void logChanges(Map<String, Pair<?, ?>> changes) {

        Supplier<String> changesStr = () -> changes.entrySet().stream()
                .map(e -> String.format("%s[%s -> %s]", e.getKey(), e.getValue().getFirst(), e.getValue().getSecond()))
                .collect(Collectors.joining(","));

        log.info("Updated user properties: %s", changesStr);
    }

    private Optional<UserLink> findLink(@NonNull String georchestraUserId) {
        Optional<UserLink> link = userLinkRepository.findById(georchestraUserId);
        log.debug("UserLink %s for georchestra user id %s", link.isPresent() ? "found" : "NOT found",
                georchestraUserId);
        return link;
    }

    private UserLink newLink(@NonNull GeorchestraUserDetails georchestraUser, @NonNull User gnUser) {
        UserLink link = new UserLink();
        link.setGeorchestraUserId(georchestraUser.getUserId());
        link.setLastUpdated(georchestraUser.getLastUpdated());
        link.setGeonetworkUser(gnUser);
        return link;
    }
}
