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
package de.iteratec.svg;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.apache.commons.io.IOUtils;
import org.apache.fop.svg.PDFTranscoder;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.svg.model.Document;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.model.impl.SvgDomUtils;


/**
 *
 */
public final class SvgGraphicWriter {

  private static final Logger LOGGER                     = Logger.getIteraplanLogger(SvgGraphicWriter.class);

  private static final String SVG_TEMP_FILE_NAME         = "tempSVGExport";
  private static final String SVG_IDENTIFIER             = "svg";

  /**
   * The maximal area (in pixels) of a transcoded raster image.
   * Corresponds to approx. 18 megapixels.
   */
  private static final long   RASTER_TRANSCODER_MAX_AREA = 18000000;

  /**
   * The step with which the image size is reduced until the image area
   * becomes smaller than the given maximal area.
   */
  private static final long   RASTER_TRANSCODER_STEP     = 100;

  private SvgGraphicWriter() {
    // empty private constructor
  }

  /**
   * Writes this document to the given output stream.
   * 
   * @param graphicsData
   *          Byte array with the diagram data
   * @param out
   *          The OutputStream to write to.
   * @throws IOException
   * @throws SvgExportException
   *           Iff the write operation was unsuccessful.
   */
  public static void writeToSvg(byte[] graphicsData, OutputStream out) throws SvgExportException {
    try {
      out.write(graphicsData);
      out.flush();
    } catch (IOException e) {
      LOGGER.error("I/O Error: Writing to output stream failed.");
      throw new SvgExportException("Unable to write graphics to output stream.", e);
    }
  }

  /**
   * Writes this document to the given output stream.
   * 
   * @param document
   *          SVG-{@link Document} to write
   * @param out
   *          The OutputStream to write to.
   * @throws SvgExportException
   *           Iff the write operation was unsuccessful.
   */
  public static void writeToSvg(Document document, OutputStream out) throws SvgExportException {

    LOGGER.debug("Trying to write the document to the output stram.");

    if (!document.isFinalized()) {

      LOGGER.error("The document has to be finilized before it can be streamed out.");
      throw new SvgExportException("The document cannot be streamed without being finalized.");
    }

    try {
      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      DOMSource source = new DOMSource(document.getDocument());
      StreamResult result = new StreamResult(out);
      transformer.transform(source, result);
    } catch (TransformerException e) {

      LOGGER.error("SVG DOM error: Transformation to output stream failed.");
      throw new SvgExportException("Unable to transform graphics to output stream.", e);
    }

    LOGGER.debug("Successfully written document to the output stream.");

  }

  /**
   * Rasterizes the generated SVG Document to a JGEP and writes in onto the given output stream.
   * 
   * @param document
   *          SVG-{@link Document} to write as JPEG
   * @param outputStream
   *          The stream to write to.
   * @throws SvgExportException
   *           Iff the write operation was unsuccessful.
   */
  public static void writeToJPEG(org.w3c.dom.Document document, OutputStream outputStream) throws SvgExportException {
    JPEGTranscoder t = new JPEGTranscoder();

    LOGGER.debug("Trying to write the document to the output stram as JPEG.");

    // Set the transcoding hints.
    t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1));
    setScaledImageTranscoderDimensions(t, getWidthFromDom(document), getHeightFromDom(document));

    write(document, outputStream, t);

  }

  /**
   * Rasterizes the generated SVG Document to a PNG and writes in onto the given output stream.
   * 
   * @param document
   *          SVG-{@link Document} to write as PNG
   * @param outputStream
   *          The stream to write to.
   * @throws SvgExportException
   *           Iff the write operation was unsuccessful.
   */
  public static void writeToPNG(org.w3c.dom.Document document, OutputStream outputStream) throws SvgExportException {
    PNGTranscoder t = new PNGTranscoder();

    LOGGER.debug("Trying to write the document to the output stram as PNG");

    // Set the transcoding hints.
    t.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.white);
    setScaledImageTranscoderDimensions(t, getWidthFromDom(document), getHeightFromDom(document));

    write(document, outputStream, t);
  }

  /**
   * Converts the generated SVG Document to a PDF and writes in onto the given output stream.
   * 
   * @param document
   *          SVG-{@link Document} to write as PDF
   * @param outputStream
   *          The stream to write to.
   * @throws SvgExportException
   *           Iff the write operation was unsuccessful.
   */
  public static void writeToPDF(org.w3c.dom.Document document, OutputStream outputStream) throws SvgExportException {
    PDFTranscoder t = new PDFTranscoder();

    LOGGER.debug("Trying to write the document to the output stream as PDF.");

    // Set the transcoding hints.
    Double width = getWidthFromDom(document);
    if (width != null) {
      t.addTranscodingHint(PDFTranscoder.KEY_WIDTH, Float.valueOf(width.floatValue()));
    }
    write(document, outputStream, t);
  }

  /**
   * We temporarily write the document to a file and then parse it again to be used for the
   * PDF, JPEG and PDF export. If we don't do this, custom paths are not being transformed from the
   * JPEGTransformer for some reason, which leads to many missing objects on the resulting image.
   * @param document
   *          Svg-{@link Document} to transform
   * @return {@link org.w3c.dom.Document} with the graphic
   */
  public static org.w3c.dom.Document transformToDomDocument(Document document) throws SvgExportException {

    LOGGER.debug("Temporarily saving the document to a local template file.");

    org.w3c.dom.Document resultDoc;

    File tempFile;
    try {
      tempFile = File.createTempFile(SVG_TEMP_FILE_NAME, SVG_IDENTIFIER);
      FileOutputStream fos = null;

      // Write the document to a file
      try {
        fos = new FileOutputStream(tempFile);
        writeToSvg(document, fos);
      } finally {
        IOUtils.closeQuietly(fos);
      }
      resultDoc = parseFromStream(new FileInputStream(tempFile));

    } catch (IOException ioe) {
      LOGGER.error("Failed to write temporary file for graphics transcoding.");
      throw new SvgExportException("Could write temporary files for graphics transformation", ioe);
    }

    try {
      tempFile.delete();

    } catch (SecurityException ex) {
      LOGGER.debug("Temporary file could not be deleted.");
    }
    return resultDoc;
  }

  /**
   * We temporarily write the document to a file and then parse it again to be used for the
   * PDF, JPEG and PDF export. If we don't do this, custom paths are not being transformed from the
   * JPEGTransformer for some reason, which leads to many missing objects on the resulting image.
   * @param diagramData
   *          Byte array with the diagram data
   * @return {@link org.w3c.dom.Document} with the graphic
   */
  public static org.w3c.dom.Document transformToDomDocument(byte[] diagramData) throws SvgExportException {

    return parseFromStream(new ByteArrayInputStream(diagramData));
  }

  private static org.w3c.dom.Document parseFromStream(InputStream in) throws SvgExportException {
    org.w3c.dom.Document resultDoc;

    try {

      // Parse the file back to another document.
      String parser = XMLResourceDescriptor.getXMLParserClassName();
      SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
      String uri = SVG_IDENTIFIER;

      try {
        resultDoc = f.createDocument(uri, in);
      } finally {
        if (in != null) {
          in.close();
        }
      }

    } catch (IOException ioe) {

      LOGGER.error("Failed to read temporary file for graphics transcoding.");
      throw new SvgExportException("Could not read temporary files for graphics transformation", ioe);
    }
    return resultDoc;
  }

  private static void write(org.w3c.dom.Document document, OutputStream outputStream, SVGAbstractTranscoder transcoder) throws SvgExportException {

    LOGGER.debug("Trying to transcode the graphics.");

    TranscoderInput input = new TranscoderInput(document);
    TranscoderOutput output = new TranscoderOutput(outputStream);

    try {
      // Transcode to JPEG.
      transcoder.transcode(input, output);

    } catch (TranscoderException e) {

      LOGGER.error("Failed to transcode the graphic to the result format.");
      throw new SvgExportException("Internal Error: Transcoding the SVG Document to JPEG failed.", getReasonForTranscoderException(e));
    }

    LOGGER.debug("Successfully transcoded the document.");

  }

  private static Throwable getReasonForTranscoderException(TranscoderException e) {

    Throwable t = e;

    while (t instanceof TranscoderException) {
      t = ((TranscoderException) t).getException();
    }
    return t;
  }

  private static Double getWidthFromDom(org.w3c.dom.Document document) {
    if (document == null) {
      return null;
    }

    String widthAttr = SvgDomUtils.getValueForAttribute("width", document.getDocumentElement());
    return (widthAttr == null) ? null : Double.valueOf(widthAttr);
  }

  private static Double getHeightFromDom(org.w3c.dom.Document document) {
    if (document == null) {
      return null;
    }

    String widthAttr = SvgDomUtils.getValueForAttribute("height", document.getDocumentElement());
    return (widthAttr == null) ? null : Double.valueOf(widthAttr);
  }

  /**
   * Scales down the width used to set the size of a resulting raster image export.
   * The width is scaled down so that the product of width and height does not exceed
   * the value given by the RASTER_TRANSCODER_MAX_AREA. This is necessary, so that
   * image transcoding does not throw an exception and no heap space issues occur.
   * 
   * @param width
   *    The original width of the SVG graphic.
   * @param height
   *    The original height of the SVG graphic.
   */
  private static void setScaledImageTranscoderDimensions(ImageTranscoder transcoder, Double width, Double height) {
    if (width != null && height != null) {
      long scaledWidth = width.longValue();
      long scaledHeight = height.longValue();

      if (scaledHeight * scaledWidth > RASTER_TRANSCODER_MAX_AREA) {
        double aspectRatio = (double) scaledHeight / scaledWidth;
        while (scaledHeight * scaledWidth > RASTER_TRANSCODER_MAX_AREA) {
          scaledWidth = scaledWidth - RASTER_TRANSCODER_STEP;
          scaledHeight = (long) Math.ceil(scaledWidth * aspectRatio);
        }
      }

      transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, Float.valueOf(scaledWidth));
      transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, Float.valueOf(scaledHeight));
    }
  }
}
