package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;

import java.lang.reflect.Parameter;

@FunnyComponent
public final class LongType extends AbstractType<Long> {

    public LongType() {
        super("long", Long.class);
    }

    @Override
    public Long apply(Origin origin, Parameter parameter, String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Long::parseLong);
    }

}
