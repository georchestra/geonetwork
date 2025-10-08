package org.geonetwork.security.external.configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.fao.geonet.domain.Profile;
import org.springframework.lang.NonNull;

import jeeves.component.ProfileManager;

public class ProfileMappingProperties implements Serializable {
    private static final long serialVersionUID = 1L;

    private Profile _default = Profile.RegisteredUser;
    private Map<String, Profile> rolemappings = new HashMap<>();

    public Profile getDefault() {
        return _default;
    }

    public void setDefault(Profile _default) {
        this._default = _default;
    }

    public Map<String, Profile> getRolemappings() {
        return rolemappings;
    }

    public void setRolemappings(Map<String, Profile> rolemappings) {
        this.rolemappings = rolemappings;
    }

    public @NonNull Profile resolveHighestProfileFromRoleNames(@NonNull List<String> roles) {
        Profile[] matches = rolesToProfile(roles);
        Profile highestUserProfile = ProfileManager.getHighestProfile(matches);
        if (highestUserProfile == null) {
            Profile defaultProfile = getDefault();
            ExternalizedSecurityProperties.log.warn(
                    "Unable to determine user Profile from roles {}. Assigning default profile {} . Available mappings: {}",
                    roles, defaultProfile, logStrMappings(getRolemappings()));
            return defaultProfile;
        }
        return highestUserProfile;
    }

    private Profile[] rolesToProfile(@NonNull List<String> roles) {
        Map<String, Profile> roleToProfileMapping = getRolemappings();
        return roles.stream()//
                .map(roleToProfileMapping::get)//
                .filter(Objects::nonNull)//
                .toArray(Profile[]::new);
    }

    private String logStrMappings(Map<String, Profile> roleToProfileMapping) {
        return roleToProfileMapping.entrySet().stream().//
                map(e -> String.format("%s -> %s", e.getKey(), e.getValue()))//
                .collect(Collectors.joining(","));
    }

    @Override
    public int hashCode() {
        return Objects.hash(_default, rolemappings);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProfileMappingProperties other = (ProfileMappingProperties) obj;
        return _default == other._default && Objects.equals(rolemappings, other.rolemappings);
    }

}