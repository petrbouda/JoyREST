/*
 * Copyright 2015 Petr Bouda
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joyrest.ittest.aspect;

import java.util.EnumMap;
import java.util.Map;

import static java.util.Objects.isNull;

public final class ContextHolder {

    private static final ThreadLocal<Map<RegistryKey, String>> THREAD_WITH_CONTEXT = new ThreadLocal<>();

    private ContextHolder() {
    }

    public static void put(RegistryKey key, String payload) {
        if (isNull(THREAD_WITH_CONTEXT.get())) {
            THREAD_WITH_CONTEXT.set(new EnumMap<>(RegistryKey.class));
        }

        THREAD_WITH_CONTEXT.get().put(key, payload);
    }

    public static String get(RegistryKey key) {
        if (isNull(THREAD_WITH_CONTEXT.get())) {
            THREAD_WITH_CONTEXT.set(new EnumMap<>(RegistryKey.class));
        }

        return THREAD_WITH_CONTEXT.get().get(key);
    }

    public static void cleanupThread() {
        THREAD_WITH_CONTEXT.remove();
    }

    public enum RegistryKey {
        FIRST_KEY,
        SECOND_KEY,
        THIRD_KEY
    }

}