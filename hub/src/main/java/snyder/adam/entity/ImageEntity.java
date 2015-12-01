/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.entity;


import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Resolution;

public class ImageEntity implements Entity {

    private Image image;
    private float x = 0;
    private float y = 0;
    private float scale = 1;

    public ImageEntity(Image image) {
        this.image = image;
    }

    @Override
    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        image.draw(x, y, scale);
    }

    @Override
    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {

    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setHeight(float height) {
        scale = height / image.getHeight();
    }

    public float getWidth() {
        return scale * image.getWidth();
    }
}
