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

import java.util.List;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.User;

/**
 * Canonical, externally defined representation of a system's user.
 * <p>
 * A GeoNetwork {@link User} will be enforced for each canonical user and kept
 * synchronized.
 * <p>
 * Being an externally defined entity, instances of of this class are immutable.
 * Use the {@link #builder() builder} to create new instances.
 */
public interface CanonicalUser {

    /**
     * External authority unique identifier. Does NOT map to {@link User#getId()}.
     * Instead, the GeoNetwork user identifier and the canonical user identifier are
     * kept in sync by the system.
     */
    String getId();

    /**
     * External authority entity unique login name, maps to
     * {@link User#getUsername()}
     */
    String getUsername();

    /**
     * User's first name, maps to {@link User#getName()}
     */
    String getFirstName();

    /**
     * User's last name, maps to {@link User#getSurname()}
     */
    String getLastName();

    /**
     * Short name of the organizational unit this user belongs to; maps to
     * {@link User#getOrganisation()}, and with the user's {@link Group#getName()
     * Group name} when the {@link GroupSyncMode#orgs "orgs"} group synchronization
     * mode is used.
     */
    String getOrganization();

    /**
     * An indication of the external entity's version, used to ensure currentness of
     * the GeoNetwork {@link User} definition. Whenever the internally kept version
     * does not match this property value, the GeoNetwork {@link User} will be
     * updated accordingly.
     */
    String getLastUpdated();

    /**
     * User's primary email address, maps to {@link User#getPrimaryAddress()}
     */
    String getEmail();

    /**
     * User's title, maps to {@link User#getKind()}
     */
    String getTitle();

    /**
     * List of authorization role names the external authority assigns to this user.
     */
    List<String> getRoles();

    static CanonicalUserImpl.Builder builder() {
        return CanonicalUserImpl.builder();
    }
}