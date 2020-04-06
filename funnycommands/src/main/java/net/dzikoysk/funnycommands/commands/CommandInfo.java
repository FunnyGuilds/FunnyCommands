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

import java.util.List;
import java.util.Map;

public final class CommandInfo {

    private final String name;
    private final String description;
    private final String permission;
    private final String usageMessage;
    private final List<String> aliases;
    private final Map<String, Integer> parameters;
    private final Map<String, TypeMapper<?>> mappers;

    CommandInfo(
            String name, String description, String permission, String usageMessage, List<String> aliases,
            Map<String, Integer> parameters, Map<String, TypeMapper<?>> mappers
    ) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.usageMessage = usageMessage;
        this.aliases = aliases;
        this.parameters = parameters;
        this.mappers = mappers;
    }

    public String getPermission() {
        return permission;
    }

    public Map<String, Integer> getParameters() {
        return parameters;
    }

    public Map<? extends String, ? extends TypeMapper<?>> getMappers() {
        return mappers;
    }

    public List<? extends String> getAliases() {
        return aliases;
    }

    public String getUsageMessage() {
        return usageMessage;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

}
