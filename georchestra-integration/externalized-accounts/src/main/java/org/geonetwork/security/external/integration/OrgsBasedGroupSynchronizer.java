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

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;
import org.geonetwork.security.external.configuration.ProfileMappingProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

class OrgsBasedGroupSynchronizer extends AbstractGroupSynchronizer {

    public OrgsBasedGroupSynchronizer(CanonicalAccountsRepository canonicalAccounts) {
        super(canonicalAccounts);
    }

    protected @Override GroupSyncMode getOrigin() {
        return GroupSyncMode.orgs;
    }

    public @Override List<CanonicalGroup> fetchCanonicalGroups() {
        return canonicalAccounts.findAllOrganizations();
    }

    protected @Override List<CanonicalGroup> resolveGroupsOf(CanonicalUser user) {
        final String orgName = user.getOrganization();
        if(!StringUtils.hasLength(orgName)) {
            return Collections.emptyList();
        }
        CanonicalGroup canonicalOrganization = this.externalGroupLinks.findByName(orgName)//
                .map(GroupLink::getCanonical)//
                // not found in db, defer to canonical source
                .orElseGet(() -> //
                canonicalAccounts.findOrganizationByName(orgName)//
                        .orElseThrow(notFound(orgName)));

        return Collections.singletonList(canonicalOrganization);
    }

    private Supplier<? extends IllegalArgumentException> notFound(final String orgName) {
        return () -> new IllegalArgumentException(
                "Organization with name '" + orgName + "' not found in internal nor external repository");
    }

    protected @Override Privilege resolvePrivilegeFor(CanonicalUser user, Group groupFromOrganization) {
        Profile profile = resolveUserProfile(user.getRoles());
        return new Privilege(groupFromOrganization, profile);
    }

    private Profile resolveUserProfile(@NonNull List<String> roles) {
        ProfileMappingProperties profileMappings = configProperties.getProfiles();
        return profileMappings.resolveHighestProfileFromRoleNames(roles);
    }
    
}
