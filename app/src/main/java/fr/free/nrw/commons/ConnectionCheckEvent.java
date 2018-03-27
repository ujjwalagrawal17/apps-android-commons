package fr.free.nrw.commons;

public class ConnectionCheckEvent {

    public boolean isConnected;

    public ConnectionCheckEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean connState() {
        return isConnected;
    }
}
