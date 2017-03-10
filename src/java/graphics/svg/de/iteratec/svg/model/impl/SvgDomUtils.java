/*
 * iteraplan is an IT Governance web application developed by iteratec, GmbH
 * Copyright (C) 2004 - 2014 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact iteratec GmbH headquarters at Inselkammerstr. 4
 * 82008 Munich - Unterhaching, Germany, or at email address info@iteratec.de.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "iteraplan" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by iteraplan".
 */
package de.iteratec.svg.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public final class SvgDomUtils {

  private static final String MASTER_SHAPE_PATTERN = "Root";

  private SvgDomUtils() {
    // avoid instantiation of this static methods-only class
  }

  public static List<Node> loadMasterShapes(Node documentRoot) {
    List<Node> masters = new ArrayList<Node>();
    findMasterShapes(documentRoot, masters);
    return masters;
  }

  public static String getValueForAttribute(String attributeName, Node parentNode) {
    NamedNodeMap attrs = parentNode.getAttributes();
    if (attrs == null) {
      return null;
    }
    Node attr = attrs.getNamedItem(attributeName);

    if (attr != null) {
      return attr.getNodeValue();
    }
    else {
      return null;
    }
  }

  public static Node getNodeWithId(Node currentShape, String searchedId) {
    // This method should provide the ability to search each shape for
    // certain markers, so that attributes can be set on the right place.
    List<Node> result = new ArrayList<Node>();
    getNodeWithIdIntern(currentShape, searchedId, result);

    // Error handling. Concept allows null? TODO Shouldn't do so...
    if (result.size() == 0) {
      return null;
    }
    return result.get(0);
  }

  public static void setOrCreateAttributeForNode(Node node, String attributeName, String attributeValue) {
    // This method should provide the option to set an attribute in a given node if
    // the attribute exists. Otherwise the attribute is being created and then the
    // requested value is set.

    if (node.getAttributes().getNamedItem(attributeName) == null) {
      // Attribute doesen't exist
      Element e = (Element) node;
      e.setAttribute(attributeName, attributeValue);
    }
    else {
      // Attribute exists
      node.getAttributes().getNamedItem(attributeName).setNodeValue(attributeValue);
    }
  }

  public static Node getCSSStylingNode(Node documentRoot) {

    Node defsNode = getFirstChildNodeWithName("defs", documentRoot);
    if (defsNode != null) {
      // gets the styles Node
      return getFirstChildNodeWithName("style", defsNode);
    }
    return null;
  }

  public static Element createNodeWithName(String nodeName, Node parentNode, Document document) {
    Element newElement = document.createElement(nodeName);
    parentNode.appendChild(newElement);
    return newElement;
  }

  private static Node getFirstChildNodeWithName(String nodeName, Node parentNode) {
    parentNode.normalize();
    NodeList childNodes = parentNode.getChildNodes();
    Node resultNode = null;
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node childElement = childNodes.item(i);
      if (childElement.getNodeName().equalsIgnoreCase(nodeName)) {
        resultNode = childElement;
      }
    }
    return resultNode;
  }

  private static void findMasterShapes(Node node, List<Node> result) {
    // Check if current element is a master and if so add it to the result list
    NamedNodeMap map = node.getAttributes();
    if (map != null) {
      Node attr = map.getNamedItem("id");
      if (attr != null && attr.getNodeValue().endsWith(MASTER_SHAPE_PATTERN)) {
        result.add(node);
        return;
      }
    }
    // Make a list of all child elements and check them
    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      Node childElement = childNodes.item(i);
      findMasterShapes(childElement, result);
    }
  }

  private static void getNodeWithIdIntern(Node node, String searchedId, List<Node> result) {

    // Check if the current node is the searched one
    NamedNodeMap map = node.getAttributes();
    if (map != null) {
      Node attribute = map.getNamedItem("id");
      if (attribute != null && attribute.getNodeValue().equalsIgnoreCase(searchedId)) {
        result.add(node);
        return;
      }
    }

    // Search the children
    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      getNodeWithIdIntern(childNodes.item(i), searchedId, result);
    }
  }

}
