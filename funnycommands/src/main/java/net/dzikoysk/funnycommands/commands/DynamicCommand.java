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

import io.vavr.control.Option;
import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.resources.binds.ArgumentsBind;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
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
    private final CommandStructure root;

    protected DynamicCommand(FunnyCommands funnyCommands, CommandStructure root, CommandInfo commandInfo) {
        super(commandInfo.getName(), commandInfo.getDescription(), commandInfo.getUsageMessage(), new ArrayList<>(commandInfo.getAliases()));
        this.funnyCommands = funnyCommands;
        this.root = root;
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] arguments) {
        Option<Origin> originValue = fetchOrigin(sender, alias, arguments);

        if (!originValue.isDefined()) {
            funnyCommands.getUsageHandler().accept(sender, root);
            return true;
        }

        Origin origin = originValue.get();
        CommandStructure matchedCommand = origin.getCommandStructure();
        CommandInfo commandInfo = matchedCommand.getMetadata().getCommandInfo();

        if (!sender.hasPermission(commandInfo.getPermission())) {
            funnyCommands.getPermissionHandler().accept(origin, commandInfo.getPermission());
            return true;
        }

        if (commandInfo.getParameters().size() != origin.getArguments().length) {
            funnyCommands.getUsageHandler().accept(sender, matchedCommand);
            return true;
        }

        Object result = invoke(matchedCommand.getMetadata().getCommandMethod(), resources -> {
            resources.on(Origin.class).assignInstance(origin);
            resources.annotatedWith(Arg.class).assignHandler(new ArgumentsBind(commandInfo, origin));

            funnyCommands.getDynamicBinds().forEach(bind -> {
                bind.accept(origin, resources);
            });
        });

        if (result == null) {
            funnyCommands.getUsageHandler().accept(sender, matchedCommand);
            return true;
        }

        BiFunction<Origin, Object, Boolean> handler = ObjectUtils.cast(funnyCommands.getResponseHandlers().get(result.getClass()));

        if (handler == null) {
            throw new FunnyCommandsException("Missing response handler for " + result.getClass());
        }

        Boolean success = handler.apply(origin, result);

        if (success == null || !success) {
            funnyCommands.getUsageHandler().accept(sender, matchedCommand);
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] arguments) throws IllegalArgumentException {
        if (root.getMetadata().getTabCompleteMethod().isDefined()) {
            return invoke(root.getMetadata().getTabCompleteMethod().get(), resources -> {});
        }

        Option<Origin> originValue = fetchOrigin(sender, alias, arguments);

        if (!originValue.isDefined()) {
            List<String> names = root.getNames();
            return arguments.length == 0 ? names : StringUtil.copyPartialMatches(arguments[arguments.length - 1], names, new ArrayList<>());
        }

        Origin origin = originValue.get();
        CommandInfo commandInfo = origin.getCommandStructure().getMetadata().getCommandInfo();

        String[] normalizedArguments = origin.getArguments();
        int lastArgument = normalizedArguments.length - 1;

        if (normalizedArguments.length > commandInfo.getCompleters().size()) {
            return Collections.emptyList();
        }

        CustomizedCompleter completer = commandInfo.getCompleters().get(lastArgument);
        return completer.apply(origin, normalizedArguments[lastArgument]);
    }

    private Option<Origin> fetchOrigin(CommandSender commandSender, String alias, String[] arguments) {
        String[] normalizedArguments = CommandUtils.normalize(arguments);
        String matched = root.getMetadata().getSimpleName();
        List<CommandStructure> matchedTree = Collections.singletonList(root);
        int index = 0;

        for (; index < normalizedArguments.length; index++) {
            String preview = matched + normalizedArguments[index];
            List<CommandStructure> previewTree = root.collectCommandsStartingWith(preview);

            if (previewTree.isEmpty()) {
                break;
            }

            matchedTree = previewTree;
        }

        if (matchedTree.isEmpty()) {
            funnyCommands.getUsageHandler().accept(commandSender, root);
            return Option.none();
        }

        if (matchedTree.size() > 1) {
            // should never happen?
            throw new FunnyCommandsException("Commands conflict: " + matchedTree.toString());
        }

        CommandStructure commandStructure = matchedTree.get(0);
        String[] commandArguments = Arrays.copyOfRange(normalizedArguments, index, normalizedArguments.length);
        Origin origin = new Origin(funnyCommands, commandSender, commandStructure, alias, commandArguments);

        return Option.of(origin);
    }

    private <T> T invoke(Method method, InjectorController controller) {
        try {
            return funnyCommands.getInjector()
                    .fork(controller)
                    .invokeMethod(method, root.getMetadata().getCommandInstance());
        } catch (InjectorException e) {
            throw new FunnyCommandsException("Lack of resources to invoke command method " + e.getMessage(), e);
        } catch (Throwable throwable) {
            throw new FunnyCommandsException("Failed to invoke method " + method, throwable);
        }
    }

}
