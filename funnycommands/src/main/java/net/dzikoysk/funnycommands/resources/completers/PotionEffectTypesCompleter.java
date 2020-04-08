package net.dzikoysk.funnycommands.resources.completers;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FunnyComponent
public final class PotionEffectTypesCompleter implements Completer {

    @Override
    public List<String> apply(final Origin origin, final String prefix, final Integer limit) {
        return CommandUtils.collectCompletions(Arrays.asList(PotionEffectType.values()), prefix, limit, ArrayList::new, type -> type.getName().toLowerCase());
    }

    @Override
    public String getName() {
        return "potion-effect-types";
    }

}
