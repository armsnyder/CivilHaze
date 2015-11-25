/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.states;

import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Entity;
import snyder.adam.Images;
import snyder.adam.Participant;
import snyder.adam.ScaledSpriteSheetFont;
import snyder.adam.network.MobileListener;
import snyder.adam.network.Server;
import snyder.adam.util.RotationalDistance;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Flame
 */
public class FooState extends BasicGameState {

    Map<String, Boolean> keyStates = new HashMap<>();
    Map<Participant, PlayerDot> players = new HashMap<>();
    LinkedList<Color> availColors = new LinkedList<>(Arrays.asList(Color.blue, Color.green, Color.magenta,
            Color.orange, Color.yellow, Color.white));
    Music intro;
    Music loop;
    ScaledSpriteSheetFont text;

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        Images.preload();
        text = Images.text;
        super.enter(container, game);
    }

    @Override
    public int getID() {
        return 1;
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
        for (PlayerDot d : players.values()) {
            d.render(container, stateBasedGame, g);
        }
        if (text != null) {
            text.setSize(3);
            text.drawString(200, 50, "come-again.net");
        }
    }

    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
        for (PlayerDot d : players.values()) {
            d.update(container, stateBasedGame, i);
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
        float width = 50;
        float height = 50;

        @Override
        public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
            g.setColor(color);
            g.fillOval((int)x, (int)y, width, height);
        }

        @Override
        public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
            float scale = .5f;
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

    class MListener implements MusicListener {

        @Override
        public void musicEnded(Music music) {
            loop.loop();
        }

        @Override
        public void musicSwapped(Music music, Music newMusic) {

        }
    }

    class Listener implements MobileListener {

        @Override
        public void onButtonPress(Participant participant, String button) {
            keyStates.put(button, true);
            try {
                loop = new Music("music/"+button+"_loop.ogg");
            } catch (SlickException e) {
                e.printStackTrace();
            }
            try {
                if (button.equalsIgnoreCase("happy_man")) {
                    loop.loop();
                } else {
                    intro = new Music("music/" + button + "_intro.ogg");
                    intro.addListener(new MListener());
                    intro.play();
                }
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onButtonRelease(Participant participant, String button) {
            keyStates.put(button, false);
        }

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
        public void onVote(Participant participant, String[] votedFor) {
            System.out.println("vote from "+participant);
            for (String vote : votedFor) {
                System.out.println("--"+vote);
            }
        }

        @Override
        public void onConnect(Participant participant) {
            System.out.println("connected "+participant);
            PlayerDot newPlayer = new PlayerDot();
            newPlayer.color = availColors.pop();
            players.put(participant, newPlayer);
        }

        @Override
        public void onDisconnect(Participant participant) {
            System.out.println("disconnect "+participant);
            availColors.push(players.get(participant).color);
            players.remove(participant);
        }

        @Override
        public void onPing(Participant participant) {
        }

        @Override
        public void onServerReady() {
            System.out.println("server ready");
        }

        @Override
        public void onServerFatalError(String description) {
            System.out.println("server error: "+description);
        }

        @Override
        public void onServerStopped() {
            System.out.println("Server stopped");
        }
    }
}