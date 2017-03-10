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
package de.iteratec.iteraplan.businesslogic.exchange.visio;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.iteratec.iteraplan.common.Logger;


public final class VisioExportUtils {

  private static final Logger LOGGER        = Logger.getIteraplanLogger(VisioExportUtils.class);

  /**
   * All generated diagrams will be saved into this folder if the system property
   * is set.
   */
  private static final String EXPORT_FOLDER = System.getProperty("visioExportTestFolder");

  private VisioExportUtils() {
    // empty private constructor to avoid Sonar's "Hide Utility Class Constructor"-violation
  }

  /**
   * @param exportDoc
   * @return The W3C DOM document created from the Visio export document.
   * @throws IOException
   * @throws ParserConfigurationException
   * @throws SAXException
   */
  public static Document visioDocumentToDOM(de.iteratec.visio.model.Document exportDoc) throws IOException, ParserConfigurationException,
      SAXException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    exportDoc.write(out);
    out.close();

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new ByteArrayInputStream(out.toByteArray()));
  }

  public static String transformToString(Node node) {
    // create string from xml tree
    StringWriter sw = new StringWriter();
    TransformerFactory transfac = TransformerFactory.newInstance();
    Transformer trans;
    try {
      trans = transfac.newTransformer();
      trans.setOutputProperty(OutputKeys.INDENT, "yes");
      StreamResult result = new StreamResult(sw);
      DOMSource source = new DOMSource(node);
      trans.transform(source, result);
    } catch (TransformerConfigurationException e) {
      LOGGER.error(e);
    } catch (TransformerException e) {
      LOGGER.error(e);
    }

    return sw.toString();
  }

  public static void writeVisio(InputStream stream) {
    if (EXPORT_FOLDER == null) {
      return;
    }    
    File exportDir = new File(EXPORT_FOLDER);
    exportDir.mkdirs();
    File outFile = new File(exportDir, "temp.vdx");
    FileOutputStream out;
    try {
      out = new FileOutputStream(outFile);
      int size = 2048;
      byte[] buffer = new byte[size];
      int length = stream.read(buffer);
      while (length != -1) {
        out.write(buffer, 0, length);
        out.flush();
        length = stream.read(buffer);
      }
      out.close();
    } catch (FileNotFoundException e) {
      LOGGER.error(e);
    } catch (IOException e) {
      LOGGER.error(e);
    }
  }

  public static void writeToFile(de.iteratec.visio.model.Document document) {
    RuntimeException re = new RuntimeException();//NOPMD - needed to get caller
    StackTraceElement caller = re.getStackTrace()[1];
    String className = caller.getClassName();
    String fileName = className.substring(className.lastIndexOf('.') + 1) + "_" + caller.getMethodName() + ".vdx";
    writeToFile(document, fileName);
  }

  public static void writeToFile(de.iteratec.visio.model.Document document, String fileName) {
    if (EXPORT_FOLDER == null) {
      return;
    }
    
    File exportDir = new File(EXPORT_FOLDER);
    exportDir.mkdirs();
    File outFile = new File(exportDir, fileName);
    try {
      document.save(outFile);
    } catch (IOException e) {
      LOGGER.error(e);
    }
  }
}
