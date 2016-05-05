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

package com.armsnyder.civilhaze.entity;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import com.armsnyder.civilhaze.Resolution;
import com.armsnyder.civilhaze.Util;
import com.armsnyder.civilhaze.states.MasterState;

/**
 * Cloud (baddie)
 */
public class Cloud extends Entity {

    class Puff {
        float x;
        float y;
        float radius;

        public Puff(float x, float y, float radius) {
            this.radius = radius;
            this.x = x;
            this.y = y;
        }
    }

    private final Puff[] puffs;
    private final int maxWidth;
    private static final float MAX_PROP = 0.5f;
    private static final float LEAF_PROP = 0.8f;
    private static final float SPEED = 2f;
    private final Vector2f velocity = new Vector2f(SPEED, 0);
    private static final float ALPHA = 0.5f;
    private static final Color COLOR = new Color(1, 1, 1, ALPHA);
    private boolean onScreen = false;
    private final MasterState gameState;
//    public boolean isOverlapping = false;

    public Cloud(float size, int numPuffs, MasterState gameState) {
        super();
        this.gameState = gameState;
        assert numPuffs >= 1;
        maxWidth = (int) (Resolution.selected.WIDTH * MAX_PROP);
        puffs = new Puff[numPuffs];
        puffs[0] = new Puff(0, 0, maxWidth * size / 2f);
        float minX = puffs[0].x-puffs[0].radius;
        float minY = puffs[0].y-puffs[0].radius;
        float maxX = puffs[0].x+puffs[0].radius;
        float maxY = puffs[0].y+puffs[0].radius;
        for (int i=1; i<numPuffs; i++) {
            Puff root = puffs[Util.RANDOM.nextInt(i)];
            double angle = Util.RANDOM.nextFloat() * Math.PI * 2;
            float leafX = root.x + root.radius*(float)Math.cos(angle);
            float leafY = root.y + root.radius*(float)Math.sin(angle);
            float leafRadius = root.radius * LEAF_PROP;
            puffs[i] = new Puff(leafX, leafY, leafRadius);
            if (leafX-leafRadius < minX) minX = leafX-leafRadius;
            if (leafX+leafRadius > maxX) maxX = leafX+leafRadius;
            if (leafY-leafRadius < minY) minY = leafY-leafRadius;
            if (leafY+leafRadius > maxY) maxY = leafY+leafRadius;
        }
        width = maxX - minX;
        height = maxY - minY;
        for (int i=0; i<numPuffs; i++) {
            puffs[i].x -= minX;
            puffs[i].y -= minY;
        }

        // Create shape
        shape = new Circle(puffs[0].x, puffs[0].y, puffs[0].radius);
        for (int i=1; i<numPuffs; i++) {
            shape = shape.union(new Circle(puffs[i].x, puffs[i].y, puffs[i].radius))[0];
        }

        // Init offscreen pos and velocity
        int edge = Util.RANDOM.nextInt(4);
        double[] angleRange = new double[]{0, 0};
        switch(edge) {
            case 0:
                y = -height;
                x = Util.RANDOM.nextFloat() * Resolution.selected.WIDTH - width/2;
                angleRange[1] = angleTo(0, Resolution.selected.HEIGHT/2f);
                angleRange[0] = angleTo(Resolution.selected.WIDTH, Resolution.selected.HEIGHT/2f);
                break;
            case 1:
                x = Resolution.selected.WIDTH + width;
                y = Util.RANDOM.nextFloat() * Resolution.selected.HEIGHT - height/2;
                angleRange[1] = angleTo(Resolution.selected.WIDTH/2f, 0);
                angleRange[0] = angleTo(Resolution.selected.WIDTH/2f, Resolution.selected.HEIGHT);
                break;
            case 2:
                y = Resolution.selected.HEIGHT + height;
                x = Util.RANDOM.nextFloat() * Resolution.selected.WIDTH - width/2;
                angleRange[1] = angleTo(Resolution.selected.WIDTH, Resolution.selected.HEIGHT/2f);
                angleRange[0] = angleTo(0, Resolution.selected.HEIGHT/2f);
                break;
            case 3:
                x = -width;
                y = Util.RANDOM.nextFloat() * Resolution.selected.HEIGHT - height/2;
                angleRange[1] = angleTo(Resolution.selected.WIDTH/2f, Resolution.selected.HEIGHT);
                angleRange[0] = angleTo(Resolution.selected.WIDTH/2f, 0);
                break;
        }
        if (angleRange[1] < angleRange[0]) angleRange[1] += Math.PI*2;
        assert angleRange[1] > angleRange[0];
        double theta = (angleRange[0] + Util.RANDOM.nextFloat()*(angleRange[1] - angleRange[0]))*180/Math.PI;
//        if (ComeAgain.debug) System.out.printf("edge: %d, minAngle: %.0f, maxAngle: %.0f, theta: %.0f\n", edge,
//                (angleRange[0]*180/Math.PI)%360, (angleRange[1]*180/Math.PI)%360, theta%360);
        velocity.setTheta(theta);
//        if (ComeAgain.debug) System.out.printf("spawned x: %.1f, y: %.1f, w: %.1f, h: %.1f, vx: %.2f, vy: %.2f\n",
//                x, y, width, height, velocity.x, velocity.y);
        shape.setX(x);
        shape.setY(y);
    }

    @Override
    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        g.setColor(COLOR);
//        if (isOverlapping) {
//            g.setColor(Color.red);
//            isOverlapping = false;
//        }
        g.fill(shape);
    }

    @Override
    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
        x += velocity.x;
        y += velocity.y;
        if (isOnScreen()) {
            onScreen = true;
        } else if (onScreen) {
            gameState.unregisterEntity(this);
        }
        shape.setX(x);
        shape.setY(y);
    }

    private double angleTo(float x, float y) {
        return Util.angle(this.x + width/2, this.y+height/2, x, y);
    }

    public void addVelocity(Vector2f vector2f) {
        addVelocity(vector2f.x, vector2f.y);
    }

    public void addVelocity(float x, float y) {
        velocity.x += x;
        velocity.y += y;
    }
}
