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

package net.dzikoysk.funnycommands.resources.responses;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;
import panda.std.Option;

public final class MultilineResponse {

    private final @Nullable CommandSender target;
    private final Object[] lines;

    public MultilineResponse(Object... lines) {
        this(null, lines);
    }

    public MultilineResponse(@Nullable CommandSender target, Object[] lines) {
        this.target = target;
        this.lines = lines;
    }

    public Option<CommandSender> getTarget() {
        return Option.of(target);
    }

    public Object[] getLines() {
        return lines;
    }

}
