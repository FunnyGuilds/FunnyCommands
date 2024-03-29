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

import net.dzikoysk.funnycommands.resources.Context;
import org.apache.commons.lang.StringUtils;
import panda.utilities.CharacterUtils;
import panda.utilities.collection.FixedStack;
import panda.utilities.text.Formatter;
import panda.utilities.text.Joiner;
import panda.std.stream.PandaStream;
import panda.utilities.collection.IStack;

import java.util.ArrayList;
import java.util.Arrays;
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

            if (!StringUtils.startsWithIgnoreCase(text, prefix)) {
                continue;
            }

            completions.add(toStringFunction.apply(element));

            if (++limiter == limit) {
                break;
            }
        }

        return completions;
    }

    public static <T extends Enum<T>> List<String> collectCompletions(Enum<T>[] elements, String prefix, int limit, Function<Integer, List<String>> listFunction) {
        return CommandUtils.collectCompletions(Arrays.asList(elements), prefix, limit, listFunction, element -> element.name().toLowerCase());
    }

    static String[] normalize(String[] arguments) {
        List<String> normalizedArguments = new ArrayList<>(arguments.length);
        IStack<Character> lock = new FixedStack<>(1);
        List<String> cache = null;

        for (int index = 0; index < arguments.length; index++) {
            String raw = arguments[index];

            if (raw.isEmpty()) {
                normalizedArguments.add(raw);
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
                String joinedArgument = Joiner.on(" ").join(cache).toString(); // join cached arguments
                joinedArgument = joinedArgument.substring(1, joinedArgument.length() - 1); // remove text operators

                normalizedArguments.add(joinedArgument);
                lock.pop(); // remove lock
            }
        }

        return normalizedArguments.toArray(new String[0]);
    }

    static List<String> format(Formatter formatter, String[] array) {
        return Stream.of(array)
                .filter(value -> !value.trim().isEmpty())
                .map(formatter::format)
                .flatMap(value -> PandaStream.of(value.split(","))
                        .map(String::trim)
                        .filterNot(String::isEmpty)
                        .toStream())
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public static Context getContext(Object... injectorArgs) {
        return (Context) injectorArgs[1];
    }

    public static CommandInfo getCommandInfo(Object... injectorArgs) {
        return (CommandInfo) injectorArgs[0];
    }

}
