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
package org.joyrest.context.helper;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.joyrest.common.annotation.General;
import org.joyrest.common.annotation.Ordered;
import org.joyrest.transform.Transformer;

import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;

public final class ConfigurationHelper {

    public static <T extends Ordered> List<T> sort(Collection<T> collection) {
        Comparator<T> aspectComparator =
            (e1, e2) -> Integer.compare(e1.getOrder(), e2.getOrder());

        return collection.stream()
            .sorted(aspectComparator)
            .collect(toList());
    }

    public static <T extends Transformer> Map<Boolean, List<T>> createTransformers(List<T> transform) {
        return transform.stream()
            .collect(partitioningBy(General::isGeneral));
    }
}
