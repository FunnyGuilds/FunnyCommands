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

package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.resources.Context;
import panda.std.function.TriFunction;
import org.panda_lang.utilities.inject.Property;

public final class TypeMapper<T> {

    private final String name;
    private final Class<?> type;
    private final TriFunction<Context, Property, String, T> deserializer;

    public TypeMapper(String name, Class<?> type, TriFunction<Context, Property, String, T> deserializer) {
        this.name = name;
        this.type = type;
        this.deserializer = deserializer;
    }

    public T map(Context context, Property parameter, String value) {
        return deserializer.apply(context, parameter, value);
    }

    public Class<?> getType() {
        return type;
    }

    public String getName() {
        return name;
    }

}
