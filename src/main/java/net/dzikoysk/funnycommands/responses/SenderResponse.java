package net.dzikoysk.funnycommands.responses;

import io.vavr.control.Option;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public final class SenderResponse {

    private final @Nullable CommandSender sender;
    private final String response;

    public SenderResponse(@Nullable CommandSender commandSender, String response) {
        this.sender = commandSender;
        this.response = response;
    }

    public SenderResponse(String response) {
        this(null, response);
    }

    public Option<CommandSender> getSender() {
        return Option.of(sender);
    }

    @Override
    public String toString() {
        return response;
    }

}
