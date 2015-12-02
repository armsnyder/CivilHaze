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
import snyder.adam.util.Callback;

/**
 * Text that pulses in and out
 */
public class FadingText extends Text {

    private float alpha;
    private int remainingMs = 0;
    private int totalMs = 0;
    private int fade = 0;
    private Callback callback;

    public FadingText(String text, float x, float y, int size, Color color, boolean startOn) {
        super(text, x, y, size, color);
        this.alpha = startOn ? 1 : 0;
    }

    @Override
    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        Images.text.setSize(size);
        Images.text.drawString(x, y, text, new Color(color.r, color.g, color.b, alpha));
    }

    @Override
    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
        if (fade != 0) {
            remainingMs -= i;
            if (remainingMs <= 0) {
                remainingMs = 0;
                if (fade < 0) {
                    alpha = 0;
                } else if (fade > 0) {
                    alpha = 1;
                }
                fade = 0;
                if (callback != null) {
                    callback.callback(null);
                }
                return;
            }
            float progress = fade < 0 ? (float)remainingMs / totalMs : (float)(totalMs-remainingMs) / totalMs;
            alpha = (float) (1-Math.cos(progress*Math.PI))/2;
        }
    }

    public int fadeIn(int milliseconds) {
        return fadeIn(milliseconds, null);
    }

    public int fadeIn(int milliseconds, Callback callback) {
        return setFade(milliseconds, callback, 1);
    }

    public int fadeOut(int milliseconds) {
        return fadeOut(milliseconds, null);
    }

    public int fadeOut(int milliseconds, Callback callback) {
        return setFade(milliseconds, callback, -1);
    }

    private int setFade(int milliseconds, Callback callback, int fade) {
        if (this.fade == 0) {
            this.callback = callback;
            remainingMs = totalMs = milliseconds;
            this.fade = fade;
            return 0;
        } else {
            return -1;
        }
    }
}
