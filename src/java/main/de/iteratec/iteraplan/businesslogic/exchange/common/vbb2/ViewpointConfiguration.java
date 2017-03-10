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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2;

import java.util.Map;

import com.google.common.collect.ImmutableMap;


/**
 * Composite configuration of a viewpoint's variables, initialized with a Map of String keys and values.
 * 
 * The keys must be not null and not empty. 
 * They can have any format, but a dot (".") is interpreted as a prefix separator.
 * The values must be not null.
 * Null or empty keys or null values lead to undefined behavior. 
 * 
 * If the original Map does not contain a value for a key, this class returns null.
 * 
 * A group of variables with a common prefix, say "p.a", "p.b", "p.c", can be accessed with a prefixed viewpoint configuration
 * withPrefix("p") with the simplified names "a", "b", "c", resp.
 * 
 * Note that a key "p" does not become an empty key "" after prefixing with "p".
 * Request the value of a key with a different prefix or no prefix at all from a prefixed
 * viewpoint configuration will return null. 
 */
public interface ViewpointConfiguration {

  class Root implements ViewpointConfiguration {
    private class Prefixed implements ViewpointConfiguration {
      private final String prefix;

      Prefixed(String prefix) {
        this.prefix = prefix;
      }

      public String get(String key) {
        return vpConfigMap.get(prefix + "." + key);
      }

      public ViewpointConfiguration withPrefix(String newPrefix) {
        return new Prefixed(this.prefix + "." + newPrefix);
      }

    }

    private final ImmutableMap<String, String> vpConfigMap;

    public Root(Map<String, String> vpConfigMap) {
      this.vpConfigMap = ImmutableMap.copyOf(vpConfigMap);
    }

    public String get(String key) {
      return vpConfigMap.get(key);
    }

    public ViewpointConfiguration withPrefix(String prefix) {
      return new Prefixed(prefix);
    }
  }

  ViewpointConfiguration withPrefix(String prefix);

  String get(String key);
}
