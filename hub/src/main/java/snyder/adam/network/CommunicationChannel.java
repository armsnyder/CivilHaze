package snyder.adam.network;

/**
 * @author Adam Snyder
 * Specifies an interface that a given communication protocol (wi-fi, Bluetooth) must adhere to
 */
public abstract class CommunicationChannel {

    protected MobileListener listener;

    public abstract void sendMessage(Object message);

    /**
     * Parses an incoming message from a mobile device and passes it on to the MobileListener
     */
    protected void handleMessage(String message) {
        //TODO: write this
    }
}
