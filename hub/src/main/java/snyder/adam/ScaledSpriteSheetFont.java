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
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.SpriteSheetFont;
import org.newdawn.slick.util.Log;

import java.io.UnsupportedEncodingException;

public class ScaledSpriteSheetFont extends SpriteSheetFont {
    private SpriteSheet font;
    private char startingCharacter;
    private int charWidth;
//    private int charHeight;
    private int horizontalCount;
    private int numChars;
    private int size = 1;
    private static final float SCALE = 0.005f;

    public ScaledSpriteSheetFont(SpriteSheet font, char startingCharacter) {
        super(font, startingCharacter);
        this.font = font;
        this.startingCharacter = startingCharacter;
        this.horizontalCount = font.getHorizontalCount();
        int verticalCount = font.getVerticalCount();
        this.charWidth = font.getWidth() / this.horizontalCount;
//        this.charHeight = font.getHeight() / verticalCount;
        this.numChars = this.horizontalCount * verticalCount;
    }

    @Override
    public void drawString(float x, float y, String text, Color col, int startIndex, int endIndex) {
        try {
            byte[] e = text.getBytes("US-ASCII");

            for(int i = 0; i < e.length; ++i) {
                int index = e[i] - this.startingCharacter;
                if(index < this.numChars) {
                    int xPos = index % this.horizontalCount;
                    int yPos = index / this.horizontalCount;
                    if(i >= startIndex && i <= endIndex) {
                        float scale = getScale();
                        this.font.getSprite(xPos, yPos).draw(x + (i * this.charWidth * scale), y, scale, col);
                    }
                }
            }
        } catch (UnsupportedEncodingException var12) {
            Log.error(var12);
        }
    }

    private float getScale() {
        return SCALE * Resolution.selected.WIDTH * size / charWidth;
    }

    public int getHeight(String text) {
        return (int) (super.getHeight(text) * getScale());
    }

    public int getWidth(String text) {
        return (int) (super.getWidth(text) * getScale());
    }

    public int getLineHeight() {
        return (int) (super.getLineHeight() * getScale());
    }

    public void setSize(int size) {
        this.size = size;
    }
}
