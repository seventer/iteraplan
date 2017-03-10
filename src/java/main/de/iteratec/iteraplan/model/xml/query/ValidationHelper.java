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
package de.iteratec.iteraplan.model.xml.query;

import java.util.List;


public final class ValidationHelper {

  /** empty private constructor to prevent instantiation */
  private ValidationHelper() {
    // hide constructor
  }

  /**
   * Checks if {@code colorAttributeValues} or {@code selectedColors} isn't set or if they have different sizes
   * @param colorAttributeValues
   *          String-list of attribute values
   * @param selectedColors
   *          List of String-values of colors assigned to the attribute values
   * @return error message according to the performed checks, null if validation was successful
   */
  public static String validateColors(List<String> colorAttributeValues, List<String> selectedColors) {
    return validateAttributeValuesAndSelectedTypeLists(colorAttributeValues, selectedColors, "color");
  }

  /**
   * Checks if {@code lineAttributeValues} or {@code selectedLineTypes} isn't set or if they have different sizes
   * @param lineAttributeValues
   *          String-list of attribute values
   * @param selectedLineTypes
   *          String-list of line types assigned to the attribute values
   * @return error message according to the performed checks, null if validation was successful
   */
  public static String validateLineTypes(List<String> lineAttributeValues, List<String> selectedLineTypes) {
    return validateAttributeValuesAndSelectedTypeLists(lineAttributeValues, selectedLineTypes, "line type");
  }

  private static String validateAttributeValuesAndSelectedTypeLists(List<String> attributeValues, List<String> selectedTypes, String kindOfSelection) {
    if ((attributeValues == null && selectedTypes != null) || attributeValues != null && selectedTypes == null) {
      return "Either the " + kindOfSelection + " attribute values or the selected " + kindOfSelection + "s are not set";
    }
    if (attributeValues != null && selectedTypes != null && attributeValues.size() != selectedTypes.size()) {
      return "The number of " + kindOfSelection + " attributes and " + kindOfSelection + "s do not match. Received " + attributeValues.size()
          + " attribute values and " + selectedTypes.size() + " " + kindOfSelection + "s";
    }
    return null;
  }

}
