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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.model.SealState;


/**
 * A backing bean that that holds the GUI data from a textual query about
 * the seal state of a queried type of building block. 
 */
public class QSealStatus implements Serializable {

  private static final long       serialVersionUID = -971563010506716914L;

  /**
   * This map maps each possible status of a {@code SealState} to a boolean 
   * value, depending on if the status has been selected by the user to be part 
   * of the query.
   */
  private Map<SealState, Boolean> statusMap = initWithDefaultData();

  private static Map<SealState, Boolean> initWithDefaultData() {
    Map<SealState, Boolean> resultMap = Maps.newLinkedHashMap();
    for (SealState status : SealState.values()) {
      resultMap.put(status, Boolean.FALSE);
    }
    
    return resultMap;
  }

  /**
   * Returns the map containing all seal states associated with the boolean values. 
   * Those boolean values represent, if the state is selected or not.
   * 
   * @return the map containing all seal states associated with the boolean values
   */
  public Map<SealState, Boolean> getStatusMap() {
    return statusMap;
  }

  /**
   * Sets the seal status for {@code key}. 
   * 
   * @param key the seal status value, see {@link SealState#getValue()}
   * @param value the flag indicating if the status is selected or not
   */
  public void setStatus(String key, Boolean value) {
    getStatusMap().put(SealState.getByValue(key), value);
  }
  
  /**
   * Returns the list containing the seal status values, which are selected.
   * 
   * @return the list containing the seal status values, which are selected
   */
  public List<String> getSelectedStatus() {
    List<String> selectedStatus = Lists.newArrayList();
    for (SealState element : getStatusMap().keySet()) {
      if (getStatusMap().get(element).equals(Boolean.TRUE)) {
        selectedStatus.add(element.toString());
      }
    }
    
    return selectedStatus;
  }

  /**
   * Sets the selected seal status's.
   * 
   * @param status the selected seal status values
   */
  public void setSelectedStatus(List<String> status) {
    for (SealState element : getStatusMap().keySet()) {
      getStatusMap().put(element, Boolean.FALSE);
    }

    if (status == null) {
      return;
    }

    // set selected elements to true
    for (String statusString : status) {
      setStatus(statusString, Boolean.TRUE);
    }
  }
}
