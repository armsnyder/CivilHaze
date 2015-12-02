/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Images;
import snyder.adam.Resolution;
import snyder.adam.Soundtrack;
import snyder.adam.entity.Background;
import snyder.adam.entity.Entity;
import snyder.adam.entity.FlashingText;
import snyder.adam.entity.Text;

import java.util.ArrayDeque;
import java.util.Arrays;


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
        registerEntities(new ArrayDeque<Entity>(Arrays.asList(
                new Text("COME WITH ME, AGAIN", offset, offset, 7, Color.black),
                new Text("COME WITH ME, AGAIN", 0, 0, 7, Color.red)
        )), 1);
        for (Entity t : getEntities(1)) {
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
