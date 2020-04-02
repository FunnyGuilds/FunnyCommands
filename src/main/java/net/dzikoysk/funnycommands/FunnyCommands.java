package net.dzikoysk.funnycommands;

import net.dzikoysk.funnycommands.commands.CommandsLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.inject.Injector;

public final class FunnyCommands {

    private final CommandsLoader commandsLoader;
    private final Injector injector;

    FunnyCommands(CommandsLoader commandsLoader, Injector injector) {
        this.commandsLoader = commandsLoader;
        this.injector = injector;
    }

    public void dispose() {
        commandsLoader.unregisterCommands();
    }

    public Injector getInjector() {
        return injector;
    }

    public static FunnyCommandsConfiguration configuration(JavaPlugin plugin) {
        return new FunnyCommandsConfiguration(plugin);
    }

}
