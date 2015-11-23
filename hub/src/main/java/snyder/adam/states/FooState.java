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

package snyder.adam.states;

import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Participant;
import snyder.adam.network.MobileListener;
import snyder.adam.network.Server;

import java.io.IOException;
import java.util.*;

/**
 * @author Flame
 */
public class FooState extends BasicGameState {

    Map<String, Boolean> keyStates = new HashMap<>();
    Map<Participant, PlayerDot> players = new HashMap<>();
    LinkedList<Color> availColors = new LinkedList<>(Arrays.asList(Color.blue, Color.green, Color.magenta,
            Color.orange, Color.yellow, Color.white));

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
            g.setColor(d.color);
            g.fillOval(d.x, d.y, 50, 50);
        }
    }

    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
        float scale = 0.05f;
        for (PlayerDot d : players.values()) {
            d.x += Math.cos(d.angle)*d.magnitude * scale;
            d.y += Math.sin(d.angle)*d.magnitude * scale;
        }
    }

    class PlayerDot {
        Color color = Color.black;
        int x = 500;
        int y = 500;
        double angle = 0;
        double magnitude = 0;
    }

    class Listener implements MobileListener {

        @Override
        public void onButtonPress(Participant participant, String button) {
            keyStates.put(button, true);
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
            players.get(participant).angle = angle;
            players.get(participant).magnitude = magnitude;
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