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
package org.georchestra.geonetwork.security.integration.keycloak;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * Tests {@link KeycloakAccountsRepository} against a fake Keycloak admin API.
 */
public class KeycloakAccountsRepositoryTest {

    private static final String ADMIN = "/admin/realms/georchestra";

    /** group name = organization name, code in the "cn" attribute */
    private static final String PSC = "{\"id\":\"g-psc\",\"name\":\"Project Steering Committee\","
            + "\"path\":\"/Project Steering Committee\","
            + "\"attributes\":{\"cn\":[\"PSC\"],\"mail\":[\"psc@example.org\"]}}";
    private static final String C2C = "{\"id\":\"g-c2c\",\"name\":\"C2C\",\"path\":\"/C2C\",\"attributes\":{}}";

    private static final String TESTADMIN = "{\"id\":\"u-admin\",\"username\":\"testadmin\",\"firstName\":\"Test\","
            + "\"lastName\":\"ADMIN\",\"email\":\"admin@example.org\",\"attributes\":{\"title\":[\"Boss\"]}}";
    private static final String TESTUSER = "{\"id\":\"u-user\",\"username\":\"testuser\",\"firstName\":\"Test\","
            + "\"lastName\":\"USER\"}";
    private static final String SERVICE_ACCOUNT = "{\"id\":\"u-sa\",\"username\":\"service-account-geonetwork\","
            + "\"serviceAccountClientId\":\"geonetwork\"}";

    private static final String ROLES = "[{\"id\":\"r-admin\",\"name\":\"GN_ADMIN\",\"description\":\"GN admin\"},"
            + "{\"id\":\"r-user\",\"name\":\"USER\"},{\"id\":\"r-off\",\"name\":\"offline_access\"},"
            + "{\"id\":\"r-def\",\"name\":\"default-roles-georchestra\"}]";

    private HttpServer server;
    /** path (without query) -> JSON response body */
    private final Map<String, String> responses = new HashMap<>();
    private final List<String> requests = new ArrayList<>();
    private final AtomicInteger tokenRequests = new AtomicInteger();

    private KeycloakAccountsRepository repository;

    @Before
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", this::handle);
        server.start();

        responses.put(ADMIN + "/groups", "[" + PSC + "," + C2C + "]");
        responses.put(ADMIN + "/roles", ROLES);
        responses.put(ADMIN + "/groups/g-psc/members", "[{\"id\":\"u-admin\"},{\"id\":\"u-user\"}]");
        responses.put(ADMIN + "/groups/g-c2c/members", "[{\"id\":\"u-admin\"}]");
        responses.put(ADMIN + "/roles/GN_ADMIN/users", "[{\"id\":\"u-admin\"}]");
        responses.put(ADMIN + "/roles/USER/users", "[{\"id\":\"u-admin\"},{\"id\":\"u-user\"}]");
        responses.put(ADMIN + "/users", "[" + TESTADMIN + "," + TESTUSER + "," + SERVICE_ACCOUNT + "]");

        KeycloakProperties config = new KeycloakProperties();
        String url = "http://localhost:" + server.getAddress().getPort() + "/";
        config.setUrl(url);
        config.setOrgCodeAttribute("cn");
        repository = new KeycloakAccountsRepository(
                new KeycloakAdminClient(url, "georchestra", "geonetwork", "secret"), config);
    }

    @After
    public void tearDown() {
        server.stop(0);
    }

    private void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        requests.add(exchange.getRequestMethod() + " " + exchange.getRequestURI());
        String body;
        int status = 200;
        if (path.equals("/realms/georchestra/protocol/openid-connect/token")) {
            tokenRequests.incrementAndGet();
            body = "{\"access_token\":\"token\",\"expires_in\":300}";
        } else if (!"Bearer token".equals(exchange.getRequestHeaders().getFirst("Authorization"))) {
            status = 401;
            body = "{}";
        } else if (responses.containsKey(path)) {
            body = responses.get(path);
        } else {
            status = 404;
            body = "{\"error\":\"not found\"}";
        }
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    @Test
    public void findAllOrganizations_mapsGroups() {
        List<CanonicalGroup> orgs = repository.findAllOrganizations();

        assertEquals(2, orgs.size());
        CanonicalGroup psc = orgs.get(0);
        assertEquals("g-psc", psc.getId());
        assertEquals("PSC", psc.getName());
        assertEquals("Project Steering Committee", psc.getOrgTitle());
        assertEquals("psc@example.org", psc.getMail());
        assertEquals(GroupSyncMode.orgs, psc.getOrigin());
        assertEquals(64, psc.getLastUpdated().length());
        // no code attribute: the group name is both the code and the title
        assertEquals("C2C", orgs.get(1).getName());
        assertEquals("C2C", orgs.get(1).getOrgTitle());
        assertEquals("the access token is reused", 1, tokenRequests.get());
    }

    @Test
    public void onlyFlaggedGroupsAreOrganizations() {
        responses.put(ADMIN + "/groups", "[" + PSC.replace("\"cn\":[\"PSC\"]", "\"cn\":[\"PSC\"],\"isOrganization\":[\"true\"]")
                + "," + C2C + "]");
        KeycloakProperties config = new KeycloakProperties();
        String url = "http://localhost:" + server.getAddress().getPort();
        config.setOrgCodeAttribute("cn");
        config.setOrgFlagAttribute("isOrganization");
        repository = new KeycloakAccountsRepository(
                new KeycloakAdminClient(url, "georchestra", "geonetwork", "secret"), config);

        assertEquals(List.of("PSC"), repository.findAllOrganizations().stream().map(CanonicalGroup::getName)
                .collect(Collectors.toList()));
    }

    @Test
    public void syncAllGroups() {
        String pscOrg = PSC.replace("\"cn\":[\"PSC\"]", "\"cn\":[\"PSC\"],\"isOrganization\":[\"true\"]");
        String projet = "{\"id\":\"g-projet\",\"name\":\"Projet Littoral\",\"path\":\"/Projet Littoral\","
                + "\"attributes\":{\"cn\":[\"PROJET_LITTORAL\"],\"isOrganization\":[\"false\"]}}";
        responses.put(ADMIN + "/groups", "[" + pscOrg + "," + projet + "]");
        responses.put(ADMIN + "/groups/g-projet/members", "[{\"id\":\"u-user\"}]");
        KeycloakProperties config = new KeycloakProperties();
        String url = "http://localhost:" + server.getAddress().getPort();
        config.setOrgCodeAttribute("cn");
        config.setOrgFlagAttribute("isOrganization");
        config.setSyncAllGroups(true);
        repository = new KeycloakAccountsRepository(
                new KeycloakAdminClient(url, "georchestra", "geonetwork", "secret"), config);

        assertEquals("every group is a GeoNetwork group", List.of("PSC", "PROJET_LITTORAL"),
                repository.findAllOrganizations().stream().map(CanonicalGroup::getName).collect(Collectors.toList()));
        CanonicalUser user = repository.findAllUsers().get(1);
        assertEquals("testuser", user.getUsername());
        assertEquals("PSC", user.getOrganization());
        assertEquals(List.of("PROJET_LITTORAL:USER", "USER"), user.getRoles());
        assertTrue(repository.findOrganizationByName("PROJET_LITTORAL").isPresent());

        responses.put(ADMIN + "/users", "[" + TESTUSER + "]");
        responses.put(ADMIN + "/users/u-user/groups", "[" + pscOrg + "," + projet + "]");
        responses.put(ADMIN + "/users/u-user/role-mappings/realm", "[{\"name\":\"USER\"}]");
        CanonicalUser found = repository.findUserByUsername("testuser").get();
        assertEquals("PSC", found.getOrganization());
        assertEquals(List.of("PROJET_LITTORAL:USER", "USER"), found.getRoles());
    }

    @Test
    public void findOrganizationByName() {
        assertEquals("g-c2c", repository.findOrganizationByName("C2C").get().getId());
        assertFalse(repository.findOrganizationByName("UNKNOWN").isPresent());
    }

    @Test
    public void organizationsUnderParentGroup() {
        responses.put(ADMIN + "/group-by-path/orgs", "{\"id\":\"g-orgs\",\"name\":\"orgs\",\"path\":\"/orgs\"}");
        responses.put(ADMIN + "/groups/g-orgs/children",
                "[" + PSC.replace("\"path\":\"/Project Steering Committee\"",
                        "\"path\":\"/orgs/Project Steering Committee\"") + "]");
        KeycloakProperties config = new KeycloakProperties();
        config.setOrgGroupPath("/orgs/");
        config.setOrgCodeAttribute("cn");
        String url = "http://localhost:" + server.getAddress().getPort();
        repository = new KeycloakAccountsRepository(
                new KeycloakAdminClient(url, "georchestra", "geonetwork", "secret"), config);

        List<CanonicalGroup> orgs = repository.findAllOrganizations();

        assertEquals(List.of("PSC"), orgs.stream().map(CanonicalGroup::getName).collect(Collectors.toList()));
    }

    @Test
    public void findAllRoles_skipsBuiltinRoles() {
        List<CanonicalGroup> roles = repository.findAllRoles();

        assertEquals(List.of("GN_ADMIN", "USER"),
                roles.stream().map(CanonicalGroup::getName).collect(Collectors.toList()));
        assertEquals("GN admin", roles.get(0).getDescription());
        assertEquals(GroupSyncMode.roles, roles.get(0).getOrigin());
    }

    @Test
    public void findRoleByName() {
        responses.put(ADMIN + "/roles/GN_ADMIN", "{\"id\":\"r-admin\",\"name\":\"GN_ADMIN\"}");

        assertEquals("r-admin", repository.findRoleByName("GN_ADMIN").get().getId());
        assertFalse(repository.findRoleByName("UNKNOWN").isPresent());
        assertFalse(repository.findRoleByName("offline_access").isPresent());
    }

    @Test
    public void findAllUsers_resolvesOrganizationAndRoles() {
        List<CanonicalUser> users = repository.findAllUsers();

        assertEquals("service accounts are ignored", List.of("testadmin", "testuser"),
                users.stream().map(CanonicalUser::getUsername).collect(Collectors.toList()));
        CanonicalUser admin = users.get(0);
        assertEquals("u-admin", admin.getId());
        assertEquals("Test", admin.getFirstName());
        assertEquals("ADMIN", admin.getLastName());
        assertEquals("admin@example.org", admin.getEmail());
        assertEquals("Boss", admin.getTitle());
        assertEquals("first organization group", "PSC", admin.getOrganization());
        assertEquals(List.of("GN_ADMIN", "USER"), admin.getRoles());

        CanonicalUser user = users.get(1);
        assertEquals("PSC", user.getOrganization());
        assertEquals(List.of("USER"), user.getRoles());
        assertNull(user.getEmail());
        assertTrue("users are paginated",
                requests.stream().anyMatch(r -> r.contains("/users?briefRepresentation=false&first=0&max=500")));
    }

    @Test
    public void findUserByUsername() {
        responses.put(ADMIN + "/users", "[" + TESTUSER + "]");
        responses.put(ADMIN + "/users/u-user/groups",
                "[{\"id\":\"g-other\",\"name\":\"other\",\"path\":\"/parent/other\"}," + PSC + "]");
        responses.put(ADMIN + "/users/u-user/role-mappings/realm",
                "[{\"name\":\"USER\"},{\"name\":\"default-roles-georchestra\"}]");

        CanonicalUser user = repository.findUserByUsername("testuser").get();

        assertEquals("u-user", user.getId());
        assertEquals("non organization groups are ignored", "PSC", user.getOrganization());
        assertEquals(List.of("USER"), user.getRoles());
        assertTrue(requests.stream().anyMatch(r -> r.contains("username=testuser") && r.contains("exact=true")));
        assertFalse(repository.findUserByUsername("someone-else").isPresent());
    }

    @Test
    public void lastUpdatedChangesWithTheUser() {
        String before = repository.findAllUsers().get(1).getLastUpdated();
        responses.put(ADMIN + "/roles/GN_ADMIN/users", "[{\"id\":\"u-admin\"},{\"id\":\"u-user\"}]");

        String after = repository.findAllUsers().get(1).getLastUpdated();

        assertNotEquals(before, after);
        assertEquals(after, repository.findAllUsers().get(1).getLastUpdated());
    }
}
