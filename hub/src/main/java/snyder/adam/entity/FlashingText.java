/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.entity;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Images;

/**
 * Text that pulses in and out
 */
public class FlashingText extends Text {

    private float speed;
    private int phase;

    public FlashingText(String text, float speed, int x, int y, int size, Color color) {
        super(text, x, y, size, color);
        this.speed = 1000/speed;
    }

    @Override
    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        Images.text.setSize(size);
        Images.text.drawString(x, y, text, new Color(color.r, color.g, color.b,
                Math.abs((float) Math.sin(phase/speed))));
    }

    @Override
    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
        phase += i;
        if (phase/speed > Math.PI) phase = 0;
    }
}
