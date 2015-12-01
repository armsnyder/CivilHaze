/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.entity;


import org.newdawn.slick.*;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Resolution;

public class Background implements Entity {

    private Image image;
    private Color filter;
    private float x;
    private float y;
    private float scale;
    private Resolution resolution;

    public Background(Image image) {
        this.image = image;
        this.filter = Color.white;
        setResolution(Resolution.selected);
    }

    @Override
    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        image.draw(x, y, scale, filter);
    }

    @Override
    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
        if (!resolution.equals(Resolution.selected)) {
            setResolution(Resolution.selected);
        }
    }

    private void setResolution(Resolution resolution) {
        this.resolution = resolution;
        int width = image.getWidth();
        int height = image.getHeight();
        int screenWidth = resolution.WIDTH;
        int screenHeight = resolution.HEIGHT;
        scale = (float)screenWidth/width;
        scale = scale*height < screenHeight ? (float)screenHeight/height : scale;
        x = (screenWidth - width * scale)/2;
        y = (screenHeight - height * scale)/2;
    }
}
