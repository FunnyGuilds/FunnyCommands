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

package net.dzikoysk.funnycommands.data;

import net.dzikoysk.funnycommands.FunnyCommands;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public final class Origin {

    private final FunnyCommands funnyCommands;
    private final CommandSender commandSender;
    private final String alias;
    private final String[] args;

    public Origin(FunnyCommands funnyCommands, CommandSender commandSender, String alias, String[] args) {
        this.funnyCommands = funnyCommands;
        this.commandSender = commandSender;
        this.alias = alias;
        this.args = args;
    }

    public String format(Object value) {
        return funnyCommands.getFormatter().format(Objects.toString(value));
    }

    public String[] getArgs() {
        return args;
    }

    public String getAlias() {
        return alias;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public FunnyCommands getFunnyCommands() {
        return funnyCommands;
    }

}
