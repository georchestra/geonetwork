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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.georchestra.config.security.GeorchestraUserDetails;
import org.georchestra.geonetwork.logging.Logging;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import jeeves.component.ProfileManager;

@Service
public class UserMapperService {
    private static final Logging log = Logging.getLogger("org.georchestra.geonetwork.security.integration");

    private static final String DEFAULT_ROLE_MAPPING_PROP = "geonetwork.profiles.default";
    private static final String DEFAULT_ROLE_MAPPING_PROP_EXPRESSION = "${geonetwork.profiles.default:RegisteredUser}";

    private static final String ROLES_MAPPING_CONFIG_PROP = "geonetwork.profiles.rolemappings";
    private static final String ROLES_MAPPING_CONFIG_PROP_EXPRESSION = "${geonetwork.profiles.rolemappings}";

    @Value(ROLES_MAPPING_CONFIG_PROP_EXPRESSION)
    private Map<String, Profile> roleToProfileMapping = Collections.emptyMap();

    @Value(DEFAULT_ROLE_MAPPING_PROP_EXPRESSION)
    private Profile defaultProfile;

    @PostConstruct
    void postConstrucCheck() {
        if (roleToProfileMapping == null)
            throw new BeanInitializationException("roleToProfileMapping is null");
        if (roleToProfileMapping.isEmpty())
            throw new BeanInitializationException(
                    "No Georchestra roles to GeoNetwork Profile mappings provided through config property map "
                            + ROLES_MAPPING_CONFIG_PROP + ".*");
        if (null == defaultProfile) {
            throw new BeanInitializationException(
                    "default user profile not set through config property " + DEFAULT_ROLE_MAPPING_PROP);
        }

        log.info("Initalized geOrchestra roles to GeoNetwork Profile mappings. Default: {}, mappings: {}",
                defaultProfile, logStrMappings());
    }

    public User toGeonetorkUser(GeorchestraUserDetails canonical) {
        User user = new User();
        updateGeonetworkUser(canonical, user);
        return user;
    }

    /**
     * Updates {@code user} properties with {@code canonical} properties
     * 
     * @param canonical the canonical user representation
     * @param user      the internal user to update properties on from
     *                  {@code canonical}
     * @return a map of changed property names to old/new value pairs
     */
    public Map<String, Pair<?, ?>> updateGeonetworkUser(@NonNull GeorchestraUserDetails canonical, User user) {
        // ? user.setEnabled(true);
        // ? user.setKind("")

        Map<String, Pair<?, ?>> changes = new HashMap<>();
        update(changes, "userName", canonical.getUsername(), user.getUsername(), user::setUsername);
        update(changes, "name", canonical.getFirstName(), user.getName(), user::setName);
        update(changes, "surName", canonical.getLastName(), user.getSurname(), user::setSurname);
        update(changes, "emailAddresses", extractEmails(canonical), user.getEmailAddresses(), user::setEmailAddresses);
        update(changes, "kind", canonical.getTitle(), user.getKind(), user::setKind);

        String organisation = canonical.getOrganization().getId();
        update(changes, "organisation", organisation, user.getOrganisation(), user::setOrganisation);

        Profile profile = resolveUserProfile(canonical.getRoles());
        update(changes, "profile", profile, user.getProfile(), user::setProfile);

        return changes;
    }

    private <V> void update(Map<String, Pair<?, ?>> changes, String propertyName, V newValue, V oldValue,
            Consumer<V> updater) {
        if (!Objects.equals(oldValue, newValue)) {
            updater.accept(newValue);
            changes.put(propertyName, Pair.of(oldValue, newValue));
        }
    }

    private @NonNull Profile resolveUserProfile(@NonNull List<String> roles) {
        Profile[] matches = rolesToProfile(roles);
        Profile highestUserProfile = ProfileManager.getHighestProfile(matches);
        if (highestUserProfile == null) {
            log.warn("Unable to determine user Profile from roles {}. Available mappings: {}", roles, logStrMappings());
            return defaultProfile;
        }
        return highestUserProfile;
    }

    private Profile[] rolesToProfile(@NonNull List<String> roles) {
        return roles.stream()//
                .map(this.roleToProfileMapping::get)//
                .filter(Objects::nonNull)//
                .toArray(Profile[]::new);
    }

    private Set<String> extractEmails(GeorchestraUserDetails canonical) {
        Set<String> emails = new HashSet<>();
        if (null != canonical.getEmail()) {
            emails.add(canonical.getEmail());
        }
        return emails;
    }

    private String logStrMappings() {
        return roleToProfileMapping.entrySet().stream().//
                map(e -> String.format("%s -> %s", e.getKey(), e.getValue()))//
                .collect(Collectors.joining(","));
    }

}
