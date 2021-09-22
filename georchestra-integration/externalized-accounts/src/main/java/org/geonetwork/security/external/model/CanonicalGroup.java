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

/**
 * Canonical representation of a group as provided by the external accounts
 * management system, that is mapped to an internal geonetwork {@link Group}.
 * <p>
 * Being an externally defined entity, instanceof of this class are immutable.
 * Use the {@link #builder() builder} to create new instances.
 */
public interface CanonicalGroup {

    String getId();

    String getName();

    GroupSyncMode getOrigin();

    String getDescription();

    String getLinkage();

    String getLastUpdated();

    static CanonicalGroupImpl.Builder builder() {
        return CanonicalGroupImpl.builder();
    }
}