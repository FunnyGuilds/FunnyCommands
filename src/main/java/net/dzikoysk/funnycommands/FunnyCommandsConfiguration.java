package net.dzikoysk.funnycommands;

import net.dzikoysk.funnycommands.mappers.TypeMapper;
import org.bukkit.Server;
import org.panda_lang.utilities.inject.InjectorResources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class FunnyCommandsConfiguration {

    protected final Server server;
    protected final Map<String, Function<String, String>> placeholders = new HashMap<>();
    protected final Collection<Class<?>> commandsClasses = new ArrayList<>();
    protected final Map<String, TypeMapper<?>> typeMappers = new HashMap<>();
    protected final Collection<Consumer<InjectorResources>> binds = new ArrayList<>();
    protected final Map<Class<? extends Exception>, Function<? extends Exception, Boolean>> exceptionHandlers = new HashMap<>();
    protected final Map<Class<?>, BiFunction<FunnyCommandContext, ?, Boolean>> responseHandlers = new HashMap<>();

    FunnyCommandsConfiguration(Server server) {
        this.server = server;
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

    public <R> FunnyCommandsConfiguration responseHandler(Class<R> responseType, BiFunction<FunnyCommandContext, R, Boolean> responseHandler) {
        this.responseHandlers.put(responseType, responseHandler);
        return this;
    }

}
