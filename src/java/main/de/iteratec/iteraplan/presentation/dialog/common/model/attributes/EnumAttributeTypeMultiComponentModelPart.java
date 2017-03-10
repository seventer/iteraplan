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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Component model part for multi value EnumAT attributes.
 */
public class EnumAttributeTypeMultiComponentModelPart extends AbstractAttributeTypeComponentModelPartBase<EnumAT> {

  /** Serialization version. */
  private static final long   serialVersionUID = -6932438496452103752L;

  private static final Logger LOGGER           = Logger.getIteraplanLogger(EnumAttributeTypeMultiComponentModelPart.class);

  /** List of connected attribute values. Is never null. */
  private List<EnumAV>        connectedAVs     = new ArrayList<EnumAV>();

  /** List of available attribute values. Is never null. */
  private final List<EnumAV>  availableAVs     = new ArrayList<EnumAV>();

  /** The id of the (available) attribute value to add. May be null. */
  private Integer             avIdToAdd;

  /** The id of the (connected) attribute value to remove. May be null. */
  private Integer             avIdToRemove;

  public EnumAttributeTypeMultiComponentModelPart(EnumAT enumAT, Set<EnumAV> connectedAVs, ComponentMode componentMode) {
    super(enumAT, componentMode);
    this.connectedAVs = new ArrayList<EnumAV>(connectedAVs);
    sort(this.connectedAVs);
  }

  @Override
  public void initializeFrom(BuildingBlock source) {
    super.initializeFrom(source);

    if (getComponentMode() != ComponentMode.READ) {
      List<EnumAV> allAVs = getAttributeType().getSortedAttributeValues();

      Set<Integer> connectedIds = new HashSet<Integer>();
      for (EnumAV enumAV : connectedAVs) {
        connectedIds.add(enumAV.getId());
      }

      for (EnumAV eav : allAVs) {
        if (!connectedIds.contains(eav.getId())) {
          availableAVs.add(eav);
        }
      }
      sort(this.connectedAVs);
    }
  }

  public void update() {
    LOGGER.debug("update with user-selected EnumAV value(s).");

    // remove AttributeValue
    if (avIdToRemove != null && avIdToRemove.intValue() > 0) {
      for (EnumAV connected : connectedAVs) {
        if (avIdToRemove.equals(connected.getId())) {
          connectedAVs.remove(connected);
          availableAVs.add(connected);
          break;
        }
      }
    }

    // add AttributeValue
    if (avIdToAdd != null && avIdToAdd.intValue() > 0) {
      for (EnumAV available : availableAVs) {
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

  public List<EnumAV> getAvailableAVsForPresentation() {
    if (availableAVs.isEmpty()) {
      return availableAVs;
    }

    List<EnumAV> availableAVsForPresentation = new ArrayList<EnumAV>(availableAVs);
    EnumAV dummy = new EnumAV();
    availableAVsForPresentation.add(0, dummy);

    return availableAVsForPresentation;
  }

  public List<EnumAV> getAvailableAVs() {
    return availableAVs;
  }

  public List<EnumAV> getConnectedAVs() {
    return connectedAVs;
  }

  public List<EnumAV> getAllAVs() {
    List<EnumAV> result = CollectionUtils.arrayList();
    result.addAll(availableAVs);
    result.addAll(connectedAVs);
    sort(result);

    return result;
  }

  public Integer getAvIdToAdd() {
    return avIdToAdd;
  }

  public void setAvIdToAdd(Integer enumAVIdToAdd) {
    this.avIdToAdd = enumAVIdToAdd;
  }

  public Integer getAvIdToRemove() {
    return avIdToRemove;
  }

  public void setAvIdToRemove(Integer enumAVIdToRemove) {
    this.avIdToRemove = enumAVIdToRemove;
  }

  public boolean isAtIsMultiValue() {
    return true;
  }

}
