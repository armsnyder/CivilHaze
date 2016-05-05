/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze.states;

import com.armsnyder.civilhaze.CivilHaze;
import com.armsnyder.civilhaze.Participant;
import com.armsnyder.civilhaze.Resolution;
import com.armsnyder.civilhaze.Soundtrack;
import com.armsnyder.civilhaze.entity.TextArea;
import com.armsnyder.civilhaze.network.MobileListener;
import com.armsnyder.civilhaze.network.Server;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;


public class CellLobbyState extends MasterState implements MobileListener {

    public static final int ID = 4;
    private boolean nextState = false;
    private boolean serverReady = false;
    private long timeAtReset;
    private TextArea status = null;

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        String instructionsText = "If you want to play, take out your phone and browse to: civilhaze.com";
        String instructionsText2 = "Make sure you are on the same Wi-Fi network";
        String instructionsText3 = "Press SPACE to continue";
        int x = Resolution.selected.WIDTH / 10;
        int y = Resolution.selected.HEIGHT / 10;
        int width = Resolution.selected.WIDTH / 10 * 8;
        TextArea instructions = new TextArea(instructionsText, x, y, width, 5, Color.white);
        TextArea instructions2 = new TextArea(instructionsText2, x, y+Resolution.selected.HEIGHT/2, width, 4,
                Color.white);
        status = new TextArea(instructionsText3, x, y+Resolution.selected.HEIGHT*3/4, width, 4, Color.white);
        instructions.alignCenter();
        instructions2.alignCenter();
        status.alignCenter();
        registerEntity(instructions, 1);
        registerEntity(instructions2, 1);
        registerEntity(status, 1);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        super.update(container, game, delta);
        if (nextState && serverReady) {
            game.enterState(FooState.ID);
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
        status.setText("Error: "+description);
        status.setColor(Color.red);
        serverReady = false;
    }

    @Override
    public void onServerStopped() {
        serverReady = false;
    }
}
