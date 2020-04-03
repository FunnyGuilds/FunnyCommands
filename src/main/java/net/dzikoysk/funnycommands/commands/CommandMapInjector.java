package net.dzikoysk.funnycommands.commands;

import net.dzikoysk.funnycommands.FunnyCommandsException;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.function.CachedSupplier;
import org.panda_lang.utilities.commons.function.ThrowingSupplier;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

final class CommandMapInjector {

    private final Supplier<JavaPlugin> plugin;
    private final Map<String, DynamicCommand> registeredCommands = new HashMap<>();

    CommandMapInjector(Supplier<JavaPlugin> plugin) {
        this.plugin = new CachedSupplier<>(plugin);
    }

    protected void register(DynamicCommand command) {
        fetchCommandMap().register(plugin.get().getName(), command);
        registeredCommands.put(command.getName(), command);
    }

    protected void unregister() {
        Map<String, Command> knownCommands = fetchKnownCommandMap();
        registeredCommands.keySet().forEach(knownCommands::remove);
    }

    private CommandMap fetchCommandMap() {
        return fetch(() -> {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            return (CommandMap) commandMapField.get(getServer());
        });
    }

    private Map<String, Command> fetchKnownCommandMap() {
        return fetch(() -> {
            CommandMap commandMap = fetchCommandMap();

            Field knownCommandMapField = commandMap.getClass().getDeclaredField("knownCommands");
            knownCommandMapField.setAccessible(true);

            return ObjectUtils.cast(knownCommandMapField.get(commandMap));
        });
    }

    private <T> T fetch(ThrowingSupplier<T, ReflectiveOperationException> supplier) {
        try {
            return supplier.get();
        } catch (ReflectiveOperationException e) {
            throw new FunnyCommandsException("Unsupported server engine", e);
        }
    }

    private Server getServer() {
        return plugin.get().getServer();
    }

}
