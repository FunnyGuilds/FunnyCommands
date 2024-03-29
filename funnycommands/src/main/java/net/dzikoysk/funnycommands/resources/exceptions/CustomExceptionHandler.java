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

package net.dzikoysk.funnycommands.resources.exceptions;

import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.DetailedExceptionHandler;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class CustomExceptionHandler<E extends Exception> implements DetailedExceptionHandler<E> {

    private final Class<E> type;
    private final BiFunction<Context, E, Boolean> exceptionConsumer;

    public CustomExceptionHandler(Class<E> type, BiFunction<Context, E, Boolean> exceptionConsumer) {
        this.type = type;
        this.exceptionConsumer = exceptionConsumer;
    }

    public CustomExceptionHandler(Class<E> type, Function<E, Boolean> exceptionConsumer) {
        this(type, (context, exception) -> exceptionConsumer.apply(exception));
    }

    @Override
    public Boolean apply(Context context, E exception) {
        return exceptionConsumer.apply(context, exception);
    }

    @Override
    public Class<E> getExceptionType() {
        return type;
    }

}
