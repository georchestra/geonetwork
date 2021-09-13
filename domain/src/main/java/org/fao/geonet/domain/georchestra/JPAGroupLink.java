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
package org.fao.geonet.domain.georchestra;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.fao.geonet.domain.Group;

/**
 * Entity that represents a link between a geOrchestra Organization (given by
 * its {@link #georchestraOrgId id}), and a GeoNetwork {@link Group}.
 * <p>
 * Used to enforce a 1:1 relationship between the two, with geOrchestra
 * organizations being the source of truth for authentication and authorization,
 * while at the same time not interfering with regular GeoNetwork internals,
 * which require groups to be defined on its PostgreSQL database.
 */
@Entity(name = "GroupLink")
@Access(AccessType.PROPERTY)
@Table(schema = "geonetwork_georchestra")
public class JPAGroupLink {

    private String georchestraOrgId;

    private Group geonetworkGroup;

    private String lastUpdated;

    @Id
    public String getGeorchestraOrgId() {
        return georchestraOrgId;
    }

    public JPAGroupLink setGeorchestraOrgId(String georchestraOrgId) {
        this.georchestraOrgId = georchestraOrgId;
        return this;
    }

    @OneToOne
    public Group getGeonetworkGroup() {
        return geonetworkGroup;
    }

    public JPAGroupLink setGeonetworkGroup(Group geonetworkGroup) {
        this.geonetworkGroup = geonetworkGroup;
        return this;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public JPAGroupLink setLastUpdated(String lastUpdated) {
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
        JPAGroupLink other = (JPAGroupLink) obj;
        return Objects.equals(geonetworkGroup, other.geonetworkGroup)
                && Objects.equals(georchestraOrgId, other.georchestraOrgId)
                && Objects.equals(lastUpdated, other.lastUpdated);
    }

}
