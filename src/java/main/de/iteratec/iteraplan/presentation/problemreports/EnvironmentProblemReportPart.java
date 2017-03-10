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

import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Strings;


/**
 * {@link ProblemReportPart} for information about the enviromment (variables), the JVM properties and the servlet container.
 */
final class EnvironmentProblemReportPart extends AbstractProblemReportPart {

  private EnvironmentProblemReportPart(String filename) {
    super(filename);
  }

  static ProblemReportPart generateEnvironmentReport(String filename, HttpServletRequest request) {
    EnvironmentProblemReportPart reportPart = new EnvironmentProblemReportPart(filename);
    PrintWriter envWriter = reportPart.getWriter();
    ServletContext servletContext = request.getSession().getServletContext();

    SecurityManager securityManager = System.getSecurityManager();
    Runtime runtime = Runtime.getRuntime();
    envWriter.println("Max Memory: " + runtime.maxMemory());
    envWriter.println("Total Memory: " + runtime.totalMemory());
    envWriter.println("Free Memory: " + runtime.freeMemory());
    envWriter.println("Security Manager used?: " + (securityManager == null ? "no" : "yes"));
    envWriter.println("-");

    envWriter.println("Servlet Container Info: " + servletContext.getServerInfo());
    envWriter.println("Servlet API Version: " + servletContext.getMajorVersion() + "." + servletContext.getMinorVersion());
    envWriter.println("-");

    envWriter.println("SERVLET CONTEXT PROPERTIES:");
    @SuppressWarnings("rawtypes")
    Enumeration initParameterNames = servletContext.getInitParameterNames();

    while (initParameterNames.hasMoreElements()) {
      String initParamName = initParameterNames.nextElement().toString();
      String initParamValue = servletContext.getInitParameter(initParamName);
      envWriter.println("  " + Strings.padEnd(initParamName, 50, PAD_CHAR) + ": " + initParamValue);
    }
    envWriter.println("-");

    envWriter.println("ENVIRONMENT PROPERTIES:");
    Map<String, String> env = System.getenv();
    for (Entry<String, String> entry : env.entrySet()) {
      envWriter.println("  " + Strings.padEnd(entry.getKey().toString(), 50, PAD_CHAR) + ": " + entry.getValue());
    }
    envWriter.println("-");

    envWriter.println("SYSTEM PROPERTIES:");
    Properties properties = System.getProperties();
    for (Entry<Object, Object> entry : properties.entrySet()) {
      envWriter.println("  " + Strings.padEnd(entry.getKey().toString(), 50, PAD_CHAR) + ": " + entry.getValue());
    }

    envWriter.close();
    return reportPart;
  }

  /**{@inheritDoc}**/
  @Override
  public String getReportPartIdentifier() {
    return "environment";
  }
}
