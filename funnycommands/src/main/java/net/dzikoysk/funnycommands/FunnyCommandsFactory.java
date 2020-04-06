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

package net.dzikoysk.funnycommands;

import net.dzikoysk.funnycommands.commands.CommandTree;
import net.dzikoysk.funnycommands.commands.Origin;
import net.dzikoysk.funnycommands.defaults.BooleanResponseHandler;
import net.dzikoysk.funnycommands.defaults.StringBind;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.commons.text.MessageFormatter;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.InjectorException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

final class FunnyCommandsFactory {

    protected FunnyCommands createFunnyCommands(FunnyCommandsConfiguration configuration) {
        MessageFormatter formatter = new MessageFormatter();

        configuration.placeholders.forEach((key, value) -> {
            formatter.register("${" + key + "}", () -> value.apply(key));
        });

        Injector injector = DependencyInjection.createInjector(resources -> {
            configuration.globalBinds.forEach(bind -> bind.accept(resources));
        });

        Collection<Object> commands = new ArrayList<>(configuration.commandsInstances);

        for (Class<?> commandClass : configuration.commandsClasses) {
            try {
                commands.add(injector.newInstance(commandClass));
            } catch (InstantiationException | InjectorException | InvocationTargetException | IllegalAccessException e) {
                throw new FunnyCommandsException("Failed to instantiate command class " + commandClass, e);
            }
        }

        if (!configuration.typeMappers.containsKey("string")) {
            configuration.type(new StringBind());
        }

        if (!configuration.responseHandlers.containsKey(Boolean.class)) {
            configuration.responseHandler(new BooleanResponseHandler());
        }

        @Nullable BiConsumer<Origin, String> permissionHandler = configuration.permissionHandler;

        if (permissionHandler == null) {
            permissionHandler = (origin, permission) -> origin.getCommandSender().sendMessage(FunnyCommandsUtils.translate("&cYou don't have permission to perform that command"));
        }

        @Nullable BiConsumer<CommandSender, CommandTree> usageHandler = configuration.usageHandler;

        if (usageHandler == null) {
            usageHandler = (sender, commandTree) -> sender.sendMessage(FunnyCommandsUtils.translate("&cUsage: " + commandTree.getMetadata().getCommandInfo().getUsageMessage()));
        }

        FunnyCommands funnyCommands = new FunnyCommands(configuration, injector, formatter, permissionHandler, usageHandler);
        funnyCommands.getCommandsLoader().registerCommands(commands);

        return funnyCommands;
    }

}
