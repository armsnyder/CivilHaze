/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public interface Entity {

    void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException;

    void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException;
}
