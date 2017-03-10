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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff.message.AssignAttributeTypeMessage;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.attribute.AttributeType;


/**
 *Implementation class of the command pattern {@link AtsApplicableChange} to associate a {@link BuildingBlockType} with an {@link AttributeType}
 */
public class AssociateAttributeTypeWithTypeOfBuildingBlockChange implements AtsApplicableChange {

  private final String      atName;
  private BuildingBlockType buildingBlockType;

  public AssociateAttributeTypeWithTypeOfBuildingBlockChange(String atName, BuildingBlockType buildingBlockType) {
    this.atName = atName;
    this.buildingBlockType = buildingBlockType;
  }

  /**{@inheritDoc}**/
  @Override
  public void writeTo(AttributeTypeService attributeTypeService) {
    AttributeType updatedAT = attributeTypeService.getAttributeTypeByName(atName);
    if (updatedAT != null) {
      //avoid null pointers when an imported AT is ignored, because
      //of being a string 0..* (which is responsibility, which is not done).
      //see also the CreateAttributeTypeChange
      updatedAT.addBuildingBlockTypeTwoWay(buildingBlockType);
      attributeTypeService.saveOrUpdate(updatedAT);
    }
  }

  /**{@inheritDoc}**/
  @Override
  public int hashCode() {
    //31 is prime
    return 31 + (31 + getClass().hashCode() + ((this.atName == null) ? 0 : this.atName.hashCode()))
        * ((this.buildingBlockType == null) ? 1 : this.buildingBlockType.hashCode());
  }

  /**{@inheritDoc}**/
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof AssociateAttributeTypeWithTypeOfBuildingBlockChange)) {
      return false;
    }
    AssociateAttributeTypeWithTypeOfBuildingBlockChange other = (AssociateAttributeTypeWithTypeOfBuildingBlockChange) obj;
    boolean namesAreEqual = this.atName == null ? other.atName == null : this.atName.equals(other.atName);
    boolean bbtsAreEqual = this.buildingBlockType == null ? other.buildingBlockType == null : this.buildingBlockType.equals(other.buildingBlockType);
    return namesAreEqual && bbtsAreEqual;
  }

  /**{@inheritDoc}**/
  @Override
  public Message getMessage() {
    return new AssignAttributeTypeMessage(atName, MessageAccess.getString(buildingBlockType.getName()));
  }
}
