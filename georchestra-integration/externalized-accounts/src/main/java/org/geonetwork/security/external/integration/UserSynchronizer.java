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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.fao.geonet.domain.UserGroup;
import org.fao.geonet.repository.UserGroupRepository;
import org.fao.geonet.repository.UserRepository;
import org.fao.geonet.repository.specification.UserGroupSpecs;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.UserLink;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.geonetwork.security.external.repository.UserLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import com.google.common.collect.Sets;

class UserSynchronizer {
    static final Logger log = LoggerFactory.getLogger(UserSynchronizer.class.getPackage().getName());

    /** GN internals require {@link ApplicationContextHolder#set} */
    private @Autowired ConfigurableApplicationContext appContext;

    /** external source of truth for users */
    private final CanonicalAccountsRepository canonicalAccounts;

    /** link management between external and internal user definitions */
    private @Autowired UserLinkRepository externalUserLinks;
    private @Autowired UserRepository gnUserRepository;

    protected @Autowired UserGroupRepository internalUserToGroupLinks;

    private @Autowired GroupSynchronizer userProvilegesResolver;

    public UserSynchronizer(CanonicalAccountsRepository canonicalAccounts) {
        Objects.requireNonNull(canonicalAccounts);
        this.canonicalAccounts = canonicalAccounts;
    }

    public List<CanonicalUser> findCanonicalUsers() {
        ApplicationContextHolder.set(appContext);
        return canonicalAccounts.findAllUsers();
    }

    public List<UserLink> getSynchronizedUsers() {
        ApplicationContextHolder.set(appContext);
        return externalUserLinks.findAll();
    }

    public Optional<UserLink> findUserLink(@NonNull String canonicalUserId) {
        ApplicationContextHolder.set(appContext);
        return externalUserLinks.findById(canonicalUserId);
    }

    @Transactional
    public void synchronizeAll() {
        ApplicationContextHolder.set(appContext);
        log.debug("Fetching canonical user definitions...");
        List<CanonicalUser> canonicalUsers = findCanonicalUsers();
        synchronizeAll(canonicalUsers);
        log.info("Users synchronization complete.");
    }

    @Transactional
    public void synchronizeAll(List<CanonicalUser> canonical) {
        ApplicationContextHolder.set(appContext);
        requireNonNull(canonical);
        canonical.forEach(u -> requireNonNull(u, "null references not accepted in user's list"));

        log.debug("Syncrhonizing {} canonical user definitions...", canonical.size());
        try {
            final Set<String> canonicalIds = canonical.stream().map(CanonicalUser::getId).collect(Collectors.toSet());
            final Map<String, UserLink> currentLinks = getExistingUserLinksById();

            deleteGoneUsers(canonicalIds, currentLinks);
            canonical.forEach(this::synchronize);
        } catch (RuntimeException e) {
            log.error("Error synchronizing users", e);
            throw e;
        }
    }

    /**
     * Ensures a GeoNetwork {@link User} exists matching the {@code canonical}
     * (externally defined) user properties and authorization settings.
     * <p>
     * 
     */
    @Transactional
    public UserLink synchronize(CanonicalUser canonical) {
        ApplicationContextHolder.set(appContext);
        requireNonNull(canonical);

        UserLink link = resolveLink(canonical);
        if (!link.isUpToDateWith(canonical)) {
            Privileges privileges = userProvilegesResolver.resolvePrivilegesFor(canonical);
            setGeonetworkUserProperties(canonical, link.getInternalUser(), privileges.getUserProfile());
            link.setLastUpdated(canonical.getLastUpdated());
            link = externalUserLinks.save(link);
            synchronizeUserGroups(link.getInternalUser(), privileges.getAdditionalProvileges());
        }
        return link;
    }

    private void synchronizeUserGroups(User user, List<Privilege> privileges) {
        Map<Integer, UserGroup> current = getCurrentUserGroups(user);
        Map<Integer, Privilege> actual = toIdMap(privileges, gl -> gl.getGroup().getId());
        Set<Integer> gone = Sets.difference(current.keySet(), actual.keySet());
        // remove gone group associations
        for (Integer goneGroupLinkId : gone) {
            UserGroup goneAssociation = current.get(goneGroupLinkId);
            this.internalUserToGroupLinks.delete(goneAssociation);
            log.info("Removed association of user {} to group {}", user.getUsername(),
                    goneAssociation.getGroup().getName());
        }
        List<UserGroup> userGroupLinks = new ArrayList<>();
        userGroupLinks.addAll(resolveNewPrivileges(user, actual, current));
        userGroupLinks.addAll(resolveUpdatedPrivileges(user, actual, current));
        this.internalUserToGroupLinks.saveAll(userGroupLinks);
    }

    private List<UserGroup> resolveUpdatedPrivileges(User user, Map<Integer, Privilege> actual,
            Map<Integer, UserGroup> current) {

        Set<Integer> commonGroupIds = Sets.intersection(actual.keySet(), current.keySet());
        return commonGroupIds.stream()//
                .map(toUpdateGroupId -> {
                    UserGroup currentUserGroup = current.get(toUpdateGroupId);
                    Privilege privilege = actual.get(toUpdateGroupId);
                    Profile profile = privilege.getProfile();
                    Profile currentProfile = currentUserGroup.getProfile();
                    Group group = privilege.getGroup();
                    if (Objects.equals(profile, currentProfile)) {
                        return null;
                    }
                    log.info("Updating user {}'s profile for group {} from {} to {}", user.getUsername(),
                            group.getName(), currentProfile, profile);
                    currentUserGroup.setProfile(profile);
                    return currentUserGroup;
                })//
                .filter(Objects::nonNull)//
                .collect(Collectors.toList());
    }

    private List<UserGroup> resolveNewPrivileges(User user, Map<Integer, Privilege> actual,
            Map<Integer, UserGroup> current) {

        final Set<Integer> newGroupIds = Sets.difference(actual.keySet(), current.keySet());
        return newGroupIds.stream()//
                .map(actual::get)//
                .map(privilege -> newUserGroup(user, privilege))//
                .collect(Collectors.toList());
    }

    private UserGroup newUserGroup(User user, Privilege privilege) {
        log.info("Adding profile {} to group {} for user {}", privilege.getProfile(), privilege.getGroup().getName(),
                user.getUsername());
        return new UserGroup().setUser(user).setGroup(privilege.getGroup()).setProfile(privilege.getProfile());
    }

    private Map<Integer, UserGroup> getCurrentUserGroups(User user) {
        Specification<UserGroup> spec = UserGroupSpecs.hasUserId(user.getId());
        List<UserGroup> userGroups = internalUserToGroupLinks.findAll(spec);
        return toIdMap(userGroups, ug -> ug.getGroup().getId());
    }

    private UserLink resolveLink(CanonicalUser canonical) {
        UserLink link = findUserLink(canonical.getId()).orElseGet(UserLink::new);
        if (link.isUpToDateWith(canonical)) {
            log.debug("GN user {} is up to date", canonical.getUsername());
        } else if (null == link.getInternalUser()) {
            // try to reconcile an existing user
            User user = this.gnUserRepository.findOneByUsername(canonical.getUsername());
            if (user == null) {
                user = new User();
                log.info("Creating GN User {} (id: {})...", canonical.getUsername(), canonical.getId());
            } else {
                log.info("Reconciling existing GN User {} with canonical user (id: {})", user.getUsername(),
                        canonical.getId());
            }
            link.setCanonicalUserId(canonical.getId());
            link.setInternalUser(user);
        } else {
            log.info("GN user {} (version '{}') is outdated, reconciling to version '{}'", //
                    canonical.getUsername(), link.getLastUpdated(), canonical.getLastUpdated());
        }
        return link;
    }

    private void setGeonetworkUserProperties(CanonicalUser canonical, User target, Profile profile) {
        requireNonNull(canonical);
        requireNonNull(target);
        target.setUsername(canonical.getUsername());
        target.setName(canonical.getFirstName());
        target.setSurname(canonical.getLastName());
        target.setOrganisation(canonical.getOrganization());

        String title = canonical.getTitle();
        if (title != null && title.length() > 16) {
            // hack: PSQLException: ERROR: value too long for type character varying(16)
            title = title.substring(0, 16);
        }
        target.setKind(title);

        target.getEmailAddresses().clear();
        if (null != canonical.getEmail()) {
            target.getEmailAddresses().add(canonical.getEmail());
        }
        target.setProfile(profile);
    }

    private Map<String, UserLink> getExistingUserLinksById() {
        return toIdMap(this.externalUserLinks.findAll(), UserLink::getCanonicalUserId);
    }

    private void deleteGoneUsers(final Set<String> canonicalUserIds, final Map<String, UserLink> currentLinks) {
        final Set<String> deleteCandidates = Sets.difference(currentLinks.keySet(), canonicalUserIds);

        for (String goneUserId : deleteCandidates) {
            UserLink goneUserLink = currentLinks.get(goneUserId);
            deleteUser(goneUserLink);
        }
    }

    private void deleteUser(UserLink userLink) {
        long recordCount = externalUserLinks.countMetadataRecords(userLink);
        if (recordCount > 0L) {
            log.warn("Cannot delete user '{}' who is owner of {} metadata record(s).",
                    userLink.getInternalUser().getName(), recordCount);
            // can't delete the user, but can delete the link and rename the user to avoid
            // conflicts with future users with the same name
            final String deletedUserName = buildDeletedUserName(userLink);
            userLink.getInternalUser().setName(deletedUserName);
            externalUserLinks.save(userLink);
            externalUserLinks.delete(userLink);
        } else {
            log.info("Deleting GeoNetwork user {}...", userLink.getInternalUser().getUsername());
            externalUserLinks.deleteLinkAndUser(userLink);
        }
    }

    private String buildDeletedUserName(UserLink link) {
        String id = link.getCanonicalUserId();
        // if it's a UUID, use it
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException notAnUUID) {
            id = UUID.randomUUID().toString();
        }
        String name = String.format("deleted_%s", id);
        return name.substring(0, Math.min(name.length(), 255));
    }

    private <K, T> Map<K, T> toIdMap(List<T> list, Function<T, K> idExtractor) {
        return list.stream().collect(Collectors.toMap(idExtractor, Function.identity()));
    }

}
