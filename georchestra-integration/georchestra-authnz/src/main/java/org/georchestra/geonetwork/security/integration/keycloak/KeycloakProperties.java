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

/**
 * Configuration of the Keycloak accounts source ({@code georchestra.keycloak.*}
 * properties of {@code geonetwork.properties}).
 */
public class KeycloakProperties {

    /** Internal base URL of Keycloak, e.g. {@code http://keycloak:8080} */
    private String url = "http://keycloak:8080";
    private String realm = "georchestra";
    /** Confidential client with service account enabled */
    private String clientId = "geonetwork";
    private String clientSecret = "";
    /**
     * Path of the group whose children are the organizations, without leading
     * slash. Empty: organizations are the top-level groups.
     */
    private String orgGroupPath = "";
    /**
     * Group attribute holding the organization identifier (code). Empty: the group name, which
     * then is the identifier.
     */
    private String orgCodeAttribute = "";
    /**
     * Group attribute flagging an organization (value {@code true}): the other groups are not
     * organizations. Empty: every candidate group is an organization.
     */
    private String orgFlagAttribute = "";
    /**
     * Synchronize every group, organization or not, as a GeoNetwork group: members of a
     * group that is not an organization get the {@code <CODE>:USER} role. Requires the
     * {@code role_per_org} synchronization mode ({@code geonetwork.syncMode}).
     */
    private boolean syncAllGroups = false;
    /**
     * Group attribute holding the organization title. Empty (or missing on a group): the group
     * name.
     */
    private String orgNameAttribute = "";
    /** Group attributes holding the other organization properties */
    private String orgMailAttribute = "mail";
    private String orgDescriptionAttribute = "description";
    private String orgLinkageAttribute = "labeledURI";
    /** User attribute holding the user title */
    private String userTitleAttribute = "title";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getOrgGroupPath() {
        return orgGroupPath;
    }

    public void setOrgGroupPath(String orgGroupPath) {
        String path = orgGroupPath == null ? "" : orgGroupPath.trim();
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        this.orgGroupPath = path;
    }

    public String getOrgCodeAttribute() {
        return orgCodeAttribute;
    }

    public void setOrgCodeAttribute(String orgCodeAttribute) {
        this.orgCodeAttribute = orgCodeAttribute;
    }

    public String getOrgFlagAttribute() {
        return orgFlagAttribute;
    }

    public void setOrgFlagAttribute(String orgFlagAttribute) {
        this.orgFlagAttribute = orgFlagAttribute;
    }

    public boolean isSyncAllGroups() {
        return syncAllGroups;
    }

    public void setSyncAllGroups(boolean syncAllGroups) {
        this.syncAllGroups = syncAllGroups;
    }

    public String getOrgNameAttribute() {
        return orgNameAttribute;
    }

    public void setOrgNameAttribute(String orgNameAttribute) {
        this.orgNameAttribute = orgNameAttribute;
    }

    public String getOrgMailAttribute() {
        return orgMailAttribute;
    }

    public void setOrgMailAttribute(String orgMailAttribute) {
        this.orgMailAttribute = orgMailAttribute;
    }

    public String getOrgDescriptionAttribute() {
        return orgDescriptionAttribute;
    }

    public void setOrgDescriptionAttribute(String orgDescriptionAttribute) {
        this.orgDescriptionAttribute = orgDescriptionAttribute;
    }

    public String getOrgLinkageAttribute() {
        return orgLinkageAttribute;
    }

    public void setOrgLinkageAttribute(String orgLinkageAttribute) {
        this.orgLinkageAttribute = orgLinkageAttribute;
    }

    public String getUserTitleAttribute() {
        return userTitleAttribute;
    }

    public void setUserTitleAttribute(String userTitleAttribute) {
        this.userTitleAttribute = userTitleAttribute;
    }
}
