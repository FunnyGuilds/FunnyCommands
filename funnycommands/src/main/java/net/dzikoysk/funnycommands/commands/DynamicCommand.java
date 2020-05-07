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
import net.dzikoysk.funnycommands.resources.ValidationException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;
import org.panda_lang.utilities.commons.ArrayUtils;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.inject.DependencyInjectionException;
import org.panda_lang.utilities.inject.MethodInjector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

final class DynamicCommand extends Command {

    private final FunnyCommands funnyCommands;
    private final Supplier<JavaPlugin> plugin;
    private final CommandStructure root;

    protected DynamicCommand(FunnyCommands funnyCommands, Supplier<JavaPlugin> plugin, CommandStructure root, CommandInfo commandInfo) {
        super(commandInfo.getName(), commandInfo.getDescription(), commandInfo.getUsageMessage(), new ArrayList<>(commandInfo.getAliases()));
        this.funnyCommands = funnyCommands;
        this.plugin = plugin;
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

        if (commandInfo.isAsync()) {
            sender.getServer().getScheduler().runTaskAsynchronously(plugin.get(), () -> execute(sender, origin, matchedCommand, commandInfo));
            return true;
        }

        execute(sender, origin, matchedCommand, commandInfo);
        return true;
    }

    private void execute(CommandSender sender, Origin origin, CommandStructure matchedCommand, CommandInfo commandInfo) {
        if (!commandInfo.getPermission().isEmpty() && !sender.hasPermission(commandInfo.getPermission())) {
            funnyCommands.getPermissionHandler().accept(origin, commandInfo.getPermission());
            return;
        }

        int argumentsCount = origin.getArguments().length;

        if (argumentsCount < commandInfo.getAmountOfRequiredParameters()) {
            funnyCommands.getUsageHandler().accept(sender, matchedCommand);
            return;
        }

        CommandMetadata metadata = matchedCommand.getMetadata();
        boolean varargs = metadata.getCommandInfo().isVarargs();

        if (!varargs && (argumentsCount > commandInfo.getParameters().size())) {
            funnyCommands.getUsageHandler().accept(sender, matchedCommand);
            return;
        }

        Object result;

        try {
            result = invoke(metadata, metadata.getCommandMethod(), origin);
        } catch (ValidationException e) {
            return;
        } catch (Throwable e) {
            throw new FunnyCommandsException("Cannot invoke command", e);
        }

        if (metadata.getCommandMethod().getMethod().getReturnType() == void.class) {
            return;
        }

        if (result == null) {
            funnyCommands.getUsageHandler().accept(sender, matchedCommand);
            return;
        }

        BiFunction<Origin, Object, Boolean> handler = ObjectUtils.cast(funnyCommands.getResponseHandlers().get(result.getClass()));

        if (handler == null) {
            throw new FunnyCommandsException("Missing response handler for " + result.getClass());
        }

        Boolean success = handler.apply(origin, result);

        if (success == null || !success) {
            funnyCommands.getUsageHandler().accept(sender, matchedCommand);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] arguments) throws IllegalArgumentException {
        // custom tab complete
        if (root.getMetadata().getTabCompleteMethod().isDefined()) {
            try {
                return invoke(root.getMetadata(), root.getMetadata().getTabCompleteMethod().get(), null);
            } catch (Throwable e) {
                throw new FunnyCommandsException("Cannot invoke command", e);
            }
        }

        Option<Origin> subcommandOrigin = fetchOrigin(sender, alias, arguments);

        // list subcommands for root request
        if (subcommandOrigin.isEmpty()) {
            List<String> names = root.getSubcommandsNames();
            return arguments.length == 0 ? names : StringUtil.copyPartialMatches(arguments[arguments.length - 1], names, new ArrayList<>());
        }

        Origin origin = subcommandOrigin.get();
        String[] normalizedArguments = origin.getArguments();

        if (origin.getCommandStructure().equals(root) && normalizedArguments.length == 1) {
            ArrayList<String> subcommands = StringUtil.copyPartialMatches(normalizedArguments[0], origin.getCommandStructure().getSubcommandsNames(), new ArrayList<>());

            if (!subcommands.isEmpty()) {
                return subcommands;
            }
        }

        CommandInfo commandInfo = origin.getCommandStructure().getMetadata().getCommandInfo();

        // skip undefined completions
        if (commandInfo.getCompleters().isEmpty()) {
            return Collections.emptyList();
        }
        // System.out.println("|" + ContentJoiner.on(",").join(normalizedArguments) + "|");

        // skip completion for exceeded arguments
        if (normalizedArguments.length > commandInfo.getCompleters().size()) {
            return Collections.emptyList();
        }

        int completerIndex = normalizedArguments.length - 1;

        if (completerIndex == -1) {
            completerIndex = 0;
        }

        CustomizedCompleter completer = commandInfo.getCompleters().get(completerIndex);

        // edge case to handle empty args array
        if (ArrayUtils.isEmpty(normalizedArguments)) {
            return completer.apply(origin, StringUtils.EMPTY);
        }

        return completer.apply(origin, normalizedArguments[completerIndex]);
    }

    private Option<Origin> fetchOrigin(CommandSender commandSender, String alias, String[] arguments) {
        String[] normalizedArguments = CommandUtils.normalize(arguments);
        CommandStructure commandStructure = root;
        int index = 0;

        for (; index < normalizedArguments.length; index++) {
            Option<CommandStructure> nextStructure = commandStructure.getSubcommandStructure(normalizedArguments[index]);

            if (nextStructure.isEmpty()) {
                break;
            }

            commandStructure = nextStructure.get();
        }

        String[] commandArguments = Arrays.copyOfRange(normalizedArguments, index, normalizedArguments.length);
        Origin origin = new Origin(funnyCommands, commandSender, commandStructure, alias, commandArguments);

        return Option.of(origin);
    }

    private <T> T invoke(CommandMetadata metadata, MethodInjector method, Origin origin) throws Throwable {
        try {
            return method.invoke(metadata.getCommandInstance(), metadata.getCommandInfo(), origin);
        } catch (DependencyInjectionException e) {
            throw new FunnyCommandsException("Dependency Injection failed due to: " + e.getMessage(), e);
        }
    }

}
