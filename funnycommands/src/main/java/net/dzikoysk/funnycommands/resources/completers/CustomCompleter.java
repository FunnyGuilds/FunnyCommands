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

package net.dzikoysk.funnycommands.resources.completers;

import net.dzikoysk.funnycommands.resources.Completer;
import net.dzikoysk.funnycommands.resources.Origin;
import org.panda_lang.utilities.commons.function.TriFunction;

import java.util.List;

public class CustomCompleter implements Completer {

    private final String name;
    private final TriFunction<Origin, String, Integer, List<String>> completer;

    public CustomCompleter(String name, TriFunction<Origin, String, Integer, List<String>> completer) {
        this.name = name;
        this.completer = completer;
    }

    @Override
    public List<String> apply(Origin origin, String prefix, Integer limit) {
        return completer.apply(origin, prefix, limit);
    }

    @Override
    public String getName() {
        return name;
    }

}
