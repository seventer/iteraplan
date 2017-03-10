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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * ComponentModelPart that represents a single AttributeTypeGroup.
 */
public class AttributeTypeGroupComponentModelPart implements Serializable {

  private static final long                     serialVersionUID = 7731821771210499638L;

  /** The attribute group to manage in this component model part.  */
  private AttributeTypeGroup                    atg              = null;

  /** List of managed component model parts for the activated attributes of the attribute group. Sorted. */
  private List<AttributeTypeComponentModelPart> atParts          = new ArrayList<AttributeTypeComponentModelPart>();

  private ComponentMode                         componentMode;

  /** true, if the current user has read and write permissions for the attribute type group. */
  private final boolean                         readWritePermitted;

  /** true, if the current user has read permissions for the attribute type group. */
  private final boolean                         readPermitted;

  public AttributeTypeGroupComponentModelPart(AttributeTypeGroup atg, ComponentMode componentMode) {
    this.atg = atg;
    this.componentMode = componentMode;
    readPermitted = UserContext.getCurrentUserContext().getPerms().userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ);
    readWritePermitted = UserContext.getCurrentUserContext().getPerms()
        .userHasAttrTypeGroupPermission(atg, AttributeTypeGroupPermissionEnum.READ_WRITE);
  }

  public void initializeFrom(BuildingBlock source, HashBucketMap<AttributeType, AttributeValue> atToAv) {
    List<AttributeType> attributeTypesAsList = new ArrayList<AttributeType>(atToAv.keySet());
    Collections.sort(attributeTypesAsList);

    // create new AttributeTypeComponentModelPart for each AttributeType
    for (AttributeType at : attributeTypesAsList) {
      ComponentMode atComponentMode = getComponentMode();
      if (at instanceof TimeseriesType && ((TimeseriesType) at).isTimeseries()) {
        // Timeseries attributes can only be edited in dedicated timeseries UI
        atComponentMode = ComponentMode.READ;
      }
      AttributeTypeComponentModelPart atPart = AbstractAttributeTypeComponentModelPartBase.createAttributeTypeComponentModelPart(source,
          atToAv.getBucketNotNull(at), at, atComponentMode);
      atParts.add(atPart);
    }
  }

  public void configure(BuildingBlock target) {
    for (AttributeTypeComponentModelPart atPart : atParts) {
      atPart.configure(target);
    }
  }

  public void update() {
    for (AttributeTypeComponentModelPart atPart : atParts) {
      atPart.update();
    }
  }

  public List<AttributeTypeComponentModelPart> getAtParts() {
    return atParts;
  }

  public AttributeTypeGroup getAtg() {
    return atg;
  }

  public boolean isReadWritePermitted() {
    return readWritePermitted;
  }

  public boolean isReadPermitted() {
    return readPermitted;
  }

  public ComponentMode getComponentMode() {
    return componentMode;
  }

}
