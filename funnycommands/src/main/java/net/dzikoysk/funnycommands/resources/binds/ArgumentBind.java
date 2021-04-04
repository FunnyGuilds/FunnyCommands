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

import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.commands.CommandInfo;
import net.dzikoysk.funnycommands.commands.CommandParameter;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.types.TypeMapper;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.function.Option;
import org.panda_lang.utilities.inject.Resources;

import java.lang.reflect.Array;
import java.util.Optional;

@FunnyComponent
public final class ArgumentBind implements Bind {

    @Override
    public void accept(Resources resources) {
        resources.annotatedWith(Arg.class).assignHandler((property, arg, args) -> {
            CommandInfo command = CommandUtils.getCommandInfo(args);
            Context context = CommandUtils.getContext(args);
            String parameter = arg.value();

            if (parameter.isEmpty()) {
                parameter = property.getName();
            }

            CommandParameter commandParameter = command.getParameters().get(parameter);

            if (commandParameter == null) {
                throw new FunnyCommandsException("Unknown parameter: " + arg.value() + " (inferred: " + parameter + ")");
            }

            String[] arguments = context.getArguments();
            String[] selected = StringUtils.EMPTY_ARRAY;

            if (commandParameter.getIndex() < arguments.length) {
                if (commandParameter.isVarargs()) {
                    selected = new String[arguments.length - commandParameter.getIndex()];
                    System.arraycopy(arguments, commandParameter.getIndex(), selected, 0, selected.length);
                }
                else {
                    selected = new String[] { arguments[commandParameter.getIndex()] };
                }
            }
            else if (!commandParameter.isOptional()) {
                throw new FunnyCommandsException(commandParameter + " is not marked as optional"); // should not happen
            }

            TypeMapper<?> mapper = command.getMappers().get(parameter);
            Object[] mappedArguments = (Object[]) Array.newInstance(mapper.getType(), selected.length);

            for (int index = 0; index < selected.length; index++) {
                mappedArguments[index] = command.getMappers()
                        .get(parameter)
                        .map(context, property, selected[index]);
            }

            Object result = mappedArguments;

            if (!commandParameter.isVarargs()) {
                if (mappedArguments.length == 0) {
                    result = null;
                }
                else if (mappedArguments.length == 1) {
                    result = mappedArguments[0];
                }
                else {
                    throw new FunnyCommandsException("Invalid amount of command parameters"); // should not happen
                }
            }

            if (property.getType().isAssignableFrom(Option.class)) {
                return Option.of(result);
            }

            if (property.getType() == Optional.class) {
                return Optional.ofNullable(result);
            }

            if (result != null) {
                return result;
            }

            if (property.getAnnotation(javax.annotation.Nullable.class) != null) {
                return null;
            }

            throw new NullPointerException("Illegal null value at " + property);
        });
    }

}
