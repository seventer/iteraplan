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
package de.iteratec.iteraplan.presentation.responsegenerators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.svg.model.SvgExportException;


/**
 * A class to summarize the common methods of all the graphics response generators.
 */
public abstract class GraphicsResponseGenerator {

  private static final Logger LOGGER            = Logger.getIteraplanLogger(GraphicsResponseGenerator.class);

  private static final String ERROR_UNKNOWN     = "Unknown";
  private static final String ERROR_UNSUPPORTED = "Unsupported";
  private static final String TEMPFILE_PREFIX   = "iteraplanDiagram";
  private static final String FILE_SUFFIX       = ".tmp";

  /**
   * Models two types of contents, i.e. either attachment or inline.
   * 
   * @author est
   */
  public static enum Content {

    ATTACH("attachment; "), INLINE("inline; "), UNKNOWN("");

    private String              typeOfContent;
    private static final String END_LINE = "; ";

    private Content(String typeOfContent) {
      this.typeOfContent = typeOfContent;
    }

    public String getValue() {
      return this.typeOfContent;
    }

    public static Content fromValue(String value) {

      String normalizedValue = value;

      /* normalize content string */
      if (!value.endsWith(END_LINE)) {
        normalizedValue += END_LINE;
      }

      for (Content content : Content.values()) {
        if (content.getValue().equals(normalizedValue)) {
          return content;
        }
      }
      return UNKNOWN;
    }

  }

  /**
   * TODO Should this really be called ResultFormat? ResultFormat seems to be used for different
   * stuff at other locations, i.e. {@link de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TabularOptionsBean#getResultFormat}
   */
  public static enum ResultFormat {
    VISIO("Visio", Constants.REPORTS_EXPORT_GRAPHICAL_VISIO, VisioResponseGenerator.class), SVG("Svg", Constants.REPORTS_EXPORT_GRAPHICAL_SVG,
        SVGResponseGenerator.class), PDF("Pdf", Constants.REPORTS_EXPORT_GRAPHICAL_PDF, PDFResponseGenerator.class), PNG("Png",
        Constants.REPORTS_EXPORT_GRAPHICAL_PNG, PNGResponseGenerator.class), JPEG("Jpeg", Constants.REPORTS_EXPORT_GRAPHICAL_JPEG,
        JPEGResponseGenerator.class), UNKNOWN("", "", null);

    private String                                     format;
    private String                                     graphicFormat;
    private Class<? extends GraphicsResponseGenerator> clazz;

    private ResultFormat(String format, String graphicFormat, Class<? extends GraphicsResponseGenerator> clazz) {
      this.format = format;
      this.graphicFormat = graphicFormat;
      this.clazz = clazz;
    }

    /**
     * @return the simple string representation of the result format (i.e. Png)
     */
    public String getFormat() {
      return this.format;
    }

    /**
     * @return one of the result formats defined in the Constants (i.e.
     *         Constants.REPORTS_EXPORT_GRAPHICAL_PNG) or empty string if unknown
     */
    public String getGraphicFormat() {
      return this.graphicFormat;
    }

    public Class<? extends GraphicsResponseGenerator> getGeneratorClass() {
      return this.clazz;
    }

    public static ResultFormat fromString(String value) {
      for (ResultFormat format : ResultFormat.values()) {
        if (format.getFormat().equalsIgnoreCase(value)) {
          return format;
        }
      }
      return UNKNOWN;
    }

  }

  /**
   * Models the type of graphical report.
   * 
   * @author est
   */
  public static enum GraphicalReport {
    PORTFOLIO("PortfolioDiagram", ReportType.PORTFOLIO.getValue()),

    LANDSCAPE("LandscapeDiagram", ReportType.LANDSCAPE.getValue()),

    MASTERPLAN("MasterplanDiagram", ReportType.MASTERPLAN.getValue()),

    CLUSTER("ClusterDiagram", ReportType.CLUSTER.getValue()),

    INFORMATIONFLOW("InformationFlowDiagram", ReportType.INFORMATIONFLOW.getValue()),

    COMPOSITE("CompositeDiagram", ReportType.COMPOSITE.getValue()),

    PIE("PieDiagram", ReportType.PIE.getValue()),

    BAR("BarDiagram", ReportType.BAR.getValue()),

    LINE("LineDiagram", ReportType.LINE.getValue()),

    VBBCLUSTER("NestingClusterDiagram", ReportType.VBBCLUSTER.getValue()),

    TIMELINE("TimelineDiagram", ReportType.TIMELINE.getValue()),

    MATRIX("MatrixDiagram", ReportType.MATRIX.getValue()),

    NEIGHBORHOOD("NeighborhoodDiagram", ReportType.NEIGHBORHOOD.getValue()),

    UNKNOWN("", "");

    private String              typeOfDiagram;
    private String              val;

    private static final String DIAGRAM = "Diagram";

    private GraphicalReport(String typeOfDiagram, String val) {
      this.typeOfDiagram = typeOfDiagram;
      this.val = val;
    }

    public String getType() {
      return this.typeOfDiagram;
    }

    public String getVal() {
      return this.val;
    }

    public static GraphicalReport fromTypeOrValueString(String typeOrValue) {
      GraphicalReport grep = fromValueString(typeOrValue);
      if (grep == UNKNOWN) {
        grep = fromTypeString(typeOrValue);
      }
      return grep;
    }

    public static GraphicalReport fromValueString(String value) {
      for (GraphicalReport grep : GraphicalReport.values()) {
        if (grep.getVal().equals(value)) {
          return grep;
        }
      }
      return UNKNOWN;
    }

    public static GraphicalReport fromTypeString(String savedQueryType) {

      String normalizedType = savedQueryType;

      /* normalize diagram type string */
      if (!savedQueryType.endsWith(DIAGRAM)) {
        normalizedType += DIAGRAM;
      }
      for (GraphicalReport grep : GraphicalReport.values()) {
        if (grep.getType().equals(normalizedType)) {
          return grep;
        }
      }
      return UNKNOWN;
    }
  }

  protected GraphicsResponseGenerator() {
    super();
  }

  public abstract GraphicsResponseGenerator getInstance();

  /**
   * Extracts the output type from the screen attribute. For details on the screen attribute format
   * see method getScript.
   * 
   * @param screen
   *          the screen specification
   * @return the output type specification or an empty string if no output type specification is
   *         found
   */
  public String getOutputType(String screen) {
    int colonPos = screen.indexOf(':');

    if (colonPos != -1) {
      return screen.substring(colonPos + 1);
    }
    return "";
  }

  /**
   * Transforms a report file into an appropriate HTTP response. This method is designed to work
   * independent from the actual graphics format. This detail is delegated to the abstract method
   * {@link #writeGraphics(GraphicExportBean, OutputStream)}. Child class response generators
   * must implement that method according to the format that they are responsible for. Additionally,
   * child class response generators must implement the abstract methods {@link #getContentType()}
   * and getContentDisposition(String,Content).
   * 
   * This implementation first writes the diagram into a temporary file and then writes that file's
   * contents to the response. This detour is necessary to find out the length of the download and
   * set the Content-Length response header.
   * 
   * @param response
   *          HTTP Response where the report file shall be written to.
   * @param graphicsBean
   *          contains the generated diagram, regardless of the specific file format
   * @param reportType
   *          Specifies which diagram type is downloaded. Influences the suggested download filename
   * @param content
   *          Enum to influence the download's Content-Disposition
   */
  public void generateResponse(HttpServletResponse response, GraphicExportBean graphicsBean, GraphicalReport reportType, Content content) {
    File tempFile = null;
    try {
      // Create a new temporary file
      tempFile = File.createTempFile(TEMPFILE_PREFIX + reportType.getType(), FILE_SUFFIX);

      FileOutputStream tempFileOStream = null;
      try {
        // now write the temporary file
        tempFileOStream = new FileOutputStream(tempFile);
        writeGraphics(graphicsBean, tempFileOStream);
        tempFileOStream.flush();
      } finally {
        IOUtils.closeQuietly(tempFileOStream);
      }

      prepareResponse(response, reportType, content, tempFile.length());
      ServletOutputStream servletOutputStream = response.getOutputStream();

      FileInputStream tempFileInStream = null;
      try {
        // read temp file back in and write it to Servlet response
        tempFileInStream = new FileInputStream(tempFile);
        IOUtils.copy(tempFileInStream, servletOutputStream);
        servletOutputStream.flush();

      } finally {
        IOUtils.closeQuietly(tempFileInStream);
      }

    } catch (Exception e) {
      if (hasBeenAborted(e)) {
        LOGGER.info("The download of the graphics document has been canceled by the user or has been aborted due to a network error.");
      }
      else {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED, e);
      }

    } finally {
      // delete temporary file
      if (tempFile != null && !tempFile.delete()) {
        LOGGER.error("Couldn't delete temporary file {0}", tempFile.toString());
      }
    }
  }

  private void prepareResponse(HttpServletResponse response, GraphicalReport reportType, Content content, long fileLength) {
    response.setContentType(getContentType());
    response.setHeader("Content-disposition", getContentDisposition(reportType, content));

    // ITERAPLAN-1732: add the length of the file to the header of the response to make IE happy
    response.addHeader("Content-Length", Long.toString(fileLength));
  }

  protected final String getContentDisposition(GraphicalReport reportType, Content content) {
    StringBuilder resultBuilder = new StringBuilder();
    resultBuilder.append(content.getValue());

    LOGGER.info("Looking up content disposition for {0} output type '{1}'", getGraphicType(), reportType);

    if (!GraphicalReport.UNKNOWN.equals(reportType)) {
      if (savedQueryTypeSupported(reportType)) {
        resultBuilder.append("filename=iteraplan");
        resultBuilder.append(reportType.getType());
        resultBuilder.append(getFileType());
      } // unsupported graphic for report type (currently visio export for cluster diagram)
      else {
        throwError(ERROR_UNSUPPORTED, reportType);
      }
    } // unknown report type
    else {
      throwError(ERROR_UNKNOWN, reportType);
    }

    String result = resultBuilder.toString();
    LOGGER.info("Returning '{0}'", result);

    return result;
  }

  protected abstract void writeGraphics(GraphicExportBean graphicsBean, OutputStream outputStream) throws IOException, SvgExportException;

  protected abstract boolean savedQueryTypeSupported(GraphicalReport grep);

  protected abstract String getContentType();

  protected abstract String getFileType();

  /**
   * @return the standard filename that should be proposed for a specific kind of graphical export.
   */
  protected String getFileNameProposition() {
    return "iteraplan" + getGraphicType() + "Export" + getFileType();
  }

  /**
   * @return The type of graphic, e.g. Visio, JPEG, PDF, etc. as String. This is derived from the
   *         concrete class's name
   */
  protected String getGraphicType() {
    return this.getClass().getName().replace("ResponseGenerator", "");
  }

  private void throwError(String typeOfError, GraphicalReport reportType) {
    LOGGER.error("{0} {1} output type: {2}", typeOfError, getGraphicType(), reportType);

    throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);

  }

  /**
   * Checks whether the generation of the HTTP response containing the SVG document has been aborted
   * by the user. This method makes the assumption that if the root cause of the given IOException
   * is an instance of TransformerException the operation has been canceled. Of course this
   * assumption might hide other reasons for why this kind of exception might have been thrown, but
   * it's the main reason.
   * <p>
   * There's a problem that makes it cumbersome to reliably detect if the generation of the response
   * has been canceled: The actual exception type at the bottom of the stack of Throwables that make
   * up the given IOException is a java.net.SocketException which in turn is probably wrapped by app
   * server specific exception types. These are then wrapped by exceptions of the various frameworks
   * that are used to write the SVG document to the HTTP response. For example, on Tomcat the stack
   * (from top to bottom) looks like the following:
   * <ul>
   * <li>java.io.IOException</li>
   * <li>javax.xml.transform.TransformerException</li>
   * <li>org.xml.sax.SAXException</li>
   * <li>org.apache.catalina.connector.ClientAbortException</
   * <li>java.net.SocketException</li>
   * </ul>
   * Therefore the above assumption is made for brevity.
   * 
   * @param ex
   *          The IOException that might indicate that the operation has been cancelled.
   * @return {@code true}, if the operation has probably been cancelled, {@code false} otherwise.
   */
  protected boolean hasBeenAborted(Exception ex) {
    Throwable t = ex;
    while ((t.getCause() != null) && !t.getCause().equals(t)) {
      if ((t.getCause() instanceof javax.net.ssl.SSLException) || (t.getCause() instanceof javax.xml.transform.TransformerException)
          || (t.getCause() instanceof java.net.SocketException)) {
        return true;
      }
      t = t.getCause();
    }
    return false;
  }
}
