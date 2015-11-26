/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.states.IntroCreditsState;

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

        this.addState(new IntroCreditsState());
        this.enterState(IntroCreditsState.ID);

//        this.addState(new FooState());
//        this.enterState(1);
    }
    
    public static void main(String[] args) throws SlickException {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int displayWidth = (int) screenSize.getWidth();
        int displayHeight = (int) screenSize.getHeight();
        Resolution defaultResolution = FULL_SCREEN ?
                Resolution.getMatchingFullScreenResolution(displayWidth, displayHeight) :
                Resolution.getMatchingWindowedResolution(displayWidth, displayHeight);
        Resolution.select(defaultResolution);
        AppGameContainer app = new AppGameContainer(new ComeAgain(), defaultResolution.WIDTH,
                defaultResolution.HEIGHT, false);
        if (FULL_SCREEN) {
            app.setDisplayMode(displayWidth, displayHeight, true);
        } else {
            app.setDisplayMode(defaultResolution.WIDTH, defaultResolution.HEIGHT, false);
        }
        app.setForceExit(false);
        app.start();
    }
}
