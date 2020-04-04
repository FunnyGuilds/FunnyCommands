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

final class CommandInfo {

    private final String name;
    private final String description;
    private final String usageMessage;
    private final List<String> aliases;
    private final List<String> arguments;

    CommandInfo(String name, String description, String usageMessage, List<String> aliases, List<String> arguments) {
        this.name = name;
        this.description = description;
        this.usageMessage = usageMessage;
        this.aliases = aliases;
        this.arguments = arguments;
    }

    List<String> getArguments() {
        return arguments;
    }

    List<String> getAliases() {
        return aliases;
    }

    String getUsageMessage() {
        return usageMessage;
    }

    String getDescription() {
        return description;
    }

    String getName() {
        return name;
    }

}
