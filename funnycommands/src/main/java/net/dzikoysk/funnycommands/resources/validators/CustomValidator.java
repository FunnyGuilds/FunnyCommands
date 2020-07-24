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

package net.dzikoysk.funnycommands.resources.validators;

import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.resources.Validator;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.commons.function.ThrowingQuadFunction;
import org.panda_lang.utilities.inject.InjectorProperty;

import java.lang.annotation.Annotation;

public class CustomValidator<A extends Annotation, V, E extends Exception> implements Validator<A, V, E> {

    private final Class<A> annotation;
    private final Class<V> type;
    private final ThrowingQuadFunction<Origin, A, InjectorProperty, V, Boolean, E> function;

    public CustomValidator(Class<A> annotation, Class<V> type, ThrowingQuadFunction<Origin, A, InjectorProperty, V, Boolean, E> function) {
        this.annotation = annotation;
        this.type = type;
        this.function = function;
    }

    @Override
    public boolean validate(Origin origin, A annotation, InjectorProperty parameter, V value) throws E {
        return function.apply(origin, annotation, parameter, value);
    }

    @Override
    public @Nullable Class<A> getAnnotation() {
        return annotation;
    }

    @Override
    public @Nullable Class<V> getType() {
        return type;
    }

}
