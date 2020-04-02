package net.dzikoysk.funnycommands.test;

import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.FunnyCommandsPlugin;
import net.dzikoysk.funnycommands.responses.SenderResponse;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import net.dzikoysk.funnycommands.stereotypes.Command;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.collection.Maps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public final class FunnyCommandsAcceptanceTestPlugin extends FunnyCommandsPlugin {

    private static final String FC_TEST_ALIAS = "fc.test-alias";

    private FunnyCommands funnyCommands;

    @Override
    public void onEnable() {
        Map<String, String> configuration = Maps.of(FC_TEST_ALIAS, "test");

        Map<String, Function<String, String>> placeholders = new HashMap<String, Function<String, String>>() {{
            put(FC_TEST_ALIAS, configuration::get);
            put("fc.time", key -> new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        }};

        // handled
        FunnyCommands commands = FunnyCommands.configuration(this)
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

    @Override
    public void onDisable() {
        funnyCommands.dispose();
    }

    @interface RandomUUID { }

    @FunnyCommand(name = "${fc.test-alias}", permission = "fc.test")
    private static final class TestCommand {

        @Command({ "<player: target>" })
        SenderResponse test(@Arg("arg-player") Player target) {
            return new SenderResponse(target, "Test ${fc.time}");
        }

    }

}
