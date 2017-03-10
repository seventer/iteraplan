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
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.FileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.presentation.GuiContext;


/**
 * {@link ProblemReportPart} for information about the iteraplan application itself.
 */
final class ApplicationProblemReportPart extends AbstractProblemReportPart {

  private static final List<String> EXCLUDED_PROPERTIES = ImmutableList.of("notification.smtpserver", "notification.port", "notification.email.from",
                                                            "notification.ssl.enable", "notification.starttls.enable", "notification.username",
                                                            "notification.password", "timeseries.enabled", "ldap.url1", "ldap.url2", "ldap.url3",
                                                            "ldap.url4", "ldap.base", "ldap.userDn", "ldap.password", "ldap.users.searchfilter",
                                                            "ldap.users.searchbase", "ldap.roles.membershipfilter", "ldap.roles.searchbase",
                                                            "ldap.roles.nameprefix", "ldap.roles.defaultrole", "urlbuilder.application.address",
                                                            "database.url", "database.login", "database.password");

  private ApplicationProblemReportPart(String filename) {
    super(filename);
  }

  static ApplicationProblemReportPart generateApplicationReport(String filename) {
    ApplicationProblemReportPart reportPart = new ApplicationProblemReportPart(filename);

    GuiContext currentGuiContext = GuiContext.getCurrentGuiContext();
    IteraplanProperties properties = IteraplanProperties.getProperties();

    PrintWriter appWriter = reportPart.getWriter();
    appWriter.println("Active Dialog: " + currentGuiContext.getActiveDialogName());
    appWriter.println("-");

    Collection<Object> allPropertyKeys = properties.getAllPropertyKeys();
    List<String> sortedPropertyKeys = Lists.newArrayList();

    for (Object key : allPropertyKeys) {
      if (!Iterables.contains(EXCLUDED_PROPERTIES, key)) {
        sortedPropertyKeys.add(String.valueOf(key));
      }
    }

    Collections.sort(sortedPropertyKeys);

    appWriter.println("APPLICATION PROPERTIES:");
    for (String key : sortedPropertyKeys) {
      if (!Iterables.contains(EXCLUDED_PROPERTIES, key)) {
        appWriter.println("  " + Strings.padEnd(key, 64, PAD_CHAR) + "=" + PAD_CHAR + properties.getProperty(String.valueOf(key)));
      }
    }
    appWriter.println("-");

    appWriter.println("LOGGER/ APPENDER:");
    @SuppressWarnings("rawtypes")
    Enumeration currentLoggers = LogManager.getCurrentLoggers();
    while (currentLoggers.hasMoreElements()) {
      Object loggerElement = currentLoggers.nextElement();
      if (loggerElement instanceof Logger) {
        Logger logger = (Logger) loggerElement;
        String loggerName = logger.getName();

        @SuppressWarnings("rawtypes")
        Enumeration allAppenders = logger.getAllAppenders();
        while (allAppenders.hasMoreElements()) {
          Object appenderElement = allAppenders.nextElement();
          if (appenderElement instanceof FileAppender) {
            FileAppender fileAppender = (FileAppender) appenderElement;
            appWriter.println("Appender: " + fileAppender.getName() + " for Logger: " + loggerName + " with Logfile: " + fileAppender.getFile());
          }
        }
      }
    }

    appWriter.close();
    return reportPart;
  }

  /**{@inheritDoc}**/
  @Override
  public String getReportPartIdentifier() {
    return "application";
  }

}
