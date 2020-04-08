package net.dzikoysk.funnycommands.resources.completers;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@FunnyComponent
public final class WorldsCompleter implements Completer {

    @Override
    public List<String> apply(final Origin origin, final String prefix, final Integer limit) {
        return CommandUtils.collectCompletions(Bukkit.getWorlds(), prefix, limit, ArrayList::new, World::getName);
    }

    @Override
    public String getName() {
        return "worlds";
    }

}
