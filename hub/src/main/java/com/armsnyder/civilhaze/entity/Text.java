/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze.entity;


import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import com.armsnyder.civilhaze.Images;
import com.armsnyder.civilhaze.ScaledSpriteSheetFont;


public class Text extends Entity {

    protected String text;
    protected int size;
    protected Color color;
    protected boolean isVisible = true;
    protected ScaledSpriteSheetFont font;

    public Text(String text, float x, float y, int size, Color color, boolean isVisible, ScaledSpriteSheetFont font) {
        super(x, y);
        this.text = text;
        this.size = size;
        this.color = color;
        this.isVisible = isVisible;
        this.font = font;
    }

    public Text(String text, float x, float y, int size, Color color) {
        this(text, x, y, size, color, true, Images.text);
    }

    @Override
    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        Images.text.setSize(size);
        Images.text.drawString(x, y, text, color);
    }

    @Override
    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
        font.setSize(size);
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void setFont(ScaledSpriteSheetFont font) {
        this.font = font;
    }

    public int getWidth() {
        font.setSize(size);
        return font.getWidth(text);
    }

    public int getHeight() {
        font.setSize(size);
        return font.getHeight(text);
    }
}
