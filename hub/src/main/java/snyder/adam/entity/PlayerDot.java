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
import org.newdawn.slick.geom.*;
import org.newdawn.slick.state.StateBasedGame;
import snyder.adam.ComeAgain;
import snyder.adam.Resolution;
import snyder.adam.Sounds;
import snyder.adam.Util;
import snyder.adam.states.FooState;
import snyder.adam.states.MasterState;
import snyder.adam.util.RotationalDistance;

import java.util.ArrayList;
import java.util.List;


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
    public FooState state = null;
    public float remainingPower = 0;
    public static float maxPower = 200;
    private boolean isAlive = true;
    private static final int SCORE_UPDATE_INTERVAL = 1000;
    private static final int SCORE_INCREMENT = 10;
    private float alpha = 1;
    private boolean scoreLoopRunning = false;
    public boolean isSpinning = false;

    public PlayerDot() {
        super(0, 0, 30, 30);
        shape = new Circle(15, 15, 15);
        reset();
    }

    @Override
    public void render(GameContainer container, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
        g.setColor(alpha < 1f ? new Color(color.r, color.g, color.b, alpha) : color);
        g.fillOval(x, y, width, height);
        g.setColor(alpha < 1f ? new Color(Color.gray.r, Color.gray.g, Color.gray.b, alpha) : Color.gray);
        g.setLineWidth(2);
        g.drawOval(x, y, width, height);
        if (remainingPower > 0) {
            g.setColor(alpha < 1f ? new Color(1f, 1f, 1f, alpha) : Color.white);
            g.setLineWidth(3);
            float percentPower = (float) remainingPower / maxPower;
            float margin = (1 - percentPower) * width / 2;
            g.drawOval(x + margin, y + margin, width * percentPower, height * percentPower);
        }
        if (isSpinning) {
            g.setColor(Color.white);
            for (int i=0; i<1+ Util.RANDOM.nextInt(3); i++) {
                float plusRadius = Util.RANDOM.nextFloat()*20;
                g.drawArc(x-plusRadius, y-plusRadius, width+plusRadius*2, height+plusRadius*2,
                        Util.RANDOM.nextFloat()*360, Util.RANDOM.nextFloat()*360);
            }
        }
    }

    @Override
    public void update(GameContainer container, StateBasedGame stateBasedGame, int i) throws SlickException {
        if (!isAlive) {
            if (alpha <= 0) {
                if (state != null) {
                    state.unregisterEntity(this);
                }
            } else {
                alpha -= i/1000f;
            }
        }
        int maxScore = 0;
        if (state != null) {
            for (Entity e : state.getEntities(2)) {
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
        shape.setX(x);
        shape.setY(y);
        int overlapCount = 0;
        if (state != null && isAlive) {
            for (Entity e : state.getEntities(3)) {
                if (e.contains(this)) {
                    overlapCount++;
                }
                if (overlapCount >= 2) break;
            }
        }
        if (overlapCount >= 2) {
            kill();
        }
        if (isSpinning) {
            subtractPower(i/10f);
            if (remainingPower <= 0) {
                isSpinning = false;
                return;
            }
            if (state != null && isAlive) {
                for (Entity e : state.getEntities(3)) {
                    float ex = e.shape.getCenterX();
                    float ey = e.shape.getCenterY();
                    double distance = Math.sqrt((ex-x+width/2)*(ex-x+width/2)+(ey-y+height/2)*(ey-y+height/2));
                    double direction = Util.angle(x+width/2, y+height/2, ex, ey)*180/Math.PI;
                    Vector2f component = new Vector2f(i*(float)(.001f/(distance/Resolution.selected.HEIGHT)), 0);
                    component.setTheta(direction);
//                    System.out.println(component.length());
                    ((Cloud)e).addVelocity(component);
                }
            }
        }
    }

    public void setState(FooState state) {
        this.state = state;
    }

    public void addPower(float power) {
        remainingPower += power;
        if (remainingPower > maxPower) remainingPower = maxPower;
    }

    public void subtractPower(float power) {
        remainingPower -= power;
        if (remainingPower < 0) remainingPower = 0;
    }

    public void kill() {
        Sounds.woosh.play();
        isAlive = false;
    }

    public void reset() {
        isAlive = true;
        alpha = 1f;
        if (!scoreLoopRunning) {
            scoreLoopRunning = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isAlive) {
                        if (state != null) FooState.score += SCORE_INCREMENT;
                        try {
                            Thread.sleep(SCORE_UPDATE_INTERVAL);
                        } catch (InterruptedException ignored) {
                        }
                    }
                    scoreLoopRunning = false;
                }
            }).start();
        }
    }
}
