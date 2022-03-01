package net.dzikoysk.funnycommands.commands;

import net.dzikoysk.funnycommands.FunnyCommands;
import net.dzikoysk.funnycommands.FunnyCommandsPlugin;
import net.dzikoysk.funnycommands.resources.Bind;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.panda_lang.utilities.inject.DependencyInjection;
import org.panda_lang.utilities.inject.Injector;
import org.panda_lang.utilities.inject.Resources;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DynamicCommandTest {

    @Test
    void shouldCallMethod() throws Exception {
        //noinspection Convert2MethodRef
        Supplier<JavaPlugin> plugin = () -> new FunnyCommandsPlugin();
        DynamicCommandTest command = new DynamicCommandTest();
        Injector injector = DependencyInjection.createInjector();
        AtomicBoolean called = new AtomicBoolean(false);

        FunnyCommands funnyCommands = FunnyCommands.configuration(plugin)
                .injector(injector)
                .command(command)
                .bind(resources -> resources.on(AtomicBoolean.class).assignInstance(called))
                .install();

        CommandInfo info = new CommandInfo(
                "test",
                "desc",
                "",
                "usage",
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyMap(),
                Collections.emptyMap(),
                false,
                true,
                false,
                true
        );

        CommandMetadata metadata = new CommandMetadata(command, info, injector.forMethod(DynamicCommandTest.class.getMethod("methodToCall", AtomicBoolean.class)), null);
        DynamicCommand dynamicCommand = new DynamicCommand(funnyCommands, plugin, new CommandStructure(metadata), info);
        dynamicCommand.execute(null, "test", new String[0]);

        assertTrue(called.get());
    }

    public void methodToCall(AtomicBoolean called) {
        called.set(true);
    }

}
