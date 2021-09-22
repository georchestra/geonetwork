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
package org.geonetwork.security.external.integration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.geonetwork.security.external.configuration.ExternalizedSecurityProperties;
import org.geonetwork.security.external.configuration.ScheduledSynchronizationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Periodically {@link AccountsReconcilingService#synchronize() synchronizes}
 * internal GeoNetwork authorization domain model objects based on the
 * {@link ScheduledSynchronizationProperties}.
 */
public class ScheduledAccountsSynchronizationService implements InitializingBean {

    static final Logger log = LoggerFactory
            .getLogger(ScheduledAccountsSynchronizationService.class.getPackage().getName());

    private @Autowired ExternalizedSecurityProperties config;
    private @Autowired AccountsReconcilingService reconcilingService;

    private ScheduledExecutorService scheduledExecutor;

    private ScheduledSynchronizationProperties getConfig() {
        return config.getScheduled();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ScheduledSynchronizationProperties config = getConfig();
        if (config.isEnabled()) {
            log.info(
                    "Periodic synchronization of users and groups is enabled by configuration and set to run every {} {}",
                    config.getDelayBetweenRuns(), config.getTimeUnit());

            this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

            log.info("Scheduling initial synchronization in {} {}...", config.getInitialDelay(), config.getTimeUnit());
            this.scheduleNextSync(config.getInitialDelay(), config.getTimeUnit());
        } else {
            log.warn("Periodic synchronization of users and groups is disabled by configuration.");
        }
    }

    private void scheduleNextSync(long delay, TimeUnit unit) {
        this.scheduledExecutor.schedule(this::synchronize, delay, unit);
    }

    public void synchronize() {
        ScheduledSynchronizationProperties config = getConfig();
        if (!config.isEnabled()) {
            log.debug("Scheduled synchronization disabled, task won't be executed.");
            return;
        }
        log.debug("Synchronizing users and groups...");
        try {
            this.reconcilingService.synchronize();

            log.debug("Synchronization succeeded, scheduling next synchronization in {} {}.",
                    config.getDelayBetweenRuns(), config.getTimeUnit());

            this.scheduleNextSync(config.getDelayBetweenRuns(), config.getTimeUnit());
        } catch (RuntimeException e) {
            if (config.isRetryOnFailure()) {
                log.warn("Synchronization failed, retrying in {} {}.", config.getRetryDelay(), config.getTimeUnit(), e);
                this.scheduleNextSync(config.getRetryDelay(), config.getTimeUnit());
            } else {
                log.warn("Synchronization failed, not retrying execution", e);
            }
        }
    }
}
