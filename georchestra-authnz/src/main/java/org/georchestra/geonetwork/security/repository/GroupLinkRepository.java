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
package org.georchestra.geonetwork.security.repository;

import static java.util.Objects.requireNonNull;
import static org.springframework.data.jpa.domain.Specification.where;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.georchestra.JPAGroupLink;
import org.fao.geonet.repository.GroupRepository;
import org.fao.geonet.repository.UserGroupRepository;
import org.fao.geonet.repository.specification.UserGroupSpecs;
import org.georchestra.geonetwork.jpa.JPAGroupLinkRepository;
import org.georchestra.security.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Repository to track 1:1 relationships between geOrchestra {@link Organization
 * orgs} (given by their id), and GeoNetwork {@link Group groups}
 */
@Service
@Transactional(value = TxType.SUPPORTS)
public class GroupLinkRepository {

    private @Autowired GroupRepository gnGroupRepository;
    private @Autowired UserGroupRepository userGroupRepository;
    private @Autowired JPAGroupLinkRepository linksRepo;

    public Optional<GroupLink> findById(@NonNull String georchestraGroupId) {
        return linksRepo.findById(georchestraGroupId).map(this::toModel);
    }

    public List<GroupLink> findAll() {
        return Lists.newArrayList(Iterables.transform(linksRepo.findAll(), this::toModel));
    }

    @Transactional
    public GroupLink save(GroupLink link) {
        requireNonNull(link);
        requireNonNull(link.getGeonetworkGroup());
        requireNonNull(link.getGeorchestraOrgId());
        requireNonNull(link.getLastUpdated());

        JPAGroupLink jpaLink = toJPA(link);
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

    private JPAGroupLink toJPA(GroupLink link) {
        Objects.requireNonNull(link);
        return new JPAGroupLink()//
                .setGeorchestraOrgId(link.getGeorchestraOrgId())//
                .setLastUpdated(link.getLastUpdated())//
                .setGeonetworkGroup(link.getGeonetworkGroup());
    }

    private GroupLink toModel(JPAGroupLink link) {
        Objects.requireNonNull(link);
        return new GroupLink()//
                .setGeorchestraOrgId(link.getGeorchestraOrgId())//
                .setLastUpdated(link.getLastUpdated())//
                .setGeonetworkGroup(link.getGeonetworkGroup());
    }

}
