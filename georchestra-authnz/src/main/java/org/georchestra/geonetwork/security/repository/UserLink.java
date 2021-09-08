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

import org.fao.geonet.domain.User;

/**
 * Entity that represents a link between a geOrchestra user (given by its
 * {@link #georchestraUserId id}), and a GeoNetwork {@link User user}.
 * <p>
 * Used to enforce a 1:1 relationship between the two, with geOrchestra users
 * being the source of truth for authentication and authorization, while at the
 * same time not interfering with regular GeoNetwork internals, which require
 * users to be defined on its PostgreSQL database.
 */
public class UserLink {
    private String georchestraUserId;
    private User geonetworkUser;
    private String lastUpdated;

    public String getGeorchestraUserId() {
        return georchestraUserId;
    }

    public UserLink setGeorchestraUserId(String georchestraUserId) {
        this.georchestraUserId = georchestraUserId;
        return this;
    }

    public User getGeonetworkUser() {
        return geonetworkUser;
    }

    public UserLink setGeonetworkUser(User geonetworkUser) {
        this.geonetworkUser = geonetworkUser;
        return this;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public UserLink setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(geonetworkUser, georchestraUserId, lastUpdated);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserLink other = (UserLink) obj;
        return Objects.equals(geonetworkUser, other.geonetworkUser)
                && Objects.equals(georchestraUserId, other.georchestraUserId)
                && Objects.equals(lastUpdated, other.lastUpdated);
    }

}
