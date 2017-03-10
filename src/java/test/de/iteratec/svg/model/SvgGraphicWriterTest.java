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
package de.iteratec.svg.model;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.svg.SvgGraphicWriter;
import de.iteratec.svg.styling.SvgBaseStyling;


public class SvgGraphicWriterTest {
  // code is only copied from the old test routines so far.
  // The only significant test in here is right now, if no exceptions are thrown

  private static final Logger LOGGER = Logger.getIteraplanLogger(SvgGraphicWriterTest.class);

  @Before
  public void setUp() {
    // Nothing to do
  }

  @After
  public void tearDown() {
    // Nothing to do
  }

  @Test
  public void testWriteToJPEG() {

    Document svgDocument = getTestDocument();

    FileOutputStream fos = getTestOutputFileStream("jpg");

    try {
      svgDocument.finalizeDocument();
      org.w3c.dom.Document domDoc = SvgGraphicWriter.transformToDomDocument(svgDocument);
      SvgGraphicWriter.writeToJPEG(domDoc, fos);
      fos.close();
    } catch (SvgExportException e) {
      LOGGER.error(e);
      fail(e.getMessage());
    } catch (IOException e) {
      LOGGER.error(e);
      fail(e.getMessage());
    }
  }

  @Test
  public void testWriteToPNG() {
    Document svgDocument = getTestDocument();
    FileOutputStream fos = getTestOutputFileStream("png");

    try {
      svgDocument.finalizeDocument();
      org.w3c.dom.Document domDoc = SvgGraphicWriter.transformToDomDocument(svgDocument);
      SvgGraphicWriter.writeToPNG(domDoc, fos);
      fos.close();
    } catch (SvgExportException e) {
      LOGGER.error(e);
      fail(e.getMessage());
    } catch (IOException e) {
      LOGGER.error(e);
      fail(e.getMessage());
    }
  }

  @Test
  public void testWriteToPDF() {
    Document svgDocument = getTestDocument();
    FileOutputStream fos = getTestOutputFileStream("pdf");

    try {
      svgDocument.finalizeDocument();
      org.w3c.dom.Document domDoc = SvgGraphicWriter.transformToDomDocument(svgDocument);
      SvgGraphicWriter.writeToPDF(domDoc, fos);
      fos.close();
    } catch (SvgExportException e) {
      LOGGER.error(e);
      fail(e.getMessage());
    } catch (IOException e) {
      LOGGER.error(e);
      fail(e.getMessage());
    }
  }

  @Test
  public void testSvgOutput() {
    Document svgDocument = getTestDocument("resources/SVGBubbleTemplate.svg");

    // Test 1
    svgDocument.setShapeCSSStylingEnabled(true);

    Map<String, String> props = new HashMap<String, String>();
    props.put(SvgBaseStyling.FILL_COLOR, "#000000");
    try {
      svgDocument.createNewCSSClass("fill", props);
      Shape shape = svgDocument.createNewShape("BubbleRoot");
      shape.setTextFieldValue(" v sfdjbgjsdf vbsdnbj vnjfnvjfn vnfsdjfdds sdfg sdfgsdfs sdg");
      shape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_SIZE, "10");
      shape.addCSSClass("legendHeaderStyle");
      shape.addTextCSSClass("bubbleText");
      shape.setResizeTextWithShape(false);
      shape.setBoundTextInShape(true);
      shape.setSize(500, 450);
      shape.setPosition(200, 200);
      shape.getDefaultTextStyle().setAttribute(SvgBaseStyling.FONT_ALIGN, "middle");
      shape.setShapeCSSEnabled(true);
      shape.setTextCSSEnabled(true);

    } catch (SvgExportException e) {
      LOGGER.error(e);
      fail(e.getMessage());
    }

    // Test 2
    FileOutputStream fos = getTestOutputFileStream("svg");

    try {
      svgDocument.finalizeDocument();
      SvgGraphicWriter.writeToSvg(svgDocument, fos);
      fos.close();
    } catch (SvgExportException e) {
      LOGGER.error(e);
      fail(e.getMessage());
    } catch (IOException e) {
      LOGGER.error(e);
      fail(e.getMessage());
    }
  }

  private Document getTestDocument(String fileName) {
    InputStream inputStream = getClass().getResourceAsStream(fileName);

    DocumentFactory svgDocumentFactory = DocumentFactory.getInstance();
    Document svgDocument = null;
    try {
      svgDocument = svgDocumentFactory.loadDocument(inputStream);
      inputStream.close();
    } catch (IOException e) {
      LOGGER.error(e);
    } catch (ParserConfigurationException e) {
      LOGGER.error(e);
    } catch (SAXException e) {
      LOGGER.error(e);
    } catch (SvgExportException e) {
      LOGGER.error(e);
    }
    return svgDocument;
  }

  private Document getTestDocument() {
    return getTestDocument("resources/iteraplanLandscapeDiagram.svg");
  }

  private FileOutputStream getTestOutputFileStream(String extension) {
    File tempOutFile;
    try {
      tempOutFile = File.createTempFile("iteraplanLandscapeDiagram", extension);
      tempOutFile.deleteOnExit();
      return new FileOutputStream(tempOutFile);
    } catch (FileNotFoundException e) {
      LOGGER.error(e);
      return null;
    } catch (IOException e) {
      LOGGER.error(e);
      return null;
    }
  }

}
