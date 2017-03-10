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
package de.iteratec.iteraplan.common.util;

import java.util.regex.Pattern;


/**
 *This class validates the names for the Building Block
 */
public final class StringInputValidator {

  //InformationSystem aren't allowed to have more than one # in the name
  private static final Pattern INFORMATIONSYSTEM_NAME_PATTERN = Pattern.compile("([^#:]*#?[^#:]*)"); //TODO information system name pattern
  //Building Blocks in general aren't allowed to have a colon in the name
  private static final Pattern BUILDING_BLOCK_NAME_PATTERN    = Pattern.compile("([^:]*)");         //TODO building block name pattern

  private StringInputValidator() {
    //prevent instantiation 
  }

  /**
   * 
   * @param input name of the Building Block
   * @return <b>true</b> if the name is valid<br />
   *         <b>false</b> if the name is invalid
   */
  public static boolean isValidBuildingBlockName(String input) {
    return isValid(input, BUILDING_BLOCK_NAME_PATTERN);
  }

  /**
   * 
   * @param input name of the Information System
   * @return <b>true</b> if the name is valid<br />
   *         <b>false</b> if the name is invalid
   */
  public static boolean isValidInformationSystemName(String input) {
    return isValid(input, INFORMATIONSYSTEM_NAME_PATTERN);
  }

  private static boolean isValid(String input, Pattern pattern) {
    if (input != null && !input.isEmpty()) { //A null or empty string can not match a Pattern
      return input.matches(pattern.pattern());
    }
    return false;
  }

}
