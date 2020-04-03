package net.dzikoysk.funnycommands.commands;

import io.vavr.control.Option;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

final class CommandMetadata implements Comparable<CommandMetadata> {

    private final Object commandInstance;
    private final CommandInfo commandInfo;
    private final Method commandMethod;
    private final @Nullable Method tabCompleteMethod;

    CommandMetadata(Object commandInstance, CommandInfo commandInfo, Method commandMethod, @Nullable Method tabCompleteMethod) {
        this.commandInstance = commandInstance;
        this.commandInfo = commandInfo;
        this.commandMethod = commandMethod;
        this.tabCompleteMethod = tabCompleteMethod;
    }

    @Override
    public int compareTo(CommandMetadata o) {
        return commandInfo.getName().compareTo(o.getName());
    }

    protected Option<Method> getTabCompleteMethod() {
        return Option.of(tabCompleteMethod);
    }

    protected Method getCommandMethod() {
        return commandMethod;
    }

    protected CommandInfo getCommandInfo() {
        return commandInfo;
    }

    protected Object getCommandInstance() {
        return commandInstance;
    }

    protected String getSimpleName() {
        String[] units = getName().split(" ");
        return units[units.length - 1];
    }

    protected String getName() {
        return commandInfo.getName();
    }

}
