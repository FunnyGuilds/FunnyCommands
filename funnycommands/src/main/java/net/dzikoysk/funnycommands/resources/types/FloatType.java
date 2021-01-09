package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.panda_lang.utilities.inject.InjectorProperty;

@FunnyComponent
public final class FloatType extends AbstractType<Float> implements CommandDataType<Float> {

    public FloatType() {
        super("float", Float.class);
    }

    @Override
    public Float apply(Origin origin, InjectorProperty required, String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Float::parseFloat);
    }

}
