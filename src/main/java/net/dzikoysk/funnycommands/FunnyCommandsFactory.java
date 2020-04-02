package net.dzikoysk.funnycommands;

import net.dzikoysk.funnycommands.commands.CommandsLoader;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.InjectorException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

final class FunnyCommandsFactory {

    protected FunnyCommands createFunnyCommands(FunnyCommandsConfiguration creator) {
        Injector injector = DependencyInjection.createInjector(resources -> {
            creator.binds.forEach(bind -> bind.accept(resources));
        });

        Collection<Object> commands = new ArrayList<>(creator.commandsInstances);

        for (Class<?> commandClass : creator.commandsClasses) {
            try {
                commands.add(injector.newInstance(commandClass));
            } catch (InstantiationException | InjectorException | InvocationTargetException | IllegalAccessException e) {
                throw new FunnyCommandsException("Failed to instantiate command class " + commandClass, e);
            }
        }

        CommandsLoader commandsLoader = creator.commandsLoader;
        commandsLoader.registerCommands(commands);

        return new FunnyCommands(commandsLoader, injector);
    }

}
