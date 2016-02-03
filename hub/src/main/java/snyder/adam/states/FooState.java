/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.states;

import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.FontUtils;
import snyder.adam.*;
import snyder.adam.entity.Cloud;
import snyder.adam.entity.Entity;
import snyder.adam.entity.Text;
import snyder.adam.network.MobileListener;
import snyder.adam.network.Server;
import snyder.adam.entity.PlayerDot;

import java.time.LocalDateTime;
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
    private int countCloudSpawns = 0;
    private static final int[] CLOUD_SPAWN_INTERVAL = {1000, 2000};
    private static final int CLOUD_SPAWN_CAP = 10;  //TODO: Increase when through testing
    private boolean isDisplaying = false;
    public static int score = 0;
    private static boolean gameOver = false;
    private Text gameOverText;
    private long timeAtReset;

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
        gameOver = false;
        timeAtReset = new Date().getTime();
        isDisplaying = true;
        Soundtrack.gameplay.play();
        for (Participant p : Server.getInstance().getParticipants()) {
            if (registerEntity(addPlayer(p), 2) != null) {
                players.get(p).state = this;
            }
        }
        registerEntity(new Text("", 0, 0, 2, Color.yellow) {
            @Override
            public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
                super.update(container, stateBasedGame, i);
                setX(font.getWidth(" "));
                setY(font.getWidth(" "));
                setText(String.format("HIGH SCORE: %d", ComeAgain.highScore));
            }
        }, 4);
        registerEntity(new Text("", 0, 0, 2, Color.magenta) {
            @Override
            public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
                super.update(container, stateBasedGame, i);
                setX(font.getWidth(" "));
                setY(font.getWidth(" ") + font.getHeight(" "));
                setText(String.format("SCORE: %d", score));
            }
        }, 4);
        Server.getInstance().setListener(this);
        for (int i = 0; i < 2; i++) {
            makeEdible();
        }
        new Thread() {
            @Override
            public void run() {
                while (isDisplaying) {
                    try {
                        Thread.sleep(CLOUD_SPAWN_INTERVAL[0] + (countCloudSpawns > CLOUD_SPAWN_CAP ? 0 :
                                (int)((CLOUD_SPAWN_INTERVAL[1] - CLOUD_SPAWN_INTERVAL[0]) *
                                        (1 - (float)countCloudSpawns / CLOUD_SPAWN_CAP))));
                    } catch (InterruptedException ignored) {}
                    if (isDisplaying) {
                        registerEntity(new Cloud(0.5f+Util.RANDOM.nextFloat()/2, 2+Util.RANDOM.nextInt(5), getThis()), 3);
                        countCloudSpawns ++;
                    }
                }
            }
        }.start();
        gameOverText = new Text("GAME OVER", 0, 0, 5, Color.red);
        int offsetX = (Resolution.selected.WIDTH-gameOverText.getWidth())/2;
        int offsetY = (Resolution.selected.HEIGHT-gameOverText.getHeight())/2;
        gameOverText.setX(offsetX);
        gameOverText.setY(offsetY);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        super.update(container, game, delta);
        if (getEntities(2).size() == 0 && !gameOver && new Date().getTime()-timeAtReset > 2) {
            gameOver = true;
        }
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        super.render(container, game, g);
        if (gameOver) {
            gameOverText.render(container, game, g);
        }
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game) throws SlickException {
        super.leave(container, game);
        isDisplaying = false;
    }

    @Override
    public int getID() {
        return ID;
    }

    private FooState getThis() {
        return this;
    }

    @Override
    public void keyPressed(int key, char c) {
        super.keyPressed(key, c);
        if (gameOver) {
            reset();
        }
    }

    private void reset() {
        timeAtReset = new Date().getTime();
        for (Entity e : getEntities(3)) { // Clouds
            unregisterEntity(e);
        }
        for (Entity e : getEntities(2)) { // Players
            unregisterEntity(e);
        }
        for (PlayerDot p : players.values()) {
            p.reset();
            registerEntity(p, 2);
        }
        Soundtrack.gameplay.playFrom(0);
        if (score > ComeAgain.highScore) ComeAgain.highScore = score;
        score = 0;
        gameOver = false;
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
        PlayerDot playerDot = addPlayer(participant);
        if (playerDot != null) {
            playerDot.state = this;
        }
        if (!gameOver) {
            registerEntity(playerDot, 2);
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
        p.kill();
    }

    private void makeEdible() {
        Edible e = new Edible(
                50+RANDOM.nextInt(Resolution.selected.WIDTH-100),
                50+RANDOM.nextInt(Resolution.selected.HEIGHT-100));
        registerEntity(e, 1);
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
            for (Entity e : getEntities(2)) {
                if (isOverlapping(e)) {
                    ((PlayerDot) e).score++;
                    ((PlayerDot) e).addPower(50);
                    unregisterEntity(this);
                    makeEdible();
                    break;
                }
            }
        }
    }
}