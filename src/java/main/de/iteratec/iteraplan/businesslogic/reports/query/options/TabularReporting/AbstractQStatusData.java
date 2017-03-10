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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.validation.Errors;


/**
 * This class implements the {@code IQStatusData} interface. It expects the status 
 * of a {@code BuildingBlock} to be implemented as an Enum.
 * 
 * The class provides a default implementation of the getStatusMap(),
 * exportCheckedStatusList() and {@code validate()}-Methods.
 */
public abstract class AbstractQStatusData<E extends Enum<E>> implements IQStatusData, Serializable {
  private static final long serialVersionUID = 4058495398942776571L;

  /**
   * This map maps each possible status of a {@code BuildingBlock} to a boolean 
   * value, depending on if the status has been selected by the user to be part 
   * of the query.
   */
  private Map<E, Boolean>   statusMap;

  public Map<E, Boolean> getStatusMap() {
    return statusMap;
  }

  public List<String> exportCheckedStatusList() {
    List<String> list = new ArrayList<String>();
    for (Map.Entry<?, Boolean> entry : statusMap.entrySet()) {
      if (entry.getValue().equals(Boolean.TRUE)) {
        list.add(entry.getKey().toString());
      }
    }
    return list;
  }

  public void validate(Errors errors) {
    for (Map.Entry<?, Boolean> entry : statusMap.entrySet()) {
      if (entry.getValue().equals(Boolean.TRUE)) {
        return;
      }
    }
    errors.reject("errors.query.noStatusSelected");
  }

  protected void setStatusMap(Map<E, Boolean> map) {
    this.statusMap = map;
  }
}
