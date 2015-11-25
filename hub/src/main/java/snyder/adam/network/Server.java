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

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import snyder.adam.Participant;
import snyder.adam.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Adam Snyder
 */
public class Server implements Runnable {

    private static final int MAX_PARTICIPANTS = 15;
    private static final long CLIENT_TIMEOUT = 15000;  // Min interval (in milliseconds) that clients must ping
    private static final long TIMEOUT_INTERVAL = 2000; // Time in milliseconds between checking for timed out clients
    private boolean isRunning = false;
    private HttpServer server = null;
    private final Map<String, Participant> participantMap;
    private final MobileListener listener;
    private boolean hasError = false;

    public Server(Map<String, Participant> participantMap, MobileListener listener, int port) throws IOException {
        this.participantMap = participantMap;
        this.listener = listener;
        try {
            server = HttpServer.create(new InetSocketAddress(InetAddress.getLocalHost(), port), 0);
        } catch (BindException e) {
            listener.onServerFatalError("Address already in use");
            return;
        }
        server.createContext("/", new InputHandler());
        server.setExecutor(null); // creates a default executor
    }

    @Override
    public void run() {
        if (server == null) return;
        try {
            updateIpTable();
        } catch (IOException e) {
            e.printStackTrace();
            signalError("Failed to read IP");
        }
        if (!hasError) {
            server.start();
            isRunning = true;
            listener.onServerReady();
        }
        while (isRunning) {
            try {
                Thread.sleep(TIMEOUT_INTERVAL);
            } catch (InterruptedException ignored) {}
            disconnectTimedOutPlayers();
        }
        server.stop(0);
        listener.onServerStopped();
    }

    public boolean isParticipantConnected(Participant participant) {
        return participantMap.containsKey(participant.getIpAddress());
    }

    public boolean isParticipantConnected(String ipAddress) {
        return participantMap.containsKey(ipAddress);
    }

    public void stop() {
        isRunning = false;
    }

    private void disconnectTimedOutPlayers() {
        long now = System.currentTimeMillis();
        synchronized (participantMap) {
            for (Iterator<Map.Entry<String, Participant>> it = participantMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry<String, Participant> entry = it.next();
                Participant participant = entry.getValue();
                if (participant.getLastPing() + CLIENT_TIMEOUT < now) {
                    listener.onDisconnect(participant);
                    it.remove();
                }
            }
        }
    }

    private void updateIpTable() throws IOException {
        String url = "http://come-again.net/api/ip/private/"+getPrivateIP();
//        String url = "http://localhost:3000/api/ip/private/"+getPrivateIP();
        try {
            String response = Util.executePost(url, "");
            JSONObject o = new JSONObject(response);
            if (!o.has("error") || ((String)o.get("error")).length() > 0) {
                signalError("Failed to update routing table");
            }
        } catch (SocketException e) {
            signalError("Failed to update routing table");
        }
    }

    private void signalError(String error) {
        listener.onServerFatalError(error);
        hasError = true;
    }

    private static String getPrivateIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    class InputHandler implements HttpHandler {

        private final int SUCCESS_CODE = 200;
        private final int ERROR_CODE = 400;

        @Override
        public void handle(HttpExchange t) throws IOException {
            String[] path = t.getRequestURI().getPath().split("/");
            try {
                Headers headers = t.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
                headers.add("Access-Control-Allow-Headers",
                        "Content-Type, Authorization, Content-Length, X-Requested-With");
                switch (t.getRequestMethod()) {
                    case "GET":
                        handleGet(t, path);
                        break;
                    case "POST":
                        handlePost(t, path);
                        break;
                    case "OPTIONS":
                        respondWithSuccess(t);
                    default:
                        respondWithError(t);
                }
            } catch (Exception e) {
                respondWithError(t);
            }
            t.close();
        }

        private void handleGet(HttpExchange t, String[] path) throws IOException {
            switch (path[1]) {
                case "connect":
                    handleConnect(t);
                    break;
                case "disconnect":
                    handleDisconnect(t);
                    break;
                case "ping":
                    handlePing(t);
                    break;
                default:
                    respondWithError(t);
            }
        }

        private void handlePost(HttpExchange t, String[] path) throws IOException {
            switch (path[1]) {
                case "input":
                    handleInput(t, path);
                    break;
                case "ping":
                    handlePing(t);
                    break;
                case "connect":
                    handleConnect(t);
                    break;
                case "disconnect":
                    handleDisconnect(t);
                    break;
                default:
                    respondWithError(t);
            }
        }

        private void handlePing(HttpExchange t) throws IOException {
            synchronized (participantMap) {
                String ip = t.getRemoteAddress().getHostName();
                if (participantMap.containsKey(ip)) {
                    Participant participant = participantMap.get(ip);
                    participant.setLastPing();
                    listener.onPing(participant);
                    respondWithSuccess(t, "true");
                } else {
                    respondWithSuccess(t, "false");
                }
            }

        }

        private void handleConnect(HttpExchange t) throws IOException {
            String ip = t.getRemoteAddress().getHostName();
            synchronized (participantMap) {
                if (participantMap.containsKey(ip)) {
                    participantMap.get(ip).setLastPing();
                    respondWithSuccess(t, "true");
                } else {
                    if (participantMap.size() < MAX_PARTICIPANTS) {
                        Participant connectedParticipant = new Participant(ip);
                        participantMap.put(ip, connectedParticipant);
                        listener.onConnect(connectedParticipant);
                        respondWithSuccess(t, "true");
                    } else {
                        respondWithSuccess(t, "false");
                    }
                }
            }
        }

        private void handleDisconnect(HttpExchange t) throws IOException {
            String ip = t.getRemoteAddress().getHostName();
            synchronized (participantMap) {
                if (participantMap.containsKey(ip)) {
                    listener.onDisconnect(participantMap.get(ip));
                    participantMap.remove(ip);
                }
            }
            respondWithSuccess(t);
        }

        private void handleInput(HttpExchange t, String[] path) throws IOException {
            String participantId = t.getRemoteAddress().getHostName();
            synchronized (participantMap) {
                if (participantMap.containsKey(participantId)) {
                    participantMap.get(participantId).setLastPing();
                    switch (path[2]) {
                        case "button":
                            handleButton(t, path, participantId);
                            break;
                        case "joystick":
                            handleJoystick(t, participantId);
                            break;
                        case "vote":
                            handleVote(t, participantId);
                            break;
                        default:
                            respondWithError(t);
                    }
                } else {
                    respondWithError(t);
                }
            }
        }

        private void handleJoystick(HttpExchange t, String participantId) throws IOException {
            JSONObject input = new JSONObject(Util.getStringFromInputStream(t.getRequestBody()));
            if (input.has("angle") && input.has("magnitude")) {
                try {
                    listener.onJoystickInput(participantMap.get(participantId), input.getDouble("angle"),
                            input.getDouble("magnitude"));
                    respondWithSuccess(t);
                } catch (JSONException e) {
                    respondWithError(t, "invalid input");
                }
            } else {
                respondWithError(t, "invalid input");
            }
        }

        private void handleButton(HttpExchange t, String[] path, String participantId) throws IOException {
            String button = path[3];
            switch (path[4]) {
                case "on":
                    listener.onButtonPress(participantMap.get(participantId), button);
                    respondWithSuccess(t);
                    break;
                case "off":
                    listener.onButtonRelease(participantMap.get(participantId), button);
                    respondWithSuccess(t);
                    break;
                default:
                    respondWithError(t);
            }
        }

        private void handleVote(HttpExchange t, String participantId) throws IOException {
            String data = Util.getStringFromInputStream(t.getRequestBody());
            JSONArray ids = new JSONArray(data);
            String[] votes = new String[ids.length()];
            for (int i = 0; i < ids.length(); i++) {
                try {
                    votes[i] = ids.getString(i);
                } catch (JSONException e) {
                    respondWithError(t, "Player ids are not Strings");
                    return;
                }
            }
            listener.onVote(participantMap.get(participantId), votes);
            respondWithSuccess(t);
        }

        private void respondWithError(HttpExchange t) throws IOException {
            t.sendResponseHeaders(ERROR_CODE, -1);
        }

        private void respondWithError(HttpExchange t, String response) throws IOException {
            t.sendResponseHeaders(ERROR_CODE, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void respondWithSuccess(HttpExchange t) throws IOException {
            t.sendResponseHeaders(SUCCESS_CODE, -1);
        }

        private void respondWithSuccess(HttpExchange t, String response) throws IOException {
            if (!response.substring(0, 1).equals("{")) response = "{\"result\": \""+response+"\"}";
            t.sendResponseHeaders(SUCCESS_CODE, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
