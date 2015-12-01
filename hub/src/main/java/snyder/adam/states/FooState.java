/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.states;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.*;
import snyder.adam.entity.Entity;
import snyder.adam.network.MobileListener;
import snyder.adam.network.Server;
import snyder.adam.util.RotationalDistance;

import java.io.IOException;
import java.util.*;

/**
 * @author Flame
 */
public class FooState extends MasterState implements MobileListener {

    public static final int ID = 1;
    private Map<Participant, PlayerDot> players = new HashMap<>();
    private LinkedList<Color> availColors = new LinkedList<>(Arrays.asList(Color.blue, Color.green, Color.magenta,
            Color.orange, Color.yellow, Color.cyan, Color.pink, Color.red, new Color(0.47f, 0, 0.87f),
            new Color(0.73f, 0.94f, 0.33f), new Color(0.29f, 0.84f, 0.72f), Color.lightGray,
            new Color(0.83f, 0.84f, 0.29f), new Color(0.19f, 0.42f, 0.71f), new Color(0.71f, 0.19f, 0.34f)));
    private static final Random RANDOM = new Random();

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
        Soundtrack.gameplay.play();
        for (Participant p : Server.getInstance().getParticipants()) {
            addPlayer(p);
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
        layers.add(new ArrayDeque<Entity>());
        layers.add(new ArrayDeque<Entity>());
        layersQueue.add(new ArrayDeque<Entity>());
        layersQueue.add(new ArrayDeque<Entity>());
    }

    @Override
    public void onButtonPress(Participant participant, String button) {}

    @Override
    public void onButtonRelease(Participant participant, String button) {}

    @Override
    public void onJoystickInput(Participant participant, double angle, double magnitude) {
        if (!players.containsKey(participant)) {
            PlayerDot newPlayer = new PlayerDot();
            newPlayer.color = availColors.pop();
            players.put(participant, newPlayer);
        }
        if (players.get(participant).speed == 0) players.get(participant).angle = angle;
        players.get(participant).desiredAngle = angle;
        players.get(participant).desiredMagnitude = magnitude;
    }

    @Override
    public void onVote(Participant participant, String[] votedFor) {}

    @Override
    public void onConnect(Participant participant) {
        addPlayer(participant);
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

    private void addPlayer(Participant participant) {
        if (availColors.size() > 0) {
            PlayerDot newPlayer = new PlayerDot();
            newPlayer.color = availColors.pop();
            newPlayer.x = 50+RANDOM.nextInt(Resolution.selected.WIDTH-100);
            newPlayer.y = 50+RANDOM.nextInt(Resolution.selected.HEIGHT-100);
            players.put(participant, newPlayer);
            layersQueue.get(1).add(newPlayer);
            participant.sendMessage("{\"result\": \"true\", \"color\": ["+newPlayer.color.r+","+newPlayer.color.g+","+
                    newPlayer.color.b+"]}");
        }
    }

    private void removePlayer(Participant participant) {
        availColors.push(players.get(participant).color);
        PlayerDot p = players.get(participant);
        players.remove(participant);
        if (layers.get(1).contains(p)) {
            layersQueue.get(1).add(p);
        }
    }

    private void makeEdible() {
        Edible e = new Edible(
                50+RANDOM.nextInt(Resolution.selected.WIDTH-100),
                50+RANDOM.nextInt(Resolution.selected.HEIGHT-100));
        layersQueue.get(0).add(e);
    }

    class Edible implements Entity {

        int x;
        int y;
        boolean doRender = true;

        public Edible(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
            if (doRender) {
                g.setColor(Color.white);
                g.fillOval(x, y, 10, 10);
            }
        }

        @Override
        public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
            if (doRender) {
                for (Entity e : layers.get(1)) {
                    PlayerDot p = (PlayerDot) e;
                    if (Math.abs(p.x - x) < 20 && Math.abs(p.y - y) < 20) {
                        doRender = false;
                        p.score++;
                        makeEdible();
                        break;
                    }
                }
            }
        }
    }

    class PlayerDot implements Entity {
        Color color = Color.black;
        double x = 500;
        double y = 500;
        double desiredAngle = 0;
        double desiredMagnitude = 0;
        double speed = 0;
        double acceleration = .01;
        double deceleration = .006;
        double angle = 0;
        double angularAcceleration = 0.05;
        float width = 30;
        float height = 30;
        int score = 0;
        boolean winning = false;

        @Override
        public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
            g.setColor(color);
            g.fillOval((float)x, (float)y, width, height);
            if (winning) {
                g.setColor(Color.white);
                g.setLineWidth(2);
                g.drawOval((float) x, (float) y, width, height);
            }
        }

        @Override
        public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {

            int maxScore = 0;
            for (Entity e : layers.get(1)) {
                if (((PlayerDot) e).score > maxScore) {
                    maxScore = ((PlayerDot) e).score;
                }
            }
            winning = maxScore == score;

            float scale = .2f;
            if (speed > desiredMagnitude) {
                double deltaSpeed = deceleration * i;
                if (deltaSpeed > speed - desiredMagnitude) {
                    speed = desiredMagnitude;
                } else {
                    speed -= deltaSpeed;
                }
            } else {
                double deltaSpeed = acceleration*i;
                if (deltaSpeed > desiredMagnitude - speed) {
                    speed = desiredMagnitude;
                } else {
                    speed += deltaSpeed;
                }
            }
            double deltaAngle = angularAcceleration * i;
            RotationalDistance remainingAngle = new RotationalDistance(angle, desiredAngle);
            if (remainingAngle.distance < deltaAngle) {
                angle = desiredAngle;
            } else {
                if (remainingAngle.direction) {
                    angle = angle - deltaAngle;
                } else {
                    angle = angle + deltaAngle;
                }
                if (angle > Math.PI) {
                    angle -= 2*Math.PI;
                }
                if (angle < -Math.PI) {
                    angle += 2*Math.PI;
                }
            }
            double deltaX = Math.cos(angle) * speed * scale * i;
            double deltaY = Math.sin(angle) * speed * scale * i;
            x += deltaX;
            y += deltaY;
            if (y > container.getHeight()-height || y < 0) {
                y -= deltaY;
            }
            if (x > container.getWidth()-width || x < 0) {
                x -= deltaX;
            }
        }
    }
}