package net.dzikoysk.funnycommands.resources.types;

import net.dzikoysk.funnycommands.FunnyCommandsUtils;
import net.dzikoysk.funnycommands.resources.CommandDataType;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.panda_lang.utilities.inject.InjectorProperty;

@FunnyComponent
public final class LongType extends AbstractType<Long> implements CommandDataType<Long> {

    public LongType() {
        super("long", Long.class);
    }

    @Override
    public Long apply(Origin origin, InjectorProperty required, String argument) {
        return FunnyCommandsUtils.parseNumber(argument, Long::parseLong);
    }

}
