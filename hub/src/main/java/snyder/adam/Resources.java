/*
 * Come Again is an interactive art piece in which participants perform a series of reckless prison breaks.
 * Copyright (C) 2015  Adam Snyder
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version. Any redistribution must give proper attribution to the original author.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package snyder.adam;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Adam Snyder
 */
public class Resources {

    private static Map<String, Image> images;
    private static Map<String, SpriteSheet> sprites;
    private static Map<String, Sound> sounds;

    private Resources() {
        images = new HashMap<>();
        sprites = new HashMap<>();
        sounds = new HashMap<>();

        loadResources();
    }

    private static void loadResources() {

    }

    public static Image loadImage(String path) throws SlickException {
        int filter = Image.FILTER_NEAREST;
        return new Image(path, false, filter);
    }

    public static SpriteSheet loadSprite(String path, int tileWidth, int tileHeight) throws SlickException {
        return new SpriteSheet(path, tileWidth, tileHeight);
    }

    public static Image getImage(String key) {
        return images.get(key);
    }

    public static SpriteSheet getSprite(String key) {
        return sprites.get(key);
    }

    public static Image getSpriteImage(String key, int x, int y) {
        return getSprite(key).getSubImage(x, y);
    }

    public static Sound getSound(String key) {
        return sounds.get(key);
    }
}
