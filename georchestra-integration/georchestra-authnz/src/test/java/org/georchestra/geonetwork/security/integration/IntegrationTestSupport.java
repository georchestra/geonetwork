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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import lombok.experimental.Delegate;
import org.fao.geonet.domain.Profile;
import org.fao.geonet.domain.User;
import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.configuration.ProfileMappingProperties;
import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.georchestra.commons.security.SecurityHeaders;
import org.georchestra.security.model.GeorchestraUser;
import org.georchestra.security.model.Organization;
import org.georchestra.security.model.Role;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;

public class IntegrationTestSupport {

    private ExternalizedSecurityProperties configProps;

    public IntegrationTestSupport(ExternalizedSecurityProperties configProps) {
        this.configProps = configProps;
    }

    public List<GeorchestraUser> loadExpectedGeorchestraUsers() {
        return loadJson("defaultUsers.json", GeorchestraUser.class);
    }

    public List<Role> loadExpectedGeorchestraRoles() {
        return loadJson("defaultRoles.json", Role.class);
    }

    public List<Organization> loadExpectedGeorchestraOrgs() {
        return loadJson("defaultOrganizations.json", Organization.class);
    }

    private <T> List<T> loadJson(String resource, Class<T> type) {
        final URL url = getClass().getResource(resource);
        assertNotNull(url);
        return loadJson(type, url);
    }

    private <T> List<T> loadJson(Class<T> type, final URL url) {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, type);
        try {
            return mapper.readValue(url, collectionType);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public void assertUser(GeorchestraUser real, CanonicalUser mapped) {
        assertEquals(real.getId(), mapped.getId());
        assertEquals(real.getUsername(), mapped.getUsername());
        assertEquals(real.getLastUpdated(), mapped.getLastUpdated());
        assertEquals(real.getFirstName(), mapped.getFirstName());
        assertEquals(real.getLastName(), mapped.getLastName());
        assertEquals(real.getOrganization(), mapped.getOrganization());
        assertEquals(real.getRoles(), mapped.getRoles());
        assertEquals(real.getEmail(), mapped.getEmail());
        assertEquals(real.getTitle(), mapped.getTitle());
    }

    public void assertUser(CanonicalUser expected, User user) {
        assertNotNull(user);
        assertEquals(expected.getUsername(), user.getUsername());
        assertEquals(expected.getOrganization(), user.getOrganisation());
        assertEquals(expected.getFirstName(), user.getName());
        assertEquals(expected.getLastName(), user.getSurname());
        assertEquals(expected.getEmail(), user.getEmail());
        String expectedTitle = expected.getTitle();
        if (null != expectedTitle && expectedTitle.length() > 16) {
            expectedTitle = expectedTitle.substring(0, 16);
        }
        assertEquals(expectedTitle, user.getKind());

        ProfileMappingProperties profileMappings = configProps.getProfiles();
        Profile expectedProfile = profileMappings.resolveHighestProfileFromRoleNames(expected.getRoles());
        assertEquals(expectedProfile, user.getProfile());
    }

    public void assertGroup(Organization real, CanonicalGroup mapped) {
        assertEquals(real.getId(), mapped.getId());
        assertEquals(real.getShortName(), mapped.getName());
        assertEquals(real.getDescription(), mapped.getDescription());
        assertEquals(real.getLastUpdated(), mapped.getLastUpdated());
        assertEquals(real.getLinkage(), mapped.getLinkage());
        assertEquals(GroupSyncMode.orgs, mapped.getOrigin());
    }

    public void assertRole(Role real, CanonicalGroup mapped) {
        assertEquals(real.getId(), mapped.getId());
        assertEquals(real.getName(), mapped.getName());
        assertEquals(real.getDescription(), mapped.getDescription());
        assertEquals(real.getLastUpdated(), mapped.getLastUpdated());
        assertEquals(GroupSyncMode.roles, mapped.getOrigin());
    }

    public String jsonEncode(GeorchestraUser preAuthUser) {
        ObjectMapper encoder = new ObjectMapper();
        encoder.configure(SerializationFeature.INDENT_OUTPUT, Boolean.FALSE);
        encoder.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, Boolean.FALSE);
        encoder.setSerializationInclusion(Include.NON_NULL);
        try {
            String json = encoder.writer().writeValueAsString(preAuthUser);
            return SecurityHeaders.encodeBase64(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static class CanonicalUserWrapper implements CanonicalUser {
        @Delegate(excludes = ExcludedListMethods.class)
        private CanonicalUser canonicalUser;

        public CanonicalUserWrapper(CanonicalUser canonicalUser) {
            this.canonicalUser = canonicalUser;
        }

        @Override
        public String getEmail() {
            return "updated";
        }

        @Override
        public String getLastUpdated() {
            return "new_date";
        }

        private abstract class ExcludedListMethods {
            public abstract String getEmail();

            public abstract String getLastUpdated();


        }
    }
}
