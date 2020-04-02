package net.dzikoysk.funnycommands.responses;

public final class SenderResponse {

    private final String response;

    public SenderResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return response;
    }

}
