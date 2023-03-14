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

import javax.annotation.Generated;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.fao.geonet.domain.Group;

/**
 * Canonical representation of a group as provided by the external accounts
 * management system, that is mapped to an internal geonetwork {@link Group}.
 * <p>
 * Being an externally defined entity, instanceof of this class are immutable.
 * Use the {@link #builder() builder} to create new instances.
 */
public class CanonicalGroupImpl implements CanonicalGroup {
    private String name;
    private String id;
    private String lastUpdated;
    private String description;
    private String linkage;
    private String orgTitle;

    private GroupSyncMode origin;

    @Generated("SparkTools")
    private CanonicalGroupImpl(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.linkage = builder.linkage;
        this.lastUpdated = builder.lastUpdated;
        this.origin = builder.origin;
        this.orgTitle = builder.orgTitle;
    }

    public @Override String getId() {
        return id;
    }

    public @Override String getName() {
        return this.name;
    }

    public @Override String getOrgTitle() {
        return this.orgTitle;
    }

    public @Override GroupSyncMode getOrigin() {
        return origin;
    }

    public @Override String getDescription() {
        return description;
    }

    public @Override String getLinkage() {
        return linkage;
    }

    public @Override String getLastUpdated() {
        return lastUpdated;
    }

    public @Override int hashCode() {
        return Objects.hash(description, id, lastUpdated, linkage, name, origin);
    }

    public @Override boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CanonicalGroupImpl other = (CanonicalGroupImpl) obj;
        return Objects.equals(description, other.description) && Objects.equals(id, other.id)
                && Objects.equals(lastUpdated, other.lastUpdated) && Objects.equals(linkage, other.linkage)
                && Objects.equals(name, other.name) && origin == other.origin && Objects.equals(orgTitle, other.orgTitle);
    }

    public @Override String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    /**
     * Creates builder to build {@link CanonicalGroup}.
     *
     * @return created builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder to build {@link CanonicalGroup}.
     */
    public static final class Builder {
        private String id;
        private String name;
        private String description;
        private String linkage;
        private String lastUpdated;
        private String orgTitle;
        private GroupSyncMode origin;

        private Builder() {
        }

        public Builder init(CanonicalGroup group) {
            this.id = group.getId();
            this.name = group.getName();
            this.orgTitle = group.getOrgTitle();
            this.description = group.getDescription();
            this.linkage = group.getLinkage();
            this.lastUpdated = group.getLastUpdated();
            this.origin = group.getOrigin();
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withOrgTitle(String orgTitle) {
            this.orgTitle = orgTitle;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withLinkage(String linkage) {
            this.linkage = linkage;
            return this;
        }

        public Builder withLastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Builder withOrigin(GroupSyncMode origin) {
            this.origin = origin;
            return this;
        }

        public CanonicalGroup build() {
            return new CanonicalGroupImpl(this);
        }
    }

}
