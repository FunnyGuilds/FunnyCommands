package net.dzikoysk.funnycommands.resources.completers;

import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;

import java.util.Collections;
import java.util.List;

@FunnyComponent
public final class EmptyCompleter implements Completer {

    @Override
    public List<String> apply(Context context, String prefix, Integer limit) {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "empty";
    }

}
