package snyder.adam.states;

import com.sun.net.httpserver.HttpServer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Server;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.obex.*;

/**
 * @author Flame
 */
public class FooState extends BasicGameState {

    static final String serverUUID = "11111111111111111111111111111123";

    @Override
    public int getID() {
        return State.FOO.getValue();
    }

    public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert server != null;
        server.createContext("/test", new Server());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {

    }

    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {

    }
}