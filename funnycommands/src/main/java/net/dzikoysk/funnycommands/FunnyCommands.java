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

import net.dzikoysk.funnycommands.commands.CommandsLoader;
import net.dzikoysk.funnycommands.commands.TypeMapper;
import net.dzikoysk.funnycommands.data.Origin;
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.commons.text.MessageFormatter;
import org.panda_lang.utilities.inject.Injector;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FunnyCommands {

    private final CommandsLoader commandsLoader;
    private final Map<String, Function<String, String>> placeholders;
    private final MessageFormatter formatter;
    private final Injector injector;
    private final Map<String, TypeMapper<?>> typeMappers;
    private final Map<Class<?>, BiFunction<Origin, ?, Boolean>> responseHandlers;

    FunnyCommands(FunnyCommandsConfiguration configuration, Injector injector, MessageFormatter formatter) {
        this.commandsLoader = new CommandsLoader(this, configuration.plugin);
        this.placeholders = configuration.placeholders;
        this.injector = injector;
        this.formatter = formatter;
        this.typeMappers = configuration.typeMappers;
        this.responseHandlers = configuration.responseHandlers;
    }

    public void dispose() {
        commandsLoader.unloadCommands();
    }

    public Map<? extends Class<?>, ? extends BiFunction<Origin, ?, Boolean>> getResponseHandlers() {
        return responseHandlers;
    }

    public Map<? extends String, ? extends Function<String, String>> getPlaceholders() {
        return placeholders;
    }

    public Map<? extends String, ? extends TypeMapper<?>> getTypeMappers() {
        return typeMappers;
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
