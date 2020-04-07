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

import io.vavr.collection.Stream;
import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.types.TypeMapper;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.commons.ReflectionUtils;
import org.panda_lang.utilities.commons.text.MessageFormatter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class CommandsLoader {

    private final FunnyCommands funnyCommands;
    private final CommandMapInjector commandMapInjector;

    public CommandsLoader(FunnyCommands funnyCommands, Supplier<JavaPlugin> plugin) {
        this.funnyCommands = funnyCommands;
        this.commandMapInjector = new CommandMapInjector(plugin);
    }

    public Collection<DynamicCommand> registerCommands(Iterable<Object> commands) {
        CommandStructure commandsTree = loadCommands(commands);
        Collection<DynamicCommand> dynamicCommands = new ArrayList<>(commandsTree.getSubcommands().size());

        for (CommandStructure commandStructure : commandsTree.getSubcommands()) {
            dynamicCommands.add(registerCommand(funnyCommands, commandStructure));
        }

        return dynamicCommands;
    }

    protected DynamicCommand registerCommand(FunnyCommands funnyCommands, CommandStructure commandStructure) {
        DynamicCommand dynamicCommand = new DynamicCommand(funnyCommands, commandStructure, commandStructure.getMetadata().getCommandInfo());
        return commandMapInjector.register(dynamicCommand);
    }

    protected CommandStructure loadCommands(Iterable<Object> commands) {
        List<CommandMetadata> metadata = Stream.ofAll(commands)
                .flatMap(this::mapCommandInstance)
                .sorted()
                .toJavaList();

        CommandStructure metadataTree = new CommandStructure(null);

        metadata.forEach(meta -> {
            String[] units = meta.getName().split(" ");
            CommandStructure parent = metadataTree;

            for (int index = 0; index < units.length - 1; index++) {
                String unit = units[index];

                parent = parent.getSubcommandStructure(unit).getOrElseThrow(() -> {
                    throw new FunnyCommandsException("Unknown command root '" + unit + "' of '" + meta.getName() + "'");
                });
            }

            parent.getSubcommandStructure(meta.getSimpleName()).peek(value -> {
                throw new FunnyCommandsException("Commands collision: " + meta.getName() + " with " + value.getMetadata().getName());
            });

            parent.add(meta);
        });

        return metadataTree;
    }

    private Collection<CommandMetadata> mapCommandInstance(Object command) {
        Set<Method> commandMethods = ReflectionUtils.getMethodsAnnotatedWith(command.getClass(), FunnyCommand.class);
        Collection<CommandMetadata> metadata = new ArrayList<>(commandMethods.size());

        for (Method commandMethod : commandMethods) {
            metadata.add(mapCommand(command, commandMethod));
        }

        return metadata;
    }

    private CommandMetadata mapCommand(Object commandInstance, Method commandMethod) {
        FunnyCommand funnyCommand = commandMethod.getAnnotation(FunnyCommand.class);
        MessageFormatter formatter = funnyCommands.getFormatter();
        List<String> parameters = CommandUtils.format(formatter, funnyCommand.parameters());

        CommandInfo bukkitCommandInfo = new CommandInfo(
                formatter.format(funnyCommand.name()),
                formatter.format(funnyCommand.description()),
                formatter.format(funnyCommand.permission()),
                formatter.format(funnyCommand.usage()),
                CommandUtils.format(formatter, funnyCommand.aliases()),
                mapCompleters(CommandUtils.format(formatter, funnyCommand.completer())),
                mapParameters(parameters),
                mapMappers(commandMethod, parameters)
        );

        return new CommandMetadata(commandInstance, bukkitCommandInfo, commandMethod, null);
    }

    private List<CustomizedCompleter> mapCompleters(Iterable<String> completersData) {
        List<CustomizedCompleter> mappedCompleters = new ArrayList<>();

        for (String completerData : completersData) {
            String[] elements = completerData.split(":");

            if (elements.length != 2) {
                throw new FunnyCommandsException("Invalid format of completer data: " + completerData);
            }

            Completer completer = funnyCommands.getCompleters().get(elements[0]);

            if (completer == null) {
                throw new FunnyCommandsException("Cannot find completer declared as " + completerData);
            }

            Integer limit = Integer.parseInt(elements[1]);
            mappedCompleters.add(((origin, prefix) -> completer.apply(origin, prefix, limit)));
        }
        
        return mappedCompleters;
    }

    private Map<String, Integer> mapParameters(List<String> parameters) {
        Map<String, Integer> parametersMappings = new HashMap<>(parameters.size());

        for (int index = 0; index < parameters.size(); index++) {
            String[] elements = parameters.get(index).split(":");

            if (elements.length != 2) {
                throw new FunnyCommandsException("Invalid format of parameter: " + parameters.get(index));
            }

            parametersMappings.put(elements[1], index);
        }

        return parametersMappings;
    }

    private Map<String, TypeMapper<?>> mapMappers(Method commandMethod, Iterable<String> parameters) {
        Map<String, TypeMapper<?>> mappers = new HashMap<>(commandMethod.getParameterCount());

        for (String parameter : parameters) {
            String[] elements = parameter.split(":");

            if (elements.length != 2) {
                throw new FunnyCommandsException("Invalid format of parameter: " + parameter);
            }

            String typeName = elements[0];
            @Nullable TypeMapper<?> typeMapper = funnyCommands.getTypeMappers().get(typeName);

            if (typeMapper == null) {
                throw new FunnyCommandsException("Unknown type " + typeName);
            }

            String parameterName = elements[1];

            if (mappers.containsKey(parameterName)) {
                throw new FunnyCommandsException("Duplicated parameter name: " + parameterName);
            }

            mappers.put(parameterName, typeMapper);
        }

        return mappers;
    }

    public void unloadCommands() {
        commandMapInjector.unregister();
    }

}
