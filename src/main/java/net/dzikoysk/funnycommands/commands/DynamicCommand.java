package net.dzikoysk.funnycommands.commands;

import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsException;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

final class DynamicCommand extends Command {

    private final FunnyCommands funnyCommands;
    private final CommandsTree commandsTree;

    protected DynamicCommand(FunnyCommands funnyCommands, CommandsTree commandsTree, CommandInfo commandInfo) {
        super(commandInfo.getName(), commandInfo.getDescription(), commandInfo.getUsageMessage(), commandInfo.getAliases());
        this.funnyCommands = funnyCommands;
        this.commandsTree = commandsTree;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return invoke(commandsTree.getMetadata().getCommandMethod());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, @Nullable Location location) throws IllegalArgumentException {
        return commandsTree.getMetadata().getTabCompleteMethod()
            .map(this::invoke)
            .map(result -> (List<String>) result)
            .getOrElse(Collections::emptyList);
    }

    private <T> T invoke(Method method) {
        try {
            return funnyCommands.getInjector().invokeMethod(method, commandsTree.getMetadata().getCommandInstance());
        } catch (Throwable throwable) {
            throw new FunnyCommandsException("Failed to invoke method " + method, throwable);
        }
    }

}
