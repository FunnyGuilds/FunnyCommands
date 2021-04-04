package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.panda_lang.utilities.inject.Property;

@FunnyComponent
public final class IntegerType extends AbstractType<Integer> implements CommandDataType<Integer> {

    public IntegerType() {
        super("integer", Integer.class);
    }

    @Override
    public Integer apply(Context context, Property required, String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Integer::parseInt);
    }

}
