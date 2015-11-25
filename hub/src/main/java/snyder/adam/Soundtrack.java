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

import org.newdawn.slick.Music;
import org.newdawn.slick.MusicListener;
import org.newdawn.slick.SlickException;

public class Soundtrack {

    private final Music[] segments;
    private final boolean autoLoop; // Automatically loop the last segment of music
    private int nowPlaying = -1;
    private int queued = -1;

    // Pre-loaded songs:
    public static Soundtrack mainTheme;
    public static Soundtrack cell;

    /**
     * Loads all the game's music. Must be called before any music can be played.
     */
    public static void load() {
        mainTheme = new Soundtrack(new String[]{"music/Main_Theme_intro_1.ogg", "music/Main_Theme_intro_2.ogg",
                "music/Main_Theme_loop.ogg"}, true);
        cell = new Soundtrack(new String[]{"music/cell_intro.ogg", "music/cell_loop.ogg"}, true);
    }

    // Private constructor:
    private Soundtrack(String[] musicSegments, boolean autoLoop) {
        this.segments = new Music[musicSegments.length];
        this.autoLoop = autoLoop;
        for (int i = 0; i < musicSegments.length; i++) {
            try {
                this.segments[i] = new Music(musicSegments[i]);
                this.segments[i].addListener(new MusicListener() {
                    @Override
                    public void musicEnded(Music music) {
                        play(queued);
                    }
                    @Override
                    public void musicSwapped(Music music, Music music1) {}
                });
            } catch (SlickException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Play a given section of the song. If the song is already playing, it will jump to and play the given section.
     * @param segment index of segment to begin playing
     * @return 0 if successful, -1 if error.
     */
    public int play(int segment) {
        if (segment >= segments.length || segment < 0) return -1;
        if (segment < segments.length-1 || !autoLoop) {
            segments[segment].play();
        } else {
            segments[segment].loop();
        }
        nowPlaying = segment;
        queued = segment + 1;
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
        return nowPlaying;
    }
}
