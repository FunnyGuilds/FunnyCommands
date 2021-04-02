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
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.types.TypeMapper;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.commons.ReflectionUtils;
import org.panda_lang.utilities.commons.function.PandaStream;
import org.panda_lang.utilities.commons.text.Formatter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class CommandsLoader {

    private final FunnyCommands funnyCommands;
    private final CommandMapInjector commandMapInjector;
    private final Supplier<JavaPlugin> plugin;

    public CommandsLoader(FunnyCommands funnyCommands, Supplier<JavaPlugin> plugin) {
        this.funnyCommands = funnyCommands;
        this.commandMapInjector = new CommandMapInjector(plugin);
        this.plugin = plugin;
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
        DynamicCommand dynamicCommand = new DynamicCommand(funnyCommands, plugin, commandStructure, commandStructure.getMetadata().getCommandInfo());
        return commandMapInjector.register(dynamicCommand);
    }

    protected CommandStructure loadCommands(Iterable<Object> commands) {
        List<CommandMetadata> metadata = PandaStream.of(commands)
                .flatMap(this::mapCommandInstance)
                .sorted()
                .toList();

        CommandStructure metadataTree = new CommandStructure(null);

        metadata.forEach(meta -> {
            String[] units = meta.getName().split(" ");
            CommandStructure parent = metadataTree;

            for (int index = 0; index < units.length - 1; index++) {
                String unit = units[index];

                parent = parent.getSubcommandStructure(unit).orThrow(() -> {
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
            metadata.addAll(mapCommand(command, commandMethod));
        }

        return metadata;
    }

    private Collection<? extends CommandMetadata> mapCommand(Object commandInstance, Method commandMethod) {
        List<CommandMetadata> result = new ArrayList<>();
        FunnyCommand funnyCommand = commandMethod.getAnnotation(FunnyCommand.class);
        Formatter formatter = funnyCommands.getFormatter();

        List<String> parameters = CommandUtils.format(formatter, funnyCommand.parameters().split(" "));
        Map<String, CommandParameter> commandParameters = mapParameters(parameters);
        boolean varargs = false;

        if (!commandParameters.isEmpty()) {
            varargs = PandaStream.of(commandParameters.values())
                    .sorted()
                    .toStream()
                    .reduce((first, second) -> second)
                    .filter(CommandParameter::isVarargs)
                    .isPresent();
        }

        List<String> names = CommandUtils.format(formatter, funnyCommand.aliases());
        names.add(formatter.format(funnyCommand.name()));

        for (String name : names) {
            CommandInfo bukkitCommandInfo = new CommandInfo(
                    name,
                    formatter.format(funnyCommand.description()),
                    formatter.format(funnyCommand.permission()),
                    formatter.format(funnyCommand.usage()),
                    Collections.emptyList(),
                    mapCompletes(CommandUtils.format(formatter, funnyCommand.completer().split(" "))),
                    commandParameters,
                    mapMappers(commandMethod, parameters),
                    funnyCommand.playerOnly(),
                    funnyCommand.acceptsExceeded(),
                    funnyCommand.async(),
                    varargs
            );

            result.add(new CommandMetadata(commandInstance, bukkitCommandInfo, funnyCommands.getInjector().forMethod(commandMethod), null));
        }

        return result;
    }

    private List<CustomizedCompleter> mapCompletes(Iterable<String> completesData) {
        List<CustomizedCompleter> mappedCompletes = new ArrayList<>();

        for (String completerData : completesData) {
            String[] elements = completerData.split(":");
            Completer completer = funnyCommands.getCompletes().get(elements[0]);

            if (completer == null) {
                throw new FunnyCommandsException("Cannot find completer declared as " + completerData);
            }

            int limit = completerData.contains(":")
                    ? Integer.parseInt(elements[1])
                    : -1;

            mappedCompletes.add(((context, prefix) -> completer.apply(context, prefix, limit)));
        }
        
        return mappedCompletes;
    }

    private Map<String, CommandParameter> mapParameters(List<String> parameters) {
        Map<String, CommandParameter> parametersMappings = new LinkedHashMap<>(parameters.size());

        for (int index = 0; index < parameters.size(); index++) {
            String parameter = parameters.get(index);

            String mappedParameter = unmapOptional(parameter);
            boolean optional = mappedParameter.length() < parameter.length();
            parameter = mappedParameter;

            String[] elements = parameter.split(":");

            if (elements.length != 2) {
                throw new FunnyCommandsException("Invalid format of parameter: " + parameter);
            }

            String name = elements[1];
            boolean varargs = false;

            if (name.endsWith("...")) {
                varargs = true;
                name = name.substring(0, name.length() - 3);
            }

            parametersMappings.put(name, new CommandParameter(index, name, optional, varargs));
        }

        return parametersMappings;
    }

    private Map<String, TypeMapper<?>> mapMappers(Method commandMethod, Iterable<String> parameters) {
        Map<String, TypeMapper<?>> mappers = new HashMap<>(commandMethod.getParameterCount());

        for (String parameter : parameters) {
            String[] elements = unmapOptional(parameter).split(":");

            if (elements.length != 2) {
                throw new FunnyCommandsException("Invalid format of parameter: " + parameter);
            }

            String typeName = elements[0];
            @Nullable TypeMapper<?> typeMapper = funnyCommands.getTypeMappers().get(typeName);

            if (typeMapper == null) {
                throw new FunnyCommandsException("Unknown type " + typeName);
            }

            String parameterName = elements[1];

            if (parameterName.endsWith("...")) {
                parameterName = parameterName.substring(0, parameterName.length() - 3);
            }

            if (mappers.containsKey(parameterName)) {
                throw new FunnyCommandsException("Duplicated parameter name: " + parameterName);
            }

            mappers.put(parameterName, typeMapper);
        }

        return mappers;
    }

    private String unmapOptional(String parameter) {
        if (parameter.startsWith("[") || parameter.endsWith("]")) {
            if (!parameter.startsWith("[") || !parameter.endsWith("]")) {
                throw new FunnyCommandsException("Invalid format of optional parameter: " + parameter);
            }

            return parameter.substring(1, parameter.length() - 1);
        }

        return parameter;
    }

    public void unloadCommands() {
        commandMapInjector.unregister();
    }

}
