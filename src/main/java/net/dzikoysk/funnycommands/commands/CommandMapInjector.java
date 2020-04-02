package net.dzikoysk.funnycommands.commands;

import net.dzikoysk.funnycommands.FunnyCommandsException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.commons.ObjectUtils;
import org.panda_lang.utilities.commons.function.ThrowingSupplier;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

final class CommandMapInjector {

    private final JavaPlugin plugin;
    private final Map<String, DynamicCommand> registeredCommands = new HashMap<>();

    CommandMapInjector(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    protected void register(DynamicCommand command) {
        fetchCommandMap().register(plugin.getName(), command);
        registeredCommands.put(command.getName(), command);
    }

    protected void unregister() {
        Map<String, Command> knownCommands = fetchKnownCommandMap();
        registeredCommands.keySet().forEach(knownCommands::remove);
    }

    private CommandMap fetchCommandMap() {
        return fetch(() -> {
            Field commandMapField = plugin.getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);

            return (CommandMap) commandMapField.get(plugin.getServer());
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

}
