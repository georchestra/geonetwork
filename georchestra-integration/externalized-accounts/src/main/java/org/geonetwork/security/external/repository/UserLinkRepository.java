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

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.fao.geonet.domain.User;
import org.fao.geonet.domain.UserGroupId_;
import org.fao.geonet.domain.external.ExternalUserLink;
import org.fao.geonet.kernel.datamanager.IMetadataUtils;
import org.fao.geonet.repository.UserGroupRepository;
import org.fao.geonet.repository.UserRepository;
import org.fao.geonet.repository.specification.MetadataSpecs;
import org.geonetwork.security.external.model.UserLink;
import org.geonetwork.security.external.repository.jpa.ExternalUserLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Repository to track 1:1 relationships between the external system users
 * (given by their id), and GeoNetwork {@link User users}
 */
@Transactional(value = TxType.SUPPORTS)
public class UserLinkRepository {

    private @Autowired UserRepository gnUserRepository;
    private @Autowired UserGroupRepository userGroupRepository;
    private @Autowired IMetadataUtils metadataRepository;

    private @Autowired ExternalUserLinkRepository linksRepo;

    public Optional<UserLink> findById(@NonNull String canonicalUserId) {
        return linksRepo.findById(canonicalUserId).map(this::toModel);
    }

    public List<UserLink> findAll() {
        return Lists.newArrayList(Iterables.transform(linksRepo.findAll(), this::toModel));
    }

    @Transactional
    public UserLink save(UserLink link) {
        requireNonNull(link);
        requireNonNull(link.getInternalUser());
        requireNonNull(link.getCanonicalUserId());
        requireNonNull(link.getLastUpdated());

        ExternalUserLink jpaLink = toJPA(link);
        User gnUser = link.getInternalUser();
        gnUser = gnUserRepository.save(gnUser);
        jpaLink.setGeonetworkUser(gnUser);
        jpaLink = linksRepo.save(jpaLink);
        return toModel(jpaLink);
    }

    private ExternalUserLink toJPA(UserLink link) {
        Objects.requireNonNull(link);
        return new ExternalUserLink()//
                .setExternalUserId(link.getCanonicalUserId())//
                .setLastUpdated(link.getLastUpdated())//
                .setGeonetworkUser(link.getInternalUser());
    }

    private UserLink toModel(ExternalUserLink link) {
        Objects.requireNonNull(link);
        return new UserLink()//
                .setCanonicalUserId(link.getExternalUserId())//
                .setLastUpdated(link.getLastUpdated())//
                .setInternalUser(link.getGeonetworkUser());
    }

    public long countMetadataRecords(UserLink link) {
        User u = link.getInternalUser();
        return metadataRepository.count(MetadataSpecs.isOwnedByUser(u.getId()));
    }

    @Transactional
    public void deleteLinkAndUser(UserLink link) {
        delete(link);
        int userId = link.getInternalUser().getId();
        this.userGroupRepository.deleteAllByIdAttribute(UserGroupId_.userId, Collections.singleton(userId));
        this.gnUserRepository.delete(link.getInternalUser());
    }

    @Transactional
    public void delete(UserLink link) {
        this.linksRepo.delete(toJPA(link));
    }
}
