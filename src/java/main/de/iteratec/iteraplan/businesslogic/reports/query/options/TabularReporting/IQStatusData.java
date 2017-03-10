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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.validation.Errors;


/**
 * This interface provides methods to deal with the GUI data from a textual query regarding the
 * status of a queried type of building block. It should be implemented by JSP backing beans that
 * hold the GUI data. It is designed to hold that data in an instance of {@code Map}.
 */
public interface IQStatusData extends Serializable {

  /**
   * Returns a map of the actual implementation of a status for a particular type of building block
   * as the key and the value {@code true} or {@code false}, depending on if the status has been
   * selected by the user to be party of the query.
   * 
   * @return {@code Map}.
   */
  Map<?, Boolean> getStatusMap();

  /**
   * Returns the boolean value of the specified status as specified on the GUI.
   * <p>
   * The status is passed as the string that is used to display it on the GUI, because this method
   * is called by JSP pages. It is up to the implementation to convert it into the actual
   * implementation of the appropriate status and store in the map along with its value.
   * 
   * @param key
   *          The status as a string.
   * @return The value of the status as specified on the GUI.
   */
  Boolean getStatus(String key);

  /**
   * Sets the status and its boolean value as specified on the GUI.
   * <p>
   * The status is passed as the string that is used to display it on the GUI, because this method
   * is called by JSP pages. It is up to the implementation to convert it into the actual
   * implementation of the appropriate status and store in the map along with its value.
   * 
   * @param key
   *          The status as a string.
   * @param value
   *          The value of the status as specified on the GUI.
   */
  void setStatus(String key, Boolean value);

  /**
   * Gets a list of Strings that represent the status that are currently selected / enabled. Needed
   * for GUI-access.
   * 
   * @return List with the currently selected status
   */
  List<String> getSelectedStatus();

  /**
   * Sets the given status as selected. Needed for GUI-access.
   * 
   * @param status
   *          list of status to mark as selected
   */
  void setSelectedStatus(List<String> status);

  /**
   * Returns a list of those statuses that have been specified on the GUI to be part of the query.
   * <p>
   * The statuses are returned as the strings that are used to display them on the GUI.
   * 
   * @return {@code List}.
   */
  List<String> exportCheckedStatusList();

  /**
   * Validates the user input.
   */
  void validate(Errors errors);
}
