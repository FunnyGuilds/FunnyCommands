package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;

import java.lang.reflect.Parameter;

@FunnyComponent
public final class FloatType implements CommandDataType<Float> {

    @Override
    public Float apply(Origin origin, Parameter parameter, String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Float::parseFloat);
    }

    @Override
    public String getName() {
        return "float";
    }

}
