package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.panda_lang.utilities.inject.InjectorProperty;

@FunnyComponent
public final class DoubleType extends AbstractType<Double> {

    public DoubleType() {
        super("double", Double.class);
    }

    @Override
    public Double apply(Origin origin, InjectorProperty required, String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Double::parseDouble);
    }

}
