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
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.presentation.dialog.ExcelImport.MassdataMemBean;


/**
 * {@link ProblemReportPart} for information about the HTTP request. 
 */
final class RequestProblemReportPart extends AbstractProblemReportPart {

  private static final Character    MASK_CHAR      = Character.valueOf('*');
  private static final List<String> MASKED_HEADERS = ImmutableList.of("cookie");

  private RequestProblemReportPart(String filename) {
    super(filename);
  }

  @SuppressWarnings("rawtypes")
  static ProblemReportPart generateRequestReport(String filename, HttpServletRequest request, Object dataObject) {
    RequestProblemReportPart reportPart = new RequestProblemReportPart(filename);

    PrintWriter requestWriter = reportPart.getWriter();
    requestWriter.println("REQUEST:");
    requestWriter.println("  Method: " + request.getMethod());
    requestWriter.println("  Protocol: " + request.getProtocol());
    requestWriter.println("  ServerName: " + request.getServerName());
    requestWriter.println("  ServerPort: " + request.getServerPort());
    requestWriter.println("  Scheme: " + request.getScheme());
    requestWriter.println("  RequestURL: " + request.getRequestURL());
    requestWriter.println("  RequestURI: " + request.getRequestURI());
    requestWriter.println("  ContextPath: " + request.getContextPath());
    requestWriter.println("  ServletPath: " + request.getServletPath());
    requestWriter.println("  PathInfo: " + request.getPathInfo());
    requestWriter.println("  PathTranslated: " + request.getPathTranslated());
    requestWriter.println("  QueryString: " + request.getQueryString());
    requestWriter.println("  ContentType: " + request.getContentType());
    requestWriter.println("  ContentLength: " + request.getContentLength());
    requestWriter.println("  CharacterEncoding: " + request.getCharacterEncoding());
    requestWriter.println("  Locale: " + request.getLocale());
    requestWriter.println("-");

    // Special information for imports/exports
    if (dataObject instanceof MassdataMemBean) {
      MassdataMemBean memBean = (MassdataMemBean) dataObject;
      requestWriter.println("IMPORT/EXPORT:");
      requestWriter.println("  FileName: " + memBean.getFileName());
      try {
        requestWriter.println("  File size: " + memBean.getFileContent().length);
      } catch (RuntimeException re) {
        // nothing
      }
      requestWriter.println("  ImportMetamodel? : " + memBean.isImportMetamodel());
      requestWriter.println("  ImportStrategy: " + memBean.getImportStrategy());
      requestWriter.println("  ImportType: " + memBean.getImportType());
      requestWriter.println("  PartialImport? : " + memBean.isPartialImport());
      requestWriter.println("  PartialExport? : " + memBean.isPartialExport());
      requestWriter.println("  FilteredTypeExport: " + memBean.getFilteredTypeExport());
      requestWriter.println("  FilteredTypeName: " + memBean.getFilteredTypeName());
      requestWriter.println("  FilteredTypePersistentName: " + memBean.getFilteredTypePersistentName());
      requestWriter.println("-");
    }

    requestWriter.println("AUTHENTICATION:");
    requestWriter.println("  AuthType: " + request.getAuthType());
    requestWriter.println("  RemoteUser: " + request.getRemoteUser());
    requestWriter.println("  UserPrincipal: " + request.getUserPrincipal().getName());
    requestWriter.println("  RequestedSessionIdFromURL? : " + request.isRequestedSessionIdFromURL());
    requestWriter.println("  RequestedSessionIdFromCookie? : " + request.isRequestedSessionIdFromCookie());
    requestWriter.println("  RequestedSessionIdValid? : " + request.isRequestedSessionIdValid());
    requestWriter.println("  Secure? : " + request.isSecure());
    requestWriter.println("-");

    requestWriter.println("HEADERS:");
    Enumeration headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = (String) headerNames.nextElement();
      Enumeration headers = request.getHeaders(headerName);
      while (headers.hasMoreElements()) {
        String headerValue = (String) headers.nextElement();
        if (Iterables.contains(MASKED_HEADERS, headerName.toLowerCase())) {
          headerValue = mask(headerValue);
        }
        requestWriter.println("  " + headerName + ": " + headerValue);
      }
    }
    requestWriter.println("-");

    requestWriter.println("PARAMS:");
    Enumeration parameterNames = request.getParameterNames();
    while (parameterNames.hasMoreElements()) {
      String paramName = (String) parameterNames.nextElement();
      List<String> maskedValues = Lists.newArrayList();
      for (String value : request.getParameterValues(paramName)) {
        maskedValues.add(mask(value));
      }
      requestWriter.println("  " + paramName + ": " + Joiner.on(",").join(maskedValues));
    }
    requestWriter.println("-");

    requestWriter.println("COOKIES:");
    for (Cookie cookie : request.getCookies()) {
      requestWriter.println("  " + cookie.getName() + ": " + mask(cookie.getValue()));
    }

    requestWriter.close();
    return reportPart;
  }

  private static String mask(String s) {
    if (s == null) {
      return "null";
    }
    else {
      return Strings.repeat(MASK_CHAR.toString(), s.length());
    }
  }

  /**{@inheritDoc}**/
  @Override
  public String getReportPartIdentifier() {
    return "request";
  }
}
