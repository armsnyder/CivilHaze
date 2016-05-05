/*
 * Civil Haze is an interactive art piece in which participants perform a series of reckless prison breaks.
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

package com.armsnyder.civilhaze;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

/**
 * Created by Adam Snyder on 2/3/16.
 */
public class Sounds {

    public static Sound woosh;

    private static boolean loaded = false;

    public static void load() throws SlickException {
        if (!loaded) {
            woosh = new Sound("music/woosh.ogg");
            loaded = true;
        }
    }
}
