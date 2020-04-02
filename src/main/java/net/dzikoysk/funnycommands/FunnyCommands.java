package net.dzikoysk.funnycommands;

import org.bukkit.Server;
import org.panda_lang.utilities.inject.Injector;

public final class FunnyCommands {

    private final Injector injector;

    FunnyCommands(Injector injector) {
        this.injector = injector;
    }

    public void dispose() {

    }

    public static FunnyCommandsConfiguration configuration(Server server) {
        return new FunnyCommandsConfiguration(server);
    }

}
