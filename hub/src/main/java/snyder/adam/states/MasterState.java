/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.states;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public abstract class MasterState extends BasicGameState {

    protected final List<Collection<Entity>> layers = new ArrayList<>();
    protected final List<Collection<Entity>> layersQueue = new ArrayList<>();

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        for (Collection<Entity> c : layers) {
            for (Entity e : c) {
                e.render(container, game, g);
            }
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        for (int i=0; i < layersQueue.size(); i++) {
            for (Iterator iterator = layersQueue.get(i).iterator(); iterator.hasNext();) {
                Entity e = (Entity) iterator.next();
                if (layers.get(i).contains(e)) {
                    layers.get(i).remove(e);
                } else {
                    layers.get(i).add(e);
                }
                iterator.remove();
            }
        }
        for (Collection<Entity> c : layers) {
            for (Entity e : c) {
                e.update(container, game, delta);
            }
        }
    }
}
