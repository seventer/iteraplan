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
package de.iteratec.iteraplan.diffs.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.common.Logger;


/**
 * Used by HistoryBBChangeset Each instance of this contains information on the changes to
 * attributes for a particular attribute group
 * 
 * @author rge
 */

public class HistoryBBAttributeGroupChangeset {

  private static final Logger   LOGGER = Logger.getIteraplanLogger(HistoryBBChangeset.class);

  private String groupName;

  // Maps of AT Name to String[AT_Name, oldValue, newValue]
  private Map<Integer, List<String[]>> mapChangedAttributes;

  public HistoryBBAttributeGroupChangeset(String groupName) {

    this.groupName = groupName;
    this.mapChangedAttributes = new HashMap<Integer, List<String[]>>();
  }

  public void addChangedAttribute(Integer idAT, String nameAT, String oldValue, String newValue) {

    String[] changedAttribute = new String[3];
    changedAttribute[0] = nameAT;
    changedAttribute[1] = oldValue;
    changedAttribute[2] = newValue;
    
    List<String[]> list = null;
    
    if (mapChangedAttributes.containsKey(idAT)) {

      // For now, just prevent two of the same (later, for enum/respon: append?)
      LOGGER.debug(" Already have a value for: " + nameAT + " id=" + idAT);
      list = mapChangedAttributes.get(idAT);
    }
    else {

      list = new ArrayList<String[]>();
      mapChangedAttributes.put(idAT, list);
    }
    
    list.add(changedAttribute);
  }

  public String getGroupName() {

    return groupName;
  }

  /**
   * @return Each String[] contains: 0-Name,1-OldValue,2-NewValue
   */
  public List<String[]> getChangedAttributes() {

    List<String[]> result = new ArrayList<String[]>();
    
    for (List<String[]> list : mapChangedAttributes.values()) {
      result.addAll(list);
    }
    
    return result;
  }

}
