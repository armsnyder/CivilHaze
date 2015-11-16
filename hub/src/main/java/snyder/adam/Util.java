package snyder.adam;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.util.Random;

/**
 * Miscellaneous utility functions
 * @author Adam Snyder
 */
public class Util {

    public static final Random RANDOM = new Random();

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

    public static String getStringFromInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
