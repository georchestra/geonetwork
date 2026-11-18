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

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.Profile;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupLink;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Synchronizes geOrchestra groups with external groups based on the user's organization and roles.
 * For each organization, a corresponding geOrchestra group is created (if it doesn't exist) and the user is added to it if they belong to that organization.
 * Additionally, if the user's roles contain an organization prefix (e.g., "ORG:ROLE"), the user is added to the group corresponding to that organization (e.g., "ORG").
 */
public class RolePerOrgBasedGroupSynchronizer extends AbstractGroupSynchronizer {

    public static final Logger log = LoggerFactory.getLogger(RolePerOrgBasedGroupSynchronizer.class.getPackage().getName());

    private static final String separator = ":";

    @Autowired
    public ExternalizedSecurityProperties config;

    public RolePerOrgBasedGroupSynchronizer(CanonicalAccountsRepository canonicalAccounts) {
        super(canonicalAccounts);
    }

    protected @Override GroupSyncMode getOrigin() {
        return GroupSyncMode.role_per_org;
    }

    public @Override List<CanonicalGroup> fetchCanonicalGroups() {
        return canonicalAccounts.findAllOrganizations();
    }

    protected @Override List<CanonicalGroup> resolveGroupsOf(CanonicalUser user) {
        final String orgName = user.getOrganization();
        Stream<String> groupsName = userRoles(user)
            .flatMap(r -> {
                if (r.contains(separator)) {
                    return Stream.of(r.split(separator)[0]);
                }
                return StringUtils.hasLength(orgName) ? Stream.of(orgName) : Stream.empty();
            })
            .distinct();
        Stream<CanonicalGroup> roleGroups = groupsName.map(role -> this.externalGroupLinks.findByName(role)//
            .map(GroupLink::getCanonical)
            .or(() -> canonicalAccounts.findOrganizationByName(role))
            .orElseThrow(notFound(role)));
        return roleGroups.collect(Collectors.toList());
    }

    @Override
    public Privileges resolvePrivilegesFor(CanonicalUser user) {
        final List<CanonicalGroup> canonicalGroups = resolveGroupsOf(user);

        Privileges userPrivileges = new Privileges(resolveDefaultProfile(user));
        Stream<Group> groups = canonicalGroups.stream().map(this::synchronize).map(GroupLink::getGeonetworkGroup);
        groups.map(g -> resolvePrivilegeFor(user, g))
            .forEach(userPrivileges.getAdditionalProvileges()::add);
        return userPrivileges;
    }

    @Override
    protected Profile resolveDefaultProfile(CanonicalUser user) {
        return configProperties.getProfiles().resolveHighestProfileFromRoleNames(getRootRolesForUser(user));
    }

    private Stream<String> userRoles(CanonicalUser user) {
        Pattern p = Pattern.compile(".+" + separator + ".+");
        return user.getRoles().stream()
            .filter(r -> p.matcher(r).matches() || config.getProfiles().getRolemappings().containsKey(r));
    }

    private Privilege resolvePrivilegeFor(CanonicalUser user, Group group) {
        String groupPrefix = group.getName() + separator;
        List<String> rolesForGroup = userRoles(user)
            .filter(r -> {
                if (r.contains(separator)) {
                    return r.startsWith(groupPrefix);
                } else {
                    return group.getName().equals(user.getOrganization());
                }
            }) //e.g filter roles for this group PSC:GN_REVIEWER and GN_EDITOR
            .map(this::getRootRole) // e.g get only the role part GN_REVIEWER
            .collect(Collectors.toList());
        Profile p = config.getProfiles().resolveHighestProfileFromRoleNames(rolesForGroup); // resolve highest profile for the roles filtered, here GN_REVIEWER
        return new Privilege(group, p);
    }

    private Supplier<? extends IllegalArgumentException> notFound(final String orgName) {
        return () -> new IllegalArgumentException(
            "Organization with name '" + orgName + "' not found in internal nor external repository");
    }

    @Override
    public List<String> getRootRolesForUser(CanonicalUser user) {
        // Used to resolve the user's global/default profile: returns relevant role names
        // with any organization prefix stripped (for example, PSC:GN_REVIEWER -> GN_REVIEWER).
        return userRoles(user).map(this::getRootRole).collect(Collectors.toList());
    }

    private String getRootRole(String role) {
        if (role.contains(separator)) {
            return role.split(separator)[1];
        }
        return role;
    }

}
