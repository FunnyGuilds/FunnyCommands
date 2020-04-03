package net.dzikoysk.funnycommands;

import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.InjectorException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

final class FunnyCommandsFactory {

    protected FunnyCommands createFunnyCommands(FunnyCommandsConfiguration configuration) {
        Injector injector = DependencyInjection.createInjector(resources -> {
            configuration.binds.forEach(bind -> bind.accept(resources));
        });

        Collection<Object> commands = new ArrayList<>(configuration.commandsInstances);

        for (Class<?> commandClass : configuration.commandsClasses) {
            try {
                commands.add(injector.newInstance(commandClass));
            } catch (InstantiationException | InjectorException | InvocationTargetException | IllegalAccessException e) {
                throw new FunnyCommandsException("Failed to instantiate command class " + commandClass, e);
            }
        }

        FunnyCommands funnyCommands = new FunnyCommands(configuration, injector);
        funnyCommands.getCommandsLoader().registerCommands(commands);

        return funnyCommands;
    }

}
