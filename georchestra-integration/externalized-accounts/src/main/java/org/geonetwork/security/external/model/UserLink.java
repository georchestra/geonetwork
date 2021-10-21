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

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import org.fao.geonet.domain.User;

/**
 * Entity that represents a link between an exteral system's
 * {@link CanonicalUser user} and a GeoNetwork {@link User user}.
 * <p>
 * Used to enforce a 1:1 relationship between the two, with the external-system
 * users being the source of truth for {@link User} state, while at the same
 * time not interfering with regular GeoNetwork internals, which require users
 * to be defined on its PostgreSQL database.
 */
public class UserLink {
    private String canonicalUserId;
    private User internalUser;
    private String internalUserLastUpdated;

    public String getCanonicalUserId() {
        return canonicalUserId;
    }

    public UserLink setCanonicalUserId(String canonicalUserId) {
        this.canonicalUserId = canonicalUserId;
        return this;
    }

    public User getInternalUser() {
        return internalUser;
    }

    public UserLink setInternalUser(User geonetworkUser) {
        this.internalUser = geonetworkUser;
        return this;
    }

    public String getLastUpdated() {
        return internalUserLastUpdated;
    }

    public UserLink setLastUpdated(String lastUpdated) {
        this.internalUserLastUpdated = lastUpdated;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(internalUser, canonicalUserId, internalUserLastUpdated);
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
        return Objects.equals(internalUser, other.internalUser)
                && Objects.equals(canonicalUserId, other.canonicalUserId)
                && Objects.equals(internalUserLastUpdated, other.internalUserLastUpdated);
    }

    /**
     * Evaluates whether the GeoNetwork {@link User user} information is current
     * with the canonical geOrchestra user.
     * 
     * @return {@code true} if the users match according to the criteria to keep
     *         them in synch, {@code false} otherwise, meaning the GeoNetwork user
     *         properties must be updated in the database to match the geOrchestra
     *         user.
     */
    public boolean isUpToDateWith(final CanonicalUser canonical) {
        requireNonNull(canonical);
        final String expected = canonical.getLastUpdated();
        final String actual = getLastUpdated();
        final boolean userExists = getInternalUser() != null;
        return Objects.equals(expected, actual) && userExists;
    }
}
