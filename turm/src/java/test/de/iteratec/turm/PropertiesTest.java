/*
 * iTURM is a User and Roles Management web application developed by iteratec, GmbH
 * Copyright (C) 2008 iteratec, GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY ITERATEC, ITERATEC DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.turm;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;


public class PropertiesTest {

  private static final String germanPropertySet  = "/ApplicationResources_de.properties";
  private static final String englishPropertySet = "/ApplicationResources_en.properties";

  @Test
  public void testApplicationResources() throws IOException {
    Set<Object> germanSet = getProperties(germanPropertySet).keySet();
    Set<Object> englishSet = getProperties(englishPropertySet).keySet();

    if (!germanSet.equals(englishSet)) {
      System.out.print("Missing in ApplicationResources_en.properties: ");
      logDifferences(germanSet, englishSet);
      System.out.print("Missing in ApplicationResources_de.properties: ");
      logDifferences(englishSet, germanSet);
      fail();
    }
  }

  private Properties getProperties(String resource) throws IOException {
    Properties properties = new Properties();
    InputStream resourceAsStream = null;
    try {
      resourceAsStream = PropertiesTest.class.getResourceAsStream(resource);
      properties.load(resourceAsStream);
    } finally {
      if (resourceAsStream != null) {
        resourceAsStream.close();
      }
    }
    return properties;
  }

  private void logDifferences(Set<?> first, Set<?> second) {
    Set<Object> firstCopy = new HashSet<Object>(first);
    firstCopy.removeAll(second);
    System.out.println(firstCopy);
  }

  @Test
  public void testDuplicateValues() throws IOException {
    Set<Entry<Object, Object>> germanSet = getProperties(germanPropertySet).entrySet();
    Set<Entry<Object, Object>> englishSet = getProperties(englishPropertySet).entrySet();

    List<String> lst;
    Iterator<Entry<Object, Object>> it;

    for (int i = 0; i < 2; i++) {

      switch (i) {
        case 0: // german
          it = germanSet.iterator();
          break;
        default: // english
          it = englishSet.iterator();
          break;
      }

      HashMap<String, List<String>> dupvalues = new HashMap<String, List<String>>();
      HashMap<String, String> values = new HashMap<String, String>();

      while (it.hasNext()) {
        Entry<Object, Object> ent = it.next();
        String val = (String) ent.getValue();
        if (values.containsKey(val)) {
          boolean exists = dupvalues.containsKey(val);
          if (exists) {
            lst = dupvalues.get(val);
          }
          else {
            lst = new ArrayList<String>();
            lst.add(values.get(val));
          }
          lst.add((String) ent.getKey());
          if (!exists) {
            dupvalues.put(val, lst);
          }
        }
        else {
          values.put(val, (String) ent.getKey());
        }
      }

      if (dupvalues.size() > 0) {
        switch (i) {
          case 0: // german
            System.out
            .println("===============Duplicate values ApplicationResources_de.properties: ====================");
            break;
          default: // english
            System.out
            .println("===============Duplicate values ApplicationResources_en.properties: ====================");
            break;
        }

        Iterator<Entry<String, List<String>>> it2 = dupvalues.entrySet().iterator();
        while (it2.hasNext()) {
          Entry<String, List<String>> ent = it2.next();
          System.out.println("Value: " + ent.getKey());
          lst = (ent.getValue());
          for (int j = 0; j < lst.size(); j++) {
            System.out.println("\t Key: " + lst.get(j));
          }
        }
      }
    }

  }

}