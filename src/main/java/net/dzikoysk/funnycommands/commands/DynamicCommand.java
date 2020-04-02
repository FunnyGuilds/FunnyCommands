package net.dzikoysk.funnycommands.commands;

import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsException;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.List;

final class DynamicCommand extends Command {

    private final FunnyCommands funnyCommands;
    private final Method executeMethod;
    private final Method tabCompleteMethod;
    private final Object commandInstance;

    protected DynamicCommand(FunnyCommands funnyCommands, BukkitCommandInfo bukkitCommandInfo, Method executeMethod, Method tabCompleteMethod, Object commandInstance) {
        super(bukkitCommandInfo.getName(), bukkitCommandInfo.getDescription(), bukkitCommandInfo.getUsageMessage(), bukkitCommandInfo.getAliases());
        this.funnyCommands = funnyCommands;
        this.executeMethod = executeMethod;
        this.tabCompleteMethod = tabCompleteMethod;
        this.commandInstance = commandInstance;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return invoke(executeMethod);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, @Nullable Location location) throws IllegalArgumentException {
        return invoke(tabCompleteMethod);
    }

    private <T> T invoke(Method method) {
        try {
            return funnyCommands.getInjector().invokeMethod(method, commandInstance);
        } catch (Throwable throwable) {
            throw new FunnyCommandsException("Failed to invoke method " + method, throwable);
        }
    }

}
