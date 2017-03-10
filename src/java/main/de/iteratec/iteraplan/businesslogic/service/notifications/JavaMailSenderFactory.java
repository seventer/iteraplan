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
package de.iteratec.iteraplan.businesslogic.service.notifications;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * A Factory for creating the instances of the {@link JavaMailSenderImpl} class. The returned
 * sender is configured and ready to use.
 */
public class JavaMailSenderFactory {

  private String  host;
  private boolean ssl;
  private boolean startTls;
  private String  port;
  private String  username;
  private String  password;
  private boolean activated;

  private static final Logger LOGGER = Logger.getIteraplanLogger(JavaMailSenderFactory.class);

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public boolean isSsl() {
    return ssl;
  }

  public void setSsl(boolean ssl) {
    this.ssl = ssl;
  }

  public boolean isStartTls() {
    return startTls;
  }

  public void setStartTls(boolean startTls) {
    this.startTls = startTls;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  /**
   * @return the user name if it is not empty, otherwise {@code null} (which is expected by Java Mail for anonymous mail submission)
   */
  public String getUsername() {
    if (StringUtils.isBlank(username)) {
      return null;
    }
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * @return the password iff a user name is configured and the password is not empty or {@code null}
   */
  public String getPassword() {
    if (getUsername() == null) {
      return null;
    }
    if (StringUtils.isBlank(password)) {
      return "";
    }
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public boolean isActivated() {
    return activated;
  }

  public void setActivated(boolean activated) {
    this.activated = activated;
  }

  /**
   * Creates the instance of {@link JavaMailSenderImpl}. The returned sender is configured and
   * ready to use. If the notifications are not activated, {@code null} will be returned.
   * 
   * @return configured instance of {@link JavaMailSenderImpl} or {@code null} if notifications are not activated
   */
  public JavaMailSender create() {
    if (!activated) {
      return null;
    }

    if (StringUtils.isBlank(getHost())) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.NOTIFICATION_CONFIGURATION_INCOMPLETE);
    }

    JavaMailSenderImpl sender = new JavaMailSenderImpl();
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Configuring mail sender framework with hostname " + getHost() + ", port " + getPort() + ". SSL connections enabled: "
          + isSsl() + ", STARTTLS enabled: " + isStartTls() + ", username " + getUsername() + ", password " + (getPassword() != null ? "***" : "null") );
    }
    sender.setHost(getHost());
    sender.setUsername(getUsername());
    sender.setPassword(getPassword());

    if (StringUtils.isNotBlank(getPort())) {
      sender.setPort(Integer.parseInt(getPort()));
    }

    Properties properties = new Properties();
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Turned on mail sender framework's DEBUG logging. It will be written to the console, respectively catalina.out");
      properties.put("mail.debug", "true");
    }

    if (isStartTls()) {
      properties.put("mail.smtp.starttls.enable", "true");
    }

    if (isSsl()) {
      properties.put("mail.smtp.ssl.enable", "true");
    }
    sender.setJavaMailProperties(properties);

    return sender;
  }
}
