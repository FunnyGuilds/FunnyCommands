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

import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.data.Origin;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.inject.InjectorController;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

final class DynamicCommand extends Command {

    private final FunnyCommands funnyCommands;
    private final CommandsTree commandsTree;

    protected DynamicCommand(FunnyCommands funnyCommands, CommandsTree commandsTree, CommandInfo commandInfo) {
        super(commandInfo.getName(), commandInfo.getDescription(), commandInfo.getUsageMessage(), commandInfo.getAliases());
        this.funnyCommands = funnyCommands;
        this.commandsTree = commandsTree;
    }

    @Override
    public boolean execute(CommandSender commandSender, String alias, String[] arguments) {
        String[] normalizedArguments = CommandUtils.normalize(arguments);
        Origin origin = new Origin(commandSender, alias, normalizedArguments);

        String matched = commandsTree.getMetadata().getSimpleName();

        for (int index = 0; index < normalizedArguments.length; index++) {
            String preview = matched + normalizedArguments[index];
        }

        return invoke(commandsTree.getMetadata().getCommandMethod(), resources -> {
            resources.on(Origin.class).assignInstance(origin);
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, @Nullable Location location) throws IllegalArgumentException {
        return Collections.emptyList();
    }

    private <T> T invoke(Method method, InjectorController controller) {
        try {
            return funnyCommands.getInjector()
                    .fork(controller)
                    .invokeMethod(method, commandsTree.getMetadata().getCommandInstance());
        } catch (Throwable throwable) {
            throw new FunnyCommandsException("Failed to invoke method " + method, throwable);
        }
    }

}
