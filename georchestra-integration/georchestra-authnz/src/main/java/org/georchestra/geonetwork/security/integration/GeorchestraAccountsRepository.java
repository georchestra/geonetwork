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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.georchestra.security.api.OrganizationsApi;
import org.georchestra.security.api.RolesApi;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.model.GeorchestraUser;
import org.georchestra.security.model.Organization;
import org.georchestra.security.model.Role;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Implementation of {@link CanonicalAccountsRepository} to fetch geOrchestra
 * security model's {@link GeorchestraUser users}, {@link Organization
 * organizations}, and {@link Role roles}.
 * <p>
 * The actual source of truth depends on the implementations of
 * {@link UsersApi}, {@link OrganizationsApi}, and {@link RolesApi} available on
 * the spring context. For example, using geOrchestra's "console" application's
 * REST API.
 */
public class GeorchestraAccountsRepository implements CanonicalAccountsRepository {

    private @Autowired UsersApi georchestraUsers;
    private @Autowired OrganizationsApi georchestraOrgs;
    private @Autowired RolesApi georchestraRoles;
    private @Autowired CanonicalModelMapper mapper;

    public @Override List<CanonicalUser> findAllUsers() {
        return georchestraUsers.findAll().stream().map(mapper::toCanonical).collect(Collectors.toList());
    }

    public @Override Optional<CanonicalUser> findUserByUsername(String username) {
        return georchestraUsers.findByUsername(username).map(mapper::toCanonical);
    }

    public @Override Optional<CanonicalGroup> findOrganizationByName(String name) {
        return georchestraOrgs.findByShortName(name).map(mapper::toCanonical);
    }

    public @Override List<CanonicalGroup> findAllOrganizations() {
        return georchestraOrgs.findAll().stream().map(mapper::toCanonical).collect(Collectors.toList());
    }

    public @Override Optional<CanonicalGroup> findRoleByName(String name) {
        return georchestraRoles.findByName(name).map(mapper::toCanonical);
    }

    public @Override List<CanonicalGroup> findAllRoles() {
        return georchestraRoles.findAll().stream().map(mapper::toCanonical).collect(Collectors.toList());
    }

}
