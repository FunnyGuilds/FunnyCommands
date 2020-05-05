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

import net.dzikoysk.funnycommands.FunnyCommandsException;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.function.Lazy;
import org.panda_lang.utilities.commons.function.ThrowingSupplier;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

final class CommandMapInjector {

    private final Supplier<JavaPlugin> plugin;
    private final Map<String, DynamicCommand> registeredCommands = new HashMap<>();
    private final Map<String, HelpTopic> registeredTopics = new HashMap<>();

    CommandMapInjector(Supplier<JavaPlugin> plugin) {
        this.plugin = new Lazy<>(plugin);
    }

    protected DynamicCommand register(DynamicCommand command) {
        fetchCommandMap().register(plugin.get().getName(), command);
        registeredCommands.put(command.getName(), command);

        HelpTopic helpTopic = new GenericCommandHelpTopic(command);
        fetchHelpTopicsMap().put(helpTopic.getName(), helpTopic);
        registeredTopics.put(helpTopic.getName(), helpTopic);

        return command;
    }

    protected void unregister() {
        Map<String, Command> knownCommands = fetchKnownCommandMap();
        registeredCommands.keySet().forEach(knownCommands::remove);

        Map<String, HelpTopic> helpTopics = fetchHelpTopicsMap();
        registeredTopics.forEach(helpTopics::remove);
    }

    private CommandMap fetchCommandMap() {
        return fetch(() -> {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            return (CommandMap) commandMapField.get(getServer());
        });
    }

    private Map<String, Command> fetchKnownCommandMap() {
        return fetch(() -> {
            CommandMap commandMap = fetchCommandMap();

            Field knownCommandMapField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandMapField.setAccessible(true);

            return ObjectUtils.cast(knownCommandMapField.get(commandMap));
        });
    }

    private TreeMap<String, HelpTopic> fetchHelpTopicsMap() {
        return fetch(() -> {
            HelpMap helpMap = getServer().getHelpMap();

            Field helpTopicsField = helpMap.getClass().getDeclaredField("helpTopics");
            helpTopicsField.setAccessible(true);

            return ObjectUtils.cast(helpTopicsField.get(helpMap));
        });
    }

    private <T> T fetch(ThrowingSupplier<T, ReflectiveOperationException> supplier) {
        try {
            return supplier.get();
        } catch (ReflectiveOperationException e) {
            throw new FunnyCommandsException("Unsupported server engine", e);
        }
    }

    private Server getServer() {
        return plugin.get().getServer();
    }

}
