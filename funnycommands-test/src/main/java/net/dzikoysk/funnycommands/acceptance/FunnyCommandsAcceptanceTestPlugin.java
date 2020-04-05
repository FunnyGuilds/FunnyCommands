/*
 * Copyright (c) 2020 Dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dzikoysk.funnycommands.acceptance;

import io.vavr.control.Option;
import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.FunnyCommandsPlugin;
import net.dzikoysk.funnycommands.responses.SenderResponse;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import net.dzikoysk.funnycommands.stereotypes.Executor;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.Nillable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.collection.Maps;
import org.panda_lang.utilities.inject.annotations.Injectable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

        GuildService guildService = new GuildService();

        // handled
        this.funnyCommands = FunnyCommands.configuration(() -> this)
                .placeholders(placeholders)
                .commands(TestCommand.class)
                .type("player", (origin, required, username) -> {
                    return super.getServer().getPlayer(username);
                })
                .type("guild", ((origin, required, guild) -> {
                    return guildService.guilds.get(guild);
                }))
                .globalBind(resources -> {
                    resources.annotatedWith(RandomUUID.class).assignInstance(UUID::randomUUID);
                })
                .dynamicBind(((origin, resources) -> {
                    resources.annotatedWithTested(Sender.class).assignInstance(origin.getCommandSender());
                }))
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

    // Test classes

    private static final class GuildService {
        private final Map<String, Guild> guilds = new HashMap<String, Guild>() {{
            put("test-guild", new Guild());
        }};
    }

    private static final class Guild {
        private final String name = "test-guild";
    }

    @Injectable
    @Retention(RetentionPolicy.RUNTIME)
    @interface RandomUUID { }

    @Injectable
    @Retention(RetentionPolicy.RUNTIME)
    @interface Sender { }

    @FunnyCommand(name = "${fc.test-alias}", permission = "fc.test", usage = "/${fc.test-alias} <player>")
    private static final class TestCommand {

        @Executor({ "player:target", "guild:arg-guild" })
        SenderResponse test(@Sender CommandSender sender, @Arg("target") @Nillable Player target, @Arg("arg-guild") Option<Guild> guild) {
            System.out.println(sender + " called " + target + " and " + guild.getOrNull());
            return new SenderResponse(target, "Test ${fc.time}");
        }

    }

}
