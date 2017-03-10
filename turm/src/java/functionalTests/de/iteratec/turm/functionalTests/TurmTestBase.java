/*
 * iTURM is a User and Roles Management web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.turm.functionalTests;

import java.io.IOException;
import java.net.MalformedURLException;


/**
 * All functional tests for turm should derive from this class. Note that
 * the tests should be run with the system property
 * "de.iteratec.turm.baseURL" set to the URL of the turm version to
 * test (e.g. "http://localhost:8080/iturm"). You can also set the system
 * property "de.iteratec.turm.proxyURL" to give a HTTP proxy to use.
 */
public abstract class TurmTestBase {

  protected final TurmSession session;
  protected int               proxyPort;
  protected String            proxyName;

  public TurmTestBase() {
    String baseURL = System.getProperty("de.iteratec.turm.baseURL");
    if (baseURL == null) {
      throw new IllegalStateException(
          "Can not retrieve property 'de.iteratec.turm.baseURL' to determine server.");
    }
    String proxyURL = System.getProperty("de.iteratec.turm.proxyURL");
    if (proxyURL == null) {
      try {
        this.session = new TurmSession(baseURL);
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException("Failed to configure base URL", e);
      }
    }
    else {
      if (proxyURL.startsWith("http://")) {
        proxyURL = proxyURL.substring(7);
      }
      this.proxyPort = 8080;
      int indexOfColon = proxyURL.indexOf(':');
      if (indexOfColon != -1) {
        proxyPort = Integer.parseInt(proxyURL.substring(indexOfColon + 1));
        this.proxyName = proxyURL.substring(0, indexOfColon);
      }
      try {
        this.session = new TurmSession(baseURL, this.proxyName, this.proxyPort);
      } catch (MalformedURLException e) {
        throw new IllegalArgumentException("Failed to configure base URL", e);
      }
    }
    AlltrustingTrustProvider.install();
  }

  protected void login() throws MalformedURLException, IOException {
    this.session.goToStartPage();
    this.session.setTextFieldByName(Constants.LOGIN_FORM, Constants.LOGIN_FORM_LOGINFIELD, Constants.DEFAULT_USER);
    this.session.setTextFieldByName(Constants.LOGIN_FORM, Constants.LOGIN_FORM_PASSWORDFIELD, Constants.DEFAULT_PASSWORD);
    this.session.clickButtonByName(Constants.LOGIN_FORM, "login_button");
  }

  protected void logout() throws IOException {
    this.session.clickElementById(Constants.LOGOUT_LINK);
  }

}
