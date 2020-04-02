package net.dzikoysk.funnycommands.mappers;

import java.util.function.Function;

public final class TypeMapper<T> {

    private final String name;
    private final Class<T> type;
    private final Function<String, T> deserializer;

    public TypeMapper(String name, Class<T> type, Function<String, T> deserializer) {
        this.name = name;
        this.type = type;
        this.deserializer = deserializer;
    }

}
