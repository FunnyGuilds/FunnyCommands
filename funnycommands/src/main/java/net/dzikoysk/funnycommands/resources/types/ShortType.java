package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.panda_lang.utilities.inject.InjectorProperty;

@FunnyComponent
public final class ShortType extends AbstractType<Short> {

    public ShortType() {
        super("short", Short.class);
    }

    @Override
    public Short apply(Origin origin, InjectorProperty required, String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Short::parseShort);
    }

}
