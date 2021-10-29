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

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;

class Privilege {
    private Group group;
    private Profile profile;

    public Privilege(Group group, Profile profile) {
        this.group = group;
        this.profile = profile;
    }

    public Group getGroup() {
        return group;
    }

    public Profile getProfile() {
        return profile;
    }

    public @Override String toString() {
        return String.format("%s[%s]", group.getName(), profile);
    }
}
