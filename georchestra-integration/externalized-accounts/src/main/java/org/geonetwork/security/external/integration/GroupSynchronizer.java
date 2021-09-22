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

import java.util.List;
import java.util.Optional;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.springframework.lang.NonNull;

/**
 * Strategy object to synchronize internal geonetwork {@link Group groups} with
 * an external system definition of groups.
 * 
 * @see OrgsBasedGroupSynchronizer
 * @see RolesBasedGroupSynchronizer
 */
interface GroupSynchronizer {

    /**
     * Reads the canonical groups from the external source of truth
     */
    List<CanonicalGroup> findCanonicalGroups();

    List<GroupLink> getSynchronizedGroups();

    Optional<GroupLink> findGroupLink(@NonNull String canonicalGroupId);

    void synchronizeAll();

    /**
     * Creates or updates a geonetwork {@link Group} for each {@code canonical}
     * group, and deletes geonetwork groups that are not in the {@code canonical}
     * list.
     */
    void synchronizeAll(List<CanonicalGroup> canonical);

    /**
     * Creates or updates a geonetwork {@link Group} to match the {@code canonical}
     * group
     * 
     * @return the up to date link between the external canonical group and the
     *         internal geonetwork group
     */
    GroupLink synchronize(CanonicalGroup canonical);

    /**
     * Resolves the internal {@link Group}/{@link Profile} pairs that compose the
     * set of privileges for the given canonical user, creating any necessary
     * {@link Group} and and external {@link GroupLink link} in the process.
     */
    Privileges resolvePrivilegesFor(CanonicalUser user);
}
