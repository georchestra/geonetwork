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

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Minimal client of the Keycloak admin REST API, authenticated with the client
 * credentials grant of a confidential client (service account).
 * <p>
 * The access token is cached until shortly before it expires, and renewed once
 * when a call is rejected with {@code 401}.
 */
public class KeycloakAdminClient {

    /** Renew the token this many seconds before it expires */
    static final int EXPIRY_MARGIN_SECONDS = 30;

    static final int PAGE_SIZE = 500;

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient http;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    private String token;
    private Instant tokenExpiresAt = Instant.EPOCH;

    public KeycloakAdminClient(String baseUrl, String realm, String clientId, String clientSecret) {
        this.baseUrl = stripTrailingSlash(baseUrl);
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.http = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    /**
     * @param path   admin API path relative to {@code /admin/realms/<realm>}, e.g.
     *               {@code /groups}
     * @param params query parameters
     * @return the response body, or empty if the resource does not exist (404)
     */
    public Optional<JsonNode> get(String path, Map<String, String> params) {
        URI uri = URI.create(baseUrl + "/admin/realms/" + realm + path + toQueryString(params));
        HttpResponse<String> response = send(uri, false);
        if (response.statusCode() == 401) {
            response = send(uri, true);
        }
        if (response.statusCode() == 404) {
            return Optional.empty();
        }
        if (response.statusCode() / 100 != 2) {
            throw new KeycloakClientException(
                    String.format("GET %s returned HTTP %d: %s", uri, response.statusCode(), response.body()));
        }
        return Optional.of(readJson(response.body(), uri));
    }

    /**
     * Fetches every element of a paginated admin API collection ({@code first} /
     * {@code max} query parameters).
     */
    public List<JsonNode> getAll(String path, Map<String, String> params) {
        List<JsonNode> all = new ArrayList<>();
        for (int first = 0;; first += PAGE_SIZE) {
            Map<String, String> pageParams = new LinkedHashMap<>(params);
            pageParams.put("first", String.valueOf(first));
            pageParams.put("max", String.valueOf(PAGE_SIZE));
            JsonNode page = get(path, pageParams).orElse(mapper.createArrayNode());
            page.forEach(all::add);
            if (page.size() < PAGE_SIZE) {
                return all;
            }
        }
    }

    public static String encodePathSegment(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private HttpResponse<String> send(URI uri, boolean renewToken) {
        HttpRequest request = HttpRequest.newBuilder(uri).timeout(TIMEOUT)//
                .header("Authorization", "Bearer " + getToken(renewToken))//
                .header("Accept", "application/json")//
                .GET().build();
        return execute(request);
    }

    synchronized String getToken(boolean forceRenewal) {
        if (forceRenewal || token == null || !Instant.now().isBefore(tokenExpiresAt)) {
            fetchToken();
        }
        return token;
    }

    private void fetchToken() {
        URI uri = URI.create(baseUrl + "/realms/" + realm + "/protocol/openid-connect/token");
        Map<String, String> form = new LinkedHashMap<>();
        form.put("grant_type", "client_credentials");
        form.put("client_id", clientId);
        form.put("client_secret", clientSecret);
        HttpRequest request = HttpRequest.newBuilder(uri).timeout(TIMEOUT)//
                .header("Content-Type", "application/x-www-form-urlencoded")//
                .POST(HttpRequest.BodyPublishers.ofString(toQueryString(form).substring(1)))//
                .build();
        HttpResponse<String> response = execute(request);
        if (response.statusCode() != 200) {
            throw new KeycloakClientException(String.format("Unable to get an access token for client '%s' from %s: HTTP %d %s",
                    clientId, uri, response.statusCode(), response.body()));
        }
        JsonNode body = readJson(response.body(), uri);
        token = body.path("access_token").asText();
        long expiresIn = body.path("expires_in").asLong(60);
        tokenExpiresAt = Instant.now().plusSeconds(Math.max(expiresIn - EXPIRY_MARGIN_SECONDS, 0));
    }

    private HttpResponse<String> execute(HttpRequest request) {
        try {
            return http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            throw new KeycloakClientException("Error calling " + request.uri() + ": " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KeycloakClientException("Interrupted calling " + request.uri(), e);
        }
    }

    private JsonNode readJson(String body, URI uri) {
        try {
            return mapper.readTree(body);
        } catch (IOException e) {
            throw new KeycloakClientException("Invalid JSON returned by " + uri, e);
        }
    }

    private static String toQueryString(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        return params.entrySet().stream()//
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))//
                .collect(Collectors.joining("&", "?", ""));
    }

    private static String stripTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    public static class KeycloakClientException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public KeycloakClientException(String message) {
            super(message);
        }

        public KeycloakClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
