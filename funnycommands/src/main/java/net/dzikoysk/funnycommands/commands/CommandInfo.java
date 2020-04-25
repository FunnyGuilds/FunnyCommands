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

import io.vavr.collection.Stream;
import net.dzikoysk.funnycommands.resources.types.TypeMapper;

import java.util.List;
import java.util.Map;

public final class CommandInfo {

    private final String name;
    private final String description;
    private final String permission;
    private final String usageMessage;
    private final List<String> aliases;
    private final List<CustomizedCompleter> completers;
    private final Map<String, CommandParameter> parameters;
    private final Map<String, TypeMapper<?>> mappers;
    private final boolean async;
    private final boolean varargs;

    CommandInfo(
            String name, String description, String permission, String usageMessage, List<String> aliases,
            List<CustomizedCompleter> completers, Map<String, CommandParameter> parameters, Map<String, TypeMapper<?>> mappers,
            boolean async, boolean varargs
    ) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.usageMessage = usageMessage;
        this.aliases = aliases;
        this.completers = completers;
        this.parameters = parameters;
        this.mappers = mappers;
        this.async = async;
        this.varargs = varargs;
    }

    public boolean isVarargs() {
        return varargs;
    }

    public boolean isAsync() {
        return async;
    }

    public int getAmountOfRequiredParameters() {
        return Stream.ofAll(getParameters().values())
                .filterNot(CommandParameter::isOptional)
                .length();
    }

    public Map<? extends String, ? extends CommandParameter> getParameters() {
        return parameters;
    }

    public Map<? extends String, ? extends TypeMapper<?>> getMappers() {
        return mappers;
    }

    public List<? extends CustomizedCompleter> getCompleters() {
        return completers;
    }

    public List<? extends String> getAliases() {
        return aliases;
    }

    public String getUsageMessage() {
        return usageMessage;
    }

    public String getPermission() {
        return permission;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

}
