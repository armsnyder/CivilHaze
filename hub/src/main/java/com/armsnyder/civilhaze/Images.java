/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze;


import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Images {
    public static ScaledSpriteSheetFont text;

    public static Image townBackground;

    private static boolean loaded = false;

    public static void load() throws SlickException {
        if (!loaded) {
            text = new ScaledSpriteSheetFont(new SpriteSheet("images/fontBlocky3.png", 13, 32, 1), ' ');
            townBackground = new Image("images/backgroundTown.png");
            loaded = true;
        }
    }
}
