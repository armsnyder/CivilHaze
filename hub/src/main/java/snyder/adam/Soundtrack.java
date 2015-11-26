/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam;

import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;

public class Soundtrack {

    private final Music music;
    private final float[] segments;

    // Pre-loaded songs:
    public static Soundtrack mainTheme;
    public static Soundtrack cell;

    /**
     * Loads all the game's music. Must be called before any music can be played.
     */
    public static void load() {
        mainTheme = new Soundtrack("music/Main_Theme.ogg", new float[]{0, 10.158f, 24.381f});
        cell = new Soundtrack("music/cell.ogg", new float[]{130, 57.426f});
    }

    // Private constructor:
    private Soundtrack(String URI, float[] markers) {
        this.segments = markers;
        Music music = null;
        try {
            music = new Music(URI);
            music.addListener(new MusicListener() {
                @Override
                public void musicEnded(Music music) {
                    play(segments.length-1);
                }
                @Override
                public void musicSwapped(Music music, Music newMusic) {}
            });
        } catch (SlickException e) {
            e.printStackTrace();
        }
        this.music = music;
    }

    /**
     * Play a given section of the song. If the song is already playing, it will jump to and play the given section.
     * @param segment index of segment to begin playing
     * @return 0 if successful, -1 if error.
     */
    public int play(int segment) {
        if (segment >= segments.length || segment < 0) return -1;
        music.setPosition(segments[segment]);
        if (!music.playing()) {
            music.play();
        }
        return 0;
    }

    /**
     * Play song from the beginning. If the song is already playing, it will jump back to the beginning.
     * @return 0 if successful, -1 if error.
     */
    public int play() {
        return play(0);
    }

    /**
     * Get the index of the musical segment currently playing. May be useful for deciding where to jump to next.
     * @return index of playing segment.
     */
    public int getPlayingSegment() {
        if (music.playing()) {
            // Probably overkill to do binary search optimization here, but hey it's good practice.
            float position = music.getPosition();
            int startIndex = 0;
            int endIndex = segments.length - 1;
            int middleIndex;
            while (startIndex < endIndex) {
                middleIndex = (endIndex+startIndex)/2;
                if (position < segments[middleIndex+1]) {
                    endIndex = middleIndex;
                } else {
                    startIndex = middleIndex + 1;
                }
            }
            return startIndex;
        } else {
            return -1;
        }
    }
}
