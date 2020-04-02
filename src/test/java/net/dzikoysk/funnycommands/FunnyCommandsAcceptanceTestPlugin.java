package net.dzikoysk.funnycommands;

import net.dzikoysk.funnycommands.responses.SenderResponse;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.collection.Maps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
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
                .responseHandler(SenderResponse.class, (context, response) -> {
                    context.getCommandSender().sendMessage(context.format(response));
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
        commands.dispose();
    }

    @interface RandomUUID { }

    static final class TestCommand {

        @FunnyCommand(name = "${fc.test-alias}", permission = "fc.test", parameters = {})
        SenderResponse test(@RandomUUID UUID uuid) {
            return new SenderResponse(uuid + ": ${fc.time}");
        }

    }

}
