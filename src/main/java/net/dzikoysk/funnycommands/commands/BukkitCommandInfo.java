package net.dzikoysk.funnycommands.commands;

import java.util.List;

final class BukkitCommandInfo {

    private final String name;
    private final String description;
    private final String usageMessage;
    private final List<String> aliases;
    private final List<String> arguments;

    BukkitCommandInfo(String name, String description, String usageMessage, List<String> aliases, List<String> arguments) {
        this.name = name;
        this.description = description;
        this.usageMessage = usageMessage;
        this.aliases = aliases;
        this.arguments = arguments;
    }

    List<String> getArguments() {
        return arguments;
    }

    List<String> getAliases() {
        return aliases;
    }

    String getUsageMessage() {
        return usageMessage;
    }

    String getDescription() {
        return description;
    }

    String getName() {
        return name;
    }

}
