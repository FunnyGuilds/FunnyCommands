package net.dzikoysk.funnycommands.commands;

import io.vavr.collection.Stream;
import io.vavr.control.Option;
import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.stereotypes.Executor;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.panda_lang.utilities.commons.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class CommandsLoader {

    private final FunnyCommands funnyCommands;
    private final CommandMapInjector commandMapInjector;

    public CommandsLoader(FunnyCommands funnyCommands, Supplier<JavaPlugin> plugin) {
        this.funnyCommands = funnyCommands;
        this.commandMapInjector = new CommandMapInjector(plugin);
    }

    public Collection<DynamicCommand> registerCommands(Iterable<Object> commands) {
        CommandsTree commandsTree = loadCommands(commands);
        Collection<DynamicCommand> dynamicCommands = new ArrayList<>(commandsTree.getChildren().size());

        for (CommandsTree commandTree : commandsTree.getChildren()) {
            dynamicCommands.add(registerCommand(funnyCommands, commandTree));
        }

        return dynamicCommands;
    }

    protected DynamicCommand registerCommand(FunnyCommands funnyCommands, CommandsTree commandTree) {
        DynamicCommand dynamicCommand = new DynamicCommand(funnyCommands, commandTree, commandTree.getMetadata().getCommandInfo());
        return commandMapInjector.register(dynamicCommand);
    }

    protected CommandsTree loadCommands(Iterable<Object> commands) {
        List<CommandMetadata> metadata = Stream.ofAll(commands)
                .map(this::mapCommand)
                .sorted()
                .toJavaList();

        CommandsTree metadataTree = new CommandsTree(null);

        metadata.forEach(meta -> {
            String[] units = meta.getName().split(" ");
            CommandsTree parent = metadataTree;

            for (int index = 0; index < units.length - 1; index++) {
                String unit = units[index];

                parent = parent.getNode(unit).getOrElseThrow(() -> {
                    throw new FunnyCommandsException("Unknown command root '" + unit + "' of '" + meta.getName() + "'");
                });
            }

            parent.add(meta);
        });

        return metadataTree;
    }

    private CommandMetadata mapCommand(Object command) {
        FunnyCommand funnyCommand = Option.of(command.getClass().getAnnotation(FunnyCommand.class)).getOrElseThrow(() -> {
            throw new FunnyCommandsException("Missing @FunnyCommand annotation in command " + command.getClass());
        });

        Method commandMethod = Option.of(ReflectionUtils.getMethodsAnnotatedWith(command.getClass(), Executor.class))
                .filter(set -> set.size() == 1)
                .map(set -> set.iterator().next())
                .getOrElseThrow(() -> {
                    throw new FunnyCommandsException("Command class has to contain the one and only executor");
                });


        Executor executor = commandMethod.getAnnotation(Executor.class);

        CommandInfo bukkitCommandInfo = new CommandInfo(
                funnyCommand.name(),
                funnyCommand.description(),
                funnyCommand.usage(),
                Arrays.asList(funnyCommand.aliases()),
                Arrays.asList(executor.value())
        );

        return new CommandMetadata(command, bukkitCommandInfo, commandMethod, null);
    }

    public void unloadCommands() {
        commandMapInjector.unregister();
    }

}
