/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.Resolution;
import snyder.adam.Soundtrack;
import snyder.adam.entity.Entity;
import snyder.adam.entity.FadingText;
import snyder.adam.util.Callback;

import java.util.ArrayDeque;
import java.util.Deque;


public class IntroCreditsState extends MasterState {

    public static final int ID = 2;
    private boolean triggerNextState = false;
    private Deque<Entity> messages = new ArrayDeque<>();

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
        messages.add(new FadingText("A game by Adam Snyder", 0, 0, 3, Color.white, false));
        messages.add(new FadingText("Art by Jae Yun", 0, 0, 3, Color.white, false));
        for (Entity e : messages) {
            FadingText t = (FadingText) e;
            t.setX((Resolution.selected.WIDTH - t.getWidth())/2);
            t.setY((Resolution.selected.HEIGHT - t.getHeight())/2);
        }
        registerEntities(messages, 1);
    }

    @Override
    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
        super.update(gameContainer, stateBasedGame, i);
        if (triggerNextState) {
            stateBasedGame.enterState(StartMenuState.ID);
        }
    }

    @Override
    public void keyPressed(int key, char c) {
        super.keyPressed(key, c);
        triggerNextState = true;
    }

    @Override
    public void enter(GameContainer container, final StateBasedGame game) throws SlickException {
        super.enter(container, game);
        Soundtrack.mainTheme.play();
        readyNextMessage();
    }

    private void readyNextMessage() {
        if (messages.size() > 0) {
            ((FadingText)messages.getFirst()).fadeIn(4000, new Callback() {
                @Override
                public void callback(Object object) {
                    ((FadingText)messages.removeFirst()).fadeOut(4000, new Callback() {
                        @Override
                        public void callback(Object object) {
                            readyNextMessage();
                        }
                    });
                }
            });
        } else {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1570);
                    } catch (InterruptedException ignored) {}
                    triggerNextState = true;
                }
            }.start();
        }
    }
}
