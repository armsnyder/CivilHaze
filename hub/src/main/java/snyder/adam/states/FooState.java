/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Participant;
import snyder.adam.Resolution;
import snyder.adam.Soundtrack;
import snyder.adam.entity.Entity;
import snyder.adam.network.MobileListener;
import snyder.adam.network.Server;
import snyder.adam.entity.PlayerDot;

import java.util.*;

/**
 * @author Flame
 */
public class FooState extends MasterState implements MobileListener {

    public static final int ID = 1;
    private static final Map<Participant, PlayerDot> players = new HashMap<>();
    private static final LinkedList<Color> availColors = new LinkedList<>(Arrays.asList(Color.blue, Color.green,
            Color.magenta, Color.orange, Color.yellow, Color.cyan, Color.pink, Color.red, new Color(0.47f, 0, 0.87f),
            new Color(0.73f, 0.94f, 0.33f), new Color(0.29f, 0.84f, 0.72f), Color.lightGray,
            new Color(0.83f, 0.84f, 0.29f), new Color(0.19f, 0.42f, 0.71f), new Color(0.71f, 0.19f, 0.34f)));
    private static final Random RANDOM = new Random();

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
        Soundtrack.gameplay.play();
        for (Participant p : Server.getInstance().getParticipants()) {
            if (registerEntity(addPlayer(p), 1) != null) {
                players.get(p).state = this;
            }
        }
        Server.getInstance().setListener(this);
        for (int i = 0; i < 10; i++) {
            makeEdible();
        }
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    }

    @Override
    public void onButtonPress(Participant participant, String button) {}

    @Override
    public void onButtonRelease(Participant participant, String button) {}

    @Override
    public void onJoystickInput(Participant participant, double angle, double magnitude) {
        if (players.containsKey(participant)) {
            if (players.get(participant).speed == 0) players.get(participant).angle = angle;
            players.get(participant).desiredAngle = angle;
            players.get(participant).desiredMagnitude = magnitude;
        }
    }

    @Override
    public void onVote(Participant participant, String[] votedFor) {}

    @Override
    public void onConnect(Participant participant) {
        if (registerEntity(addPlayer(participant), 1) != null) {
            players.get(participant).state = this;
        }
    }

    @Override
    public void onDisconnect(Participant participant) {
        removePlayer(participant);
    }

    @Override
    public void onPing(Participant participant) {}

    @Override
    public void onServerReady() {}

    @Override
    public void onServerFatalError(String description) {}

    @Override
    public void onServerStopped() {}

    static PlayerDot addPlayer(Participant participant) {
        if (players.containsKey(participant)) {
            PlayerDot player = players.get(participant);
            participant.sendMessage("color", new float[]{player.color.r, player.color.g, player.color.b});
            return players.get(participant);
        } else {
            if (availColors.size() > 0) {
                PlayerDot newPlayer = new PlayerDot();
                newPlayer.color = availColors.pop();
                newPlayer.setX(50 + RANDOM.nextFloat() * (Resolution.selected.WIDTH - 100));
                newPlayer.setY(50 + RANDOM.nextFloat() * (Resolution.selected.HEIGHT - 100));
                players.put(participant, newPlayer);
                participant.sendMessage("color", new float[]{newPlayer.color.r, newPlayer.color.g, newPlayer.color.b});
                return newPlayer;
            } else {
                return null;
            }
        }
    }

    private void removePlayer(Participant participant) {
        availColors.push(players.get(participant).color);
        PlayerDot p = players.get(participant);
        players.remove(participant);
        unregisterEntity(p);
    }

    private void makeEdible() {
        Edible e = new Edible(
                50+RANDOM.nextInt(Resolution.selected.WIDTH-100),
                50+RANDOM.nextInt(Resolution.selected.HEIGHT-100));
        registerEntity(e, 0);
    }

    class Edible extends Entity {

        public Edible(float x, float y) {
            super(x, y, 20, 20);
        }


        @Override
        public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
            g.setColor(Color.white);
            g.fillOval(x, y, width, height);
        }

        @Override
        public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
            for (Entity e : getEntities(1)) {
                if (isOverlapping(e)) {
                    ((PlayerDot) e).score++;
                    unregisterEntity(this);
                    makeEdible();
                    break;
                }
            }
        }
    }
}