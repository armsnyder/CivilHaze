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
            s = new Server(new HashMap<Integer, Participant>(), new Listener(), 8000);
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
            System.out.println("button press");
        }

        @Override
        public void onButtonRelease(Participant participant, String button) {
            System.out.println("button release");

        }

        @Override
        public void onVote(Participant participant, int[] votedFor) {
            System.out.println("vote");

        }

        @Override
        public void onConnect(Participant participant) {
            System.out.println("connect");

        }

        @Override
        public void onDisconnect(Participant participant) {
            System.out.println("disconnect");

        }

        @Override
        public void onPing(Participant participant) {
            System.out.println("ping from "+participant.getId());

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