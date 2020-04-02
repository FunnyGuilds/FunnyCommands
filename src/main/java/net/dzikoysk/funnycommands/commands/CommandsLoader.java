package net.dzikoysk.funnycommands.commands;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;

public final class CommandsLoader {

    private final CommandMapInjector commandMapInjector;

    public CommandsLoader(JavaPlugin plugin) {
        this.commandMapInjector = new CommandMapInjector(plugin);
    }

    public void registerCommands(Collection<Object> commands) {

    }

    public void unregisterCommands() {
        commandMapInjector.unregister();
    }

}
