package snyder.adam;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Miscellaneous utility functions
 * @author Adam Snyder
 */
public class Util {

    public static Document getXMLResource(String path) throws ParserConfigurationException, IOException, SAXException {
        File xmlFile = getResource(path);
        return getXML(xmlFile);
    }

    public static Document getXMLResource(String path, ClassLoader classLoader)
            throws ParserConfigurationException, IOException, SAXException {
        File xmlFile = getResource(path, classLoader);
        return getXML(xmlFile);
    }

    public static File getResource(String path) {
        ClassLoader classLoader = Util.class.getClassLoader();
        return getResource(path, classLoader);
    }

    public static File getResource(String path, ClassLoader classLoader) {
        URL url = classLoader.getResource(path);
        assert url != null;
        return new File(url.getFile());
    }

    public static Document getXML(File xmlFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();
        return document;
    }
}
