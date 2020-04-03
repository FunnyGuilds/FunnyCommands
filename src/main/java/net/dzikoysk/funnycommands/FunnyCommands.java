package net.dzikoysk.funnycommands;

import net.dzikoysk.funnycommands.commands.CommandsLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.inject.Injector;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FunnyCommands {

    private final CommandsLoader commandsLoader;
    private final Map<String, Function<String, String>> placeholders;
    private final Injector injector;

    FunnyCommands(FunnyCommandsConfiguration configuration, Injector injector) {
        this.commandsLoader = new CommandsLoader(this, configuration.plugin);
        this.placeholders = configuration.placeholders;
        this.injector = injector;
    }

    public void dispose() {
        commandsLoader.unloadCommands();
    }

    public Map<? extends String, ? extends Function<String, String>> getPlaceholders() {
        return placeholders;
    }

    public CommandsLoader getCommandsLoader() {
        return commandsLoader;
    }

    public Injector getInjector() {
        return injector;
    }

    public static FunnyCommandsConfiguration configuration(Supplier<JavaPlugin> plugin) {
        return new FunnyCommandsConfiguration(plugin);
    }

}
