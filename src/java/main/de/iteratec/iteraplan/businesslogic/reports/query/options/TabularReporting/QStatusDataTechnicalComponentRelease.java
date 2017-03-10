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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
import de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus;


/**
 * The backing bean for query data about the status of a {@code TechnicalComponentRelease}.
 */
public class QStatusDataTechnicalComponentRelease extends AbstractQStatusData<TypeOfStatus> {

  /** Serialization version. */
  private static final long serialVersionUID = 5761074596694039886L;

  /**
   * Constructor.
   * <p>
   * Initializes the map declared in {@code AbstractQStatusData} with the statuses that should be
   * displayed and settable on the GUI.
   */
  public QStatusDataTechnicalComponentRelease() {
    setStatusMap(getStatusData());
  }

  private Map<TypeOfStatus, Boolean> getStatusData() {

    Map<TypeOfStatus, Boolean> resultMap = new LinkedHashMap<TypeOfStatus, Boolean>();
    for (TypeOfStatus status : TypeOfStatus.values()) {
      if (status.equals(TypeOfStatus.CURRENT)) {
        resultMap.put(status, Boolean.TRUE);
      }
      else {
        resultMap.put(status, Boolean.FALSE);
      }
    }
    return resultMap;
  }

  public Boolean getStatus(String key) {
    String name = StringEnumReflectionHelper.getNameFromValue(TypeOfStatus.class, key);
    return getStatusMap().get(Enum.valueOf(TypeOfStatus.class, name));
  }

  public void setStatus(String key, Boolean value) {
    String name = StringEnumReflectionHelper.getNameFromValue(TypeOfStatus.class, key);
    getStatusMap().put(Enum.valueOf(TypeOfStatus.class, name), value);
  }

  public List<String> getSelectedStatus() {
    List<String> selectedStatus = new ArrayList<String>();
    for (TypeOfStatus element : getStatusMap().keySet()) {
      if (getStatusMap().get(element).equals(Boolean.TRUE)) {
        selectedStatus.add(element.toString());
      }
    }
    return selectedStatus;
  }

  public void setSelectedStatus(List<String> status) {
    // first set all elements to false
    for (TypeOfStatus element : getStatusMap().keySet()) {
      getStatusMap().put(element, Boolean.FALSE);
    }
    if (status == null) {
      // nothing to do
      return;
    }
    // set selected elements to true
    for (String statusString : status) {
      String name = StringEnumReflectionHelper.getNameFromValue(TypeOfStatus.class, statusString);
      getStatusMap().put(Enum.valueOf(TypeOfStatus.class, name), Boolean.TRUE);
    }
  }
}