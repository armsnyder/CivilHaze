/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package snyder.adam.util;

public class RotationalDistance {

    public final double distance;
    public final boolean direction;

    public RotationalDistance(double distance, boolean direction) {
        this.distance = distance;
        this.direction = direction;
    }

    public RotationalDistance(double angle1, double angle2) {
        double normalDistance = Math.abs(angle1-angle2);
        double wrapDistance = Math.abs(2*Math.PI + (angle1 < angle2 ? angle1-angle2 : angle2-angle1));
        if (normalDistance < wrapDistance) {
            distance = normalDistance;
            direction = angle1 > angle2;
        } else {
            distance = wrapDistance;
            direction = angle1 < angle2;
        }
    }
}
