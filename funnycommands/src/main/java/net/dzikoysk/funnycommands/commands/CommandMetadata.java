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

package net.dzikoysk.funnycommands.commands;

import io.vavr.control.Option;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

public final class CommandMetadata implements Comparable<CommandMetadata> {

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

    protected Object getCommandInstance() {
        return commandInstance;
    }

    public CommandInfo getCommandInfo() {
        return commandInfo;
    }

    public String getSimpleName() {
        String[] units = getName().split(" ");
        return units[units.length - 1];
    }

    public String getName() {
        return commandInfo.getName();
    }

}
