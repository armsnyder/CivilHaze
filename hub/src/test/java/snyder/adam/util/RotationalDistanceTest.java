/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by flame on 11/23/15.
 */
public class RotationalDistanceTest {

    static final double DELTA = 0.001;

    @Test
    public void test1() throws Exception {
        double angle1 = Math.PI;
        double angle2 = Math.PI;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(0, r.distance, DELTA);
    }

    @Test
    public void test2() throws Exception {
        double angle1 = -Math.PI;
        double angle2 = -Math.PI;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(0, r.distance, DELTA);
    }

    @Test
    public void test3() throws Exception {
        double angle1 = 0;
        double angle2 = 0;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(0, r.distance, DELTA);
    }

    @Test
    public void test4() throws Exception {
        double angle1 = 0;
        double angle2 = 0.1;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(0.1, r.distance, DELTA);
        assertEquals(false, r.direction);
    }

    @Test
    public void test5() throws Exception {
        double angle1 = 0.1;
        double angle2 = 0;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(0.1, r.distance, DELTA);
        assertEquals(true, r.direction);
    }

    @Test
    public void test6() throws Exception {
        double angle1 = Math.PI-0.1;
        double angle2 = Math.PI;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(0.1, r.distance, DELTA);
        assertEquals(false, r.direction);
    }

    @Test
    public void test7() throws Exception {
        double angle1 = Math.PI;
        double angle2 = Math.PI-0.1;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(0.1, r.distance, DELTA);
        assertEquals(true, r.direction);
    }

    @Test
    public void test8() throws Exception {
        double angle1 = Math.PI-0.1;
        double angle2 = -Math.PI;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(0.1, r.distance, DELTA);
        assertEquals(false, r.direction);
    }

    @Test
    public void test9() throws Exception {
        double angle1 = -Math.PI;
        double angle2 = Math.PI-0.1;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(0.1, r.distance, DELTA);
        assertEquals(true, r.direction);
    }

    @Test
    public void test10() throws Exception {
        double angle1 = Math.PI/2;
        double angle2 = -Math.PI/2;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(Math.PI, r.distance, DELTA);
    }

    @Test
    public void test11() throws Exception {
        double angle1 = 0;
        double angle2 = -Math.PI;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(Math.PI, r.distance, DELTA);
    }

    @Test
    public void test12() throws Exception {
        double angle1 = 0;
        double angle2 = Math.PI;
        RotationalDistance r = new RotationalDistance(angle1, angle2);
        assertEquals(Math.PI, r.distance, DELTA);
    }
}