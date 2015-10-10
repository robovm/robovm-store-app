/*
 * Copyright (C) 2013-2015 RoboVM AB
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *   
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.robovm.store.api;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AuthToken {
    private static final long TOKEN_LIFE_TIME = 30; // minutes

    private final String token;
    private ScheduledFuture<?> expiredFuture;

    public AuthToken(String token) {
        this(token, null);
    }

    public AuthToken(String token, Runnable expired) {
        this.token = token;

        if (expired != null) {
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            expiredFuture = executor.schedule(expired, TOKEN_LIFE_TIME, TimeUnit.MINUTES);
        }
    }

    public boolean isExpired() {
        return expiredFuture.isDone();
    }

    public String getTokenString() {
        return token;
    }
}
