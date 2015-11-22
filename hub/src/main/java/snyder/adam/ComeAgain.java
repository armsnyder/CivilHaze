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

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.states.FooState;

import java.awt.*;

/**
 * @author Adam Snyder
 */
public class ComeAgain extends StateBasedGame {

    /** Full screen */
    private static final boolean FULL_SCREEN = false;

    /** Screen title */
    private static final String TITLE = "Come With Me, Again";

    /** Frame rate */
    private static final int FPS = 60;

    /** Logic update interval */
    private static final int LOGIC_UPDATE_INTERVAL = FPS;

    /** Debug mode enable */
    private static final boolean DEBUG = true;

    /** VSync enable */
    private static final boolean V_SYNC = true;

    public ComeAgain() {
        super(TITLE);
    }

    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        container.setMaximumLogicUpdateInterval(LOGIC_UPDATE_INTERVAL);
        container.setAlwaysRender(true);
        container.setTargetFrameRate(FPS);
        container.setShowFPS(DEBUG);
        container.setVSync(V_SYNC);

        this.addState(new FooState());
    }
    
    public static void main(String[] args) throws SlickException {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int displayWidth = (int) screenSize.getWidth();
        int displayHeight = (int) screenSize.getHeight();
        Resolution defaultResolution = FULL_SCREEN ?
                Resolution.getMatchingFullScreenResolution(displayWidth, displayHeight) :
                Resolution.getMatchingWindowedResolution(displayWidth, displayHeight);
        AppGameContainer app = new AppGameContainer(new ScalableGame(new ComeAgain(), defaultResolution.WIDTH,
                defaultResolution.HEIGHT, false));
        if (FULL_SCREEN) {
            app.setDisplayMode(displayWidth, displayHeight, true);
        } else {
            app.setDisplayMode(defaultResolution.WIDTH, defaultResolution.HEIGHT, false);
        }
        app.setForceExit(false);
        app.start();
    }
}
