/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze.network;

import com.armsnyder.civilhaze.Participant;

/**
 * @author Adam Snyder
 * Listens for communication from mobile devices
 */
public interface MobileListener {

    void onButtonPress(Participant participant, String button);

    void onButtonRelease(Participant participant, String button);

    void onJoystickInput(Participant participant, double angle, double magnitude);

    void onVote(Participant participant, String[] votedFor);

    void onConnect(Participant participant);

    void onDisconnect(Participant participant);

    void onPing(Participant participant);

    void onServerReady();

    void onServerFatalError(String description);

    void onServerStopped();
}
