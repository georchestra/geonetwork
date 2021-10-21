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

import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Delegates to the appropriate {@link GroupSynchronizer} implementation
 * depending on the {@link ExternalizedSecurityProperties#getSyncMode()
 * georchestra.syncMode} configuration.
 */
class GroupSynchronizerProxy implements GroupSynchronizer {

    private @Autowired ExternalizedSecurityProperties props;

    private @Autowired OrgsBasedGroupSynchronizer orgsSynchronizer;
    private @Autowired RolesBasedGroupSynchronizer rolesSynchronizer;

    private GroupSynchronizer resolve() {
        final GroupSyncMode syncMode = props.getSyncMode();
        switch (syncMode) {
        case orgs:
            return orgsSynchronizer;
        case roles:
            return rolesSynchronizer;
        default:
            throw new IllegalStateException("Invalid sync mode: " + syncMode);
        }
    }

    public @Override List<CanonicalGroup> fetchCanonicalGroups() {
        return resolve().fetchCanonicalGroups();
    }

    public @Override List<GroupLink> getSynchronizedGroups() {
        return resolve().getSynchronizedGroups();
    }

    public @Override Optional<GroupLink> findGroupLink(String canonicalGroupId) {
        return resolve().findGroupLink(canonicalGroupId);
    }

    public @Override void synchronizeAll() {
        resolve().synchronizeAll();
    }

    public @Override void synchronizeAll(List<CanonicalGroup> canonical) {
        resolve().synchronizeAll(canonical);
    }

    public @Override GroupLink synchronize(CanonicalGroup canonical) {
        return resolve().synchronize(canonical);
    }

    public @Override Privileges resolvePrivilegesFor(CanonicalUser user) {
        return resolve().resolvePrivilegesFor(user);
    }
}
