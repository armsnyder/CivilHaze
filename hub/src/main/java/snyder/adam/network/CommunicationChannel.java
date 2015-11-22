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
