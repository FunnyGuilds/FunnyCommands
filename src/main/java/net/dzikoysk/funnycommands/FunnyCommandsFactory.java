package net.dzikoysk.funnycommands;

import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;

final class FunnyCommandsFactory {

    protected FunnyCommands createFunnyCommands(FunnyCommandsConfiguration creator) {
        Injector injector = DependencyInjection.createInjector(resources -> {
            creator.binds.forEach(bind -> bind.accept(resources));
        });

        // register commands or sth

        return new FunnyCommands(injector);
    }

}
