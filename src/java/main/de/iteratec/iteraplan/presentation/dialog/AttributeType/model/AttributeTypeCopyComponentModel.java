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
package de.iteratec.iteraplan.presentation.dialog.AttributeType.model;

import java.util.HashSet;
import java.util.Set;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.NullSafeModel;


/**
 * GUI model for copying {@link AttributeType}s.
 */
public class AttributeTypeCopyComponentModel extends AttributeTypeComponentModel {

  /** Serialization version. */
  private static final long serialVersionUID                 = 6209216313304407748L;

  private AttributeType     attributeToCopy;

  private boolean           copyAttributeValues              = true;
  private boolean           copyAssociatedBuildingBlockTypes = true;

  public AttributeTypeCopyComponentModel() {
    super(ComponentMode.CREATE);
  }

  @Override
  public void initializeFrom(AttributeType attributeType) {
    Preconditions.checkNotNull(attributeType);

    this.attributeToCopy = attributeType;

    for (ComponentModel<AttributeType> model : getBaseModels()) {
      model.initializeFrom(attributeType);
    }
    getNameModel().setName("");
    getBuildingBlockTypeModel().initializeFrom(attributeType);

    if (getTypeOfAttribute() == null) {
      initTypeOfAttribute(attributeType);
    }
    getAttributeTypeSpecializationComponentModel().initializeFrom(attributeType);
  }

  @Override
  protected void initTypeOfAttribute(AttributeType attributeType) {
    setTypeOfAttribute(attributeType.getTypeOfAttribute());
    if (getTypeOfAttribute() == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    switch (getTypeOfAttribute()) {
      case DATE:
        setAttributeTypeSpecializationComponentModel(new NullSafeModel<AttributeType>());
        break;
      case ENUM:
        setAttributeTypeSpecializationComponentModel(new EnumAttributeTypeCopyComponentModel(getComponentMode()));
        break;
      case NUMBER:
        setAttributeTypeSpecializationComponentModel(new NumberAttributeTypeComponentModel(getComponentMode()));
        break;
      case RESPONSIBILITY:
        setAttributeTypeSpecializationComponentModel(new ResponsibilityAttributeTypeComponentModel(getComponentMode()));
        break;
      case TEXT:
        setAttributeTypeSpecializationComponentModel(new TextAttributeTypeComponentModel(getComponentMode()));
        break;
      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  @Override
  public void configure(AttributeType attributeType) {
    for (ComponentModel<AttributeType> model : getBaseModels()) {
      model.configure(attributeType);
    }
    if (copyAttributeValues) {
      Preconditions.checkNotNull(getAttributeTypeSpecializationComponentModel());
      getAttributeTypeSpecializationComponentModel().configure(attributeType);
    }

    if (!copyAssociatedBuildingBlockTypes) {
      Set<BuildingBlockType> bbts = new HashSet<BuildingBlockType>();
      attributeType.setBuildingBlockTypes(bbts);
    }
  }

  public AttributeType getAttributeToCopy() {
    return attributeToCopy;
  }

  public boolean isCopyAttributeValues() {
    return copyAttributeValues;
  }

  public void setCopyAttributeValues(boolean copyAttributeValues) {
    this.copyAttributeValues = copyAttributeValues;
  }

  public boolean isCopyAssociatedBuildingBlockTypes() {
    return copyAssociatedBuildingBlockTypes;
  }

  public void setCopyAssociatedBuildingBlockTypes(boolean copyAssociatedBuildingBlockTypes) {
    this.copyAssociatedBuildingBlockTypes = copyAssociatedBuildingBlockTypes;
  }

}
