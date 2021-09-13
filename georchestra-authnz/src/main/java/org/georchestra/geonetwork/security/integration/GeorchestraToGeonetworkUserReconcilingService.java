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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Pair;
import org.fao.geonet.domain.User;
import org.georchestra.geonetwork.logging.Logging;
import org.georchestra.geonetwork.security.repository.GroupLink;
import org.georchestra.geonetwork.security.repository.GroupLinkRepository;
import org.georchestra.geonetwork.security.repository.UserLink;
import org.georchestra.geonetwork.security.repository.UserLinkRepository;
import org.georchestra.security.model.GeorchestraUser;
import org.georchestra.security.model.Organization;
import org.georchestra.security.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

/**
 * Service that
 */
@Service
public class GeorchestraToGeonetworkUserReconcilingService {

    private static final Logging log = Logging.getLogger("org.georchestra.geonetwork.security.integration");

    private @Autowired GroupLinkRepository groupLinks;
    private @Autowired UserLinkRepository userLinks;
    private @Autowired ModelMapperService mapper;

    private final UserLocks locks = new UserLocks();

    private @Autowired ConfigurableApplicationContext appContext;

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
    public @NonNull UserLink forceMatchingGeonetworkUser(@NonNull GeorchestraUser georchestraUser) {
        final String userId = georchestraUser.getId();
        log.debug("Forcing up-to-date user from geOrchestra user %s (%s)...", georchestraUser.getUsername(), userId);
        // avoid concurrent requests updating/creating the same User
        final Lock lock = locks.getLock(userId);
        lock.lock();
        try {
            UserLink link = findUserLink(userId).orElse(null);
            if (link == null || link.getGeonetworkUser() == null) {
                return createGeonetworkUser(georchestraUser);
            }
            return reconcile(georchestraUser, link);
        } finally {
            lock.unlock();
        }
    }

    private @NonNull UserLink createGeonetworkUser(@NonNull GeorchestraUser georUser) {
        final String userId = georUser.getId();
        log.info("Creating GN User %s (%s)...", userId, georUser.getUsername());
        User newUser = mapper.toGeonetorkUser(georUser);
        UserLink link = findUserLink(userId).orElseGet(() -> newLink(georUser, newUser));
        link = userLinks.save(link);
        log.info("Created GN User %s (%s)...", userId, georUser.getUsername());
        return link;
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
    private boolean gnUserIsUpToDate(final @NonNull GeorchestraUser preAuthUser, final @NonNull UserLink link) {
        final String expected = preAuthUser.getLastUpdated();
        final String actual = link.getLastUpdated();
        final boolean userExists = link.getGeonetworkUser() != null;
        return Objects.equals(expected, actual) && userExists;
    }

    private boolean gnGroupIsUpToDate(final @NonNull Organization canonical, final @NonNull GroupLink link) {
        final String expected = canonical.getLastUpdated();
        final String actual = link.getLastUpdated();
        final boolean orgExists = link.getGeonetworkGroup() != null;
        return Objects.equals(expected, actual) && orgExists;
    }

    private @NonNull UserLink reconcile(@NonNull GeorchestraUser canonical, @NonNull UserLink link) {
        if (gnUserIsUpToDate(canonical, link)) {
            log.debug("GN user %s is up to date. Id: %s, version: %s", link.getGeonetworkUser().getUsername(),
                    canonical.getId(), link.getLastUpdated());
            return link;
        }
        final User user = link.getGeonetworkUser();
        log.info("GN user %s (version '%s') is outdated, reconciling to version '%s'", //
                canonical.getUsername(), link.getLastUpdated(), canonical.getLastUpdated());

        Map<String, Pair<?, ?>> changes = mapper.updateGeonetworkUser(canonical, user);
        link.setLastUpdated(canonical.getLastUpdated());
        link = userLinks.save(link);
        logChanges(changes);
        return link;
    }

    private void logChanges(Map<String, Pair<?, ?>> changes) {

        Supplier<String> changesStr = () -> changes.entrySet().stream()
                .map(e -> String.format("%s[%s -> %s]", e.getKey(), e.getValue().one(), e.getValue().two()))
                .collect(Collectors.joining(","));

        log.info("Updated user properties: %s", changesStr);
    }

    public Optional<UserLink> findUserLink(@NonNull String georchestraUserId) {
        return userLinks.findById(georchestraUserId);
    }

    public Optional<GroupLink> findGroupLink(@NonNull String georchestraGroupId) {
        return groupLinks.findById(georchestraGroupId);
    }

    public Optional<UserLink> findUpToDateUserLink(@NonNull GeorchestraUser canonical) {
        return findUserLink(canonical.getId())//
                .filter(link -> gnUserIsUpToDate(canonical, link));
    }

    public Optional<GroupLink> findUpToDateGroupLink(@NonNull Organization canonical) {
        return findGroupLink(canonical.getId())//
                .filter(link -> gnGroupIsUpToDate(canonical, link));
    }

    private UserLink newLink(@NonNull GeorchestraUser georchestraUser, @NonNull User gnUser) {
        UserLink link = new UserLink();
        link.setGeorchestraUserId(georchestraUser.getId());
        link.setLastUpdated(georchestraUser.getLastUpdated());
        link.setGeonetworkUser(gnUser);
        return link;
    }

    @Transactional
    public @NonNull GroupLink forceMatchingGeonetworkGroup(@NonNull Organization georchestraOrg) {
        final String groupId = georchestraOrg.getId();
        // avoid concurrent requests updating/creating the same User
        final Lock lock = locks.getLock(groupId);
        lock.lock();
        try {
            GroupLink link = findGroupLink(groupId).orElse(null);
            if (link == null || link.getGeonetworkGroup() == null) {
                log.info("Creating new Group for Organization %s...", georchestraOrg.getShortName());
                link = createGeonetworkGroup(georchestraOrg);
                log.info("Created group %s (id: %s, version: %s)", link.getGeonetworkGroup().getName(),
                        link.getGeorchestraOrgId(), link.getLastUpdated());
                return link;
            }
            return reconcile(georchestraOrg, link);
        } finally {
            lock.unlock();
        }
    }

    private GroupLink createGeonetworkGroup(Organization org) {
        Group group = mapper.toGeonetorkGroup(org);
        GroupLink link = new GroupLink();
        link.setGeonetworkGroup(group);
        link.setGeorchestraOrgId(org.getId());
        link.setLastUpdated(org.getLastUpdated());
        return this.groupLinks.save(link);
    }

    private GroupLink reconcile(Organization canonical, GroupLink link) {
        if (gnGroupIsUpToDate(canonical, link)) {
            log.debug("GN group is up to date with Organization %s. Id: %s, version: %s", canonical.getShortName(),
                    canonical.getId(), link.getLastUpdated());
            return link;
        }
        final Group group = link.getGeonetworkGroup();
        log.info("GN group %s (Id: %s, version '%s') is outdated, reconciling to version '%s'", //
                canonical.getShortName(), link.getGeorchestraOrgId(), link.getLastUpdated(),
                canonical.getLastUpdated());

        Map<String, Pair<?, ?>> changes = mapper.updateGeonetworkGroup(canonical, group);
        link.setLastUpdated(canonical.getLastUpdated());
        link = groupLinks.save(link);
        logChanges(changes);
        return link;
    }

    @Transactional
    public void synchronizeUsers(List<GeorchestraUser> canonical) {
        ApplicationContextHolder.set(appContext);
        try {
            final Map<String, UserLink> currentLinks = getExistingUserLinksById();
            final Map<String, GeorchestraUser> actual = toIdMap(canonical, GeorchestraUser::getId);

            createNewUsers(currentLinks, actual);

            reconcileMismatchedUsers(currentLinks, actual);

            deleteGoneUsers(currentLinks, actual);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void synchronizeGroupsWithOrganizations(List<Organization> canonical) {
        ApplicationContextHolder.set(appContext);
        try {
            final Map<String, GroupLink> currentLinks = getExistingGroupLinksById();
            final Map<String, Organization> actual = toIdMap(canonical, Organization::getId);

            createNewGroups(currentLinks, actual);

            reconcileMismatchedGroups(currentLinks, actual);

            deleteGoneGroups(currentLinks, actual);
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void createNewUsers(final Map<String, UserLink> currentLinks, final Map<String, GeorchestraUser> actual) {
        // create new users
        final Set<String> newUserIds = Sets.difference(actual.keySet(), currentLinks.keySet());
        newUserIds.forEach(id -> this.forceMatchingGeonetworkUser(actual.get(id)));
    }

    private void reconcileMismatchedUsers(final Map<String, UserLink> currentLinks,
            final Map<String, GeorchestraUser> actual) {
        // update info on existing users
        final Set<String> updateCandidates = Sets.intersection(currentLinks.keySet(), actual.keySet());
        updateCandidates.forEach(id -> {
            GeorchestraUser canonical = actual.get(id);
            UserLink link = currentLinks.get(id);
            this.reconcile(canonical, link);
        });
    }

    private void deleteGoneUsers(final Map<String, UserLink> currentLinks, final Map<String, GeorchestraUser> actual) {
        final Set<String> deleteCandidates = Sets.difference(currentLinks.keySet(), actual.keySet());

        for (String userId : deleteCandidates) {
            UserLink link = currentLinks.get(userId);
            long recordCount = userLinks.countMetadataRecords(link);
            if (recordCount > 0L) {
                log.warn("Cannot delete user '%s' who is owner of %d metadata record(s).",
                        link.getGeonetworkUser().getName(), recordCount);
                // can't delete the user, but can delete the link and rename the user to avoid
                // conflicts with future users with the same name
                final String deletedUserName = buildDeletedUserName(link);
                link.getGeonetworkUser().setName(deletedUserName);
                userLinks.save(link);
                userLinks.delete(link);
            } else {
                log.info("Deleting GeoNetwork user %s...", link.getGeonetworkUser().getUsername());
                userLinks.deleteLinkAndUser(link);
            }
        }
    }

    private void createNewGroups(final Map<String, GroupLink> currentLinks, final Map<String, Organization> actual) {
        // create new users
        final Set<String> newOrgsIds = Sets.difference(actual.keySet(), currentLinks.keySet());
        if (newOrgsIds.isEmpty()) {
            log.debug("No new organizations found.");
            return;
        }
        newOrgsIds.forEach(id -> this.forceMatchingGeonetworkGroup(actual.get(id)));
    }

    private void reconcileMismatchedGroups(final Map<String, GroupLink> currentLinks,
            final Map<String, Organization> actual) {
        // update info on existing users
        final Set<String> updateCandidates = Sets.intersection(currentLinks.keySet(), actual.keySet());
        if (updateCandidates.isEmpty()) {
            log.debug("No organizations changed");
            return;
        }
        updateCandidates.forEach(id -> {
            Organization canonical = actual.get(id);
            GroupLink link = currentLinks.get(id);
            this.reconcile(canonical, link);
        });
    }

    private void deleteGoneGroups(final Map<String, GroupLink> currentLinks, final Map<String, Organization> actual) {
        final Set<String> deleteCandidates = Sets.difference(currentLinks.keySet(), actual.keySet());
        if (deleteCandidates.isEmpty()) {
            log.info("No organizations were deleted.");
            return;
        }
        for (String groupId : deleteCandidates) {
            GroupLink link = currentLinks.get(groupId);
            final int usersCount = groupLinks.countGroupUsers(link);
            if (usersCount > 0L) {
                log.warn("Cannot delete group '%s', %d users still linked to it.", link.getGeonetworkGroup().getName(),
                        usersCount);
            } else {
                log.info("Deleting GeoNetwork group %s...", link.getGeonetworkGroup().getName());
                groupLinks.deleteLinkAndGroup(link);
            }
        }
    }

    private String buildDeletedUserName(UserLink link) {
        String id = link.getGeorchestraUserId();
        // if it's a UUID, use it
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException notAnUUID) {
            id = UUID.randomUUID().toString();
        }
        String name = String.format("deleted_%s", id);
        return name.substring(0, Math.min(name.length(), 255));
    }

    private Map<String, UserLink> getExistingUserLinksById() {
        return this.userLinks.findAll().stream()
                .collect(Collectors.toMap(UserLink::getGeorchestraUserId, Function.identity()));
    }

    private Map<String, GroupLink> getExistingGroupLinksById() {
        return this.groupLinks.findAll().stream()
                .collect(Collectors.toMap(GroupLink::getGeorchestraOrgId, Function.identity()));
    }

    private <T> Map<String, T> toIdMap(List<T> list, Function<T, String> idExtractor) {
        final Map<String, T> actual = list.stream().collect(Collectors.toMap(idExtractor, Function.identity()));
        return actual;
    }

    public void synchronizeGroupsWithRoles(List<Role> roles) {
        // TODO Auto-generated method stub

    }

}
