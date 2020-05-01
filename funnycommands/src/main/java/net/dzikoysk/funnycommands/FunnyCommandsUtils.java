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

package net.dzikoysk.funnycommands;

import net.dzikoysk.funnycommands.commands.CommandParameter;
import org.bukkit.ChatColor;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class FunnyCommandsUtils {

    private static final String USAGE_PREFIX = "&c/";

    public static final String ARGUMENT_SEPARATOR = " ";

    private static final String USAGE_REQUIRED_PARAMETER_OPEN = "<";
    private static final String USAGE_REQUIRED_PAREMETER_CLOSE = ">";

    private static final String USAGE_OPTIONAL_PARAMETER_OPEN = "[";
    private static final String USAGE_OPTIONAL_PARAMETER_CLOSE = "]";

    private FunnyCommandsUtils() { }

    public static String translate(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    static URL getURL(Class<?> clazz) {
        try {
            return clazz.getProtectionDomain().getCodeSource().getLocation().toURI().toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new FunnyCommandsException("Cannot get URL", e);
        }
    }

    public static <T extends Number> T parseNumber(String str, Function<String, T> parser) {
        try {
            return parser.apply(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String formatUsage(String commandName, Collection<? extends CommandParameter> parameters) {
        return USAGE_PREFIX + commandName + ARGUMENT_SEPARATOR + formatUsageParameters(parameters);
    }

    public static String formatUsageParameters(Collection<? extends CommandParameter> parameters) {
        return parameters.stream()
                .map(FunnyCommandsUtils::formatUsageParameter)
                .collect(Collectors.joining(ARGUMENT_SEPARATOR));
    }

    public static String formatUsageParameter(CommandParameter parameter) {
        if (!parameter.isOptional()) {
            return formatUsageRequiredParameter(parameter.getName());
        } else {
            return formatUsageOptionalParameter(parameter.getName());
        }
    }

    public static String formatUsageRequiredParameter(String parameterName) {
        return formatUsageParameter(parameterName, USAGE_REQUIRED_PARAMETER_OPEN, USAGE_REQUIRED_PAREMETER_CLOSE);
    }

    public static String formatUsageOptionalParameter(String parameterName) {
        return formatUsageParameter(parameterName, USAGE_OPTIONAL_PARAMETER_OPEN, USAGE_OPTIONAL_PARAMETER_CLOSE);
    }

    public static String formatUsageParameter(String parameterName, String open, String close) {
        return open + parameterName + close;
    }

}
