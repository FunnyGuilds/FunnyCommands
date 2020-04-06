/*
 * Copyright (c) 2020 Dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.funnycommands.commands;

import org.panda_lang.utilities.commons.function.TriFunction;

import java.lang.reflect.Parameter;

public final class TypeMapper<T> {

    private final String name;
    private final TriFunction<Origin, Parameter, String, T> deserializer;

    public TypeMapper(String name, TriFunction<Origin, Parameter, String, T> deserializer) {
        this.name = name;
        this.deserializer = deserializer;
    }

    public T map(Origin origin, Parameter parameter, String value) {
        return deserializer.apply(origin, parameter, value);
    }

    public String getName() {
        return name;
    }

}
