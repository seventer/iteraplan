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
package de.iteratec.iteraplan.presentation.problemreports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.webflow.engine.RequestControlContext;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;


/**
 * Generate ProblemReports for the current Exception.
 * Can be downloaded by iteraplan Admin only. 
 */
public final class IteraplanProblemReport implements Serializable

{
  private static final long   serialVersionUID                            = 8260403931667426796L;

  private static final Logger LOGGER                                      = Logger.getIteraplanLogger(IteraplanProblemReport.class);

  // JSP attributes
  public static final String  JSP_ATTRIBUTE_PROBLEM_REPORT_ENABLED        = "_iteraplan_problem_report_enabled";
  public static final String  JSP_ATTRIBUTE_PROBLEM_REPORT_DISPLAY_IN_GUI = "_iteraplan_problem_report_display_gui";
  public static final String  JSP_ATTRIBUTE_PROBLEM_REPORT_KEY            = "_iteraplan_problem_report_key";
  public static final String  JSP_ATTRIBUTE_PROBLEM_REPORT_PARTS          = "_iteraplan_problem_report_parts";
  private static final String JSP_ATTRIBUTE_STORED_PROBLEM_REPORTS        = "_stored_problem_reports";

  /** Maximal number of reports kept in session scope (LIFO). */
  private static final int    MAX_REPORTS_IN_SESSION                      = 7;

  /** Maximal number of reports kept in application scope (LIFO). */
  private static final int    MAX_REPORTS_IN_APPLICATION                  = 1000;

  /** Unique (in scope) report key. */
  private final String        key                                         = UUID.randomUUID().toString();

  /** Zipped report data. */
  private byte[]              zipBuffer;

  /** The generated download link for this report. */
  private String              downloadLink;

  /** Timestamp of report creation. */
  private long                problemTime;

  private IteraplanProblemReport() {
    // Use factory methods to create a report.
  }

  /** Helper method to get {@link HttpServletRequest} from Flow */
  private static HttpServletRequest getNativeRequest(RequestControlContext ctx) {
    Object nativeRequest = ctx.getExternalContext().getNativeRequest();
    if (nativeRequest instanceof HttpServletRequest) {
      return (HttpServletRequest) nativeRequest;
    }
    return null;
  }

  /** Static factory to use from Flow */
  public static void createFromFlow(Throwable ex, RequestControlContext ctx) {
    createNewProblemReport(ex, getNativeRequest(ctx), ctx.getFlowScope().get("memBean"));
  }

  /** Static factory to use from Controller */
  public static void createFromController(Throwable ex, HttpServletRequest request) {
    createNewProblemReport(ex, request, null);
  }

  private static void createNewProblemReport(Throwable ex, HttpServletRequest request, Object dataObject) {
    if (isProblemReportEnabled()) {
      IteraplanProblemReport problemReport = new IteraplanProblemReport();
      try {
        problemReport.generateReport(ex, request, dataObject);
      } catch (Exception e) {
        LOGGER.warn("Error creating problem report: " + e.getMessage());
      }
    }
  }

  public static boolean isProblemReportEnabled() {

    try {
      return IteraplanProperties.getBooleanProperty(IteraplanProperties.PROP_PROBLEM_REPORTS_ENABLED);
    } catch (Exception e) {
      // Enabled as default, when property is not set
      return true;
    }
  }

  public static boolean isDisplayInGui() {
    try {
      return UserContext.getCurrentPerms().isUserIsAdministrator()
          && IteraplanProperties.getBooleanProperty(IteraplanProperties.PROP_PROBLEM_REPORTS_DISPLAY_IN_GUI);
    } catch (Exception e) {
      // Disabled as default, when property is not set
      return false;
    }
  }

  public void storeInSessionScope(HttpServletRequest request) {
    storeInAttributeContainer(new WebappAttributeContainer(request.getSession()), MAX_REPORTS_IN_SESSION);
  }

  public void storeInApplicationScope(HttpServletRequest request) {
    storeInAttributeContainer(new WebappAttributeContainer(request.getSession().getServletContext()), MAX_REPORTS_IN_APPLICATION);
  }

  public static IteraplanProblemReport readFromSessionScope(String lookupKey, HttpServletRequest request) {
    return readFromAttributeContainer(lookupKey, new WebappAttributeContainer(request.getSession()));
  }

  public static IteraplanProblemReport readFromApplicationScope(String lookupKey, HttpServletRequest request) {
    return readFromAttributeContainer(lookupKey, new WebappAttributeContainer(request.getSession().getServletContext()));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void storeInAttributeContainer(WebappAttributeContainer attributeContainer, int maxSize) {
    Object attributeObject = attributeContainer.getAttribute(JSP_ATTRIBUTE_STORED_PROBLEM_REPORTS);
    Map attribute;
    if (attributeObject instanceof Map) {
      attribute = (Map) attributeObject;
    }
    else {
      attribute = new LimitedLinkedHashMap(maxSize);
      attributeContainer.setAttribute(JSP_ATTRIBUTE_STORED_PROBLEM_REPORTS, attribute);
    }
    attribute.put(getKey(), this);
    LOGGER.debug("Stored problem report " + getKey() + " in memory.");
  }

  @SuppressWarnings("rawtypes")
  private static IteraplanProblemReport readFromAttributeContainer(String lookupKey, WebappAttributeContainer attributeContainer) {
    Object attributeObject = attributeContainer.getAttribute(JSP_ATTRIBUTE_STORED_PROBLEM_REPORTS);
    Map attribute;
    if (attributeObject instanceof Map) {
      attribute = (Map) attributeObject;
      return (IteraplanProblemReport) attribute.get(lookupKey);
    }
    return null;
  }

  private Map<String, ProblemReportPart> generateReportParts(Throwable ex, HttpServletRequest request, Object dataObject) {
    Map<String, ProblemReportPart> reportParts = Maps.newLinkedHashMap();

    // add new parts here
    reportParts.put("stacktrace", StacktraceProblemReportPart.generateStacktraceReport("stacktrace.txt", ex));
    reportParts.put("request", RequestProblemReportPart.generateRequestReport("http-request.txt", request, dataObject));
    reportParts.put("application", ApplicationProblemReportPart.generateApplicationReport("application.txt"));
    reportParts.put("permission", PermissionProblemReportPart.generatePermissionReport("permissions.txt"));
    reportParts.put("environment", EnvironmentProblemReportPart.generateEnvironmentReport("java.txt", request));
    reportParts.put("database", DatabaseProblemReportPart.generateDatabaseReport("database.txt", request));

    return reportParts;
  }

  private void generateReport(Throwable ex, HttpServletRequest request, Object dataObject) {
    this.problemTime = System.currentTimeMillis();

    Map<String, ProblemReportPart> reportParts = generateReportParts(ex, request, dataObject);
    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
    writeAsZip(byteArrayOut, reportParts);
    zipBuffer = byteArrayOut.toByteArray();

    try {
      byteArrayOut.close();
    } catch (IOException e) {
      LOGGER.warn("Can't write problem report data!");
    }

    downloadLink = URLBuilder.getApplicationURL(request) + "/miscellaneous/requestProblemReport.do?reportKey=" + getKey();

    // store values in request scope for JSP
    request.setAttribute(JSP_ATTRIBUTE_PROBLEM_REPORT_ENABLED, "true");
    request.setAttribute(JSP_ATTRIBUTE_PROBLEM_REPORT_PARTS, reportParts);
    request.setAttribute(JSP_ATTRIBUTE_PROBLEM_REPORT_KEY, getKey());
    if (isDisplayInGui()) {
      request.setAttribute(JSP_ATTRIBUTE_PROBLEM_REPORT_DISPLAY_IN_GUI, "true");
    }

    // release resource for garbage collection
    reportParts = null;

    storeInAttributeContainer(new WebappAttributeContainer(request.getSession()), 42);
  }

  public String getMailtoLink() {

    // mail body
    StringBuilder mailBodyBuilder = new StringBuilder(32);
    mailBodyBuilder.append(MessageAccess.getString("problemreport.mail.body.comment"));
    mailBodyBuilder.append("\n\n");
    mailBodyBuilder.append(MessageAccess.getString("problemreport.mail.body.textbeforelink"));
    mailBodyBuilder.append("\n");
    mailBodyBuilder.append(getDownloadLink());
    mailBodyBuilder.append("\n\n");
    mailBodyBuilder.append(MessageAccess.getString("problemreport.mail.body.textafterlink"));

    // mail to
    String adminMail;
    try {
      adminMail = IteraplanProperties.getProperties().getProperty(IteraplanProperties.ADMIN_EMAIL);
    } catch (IteraplanTechnicalException e) {
      adminMail = MessageAccess.getString("problemreport.configure.adminmail");
    }

    // mail subject
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, UserContext.getCurrentLocale());
    String mailSubject = MessageAccess.getString("problemreport.mail.subject") + ":  " + df.format(new Date());

    StringBuilder builder = new StringBuilder(32);
    builder.append("mailto:");
    builder.append(adminMail);
    try {
      builder.append("?subject=");
      builder.append(URLEncoder.encode(mailSubject, "UTF-8"));
      builder.append("&body=");
      builder.append(URLEncoder.encode(mailBodyBuilder.toString(), "UTF-8"));

    } catch (UnsupportedEncodingException e) {
      // should never happen
    }

    return builder.toString().replace("+", "%20");
  }

  public String getDownloadLink() {
    return downloadLink;
  }

  private void writeAsZip(OutputStream out, Map<String, ProblemReportPart> reportParts) {

    if (out == null) {
      return;
    }

    ZipOutputStream zipOutputStream = new ZipOutputStream(out);
    try {
      for (Entry<String, ProblemReportPart> erpEntry : reportParts.entrySet()) {
        ProblemReportPart erp = erpEntry.getValue();
        ZipEntry entry = new ZipEntry(erp.getFilename());
        zipOutputStream.putNextEntry(entry);
        zipOutputStream.write(erp.getByteArray());
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        zipOutputStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public String getKey() {
    return key;
  }

  public int getZipSize() {
    return zipBuffer == null ? 0 : zipBuffer.length;
  }

  public long getProblemTime() {
    return problemTime;
  }

  public byte[] getData() {
    return zipBuffer.clone();
  }

  /**
   * This is a wrapper type for all servlet container's web application types, which can store atrributes in their scope:
   * - {@link ServletRequest}: just for request lifetime
   * - {@link HttpSession}: just for session lifetime
   * - {@link ServletContext}: global for the web application 
   */
  public static class WebappAttributeContainer {

    private final Object attributeContainer;

    public WebappAttributeContainer(ServletRequest request) throws IllegalArgumentException {
      this.attributeContainer = request;
    }

    public WebappAttributeContainer(ServletContext servletContext) throws IllegalArgumentException {
      this.attributeContainer = servletContext;
    }

    public WebappAttributeContainer(HttpSession session) throws IllegalArgumentException {
      this.attributeContainer = session;
    }

    public Object getAttribute(String attrName) {
      if (attributeContainer instanceof ServletRequest) {
        return ((HttpServletRequest) attributeContainer).getAttribute(attrName);
      }
      else if (attributeContainer instanceof ServletContext) {
        return ((ServletContext) attributeContainer).getAttribute(attrName);
      }
      else if (attributeContainer instanceof HttpSession) {
        return ((HttpSession) attributeContainer).getAttribute(attrName);
      }
      return null;
    }

    public void setAttribute(String attrName, Object attrValue) {
      if (attributeContainer instanceof ServletRequest) {
        ((HttpServletRequest) attributeContainer).setAttribute(attrName, attrValue);
      }
      else if (attributeContainer instanceof ServletContext) {
        ((ServletContext) attributeContainer).setAttribute(attrName, attrValue);
      }
      else if (attributeContainer instanceof HttpSession) {
        ((HttpSession) attributeContainer).setAttribute(attrName, attrValue);
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private static class LimitedLinkedHashMap extends LinkedHashMap {

    private static final long serialVersionUID = -2728840236292089906L;

    private final int         maxSize;

    public LimitedLinkedHashMap(int maxSize) {
      this.maxSize = maxSize;
    }

    /**{@inheritDoc}**/
    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
      return size() > maxSize;
    }

  }
}
