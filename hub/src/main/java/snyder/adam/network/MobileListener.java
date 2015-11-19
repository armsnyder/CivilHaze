package snyder.adam.network;

import snyder.adam.Participant;

/**
 * @author Adam Snyder
 * Listens for communication from mobile devices
 */
public interface MobileListener {

    void onButtonPress(Participant participant, String button);

    void onButtonRelease(Participant participant, String button);

    void onVote(Participant participant, int[] votedFor);

    void onConnect(Participant participant);

    void onDisconnect(Participant participant);

    void onPing(Participant participant);

    void onServerReady();

    void onServerFatalError(String description);
}
