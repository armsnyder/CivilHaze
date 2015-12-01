/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.FlashingText;
import snyder.adam.Images;
import snyder.adam.Resolution;
import snyder.adam.Soundtrack;


public class StartMenuState extends BasicGameState {

    public static final int ID = 3;
    private boolean triggerNextState = false;

    private FlashingText text;

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {

    }

    @Override
    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics graphics) throws SlickException {
        if (text != null) {
            text.render(gameContainer, stateBasedGame, graphics);
        }
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
        if (text != null) {
            text.update(gameContainer, stateBasedGame, i);
        }
        if (triggerNextState) {
            stateBasedGame.enterState(CellLobbyState.ID);
        }
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        if (Soundtrack.mainTheme.getPlayingSegment() == 0 || Soundtrack.mainTheme.getPlayingSegment() == -1) {
            Soundtrack.mainTheme.playFrom(1);
        }
        String s = "PRESS ANY KEY TO START";
        int x = (Resolution.selected.WIDTH - Images.text.getWidth(s))/2;
        int y = (Resolution.selected.HEIGHT - Images.text.getHeight(s))/2;
        text = new FlashingText(s, 2, x, y, 5, Color.white);
        super.enter(container, game);
    }

    @Override
    public void keyPressed(int key, char c) {
        triggerNextState = true;
        super.keyPressed(key, c);
    }
}
