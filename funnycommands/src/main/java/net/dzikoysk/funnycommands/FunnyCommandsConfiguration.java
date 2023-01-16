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
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.DefaultResources;
import net.dzikoysk.funnycommands.resources.DetailedExceptionHandler;
import net.dzikoysk.funnycommands.resources.ExceptionHandler;
import net.dzikoysk.funnycommands.resources.PermissionHandler;
import net.dzikoysk.funnycommands.resources.ResponseHandler;
import net.dzikoysk.funnycommands.resources.UsageHandler;
import net.dzikoysk.funnycommands.resources.Validator;
import net.dzikoysk.funnycommands.resources.completers.CustomCompleter;
import net.dzikoysk.funnycommands.resources.exceptions.CustomExceptionHandler;
import net.dzikoysk.funnycommands.resources.responses.CustomResponseHandler;
import net.dzikoysk.funnycommands.resources.types.TypeMapper;
import net.dzikoysk.funnycommands.resources.validators.CustomValidator;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Property;
import panda.std.Lazy;
import panda.std.Option;
import panda.std.function.ThrowingQuadFunction;
import panda.std.function.TriFunction;
import panda.utilities.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunnyCommandsConfiguration {

    protected final Supplier<JavaPlugin> plugin;
    protected final Map<String, Function<String, String>> placeholders = new HashMap<>();
    protected final Collection<Class<?>> commandsClasses = new ArrayList<>();
    protected final Collection<Object> commandsInstances = new ArrayList<>();
    protected final Map<String, Completer> completes = new HashMap<>();
    protected final Map<String, TypeMapper<?>> typeMappers = new HashMap<>();
    protected final Collection<Bind> binds = new ArrayList<>();
    protected final Collection<Validator<?, ?, ?>> validators = new ArrayList<>();
    protected final Map<Class<? extends Exception>, DetailedExceptionHandler<? extends Exception>> exceptionHandlers = new HashMap<>();
    protected final Map<Class<?>, ResponseHandler<?>> responseHandlers = new HashMap<>();
    protected Injector injector = DependencyInjection.createInjector();
    protected Option<PermissionHandler> permissionHandler = Option.none();
    protected Option<UsageHandler> usageHandler = Option.none();

    FunnyCommandsConfiguration(Supplier<JavaPlugin> plugin) {
        this.plugin = new Lazy<>(plugin);
    }

    public FunnyCommands install() {
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
        if (componentInstance == null) {
            throw new IllegalArgumentException("Component instance cannot be null");
        }

        if (componentInstance instanceof CommandDataType) {
            return type(ObjectUtils.cast(componentInstance));
        }

        if (componentInstance instanceof Completer) {
            return completer(ObjectUtils.cast(componentInstance));
        }

        if (componentInstance instanceof Bind) {
            return bind(ObjectUtils.cast(componentInstance));
        }

        if (componentInstance instanceof Validator) {
            return validator(ObjectUtils.cast(componentInstance));
        }

        if (componentInstance instanceof ResponseHandler) {
            return responseHandler(ObjectUtils.cast(componentInstance));
        }

        //noinspection deprecation
        if (componentInstance instanceof ExceptionHandler)  {
            //noinspection deprecation
            return exceptionHandler((ExceptionHandler<? extends Exception>) componentInstance);
        }

        if (componentInstance instanceof DetailedExceptionHandler)  {
            return exceptionHandler((DetailedExceptionHandler<? extends Exception>) componentInstance);
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

    public <T> FunnyCommandsConfiguration type(String typeName, Class<T> type, TriFunction<Context, Property, String, T> deserializer) {
        this.typeMappers.put(typeName, new TypeMapper<>(typeName, type, deserializer));
        return this;
    }

    public FunnyCommandsConfiguration bind(Bind bind) {
        this.binds.add(bind);
        return this;
    }

    public <A extends Annotation, V, E extends Exception> FunnyCommandsConfiguration validator(@Nullable  Class<A> annotation, @Nullable Class<V> type, ThrowingQuadFunction<Context, A, Property, V, Boolean, E> function) {
        return validator(new CustomValidator<>(annotation, type, function));
    }

    public <A extends Annotation, V, E extends Exception> FunnyCommandsConfiguration validator(Validator<A, V, E> validator) {
        validators.add(validator);
        return this;
    }

    public FunnyCommandsConfiguration completer(String name, TriFunction<Context, String, Integer, List<String>> completer) {
        return completer(new CustomCompleter(name, completer));
    }

    public FunnyCommandsConfiguration completer(Completer completer) {
        this.completes.put(completer.getName(), completer);
        return this;
    }

    public <E extends Exception> FunnyCommandsConfiguration exceptionHandler(DetailedExceptionHandler<E> exceptionHandler) {
        exceptionHandlers.put(exceptionHandler.getExceptionType(), exceptionHandler);
        return this;
    }

    public <E extends Exception> FunnyCommandsConfiguration exceptionHandler(Class<E> exceptionType, BiFunction<Context, E, Boolean> exceptionConsumer) {
        return exceptionHandler(new CustomExceptionHandler<>(exceptionType, exceptionConsumer));
    }

    @Deprecated
    public <E extends Exception> FunnyCommandsConfiguration exceptionHandler(Class<E> exceptionType, Function<E, Boolean> exceptionConsumer) {
        return exceptionHandler(new CustomExceptionHandler<>(exceptionType, exceptionConsumer));
    }

    @Deprecated
    public <E extends Exception> FunnyCommandsConfiguration exceptionHandler(ExceptionHandler<E> exceptionHandler) {
        return exceptionHandler(new CustomExceptionHandler<>(exceptionHandler.getExceptionType(), exceptionHandler));
    }

    public <R> FunnyCommandsConfiguration responseHandler(ResponseHandler<R> responseHandler) {
        responseHandlers.put(responseHandler.getResponseType(), responseHandler);
        return this;
    }

    public <R> FunnyCommandsConfiguration responseHandler(Class<R> responseType, BiFunction<Context, R, Boolean> responseHandler) {
        return responseHandler(new CustomResponseHandler<>(responseType, responseHandler));
    }

    public FunnyCommandsConfiguration permissionHandler(PermissionHandler permissionHandler) {
        this.permissionHandler = Option.of(permissionHandler);
        return this;
    }

    public FunnyCommandsConfiguration usageHandler(UsageHandler usageHandler) {
        this.usageHandler = Option.of(usageHandler);
        return this;
    }

    public FunnyCommandsConfiguration injector(Injector injector) {
        this.injector = injector;
        return this;
    }

}
