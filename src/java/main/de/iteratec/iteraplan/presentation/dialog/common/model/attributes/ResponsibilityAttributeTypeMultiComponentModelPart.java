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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model part for multi value ResponsibilityAT attributes.
 * 
 * @author Abdeltif Klia (iteratec GmbH)
 */
public class ResponsibilityAttributeTypeMultiComponentModelPart extends AbstractAttributeTypeComponentModelPartBase<ResponsibilityAT> {

  /** Serialization version. */
  private static final long            serialVersionUID = 8111779071804873840L;
  private static final Logger          LOGGER           = Logger.getIteraplanLogger(ResponsibilityAttributeTypeMultiComponentModelPart.class);

  /** List of connected attribute values. Is never null. */
  private List<ResponsibilityAV>       connectedAVs     = new ArrayList<ResponsibilityAV>();

  /** List of available attribute values. Is never null. */
  private final List<ResponsibilityAV> availableAVs     = new ArrayList<ResponsibilityAV>();

  /** The id of the (available) attribute value to add. May be null. */
  private Integer                      avIdToAdd;

  /** The id of the (connected) attribute value to remove. May be null. */
  private Integer                      avIdToRemove;

  public ResponsibilityAttributeTypeMultiComponentModelPart(ResponsibilityAT responsibilityAT, Set<ResponsibilityAV> connectedAVs,
      ComponentMode componentMode) {
    super(responsibilityAT, componentMode);
    this.connectedAVs = new ArrayList<ResponsibilityAV>(connectedAVs);
    sort(this.connectedAVs);
  }

  @Override
  public void initializeFrom(BuildingBlock source) {
    super.initializeFrom(source);

    if (getComponentMode() != ComponentMode.READ) {
      Set<Integer> connectedIds = Sets.newHashSet();
      for (ResponsibilityAV responsibilityAV : connectedAVs) {
        connectedIds.add(responsibilityAV.getId());
      }

      Collection<ResponsibilityAV> allAVs = getAttributeType().getAttributeValues();
      for (ResponsibilityAV eav : allAVs) {
        if (!connectedIds.contains(eav.getId())) {
          availableAVs.add(eav);
        }
      }
      sort(availableAVs);
    }
  }

  public void update() {
    LOGGER.debug("update with user-selected ResponsibilityAV value(s).");

    // remove AttributeValue
    if (avIdToRemove != null && avIdToRemove.intValue() > 0) {
      for (ResponsibilityAV connected : connectedAVs) {
        if (avIdToRemove.equals(connected.getId())) {
          connectedAVs.remove(connected);
          availableAVs.add(connected);
          break;
        }
      }
    }

    if (avIdToAdd != null && avIdToAdd.intValue() > 0) {
      for (ResponsibilityAV available : availableAVs) {
        if (avIdToAdd.equals(available.getId())) {
          availableAVs.remove(available);
          connectedAVs.add(available);
          break;
        }
      }
    }

    sort(connectedAVs);
    sort(availableAVs);
    avIdToAdd = null;
    avIdToRemove = null;
  }

  @Override
  public void configure(BuildingBlock target) {
    SpringServiceFactory.getAttributeValueService().setReferenceValues(target, connectedAVs, getAttributeType().getId());
  }

  public List<ResponsibilityAV> getAvailableAVsForPresentation() {
    if (availableAVs.isEmpty()) {
      return availableAVs;
    }
    List<ResponsibilityAV> availableAVsForPresentation = new ArrayList<ResponsibilityAV>(availableAVs);
    availableAVsForPresentation.add(0, new ResponsibilityAV());
    return availableAVsForPresentation;
  }

  public List<ResponsibilityAV> getAvailableAVs() {
    return availableAVs;
  }

  public List<ResponsibilityAV> getConnectedAVs() {
    return connectedAVs;
  }

  public Integer getAvIdToAdd() {
    return avIdToAdd;
  }

  public void setAvIdToAdd(Integer responsibilityAVIdToAdd) {
    this.avIdToAdd = responsibilityAVIdToAdd;
  }

  public Integer getAvIdToRemove() {
    return avIdToRemove;
  }

  public void setAvIdToRemove(Integer responsibilityAVIdToRemove) {
    this.avIdToRemove = responsibilityAVIdToRemove;
  }

  public boolean isAtIsMultiValue() {
    return true;
  }

}