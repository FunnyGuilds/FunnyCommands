package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;

import java.lang.reflect.Parameter;

@FunnyComponent
public final class ShortType implements CommandDataType<Short> {

    @Override
    public Short apply(Origin origin, Parameter parameter, String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Short::parseShort);
    }

    @Override
    public String getName() {
        return "short";
    }

}
