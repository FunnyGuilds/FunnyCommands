package net.dzikoysk.funnycommands;

public class FunnyCommandsException extends RuntimeException {

    public FunnyCommandsException(String message) {
        super(message);
    }

    public FunnyCommandsException(String message, Throwable cause) {
        super(message, cause);
    }

}
