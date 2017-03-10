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
package de.iteratec.iteraplan.elasticeam.metamodel;

import java.util.Comparator;
import java.util.Map;

import com.google.common.collect.Maps;


/**
 *  This class represents a possible order of universal types in the metamodel. 
 *  In this case the order is hardcoded and represents the order of the EA Data menu from the UI
 */
public final class UniversalTypeExpressionOrderFix implements UniversalTypeExpressionOrder, Comparator<String> {

  private static final long                 serialVersionUID = -585694073449764461L;

  private final static Map<String, Integer> UNIVERSAL_TYPES  = Maps.newHashMap();
  static {
    UNIVERSAL_TYPES.put("BusinessDomain", Integer.valueOf(0));
    UNIVERSAL_TYPES.put("BusinessProcess", Integer.valueOf(1));
    UNIVERSAL_TYPES.put("BusinessUnit", Integer.valueOf(2));
    UNIVERSAL_TYPES.put("Product", Integer.valueOf(3));
    UNIVERSAL_TYPES.put("BusinessFunction", Integer.valueOf(4));
    UNIVERSAL_TYPES.put("BusinessObject", Integer.valueOf(5));
    UNIVERSAL_TYPES.put("BusinessMapping", Integer.valueOf(6));
    UNIVERSAL_TYPES.put("InformationSystemDomain", Integer.valueOf(7));
    UNIVERSAL_TYPES.put("InformationSystem", Integer.valueOf(8));
    UNIVERSAL_TYPES.put("InformationFlow", Integer.valueOf(9));
    UNIVERSAL_TYPES.put("InformationSystemInterface", Integer.valueOf(10));
    UNIVERSAL_TYPES.put("ArchitecturalDomain", Integer.valueOf(11));
    UNIVERSAL_TYPES.put("TechnicalComponent", Integer.valueOf(12));
    UNIVERSAL_TYPES.put("InfrastructureElement", Integer.valueOf(13));
    UNIVERSAL_TYPES.put("Project", Integer.valueOf(14));
  }

  @SuppressWarnings("boxing")
  public int compareNames(String firstName, String secondName) {
    if (UNIVERSAL_TYPES.containsKey(firstName) && !UNIVERSAL_TYPES.containsKey(secondName)) {
      return -1;
    }
    else if (!UNIVERSAL_TYPES.containsKey(firstName) && UNIVERSAL_TYPES.containsKey(secondName)) {
      return 1;
    }
    else if (!UNIVERSAL_TYPES.containsKey(firstName) && !UNIVERSAL_TYPES.containsKey(secondName)) {
      return 0;
    }
    else {
      return (int) Math.signum(UNIVERSAL_TYPES.get(firstName) - UNIVERSAL_TYPES.get(secondName));
    }

  }

  public int compare(String o1, String o2) {
    return compareNames(o1, o2);
  }
}
