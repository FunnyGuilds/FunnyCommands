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

import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.data.Origin;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

final class ArgumentsHandler implements BiFunction<Class<?>, Arg, Object> {

    private final CommandInfo command;
    private final Origin origin;

    ArgumentsHandler(CommandInfo command, Origin origin) {
        this.command = command;
        this.origin = origin;
    }

    @Override
    public Object apply(Class<?> aClass, Arg arg) {
        @Nullable Integer parameterIndex = command.getParameters().get(arg.value());

        if (parameterIndex == null) {
            throw new FunnyCommandsException("Unknown parameter: " + arg.value());
        }

        return command.getMappers()
                .get(arg.value())
                .map(origin, origin.getArgs()[parameterIndex]);
    }

}
