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
package de.iteratec.iteraplan.businesslogic.exchange.common.informationflow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Class to parse a visio information flow layout template.
 */
public class VisioInformationFlowTemplateParser {

  private static final String                            APPLICATION_SHAPE_XPATH = "Shapes/Shape[Prop[@NameU='ApplicationName']]";
  private static final String                            ID_PROPERTY_XPATH       = "Prop[@NameU='Id']/Value/text()";
  private static final String                            PINX_XPATH              = "XForm/PinX/text()";
  private static final String                            PINY_XPATH              = "XForm/PinY/text()";

  private static final Logger                            LOGGER                  = Logger
                                                                                     .getIteraplanLogger(VisioInformationFlowTemplateParser.class);

  private final File                                     templateFile;
  private Map<Integer, VisioInformationFlowTemplateNode> parsedNodes;

  public VisioInformationFlowTemplateParser(File templateFile) {
    this.templateFile = templateFile;
  }

  public Map<Integer, VisioInformationFlowTemplateNode> getNodeMap() {
    if (parsedNodes == null) {
      parseTemplate();
    }
    return parsedNodes;
  }

  private Document parseXmlFile() {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

    try {
      DocumentBuilder db = dbf.newDocumentBuilder();
      FileInputStream fis = FileUtils.openInputStream(templateFile);
      return db.parse(fis);
    } catch (SAXException se) {
      LOGGER.error("Error during parsing of template '" + templateFile.getName() + "'.", se);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    } catch (ParserConfigurationException pce) {
      LOGGER.error("Error during parser creation.", pce);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    } catch (IOException ie) {
      LOGGER.error("I/O Error during parsing of template '" + templateFile.getName() + "'.", ie);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public void parseTemplate() {
    Document dom = parseXmlFile();

    XPath xPath = XPathFactory.newInstance().newXPath();
    String xPathFirstLevelApplicationNodesExpression = "/VisioDocument/Pages/Page[1]/" + APPLICATION_SHAPE_XPATH;
    parsedNodes = Maps.newHashMap();

    try {
      NodeList firstLevelApplicationNodes = (NodeList) xPath.evaluate(xPathFirstLevelApplicationNodesExpression, dom, XPathConstants.NODESET);
      for (int i = 0; i < firstLevelApplicationNodes.getLength(); i++) {
        Node node = firstLevelApplicationNodes.item(i);
        parseNodeAndAddToMap(node, null);
      }
    } catch (XPathExpressionException e) {
      LOGGER.error("XPath-error during parsing of template '" + templateFile.getName() + "'.", e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

  }

  /**
   * Extracts the relevant information from the given {@link Node} into a
   * {@link VisioInformationFlowTemplateNode} and adds this to the given
   * nodes map. Child nodes will be recursively added to the map, too, and the
   * parent/child references of affected {@link VisioInformationFlowTemplateNode}
   * updated.
   * @param node
   *          {@link Node} to parse.
   * @param parentNode
   *          The, already parsed, parent of the node to be parsed here. Can be null for the root node.
   */
  private void parseNodeAndAddToMap(Node node, VisioInformationFlowTemplateNode parentNode) throws XPathExpressionException {

    XPath xPath = XPathFactory.newInstance().newXPath();

    String idString = (String) xPath.evaluate(ID_PROPERTY_XPATH, node, XPathConstants.STRING);
    Integer id = Integer.valueOf(idString);
    String pinXString = (String) xPath.evaluate(PINX_XPATH, node, XPathConstants.STRING);
    double pinX = Double.parseDouble(pinXString);
    String pinYString = (String) xPath.evaluate(PINY_XPATH, node, XPathConstants.STRING);
    double pinY = Double.parseDouble(pinYString);

    VisioInformationFlowTemplateNode parsedNode = new VisioInformationFlowTemplateNode(id, pinX, pinY);
    parsedNodes.put(id, parsedNode);

    if (parentNode != null) {
      parsedNode.setParentId(parentNode.getId());
      parentNode.addChildrenId(id);
    }

    NodeList childApplicationNodes = (NodeList) xPath.evaluate(APPLICATION_SHAPE_XPATH, node, XPathConstants.NODESET);

    for (int i = 0; i < childApplicationNodes.getLength(); i++) {
      parseNodeAndAddToMap(childApplicationNodes.item(i), parsedNode);
    }
  }

}
