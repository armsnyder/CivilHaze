package snyder.adam;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.ScalableGame;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.states.FooState;

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
        AppGameContainer app = new AppGameContainer(null);
        int displayWidth = app.getScreenWidth();
        int displayHeight = app.getScreenHeight();
        Resolution defaultResolution = FULL_SCREEN ?
                Resolution.getMatchingFullScreenResolution(displayWidth, displayHeight) :
                Resolution.getMatchingWindowedResolution(displayWidth, displayHeight);
        app = new AppGameContainer(new ScalableGame(new ComeAgain(), defaultResolution.WIDTH, defaultResolution.HEIGHT,
                false));
        if (FULL_SCREEN) {
            app.setDisplayMode(displayWidth, displayHeight, true);
        } else {
            app.setDisplayMode(defaultResolution.WIDTH, defaultResolution.HEIGHT, false);
        }
        app.setForceExit(false);
        app.start();
    }
}
