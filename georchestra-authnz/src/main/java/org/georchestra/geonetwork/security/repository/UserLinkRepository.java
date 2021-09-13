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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.fao.geonet.domain.User;
import org.fao.geonet.domain.UserGroupId_;
import org.fao.geonet.domain.georchestra.JPAUserLink;
import org.fao.geonet.kernel.datamanager.IMetadataUtils;
import org.fao.geonet.repository.UserGroupRepository;
import org.fao.geonet.repository.UserRepository;
import org.fao.geonet.repository.specification.MetadataSpecs;
import org.georchestra.geonetwork.jpa.JPAUserLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Repository to track 1:1 relationships between geOrchestra users (given by
 * their id), and GeoNetwork {@link User users}
 */
@Service
@Transactional(value = TxType.SUPPORTS)
public class UserLinkRepository {

    private @Autowired UserRepository gnUserRepository;
    private @Autowired UserGroupRepository userGroupRepository;
    private @Autowired IMetadataUtils metadataRepository;

    private @Autowired JPAUserLinkRepository linksRepo;

    public Optional<UserLink> findById(@NonNull String georchestraUserId) {
        return linksRepo.findById(georchestraUserId).map(this::toModel);
    }

    public List<UserLink> findAll() {
        return Lists.newArrayList(Iterables.transform(linksRepo.findAll(), this::toModel));
    }

    @Transactional
    public UserLink save(UserLink link) {
        requireNonNull(link);
        requireNonNull(link.getGeonetworkUser());
        requireNonNull(link.getGeorchestraUserId());
        requireNonNull(link.getLastUpdated());

        JPAUserLink jpaLink = toJPA(link);
        User gnUser = link.getGeonetworkUser();
        gnUser = gnUserRepository.save(gnUser);
        jpaLink.setGeonetworkUser(gnUser);
        jpaLink = linksRepo.save(jpaLink);
        return toModel(jpaLink);
    }

    private JPAUserLink toJPA(UserLink link) {
        Objects.requireNonNull(link);
        return new JPAUserLink()//
                .setGeorchestraUserId(link.getGeorchestraUserId())//
                .setLastUpdated(link.getLastUpdated())//
                .setGeonetworkUser(link.getGeonetworkUser());
    }

    private UserLink toModel(JPAUserLink link) {
        Objects.requireNonNull(link);
        return new UserLink()//
                .setGeorchestraUserId(link.getGeorchestraUserId())//
                .setLastUpdated(link.getLastUpdated())//
                .setGeonetworkUser(link.getGeonetworkUser());
    }

    public long countMetadataRecords(UserLink link) {
        User u = link.getGeonetworkUser();
        return metadataRepository.count(MetadataSpecs.isOwnedByUser(u.getId()));
    }

    @Transactional
    public void deleteLinkAndUser(UserLink link) {
        delete(link);
        int userId = link.getGeonetworkUser().getId();
        this.userGroupRepository.deleteAllByIdAttribute(UserGroupId_.userId, Collections.singleton(userId));
        this.gnUserRepository.delete(link.getGeonetworkUser());
    }

    @Transactional
    public void delete(UserLink link) {
        this.linksRepo.delete(toJPA(link));
    }
}
