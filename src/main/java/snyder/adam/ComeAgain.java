package snyder.adam;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.states.GameState;

/**
 * @author Adam Snyder
 */
public class ComeAgain extends StateBasedGame {

    /** Screen width */
    private static final int WIDTH = 800;
    /** Screen height */
    private static final int HEIGHT = 600;

    /** Screen title */
    private static final String TITLE = "Come With Me, Again";

    /** Frame rate */
    private static final int FPS = 60;

    /** Logic update interval */
    private static final int LOGIC_UPDATE_INTERVAL = FPS;

    /** Debug mode enable */
    private static final boolean DEBUG = false;

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

        this.addState(new GameState());
    }
    
    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new ComeAgain());
        app.setDisplayMode(WIDTH, HEIGHT, false);
        app.setForceExit(false);
        app.start();
    }
}
