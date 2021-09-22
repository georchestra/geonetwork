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
package org.geonetwork.security.external.configuration;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ScheduledSynchronizationProperties implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean enabled = true;
    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private boolean retryOnFailure = true;
    private int initialDelay = 10;
    private int retryDelay = 10;
    private int delayBetweenRuns = 60;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public boolean isRetryOnFailure() {
        return retryOnFailure;
    }

    public void setRetryOnFailure(boolean retryOnFailure) {
        this.retryOnFailure = retryOnFailure;
    }

    public int getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(int initialDelay) {
        this.initialDelay = initialDelay;
    }

    public int getRetryDelay() {
        return retryDelay;
    }

    public void setRetryDelay(int retryDelay) {
        this.retryDelay = retryDelay;
    }

    public int getDelayBetweenRuns() {
        return delayBetweenRuns;
    }

    public void setDelayBetweenRuns(int delayBetweenRuns) {
        this.delayBetweenRuns = delayBetweenRuns;
    }

    @Override
    public int hashCode() {
        return Objects.hash(delayBetweenRuns, enabled, initialDelay, retryDelay, retryOnFailure, timeUnit);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ScheduledSynchronizationProperties other = (ScheduledSynchronizationProperties) obj;
        return delayBetweenRuns == other.delayBetweenRuns && enabled == other.enabled
                && initialDelay == other.initialDelay && retryDelay == other.retryDelay
                && retryOnFailure == other.retryOnFailure && timeUnit == other.timeUnit;
    }

}