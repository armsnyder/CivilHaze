/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public abstract class Entity {

    protected float x = 0;
    protected float y = 0;
    protected float width = 0;
    protected float height = 0;

    public Entity() {}

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g)
            throws SlickException;

    public abstract void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException;

    public boolean isOverlapping(Entity e) {
        return x <= e.x + e.width && x + width >= e.x && y <= e.y + e.height && y + height >= e.y;
    }

    public boolean contains(Entity e) {
        return x < e.x && x + width > e.x + e.width && y < e.y && y + height > e.y + e.height;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
