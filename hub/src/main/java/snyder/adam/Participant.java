/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam;

/**
 * @author Adam Snyder
 */
public class Participant {
    private final String ipAddress;
    private long lastPing;

    public Participant(String ipAddress) {
        this.ipAddress = ipAddress;
        setLastPing();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public long getLastPing() {
        return lastPing;
    }

    public void setLastPing() {
        this.lastPing = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return ipAddress;
    }
}
