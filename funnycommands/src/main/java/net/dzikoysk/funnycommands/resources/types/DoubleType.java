package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.panda_lang.utilities.inject.InjectorProperty;

@FunnyComponent
public final class DoubleType extends AbstractType<Double> implements CommandDataType<Double> {

    public DoubleType() {
        super("double", Double.class);
    }

    @Override
    public Double apply(Context context, InjectorProperty required, String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Double::parseDouble);
    }

}
