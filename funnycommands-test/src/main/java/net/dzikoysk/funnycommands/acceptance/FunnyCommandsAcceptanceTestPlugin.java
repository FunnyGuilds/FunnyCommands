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

import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsConstants;
import net.dzikoysk.funnycommands.FunnyCommandsPlugin;
import net.dzikoysk.funnycommands.commands.CommandUtils;
import net.dzikoysk.funnycommands.resources.Context;
import net.dzikoysk.funnycommands.resources.responses.MultilineResponse;
import net.dzikoysk.funnycommands.resources.types.PlayerType;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.panda_lang.utilities.inject.annotations.Injectable;
import panda.std.Option;
import panda.utilities.text.Joiner;

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

    private static final Map<String, Function<String, String>> PLACEHOLDERS = new HashMap<String, Function<String, String>>() {{
        put("name", key -> "test");
        put("aliases", key -> "t1, t2");
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
                .registerDefaultComponents()
                .registerComponent(new TestCommand())
                .type(new PlayerType(super.getServer()))
                .type("guild", Guild.class, ((context, required, guild) -> guildService.guilds.get(guild)))
                .bind(resources -> resources.annotatedWith(PluginInstance.class).assignHandler(((parameter, pluginInstance, injectorArgs) -> plugin)))
                .completer("guilds", (context, prefix, limit) -> CommandUtils.collectCompletions(guildService.guilds.values(), prefix, limit, ArrayList::new, guild -> guild.name))
                .validator(ArrayLengthValidator.class, null, ((context, arrayLengthValidator, parameter, value) -> {
                    if (!parameter.getType().isArray()) {
                        throw new IllegalArgumentException(parameter + "is not an array");
                    }

                    Object[] array = (Object[]) value;

                    if (array.length > arrayLengthValidator.maxLength()) {
                        context.getCommandSender().sendMessage(context.format("&cToo many arguments"));
                        return false;
                    }

                    return true;
                }))
                .install();
    }

    @Override
    public void onDisable() {
        funnyCommands.dispose();
    }

    public static final class TestCommand {

        @FunnyCommand(name = "${name} version", description = "Test subcommand", usage = "/${name} version")
        public String version() {
            return "&a" + FunnyCommandsConstants.VERSION + " in " + Thread.currentThread().getName();
        }

        @FunnyCommand(name = "root")
        public String root() {
            return "root";
        }

        @FunnyCommand(name = "root sub1")
        public String rootSub1() {
            return "root sub1";
        }

        @FunnyCommand(name = "root sub2")
        public String rootSub2() {
            return "root sub2";
        }

        @FunnyCommand(name = "root sub2 sub1")
        public String rootSub3() {
            return "root sub2 sub1";
        }

        @FunnyCommand(name = "kerneltest")
        public void test(CommandSender sender, @PluginInstance FunnyCommandsAcceptanceTestPlugin plugin) {
            sender.sendMessage("Siema, to dziala " + plugin.getName());
        }

        @FunnyCommand(name = "varargs", parameters = "string:content...")
        public String varargs(@Arg @ArrayLengthValidator(maxLength = 4) String[] content) {
            return Joiner.on(", ").join(content).toString();
        }

        @FunnyCommand(name = "exceeded", acceptsExceeded = true)
        public void exceeded(String[] args) {
            System.out.println("Exceeded: " + Joiner.on(", ").join(args));
        }

        @FunnyCommand(
                name = "${name}",
                aliases = "${aliases}",
                description = "Test ${name} command",
                permission = "funnycommands.test",
                usage = "/${name} <player> [guild]",
                completer = "online-players:5 guilds:5",
                parameters = "player:target [guild:arg-guild]",
                async = true
        )
        public MultilineResponse test(Context context, CommandSender sender, @Arg @Nullable Player target, @Arg("arg-guild") Option<Guild> guild) {
            return new MultilineResponse(
                    "Test ${time} > " + sender + " called " + target + " and " + guild.getOrNull() + " in " + Thread.currentThread().getName(),
                    "Subcommands: ",
                    Joiner.on(", ").join(context.getCommandStructure().getSubcommandsNames())
            );
        }

    }

}
