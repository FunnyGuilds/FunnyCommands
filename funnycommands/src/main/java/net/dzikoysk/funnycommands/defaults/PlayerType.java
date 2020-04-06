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

package net.dzikoysk.funnycommands.defaults;

import net.dzikoysk.funnycommands.commands.CommandDataType;
import net.dzikoysk.funnycommands.commands.Origin;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.lang.reflect.Parameter;

public final class PlayerType implements CommandDataType<Player> {

    private final Server server;

    public PlayerType(Server server) {
        this.server = server;
    }

    @Override
    public Player apply(Origin origin, Parameter parameter, String argument) {
        return server.getPlayer(argument);
    }

    @Override
    public String getName() {
        return "player";
    }

}
