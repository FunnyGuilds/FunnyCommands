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

import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.DefaultResources;
import net.dzikoysk.funnycommands.resources.ExceptionHandler;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.resources.PermissionHandler;
import net.dzikoysk.funnycommands.resources.ResponseHandler;
import net.dzikoysk.funnycommands.resources.UsageHandler;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.resources.completers.CustomCompleter;
import net.dzikoysk.funnycommands.resources.exceptions.CustomExceptionHandler;
import net.dzikoysk.funnycommands.resources.responses.CustomResponseHandler;
import net.dzikoysk.funnycommands.resources.types.TypeMapper;
import net.dzikoysk.funnycommands.resources.validators.CustomValidator;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.function.Lazy;
import org.panda_lang.utilities.commons.function.ThrowingQuadFunction;
import org.panda_lang.utilities.commons.function.TriFunction;
import org.panda_lang.utilities.inject.InjectorProperty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FunnyCommandsConfiguration {

    protected final Supplier<JavaPlugin> plugin;
    protected final Map<String, Function<String, String>> placeholders = new HashMap<>();
    protected final Collection<Class<?>> commandsClasses = new ArrayList<>();
    protected final Collection<Object> commandsInstances = new ArrayList<>();
    protected final Map<String, Completer> completers = new HashMap<>();
    protected final Map<String, TypeMapper<?>> typeMappers = new HashMap<>();
    protected final Collection<Bind> binds = new ArrayList<>();
    protected final Collection<Validator<?, ?, ?>> validators = new ArrayList<>();
    protected final Map<Class<? extends Exception>, ExceptionHandler<? extends Exception>> exceptionHandlers = new HashMap<>();
    protected final Map<Class<?>, ResponseHandler<?>> responseHandlers = new HashMap<>();
    protected PermissionHandler permissionHandler;
    protected UsageHandler usageHandler;

    FunnyCommandsConfiguration(Supplier<JavaPlugin> plugin) {
        this.plugin = new Lazy<>(plugin);
    }

    public FunnyCommands hook() {
        FunnyCommandsFactory factory = new FunnyCommandsFactory();
        return factory.createFunnyCommands(this);
    }

    public FunnyCommandsConfiguration registerDefaultComponents() {
        return registerComponents(DefaultResources.ALL);
    }

    public FunnyCommandsConfiguration registerComponents(Object... components) {
        return registerComponents(Arrays.asList(components));
    }

    public FunnyCommandsConfiguration registerComponents(Iterable<? extends Object> components) {
        for (Object component : components) {
            registerComponent(component);
        }

        return this;
    }

    public FunnyCommandsConfiguration registerComponent(Object componentInstance) {
        Class<?> componentType = componentInstance.getClass();

        if (CommandDataType.class.isAssignableFrom(componentType)) {
            return type(ObjectUtils.cast(componentInstance));
        }

        if (Completer.class.isAssignableFrom(componentType)) {
            return completer(ObjectUtils.cast(componentInstance));
        }

        if (Bind.class.isAssignableFrom(componentType)) {
            return bind(ObjectUtils.cast(componentInstance));
        }

        if (Validator.class.isAssignableFrom(componentType)) {
            return validator(ObjectUtils.cast(componentInstance));
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
        return type(commandDataType.getName(), commandDataType.getType(), commandDataType);
    }

    public <T> FunnyCommandsConfiguration type(String typeName, Class<T> type, TriFunction<Origin, InjectorProperty, String, T> deserializer) {
        this.typeMappers.put(typeName, new TypeMapper<>(typeName, type, deserializer));
        return this;
    }

    public FunnyCommandsConfiguration bind(Bind bind) {
        this.binds.add(bind);
        return this;
    }

    public <A extends Annotation, V, E extends Exception> FunnyCommandsConfiguration validator(@Nullable  Class<A> annotation, @Nullable Class<V> type, ThrowingQuadFunction<Origin, A, InjectorProperty, V, Boolean, E> function) {
        return validator(new CustomValidator<>(annotation, type, function));
    }

    public <A extends Annotation, V, E extends Exception> FunnyCommandsConfiguration validator(Validator<A, V, E> validator) {
        validators.add(validator);
        return this;
    }

    public FunnyCommandsConfiguration completer(String name, TriFunction<Origin, String, Integer, List<String>> completer) {
        return completer(new CustomCompleter(name, completer));
    }

    public FunnyCommandsConfiguration completer(Completer completer) {
        this.completers.put(completer.getName(), completer);
        return this;
    }

    public <E extends Exception> FunnyCommandsConfiguration exceptionHandler(ExceptionHandler<E> exceptionHandler) {
        exceptionHandlers.put(exceptionHandler.getExceptionType(), exceptionHandler);
        return this;
    }

    public <E extends Exception> FunnyCommandsConfiguration exceptionHandler(Class<E> exceptionType, Function<E, Boolean> exceptionConsumer) {
        return exceptionHandler(new CustomExceptionHandler<>(exceptionType, exceptionConsumer));
    }

    public <R> FunnyCommandsConfiguration responseHandler(ResponseHandler<R> responseHandler) {
        responseHandlers.put(responseHandler.getResponseType(), responseHandler);
        return this;
    }

    public <R> FunnyCommandsConfiguration responseHandler(Class<R> responseType, BiFunction<Origin, R, Boolean> responseHandler) {
        return responseHandler(new CustomResponseHandler<>(responseType, responseHandler));
    }

    public FunnyCommandsConfiguration permissionHandler(PermissionHandler permissionHandler) {
        this.permissionHandler = permissionHandler;
        return this;
    }

    public FunnyCommandsConfiguration usageHandler(UsageHandler usageHandler) {
        this.usageHandler = usageHandler;
        return this;
    }

}
