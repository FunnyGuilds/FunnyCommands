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

package net.dzikoysk.funnycommands.resources.binds;

import io.vavr.control.Option;
import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.commands.CommandInfo;
import net.dzikoysk.funnycommands.resources.DynamicBind;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import net.dzikoysk.funnycommands.stereotypes.Nillable;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.inject.InjectorResources;

import java.lang.reflect.Parameter;
import java.util.Optional;
import java.util.function.BiFunction;

public final class ArgumentsBind implements DynamicBind, BiFunction<Parameter, Arg, Object> {

    private final CommandInfo command;
    private final Origin origin;

    public ArgumentsBind(CommandInfo command, Origin origin) {
        this.command = command;
        this.origin = origin;
    }

    @Override
    public void accept(Origin origin, InjectorResources resources) {
        resources.annotatedWith(Arg.class).assignHandler(this);
    }

    @Override
    public @Nullable Object apply(Parameter required, Arg arg) {
        @Nullable Integer parameterIndex = command.getParameters().get(arg.value());

        if (parameterIndex == null) {
            throw new FunnyCommandsException("Unknown parameter: " + arg.value());
        }

        Object result = command.getMappers()
                .get(arg.value())
                .map(origin, required, origin.getArguments()[parameterIndex]);

        if (required.getType().isAssignableFrom(Option.class)) {
            return Option.of(result);
        }

        if (required.getType() == Optional.class) {
            return Optional.ofNullable(result);
        }

        if (result != null) {
            return result;
        }

        if (required.getAnnotation(Nillable.class) != null) {
            return null;
        }

        throw new NullPointerException("Illegal null value at " + required);
    }

}
