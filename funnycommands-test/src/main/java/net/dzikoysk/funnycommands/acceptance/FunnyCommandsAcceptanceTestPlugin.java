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
import net.dzikoysk.funnycommands.FunnyCommandsException;
import net.dzikoysk.funnycommands.FunnyCommandsPlugin;
import net.dzikoysk.funnycommands.responses.SenderResponse;
import net.dzikoysk.funnycommands.stereotypes.Arg;
import net.dzikoysk.funnycommands.stereotypes.Executor;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
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
        this.funnyCommands = FunnyCommands.configuration(() -> this)
                .placeholders(placeholders)
                .commands(TestCommand.class)
                .type("player", Player.class, (origin, username) -> super.getServer().getPlayer(username))
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

    @FunnyCommand(name = "${fc.test-alias}", permission = "fc.test", usage = "/${fc.test-alias} <player>")
    private static final class TestCommand {

        @Executor({ "player:target" })
        SenderResponse test(@Arg("target") @Nullable Player target) {
            System.out.println("target: " + target);
            return new SenderResponse(target, "Test ${fc.time}");
        }

    }

}
