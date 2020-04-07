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

package net.dzikoysk.funnycommands.resources;

import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.commands.CommandStructure;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public final class Origin {

    private final FunnyCommands funnyCommands;
    private final CommandSender commandSender;
    private final CommandStructure commandStructure;
    private final String alias;
    private final String[] arguments;

    public Origin(FunnyCommands funnyCommands, CommandSender commandSender, CommandStructure commandStructure, String alias, String[] arguments) {
        this.funnyCommands = funnyCommands;
        this.commandSender = commandSender;
        this.commandStructure = commandStructure;
        this.alias = alias;
        this.arguments = arguments;
    }

    public String format(Object value) {
        return FunnyCommandsUtils.translate(funnyCommands.getFormatter().format(Objects.toString(value)));
    }

    public String[] getArguments() {
        return arguments;
    }

    public String getAlias() {
        return alias;
    }

    public CommandStructure getCommandStructure() {
        return commandStructure;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public FunnyCommands getFunnyCommands() {
        return funnyCommands;
    }

}
