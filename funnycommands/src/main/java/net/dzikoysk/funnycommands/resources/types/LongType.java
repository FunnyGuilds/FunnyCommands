package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;

import java.lang.reflect.Parameter;

@FunnyComponent
public final class LongType implements CommandDataType<Long> {

    @Override
    public Long apply(final Origin origin, final Parameter parameter, final String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Long::parseLong);
    }

    @Override
    public String getName() {
        return "long";
    }

}
