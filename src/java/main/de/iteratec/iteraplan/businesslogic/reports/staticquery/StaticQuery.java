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
package de.iteratec.iteraplan.businesslogic.reports.staticquery;

import java.util.Map;


/**
 * The interface that all static queries must implement. It provides the {@link #execute(Map)}
 * method that executes the concrete query based upon a map of parameters and returns the results.
 * Furthermore, it provides the {@link #initializeConfiguration()} method. This method is used to
 * initialize and return all information regarding the configuration of a static query in order to
 * display it correctly in the user interface. The method {@link #getConfiguration()} returns the
 * initialized configuration.
 */
public interface StaticQuery {

  /**
   * Contains and executes the logic of the static query.
   * 
   * @param parameters
   *          A map of parameter names to {@link Parameter}s. The parameters contain the selected
   *          values of a particular static query. This parameter may be null, if the check is not
   *          parameterized.
   * @return An object of type {@link Result} which contains all information about the results of the
   *         static query.
   */
  Result execute(Map<String, Parameter> parameters);

  /**
   * Returns the configuration of the static query.
   * <p>
   * Note that an {@link java.lang.IllegalStateException} is thrown if the configuration has not
   * been initialized before calling this method.
   * 
   * @return An object of type {@link Configuration}.
   */
  Configuration getConfiguration();

  /**
   * Initializes and returns the configuration of the static query.
   * <p>
   * This method should only be called once, that is to say when the static query is initialized for
   * the first time.
   * 
   * @return An object of type {@link Configuration}.
   */
  Configuration initializeConfiguration();
}
