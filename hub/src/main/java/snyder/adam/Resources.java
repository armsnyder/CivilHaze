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
