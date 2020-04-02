package net.dzikoysk.funnycommands;

public final class FunnyCommandsIntegrationPlugin extends FunnyCommandsPlugin {

    @Override
    public void onEnable() {
        super.getCommand("fc").setExecutor((commandSender, command, s, strings) -> {
            System.out.println("FunnyCommands v" + FunnyCommandsConstants.VERSION);
            return false;
        });
    }

}
