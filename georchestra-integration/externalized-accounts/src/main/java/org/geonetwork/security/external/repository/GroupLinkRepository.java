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
package org.geonetwork.security.external.repository;

import static java.util.Objects.requireNonNull;
import static org.springframework.data.jpa.domain.Specification.where;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.xml.registry.infomodel.Organization;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.UserGroupId_;
import org.fao.geonet.domain.external.ExternalGroupLink;
import org.fao.geonet.repository.GroupRepository;
import org.fao.geonet.repository.UserGroupRepository;
import org.fao.geonet.repository.specification.UserGroupSpecs;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.repository.jpa.ExternalGroupLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Repository to track 1:1 relationships between geOrchestra {@link Organization
 * orgs} (given by their id), and GeoNetwork {@link Group groups}
 */
@Transactional(value = TxType.SUPPORTS)
public class GroupLinkRepository {

    private @Autowired GroupRepository gnGroupRepository;
    private @Autowired UserGroupRepository userGroupRepository;
    private @Autowired ExternalGroupLinkRepository linksRepo;

    public Optional<GroupLink> findById(@NonNull String canonicalGroupId) {
        return linksRepo.findById(canonicalGroupId).map(this::toModel);
    }

    public Optional<GroupLink> findByName(@NonNull String name) {
        return linksRepo.findByName(name).map(this::toModel);
    }

    public List<GroupLink> findAll() {
        return Lists.newArrayList(Iterables.transform(linksRepo.findAll(), this::toModel));
    }

    @Transactional
    public GroupLink save(GroupLink link) {
        requireNonNull(link);
        requireNonNull(link.getGeonetworkGroup());
        requireNonNull(link.getCanonical());
        requireNonNull(link.getCanonical().getId());
        requireNonNull(link.getCanonical().getName());
        requireNonNull(link.getCanonical().getLastUpdated());
        requireNonNull(link.getCanonical().getOrigin());

        ExternalGroupLink jpaLink = toJPA(link);
        Group gnGroup = link.getGeonetworkGroup();
        gnGroup = gnGroupRepository.save(gnGroup);
        jpaLink.setGeonetworkGroup(gnGroup);
        jpaLink = linksRepo.save(jpaLink);
        return toModel(jpaLink);
    }

    @Transactional
    public void deleteLinkAndGroup(GroupLink link) {
        delete(link);
        this.gnGroupRepository.delete(link.getGeonetworkGroup());
    }

    @Transactional
    public void delete(GroupLink link) {
        this.linksRepo.delete(toJPA(link));
    }

    public int countGroupUsers(GroupLink link) {
        final int groupId = link.getGeonetworkGroup().getId();
        long userLinksToGroup = this.userGroupRepository.count(where(UserGroupSpecs.hasGroupId(groupId)));
        return (int) userLinksToGroup;
    }

    @Transactional
    public void removeUsersFromGroup(Group geonetworkGroup) {
        int groupId = geonetworkGroup.getId();
        this.userGroupRepository.deleteAllByIdAttribute(UserGroupId_.groupId, Collections.singleton(groupId));
    }

    private ExternalGroupLink toJPA(GroupLink link) {
        Objects.requireNonNull(link);
        return new ExternalGroupLink()//
                .setOrigin(toJpa(link.getCanonical().getOrigin()))//
                .setGeonetworkGroup(link.getGeonetworkGroup())//
                .setExternalId(link.getCanonical().getId())//
                .setName(link.getCanonical().getName())//
                .setLastUpdated(link.getCanonical().getLastUpdated())//
                .setDescription(link.getCanonical().getDescription())//
                .setLinkage(link.getCanonical().getLinkage());
    }

    private GroupLink toModel(ExternalGroupLink link) {
        Objects.requireNonNull(link);
        return new GroupLink()//
                .setCanonical(toCanonical(link))//
                .setGeonetworkGroup(link.getGeonetworkGroup());
    }

    private CanonicalGroup toCanonical(ExternalGroupLink link) {
        return CanonicalGroup.builder()//
                .withId(link.getExternalId())//
                .withName(link.getName())//
                .withDescription(link.getDescription())//
                .withLastUpdated(link.getLastUpdated())//
                .withLinkage(link.getLinkage())//
                .withOrigin(toModel(link.getOrigin()))//
                .build();
    }

    private ExternalGroupLink.GroupSyncMode toJpa(GroupSyncMode origin) {
        switch (origin) {
        case orgs:
            return ExternalGroupLink.GroupSyncMode.org;
        case roles:
            return ExternalGroupLink.GroupSyncMode.role;
        default:
            throw new IllegalArgumentException("Unexpected GroupSyncMode: " + origin);
        }
    }

    private GroupSyncMode toModel(ExternalGroupLink.GroupSyncMode jpa) {
        switch (jpa) {
        case org:
            return GroupSyncMode.orgs;
        case role:
            return GroupSyncMode.roles;
        default:
            throw new IllegalArgumentException("Unexpected ExternalGroupLink.GroupSyncMode: " + jpa);
        }
    }

}
