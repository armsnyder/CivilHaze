/*
 * Copyright (c) 2015 Adam Snyder. All rights reserved.
 */

package com.armsnyder.civilhaze;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * @author Adam Snyder
 */
public class DialogueReader {
    private final Element dialogueXML;

    public DialogueReader(String groupID) throws ParserConfigurationException, IOException, SAXException {
        Document document = Util.getXMLResource("dialogue.xml");
        NodeList groups = document.getElementsByTagName("group");
        Element dialogueXMLTemp = null;
        for (int i = 0; i < groups.getLength(); i++) {
            Node groupNode = groups.item(i);
            if (groupNode.getNodeType() == Node.ELEMENT_NODE) {
                Element groupElement = ((Element) groupNode);
                String iGroupID = groupElement.getAttribute("id");
                if (iGroupID.equals(groupID)) {
                    dialogueXMLTemp = groupElement;
                    break;
                }
            }
        }
        dialogueXML = dialogueXMLTemp;
    }
}
