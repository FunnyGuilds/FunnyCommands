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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.panda_lang.utilities.commons.ArrayUtils;

class CommandUtilsTest {

    private static final String[] ARGUMENTS = ArrayUtils.of("param1", "'text", "with", "spaces'", "param2");

    @Test
    void normalize() {
        Assertions.assertArrayEquals(ArrayUtils.of("param1", "text with spaces", "param2"), CommandUtils.normalize(ARGUMENTS));
    }

}