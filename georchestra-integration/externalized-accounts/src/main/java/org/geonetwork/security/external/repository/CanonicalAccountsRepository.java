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
package org.geonetwork.security.external.repository;

import java.util.List;
import java.util.Optional;

import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;

public interface CanonicalAccountsRepository {

    /**
     * Return all external authority users in their canonical form
     */
    List<CanonicalUser> findAllUsers();

    /**
     * Return all external authority groups that represent an Organization
     */
    List<CanonicalGroup> findAllOrganizations();

    /**
     * Return all external authority groups that represent an authorization ROLE
     */
    List<CanonicalGroup> findAllRoles();

    /**
     * Find a canonical user representing an externally defined "user" or "account"
     * by login name.
     * <p>
     * Note whereas an user's login name may change over time, it's required to be
     * unique.
     */
    Optional<CanonicalUser> findUserByUsername(String username);

    /**
     * Find a canonical group representing an externally defined "organization" by
     * name.
     * <p>
     * Note whereas an Organization name may change over time, it's required to be
     * unique.
     */
    Optional<CanonicalGroup> findOrganizationByName(String name);

    /**
     * Find a canonical group representing an externally defined "user role" by
     * name.
     * <p>
     * Note whereas an Role name may change over time, it's required to be unique.
     */
    Optional<CanonicalGroup> findRoleByName(String name);

    Optional<byte[]> getLogo(String id);
}
