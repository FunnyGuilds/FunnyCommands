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

import net.dzikoysk.funnycommands.data.Origin;
import net.dzikoysk.funnycommands.mappers.TypeMapper;
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.commons.function.CachedSupplier;
import org.panda_lang.utilities.inject.InjectorResources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FunnyCommandsConfiguration {

    protected final Supplier<JavaPlugin> plugin;
    protected final Map<String, Function<String, String>> placeholders = new HashMap<>();
    protected final Collection<Class<?>> commandsClasses = new ArrayList<>();
    protected final Collection<Object> commandsInstances = new ArrayList<>();
    protected final Map<String, TypeMapper<?>> typeMappers = new HashMap<>();
    protected final Collection<Consumer<InjectorResources>> binds = new ArrayList<>();
    protected final Map<Class<? extends Exception>, Function<? extends Exception, Boolean>> exceptionHandlers = new HashMap<>();
    protected final Map<Class<?>, BiFunction<Origin, ?, Boolean>> responseHandlers = new HashMap<>();

    FunnyCommandsConfiguration(Supplier<JavaPlugin> plugin) {
        this.plugin = new CachedSupplier<>(plugin);
    }

    public FunnyCommands create() {
        FunnyCommandsFactory factory = new FunnyCommandsFactory();
        return factory.createFunnyCommands(this);
    }

    public FunnyCommandsConfiguration placeholders(Map<String, Function<String, String>> placeholders) {
        this.placeholders.putAll(placeholders);
        return this;
    }

    public FunnyCommandsConfiguration commands(Collection<? extends Class<?>> commandsClasses) {
        this.commandsClasses.addAll(commandsClasses);
        return this;
    }

    public FunnyCommandsConfiguration command(Object command) {
        this.commandsInstances.add(command);
        return this;
    }

    public FunnyCommandsConfiguration commands(Class<?>... commandsClasses) {
        return commands(Arrays.asList(commandsClasses));
    }

    public <T> FunnyCommandsConfiguration type(String typeName, Class<T> typeClass, Function<String, T> deserializer) {
        this.typeMappers.put(typeName, new TypeMapper<>(typeName, typeClass, deserializer));
        return this;
    }

    public FunnyCommandsConfiguration bind(Consumer<InjectorResources> resourcesConsumer) {
        this.binds.add(resourcesConsumer);
        return this;
    }

    public <E extends Exception> FunnyCommandsConfiguration exceptionHandler(Class<E> exceptionType, Function<E, Boolean> exceptionConsumer) {
        this.exceptionHandlers.put(exceptionType, exceptionConsumer);
        return this;
    }

    public <R> FunnyCommandsConfiguration responseHandler(Class<R> responseType, BiFunction<Origin, R, Boolean> responseHandler) {
        this.responseHandlers.put(responseType, responseHandler);
        return this;
    }

}