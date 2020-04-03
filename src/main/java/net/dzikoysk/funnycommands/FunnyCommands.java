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
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.inject.Injector;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FunnyCommands {

    private final CommandsLoader commandsLoader;
    private final Map<String, Function<String, String>> placeholders;
    private final Injector injector;

    FunnyCommands(FunnyCommandsConfiguration configuration, Injector injector) {
        this.commandsLoader = new CommandsLoader(this, configuration.plugin);
        this.placeholders = configuration.placeholders;
        this.injector = injector;
    }

    public void dispose() {
        commandsLoader.unloadCommands();
    }

    public Map<? extends String, ? extends Function<String, String>> getPlaceholders() {
        return placeholders;
    }

    public CommandsLoader getCommandsLoader() {
        return commandsLoader;
    }

    public Injector getInjector() {
        return injector;
    }

    public static FunnyCommandsConfiguration configuration(Supplier<JavaPlugin> plugin) {
        return new FunnyCommandsConfiguration(plugin);
    }

}
