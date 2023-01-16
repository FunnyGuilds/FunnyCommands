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
import net.dzikoysk.funnycommands.resources.DetailedExceptionHandler;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.resources.types.TypeMapper;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import panda.utilities.text.Formatter;
import org.panda_lang.utilities.inject.Injector;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FunnyCommands {

    private final FunnyCommandsConfiguration configuration;
    private final CommandsLoader commandsLoader;
    private final Formatter formatter;
    private final Injector injector;
    private final BiConsumer<Context, String> permissionHandler;
    private final BiConsumer<CommandSender, CommandStructure> usageHandler;

    FunnyCommands(FunnyCommandsConfiguration configuration, Injector injector, Formatter formatter, BiConsumer<Context, String> permissionHandler, BiConsumer<CommandSender, CommandStructure> usageHandler) {
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

    public BiConsumer<Context, String> getPermissionHandler() {
        return permissionHandler;
    }

    public Map<Class<? extends Exception>, DetailedExceptionHandler<? extends Exception>> getExceptionHandlers() {
        return configuration.exceptionHandlers;
    }

    public Map<? extends Class<?>, ? extends BiFunction<Context, ?, Boolean>> getResponseHandlers() {
        return configuration.responseHandlers;
    }

    public Collection<? extends Validator<?, ?, ?>> getValidators() {
        return configuration.validators;
    }

    public Map<? extends String, ? extends Function<String, String>> getPlaceholders() {
        return configuration.placeholders;
    }

    public Map<? extends String, ? extends Completer> getCompletes() {
        return configuration.completes;
    }

    public Map<? extends String, ? extends TypeMapper<?>> getTypeMappers() {
        return configuration.typeMappers;
    }

    public CommandsLoader getCommandsLoader() {
        return commandsLoader;
    }

    public Formatter getFormatter() {
        return formatter;
    }

    public Injector getInjector() {
        return injector;
    }

    public static FunnyCommandsConfiguration configuration(Supplier<JavaPlugin> plugin) {
        return new FunnyCommandsConfiguration(plugin);
    }

}
