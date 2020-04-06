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

import net.dzikoysk.funnycommands.commands.CommandDataType;
import net.dzikoysk.funnycommands.commands.CommandTree;
import net.dzikoysk.funnycommands.commands.DynamicBind;
import net.dzikoysk.funnycommands.commands.ExceptionHandler;
import net.dzikoysk.funnycommands.commands.GlobalBind;
import net.dzikoysk.funnycommands.commands.Origin;
import net.dzikoysk.funnycommands.commands.ResponseHandler;
import net.dzikoysk.funnycommands.commands.TypeMapper;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.atteo.classindex.ClassIndex;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.annotations.AnnotationsScanner;
import org.panda_lang.utilities.annotations.monads.filters.JavaFilter;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.function.CachedSupplier;
import org.panda_lang.utilities.commons.function.TriFunction;
import org.panda_lang.utilities.inject.InjectorResources;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
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
    protected final Collection<Consumer<InjectorResources>> globalBinds = new ArrayList<>();
    protected final Collection<BiConsumer<Origin, InjectorResources>> dynamicBinds = new ArrayList<>();
    protected final Map<Class<? extends Exception>, Function<? extends Exception, Boolean>> exceptionHandlers = new HashMap<>();
    protected final Map<Class<?>, BiFunction<Origin, ?, Boolean>> responseHandlers = new HashMap<>();
    protected BiConsumer<Origin, String> permissionHandler;
    protected BiConsumer<CommandSender, CommandTree> usageHandler;

    FunnyCommandsConfiguration(Supplier<JavaPlugin> plugin) {
        this.plugin = new CachedSupplier<>(plugin);
    }

    public FunnyCommands create() {
        FunnyCommandsFactory factory = new FunnyCommandsFactory();
        return factory.createFunnyCommands(this);
    }

    public FunnyCommandsConfiguration registerAllComponents(Class<?> pluginClass) {
        Collection<Class<?>> components = AnnotationsScanner.configuration()
                .includeResources(FunnyCommandsUtils.getURL(pluginClass))
                .build()
                .createProcess()
                .addURLFilters(new JavaFilter())
                .fetch()
                .createSelector()
                .selectTypesAnnotatedWith(FunnyComponent.class);

        return registerComponents(components);
    }

    public FunnyCommandsConfiguration registerProcessedComponents() {
        return registerComponents(ClassIndex.getAnnotated(FunnyComponent.class, FunnyCommands.class.getClassLoader()));
    }

    private FunnyCommandsConfiguration registerComponents(Iterable<Class<?>> components) {
        for (Class<?> componentClass : components) {
            try {
                Constructor<?> constructor = componentClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                registerComponent(constructor.newInstance());
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new FunnyCommandsException("Cannot create component " + componentClass, e);
            }
        }

        return this;
    }

    private <T> FunnyCommandsConfiguration registerComponent(Object componentInstance) {
        Class<?> componentType = componentInstance.getClass();

        if (CommandDataType.class.isAssignableFrom(componentType)) {
            return type(ObjectUtils.cast(componentInstance));
        }

        if (GlobalBind.class.isAssignableFrom(componentType)) {
            return globalBind(ObjectUtils.cast(componentInstance));
        }

        if (DynamicBind.class.isAssignableFrom(componentType)) {
            return dynamicBind(ObjectUtils.cast(componentInstance));
        }

        if (ResponseHandler.class.isAssignableFrom(componentType)) {
            return responseHandler(ObjectUtils.cast(componentInstance));
        }

        if (ExceptionHandler.class.isAssignableFrom(componentType))  {
            return exceptionHandler(ObjectUtils.cast(componentInstance));
        }

        return command(componentInstance);
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

    public <T> FunnyCommandsConfiguration type(CommandDataType<T> commandDataType) {
        return type(commandDataType.getName(), commandDataType);
    }

    public <T> FunnyCommandsConfiguration type(String typeName, TriFunction<Origin, Parameter, String, T> deserializer) {
        this.typeMappers.put(typeName, new TypeMapper<>(typeName, deserializer));
        return this;
    }

    public FunnyCommandsConfiguration globalBind(Consumer<InjectorResources> bind) {
        this.globalBinds.add(bind);
        return this;
    }

    public FunnyCommandsConfiguration dynamicBind(BiConsumer<Origin, InjectorResources> bind) {
        this.dynamicBinds.add(bind);
        return this;
    }

    public <E extends Exception> FunnyCommandsConfiguration exceptionHandler(ExceptionHandler<E> exceptionHandler) {
        return exceptionHandler(exceptionHandler.getExceptionType(), exceptionHandler);
    }

    public <E extends Exception> FunnyCommandsConfiguration exceptionHandler(Class<E> exceptionType, Function<E, Boolean> exceptionConsumer) {
        this.exceptionHandlers.put(exceptionType, exceptionConsumer);
        return this;
    }

    public <R> FunnyCommandsConfiguration responseHandler(ResponseHandler<R> responseHandler) {
        return responseHandler(responseHandler.getResponseType(), responseHandler);
    }

    public <R> FunnyCommandsConfiguration responseHandler(Class<R> responseType, BiFunction<Origin, R, Boolean> responseHandler) {
        this.responseHandlers.put(responseType, responseHandler);
        return this;
    }

    public FunnyCommandsConfiguration permissionHandler(BiConsumer<Origin, String> permissionHandler) {
        this.permissionHandler = permissionHandler;
        return this;
    }

    public FunnyCommandsConfiguration usageHandler(BiConsumer<CommandSender, CommandTree> usageHandler) {
        this.usageHandler = usageHandler;
        return this;
    }

}
