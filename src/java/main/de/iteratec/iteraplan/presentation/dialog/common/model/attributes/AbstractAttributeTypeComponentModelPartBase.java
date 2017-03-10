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

import java.util.HashSet;
import java.util.List;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * Abstract base class for all {@link AttributeTypeComponentModelPart}s.
 */
public abstract class AbstractAttributeTypeComponentModelPartBase<T extends AttributeType> implements AttributeTypeComponentModelPart {

  private static final long serialVersionUID = -7219873181610904685L;
  private final T           attributeType;
  private BuildingBlock     buildingBlock    = null;
  private ComponentMode     componentMode;

  public AbstractAttributeTypeComponentModelPartBase(T attributeType, ComponentMode componentMode) {
    this.attributeType = attributeType;
    this.componentMode = componentMode;
  }

  public void initializeFrom(BuildingBlock source) {
    buildingBlock = source;
  }

  public void configure(BuildingBlock target) {
    // Nothing to do.
  }

  public T getAttributeType() {
    return attributeType;
  }

  public BuildingBlock getBuildingBlock() {
    return buildingBlock;
  }

  /** Sort the contained attribute values. */
  protected <V extends AttributeValue> void sort(List<V> attributeValues) {
    attributeType.getTypeOfAttribute().sort(attributeValues);
  }

  /**
   * Returns the component mode. 
   * 
   * @return the component mode
   */
  public ComponentMode getComponentMode() {
    return componentMode;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static AttributeTypeComponentModelPart createAttributeTypeComponentModelPart(BuildingBlock source, List<AttributeValue> connectedAVs,
                                                                                      AttributeType at, ComponentMode componentMode) {
    AttributeTypeComponentModelPart atPart = null;
    if (at instanceof EnumAT) {
      EnumAT eat = (EnumAT) at;
      if (eat.isMultiassignmenttype()) {
        atPart = new EnumAttributeTypeMultiComponentModelPart(eat, new HashSet(connectedAVs), componentMode);
      }
      else {
        EnumAV eav = (EnumAV) (connectedAVs.isEmpty() ? null : connectedAVs.get(0));
        atPart = new EnumAttributeTypeSingleComponentModelPart(eat, eav, componentMode);
      }
    }
    else if (at instanceof NumberAT) {
      NumberAT nat = (NumberAT) at;
      NumberAV nav = (NumberAV) (connectedAVs.isEmpty() ? null : connectedAVs.get(0));
      atPart = new NumberAttributeTypeComponentModelPart(nat, nav, componentMode);
    }
    else if (at instanceof TextAT) {
      TextAT tat = (TextAT) at;
      TextAV tav = (TextAV) (connectedAVs.isEmpty() ? null : connectedAVs.get(0));
      atPart = new TextAttributeTypeComponentModelPart(tat, tav, componentMode);
    }
    else if (at instanceof DateAT) {
      DateAT dat = (DateAT) at;
      DateAV dav = (DateAV) (connectedAVs.isEmpty() ? null : connectedAVs.get(0));
      atPart = new DateAttributeTypeComponentModelPart(dat, dav, componentMode);
    }
    else if (at instanceof ResponsibilityAT) {
      ResponsibilityAT rat = (ResponsibilityAT) at;
      if (rat.isMultiassignmenttype()) {
        atPart = new ResponsibilityAttributeTypeMultiComponentModelPart(rat, new HashSet(connectedAVs), componentMode);
      }
      else {
        ResponsibilityAV rav = (ResponsibilityAV) (connectedAVs.isEmpty() ? null : connectedAVs.get(0));
        atPart = new ResponsibilityAttributeTypeSingleComponentModelPart(rat, rav, componentMode);
      }
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    atPart.initializeFrom(source);
    return atPart;
  }
}