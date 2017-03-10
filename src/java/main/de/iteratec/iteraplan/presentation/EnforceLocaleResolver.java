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
package de.iteratec.iteraplan.presentation;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.i18n.AbstractLocaleResolver;


/**
 * The class allows to enforce that a particular Locale is used for the application, regardless of the users' browser preferences. It replaces the
 * previous use of {@link org.springframework.web.servlet.i18n.SessionLocaleResolver}
 * 
 * <p>By default, English is enforced.</p>
 * 
 * @author rbe
 */
public class EnforceLocaleResolver extends AbstractLocaleResolver {

  private Locale enforcedLocale = new Locale("en");

  public Locale resolveLocale(HttpServletRequest request) {
    return enforcedLocale;
  }

  public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    // no action needed for setLocale
  }

  public void setEnforcedLocale(Locale enforcedlocale) {
    this.enforcedLocale = enforcedlocale;
  }

  /**
   * A locale attribute in the user's session in case a custom setting is used
   * More precisely, English is a custom setting for the chosen language
   * useful setting for Date validation in English
   */
  public void setEnforcedLocaleString(String enforcedLocaleString) {
    if (! StringUtils.isEmpty(enforcedLocaleString)) {
      this.enforcedLocale = new Locale(enforcedLocaleString);
    }
  }

}
