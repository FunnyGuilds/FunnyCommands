package net.dzikoysk.funnycommands.resources.binds;

import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Bind;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.Resources;

@FunnyComponent
public final class PlayerBind implements Bind {

    @Override
    public void accept(Resources injectorResources) {
        injectorResources.on(Player.class).assignHandler((injectorProperty, annotation, args) -> {
            Context context = CommandUtils.getContext(args);
            CommandSender commandSender = context.getCommandSender();

            if (!(commandSender instanceof Player)) {
                throw new IllegalStateException("Cannot use player bind in non-player command");
            }

            return commandSender;
        });
    }

}
