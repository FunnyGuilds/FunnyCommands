package net.dzikoysk.funnycommands.resources.completers;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

@FunnyComponent
public final class MaterialsCompleter implements Completer {

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return CommandUtils.collectCompletions(Material.values(), prefix, limit, ArrayList::new);
    }

    @Override
    public String getName() {
        return "materials";
    }

}
