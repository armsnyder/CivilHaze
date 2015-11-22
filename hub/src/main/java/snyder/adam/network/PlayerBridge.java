/*
 * Come Again is an interactive art piece in which participants perform a series of reckless prison breaks.
 * Copyright (C) 2015  Adam Snyder
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version. Any redistribution must give proper attribution to the original author.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
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
