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

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.User;

/**
 * Canonical representation of a group as provided by the external accounts
 * management system, that is mapped to an internal geonetwork {@link Group}.
 * <p>
 * Being an externally defined entity, instances of of this class are immutable.
 * Use the {@link #builder() builder} to create new instances.
 */
public interface CanonicalGroup {

    /**
     * External authority unique identifier. Does NOT map to {@link User#getId()}.
     * Instead, the GeoNetwork user identifier and the canonical user identifier are
     * kept in sync by the system.
     */
    String getId();

    /**
     * External authority entity unique name, maps to {@link Group#getName()}
     */
    String getName();

    String getOrgTitle();

    /**
     * An indication of the external entity's version, used to ensure currentness of
     * the GeoNetwork {@link Group} definition. Whenever the internally kept version
     * does not match this property value, the GeoNetwork {@link Group} will be
     * updated accordingly.
     */
    String getLastUpdated();

    /**
     * An indication of whether the external authority refers to an "organization"
     * or a "role"
     */
    GroupSyncMode getOrigin();

    /**
     * Optional entity description, maps to {@link Group#getDescription()}
     */
    String getDescription();

    /**
     * Optional entity URL, maps to {@link Group#getWebsite()}
     */
    String getLinkage();

    static CanonicalGroupImpl.Builder builder() {
        return CanonicalGroupImpl.builder();
    }
}
