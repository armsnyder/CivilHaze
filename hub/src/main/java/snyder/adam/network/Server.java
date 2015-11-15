package snyder.adam.network;

import com.sun.net.httpserver.*;
import snyder.adam.Participant;
import snyder.adam.Util;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

/**
 * @author Adam Snyder
 */
public class Server implements Runnable {

    private static final int MAX_PARTICIPANTS = 15;
    private final HttpServer server;
    private final Map<Integer, Participant> participantMap;
    private MobileListener listener;

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
                default:
                    respondWithError(t);
            }
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
                default:
                    respondWithError(t);
            }
        }

        private void handleInput(HttpExchange t, String[] path) throws IOException {
            int participantId = Integer.parseInt(path[2]);
            synchronized (participantMap) {
                if (participantMap.containsKey(participantId)) {
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
