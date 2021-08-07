package org.georchestra.geonetwork.security.repository;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.fao.geonet.domain.User;

import lombok.Data;

/**
 * Entity that represents a link between a geOrchestra user (given by its
 * {@link #georchestraUserId id}), and a GeoNetwork {@link User user}.
 * <p>
 * Used to enforce a 1:1 relationship between the two, with geOrchestra users
 * being the source of truth for authentication and authorization, while at the
 * same time not interfering with regular GeoNetwork internals, which require
 * users to be defined on its PostgreSQL database.
 */
@Data
@Entity
@Table(schema = "geonetwork_georchestra")
public class UserLink {

    @Id
    private String georchestraUserId;

    @OneToOne
    private User geonetworkUser;
}
