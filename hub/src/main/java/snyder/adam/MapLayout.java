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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Defines the placement of all the background and static elements of a game map
 * @author Adam Snyder
 */
public class MapLayout {
    private ArrayList<TypedDataTile> background;
    private ArrayList<BuildingDataTile> buildings;
    private ArrayList<DataTile> fence;
    private ArrayList<DataTile> gate;
    private int width;
    private int height;

    public MapLayout() {
        init();
    }

    private void init() {
        background = new ArrayList<>();
        buildings = new ArrayList<>();
        fence = new ArrayList<>();
        gate = new ArrayList<>();
        width = 0;
        height = 0;
    }

    public void loadData(Element layout) {
        init();

        width = Integer.parseInt(layout.getAttribute("width"));
        height = Integer.parseInt(layout.getAttribute("height"));

        NodeList buildingList = layout.getElementsByTagName("building");
        for (int i = 0; i < buildingList.getLength(); i++) {
            Element building = (Element) buildingList.item(i);
            String type = building.getTextContent();
            int x = Integer.parseInt(building.getAttribute("x"));
            int y = Integer.parseInt(building.getAttribute("y"));
            int width = Integer.parseInt(building.getAttribute("width"));
            int height = Integer.parseInt(building.getAttribute("height"));
            buildings.add(new BuildingDataTile(type, x, y, width, height));
        }

        Element fenceElement = (Element) layout.getElementsByTagName("fence").item(0);
        if (fenceElement != null) {
            NodeList fenceList = fenceElement.getElementsByTagName("tile");
            for (int i = 0; i < fenceList.getLength(); i++) {
                Element tile = (Element) fenceList.item(i);
                int x = Integer.parseInt(tile.getAttribute("x"));
                int y = Integer.parseInt(tile.getAttribute("y"));
                fence.add(new DataTile(x, y));
            }
        }

        Element gateElement = (Element) layout.getElementsByTagName("gate").item(0);
        if (gateElement != null) {
            NodeList gateList = gateElement.getElementsByTagName("tile");
            for (int i = 0; i < gateList.getLength(); i++) {
                Element tile = (Element) gateList.item(i);
                int x = Integer.parseInt(tile.getAttribute("x"));
                int y = Integer.parseInt(tile.getAttribute("y"));
                gate.add(new DataTile(x, y));
            }
        }

        Element backgroundElement = (Element) layout.getElementsByTagName("background").item(0);
        if (backgroundElement != null) {
            NodeList backgroundList = backgroundElement.getElementsByTagName("tile");
            for (int i = 0; i < backgroundList.getLength(); i++) {
                Element tile = (Element) backgroundList.item(i);
                String type = tile.getTextContent();
                int x = Integer.parseInt(tile.getAttribute("x"));
                int y = Integer.parseInt(tile.getAttribute("y"));
                background.add(new TypedDataTile(type, x, y));
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<TypedDataTile> getBackground() {
        return (ArrayList<TypedDataTile>) background.clone();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<BuildingDataTile> getBuildings() {
        return (ArrayList<BuildingDataTile>) buildings.clone();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<DataTile> getFence() {
        return (ArrayList<DataTile>) fence.clone();
    }

    @SuppressWarnings("unchecked")
    public ArrayList<DataTile> getGate() {
        return (ArrayList<DataTile>) gate.clone();
    }

    class DataTile {
        public final int x;
        public final int y;

        public DataTile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    class TypedDataTile extends DataTile {
        public final String type;

        public TypedDataTile(String type, int x, int y) {
            super(x, y);
            this.type = type;
        }
    }

    class BuildingDataTile extends TypedDataTile {
        public final int width;
        public final int height;

        public BuildingDataTile(String type, int x, int y, int width, int height) {
            super(type, x, y);
            this.width = width;
            this.height = height;
        }
    }
}
