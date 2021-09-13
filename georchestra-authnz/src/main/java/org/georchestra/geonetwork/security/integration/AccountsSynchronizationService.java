//=============================================================================
//===	Copyright (C) 2001-2012 Food and Agriculture Organization of the
//===	United Nations (FAO-UN), United Nations World Food Programme (WFP)
//===	and United Nations Environment Programme (UNEP)
//===
//===	This program is free software; you can redistribute it and/or modify
//===	it under the terms of the GNU General Public License as published by
//===	the Free Software Foundation; either version 2 of the License, or (at
//===	your option) any later version.
//===
//===	This program is distributed in the hope that it will be useful, but
//===	WITHOUT ANY WARRANTY; without even the implied warranty of
//===	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//===	General Public License for more details.
//===
//===	You should have received a copy of the GNU General Public License
//===	along with this program; if not, write to the Free Software
//===	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301, USA
//===
//===	Contact: Jeroen Ticheler - FAO - Viale delle Terme di Caracalla 2,
//===	Rome - Italy. email: geonetwork@osgeo.org
//==============================================================================
package org.georchestra.geonetwork.security.integration;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.georchestra.geonetwork.logging.Logging;
import org.georchestra.security.api.OrganizationsApi;
import org.georchestra.security.api.RolesApi;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.model.GeorchestraUser;
import org.georchestra.security.model.Organization;
import org.georchestra.security.model.Role;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountsSynchronizationService implements InitializingBean {

    private static final Logging log = Logging.getLogger("org.georchestra.geonetwork.security.integration");

    private @Autowired RolesApi rolesApi;
    private @Autowired OrganizationsApi organizationsApi;
    private @Autowired UsersApi usersApi;

    private @Autowired GeorchestraToGeonetworkUserReconcilingService reconcilingService;

    private ScheduledExecutorService scheduledExecutor;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Scheduling initial synchronization in 10 seconds...");
        this.scheduledExecutor = Executors.newScheduledThreadPool(1);
        this.scheduleNextSync(10, TimeUnit.SECONDS);
    }

    public void synchronize() {
        log.info("Synchronizing users and groups...");
        try {
            synchronizeUsers();
            synchronizeGroupsWithOrganizations();
            log.info("Synchronization succeeded, scheduling next synchronization in 1 minute.");
            this.scheduleNextSync(1, TimeUnit.MINUTES);
        } catch (RuntimeException e) {
            log.warn("Synchronization failed, retrying in 10 seconds. Error message is %s", e.getMessage());
            this.scheduleNextSync(10, TimeUnit.SECONDS);
        }
    }

    private void scheduleNextSync(long delay, TimeUnit unit) {
        this.scheduledExecutor.schedule(this::synchronize, delay, unit);
    }

    private void synchronizeUsers() {
        log.info("Querying canonical users list...");
        List<GeorchestraUser> actualUsers = usersApi.findAll();
        log.info("Got %,d users, synchronizing with GeoNetwork users...", actualUsers.size());
        this.reconcilingService.synchronizeUsers(actualUsers);
    }

    private void synchronizeGroupsWithOrganizations() {
        log.info("Querying canonical organizations list...");
        List<Organization> orgs = organizationsApi.findAll();
        log.info("Got %,d organizations, synchronizing with GeoNetwork groups...", orgs.size());
        this.reconcilingService.synchronizeGroupsWithOrganizations(orgs);
    }

    private void synchronizeGroupsWithRoles() {
        log.info("Querying canonical roles list...");
        List<Role> roles = rolesApi.findAll();
        log.info("Got %,d roles, synchronizing with GeoNetwork groups...", roles.size());
        this.reconcilingService.synchronizeGroupsWithRoles(roles);
    }

}
