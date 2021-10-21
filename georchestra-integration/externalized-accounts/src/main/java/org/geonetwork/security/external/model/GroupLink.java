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
package org.geonetwork.security.external.model;

import java.util.Objects;

import org.fao.geonet.domain.Group;

/**
 * Entity that represents a link between an external system's Organization or
 * Role, and a GeoNetwork {@link Group}.
 * <p>
 * Used to enforce a 1:1 relationship between the two, with the external system
 * organizations or roles as the source of truth for authentication and
 * authorization, while at the same time not interfering with regular GeoNetwork
 * internals, which require groups to be defined on its PostgreSQL database.
 */
public class GroupLink {

    private CanonicalGroup canonical;
    private Group geonetworkGroup;

    public CanonicalGroup getCanonical() {
        return canonical;
    }

    public GroupLink setCanonical(CanonicalGroup canonical) {
        this.canonical = canonical;
        return this;
    }

    public Group getGeonetworkGroup() {
        return geonetworkGroup;
    }

    public GroupLink setGeonetworkGroup(Group geonetworkGroup) {
        this.geonetworkGroup = geonetworkGroup;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(canonical, geonetworkGroup);
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
        return Objects.equals(canonical, other.canonical) && Objects.equals(geonetworkGroup, other.geonetworkGroup);
    }

    public boolean isUpToDateWith(CanonicalGroup canonical) {
        final boolean groupExists = getGeonetworkGroup() != null;
        return groupExists && Objects.equals(canonical.getLastUpdated(), getCanonical().getLastUpdated());
    }

}
