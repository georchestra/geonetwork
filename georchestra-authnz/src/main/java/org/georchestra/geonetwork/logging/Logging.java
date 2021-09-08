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
package org.georchestra.geonetwork.logging;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.apache.log4j.Logger;

/**
 * Logger abstraction using {@link String#format} syntax
 */
public class Logging {

    private static ConcurrentMap<String, Logging> loggers = new ConcurrentHashMap<>();

    private final Logger logger;

    private Logging(String topic) {
        this.logger = Logger.getLogger(topic);
    }

    public static Logging getLogger(String topic) {
        return loggers.computeIfAbsent(topic, Logging::new);
    }

    @SafeVarargs
    public final void debug(String msgFormat, Supplier<?>... args) {
        if (logger.isDebugEnabled())
            debug(msgFormat, resolveArgs(args));
    }

    public void debug(String msgFormat, Object... msgArgs) {
        String msg = resolveMessage(msgFormat, msgArgs);
        System.err.println("--------------- " + msg);
        if (logger.isDebugEnabled())
            logger.debug(msg);
    }

    @SafeVarargs
    public final void info(String msgFormat, Supplier<?>... args) {
        info(msgFormat, resolveArgs(args));
    }

    public void info(String msgFormat, Object... msgArgs) {
        String msg = resolveMessage(msgFormat, msgArgs);
        System.err.println("--------------- " + msg);
        if (logger.isInfoEnabled()) {
            logger.info(msg);
        }
    }

    @SafeVarargs
    public final void warn(String msgFormat, Supplier<?>... args) {
        warn(msgFormat, resolveArgs(args));
    }

    public void warn(String msgFormat, Object... msgArgs) {
        String msg = resolveMessage(msgFormat, msgArgs);
        System.err.println("--------------- " + msg);
        if (logger.isInfoEnabled()) {
            logger.info(msg);
        }
    }

    private String resolveMessage(String msgFormat, Object... msgArgs) {
        return String.format(msgFormat, msgArgs);
    }

    private Object[] resolveArgs(Supplier<?>... args) {
        return args == null ? null : Arrays.stream(args).map(Supplier::get).toArray(Object[]::new);
    }

}
