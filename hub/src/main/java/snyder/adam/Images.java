/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam;


import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

public class Images {
    public static ScaledSpriteSheetFont text;
    private static boolean preloaded = false;
//    private static boolean loaded = false;

    public static void preload() throws SlickException {
        if (!preloaded) {
            text = new ScaledSpriteSheetFont(new SpriteSheet("images/fontBlocky.png", 5, 9, 1), ' ');
            preloaded = true;
        }
    }
}
