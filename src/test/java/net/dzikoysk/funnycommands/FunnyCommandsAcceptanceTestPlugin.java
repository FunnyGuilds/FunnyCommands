package net.dzikoysk.funnycommands;

import net.dzikoysk.funnycommands.responses.SenderResponse;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import net.dzikoysk.funnycommands.stereotypes.Command;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.TabCompleter;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.collection.Maps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public final class FunnyCommandsAcceptanceTestPlugin extends FunnyCommandsPlugin {

    private FunnyCommands commands;

    @Override
    public void onEnable() {
        Map<String, String> configuration = Maps.of("fc.test-alias", "test");

        Map<String, Function<String, String>> placeholders = new HashMap<String, Function<String, String>>() {{
            put("fc.test-alias", configuration::get);
            put("fc.time", key -> new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        }};

        this.commands = FunnyCommands.configuration(super.getServer())
                .placeholders(placeholders)
                .commands(TestCommand.class)
                .type("player", Player.class, username -> super.getServer().getPlayer(username))
                .bind(resources -> resources.annotatedWith(RandomUUID.class).assignInstance(UUID::randomUUID))
                .responseHandler(boolean.class, (context, response) -> true)
                .responseHandler(SenderResponse.class, (context, response) -> {
                    response.getSender()
                            .getOrElse(context::getCommandSender)
                            .sendMessage(context.format(response));

                    return true;
                })
                .exceptionHandler(FunnyCommandsException.class, e -> {
                    e.printStackTrace();
                    return true; // handled
                })
                .create();
    }

    @interface RandomUUID { }

    @FunnyCommand(name = "${fc.test-alias}", permission = "fc.test")
    private static final class TestCommand {

        @Command({ "<player: target>" })
        SenderResponse test(@Arg("arg-player") Player target) {
            return new SenderResponse(target, "Test ${fc.time}");
        }

        @TabCompleter
        List<String> complete() {
            return Collections.emptyList();
        }

    }

}
