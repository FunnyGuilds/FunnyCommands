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

package net.dzikoysk.funnycommands.commands;

import org.panda_lang.utilities.commons.CharacterUtils;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.collection.FixedStack;
import org.panda_lang.utilities.commons.collection.IStack;
import org.panda_lang.utilities.commons.text.ContentJoiner;
import org.panda_lang.utilities.commons.text.MessageFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CommandUtils {

    private static final char[] TEXT_OPERATORS = { '"', '\'' };

    private CommandUtils() { }

    public static <T> List<String> collectCompletions(Iterable<T> collection, String prefix, int limit, Function<Integer, List<String>> listFunction, Function<T, String> toStringFunction) {
        List<String> completions = listFunction.apply(limit);
        int limiter = 0;

        for (T element : collection) {
            String text = toStringFunction.apply(element);

            if (!text.startsWith(prefix)) {
                continue;
            }

            completions.add(toStringFunction.apply(element));

            if (++limiter == limit) {
                break;
            }
        }

        return completions;
    }

    static String[] normalize(String[] arguments) {
        List<String> normalizedArguments = new ArrayList<>(arguments.length);
        IStack<Character> lock = new FixedStack<>(1);
        List<String> cache = null;

        for (int index = 0; index < arguments.length; index++) {
            String raw = arguments[index];

            if (raw.isEmpty()) {
                continue;
            }

            if (lock.isEmpty()) {
                char firstCharacter = raw.charAt(0);

                if (CharacterUtils.belongsTo(firstCharacter, TEXT_OPERATORS)) {
                    cache = new ArrayList<>(arguments.length - index);
                    cache.add(raw);

                    lock.push(firstCharacter); // enable lock
                    continue;
                }

                normalizedArguments.add(raw);
                continue;
            }

            char lastCharacter = raw.charAt(raw.length() - 1);
            Objects.requireNonNull(cache).add(raw);

            if (CharacterUtils.belongsTo(lastCharacter, TEXT_OPERATORS) && lock.peek() == lastCharacter) {
                String joinedArgument = ContentJoiner.on(" ").join(cache).toString(); // join cached arguments
                joinedArgument = joinedArgument.substring(1, joinedArgument.length() - 1); // remove text operators

                normalizedArguments.add(joinedArgument);
                lock.pop(); // remove lock
            }
        }

        return normalizedArguments.toArray(StringUtils.EMPTY_ARRAY);
    }

    static List<String> format(MessageFormatter formatter, String[] array) {
        return Stream.of(array)
                .map(formatter::format)
                .collect(Collectors.toList());
    }

}
