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

import org.springframework.validation.Errors;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.AbstractComponentModelBase;


/**
 * ComponentModel for AttributeValueAssignments, AttributeValues, AttributeTypes and
 * AttributeTypeGroups. Contrary to the business model, the ComponentModel for attributes is
 * structured in the following way:
 * <ol>
 * <li>AttributeTypeGroups</li>
 * <li>AttributeTypes</li>
 * <li>AttributeValues</li>
 * </ol>
 * Only attribute types that are activated for the building block are considered. Only attribute
 * type groups for which the user has read permissions are considered.
 * <p>
 * To use this abstract class you must implement the showATG(AttributeTypeGroup atg) method. This
 * method decides whether a given AttributeTypeGroup should be managed by this component model or
 * not. This is used to split the ATGs between the normal and the toplevelAttributeComponentModels.
 * </p>
 */
public abstract class AttributesComponentModel extends AbstractComponentModelBase<BuildingBlock> {

  private static final long                                serialVersionUID = -5785161883955118147L;
  /** List of AttributeTypeGroupComponentModelPart. Sorted. */
  private final List<AttributeTypeGroupComponentModelPart> atgParts         = new ArrayList<AttributeTypeGroupComponentModelPart>();

  /**
   * @param componentMode
   * @param htmlId
   */
  public AttributesComponentModel(ComponentMode componentMode, String htmlId) {
    super(componentMode, htmlId);
  }

  public void initializeFrom(BuildingBlock buildingBlock) {
    // first, build up the mapping AttributeType -> AttributeValues
    Multimap<AttributeType, AttributeValue> atToAv = ArrayListMultimap.create();
    for (AttributeValueAssignment ava : buildingBlock.getAttributeValueAssignments()) {
      AttributeValue av = ava.getAttributeValue();
      AttributeType at = av.getAbstractAttributeType();
      atToAv.put(at, av);
    }

    // then, build up the mapping AttributeTypeGroup -> AttributeTypes
    // and build map from IDs to AttributeTypes in parallel
    SetMultimap<AttributeTypeGroup, AttributeType> atgToAt = TreeMultimap.create();
    BuildingBlockType buildingBlockType = buildingBlock.getBuildingBlockType();

    for (AttributeType at : buildingBlockType.getAttributeTypes()) {
      AttributeTypeGroup atg = at.getAttributeTypeGroup();
      if (UserContext.getCurrentUserContext().getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ) && showATG(atg)) {
        atgToAt.put(atg, at);
      }
    }

    // initialize AttributeTypeGroupComponentModelParts
    for (AttributeTypeGroup atg : atgToAt.keySet()) {
      HashBucketMap<AttributeType, AttributeValue> atInGroupToAv = new HashBucketMap<AttributeType, AttributeValue>();
      for (AttributeType at : atgToAt.get(atg)) {
        Collection<AttributeValue> values = atToAv.get(at);
        atInGroupToAv.addAll(at, values);
      }
      
      AttributeTypeGroupComponentModelPart atgPart = new AttributeTypeGroupComponentModelPart(atg, getComponentMode());
      atgPart.initializeFrom(buildingBlock, atInGroupToAv);
      atgParts.add(atgPart);
    } 
  }

  public void configure(BuildingBlock target) {
    for (AttributeTypeGroupComponentModelPart atgPart : atgParts) {
      atgPart.configure(target);
    }
  }

  public void update() {
    for (AttributeTypeGroupComponentModelPart atgPart : atgParts) {
      atgPart.update();
    }
  }

  public List<AttributeTypeGroupComponentModelPart> getAtgParts() {
    return atgParts;
  }

  /**
   * Checks if the ATG should be contained within this AttributesComponentModel or not
   * 
   * @param atg
   * @return true if this ATG should be contained in this AttributesComponentModel, false otherwise
   */
  public abstract boolean showATG(AttributeTypeGroup atg);

  public void validate(Errors errors) {
    // do nothing
  }

}
