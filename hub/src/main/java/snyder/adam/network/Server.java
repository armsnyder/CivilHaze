package snyder.adam.network;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;
import snyder.adam.Participant;
import snyder.adam.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private final HttpServer server;
    private final Map<Integer, Participant> participantMap;
    private final MobileListener listener;
    private boolean hasError = false;

    public Server(Map<Integer, Participant> participantMap, MobileListener listener, int port) throws IOException {
        this.participantMap = participantMap;
        this.listener = listener;
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new InputHandler());
        server.setExecutor(null); // creates a default executor
    }

    @Override
    public void run() {
        server.start();
        try {
            updateIpTable();
        } catch (IOException e) {
            signalError("Failed to read IP");
        }
        isRunning = true;
        if (!hasError) listener.onServerReady();
        while (isRunning) {
            try {
                Thread.sleep(TIMEOUT_INTERVAL);
            } catch (InterruptedException ignored) {}
            disconnectTimedOutPlayers();
        }

    }

    public boolean isParticipantConnected(Participant participant) {
        return participantMap.containsKey(participant.getId());
    }

    public boolean isParticipantConnected(int id) {
        return participantMap.containsKey(id);
    }

    private void disconnectTimedOutPlayers() {
        long now = System.currentTimeMillis();
        synchronized (participantMap) {
            for (Iterator<Map.Entry<Integer, Participant>> it = participantMap.entrySet().iterator(); it.hasNext();) {
                Map.Entry<Integer, Participant> entry = it.next();
                Participant participant = entry.getValue();
                if (participant.getLastPing() + CLIENT_TIMEOUT < now) {
                    listener.onDisconnect(participant);
                    it.remove();
                }
            }
        }
    }

    private void updateIpTable() throws IOException {
        String url = "http://come-again.net/api/private";
        String publicIP = Long.toString(getByteIP(getPublicIP()));
        String privateIP = Long.toString(getByteIP(getPrivateIP()));
        String params = "{\"publicIP\":"+publicIP+",\"privateIP\":"+privateIP+"}";
        try {
            String response = Util.executePost(url, params);
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

    private static String getPublicIP() throws IOException {
        URL whatismyip = new URL("http://checkip.amazonaws.com");  //TODO: Don't rely on external source
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
        return in.readLine();
    }

    private static String getPrivateIP() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    private static long getByteIP(String address) throws UnknownHostException {
        InetAddress inetAddress = InetAddress.getByName(address);
        long result = 0;
        for (byte b: inetAddress.getAddress())
        {
            result = result << 8 | (b & 0xFF);
        }
        return result;
    }

    class InputHandler implements HttpHandler {

        private final int SUCCESS_CODE = 200;
        private final int ERROR_CODE = 400;

        @Override
        public void handle(HttpExchange t) throws IOException {
            String[] path = t.getRequestURI().getPath().split("/");
            try {
                switch (t.getRequestMethod()) {
                    case "GET":
                        handleGet(t, path);
                        break;
                    case "POST":
                        handlePost(t, path);
                        break;
                    default:
                        respondWithError(t);
                }
            } catch (IndexOutOfBoundsException e) {
                respondWithError(t);
            }
            t.close();
        }

        private void handleGet(HttpExchange t, String[] path) throws IOException {
            Headers headers = t.getResponseHeaders();
            headers.add("Content-Type", "application/json");
            switch (path[1]) {
                case "connect":
                    handleConnect(t);
                    break;
                case "ping":
                    handlePing(t, path);
                default:
                    respondWithError(t);
            }
        }

        private void handlePing(HttpExchange t, String[] path) throws IOException {
            if (path.length > 2) {
                synchronized (participantMap) {
                    int participantId;
                    try {
                        participantId = Integer.parseInt(path[2]);
                    } catch (NumberFormatException e) {
                        respondWithError(t);
                        return;
                    }
                    if (participantMap.containsKey(participantId)) {
                        Participant participant = participantMap.get(participantId);
                        participant.setLastPing();
                        listener.onPing(participant);
                    } else {
                        respondWithError(t);
                        return;
                    }
                }
            }
            respondWithSuccess(t, "pong");
        }

        private void handleConnect(HttpExchange t) throws IOException {
            synchronized (participantMap) {
                if (participantMap.size() < MAX_PARTICIPANTS) {
                    int participantId = -1;
                    for (int i=0; i<MAX_PARTICIPANTS; i++) {
                        if (!participantMap.containsKey(i)) {
                            participantId = i;
                            break;
                        }
                    }
                    if (participantId > -1) {
                        Participant connectedParticipant = new Participant(participantId);
                        participantMap.put(participantId, connectedParticipant);
                        listener.onConnect(connectedParticipant);
                        respondWithSuccess(t, "{\"connected\":true, \"id\":" + participantId + "}");
                    } else {
                        respondWithSuccess(t, "{\"connected\":false}");
                    }
                } else {
                    respondWithSuccess(t, "{\"connected\":false}");
                }
            }
        }

        private void handlePost(HttpExchange t, String[] path) throws IOException {
            switch (path[1]) {
                case "input":
                    handleInput(t, path);
                    break;
                case "ping":
                    handlePing(t, path);
                default:
                    respondWithError(t);
            }
        }

        private void handleInput(HttpExchange t, String[] path) throws IOException {
            int participantId = Integer.parseInt(path[2]);
            synchronized (participantMap) {
                if (participantMap.containsKey(participantId)) {
                    participantMap.get(participantId).setLastPing();
                    switch (path[3]) {
                        case "button":
                            handleButton(t, path, participantId);
                            break;
                        case "vote":
                            handleVote(t, path, participantId);
                            break;
                        default:
                            respondWithError(t);
                    }
                } else {
                    respondWithError(t);
                }
            }
        }

        private void handleButton(HttpExchange t, String[] path, int participantId) throws IOException {
            String button = path[4];
            switch (path[5]) {
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

        private void handleVote(HttpExchange t, String[] path, int participantId) throws IOException {
            String data = Util.getStringFromInputStream(t.getRequestBody());
            data = data.replaceAll("[^0-9]+", " ");
            String[] voteStrings = data.trim().split(" ");
            int[] votes = new int[voteStrings.length];
            for (int i = 0; i < voteStrings.length; i++) {
                votes[i] = Integer.parseInt(voteStrings[i]);
            }
            listener.onVote(participantMap.get(participantId), votes);
            respondWithSuccess(t);
        }

        private void respondWithError(HttpExchange t) throws IOException {
            t.sendResponseHeaders(ERROR_CODE, 0);
        }

//        private void respondWithError(HttpExchange t, String response) throws IOException {
//            t.sendResponseHeaders(ERROR_CODE, response.getBytes().length);
//            OutputStream os = t.getResponseBody();
//            os.write(response.getBytes());
//            os.close();
//        }

        private void respondWithSuccess(HttpExchange t) throws IOException {
            t.sendResponseHeaders(SUCCESS_CODE, 0);
        }

        private void respondWithSuccess(HttpExchange t, String response) throws IOException {
            t.sendResponseHeaders(SUCCESS_CODE, response.getBytes().length);
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
