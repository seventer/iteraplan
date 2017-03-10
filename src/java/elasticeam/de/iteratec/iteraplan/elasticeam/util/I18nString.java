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
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;


/**
 * Internationalized String mapping a locale to a locale-specific String.
 */
public class I18nString {

  private static final Locale[] DEFAULTS = new Locale[] { new Locale("en"), new Locale("en", "US"), new Locale("en", "UK") };

  private Map<Locale, String>   strings;

  /**
   * Default constructor.
   */
  public I18nString() {
    this.strings = Maps.newHashMap();
  }

  /**
   * Creates a new instance, which is a copy of the source.
   * @param source
   */
  public I18nString(I18nString source) {
    this();
    for (Entry<Locale, String> entry : source.getStrings()) {
      this.strings.put(entry.getKey(), entry.getValue());
    }
  }

  /**
   * Sets the international term for a given locale.
   * @param locale
   * @param string
   */
  public void set(Locale locale, String string) {
    if (string == null) {
      this.strings.remove(locale);
    }
    else {
      this.strings.put(locale, string);
    }
  }

  /**
   * @param locale
   * @return the internationalized string for the given locale. may be null
   */
  public String get(Locale locale) {
    if (this.strings.containsKey(locale)) {
      return this.strings.get(locale);
    }
    for (Entry<Locale, String> localString : this.strings.entrySet()) {
      if (localString.getKey().getLanguage().equals(locale.getLanguage())) {
        return localString.getValue();
      }
    }
    for (Locale def : DEFAULTS) {
      if (this.strings.containsKey(def)) {
        return this.strings.get(def);
      }
    }
    return "";
  }

  /**
   * @return the set of all locale-specific strings.
   */
  Set<Entry<Locale, String>> getStrings() {
    return this.strings.entrySet();
  }

  /**{@inheritDoc}**/
  @Override
  public String toString() {
    return this.strings.toString();
  }
}