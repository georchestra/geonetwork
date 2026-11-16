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

import static org.georchestra.geonetwork.security.integration.keycloak.KeycloakAdminClient.encodePathSegment;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.geonetwork.security.external.model.CanonicalGroup;
import org.geonetwork.security.external.model.CanonicalUser;
import org.geonetwork.security.external.model.GroupSyncMode;
import org.geonetwork.security.external.repository.CanonicalAccountsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * {@link CanonicalAccountsRepository} backed by the Keycloak admin REST API, an
 * alternative to the geOrchestra console as the source of users, organizations
 * and roles for the GeoNetwork accounts synchronization.
 * <p>
 * Mapping with the geOrchestra model:
 * <ul>
 * <li>organization = Keycloak group: its identifier is the code attribute of the group
 * ({@link KeycloakProperties#getOrgCodeAttribute()}, else the group name), its title the group
 * name (or {@link KeycloakProperties#getOrgNameAttribute()}); mail, description and linkage
 * come from group attributes. Organizations
 * are the top-level groups, or the children of {@link KeycloakProperties#getOrgGroupPath()}.
 * <li>role = Keycloak realm role (built-in Keycloak roles are ignored)
 * <li>user = Keycloak user, its organization being its first organization group
 * and its roles its realm roles. Service accounts are ignored.
 * </ul>
 * With the geOrchestra LDAP federated in Keycloak (group mapper on
 * {@code ou=orgs}, role mapper on {@code ou=roles}), this exposes the same
 * accounts as the console.
 * <p>
 * Identifiers are the Keycloak ids. {@code lastUpdated} is a hash of the
 * synchronized properties, so that GeoNetwork only updates what changed.
 * <p>
 * The client service account needs the {@code realm-management} roles
 * {@code view-users}, {@code query-groups} and {@code view-realm}.
 */
public class KeycloakAccountsRepository implements CanonicalAccountsRepository {

    private static final Logger log = LoggerFactory.getLogger(KeycloakAccountsRepository.class);

    private static final Map<String, String> FULL = Map.of("briefRepresentation", "false");

    private final KeycloakAdminClient client;
    private final KeycloakProperties config;

    public KeycloakAccountsRepository(KeycloakAdminClient client, KeycloakProperties config) {
        this.client = Objects.requireNonNull(client);
        this.config = Objects.requireNonNull(config);
    }

    // ------------------------------------------------------------------ users

    @Override
    public List<CanonicalUser> findAllUsers() {
        Map<String, String> orgByUserId = new HashMap<>();
        Map<String, Set<String>> rolesByUserId = new HashMap<>();
        for (JsonNode group : config.isSyncAllGroups() ? candidateGroups() : orgGroups()) {
            String code = code(group);
            boolean organization = isFlagged(group);
            for (JsonNode member : client.getAll("/groups/" + group.path("id").asText() + "/members",
                    Map.of("briefRepresentation", "true"))) {
                String userId = member.path("id").asText();
                if (organization) {
                    orgByUserId.putIfAbsent(userId, code);
                } else {
                    rolesByUserId.computeIfAbsent(userId, id -> new TreeSet<>()).add(groupMemberRole(code));
                }
            }
        }

        for (JsonNode role : realmRoles()) {
            String roleName = role.path("name").asText();
            for (JsonNode member : client.getAll("/roles/" + encodePathSegment(roleName) + "/users",
                    Map.of("briefRepresentation", "true"))) {
                rolesByUserId.computeIfAbsent(member.path("id").asText(), id -> new TreeSet<>()).add(roleName);
            }
        }

        List<CanonicalUser> users = client.getAll("/users", FULL).stream()//
                .filter(this::isRegularUser)//
                .map(user -> {
                    String id = user.path("id").asText();
                    return toCanonicalUser(user, orgByUserId.get(id),
                            rolesByUserId.getOrDefault(id, Collections.emptySet()));
                }).collect(Collectors.toList());
        log.debug("Fetched {} users from Keycloak", users.size());
        return users;
    }

    @Override
    public Optional<CanonicalUser> findUserByUsername(String username) {
        Map<String, String> params = new LinkedHashMap<>(FULL);
        params.put("username", username);
        params.put("exact", "true");
        Optional<JsonNode> user = client.get("/users", params).map(this::stream)
                .flatMap(users -> users.stream()//
                        .filter(u -> username.equalsIgnoreCase(u.path("username").asText()))//
                        .filter(this::isRegularUser)//
                        .findFirst());
        return user.map(u -> {
            String id = u.path("id").asText();
            List<JsonNode> groups = client.get("/users/" + id + "/groups", FULL).map(this::stream)
                    .orElse(List.of());
            String org = groups.stream()//
                    .filter(this::isOrgGroup)//
                    .map(this::code)//
                    .findFirst().orElse(null);
            Set<String> roles = client.get("/users/" + id + "/role-mappings/realm", Map.of()).map(this::stream)
                    .orElse(List.of()).stream()//
                    .map(r -> r.path("name").asText())//
                    .filter(name -> !isBuiltinRole(name))//
                    .collect(Collectors.toCollection(TreeSet::new));
            if (config.isSyncAllGroups()) {
                groups.stream().filter(this::isCandidateGroup).filter(g -> !isFlagged(g))
                        .forEach(g -> roles.add(groupMemberRole(code(g))));
            }
            return toCanonicalUser(u, org, roles);
        });
    }

    // ---------------------------------------------------------- organizations

    @Override
    public List<CanonicalGroup> findAllOrganizations() {
        return (config.isSyncAllGroups() ? candidateGroups() : orgGroups()).stream()
                .map(this::toCanonicalOrganization).collect(Collectors.toList());
    }

    @Override
    public Optional<CanonicalGroup> findOrganizationByName(String name) {
        return (config.isSyncAllGroups() ? candidateGroups() : orgGroups()).stream()//
                .filter(g -> name.equals(code(g)))//
                .findFirst().map(this::toCanonicalOrganization);
    }

    @Override
    public Optional<byte[]> getLogo(String id) {
        return Optional.empty();
    }

    // ------------------------------------------------------------------ roles

    @Override
    public List<CanonicalGroup> findAllRoles() {
        return realmRoles().stream().map(this::toCanonicalRole).collect(Collectors.toList());
    }

    @Override
    public Optional<CanonicalGroup> findRoleByName(String name) {
        if (isBuiltinRole(name)) {
            return Optional.empty();
        }
        return client.get("/roles/" + encodePathSegment(name), Map.of()).map(this::toCanonicalRole);
    }

    // ---------------------------------------------------------------- helpers

    List<JsonNode> orgGroups() {
        return candidateGroups().stream().filter(this::isFlagged).collect(Collectors.toList());
    }

    /**
     * Role given to the members of a group that is not an organization, with
     * {@link KeycloakProperties#isSyncAllGroups()}: {@code <CODE>:USER}, which the
     * {@code role_per_org} synchronization mode turns into a membership of the
     * GeoNetwork group {@code <CODE>}, with the profile mapped from {@code USER}
     * (RegisteredUser).
     */
    static String groupMemberRole(String code) {
        return code + ":USER";
    }

    /** Whether a group (as returned with its path) is a candidate group (organization or not) */
    boolean isCandidateGroup(JsonNode group) {
        String path = group.path("path").asText();
        String name = group.path("name").asText();
        String parentPath = config.getOrgGroupPath();
        return (parentPath.isEmpty() ? "/" + name : "/" + parentPath + "/" + name).equals(path);
    }

    /** Top-level groups, or children of the organizations parent group, organizations or not */
    List<JsonNode> candidateGroups() {
        String parentPath = config.getOrgGroupPath();
        List<JsonNode> groups;
        if (parentPath.isEmpty()) {
            groups = client.getAll("/groups", FULL);
        } else {
            String parentId = client.get("/group-by-path/" + encodePath(parentPath), Map.of())//
                    .map(g -> g.path("id").asText())//
                    .orElseThrow(() -> new KeycloakAdminClient.KeycloakClientException(
                            "Organizations parent group not found: /" + parentPath));
            groups = client.getAll("/groups/" + parentId + "/children", FULL);
        }
        return groups;
    }

    /**
     * Whether a group (as returned with its {@code path}) is an organization
     */
    boolean isOrgGroup(JsonNode group) {
        String path = group.path("path").asText();
        String name = group.path("name").asText();
        String parentPath = config.getOrgGroupPath();
        String expected = parentPath.isEmpty() ? "/" + name : "/" + parentPath + "/" + name;
        return expected.equals(path) && isFlagged(group);
    }

    /**
     * Whether a group is flagged as an organization ({@link KeycloakProperties#getOrgFlagAttribute()}
     * = {@code true}); always true when no flag attribute is configured
     */
    boolean isFlagged(JsonNode group) {
        String flag = config.getOrgFlagAttribute();
        return flag == null || flag.isEmpty() || "true".equalsIgnoreCase(attribute(group, flag));
    }

    private static String encodePath(String path) {
        return Arrays.stream(path.split("/")).map(KeycloakAdminClient::encodePathSegment)
                .collect(Collectors.joining("/"));
    }

    private List<JsonNode> realmRoles() {
        return client.getAll("/roles", FULL).stream()//
                .filter(r -> !isBuiltinRole(r.path("name").asText()))//
                .collect(Collectors.toList());
    }

    boolean isBuiltinRole(String name) {
        return "offline_access".equals(name) || "uma_authorization".equals(name)
                || ("default-roles-" + config.getRealm()).equals(name);
    }

    private boolean isRegularUser(JsonNode user) {
        return !user.hasNonNull("serviceAccountClientId")
                && !user.path("username").asText().startsWith("service-account-");
    }

    private CanonicalUser toCanonicalUser(JsonNode user, String organization, Set<String> roles) {
        String id = user.path("id").asText();
        String username = user.path("username").asText();
        String firstName = text(user, "firstName");
        String lastName = text(user, "lastName");
        String email = text(user, "email");
        String title = attribute(user, config.getUserTitleAttribute());
        List<String> roleList = new ArrayList<>(roles);
        return CanonicalUser.builder()//
                .withId(id)//
                .withUsername(username)//
                .withFirstName(firstName)//
                .withLastName(lastName)//
                .withEmail(email)//
                .withTitle(title)//
                .withOrganization(organization)//
                .withRoles(roleList)//
                .withLastUpdated(hash(id, username, firstName, lastName, email, title, organization,
                        String.join(",", roleList)))//
                .build();
    }

    private CanonicalGroup toCanonicalOrganization(JsonNode group) {
        String id = group.path("id").asText();
        String name = code(group);
        String title = Optional.ofNullable(attribute(group, config.getOrgNameAttribute()))
                .orElse(group.path("name").asText());
        String mail = attribute(group, config.getOrgMailAttribute());
        String description = attribute(group, config.getOrgDescriptionAttribute());
        String linkage = attribute(group, config.getOrgLinkageAttribute());
        return CanonicalGroup.builder()//
                .withId(id)//
                .withName(name)//
                .withOrgTitle(title)//
                .withMail(mail)//
                .withDescription(description)//
                .withLinkage(linkage)//
                .withOrigin(GroupSyncMode.orgs)//
                .withLastUpdated(hash(id, name, title, mail, description, linkage))//
                .build();
    }

    private CanonicalGroup toCanonicalRole(JsonNode role) {
        String id = role.path("id").asText();
        String name = role.path("name").asText();
        String description = text(role, "description");
        return CanonicalGroup.builder()//
                .withId(id)//
                .withName(name)//
                .withDescription(description)//
                .withOrigin(GroupSyncMode.roles)//
                .withLastUpdated(hash(id, name, description))//
                .build();
    }

    private List<JsonNode> stream(JsonNode array) {
        return StreamSupport.stream(array.spliterator(), false).collect(Collectors.toList());
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    /**
     * The organization identifier: the {@link KeycloakProperties#getOrgCodeAttribute() code
     * attribute} of its group if set, else the group name
     */
    String code(JsonNode group) {
        return Optional.ofNullable(attribute(group, config.getOrgCodeAttribute()))
                .orElse(group.path("name").asText());
    }

    private static String attribute(JsonNode node, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        JsonNode values = node.path("attributes").path(name);
        return values.isArray() && values.size() > 0 ? values.get(0).asText() : null;
    }

    static String hash(String... values) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            for (String value : values) {
                digest.update(String.valueOf(value).getBytes(StandardCharsets.UTF_8));
                digest.update((byte) 0);
            }
            StringBuilder hex = new StringBuilder();
            for (byte b : digest.digest()) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
