package net.dzikoysk.funnycommands.resources.completers;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.Sound;

import java.util.ArrayList;
import java.util.List;

@FunnyComponent
public final class SoundsCompleter implements Completer {

    @Override
    public List<String> apply(final Origin origin, final String prefix, final Integer limit) {
        return CommandUtils.collectCompletions(Sound.values(), prefix, limit, ArrayList::new);
    }

    @Override
    public String getName() {
        return "sounds";
    }

}
