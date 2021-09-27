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
package org.georchestra.geonetwork.security.integration;

import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.georchestra.security.model.GeorchestraUser;
import org.georchestra.security.model.Organization;
import org.georchestra.security.model.Role;

/**
 * Maps georchestra security api model objects to
 * {@code gn-externalized-accounts} canonical accounts object model
 *
 */
public class CanonicalModelMapper {

    public CanonicalUser toCanonical(GeorchestraUser user) {
        return CanonicalUser.builder()//
                .withId(user.getId())//
                .withUsername(user.getUsername())//
                .withFirstName(user.getFirstName())//
                .withLastName(user.getLastName())//
                .withOrganization(user.getOrganization())//
                .withRoles(user.getRoles())//
                .withLastUpdated(user.getLastUpdated())//
                .withEmail(user.getEmail())//
                .withTitle(user.getTitle())//
                .build();
    }

    public CanonicalGroup toCanonical(Organization org) {
        return CanonicalGroup.builder()//
                .withId(org.getId())//
                .withName(org.getShortName())//
                .withDescription(org.getDescription())//
                .withLastUpdated(org.getLastUpdated())//
                .withLinkage(org.getLinkage())//
                .withOrigin(GroupSyncMode.orgs)//
                .build();
    }

    public CanonicalGroup toCanonical(Role role) {
        return CanonicalGroup.builder()//
                .withId(role.getId())//
                .withName(role.getName())//
                .withDescription(role.getDescription())//
                .withLastUpdated(role.getLastUpdated())//
                .withOrigin(GroupSyncMode.roles)//
                .build();
    }

}
