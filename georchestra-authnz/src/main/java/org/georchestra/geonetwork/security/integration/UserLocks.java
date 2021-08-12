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
package org.georchestra.geonetwork.security.integration;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.lang.NonNull;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

class UserLocks {

    private static final int MAX_LOCKS = 256;

    private final ConcurrentMap<Integer, Lock> locks = new ConcurrentHashMap<>();

    public Lock getUserLock(@NonNull String userId) {
        HashCode hashCode = Hashing.goodFastHash(64).hashString(userId, StandardCharsets.UTF_8);
        int bucket = Hashing.consistentHash(hashCode, MAX_LOCKS);
        return locks.computeIfAbsent(bucket, b -> new ReentrantLock());
    }

}
