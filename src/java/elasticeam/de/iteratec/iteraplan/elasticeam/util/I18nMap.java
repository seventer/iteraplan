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
package de.iteratec.iteraplan.elasticeam.util;

import java.util.Locale;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.exception.MetamodelException;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;


/**
 * A utility used to map metamodel elements to their names in different languages and vice-versa.
 *
 * @param <N>
 */
public final class I18nMap<N extends NamedExpression> {
  private final Map<Locale, BiMap<String, N>> valueByLocalizedName;

  public static <N extends NamedExpression> I18nMap<N> create() {
    return new I18nMap<N>();
  }

  private I18nMap() {
    this.valueByLocalizedName = Maps.newHashMap();
  }

  /**
   * Retrieves a metamodel element by its name in a given locale.
   * 
   * @param locale
   *    The locale to search into.
   * @param key
   *    The name of the metamodel element.
   * @return
   *    The metamodel element, or <b>null</b> if it can not be found.
   */
  public N get(Locale locale, String key) {
    if (!this.valueByLocalizedName.containsKey(locale)) {
      return null;
    }
    if (this.valueByLocalizedName.get(locale).containsKey(key)) {
      return this.valueByLocalizedName.get(locale).get(key);
    }
    else {
      return null;
    }
  }

  /**
   * Adds a new metamodel element and name to the map.
   * 
   * @param locale
   *    The locale of the name.
   * @param key
   *    The name.
   * @param value
   *    The metamodel element.
   */
  public void set(Locale locale, String key, N value) {
    if (!this.valueByLocalizedName.containsKey(locale)) {
      BiMap<String, N> localeValues = HashBiMap.create();
      this.valueByLocalizedName.put(locale, localeValues);
    }
    BiMap<String, N> localeValues = this.valueByLocalizedName.get(locale);
    if (localeValues.containsKey(key) && !value.equals(localeValues.get(key))) {
      throw new MetamodelException(ElasticeamException.GENERAL_ERROR, "Duplicate locale name " + key + " in locale " + locale + ".");
    }
    if (key == null) {
      localeValues.inverse().remove(value);
    }
    else {
      if (localeValues.inverse().containsKey(value)) {
        localeValues.inverse().remove(value);
      }
      localeValues.put(key, value);
    }
  }

  /**
   * Removes a metamodel element for the map.
   * @param value
   *    The element to remove.
   */
  public void remove(N value) {
    for (BiMap<String, N> valuesPerLocale : valueByLocalizedName.values()) {
      valuesPerLocale.inverse().remove(value);
    }
  }
}
