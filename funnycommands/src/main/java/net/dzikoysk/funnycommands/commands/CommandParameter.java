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

import org.jetbrains.annotations.NotNull;

public final class CommandParameter implements Comparable<CommandParameter> {

    private final int index;
    private final String name;
    private final boolean optional;
    private final boolean varargs;

    CommandParameter(int index, String name, boolean optional, boolean varargs) {
        this.index = index;
        this.name = name;
        this.optional = optional;
        this.varargs = varargs;
    }

    @Override
    public int compareTo(@NotNull CommandParameter o) {
        return Integer.compare(index, o.getIndex());
    }

    public boolean isVarargs() {
        return varargs;
    }

    public boolean isOptional() {
        return optional;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

}
