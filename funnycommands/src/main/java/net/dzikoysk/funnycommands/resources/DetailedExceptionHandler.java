package net.dzikoysk.funnycommands.resources;

import java.util.function.BiFunction;

public interface DetailedExceptionHandler<E extends Exception> extends BiFunction<Context, E, Boolean> {

    Class<E> getExceptionType();

}
