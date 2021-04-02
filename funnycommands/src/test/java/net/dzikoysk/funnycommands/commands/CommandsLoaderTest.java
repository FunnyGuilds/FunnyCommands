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

import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.stereotypes.FunnyCommand;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class CommandsLoaderTest {

    @Test
    void loadCommands() {
        FunnyCommands funnyCommands = FunnyCommands.configuration(() -> {
            throw new UnsupportedOperationException();
        }).install();

        CommandStructure tree = funnyCommands.getCommandsLoader().loadCommands(Arrays.asList(
                new CommandA(),
                new CommandB(), new CommandB1(), new CommandB2(), new CommandB12(),
                new CommandC()
        ));

        tree.print();

        String[] subdomains = tree.collectCommandsStartingWith("b 1").stream()
                .map(CommandStructure::getName)
                .sorted()
                .toArray(String[]::new);

        Assertions.assertArrayEquals(new String[] { "b 1", "b 1 2" }, subdomains);
    }

    private static final class CommandA {
        @FunnyCommand(name = "a")
        protected boolean a() { return true; }
    }

    private static final class CommandB {
        @FunnyCommand(name = "b")
        protected boolean b() { return true; }
    }

    private static final class CommandB1 {
        @FunnyCommand(name = "b 1")
        protected boolean b1() { return true; }
    }

    private static final class CommandB2 {
        @FunnyCommand(name = "b 2")
        protected boolean b2() { return true; }
    }

    private static final class CommandB12 {
        @FunnyCommand(name = "b 1 2")
        protected boolean b12() { return true; }
    }

    private static final class CommandC {
        @FunnyCommand(name = "c")
        protected boolean c() { return true; }
    }

}