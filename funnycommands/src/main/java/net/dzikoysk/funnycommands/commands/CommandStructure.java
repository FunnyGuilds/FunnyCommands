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
import org.panda_lang.utilities.commons.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CommandStructure {

    private final CommandMetadata element;
    private final Map<String, CommandStructure> children;

    public CommandStructure(CommandMetadata element) {
        this.element = element;
        this.children = new HashMap<>();
    }

    public void print() {
        print(this, 0);
    }

    private void print(CommandStructure node, int level) {
        for (CommandStructure child : node.getSubcommands()) {
            System.out.println(StringUtils.buildSpace(level * 2) + (level == 0 ? "/" : "..") + child.getSimpleName() + " -> " + child.element);
            child.print(child, level + 1);
        }
    }

    public CommandStructure computeIfAbsent(String name, Function<String, CommandMetadata> function) {
        return getSubcommandStructure(name).getOrElse(() -> add(function.apply(name)));
    }

    public CommandStructure add(CommandMetadata element) {
        CommandStructure tree = new CommandStructure(element);
        children.put(tree.getSimpleName(), tree);
        return tree;
    }

    public Option<CommandMetadata> get(String name) {
        return getSubcommandStructure(name).map(CommandStructure::getMetadata);
    }

    public List<CommandStructure> collectCommandsStartingWith(String str) {
        List<CommandStructure> nodes = new ArrayList<>();

        for (CommandStructure tree : children.values()) {
            if (tree.getName().startsWith(str)) {
                nodes.add(tree);
            }

            nodes.addAll(tree.collectCommandsStartingWith(str));
        }

        return nodes;
    }

    public Option<CommandStructure> getSubcommandStructure(String nodeName) {
        for (CommandStructure child : children.values()) {
            if (child.getSimpleName().equalsIgnoreCase(nodeName)) {
                return Option.some(child);
            }

            for (String alias : child.element.getCommandInfo().getAliases()) {
                if (alias.equalsIgnoreCase(nodeName)) {
                    return Option.some(child);
                }
            }
        }
        return Option.none();
    }

    public Collection<CommandStructure> getSubcommands() {
        return children.values();
    }

    public List<String> getSubcommandsNames() {
        return getSubcommands().stream()
                .map(CommandStructure::getSimpleName)
                .collect(Collectors.toList());
    }

    public CommandMetadata getMetadata() {
        return element;
    }

    public String getSimpleName() {
        return element.getSimpleName();
    }

    public String getName() {
        return element.getName();
    }

}