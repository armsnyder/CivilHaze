/*
 * Come Again is an interactive art piece in which participants perform a series of reckless prison breaks.
 * Copyright (C) 2015  Adam Snyder
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version. Any redistribution must give proper attribution to the original author.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package snyder.adam;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import snyder.adam.MapLayout;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author Adam Snyder
 */
public class MapLayoutTest {

    @Test
    public void testLoadJSON_empty() throws Exception {
        MapLayout mapLayout = new MapLayout();
        Element map = getMap("empty");
        mapLayout.loadData(map);
        assertEquals(0, mapLayout.getWidth());
        assertEquals(0, mapLayout.getHeight());
        assertEquals(0, mapLayout.getBackground().size());
        assertEquals(0, mapLayout.getBuildings().size());
        assertEquals(0, mapLayout.getFence().size());
        assertEquals(0, mapLayout.getGate().size());
    }

    @Test
    public void testLoadJSON_simple() throws Exception {
        MapLayout mapLayout = new MapLayout();
        Element map = getMap("simple");
        mapLayout.loadData(map);
        assertEquals(1, mapLayout.getWidth());
        assertEquals(1, mapLayout.getHeight());
        assertEquals(1, mapLayout.getBackground().size());
        assertEquals("dirt", mapLayout.getBackground().get(0).type);
        assertEquals(0, mapLayout.getBackground().get(0).x);
        assertEquals(0, mapLayout.getBackground().get(0).y);
        assertEquals(1, mapLayout.getBuildings().size());
        assertEquals("house", mapLayout.getBuildings().get(0).type);
        assertEquals(0, mapLayout.getBuildings().get(0).x);
        assertEquals(0, mapLayout.getBuildings().get(0).y);
        assertEquals(1, mapLayout.getBuildings().get(0).width);
        assertEquals(1, mapLayout.getBuildings().get(0).height);
        assertEquals(0, mapLayout.getFence().size());
        assertEquals(0, mapLayout.getGate().size());
    }

    @Test
    public void testLoadJSON_complex() throws Exception {
        MapLayout mapLayout = new MapLayout();
        Element map = getMap("complex");
        mapLayout.loadData(map);
        assertEquals(7, mapLayout.getWidth());
        assertEquals(5, mapLayout.getHeight());
        assertEquals(35, mapLayout.getBackground().size());
        assertEquals(4, mapLayout.getBuildings().size());
        assertEquals(20, mapLayout.getFence().size());
        assertEquals(2, mapLayout.getGate().size());
    }

    private Element getMap(String key) throws IOException, ParserConfigurationException, SAXException {
        Document document = Util.getXMLResource("mapLayouts.xml", getClass().getClassLoader());
        NodeList nodeList = document.getElementsByTagName("layout");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            String name = item.getAttributes().getNamedItem("name").getNodeValue();
            if (name.equals(key)) {
                return (Element) item;
            }
        }
        return null;
    }
}