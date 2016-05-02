/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze.entity;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.state.StateBasedGame;
import com.armsnyder.civilhaze.Resolution;

public abstract class Entity {

    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected Shape shape;

    public Entity() {
        this(0, 0);
    }

    public Entity(float x, float y) {
        this(x, y, 0, 0);
    }

    public Entity(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.shape = new Rectangle(x, y, width, height);
    }

    public abstract void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g)
            throws SlickException;

    public abstract void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException;

    public boolean isOverlapping(Entity e) {
        return shape.intersects(e.shape) || contains(e);
    }

    public boolean contains(Entity e) {
        return shape.contains(e.shape);
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean isOnScreen() {
        return x + width > 0 && x < Resolution.selected.WIDTH && y + height > 0 && y < Resolution.selected.HEIGHT;
    }
}
