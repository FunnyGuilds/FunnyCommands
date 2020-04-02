package net.dzikoysk.funnycommands;

public final class FunnyCommandsAcceptanceTestPlugin extends FunnyCommandsPlugin {

    private FunnyCommands commands;

    @Override
    public void onEnable() {
        this.commands = FunnyCommands.configuration(this)
                .placeholders(/* Configuration map */)
                .commands(/* Array of classes */)
                .type(/* type name, type deserializer */)
                .bind(/* annotation, processor */)
                .exceptionHandler(/* exception type, exception handler */)
                .create();
    }

    @Override
    public void onDisable() {
        commands.dispose();
    }

}
