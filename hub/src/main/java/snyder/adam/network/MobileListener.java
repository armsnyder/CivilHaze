package snyder.adam.network;

/**
 * @author Adam Snyder
 * Listens for communication from mobile devices
 */
public interface MobileListener {

    void onButtonPress(String button);

    void onButtonRelease(String button);

    void onVote(String[] votedFor);
}
