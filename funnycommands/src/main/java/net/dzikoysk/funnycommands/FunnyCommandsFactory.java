/*
 * Copyright (c) 2020 Dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
