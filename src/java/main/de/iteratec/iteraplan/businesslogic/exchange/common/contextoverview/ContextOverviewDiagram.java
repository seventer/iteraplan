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
package de.iteratec.iteraplan.businesslogic.exchange.common.contextoverview;

import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * This is the instance of the ContextOverviewDiagram. It owns all information about the report and creates the SVG 
 */
public class ContextOverviewDiagram {

  private String                          centralIsPngUrl;
  private NameUriPair                     centralIsNameUriPair;
  private String[]                        pngURLs;
  private String[]                        messageKeys;
  private Map<Integer, List<NameUriPair>> allBuildingBlocks;
  private Document                        doc;
  private Element                         svgRoot;


  private static final String             FIELDWIDTH  = "824";
  private static final String             FIELDHEIGHT = "690";

  // ctor builds w3c svg document with root, including fixed part for title
  public ContextOverviewDiagram() {
    pngURLs = new String[13];
    messageKeys = new String[13];
    allBuildingBlocks = new HashMap<Integer, List<NameUriPair>>();

    DOMImplementation dom = SVGDOMImplementation.getDOMImplementation();
    String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    doc = dom.createDocument(svgNS, "svg", null);
    svgRoot = doc.getDocumentElement();
    setWidthHeight(svgRoot, "2480", "3508");

    Element group = doc.createElement("g");
    setBasicGroupAttributes(group);
    setXYOffset(group, "4", "4");

    svgRoot.appendChild(group);

    Element aTitle = doc.createElement("a");
    Element titleTextA = doc.createElement("text");
    setXY(titleTextA, "250", "200");
    titleTextA.setAttribute("font-size", "64px");
    titleTextA.setTextContent("itera");

    Element titleTextB = doc.createElement("text");
    setXY(titleTextB, "364", "200");
    titleTextB.setAttribute("font-size", "64px");
    titleTextB.setAttribute("fill", "#A9218E");
    titleTextB.setTextContent("plan");

    Element titleTextC = doc.createElement("text");
    setXY(titleTextC, "110", "330");
    titleTextC.setAttribute("font-size", "48px");
    titleTextC.setAttribute("fill", "#A9218E");
    titleTextC.setTextContent("Context Overview Diagram");

    aTitle.appendChild(titleTextA);
    aTitle.appendChild(titleTextB);
    aTitle.appendChild(titleTextC);
    group.appendChild(aTitle);

  }

  //This Method is called for each Part, except Title and central IS, to create the individual SVG structure 
  public void createListPart(int part, String typeName, List<NameUriPair> nups, String pngURL) {

    Element group = doc.createElement("g");
    setBasicGroupAttributes(group);
    setXYOffset(group, String.valueOf(getOffsetX(part)), String.valueOf(getOffsetY(part)));

    svgRoot.appendChild(group);

    Element currentIcon = doc.createElement("image");
    currentIcon.setAttribute("xlink:href", pngURL);
    setXY(currentIcon, String.valueOf(10 + getOffsetX(part)), String.valueOf(10 + getOffsetY(part)));
    setWidthHeight(currentIcon, "100", "100");

    group.appendChild(currentIcon);

    Element currentLabel = doc.createElement("a");
    Element currentLabelText = doc.createElement("text");
    setXY(currentLabelText, String.valueOf(120 + getOffsetX(part)), String.valueOf(75 + getOffsetY(part)));
    setWidthHeight(currentLabelText, "704", "120");
    currentLabelText.setAttribute("font-size", "48px");
    currentLabelText.setAttribute("font-weight", "bold");
    currentLabelText.setTextContent(typeName);

    currentLabel.appendChild(currentLabelText);
    group.appendChild(currentLabel);

    //create Building Block Lines
    Element allLines = doc.createElement("a");
    allLines.setAttribute("font-size", "36px");

    Element currentLine0 = doc.createElement("text");
    setXY(currentLine0, String.valueOf(10 + getOffsetX(part)), String.valueOf(170 + getOffsetY(part)));
    int lineNumber = 0;
    if (nups.size() > 0) {
      setLineContent(nups, allLines, currentLine0, lineNumber);
    }

    Element currentLine1 = doc.createElement("text");
    setXY(currentLine1, String.valueOf(10 + getOffsetX(part)), String.valueOf(255 + getOffsetY(part)));
    lineNumber = 1;
    if (nups.size() > lineNumber) {
      setLineContent(nups, allLines, currentLine1, lineNumber);
    }


    Element currentLine2 = doc.createElement("text");
    setXY(currentLine2, String.valueOf(10 + getOffsetX(part)), String.valueOf(340 + getOffsetY(part)));
    lineNumber = 2;
    if (nups.size() > lineNumber) {
      setLineContent(nups, allLines, currentLine2, lineNumber);
    }


    Element currentLine3 = doc.createElement("text");
    setXY(currentLine3, String.valueOf(10 + getOffsetX(part)), String.valueOf(425 + getOffsetY(part)));
    lineNumber = 3;
    if (nups.size() > lineNumber) {
      setLineContent(nups, allLines, currentLine3, lineNumber);
    }


    Element currentLine4 = doc.createElement("text");
    setXY(currentLine4, String.valueOf(10 + getOffsetX(part)), String.valueOf(510 + getOffsetY(part)));
    lineNumber = 4;
    if (nups.size() > lineNumber) {
      setLineContent(nups, allLines, currentLine4, lineNumber);
    }


    Element currentLine5 = doc.createElement("text");
    setXY(currentLine5, String.valueOf(10 + getOffsetX(part)), String.valueOf(595 + getOffsetY(part)));
    lineNumber = 5;
    if (nups.size() > lineNumber) {
      Element aLink = doc.createElement("a");
      currentLine5.setTextContent(nups.get(5).getName());
      if (null != nups.get(5).getUri()) {
        aLink.setAttribute("xlink:href", nups.get(5).getUri());
      }
      aLink.appendChild(currentLine5);
      allLines.appendChild(aLink);
    }

    group.appendChild(allLines);

  }

  /**
   * @param nups
   * @param allLines
   * @param currentLine
   */
  private void setLineContent(List<NameUriPair> nups, Element allLines, Element currentLine, int lineNumber) {
    Element aLink = doc.createElement("a");
    aLink.setAttribute("xlink:href", nups.get(lineNumber).getUri());
    currentLine.setTextContent(nups.get(lineNumber).getName());
    aLink.appendChild(currentLine);
    allLines.appendChild(aLink);
  }

  /**
   * @param group
   */
  private void setBasicGroupAttributes(Element group) {
    group.setAttribute("id", "TransformNode");
    group.setAttribute("owidth", FIELDWIDTH);
    group.setAttribute("oheight", FIELDHEIGHT);
  }

  private void setXYOffset(Element group, String ox, String oy) {
    group.setAttribute("ox", ox);
    group.setAttribute("oy", oy);
  }

  private void setXY(Element element, String x, String y) {
    element.setAttribute("x", x);
    element.setAttribute("y", y);
  }

  private void setWidthHeight(Element element, String width, String height) {
    element.setAttribute("width", width);
    element.setAttribute("height", height);
  }

  //This Method creates the Part of central IS
  public void createCentralPart() {
    
    Element group = doc.createElement("g");
    setBasicGroupAttributes(group);
    setXYOffset(group, "828", "1384");

    svgRoot.appendChild(group);
    
    Element centralIcon = doc.createElement("image");
    centralIcon.setAttribute("xlink:href", centralIsPngUrl);
    setXY(centralIcon, "838", "1394");
    setWidthHeight(centralIcon, "100", "100");

    group.appendChild(centralIcon);

    Element aLinkLabel = doc.createElement("a");
    aLinkLabel.setAttribute("xlink:href", centralIsNameUriPair.getUri());
    Element centralIsLabel = doc.createElement("text");
    setXY(centralIsLabel, "948", "1459");
    setWidthHeight(centralIsLabel, FIELDWIDTH, "120");
    centralIsLabel.setAttribute("font-size", "52px");
    centralIsLabel.setAttribute("font-weight", "bold");
    centralIsLabel.setAttribute("text-decoration", "underline");
    centralIsLabel.setTextContent(centralIsNameUriPair.getName());

    aLinkLabel.appendChild(centralIsLabel);
    group.appendChild(aLinkLabel);

  }

  //This method creates the timestamp as footer
  public void createTimestamp() {

    Element group = doc.createElement("g");
    group.setAttribute("id", "TransformNode");
    group.setAttribute("owidth", FIELDWIDTH);
    group.setAttribute("oheigth", "30");
    setXYOffset(group, "1652", "3474");

    svgRoot.appendChild(group);

    Element timestamp = doc.createElement("text");
    timestamp.setAttribute("font-size", "36px");
    setXY(timestamp, "1662", "3484");

    Date now = new Date();
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM, Locale.ENGLISH);
    timestamp.setTextContent("global.timestamp" + ": " + df.format(now));
    group.appendChild(timestamp);
  }

  //Method to execute the Diagram creation
  //TODO include visualization in running System
  public void showDiagramm() {
    for (int part = 0; part < 13; part++) {
      createListPart(part, messageKeys[part], allBuildingBlocks.get(Integer.valueOf(part)), pngURLs[part]);
    }
    createCentralPart();
    createTimestamp();
  }

  //Methods to calculate offset 
  public int getOffsetX(int part) {

    switch (part) {
      case 0:
        return 828;
      case 1:
        return 1652;
      case 2:
        return 4;
      case 3:
        return 828;
      case 4:
        return 1652;
      case 5:
        return 4;
      case 6:
        return 1652;
      case 7:
        return 4;
      case 8:
        return 828;
      case 9:
        return 1652;
      case 10:
        return 4;
      case 11:
        return 828;
      case 12:
        return 1652;
      default:
        return 0;
    }

  }

  public int getOffsetY(int part) {

    switch (part) {
      case 0:
        return 4;
      case 1:
        return 4;
      case 2:
        return 694;
      case 3:
        return 694;
      case 4:
        return 694;
      case 5:
        return 1384;
      case 6:
        return 1384;
      case 7:
        return 2074;
      case 8:
        return 2074;
      case 9:
        return 2074;
      case 10:
        return 2764;
      case 11:
        return 2764;
      case 12:
        return 2764;
      default:
        return 0;
    }

  }


  public void setPngURLs(int idx, String pngURL) {
    pngURLs[idx] = pngURL;
  }

  public void setMessageKey(int idx, String messageKey) {
    messageKeys[idx] = messageKey;
  }

  public void setBuildingBlocks(int idx, List<NameUriPair> buildingBlocks) {
    allBuildingBlocks.put(Integer.valueOf(idx), buildingBlocks);
  }

  public void setCentralIsPng(String centralIsPng) {
    this.centralIsPngUrl = centralIsPng;
  }

  public void setCentralIsNameUriPair(NameUriPair centralIsNameUriPair) {
    this.centralIsNameUriPair = centralIsNameUriPair;
  }

  //method to convert Document to String for testing
  public String getStringFromDocument() {
    try {
      DOMSource domSource = new DOMSource(doc);
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.transform(domSource, result);
      return writer.toString();
    } catch (TransformerException ex) {
      ex.printStackTrace();
      return null;
    }
  }

}
