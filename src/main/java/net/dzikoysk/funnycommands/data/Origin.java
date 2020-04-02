package net.dzikoysk.funnycommands.data;

import org.bukkit.command.CommandSender;

public final class Origin {

    private final CommandSender commandSender;

    public Origin(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public String format(Object value) {
        return value.toString();
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

}
