/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
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
import java.util.Collection;
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
    private MobileListener listener;
    private boolean hasError = false;
    private static Server _instance;

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
        _instance = this;
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

    public static Server getInstance() {
        return _instance;
    }

    public Collection<Participant> getParticipants() {
        return participantMap.values();
    }

    public void setListener(MobileListener listener) {
        this.listener = listener;
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
            JSONObject responseObject = new JSONObject();
            String participantId = t.getRemoteAddress().getHostName();
            try {
                Headers headers = t.getResponseHeaders();
                headers.add("Content-Type", "application/json");
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
                headers.add("Access-Control-Allow-Headers",
                        "Content-Type, Authorization, Content-Length, X-Requested-With");
                switch (t.getRequestMethod()) {
                    case "GET":
                        handleGet(t, path, responseObject, participantId);
                        break;
                    case "POST":
                        handlePost(t, path, responseObject, participantId);
                        break;
                    case "OPTIONS":
                        respondWithSuccess(t, responseObject);
                    default:
                        respondWithError(t, responseObject);
                }
            } catch (Exception e) {
                respondWithError(t, responseObject);
            }
            t.close();
        }

        private void postProcess(JSONObject responseObject, String participantId) {
            synchronized (participantMap) {
                if (participantMap.containsKey(participantId)) {
                    responseObject.put("connected", true);
                    Participant participant = participantMap.get(participantId);
                    participant.setLastPing();
                    listener.onPing(participant);
                    JSONArray messages = participant.retrieveMessages();
                    if (messages.length() > 0) {
                        responseObject.put("messages", messages);
                    }
                } else {
                    responseObject.put("connected", false);
                }
            }
        }

        private void handleGet(HttpExchange t, String[] path, JSONObject responseObject, String participantId)
                throws IOException {
            switch (path[1]) {
                case "connect":
                    handleConnect(t, participantId, responseObject);
                    break;
                case "disconnect":
                    handleDisconnect(t, participantId, responseObject);
                    break;
                case "ping":
                    respondWithSuccess(t, responseObject);
                    break;
                default:
                    respondWithError(t, responseObject, "bad path");
            }
        }

        private void handlePost(HttpExchange t, String[] path, JSONObject responseObject, String participantId)
                throws IOException {
            switch (path[1]) {
                case "input":
                    handleInput(t, path, participantId, responseObject);
                    break;
                case "ping":
                    respondWithSuccess(t, responseObject);
                    break;
                case "connect":
                    handleConnect(t, participantId, responseObject);
                    break;
                case "disconnect":
                    handleDisconnect(t, participantId, responseObject);
                    break;
                default:
                    respondWithError(t, responseObject, "bad path");
            }
        }

        private void handleConnect(HttpExchange t, String participantId, JSONObject responseObject) throws IOException {
            synchronized (participantMap) {
                if (!participantMap.containsKey(participantId)) {
                    if (participantMap.size() < MAX_PARTICIPANTS) {
                        Participant connectedParticipant = new Participant(participantId);
                        participantMap.put(participantId, connectedParticipant);
                        listener.onConnect(connectedParticipant);
                    }
                }
            }
            respondWithSuccess(t, responseObject);
        }

        private void handleDisconnect(HttpExchange t, String participantId, JSONObject responseObject)
                throws IOException {
            synchronized (participantMap) {
                if (participantMap.containsKey(participantId)) {
                    listener.onDisconnect(participantMap.get(participantId));
                    participantMap.remove(participantId);
                }
            }
            respondWithSuccess(t, responseObject);
        }

        private void handleInput(HttpExchange t, String[] path, String participantId, JSONObject responseObject)
                throws IOException {
            synchronized (participantMap) {
                if (participantMap.containsKey(participantId)) {
                    switch (path[2]) {
                        case "button":
                            handleButton(t, path, participantId, responseObject);
                            break;
                        case "joystick":
                            handleJoystick(t, participantId, responseObject);
                            break;
                        case "vote":
                            handleVote(t, participantId, responseObject);
                            break;
                        default:
                            respondWithError(t, responseObject, "bad path");
                    }
                } else {
                    respondWithError(t, responseObject, "connection required");
                }
            }
        }

        private void handleJoystick(HttpExchange t, String participantId, JSONObject responseObject)
                throws IOException {
            JSONObject input = new JSONObject(Util.getStringFromInputStream(t.getRequestBody()));
            if (input.has("angle") && input.has("magnitude")) {
                try {
                    listener.onJoystickInput(participantMap.get(participantId), input.getDouble("angle"),
                            input.getDouble("magnitude"));
                    respondWithSuccess(t, responseObject);
                } catch (JSONException e) {
                    respondWithError(t, responseObject, "invalid input");
                }
            } else {
                respondWithError(t, responseObject, "invalid input");
            }
        }

        private void handleButton(HttpExchange t, String[] path, String participantId, JSONObject responseObject)
                throws IOException {
            String button = path[3];
            switch (path[4]) {
                case "on":
                    listener.onButtonPress(participantMap.get(participantId), button);
                    respondWithSuccess(t, responseObject);
                    break;
                case "off":
                    listener.onButtonRelease(participantMap.get(participantId), button);
                    respondWithSuccess(t, responseObject);
                    break;
                default:
                    respondWithError(t, responseObject, "bad path");
            }
        }

        private void handleVote(HttpExchange t, String participantId, JSONObject responseObject) throws IOException {
            String data = Util.getStringFromInputStream(t.getRequestBody());
            JSONArray ids = new JSONArray(data);
            String[] votes = new String[ids.length()];
            for (int i = 0; i < ids.length(); i++) {
                try {
                    votes[i] = ids.getString(i);
                } catch (JSONException e) {
                    respondWithError(t, responseObject, "Player ids are not Strings");
                    return;
                }
            }
            listener.onVote(participantMap.get(participantId), votes);
            respondWithSuccess(t, responseObject);
        }

        private void respondWithError(HttpExchange t, JSONObject responseObject) throws IOException {
            respondWithError(t, responseObject, "true");
        }

        private void respondWithError(HttpExchange t, JSONObject responseObject, Object errorMessage)
                throws IOException {
            responseObject.put("error", errorMessage);
            respond(t, ERROR_CODE, responseObject);
        }

        private void respondWithSuccess(HttpExchange t, JSONObject responseObject, Object successMessage)
                throws IOException {
            responseObject.put("success", successMessage);
            respond(t, SUCCESS_CODE, responseObject);
        }

        private void respondWithSuccess(HttpExchange t, JSONObject responseObject) throws IOException {
            respondWithSuccess(t, responseObject, "true");
        }

        private void respond(HttpExchange t, int code, JSONObject responseObject)
                throws IOException {
            postProcess(responseObject, t.getRemoteAddress().getHostName());
            if (!responseObject.has("error")) responseObject.put("error", "false");
            if (!responseObject.has("success")) responseObject.put("success", "false");
            if (responseObject.get("success").toString().equals("true") && responseObject.get("error").toString().equals("true")) {
                // TODO: Figure out why this is happening
                System.out.println(t.getRequestURI().getPath());
            }
            String response = responseObject.toString();
            t.sendResponseHeaders(code, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
