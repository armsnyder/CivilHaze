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

package snyder.adam.entity;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.states.MasterState;
import snyder.adam.util.RotationalDistance;


public class PlayerDot extends Entity {
    public Color color = Color.black;
    public double desiredAngle = 0;
    public double desiredMagnitude = 0;
    public double speed = 0;
    public double acceleration = .01;
    public double deceleration = .006;
    public double angle = 0;
    public double angularAcceleration = 0.05;
    public int score = 0;
    public boolean winning = false;
    public MasterState state = null;

    public PlayerDot() {
        super(0, 0, 30, 30);
    }

    @Override
    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        g.setColor(color);
        g.fillOval(x, y, width, height);
        if (winning) {
            g.setColor(Color.white);
            g.setLineWidth(2);
            g.drawOval(x, y, width, height);
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {

        int maxScore = 0;
        if (state != null) {
            for (Entity e : state.getEntities(1)) {
                if (((PlayerDot) e).score > maxScore) {
                    maxScore = ((PlayerDot) e).score;
                }
            }
        }
        winning = maxScore == score;

        float scale = .2f;
        if (speed > desiredMagnitude) {
            double deltaSpeed = deceleration * i;
            if (deltaSpeed > speed - desiredMagnitude) {
                speed = desiredMagnitude;
            } else {
                speed -= deltaSpeed;
            }
        } else {
            double deltaSpeed = acceleration*i;
            if (deltaSpeed > desiredMagnitude - speed) {
                speed = desiredMagnitude;
            } else {
                speed += deltaSpeed;
            }
        }
        double deltaAngle = angularAcceleration * i;
        RotationalDistance remainingAngle = new RotationalDistance(angle, desiredAngle);
        if (remainingAngle.distance < deltaAngle) {
            angle = desiredAngle;
        } else {
            if (remainingAngle.direction) {
                angle = angle - deltaAngle;
            } else {
                angle = angle + deltaAngle;
            }
            if (angle > Math.PI) {
                angle -= 2*Math.PI;
            }
            if (angle < -Math.PI) {
                angle += 2*Math.PI;
            }
        }
        double deltaX = Math.cos(angle) * speed * scale * i;
        double deltaY = Math.sin(angle) * speed * scale * i;
        x += deltaX;
        y += deltaY;
        if (y > container.getHeight()-height || y < 0) {
            y -= deltaY;
        }
        if (x > container.getWidth()-width || x < 0) {
            x -= deltaX;
        }
    }

    public void setState(MasterState state) {
        this.state = state;
    }
}
