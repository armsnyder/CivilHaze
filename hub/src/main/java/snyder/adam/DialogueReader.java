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
