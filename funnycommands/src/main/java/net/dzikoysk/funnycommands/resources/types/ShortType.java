package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.panda_lang.utilities.inject.Property;

@FunnyComponent
public final class ShortType extends AbstractType<Short> implements CommandDataType<Short> {

    public ShortType() {
        super("short", Short.class);
    }

    @Override
    public Short apply(Context context, Property required, String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Short::parseShort);
    }

}
