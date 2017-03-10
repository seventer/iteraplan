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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.iteratec.iteraplan.common.MessageAccess;


/**
 * Utility class designed to inspect String-valued Enums.
 * 
 * @author refer to <a>http://www.hibernate.org/273.html</a>
 */
public final class StringEnumReflectionHelper {

  private StringEnumReflectionHelper() {
    // do not instantiate this class
  }

  /**
   * All Enum constants (instances) declared in the specified class.
   * 
   * @param enumClass
   *          Class to reflect
   * @return Array of all declared Enum constants (instances).
   */
  private static <T extends Enum<T>> T[] getValues(Class<T> enumClass) {
    return enumClass.getEnumConstants();
  }

  /**
   * All possible string values declared in the specified Enum class.
   * 
   * @param enumClass
   *          Class to reflect.
   * @return Array of all possible string values.
   */
  public static <T extends Enum<T>> String[] getStringValues(Class<T> enumClass) {
    T[] values = getValues(enumClass);
    String[] result = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      result[i] = values[i].toString();
    }
    return result;
  }

  /**
   * Returns the name of the Enum instance declared in the given Enum class which holds the
   * specified string value. If the value corresponds to duplicate enum instances the first
   * occurency is returned.
   * 
   * @param enumClass
   *          Class to reflect.
   * @param value
   *          String value.
   * @return Name of the Enum instance.
   */
  public static <T extends Enum<T>> String getNameFromValue(Class<T> enumClass, String value) {
    T[] values = getValues(enumClass);
    for (int i = 0; i < values.length; i++) {
      if (values[i].toString().compareTo(value) == 0) {
        return values[i].name();
      }
    }
    return "";
  }

  /**
   * Returns all key values declared in the specific Enum class and translates the values.
   * 
   * @param enumClass
   *          Class to reflect
   * @param locale
   *          defines the language
   * @return List of all translated keys
   */
  public static <T extends Enum<T>> List<String> getLanguageSpecificEnumValues(Class<T> enumClass, Locale locale) {
    List<String> languageValues = new ArrayList<String>();
    String[] statusList = StringEnumReflectionHelper.getStringValues(enumClass);
    for (String key : statusList) {
      languageValues.add(MessageAccess.getStringOrNull(key, locale));
    }
    return languageValues;
  }

  /**
   * Returns a mapping: Enum -> language specific string
   * 
   * @param enumClass
   *          Class to reflect
   * @param locale
   *          defines the language
   * @return Map with different enum values as keys and the language specific corresponding string
   */
  public static <T extends Enum<T>> Map<T, String> getLanguageSpecificMappingValues(Class<T> enumClass, Locale locale) {
    T[] values = getValues(enumClass);
    Map<T, String> mapping = new HashMap<T, String>();
    for (T value : values) {
      mapping.put(value, MessageAccess.getStringOrNull(value.toString(), locale));
    }
    return mapping;
  }

}