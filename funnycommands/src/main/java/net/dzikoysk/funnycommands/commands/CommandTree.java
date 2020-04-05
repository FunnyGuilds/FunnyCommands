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

public final class CommandTree {

    private final CommandMetadata element;
    private final Map<String, CommandTree> children;

    public CommandTree(CommandMetadata element) {
        this.element = element;
        this.children = new HashMap<>();
    }

    public void print() {
        print(this, 0);
    }

    private void print(CommandTree node, int level) {
        for (CommandTree child : node.getChildren()) {
            System.out.println(StringUtils.buildSpace(level * 2) + (level == 0 ? "/" : "..") + child.getSimpleName() + " -> " + child.element);
            child.print(child, level + 1);
        }
    }

    public CommandTree computeIfAbsent(String name, Function<String, CommandMetadata> function) {
        return getNode(name).getOrElse(() -> add(function.apply(name)));
    }

    public CommandTree add(CommandMetadata element) {
        CommandTree tree = new CommandTree(element);
        children.put(tree.getSimpleName(), tree);
        return tree;
    }

    public Option<CommandMetadata> get(String name) {
        return getNode(name).map(CommandTree::getMetadata);
    }

    public List<CommandTree> collectCommandsStartingWith(String str) {
        List<CommandTree> nodes = new ArrayList<>();

        for (CommandTree tree : children.values()) {
            if (tree.getName().startsWith(str)) {
                nodes.add(tree);
            }

            nodes.addAll(tree.collectCommandsStartingWith(str));
        }

        return nodes;
    }

    public Option<CommandTree> getNode(String nodeName) {
        return Option.of(children.get(nodeName));
    }

    public Collection<CommandTree> getChildren() {
        return children.values();
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