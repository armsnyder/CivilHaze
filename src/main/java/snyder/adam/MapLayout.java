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
        background = new ArrayList<TypedDataTile>();
        buildings = new ArrayList<BuildingDataTile>();
        fence = new ArrayList<DataTile>();
        gate = new ArrayList<DataTile>();
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

    public ArrayList<TypedDataTile> getBackground() {
        return (ArrayList<TypedDataTile>) background.clone();
    }

    public ArrayList<BuildingDataTile> getBuildings() {
        return (ArrayList<BuildingDataTile>) buildings.clone();
    }

    public ArrayList<DataTile> getFence() {
        return (ArrayList<DataTile>) fence.clone();
    }

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
