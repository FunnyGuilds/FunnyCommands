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

package net.dzikoysk.funnycommands.responses;

import io.vavr.control.Option;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public final class SenderResponse {

    private final @Nullable CommandSender sender;
    private final String response;

    public SenderResponse(@Nullable CommandSender commandSender, String response) {
        this.sender = commandSender;
        this.response = response;
    }

    public SenderResponse(String response) {
        this(null, response);
    }

    public Option<CommandSender> getSender() {
        return Option.of(sender);
    }

    @Override
    public String toString() {
        return response;
    }

}
