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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.support.RequestContextUtils;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;


/**
 * This interceptor is responsible for setting the currently active Locale to the current UserContext.
 * It extends Spring's {@link org.springframework.web.servlet.i18n.LocaleChangeInterceptor}
 * and adds an update for iteraplan's internal Locale store in {@link UserContext}.
 */
@Service
public class LocaleInterceptor extends LocaleChangeInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws ServletException {

    boolean superSuccess = super.preHandle(req, resp, handler);
    if (!superSuccess) {
      // this won't happen
      return false;
    }

    Locale newLocale = RequestContextUtils.getLocale(req);
    Locale locale = Constants.LOCALES.contains(newLocale.getLanguage()) ? newLocale : (new Locale("en"));

    UserContext userContext = UserContext.getCurrentUserContext();
    if (userContext != null) {
      userContext.setLocale(locale);
    }

    ElasticMiContext elasticMiContext = ElasticMiContext.getCurrentContext();
    if (elasticMiContext != null) {
      elasticMiContext.setLocale(locale);
    }

    // set the Spring Locale to the current Locale (needed for validation Error messages for example
    LocaleContextHolder.setLocale(locale);

    return true;
  }

}