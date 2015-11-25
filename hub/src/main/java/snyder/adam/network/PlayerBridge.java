/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.network;

import snyder.adam.exceptions.EmptyChannelListException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam Snyder
 * Maintains connection between the game and the player devices and contains methods for communication
 */
public class PlayerBridge extends CommunicationChannel {

    List<CommunicationChannel> channels = new ArrayList<>(1);

    public PlayerBridge(MobileListener listener) {
        this.listener = listener;
    }

    public PlayerBridge(MobileListener listener, List<CommunicationChannel> channels) {
        this.listener = listener;
        this.channels = channels;
    }

    public PlayerBridge(MobileListener listener, CommunicationChannel channel) {
        this.listener = listener;
        addChannel(channel);
    }

    public void addChannel(CommunicationChannel channel) {
        this.channels.add(channel);
    }

    @Override
    public void sendMessage(Object message) {
        if (channels.size() == 0) {
            throw new EmptyChannelListException();
        }
        for (CommunicationChannel c : channels) {
            c.sendMessage(message);
        }
    }
}
