package net.dzikoysk.funnycommands.commands;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

final class CommandMetadata implements Comparable<CommandMetadata> {

    private final BukkitCommandInfo commandInfo;
    private final Method commandMethod;
    private final @Nullable Method tabCompleteMethod;

    CommandMetadata(BukkitCommandInfo commandInfo, Method commandMethod, @Nullable Method tabCompleteMethod) {
        this.commandInfo = commandInfo;
        this.commandMethod = commandMethod;
        this.tabCompleteMethod = tabCompleteMethod;
    }

    @Override
    public int compareTo(CommandMetadata o) {
        return commandInfo.getName().compareTo(o.getName());
    }

    protected String getSimpleName() {
        String[] units = getName().split(" ");
        return units[units.length - 1];
    }

    protected String getName() {
        return commandInfo.getName();
    }

}
