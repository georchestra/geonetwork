/*
 * Copyright (C) 2009-2025 by the geOrchestra PSC
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
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.fao.geonet.domain.Group;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.Objects.*;

@Deprecated
public class RolesBasedGroupSynchronizer extends AbstractGroupSynchronizer {

    public static final Logger log = LoggerFactory.getLogger(RolesBasedGroupSynchronizer.class.getPackage().getName());

    static final Set<String> georchestraDefaultRoleNames = Set.of("SUPERUSER","ORGADMIN","MAPSTORE_ADMIN","REFERENT","EMAILPROXY",
        "ADMINISTRATOR", "IMPORT", "USER", "GN_EDITOR", "GN_REVIEWER", "GN_ADMIN");

    @Autowired
    public ExternalizedSecurityProperties config;

    public RolesBasedGroupSynchronizer(CanonicalAccountsRepository canonicalAccounts) {
        super(canonicalAccounts);
    }

    protected @Override GroupSyncMode getOrigin() {
        return GroupSyncMode.roles;
    }

    /**
     * Gets all the canonical groups representing Roles from
     * {@link CanonicalAccountsRepository#findAllRoles()} and filters them out using
     * the {@link Pattern} provided in the
     * {@link ExternalizedSecurityProperties#getSyncRolesFilter() configuration}.
     * <p>
     * If the pattern contains groups, the returned {@link CanonicalGroup}s are
     * renamed using the concatenation of groups matching each role name. For
     * example, for a pattern {@literal EL_(.*)}, and roles
     * {@literal ADMIN, EL_User, EL_Editor}, the returned {@link CanonicalGroup}s
     * will be {@literal User} and {@literal Editor}.
     */
    public @Override List<CanonicalGroup> fetchCanonicalGroups() {
        List<CanonicalGroup> roles = canonicalAccounts.findAllRoles();
        Stream<CanonicalGroup> matches = roles.stream().filter(this::doesNotMatchesGeorchestraDefaultRoleNameFilter).filter(this::matchesRoleNameFilter);
        return matches.map(this::renameRoleUsingConfigPattern).collect(Collectors.toList());
    }

    private CanonicalGroup renameRoleUsingConfigPattern(CanonicalGroup role) {
        Pattern pattern = config.getSyncRolesFilter();
        String name = role.getName();
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("role name filtering shall be performed prior to calling this method");
        }
        int groupCount = matcher.groupCount();
        if (groupCount == 0) {
            return role;
        }
        String filteredName = IntStream.rangeClosed(1, groupCount).mapToObj(matcher::group)
                .collect(Collectors.joining());
        return CanonicalGroup.builder().init(role).withName(filteredName).build();
    }

    protected @Override List<CanonicalGroup> resolveGroupsOf(CanonicalUser user) {
        Stream<String> roleNames = user.getRoles().stream().filter(this::doesNotMatchesGeorchestraDefaultRoleNameFilter).filter(config::matchesRoleNameFilter);

        Stream<CanonicalGroup> roleGroups = roleNames.map(role -> this.externalGroupLinks.findByName(role)//
                .map(GroupLink::getCanonical)//
                .orElseGet(() -> //
                canonicalAccounts.findRoleByName(role)//
                        .orElseThrow(notFound(role))));

        return roleGroups.collect(Collectors.toList());
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

    private boolean doesNotMatchesGeorchestraDefaultRoleNameFilter(String roleName) {
        requireNonNull(roleName);
        return !georchestraDefaultRoleNames.contains(roleName);
    }

    private boolean doesNotMatchesGeorchestraDefaultRoleNameFilter(CanonicalGroup role) {
        requireNonNull(role);
        return doesNotMatchesGeorchestraDefaultRoleNameFilter(role.getName());
    }

    @Override
    protected GroupLink getSyncElements(Group group, CanonicalGroup canonical, GroupLink link) {
        group.setName(canonical.getName());
        group.setDescription(canonical.getDescription());
        link.setCanonical(canonical);
        return link;
    }
}
