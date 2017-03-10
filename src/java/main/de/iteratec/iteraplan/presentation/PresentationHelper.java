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

import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.NamedId;


/**
 * Contains helper methods for classes in the presentation tier.
 */
public final class PresentationHelper {
  private PresentationHelper() {
    // no instance needed. Only providing static methods
  }

  /**
   * Takes a list of strings and creates {@link NamedId}s out of them. These can be used in combo
   * boxes. <br>
   * <br>
   * The string is placed into the name and description field of each NamedId. 
   * The string is not shortened to a specific length because it must not be 
   * displayed as shortened in the attributeValueDropDown of the
   * Building Blocks and Spreadsheet Reports.
   * 
   * @param strings
   *          A list of Strings
   * @return A list of NamedId.
   */
  public static List<NamedId> convertStringsToNamedIds(List<String> strings) {
    List<NamedId> namedIds = Lists.newArrayList();
    for (String str: strings) {
      NamedId namedId = new NamedId(null, str, str);
      namedIds.add(namedId);
    }
    
    return namedIds;
  }

  /**
   * Tries to parse a string into an Integer. Throws an IteraplanTechnicalException if the string
   * can not be parsed.
   * 
   * @param idString
   *          the String to parse
   * @return -1 if idString is null; the parsed Integer otherwise
   */
  public static Integer parseId(String idString) {
    Integer id = Integer.valueOf(-1);

    if (idString != null) {
      try {
        id = Integer.valueOf(idString);
      } catch (NumberFormatException e) {
        // TODO throw better exception..
        throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND, e);
      }
    }

    return id;
  }

}
