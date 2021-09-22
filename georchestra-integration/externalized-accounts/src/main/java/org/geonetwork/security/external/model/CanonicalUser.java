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

/**
 * Canonical, externally defined representation of a system's user.
 */
public interface CanonicalUser {

    String getId();

    String getUsername();

    String getFirstName();

    String getLastName();

    String getOrganization();

    String getLastUpdated();

    String getEmail();

    String getTitle();

    List<String> getRoles();

    static CanonicalUserImpl.Builder builder() {
        return CanonicalUserImpl.builder();
    }
}