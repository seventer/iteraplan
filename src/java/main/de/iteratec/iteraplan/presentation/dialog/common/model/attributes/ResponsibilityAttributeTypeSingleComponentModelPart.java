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
package de.iteratec.iteraplan.presentation.dialog.common.model.attributes;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model part for single value ResponsibilityAT attributes.
 */
public class ResponsibilityAttributeTypeSingleComponentModelPart extends AbstractAttributeTypeComponentModelPartBase<ResponsibilityAT> {

  /** Serialization version. */
  private static final long      serialVersionUID = 2164850175811186693L;

  private static final Logger    LOGGER           = Logger.getIteraplanLogger(ResponsibilityAttributeTypeSingleComponentModelPart.class);

  /** The selected attributeValue. Never null. */
  private ResponsibilityAV       attributeValue   = new ResponsibilityAV();

  /** Sorted list of all possible attribute values. Is never null. */
  private List<ResponsibilityAV> availableAVs     = new ArrayList<ResponsibilityAV>();

  /** The id of the selected attributeValue. May be null. */
  private Integer                avIdToSet        = null;

  public ResponsibilityAttributeTypeSingleComponentModelPart(ResponsibilityAT attributeType, ResponsibilityAV attributeValue,
      ComponentMode componentMode) {
    super(attributeType, componentMode);
    if (attributeValue != null) {
      this.attributeValue = attributeValue;
    }
    avIdToSet = this.attributeValue.getId();
  }

  @Override
  public void initializeFrom(BuildingBlock source) {
    super.initializeFrom(source);

    if (getComponentMode() != ComponentMode.READ) {
      availableAVs = getAttributeType().getSortedAttributeValues();
    }
  }

  public void update() {
    LOGGER.debug("update with user-selected ResponsibilityAV value.");

    if (avIdToSet != null && avIdToSet.intValue() > 0) {
      for (ResponsibilityAV eav : availableAVs) {
        if (eav.getId().equals(avIdToSet)) {
          attributeValue = eav;
          break;
        }
      }
    }
    else {
      attributeValue = new ResponsibilityAV();
    }

    // determine available list
    sort(availableAVs);
    if (attributeValue != null) {
      avIdToSet = attributeValue.getId();
    }
  }

  @Override
  public void configure(BuildingBlock target) {
    SpringServiceFactory.getAttributeValueService().setReferenceValues(target, Lists.newArrayList(attributeValue), getAttributeType().getId());
  }

  public List<ResponsibilityAV> getAvailableAVsForPresentation() {
    if (availableAVs.isEmpty()) {
      return availableAVs;
    }

    List<ResponsibilityAV> availableAVsForPresentation = new ArrayList<ResponsibilityAV>(availableAVs);
    ResponsibilityAV dummy = new ResponsibilityAV();
    availableAVsForPresentation.add(0, dummy);
    return availableAVsForPresentation;
  }

  public boolean isAtIsMultiValue() {
    return false;
  }

  public ResponsibilityAV getAttributeValue() {
    return attributeValue;
  }

  public Integer getAvIdToSet() {
    return avIdToSet;
  }

  public void setAvIdToSet(Integer avIdToSet) {
    this.avIdToSet = avIdToSet;
  }

  public List<ResponsibilityAV> getAvailableAVs() {
    return availableAVs;
  }

}