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
import net.dzikoysk.funnycommands.stereotypes.Arg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.inject.InjectorController;
import org.panda_lang.utilities.inject.InjectorException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

final class DynamicCommand extends Command {

    private final FunnyCommands funnyCommands;
    private final CommandTree commandsTree;

    protected DynamicCommand(FunnyCommands funnyCommands, CommandTree commandsTree, CommandInfo commandInfo) {
        super(commandInfo.getName(), commandInfo.getDescription(), commandInfo.getUsageMessage(), new ArrayList<>(commandInfo.getAliases()));
        this.funnyCommands = funnyCommands;
        this.commandsTree = commandsTree;
    }

    @Override
    public boolean execute(CommandSender commandSender, String alias, String[] arguments) {
        String[] normalizedArguments = CommandUtils.normalize(arguments);
        String matched = commandsTree.getMetadata().getSimpleName();
        List<CommandTree> matchedTree = Collections.singletonList(commandsTree);
        int index = 0;

        for (; index < normalizedArguments.length; index++) {
            String preview = matched + normalizedArguments[index];
            List<CommandTree> previewTree = commandsTree.collectCommandsStartingWith(preview);

            if (previewTree.isEmpty()) {
                break;
            }

            matchedTree = previewTree;
        }

        if (matchedTree.isEmpty()) {
            funnyCommands.getUsageHandler().accept(commandSender, commandsTree);
            return true;
        }

        if (matchedTree.size() > 1) {
            // should never happen?
            throw new FunnyCommandsException("Commands conflict: " + matchedTree.toString());
        }

        CommandTree commandTree = matchedTree.get(0);
        CommandInfo command = commandTree.getMetadata().getCommandInfo();

        String[] commandArguments = Arrays.copyOfRange(normalizedArguments, index, normalizedArguments.length);
        Origin origin = new Origin(funnyCommands, commandSender, alias, commandArguments);
        String permission = commandTree.getMetadata().getCommandInfo().getPermission();

        if (!commandSender.hasPermission(permission)) {
            funnyCommands.getPermissionHandler().accept(origin, permission);
            return true;
        }

        if (command.getParameters().size() != commandArguments.length) {
            funnyCommands.getUsageHandler().accept(commandSender, commandTree);
            return true;
        }

        Object result = invoke(commandsTree.getMetadata().getCommandMethod(), resources -> {
            resources.on(Origin.class).assignInstance(origin);
            resources.annotatedWith(Arg.class).assignHandler(new ArgumentsHandler(command, origin));

            funnyCommands.getDynamicBinds().forEach(bind -> {
                bind.accept(origin, resources);
            });
        });

        if (result == null) {
            funnyCommands.getUsageHandler().accept(commandSender, commandTree);
            return true;
        }

        BiFunction<Origin, Object, Boolean> handler = ObjectUtils.cast(funnyCommands.getResponseHandlers().get(result.getClass()));

        if (handler == null) {
            throw new FunnyCommandsException("Missing response handler for " + result.getClass());
        }

        Boolean success = handler.apply(origin, result);

        if (success == null || !success) {
            funnyCommands.getUsageHandler().accept(commandSender, commandTree);
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return super.tabComplete(sender, alias, args);
    }

    private <T> T invoke(Method method, InjectorController controller) {
        try {
            return funnyCommands.getInjector()
                    .fork(controller)
                    .invokeMethod(method, commandsTree.getMetadata().getCommandInstance());
        } catch (InjectorException e) {
            throw new FunnyCommandsException("Lack of resources to invoke command method " + e.getMessage(), e);
        } catch (Throwable throwable) {
            throw new FunnyCommandsException("Failed to invoke method " + method, throwable);
        }
    }

}
