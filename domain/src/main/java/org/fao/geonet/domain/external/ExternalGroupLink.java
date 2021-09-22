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

import org.fao.geonet.domain.Group;

/**
 * Entity that represents a link between an external system's Group definition
 * (given by its {@link #georchestraOrgId id}), and a GeoNetwork {@link Group}.
 * <p>
 * Used to enforce a 1:1 relationship between the two, with the external system
 * group being the source of truth for authentication and authorization, while
 * at the same time not interfering with regular GeoNetwork internals, which
 * require groups to be defined on its PostgreSQL database.
 */
@Entity
@Access(AccessType.PROPERTY)
@Table(name = "group_external_link")
public class ExternalGroupLink {

    /**
     * Determines whether the GeoNetwork {@link Group group} was synchronized from
     * the external system's representation of an Organization or an authentication
     * role.
     */
    public enum GroupSyncMode {
        org, role;
    }

    private String externalId;
    private GroupSyncMode origin;
    private String name;
    private String description;
    private String linkage;
    private String lastUpdated;

    private Group geonetworkGroup;

    @Id
    public String getExternalId() {
        return externalId;
    }

    public ExternalGroupLink setExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public GroupSyncMode getOrigin() {
        return origin;
    }

    public ExternalGroupLink setOrigin(GroupSyncMode origin) {
        this.origin = origin;
        return this;
    }

    @OneToOne(optional = true)
    public Group getGeonetworkGroup() {
        return geonetworkGroup;
    }

    public ExternalGroupLink setGeonetworkGroup(Group geonetworkGroup) {
        this.geonetworkGroup = geonetworkGroup;
        return this;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public ExternalGroupLink setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
        return this;
    }

    public String getName() {
        return name;
    }

    public ExternalGroupLink setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ExternalGroupLink setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getLinkage() {
        return linkage;
    }

    public ExternalGroupLink setLinkage(String linkage) {
        this.linkage = linkage;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, geonetworkGroup, externalId, lastUpdated, linkage, name, origin);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExternalGroupLink other = (ExternalGroupLink) obj;
        return Objects.equals(description, other.description) && Objects.equals(geonetworkGroup, other.geonetworkGroup)
                && Objects.equals(externalId, other.externalId) && Objects.equals(lastUpdated, other.lastUpdated)
                && Objects.equals(linkage, other.linkage) && Objects.equals(name, other.name) && origin == other.origin;
    }

}
