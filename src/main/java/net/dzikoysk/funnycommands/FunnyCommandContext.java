package net.dzikoysk.funnycommands;

import org.bukkit.command.CommandSender;

public final class FunnyCommandContext {

    private final CommandSender commandSender;

    public FunnyCommandContext(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public String format(Object value) {
        return value.toString();
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

}
