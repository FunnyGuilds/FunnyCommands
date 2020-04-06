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
import net.dzikoysk.funnycommands.FunnyCommandsPlugin;
import net.dzikoysk.funnycommands.defaults.PlayerType;
import net.dzikoysk.funnycommands.responses.SenderResponse;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import net.dzikoysk.funnycommands.stereotypes.Nillable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.collection.Maps;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class FunnyCommandsAcceptanceTestPlugin extends FunnyCommandsPlugin {

    // Test classes

    private static final String FC_TEST_ALIAS = "fc.test-alias";
    private static final Map<String, String> CONFIGURATION = Maps.of(FC_TEST_ALIAS, "test");
    private static final Map<String, Function<String, String>> PLACEHOLDERS = new HashMap<String, Function<String, String>>() {{
        put(FC_TEST_ALIAS, CONFIGURATION::get);
        put("fc.time", key -> new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
    }};

    private static final class GuildService {
        private final Map<String, Guild> guilds = new HashMap<String, Guild>() {{
            put("test-guild", new Guild());
        }};
    }

    private static final class Guild {
        private final String name = "test-guild";
    }

    // Example plugin

    private FunnyCommands funnyCommands;

    @Override
    public void onEnable() {
        GuildService guildService = new GuildService();

        this.funnyCommands = FunnyCommands.configuration(() -> this)
                .placeholders(PLACEHOLDERS)
                .registerComponents()
                .type(new PlayerType(super.getServer()))
                .type("guild", ((origin, required, guild) -> guildService.guilds.get(guild)))
                .create();
    }

    @Override
    public void onDisable() {
        funnyCommands.dispose();
    }

    @FunnyComponent
    private static final class TestCommand {

        @FunnyCommand(
            name = "${fc.test-alias}",
            permission = "fc.test",
            usage = "/${fc.test-alias} <player>",
            completer = { "online-players", "guilds"},
            parameters = { "player:target", "guild:arg-guild" }
        )
        SenderResponse test(CommandSender sender, @Arg("target") @Nillable Player target, @Arg("arg-guild") Option<Guild> guild) {
            System.out.println(sender + " called " + target + " and " + guild.getOrNull());
            return new SenderResponse(target, "Test ${fc.time}");
        }

    }

}
