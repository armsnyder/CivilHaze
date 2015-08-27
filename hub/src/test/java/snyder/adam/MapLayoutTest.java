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
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource("mapLayouts.xml");
        assert url != null;
        File xmlFile = new File(url.getFile());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();
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