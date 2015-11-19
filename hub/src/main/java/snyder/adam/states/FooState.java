package snyder.adam.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Participant;
import snyder.adam.network.MobileListener;
import snyder.adam.network.Server;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author Flame
 */
public class FooState extends BasicGameState {

    @Override
    public int getID() {
        return State.FOO.getValue();
    }

    public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
        Server s = null;
        try {
            s = new Server(new HashMap<String, Participant>(), new Listener(), 8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread t = new Thread(s);
        t.start();
    }

    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {

    }

    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {

    }

    class Listener implements MobileListener {

        @Override
        public void onButtonPress(Participant participant, String button) {
            System.out.println(participant+" pressed "+button);
        }

        @Override
        public void onButtonRelease(Participant participant, String button) {
            System.out.println(participant+" released "+button);

        }

        @Override
        public void onVote(Participant participant, int[] votedFor) {
            System.out.println("vote from "+participant);

        }

        @Override
        public void onConnect(Participant participant) {
            System.out.println("connected "+participant);

        }

        @Override
        public void onDisconnect(Participant participant) {
            System.out.println("disconnect "+participant);

        }

        @Override
        public void onPing(Participant participant) {
            System.out.println("ping from "+participant);

        }

        @Override
        public void onServerReady() {
            System.out.println("server ready");
        }

        @Override
        public void onServerFatalError(String description) {
            System.out.println("server error: "+description);
        }
    }
}