package org.georchestra.geonetwork.security.integration;

import java.util.Objects;
import java.util.Optional;

import org.fao.geonet.domain.User;
import org.georchestra.config.security.GeorchestraUserDetails;
import org.georchestra.geonetwork.security.repository.UserLink;
import org.georchestra.geonetwork.security.repository.UserLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Service that
 */
@Service
@Slf4j(topic = "org.georchestra.geonetwork.security.integration")
public class GeorchestraToGeonetworkUserReconcilingService {

    private @Autowired UserLinkRepository userLinkRepository;

    public Optional<User> findGeonetworkUser(@NonNull GeorchestraUserDetails georchestraUser) {
        if (georchestraUser.isAnonymous()) {
            throw new IllegalArgumentException("pre-authenticated geOrchestra user is annonymous");
        }
        final String georchestraUserId = georchestraUser.getUserId();
        Objects.requireNonNull(georchestraUserId,
                () -> "geOrchestra user id not provided. User name: " + georchestraUser.getUsername());

        return userLinkRepository.findById(georchestraUserId).map(UserLink::getGeonetworkUser);
    }

    /**
     * Takes {@code georchestraUser} as the canonical representation of a given
     * user, and returns the GeoNetwork {@link User user} that's linked to it,
     * possibly reconciling (i.e. creating or updating) the GeoNetwork user
     * properties.
     * <p>
     * If the GN user does not exist, one will be created. If the GN user properties
     * are outdated with regard to the geOrchestra user (rather, the relevant ones
     * for the sake of keeping the credentials in synch with the geOrchestra user),
     * the GN user will be updated to match the canonical information provided by
     * geOrchestra's security proxy (or whatever other means the canonical user
     * representation was obtained from).
     * <p>
     * When this method returns, it is assured that the returned GeoNetwork user
     * matches the credentials of the provided canonical user info.
     */
    public @NonNull User getMatchingGeonetworkUser(@NonNull GeorchestraUserDetails georchestraUser) {

        Optional<User> gnUser = findGeonetworkUser(georchestraUser);

        return null;
    }

    /**
     * Evaluates whether the GeoNetwork {@link User user} information is current
     * with the canonical geOrchestra user.
     * 
     * @return {@code true} if the users match according to the criteria to keep
     *         them in synch, {@code false} otherwise, meaning the GeoNetwork user
     *         properties must be updated in the database to match the geOrchestra
     *         user.
     */
    public boolean gnUserIsUpToDate(final @NonNull User gnUser, final @NonNull GeorchestraUserDetails preAuthUser) {
        // TODO: define the criteria to check whether the two user representations are
        // in sync. It could be that a "lastUpdated" timestamp is provided, and
        // maintained as a property of UserLink (given there's no such property in
        // User), or an heuristic approach comparing the relevant properties.
        return true;
    }
}
