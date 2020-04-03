package net.dzikoysk.funnycommands.commands;

import io.vavr.control.Option;
import org.panda_lang.utilities.commons.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class CommandsTree {

    private final CommandMetadata element;
    private final Map<String, CommandsTree> children;

    public CommandsTree(CommandMetadata element) {
        this.element = element;
        this.children = new HashMap<>();
    }

    public void print() {
        print(this, 0);
    }

    private void print(CommandsTree node, int level) {
        for (CommandsTree child : node.getChildren()) {
            System.out.println(StringUtils.buildSpace(level * 2) + (level == 0 ? "/" : "..") + child.getSimpleName() + " -> " + child.element);
            child.print(child, level + 1);
        }
    }

    public CommandsTree computeIfAbsent(String name, Function<String, CommandMetadata> function) {
        return getNode(name).getOrElse(() -> add(function.apply(name)));
    }

    public CommandsTree add(CommandMetadata element) {
        CommandsTree tree = new CommandsTree(element);
        children.put(tree.getSimpleName(), tree);
        return tree;
    }

    public Option<CommandMetadata> get(String name) {
        return getNode(name).map(CommandsTree::getMetadata);
    }

    public Collection<CommandsTree> collectCommandsStartingWith(String str) {
        Collection<CommandsTree> nodes = new ArrayList<>();

        for (CommandsTree tree : children.values()) {
            if (tree.getName().startsWith(str)) {
                nodes.add(tree);
            }

            nodes.addAll(tree.collectCommandsStartingWith(str));
        }

        return nodes;
    }

    public Option<CommandsTree> getNode(String nodeName) {
        return Option.of(children.get(nodeName));
    }

    public Collection<CommandsTree> getChildren() {
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