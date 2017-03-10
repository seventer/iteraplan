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
package de.iteratec.iteraplan.presentation.dialog.common;

import java.util.Map;

import org.springframework.validation.Errors;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.common.model.BuildingBlockComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.timeseries.TimeseriesAttributeComponentModel;


/**
 * Memory Bean base class for all memory beans of building blocks which can have timeseries attributes.
 * Adds timeseries component models for display and edit of timeseries attribute values.
 */
public abstract class CanHaveTimeseriesBaseMemBean<S extends BuildingBlock, T extends BuildingBlockComponentModel<S>> extends BaseMemBean<S, T> {
  private static final long                               serialVersionUID          = 466455022200959252L;

  /** The id of the timeseries attribute which currently is to be edited */
  private Integer                                         currentTimeseriesAttributeId;

  /** {@link TimeseriesAttributeComponentModel}s, mapped by the ID of their attribute type */
  private Map<Integer, TimeseriesAttributeComponentModel> timeseriesComponentModels = Maps.newHashMap();

  /**
   * Returns the component model for timeseries information
   * @return A map of attribute type IDs to {@link TimeseriesAttributeComponentModel}s. Cannot be null.
   */
  public Map<Integer, TimeseriesAttributeComponentModel> getTimeseriesComponentModels() {
    return timeseriesComponentModels;
  }

  /**
   * @return The {@link TimeseriesAttributeComponentModel} belonging to the attribute with the {@link #currentTimeseriesAttributeId}.
   */
  public TimeseriesAttributeComponentModel getCurrentTimeseriesComponentModel() {
    return timeseriesComponentModels.get(currentTimeseriesAttributeId);
  }

  @Override
  public void validateEdit(Errors errors) {
    for (Map.Entry<Integer, TimeseriesAttributeComponentModel> entry : timeseriesComponentModels.entrySet()) {
      errors.pushNestedPath("timeseriesComponentModels[" + entry.getKey() + "]");
      entry.getValue().validate(errors);
      errors.popNestedPath();
    }
  }

  public Integer getCurrentTimeseriesAttributeId() {
    return currentTimeseriesAttributeId;
  }

  public void setCurrentTimeseriesAttributeId(Integer currentTimeseriesAttributeId) {
    this.currentTimeseriesAttributeId = currentTimeseriesAttributeId;
  }
}
