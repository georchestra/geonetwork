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

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.fao.geonet.ApplicationContextHolder;
import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Language;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.repository.GroupRepository;
import org.fao.geonet.repository.LanguageRepository;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.geonetwork.security.external.repository.GroupLinkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.common.collect.Sets;

abstract class AbstractGroupSynchronizer implements GroupSynchronizer {
    static final Logger log = LoggerFactory.getLogger(AbstractGroupSynchronizer.class.getPackage().getName());

    /** GN internals require {@link ApplicationContextHolder#set} */
    private @Autowired ConfigurableApplicationContext appContext;

    protected final CanonicalAccountsRepository canonicalAccounts;

    protected @Autowired GroupLinkRepository externalGroupLinks;
    private @Autowired GroupRepository gnGroupRepository;

    private @Autowired LanguageRepository langRepository;

    protected @Autowired ExternalizedSecurityProperties configProperties;

    protected AbstractGroupSynchronizer(CanonicalAccountsRepository canonicalAccounts) {
        Objects.requireNonNull(canonicalAccounts);
        this.canonicalAccounts = canonicalAccounts;
    }

    protected abstract GroupSyncMode getOrigin();

    @Override
    public List<GroupLink> getSynchronizedGroups() {
        ApplicationContextHolder.set(appContext);
        return externalGroupLinks.findAll();
    }

    @Override
    public Optional<GroupLink> findGroupLink(String canonicalGroupId) {
        ApplicationContextHolder.set(appContext);
        return externalGroupLinks.findById(canonicalGroupId);
    }

    @Override
    @Transactional
    public void synchronizeAll() {
        ApplicationContextHolder.set(appContext);
        List<CanonicalGroup> canonicalGroups = findCanonicalGroups();
        synchronizeAll(canonicalGroups);
    }

    @Override
    @Transactional
    public void synchronizeAll(List<CanonicalGroup> canonical) {
        ApplicationContextHolder.set(appContext);
        requireNonNull(canonical);
        canonical.forEach(u -> requireNonNull(u, "null references not accepted in groups list"));
        log.debug("Syncrhonizing {} canonical group definitions...", canonical.size());
        try {
            final Set<String> canonicalIds = canonical.stream().map(CanonicalGroup::getId).collect(Collectors.toSet());
            final Map<String, GroupLink> currentLinks = getExistingGroupLinksById();

            deleteGoneGroups(currentLinks, canonicalIds);
            canonical.forEach(this::synchronize);

        } catch (RuntimeException e) {
            log.error("Error synchronizing groups from {}", getOrigin(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public GroupLink synchronize(CanonicalGroup canonical) {
        ApplicationContextHolder.set(appContext);

        final String canonicalGroupId = canonical.getId();

        GroupLink link = findGroupLink(canonicalGroupId).orElseGet(GroupLink::new);
        if (link.isUpToDateWith(canonical)) {
            log.debug("GN group {} is up to date. Origin: {}, Id: {}, version: {}", canonical.getName(),
                    link.getCanonical().getOrigin(), canonical.getId(), canonical.getLastUpdated());
            return link;
        }

        if (null == link.getGeonetworkGroup()) {
            // try to reconcile an existing group
            Group group = this.gnGroupRepository.findByName(canonical.getName());
            if (null == group) {
                group = new Group();
                log.info("Creatinng GN group {} (Id: {}, version '{}')", //
                        canonical.getName(), canonical.getId(), canonical.getLastUpdated());
            } else {
                log.info("Reconciling existing GN group {} with canonical group (id: {})", group.getName(),
                        canonical.getId());
            }
            link.setGeonetworkGroup(group);
        } else {
            log.info("Updating GN group {} (Id: {}, version '{}'), reconciling to version '{}'", //
                    canonical.getName(), canonical.getId(), link.getCanonical().getLastUpdated(),
                    canonical.getLastUpdated());
        }

        Group group = link.getGeonetworkGroup();
        final boolean nameChanged = !Objects.equals(canonical.getName(), group.getName());
        if (nameChanged || group.getLabelTranslations().isEmpty()) {
            updateLabelTranslations(canonical, group);
        }

        group.setName(canonical.getName());
        group.setDescription(canonical.getDescription());
        group.setWebsite(canonical.getLinkage());
        link.setCanonical(canonical);
        link = externalGroupLinks.save(link);
        assert link.isUpToDateWith(canonical);

        return link;
    }

    @Override
    @Transactional
    public Privileges resolvePrivilegesFor(CanonicalUser user) {
        ApplicationContextHolder.set(appContext);
        final List<CanonicalGroup> canonicalGroups = resolveGroupsOf(user);

        Privileges userPrivileges = new Privileges(resolveDefaultProfile(user));
        Stream<Group> groups = canonicalGroups.stream().map(this::synchronize).map(GroupLink::getGeonetworkGroup);
        groups.map(g -> resolvePrivilegeFor(user, g)).forEach(userPrivileges.getAdditionalProvileges()::add);
        return userPrivileges;
    }

    /**
     * Strategy method to resolve which canonical groups a user belongs to
     */
    protected abstract List<CanonicalGroup> resolveGroupsOf(CanonicalUser user);

    protected Profile resolveDefaultProfile(CanonicalUser user) {
        return configProperties.getProfiles().resolveHighestProfileFromRoleNames(user.getRoles());
    }

    protected abstract Privilege resolvePrivilegeFor(CanonicalUser user, Group group);

    private Map<String, GroupLink> getExistingGroupLinksById() {
        return toIdMap(this.externalGroupLinks.findAll(), g -> g.getCanonical().getId());
    }

    private void deleteGoneGroups(final Map<String, GroupLink> currentLinks, final Set<String> canonicalGroupIds) {
        final Set<String> deleteCandidateIds = Sets.difference(currentLinks.keySet(), canonicalGroupIds);
        if (deleteCandidateIds.isEmpty()) {
            log.info("No organizations were deleted.");
            return;
        }
        for (String groupId : deleteCandidateIds) {
            final GroupLink link = currentLinks.get(groupId);
            final Group geonetworkGroup = link.getGeonetworkGroup();
            final int usersCount = externalGroupLinks.countGroupUsers(link);
            if (usersCount > 0L) {
                log.warn("Removing {} users from group '{}'.", usersCount, geonetworkGroup.getName());
                externalGroupLinks.removeUsersFromGroup(geonetworkGroup);
            }
            log.info("Deleting GeoNetwork group {}...", geonetworkGroup.getName());
            externalGroupLinks.deleteLinkAndGroup(link);
        }
    }

    private void updateLabelTranslations(CanonicalGroup canonical, Group group) {
        final String newName = canonical.getName();
        Map<String, String> newLangs = langRepository.findAll().stream()
                .collect(Collectors.toMap(Language::getId, l -> newName));
        newLangs.forEach(group.getLabelTranslations()::put);
    }

    private <T> Map<String, T> toIdMap(List<T> list, Function<T, String> idExtractor) {
        final Map<String, T> actual = list.stream().collect(Collectors.toMap(idExtractor, Function.identity()));
        return actual;
    }

}
