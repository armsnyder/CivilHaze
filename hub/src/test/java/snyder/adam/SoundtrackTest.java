/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze;

public class SoundtrackTest {

//    Constructor<Soundtrack> constructor;
//    Method getPlayingSegment;
//
//
//    @Before
//    public void setUp() throws Exception {
//        constructor = Soundtrack.class.getDeclaredConstructor(String.class, float[].class);
//        constructor.setAccessible(true);
//        getPlayingSegment = Soundtrack.class.getDeclaredMethod("getPlayingSegment", float.class, int.class,
//                int.class);
//        getPlayingSegment.setAccessible(true);
//    }
//
//    private int getSegment(float[] markers, float position) throws Exception {
//        Soundtrack soundtrack = constructor.newInstance("harp.ogg", markers);
//        return (int) getPlayingSegment.invoke(soundtrack, position, 0, markers.length-1);
//    }
//
//    @Test
//    public void testGetPlayingSegment_1() throws Exception {
//        int playing = getSegment(new float[]{0, 0.5f}, 0.2f);
//        assertEquals(0, playing);
//    }
//
//    @Test
//    public void testGetPlayingSegment_2() throws Exception {
//        int playing = getSegment(new float[]{0, 0.5f}, 0);
//        assertEquals(0, playing);
//    }
//
//    @Test
//    public void testGetPlayingSegment_3() throws Exception {
//        int playing = getSegment(new float[]{0, 0.5f}, 0.6f);
//        assertEquals(1, playing);
//    }
//
//    @Test
//    public void testGetPlayingSegment_4() throws Exception {
//        int playing = getSegment(new float[]{0, 0.5f}, 0.5f);
//        assertEquals(1, playing);
//    }
//
//    @Test
//    public void testGetPlayingSegment_5() throws Exception {
//        int playing = getSegment(new float[]{0}, 0.5f);
//        assertEquals(0, playing);
//    }
//
//    @Test
//    public void testGetPlayingSegment_6() throws Exception {
//        int playing = getSegment(new float[]{0}, 0);
//        assertEquals(0, playing);
//    }
//
//    @Test
//    public void testGetPlayingSegment_7() throws Exception {
//        int playing = getSegment(new float[]{0, 0.2f, 0.4f, 0.6f, 0.8f}, 0.7f);
//        assertEquals(3, playing);
//    }
}