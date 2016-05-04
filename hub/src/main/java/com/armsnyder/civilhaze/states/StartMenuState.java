/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import com.armsnyder.civilhaze.Images;
import com.armsnyder.civilhaze.Resolution;
import com.armsnyder.civilhaze.Soundtrack;
import com.armsnyder.civilhaze.entity.Background;
import com.armsnyder.civilhaze.entity.Entity;
import com.armsnyder.civilhaze.entity.FlashingText;
import com.armsnyder.civilhaze.entity.Text;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;


public class StartMenuState extends MasterState {

    public static final int ID = 3;
    private boolean triggerNextState = false;

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {

        // Add background:
        registerEntity(new Background(Images.townBackground), 0);

        // Add title text:
        Images.text.setSize(7);
        int offset = Images.text.getWidth(" ") / 10;
        registerEntity(new Text("CIVIL HAZE", offset, offset, 7, Color.black), 1);
        registerEntity(new Text("CIVIL HAZE", 0, 0, 7, Color.red), 2);
        for (Entity t : getEntities(new int[]{1, 2})) {
            Text text = (Text) t;
            text.setX(text.getX() + (Resolution.selected.WIDTH-text.getWidth()) / 2);
            text.setY(text.getY() + (Resolution.selected.HEIGHT-text.getHeight()) / 5);
        }

        // Add flashing START text:
        FlashingText startText = new FlashingText("PRESS ANY KEY TO START", 2, 0, 0, 4, Color.red);
        startText.setX((Resolution.selected.WIDTH - startText.getWidth()) / 2);
        startText.setY((Resolution.selected.HEIGHT - startText.getHeight()) * 4 / 5);
        registerEntity(startText, 1);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
        super.update(gameContainer, stateBasedGame, i);
        if (triggerNextState) {
            stateBasedGame.enterState(CellLobbyState.ID);
        }
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
        if (Soundtrack.mainTheme.getPlayingSegment() == 0 || Soundtrack.mainTheme.getPlayingSegment() == -1) {
            Soundtrack.mainTheme.playFrom(1);
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        super.keyPressed(key, c);
        triggerNextState = true;
    }
}
