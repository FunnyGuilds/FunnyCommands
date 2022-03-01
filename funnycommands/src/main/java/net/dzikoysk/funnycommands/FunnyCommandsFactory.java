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

import net.dzikoysk.funnycommands.commands.CommandInfo;
import net.dzikoysk.funnycommands.commands.CommandStructure;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.ValidationException;
import net.dzikoysk.funnycommands.resources.responses.BooleanResponseHandler;
import net.dzikoysk.funnycommands.resources.types.StringType;
import org.bukkit.command.CommandSender;
import org.panda_lang.utilities.inject.Injector;
import panda.std.Option;
import panda.utilities.ObjectUtils;
import panda.utilities.text.Formatter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;

final class FunnyCommandsFactory {

    FunnyCommands createFunnyCommands(FunnyCommandsConfiguration configuration) {
        Formatter formatter = new Formatter(message -> Option.when(message.contains("${"), () -> new ValidationException("Message '" + message + "' contains unresolved placeholders")));

        configuration.placeholders.forEach((key, value) -> {
            formatter.register("${" + key + "}", () -> value.apply(key));
        });

        Injector injector = configuration.injector;
        configuration.binds.forEach(bind -> bind.accept(injector.getResources()));

        configuration.validators.forEach(validator -> {
            if (validator.getType() == null && validator.getAnnotation() == null) {
                throw new IllegalStateException("Invalid validator configuration - you have to associate at least a type or annotation");
            }

            injector.getResources().processAnnotatedType(validator.getAnnotation(), validator.getType(), (annotation, parameter, value, injectorArgs) -> {
                boolean success = validator.validate(CommandUtils.getContext(injectorArgs), ObjectUtils.cast(annotation), parameter, ObjectUtils.cast(value));

                if (!success) {
                    throw new ValidationException();
                }

                return value;
            });
        });

        Collection<Object> commands = new ArrayList<>(configuration.commandsInstances);

        for (Class<?> commandClass : configuration.commandsClasses) {
            try {
                commands.add(injector.newInstanceWithFields(commandClass));
            } catch (Throwable throwable) {
                throw new FunnyCommandsException("Failed to instantiate command class " + commandClass, throwable);
            }
        }

        if (!configuration.typeMappers.containsKey("string")) {
            configuration.type(new StringType());
        }

        if (!configuration.responseHandlers.containsKey(Boolean.class)) {
            configuration.responseHandler(new BooleanResponseHandler());
        }

        BiConsumer<Context, String> permissionHandler = configuration.permissionHandler
                .orElseGet((context, permission) -> context.getCommandSender().sendMessage(FunnyCommandsUtils.translate("&cYou don't have permission to perform that command")));

        BiConsumer<CommandSender, CommandStructure> usageHandler = configuration.usageHandler
                .orElseGet((sender, commandTree) -> {
                    CommandInfo commandInfo = commandTree.getMetadata().getCommandInfo();
                    String usageMessage = commandInfo.getUsageMessage();

                    if (usageMessage.isEmpty()) {
                        usageMessage = FunnyCommandsUtils.formatUsage(commandInfo.getName(), commandInfo.getParameters().values());
                    }

                    sender.sendMessage(FunnyCommandsUtils.translate(usageMessage));
                });

        FunnyCommands funnyCommands = new FunnyCommands(configuration, injector, formatter, permissionHandler, usageHandler);
        funnyCommands.getCommandsLoader().registerCommands(commands);

        return funnyCommands;
    }

}
