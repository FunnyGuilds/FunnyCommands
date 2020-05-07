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
import net.dzikoysk.funnycommands.FunnyCommandsConstants;
import net.dzikoysk.funnycommands.FunnyCommandsPlugin;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Origin;
import net.dzikoysk.funnycommands.resources.responses.MultilineResponse;
import net.dzikoysk.funnycommands.resources.types.PlayerType;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import net.dzikoysk.funnycommands.stereotypes.FunnyComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.commons.collection.Maps;
import org.panda_lang.utilities.commons.text.ContentJoiner;
import org.panda_lang.utilities.inject.annotations.Injectable;

import javax.annotation.Nullable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class FunnyCommandsAcceptanceTestPlugin extends FunnyCommandsPlugin {

    // Test classes

    private static final Map<String, String> CONFIGURATION = Maps.of("name", "test");
    private static final Map<String, Function<String, String>> PLACEHOLDERS = new HashMap<String, Function<String, String>>() {{
        put("name", CONFIGURATION::get);
        put("time", key -> new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
    }};

    private static final class GuildService {
        private final Map<String, Guild> guilds = new HashMap<String, Guild>() {{
            for (int index = 0; index < 10; index++) {
                put("test-" + index + "-guild", new Guild("test-" + index + "-guild"));
            }
        }};
    }

    private static final class Guild {
        private final String name;
        public Guild(String name) { this.name = name; }
    }

    @Injectable
    @Retention(RetentionPolicy.RUNTIME)
    private @interface PluginInstance { }

    @Retention(RetentionPolicy.RUNTIME)
    private @interface ArrayLengthValidator {
        int maxLength();
    }

    // Example plugin

    private FunnyCommands funnyCommands;

    @Override
    public void onEnable() {
        FunnyCommandsAcceptanceTestPlugin plugin = this;
        GuildService guildService = new GuildService();

        this.funnyCommands = FunnyCommands.configuration(() -> this)
                .placeholders(PLACEHOLDERS)
                .registerProcessedComponents()
                .type(new PlayerType(super.getServer()))
                .type("guild", Guild.class, ((origin, required, guild) ->  {
                    return guildService.guilds.get(guild);
                }))
                .bind(resources -> {
                    resources.annotatedWith(PluginInstance.class).assignHandler(((parameter, pluginInstance, injectorArgs) -> {
                        return plugin;
                    }));
                })
                .completer("guilds", (origin, prefix, limit) -> {
                    return CommandUtils.collectCompletions(guildService.guilds.values(), prefix, limit, ArrayList::new, guild -> guild.name);
                })
                .validator(ArrayLengthValidator.class, null, ((origin, arrayLengthValidator, parameter, value) -> {
                    if (!parameter.getType().isArray()) {
                        throw new IllegalArgumentException(parameter + "is not an array");
                    }

                    Object[] array = (Object[]) value;

                    if (array.length > arrayLengthValidator.maxLength()) {
                        origin.getCommandSender().sendMessage(origin.format("&cToo many arguments"));
                        return false;
                    }

                    return true;
                }))
                .hook();
    }

    @Override
    public void onDisable() {
        funnyCommands.dispose();
    }

    @FunnyComponent
    private static final class TestCommand {

        @FunnyCommand(
            name = "${name}",
            description = "Test ${name} command",
            permission = "funnycommands.test",
            usage = "/${name} <player> [guild]",
            completer = { "online-players:5", "guilds:5"},
            parameters = { "player:target", "[guild:arg-guild]" },
            async = true
        )
        MultilineResponse test(Origin origin, CommandSender sender, @Arg @Nullable Player target, @Arg("arg-guild") Option<Guild> guild) {
            return new MultilineResponse(
                    "Test ${time} > " + sender + " called " + target + " and " + guild.getOrNull() + " in " + Thread.currentThread().getName(),
                    "Subcommands: ",
                    ContentJoiner.on(", ").join(origin.getCommandStructure().getSubcommandsNames())
            );
        }

        @FunnyCommand(name = "${name} version", description = "Test subcommand", usage = "/${name} version")
        protected String version() {
            return "&a" + FunnyCommandsConstants.VERSION + " in " + Thread.currentThread().getName();
        }

        @FunnyCommand(name = "root")
        protected String root() {
            return "root";
        }

        @FunnyCommand(name = "root sub1")
        protected String rootSub1() {
            return "root sub1";
        }

        @FunnyCommand(name = "root sub2")
        protected String rootSub2() {
            return "root sub2";
        }

        @FunnyCommand(name = "root sub2 sub1")
        protected String rootSub3() {
            return "root sub2 sub1";
        }

        @FunnyCommand(name = "kerneltest")
        protected void test(CommandSender sender, @PluginInstance FunnyCommandsAcceptanceTestPlugin plugin) {
            sender.sendMessage("Siema, to dziala " + plugin.getName());
        }

        @FunnyCommand(name = "varargs", parameters = "string:content...")
        protected String varargs(@Arg @ArrayLengthValidator(maxLength = 4) String[] content) {
            return ContentJoiner.on(", ").join(content).toString();
        }

    }

}
