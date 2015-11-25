/*
 * Come Again is an interactive art piece in which participants perform a series of reckless prison breaks.
 * Copyright (C) 2015  Adam Snyder
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, version 3. Any redistribution must give proper attribution to
 * the original author.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package snyder.adam;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Text that pulses in and out
 */
public class FlashingText implements Entity {
    private String text;
    private float speed;
    private int x;
    private int y;
    private int size;
    private Color color;
    private int phase;

    public FlashingText(String text, float speed, int x, int y, int size, Color color) {
        this.text = text;
        this.speed = 1000/speed;
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
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
