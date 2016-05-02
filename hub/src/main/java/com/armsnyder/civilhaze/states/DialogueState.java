/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze.states;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import com.armsnyder.civilhaze.Images;
import com.armsnyder.civilhaze.Resolution;
import com.armsnyder.civilhaze.Soundtrack;
import com.armsnyder.civilhaze.entity.Background;
import com.armsnyder.civilhaze.entity.Entity;
import com.armsnyder.civilhaze.entity.ImageEntity;
import com.armsnyder.civilhaze.entity.TextArea;

import java.util.Date;


public class DialogueState extends MasterState {

    public static final int ID = 5;
    private boolean nextState = false;
    private long timeAtReset;

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        registerEntity(new Background(Images.prisonBackground), 0);
        ImageEntity oldMan = new ImageEntity(Images.oldManEating);
        oldMan.setHeight(Resolution.selected.HEIGHT*7/8);
        oldMan.setY(Resolution.selected.HEIGHT/8);
        oldMan.setX((Resolution.selected.WIDTH-oldMan.getWidth())/2);
        registerEntity(oldMan, 1);
        final int width = Resolution.selected.WIDTH * 9 / 10;
        final int height = Resolution.selected.HEIGHT / 3;
        final int edge = (Resolution.selected.WIDTH - width) / 2;
        final int y = Resolution.selected.HEIGHT-height-edge;
        final int cornerRadius = 50;
        registerEntity(new Entity(edge, y, width, height) {
            @Override
            public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g)
                    throws SlickException {
                g.setColor(new Color(1, 1, 1, 0.9f));
                g.fillRoundRect(x, y, width, height, cornerRadius);
                g.setColor(Color.black);
                g.setLineWidth(6);
                g.drawRoundRect(x, y, width, height, cornerRadius);
            }

            @Override
            public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {

            }
        }, 2);
        TextArea dialogue = new TextArea(
                "Hey kids! So you want to get out of this cell? I can help you out, but you'd better be careful " +
                        "outside. If you get lost in the fog, you'll never be found again! So, look out for each " +
                        "other...", edge+cornerRadius,
                y+cornerRadius, width-(2*cornerRadius), 3, Color.black);
        registerEntity(dialogue, 3);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        super.update(container, game, delta);
        if (nextState) {
            game.enterState(FooState.ID);
        }
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game) throws SlickException {
        super.enter(container, game);
        timeAtReset = new Date().getTime();
        Soundtrack.ohNo.play();
        Soundtrack.ohNo.setVolume(0.8f);
    }

    @Override
    public void keyPressed(int key, char c) {
        super.keyPressed(key, c);
        if (c == ' ' && new Date().getTime()-timeAtReset > 2000){
            nextState = true;
        }
    }
}
