/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Map;

/**
 * @author Adam Snyder
 */
public class Participant {
    private final String ipAddress;
    private long lastPing;
    private JSONArray outgoingMessages;

    public Participant(String ipAddress) {
        this.ipAddress = ipAddress;
        outgoingMessages = new JSONArray();
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

    public void sendMessage(JSONObject message) {
        outgoingMessages.put(message);
    }

    public void sendMessage(Map<String, Object> message) {
        sendMessage(new JSONObject(message));
    }

    public void sendMessage(String key, Object value) {
        sendMessage(Collections.singletonMap(key, value));
    }

    public JSONArray retrieveMessages() {
        JSONArray result = outgoingMessages;
        outgoingMessages = new JSONArray();
        return result;
    }
}
