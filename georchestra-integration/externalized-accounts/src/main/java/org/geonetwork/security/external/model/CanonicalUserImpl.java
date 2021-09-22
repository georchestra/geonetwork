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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.annotation.Generated;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CanonicalUserImpl implements Serializable, CanonicalUser {
    private static final long serialVersionUID = -1;

    /////// Default mandatory properties. /////
    private String username;
    private String id;
    private String organization;
    private List<String> roles = new ArrayList<>();
    /**
     * String that somehow represents the current version, may be a timestamp, a
     * hash, etc. Provided by request header {@code sec-lastupdated}
     */
    private String lastUpdated;


    /////// Default optional properties. /////
    /////// Some may be made mandatory on a per-application basis /////

    private String firstName;
    private String lastName;
    private String email;
    private String title;

    @Generated("SparkTools")
    private CanonicalUserImpl(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.organization = builder.organization;
        this.roles = builder.roles;
        this.lastUpdated = builder.lastUpdated;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.title = builder.title;
    }

    public @Override String getId() {
        return id;
    }

    public @Override String getUsername() {
        return username;
    }

    public @Override String getOrganization() {
        return organization;
    }

    public @Override String getLastUpdated() {
        return lastUpdated;
    }

    public @Override String getFirstName() {
        return firstName;
    }

    public @Override String getLastName() {
        return lastName;
    }

    public @Override String getEmail() {
        return email;
    }

    public @Override String getTitle() {
        return title;
    }

    public @Override List<String> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    public @Override int hashCode() {
        return Objects.hash(email, firstName, id, lastName, lastUpdated, organization, roles, title, username);
    }

    public @Override String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public @Override boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CanonicalUserImpl other = (CanonicalUserImpl) obj;
        return Objects.equals(email, other.email) && Objects.equals(firstName, other.firstName)
                && Objects.equals(id, other.id) && Objects.equals(lastName, other.lastName)
                && Objects.equals(lastUpdated, other.lastUpdated) && Objects.equals(organization, other.organization)
                && Objects.equals(roles, other.roles) && Objects.equals(title, other.title)
                && Objects.equals(username, other.username);
    }

    /**
     * Creates builder to build {@link CanonicalUser}.
     * 
     * @return created builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder to build {@link CanonicalUser}.
     */
    public static final class Builder {
        private String id;
        private String username;
        private String organization;
        private List<String> roles = Collections.emptyList();
        private String lastUpdated;
        private String firstName;
        private String lastName;
        private String email;
        private String title;

        private Builder() {
        }

        public Builder init(CanonicalUser from) {
            this.id = from.getId();
            this.username = from.getUsername();
            this.firstName = from.getFirstName();
            this.lastName = from.getLastName();
            this.lastUpdated = from.getLastUpdated();
            this.organization = from.getOrganization();
            this.roles = new ArrayList<>(from.getRoles());
            this.title = from.getTitle();
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder withOrganization(String organization) {
            this.organization = organization;
            return this;
        }

        public Builder withRoles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder withLastUpdated(String lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }

        public Builder withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder withLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public CanonicalUser build() {
            return new CanonicalUserImpl(this);
        }
    }

}
