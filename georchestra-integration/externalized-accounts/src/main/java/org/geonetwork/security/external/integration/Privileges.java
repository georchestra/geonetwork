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
package org.geonetwork.security.external.integration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.fao.geonet.domain.Profile;

class Privileges {
    private Profile userProfile;

    private List<Privilege> additionalProvileges = new ArrayList<>();

    public Privileges(Profile defaultProfile) {
        Objects.requireNonNull(defaultProfile);
        this.userProfile = defaultProfile;
    }

    public Profile getUserProfile() {
        return userProfile;
    }

    public List<Privilege> getAdditionalProvileges() {
        return additionalProvileges;
    }

    public @Override String toString() {
        return String.format("profile: %s, other: %s", userProfile,
                additionalProvileges.stream().map(Privilege::toString).collect(Collectors.joining(", ")));
    }
}
