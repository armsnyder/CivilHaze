package snyder.adam;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public abstract class Entity {

    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {}

    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {}
}
