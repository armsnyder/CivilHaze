/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Images;
import snyder.adam.Participant;
import snyder.adam.Resolution;
import snyder.adam.Soundtrack;
import snyder.adam.entity.Background;
import snyder.adam.entity.TextArea;
import snyder.adam.network.MobileListener;
import snyder.adam.network.Server;

import java.io.IOException;
import java.util.HashMap;


public class CellLobbyState extends MasterState implements MobileListener {

    public static final int ID = 4;
    private boolean nextState = false;
    private boolean serverReady = false;

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        registerEntity(new Background(Images.prisonBackground), 0);
        String instructionsText = "If you want to play, take out your phone and browse to: come-again.net";
        int x = Resolution.selected.WIDTH / 10;
        int y = Resolution.selected.HEIGHT / 10;
        int width = Resolution.selected.WIDTH / 10 * 8;
        TextArea instructions = new TextArea(instructionsText, x, y, width, 5, Color.white);
        instructions.alignCenter();
        registerEntity(instructions, 1);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        super.update(container, game, delta);
        if (nextState && serverReady) {
            game.enterState(DialogueState.ID);
        }
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
        Soundtrack.cell.play();
        Server s = null;
        try {
            s = new Server(new HashMap<String, Participant>(), this, 8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread t = new Thread(s);
        t.start();
    }

    @Override
    public void keyPressed(int key, char c) {
        super.keyPressed(key, c);
        nextState = true;
    }

    @Override
    public void onButtonPress(Participant participant, String button) {}

    @Override
    public void onButtonRelease(Participant participant, String button) {}

    @Override
    public void onJoystickInput(Participant participant, double angle, double magnitude) {}

    @Override
    public void onVote(Participant participant, String[] votedFor) {}

    @Override
    public void onConnect(Participant participant) {}

    @Override
    public void onDisconnect(Participant participant) {}

    @Override
    public void onPing(Participant participant) {}

    @Override
    public void onServerReady() {
        serverReady = true;
        System.out.println("Ready");
    }

    @Override
    public void onServerFatalError(String description) {
        serverReady = false;
    }

    @Override
    public void onServerStopped() {
        serverReady = false;
    }
}
