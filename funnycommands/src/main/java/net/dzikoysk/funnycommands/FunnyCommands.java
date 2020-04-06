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

import net.dzikoysk.funnycommands.commands.CommandStructure;
import net.dzikoysk.funnycommands.commands.CommandsLoader;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.types.TypeMapper;
import net.dzikoysk.funnycommands.resources.Origin;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.commons.text.MessageFormatter;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.InjectorResources;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FunnyCommands {

    private final FunnyCommandsConfiguration configuration;
    private final CommandsLoader commandsLoader;
    private final MessageFormatter formatter;
    private final Injector injector;
    private final BiConsumer<Origin, String> permissionHandler;
    private final BiConsumer<CommandSender, CommandStructure> usageHandler;

    FunnyCommands(FunnyCommandsConfiguration configuration, Injector injector, MessageFormatter formatter, BiConsumer<Origin, String> permissionHandler, BiConsumer<CommandSender, CommandStructure> usageHandler) {
        this.injector = injector;
        this.formatter = formatter;
        this.configuration = configuration;
        this.permissionHandler = permissionHandler;
        this.usageHandler = usageHandler;
        this.commandsLoader = new CommandsLoader(this, configuration.plugin);
    }

    public void dispose() {
        commandsLoader.unloadCommands();
    }

    public BiConsumer<CommandSender, CommandStructure> getUsageHandler() {
        return usageHandler;
    }

    public BiConsumer<Origin, String> getPermissionHandler() {
        return permissionHandler;
    }

    public Map<? extends Class<?>, ? extends BiFunction<Origin, ?, Boolean>> getResponseHandlers() {
        return configuration.responseHandlers;
    }

    public Collection<? extends BiConsumer<Origin, InjectorResources>> getDynamicBinds() {
        return configuration.dynamicBinds;
    }

    public Map<? extends String, ? extends Function<String, String>> getPlaceholders() {
        return configuration.placeholders;
    }

    public Map<? extends String, ? extends Completer> getCompleters() {
        return configuration.completers;
    }

    public Map<? extends String, ? extends TypeMapper<?>> getTypeMappers() {
        return configuration.typeMappers;
    }

    public CommandsLoader getCommandsLoader() {
        return commandsLoader;
    }

    public MessageFormatter getFormatter() {
        return formatter;
    }

    public Injector getInjector() {
        return injector;
    }

    public static FunnyCommandsConfiguration configuration(Supplier<JavaPlugin> plugin) {
        return new FunnyCommandsConfiguration(plugin);
    }

}
