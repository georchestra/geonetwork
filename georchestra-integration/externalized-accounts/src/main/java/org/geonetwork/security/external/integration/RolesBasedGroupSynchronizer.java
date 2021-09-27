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

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.configuration.ProfileMappingProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class RolesBasedGroupSynchronizer extends AbstractGroupSynchronizer {

    public static final Logger log = LoggerFactory.getLogger(RolesBasedGroupSynchronizer.class.getPackage().getName());

    @Autowired
    public ExternalizedSecurityProperties config;

    public RolesBasedGroupSynchronizer(CanonicalAccountsRepository canonicalAccounts) {
        super(canonicalAccounts);
    }

    protected @Override GroupSyncMode getOrigin() {
        return GroupSyncMode.roles;
    }

    public @Override List<CanonicalGroup> fetchCanonicalGroups() {
        List<CanonicalGroup> roles = canonicalAccounts.findAllRoles();
        return roles.stream().filter(this::matchesRoleNameFilter).collect(Collectors.toList());
    }

    protected @Override List<CanonicalGroup> resolveGroupsOf(CanonicalUser user) {
        Stream<String> roleNames = user.getRoles().stream().filter(config::matchesRoleNameFilter);

        Stream<CanonicalGroup> roleGroups = roleNames.map(role -> this.externalGroupLinks.findByName(role)//
                .map(GroupLink::getCanonical)//
                .orElseGet(() -> //
                canonicalAccounts.findRoleByName(role)//
                        .orElseThrow(notFound(role))));

        return roleGroups.collect(Collectors.toList());
    }

    protected @Override Privilege resolvePrivilegeFor(CanonicalUser user, Group groupFromRole) {
        final String roleName = groupFromRole.getName();

        ProfileMappingProperties profileMappings = configProperties.getProfiles();
        Profile profile = profileMappings.resolveHighestProfileFromRoleNames(Collections.singletonList(roleName));

        return new Privilege(groupFromRole, profile);
    }

    private Supplier<? extends IllegalArgumentException> notFound(String role) {
        return () -> new IllegalArgumentException("Role " + role + " not found in internal or external repository");
    }

    private boolean matchesRoleNameFilter(CanonicalGroup role) {
        requireNonNull(role);
        requireNonNull(role.getName());
        String name = role.getName();
        return config.matchesRoleNameFilter(name);
    }

}
