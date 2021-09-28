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
package org.geonetwork.security.external.configuration;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import org.geonetwork.security.external.model.GroupSyncMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExternalizedSecurityProperties implements Serializable {
    private static final long serialVersionUID = 1L;

    static final Logger log = LoggerFactory.getLogger(ExternalizedSecurityProperties.class.getPackage().getName());

    private GroupSyncMode syncMode = GroupSyncMode.orgs;

    private Pattern syncRolesFilter = Pattern.compile(".*");

    private ProfileMappingProperties profiles = new ProfileMappingProperties();

    private ScheduledSynchronizationProperties scheduled = new ScheduledSynchronizationProperties();

    public GroupSyncMode getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(GroupSyncMode syncMode) {
        Objects.requireNonNull(syncMode, "syncMode config property can't be null");
        this.syncMode = syncMode;
    }

    public void setSyncRolesFilter(Pattern syncRolesFilter) {
        Objects.requireNonNull(syncRolesFilter, "syncRolesFilter config property can't be null");
        this.syncRolesFilter = syncRolesFilter;
    }

    public Pattern getSyncRolesFilter() {
        return this.syncRolesFilter;
    }

    public ProfileMappingProperties getProfiles() {
        return profiles;
    }

    public void setProfiles(ProfileMappingProperties profiles) {
        this.profiles = profiles;
    }

    public ScheduledSynchronizationProperties getScheduled() {
        return scheduled;
    }

    public void setScheduled(ScheduledSynchronizationProperties scheduled) {
        this.scheduled = scheduled;
    }

    @Override
    public int hashCode() {
        return Objects.hash(profiles, scheduled, syncMode, syncRolesFilter);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ExternalizedSecurityProperties other = (ExternalizedSecurityProperties) obj;
        return Objects.equals(profiles, other.profiles) && Objects.equals(scheduled, other.scheduled)
                && syncMode == other.syncMode && Objects.equals(syncRolesFilter, other.syncRolesFilter);
    }

    public boolean matchesRoleNameFilter(String roleName) {
        Pattern pattern = getSyncRolesFilter();
        boolean matches = pattern.matcher(roleName).matches();
        if (!matches) {
            log.debug("Role {} does not match geonetwork.syncRolesFilter regex {}", roleName, pattern);
        }
        return matches;
    }

}
