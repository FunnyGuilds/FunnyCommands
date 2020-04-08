package net.dzikoysk.funnycommands.resources.completers;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.DyeColor;

import java.util.ArrayList;
import java.util.List;

@FunnyComponent
public final class DyeColorsCompleter implements Completer {

    @Override
    public List<String> apply(Origin origin, String prefix, Integer limit) {
        return CommandUtils.collectCompletions(DyeColor.values(), prefix, limit, ArrayList::new);
    }

    @Override
    public String getName() {
        return "dye-colors";
    }

}
