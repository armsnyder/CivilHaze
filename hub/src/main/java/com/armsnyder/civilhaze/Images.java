/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze;


import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Images {
    public static ScaledSpriteSheetFont textTiny;
    public static ScaledSpriteSheetFont textMed;
    public static ScaledSpriteSheetFont text;

    public static Image prisonBackground;
    public static Image townBackground;
    public static Image oldManEating;

    private static boolean loaded = false;

    public static void load() throws SlickException {
        if (!loaded) {
            textTiny = new ScaledSpriteSheetFont(new SpriteSheet("images/fontBlocky.png", 5, 9, 1), ' ');
            textMed = new ScaledSpriteSheetFont(new SpriteSheet("images/fontBlocky2.png", 7, 7, 1), ' ');
            text = new ScaledSpriteSheetFont(new SpriteSheet("images/fontBlocky3.png", 13, 32, 1), ' ');
            prisonBackground = new Image("images/backgroundPrison.png");
            townBackground = new Image("images/backgroundTown.png");
            oldManEating = new Image("images/old_man_eating.png");
            loaded = true;
        }
    }
}
