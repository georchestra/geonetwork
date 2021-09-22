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
package org.fao.geonet.domain.external;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.fao.geonet.domain.User;

/**
 * Entity that represents a link between an external-system's user(given by its
 * {@link #externalUserId id}), and a GeoNetwork {@link User user}.
 * <p>
 * Used to enforce a 1:1 relationship between the two, with the external system
 * users being the source of truth for authentication and authorization, while
 * at the same time not interfering with regular GeoNetwork internals, which
 * require users to be defined on its PostgreSQL database.
 */
@Entity
@Access(AccessType.PROPERTY)
@Table(name = "user_external_link")
public class ExternalUserLink {

    private String externalUserId;

    private String lastUpdated;

    private User geonetworkUser;

    @Id
    public String getExternalUserId() {
        return externalUserId;
    }

    @OneToOne(optional = true)
    public User getGeonetworkUser() {
        return geonetworkUser;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public ExternalUserLink setExternalUserId(String externalUserId) {
        this.externalUserId = externalUserId;
        return this;
    }

    public ExternalUserLink setGeonetworkUser(User geonetworkUser) {
        this.geonetworkUser = geonetworkUser;
        return this;
    }

    public ExternalUserLink setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(geonetworkUser, externalUserId, lastUpdated);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExternalUserLink other = (ExternalUserLink) obj;
        return Objects.equals(geonetworkUser, other.geonetworkUser)
                && Objects.equals(externalUserId, other.externalUserId)
                && Objects.equals(lastUpdated, other.lastUpdated);
    }

}
