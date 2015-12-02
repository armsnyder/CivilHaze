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

package snyder.adam;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.newdawn.slick.Color;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;


public class ParticipantTest {

    @Test
    public void testAddMessage() throws Exception {
        Participant p = new Participant("foo");
        Map<String, Object> map = new HashMap<>();
        map.put("a", true);
        map.put("c", 1);
        map.put("g", "hello");
        map.put("6", "hello");
        p.sendMessage(map);
        assertEquals("[{\"a\":true,\"c\":1,\"6\":\"hello\",\"g\":\"hello\"}]", p.retrieveMessages().toString());
        assertEquals("[]", p.retrieveMessages().toString());
        p.sendMessage("a", "cool");
        assertEquals("[{\"a\":\"cool\"}]", p.retrieveMessages().toString());
        assertEquals("[]", p.retrieveMessages().toString());
    }

    @Test
    public void testNoMessage() throws Exception {
        Participant p = new Participant("foo");
        Map<String, Object> map = new HashMap<>();
        p.sendMessage(map);
        assertEquals("[{}]", p.retrieveMessages().toString());
        assertEquals("[]", p.retrieveMessages().toString());
    }

    @Test
    public void testMultipleMessages() throws Exception {
        Participant p = new Participant("foo");
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        p.sendMessage(map);
        map.remove("a");
        map.put("G", true);
        p.sendMessage(map);
        assertEquals("[{\"a\":1},{\"G\":true}]", p.retrieveMessages().toString());
        assertEquals("[]", p.retrieveMessages().toString());
    }

    @Test
    public void testMisc() throws Exception {
        JSONObject o = new JSONObject();
        assertEquals(0, o.length());
        o.put("connected", false);
        assertEquals(1, o.length());
        assertEquals("{\"connected\":false}", o.toString());
        o.put("connected", true);
        assertEquals(1, o.length());
        assertEquals("{\"connected\":true}", o.toString());
        JSONArray a = new JSONArray();
        assertEquals(0, a.length());
        a.put("a");
        assertEquals(1, a.length());
        o = new JSONObject();
        o.put("a", new String[]{"b", "c", "d"});
        assertEquals("{\"a\":[\"b\",\"c\",\"d\"]}", o.toString());
    }
}