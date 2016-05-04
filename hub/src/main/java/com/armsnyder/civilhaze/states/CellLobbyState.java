/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze.states;

import org.lwjgl.Sys;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import com.armsnyder.civilhaze.*;
import com.armsnyder.civilhaze.entity.Background;
import com.armsnyder.civilhaze.entity.TextArea;
import com.armsnyder.civilhaze.network.MobileListener;
import com.armsnyder.civilhaze.network.Server;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;


public class CellLobbyState extends MasterState implements MobileListener {

    public static final int ID = 4;
    private boolean nextState = false;
    private boolean serverReady = false;
    private long timeAtReset;

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        registerEntity(new Background(Images.prisonBackground), 0);
        String instructionsText = "If you want to play, take out your phone and browse to: civilhaze.com";
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
        timeAtReset = new Date().getTime();
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
        if (c == ' ' && new Date().getTime()-timeAtReset > 2000){
            nextState = true;
        }
    }

    @Override
    public void onButtonPress(Participant participant, String button) {
        if (CivilHaze.debug) System.out.println(button+" down");
    }

    @Override
    public void onButtonRelease(Participant participant, String button) {
        if (CivilHaze.debug) System.out.println(button+" up");
    }

    @Override
    public void onJoystickInput(Participant participant, double angle, double magnitude) {}

    @Override
    public void onVote(Participant participant, String[] votedFor) {}

    @Override
    public void onConnect(Participant participant) {
        FooState.addPlayer(participant);
    }

    @Override
    public void onDisconnect(Participant participant) {
        FooState.removePlayer(participant);
    }

    @Override
    public void onPing(Participant participant) {}

    @Override
    public void onServerReady() {
        serverReady = true;
        System.out.println("Ready");
    }

    @Override
    public void onServerFatalError(String description) {
        System.out.println(description);
        serverReady = false;
    }

    @Override
    public void onServerStopped() {
        serverReady = false;
    }
}
