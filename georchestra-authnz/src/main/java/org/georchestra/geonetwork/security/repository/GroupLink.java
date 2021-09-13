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

import org.fao.geonet.domain.Group;
import org.georchestra.security.model.Organization;

/**
 * Entity that represents a link between a geOrchestra {@link Organization} and
 * a GeoNetwork {@link Group}.
 * <p>
 * Used to enforce a 1:1 relationship between the two, with geOrchestra
 * organizations being the source of truth for authentication and authorization,
 * while at the same time not interfering with regular GeoNetwork internals,
 * which require groups to be defined on its PostgreSQL database.
 */
public class GroupLink {
    private String georchestraOrgId;
    private Group geonetworkGroup;
    private String lastUpdated;

    public String getGeorchestraOrgId() {
        return georchestraOrgId;
    }

    public GroupLink setGeorchestraOrgId(String georchestraOrgId) {
        this.georchestraOrgId = georchestraOrgId;
        return this;
    }

    public Group getGeonetworkGroup() {
        return geonetworkGroup;
    }

    public GroupLink setGeonetworkGroup(Group geonetworkGroup) {
        this.geonetworkGroup = geonetworkGroup;
        return this;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public GroupLink setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(geonetworkGroup, georchestraOrgId, lastUpdated);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GroupLink other = (GroupLink) obj;
        return Objects.equals(geonetworkGroup, other.geonetworkGroup)
                && Objects.equals(georchestraOrgId, other.georchestraOrgId)
                && Objects.equals(lastUpdated, other.lastUpdated);
    }

}
