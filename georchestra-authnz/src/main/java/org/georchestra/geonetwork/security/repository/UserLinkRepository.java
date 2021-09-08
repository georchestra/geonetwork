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

import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.fao.geonet.domain.User;
import org.fao.geonet.domain.georchestra.JPAUserLink;
import org.georchestra.geonetwork.jpa.JPAUserLinkRepository;
import org.georchestra.geonetwork.logging.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

/**
 * Repository to track 1:1 relationships between geOrchestra users (given by
 * their id), and GeoNetwork {@link User users}
 */
@Service
public class UserLinkRepository {
    private static final Logging log = Logging.getLogger("org.georchestra.geonetwork.security.repository");

    private @Autowired JPAUserLinkRepository _repo;

    private JPAUserLinkRepository repo() {
        return _repo;
    }

    public Optional<UserLink> findById(@NonNull String georchestraUserId) {
        return repo().findById(georchestraUserId).map(this::toModel);
    }

    @Transactional
    public void save(UserLink link) {
        Objects.requireNonNull(link);
        JPAUserLink entity = toJPA(link);
        repo().save(entity);
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
}
