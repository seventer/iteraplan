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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.base.Optional;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.AttributeTypeViolationMessage;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.VirtualAttributeType;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeGroupService;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


/**
 * Class to find the differences for {@link AttributeType}s in the current Metamodel and the imported one, differences are only additive
 */
public class IteraplanAttributeDiffer {

  private AttributeTypeGroupService attributeTypeGroupService;
  private AttributeTypeService      attributeTypeService;
  private BuildingBlockTypeService  buildingBlockTypeService;
  private MessageListener           messageListener;

  public IteraplanAttributeDiffer(AttributeTypeGroupService attributeTypeGroupService, AttributeTypeService attributeTypeService,
      BuildingBlockTypeService buildingBlockTypeService, MessageListener messageListener) {
    this.attributeTypeGroupService = attributeTypeGroupService;
    this.attributeTypeService = attributeTypeService;
    this.buildingBlockTypeService = buildingBlockTypeService;
    this.messageListener = messageListener;
  }

  public void setAttributeTypeGroupService(AttributeTypeGroupService attributeTypeGroupService) {
    this.attributeTypeGroupService = attributeTypeGroupService;
  }

  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }

  public void setBuildingBlockTypeService(BuildingBlockTypeService buildingBlockTypeService) {
    this.buildingBlockTypeService = buildingBlockTypeService;
  }

  /**
   * Compares the given {@link VirtualAttributeType}s with the existing {@link AttributeType}s and returns a list of executable changes
   * @param virtualAttributeTypes
   * @return list of changes that can be executed
   */
  public List<AtsApplicableChange> findDifferences(List<VirtualAttributeType> virtualAttributeTypes) {
    List<AtsApplicableChange> atChanges = new ArrayList<AtsApplicableChange>();
    for (VirtualAttributeType virtualAttributeType : virtualAttributeTypes) {
      AttributeType existingAT = attributeTypeService.getAttributeTypeByName(virtualAttributeType.getAtName());
      if (existingAT != null) {
        createDiffsForExistingAT(atChanges, virtualAttributeType, existingAT);
      }
      else {
        createDiffsForNewAT(atChanges, virtualAttributeType);
      }
    }
    return atChanges;
  }

  private void createDiffsForNewAT(List<AtsApplicableChange> atChanges, VirtualAttributeType virtualAttributeType) {
    if (isForbiddenAttributeTypeName(virtualAttributeType.getAtName())) {
      messageListener.onMessage(new AttributeTypeViolationMessage(virtualAttributeType.getAtName()));
      return;
    }

    atChanges.add(new CreateAttributeTypeChange(virtualAttributeType, attributeTypeGroupService.getStandardAttributeTypeGroup()));
    addBBTAssocciationChanges(atChanges, virtualAttributeType);
    addEnumLiteralChangesForNewAT(atChanges, virtualAttributeType);
  }

  private void createDiffsForExistingAT(List<AtsApplicableChange> atChanges, VirtualAttributeType virtualAttributeType, AttributeType existingAT) {
    if (isResponsibilityAT(virtualAttributeType, existingAT)) {
      //resp-ATs are represented as string attributes in elasticMi
      virtualAttributeType.setAtType(ResponsibilityAT.class);
    }

    if (isInvalidAT(virtualAttributeType, existingAT)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.GENERAL_IMPORT_ERROR);
      //"Type " + importedAT.getClass().getSimpleName() + " of " + importedAT.getName()
      //+ " does not match existing type" + existingAT.getClass().getSimpleName()            
    }

    addBBTChanges(atChanges, existingAT, virtualAttributeType.getAssociatedToBB());
    if (isEnumAT(virtualAttributeType, existingAT)) {
      addEnumLiteralChangesForExistingAT(atChanges, (EnumAT) existingAT, virtualAttributeType);
    }
  }

  private void addBBTChanges(List<AtsApplicableChange> atChanges, AttributeType existingAT, Set<TypeOfBuildingBlock> typeOfBuildingBlocks) {
    for (TypeOfBuildingBlock tobb : typeOfBuildingBlocks) {
      if (!tobbIsAlreadyAssigned(existingAT, tobb)) {
        BuildingBlockType bbt = buildingBlockTypeService.getBuildingBlockTypeByType(tobb);
        AssociateAttributeTypeWithTypeOfBuildingBlockChange associateDiff = new AssociateAttributeTypeWithTypeOfBuildingBlockChange(
            existingAT.getName(), bbt);
        if (!atChanges.contains(associateDiff)) {
          atChanges.add(associateDiff);
        }
      }
    }
  }

  private void addEnumLiteralChangesForExistingAT(List<AtsApplicableChange> atChanges, EnumAT existingAT, VirtualAttributeType importedAT) {
    for (String literal : importedAT.getEnumAV()) {

      Optional<String> existingLiteral = enumLiteralExists(existingAT, literal);
      if (existingLiteral.isPresent()) {
        if (caseDiffers(existingLiteral.get(), literal)) {
          messageListener.onMessage(new AttributeTypeViolationMessage(existingAT.getName(), literal));
        }
        continue;
      }

      CreateEnumAvChange enumAV = new CreateEnumAvChange(literal, importedAT.getAtName());
      if (atChanges.contains(enumAV)) {
        continue;
      }

      atChanges.add(enumAV);
    }
  }

  private boolean caseDiffers(String existingATName, String literal) {
    return literal.equalsIgnoreCase(existingATName) && !literal.equals(existingATName);
  }

  private void addEnumLiteralChangesForNewAT(List<AtsApplicableChange> atChanges, VirtualAttributeType virtualAttributeType) {
    for (String literal : virtualAttributeType.getEnumAV()) {
      if (isForbiddenAttributeTypeName(literal)) {
        messageListener.onMessage(new AttributeTypeViolationMessage(literal));
        continue;
      }

      atChanges.add(new CreateEnumAvChange(literal, virtualAttributeType.getAtName()));
    }
  }

  private void addBBTAssocciationChanges(List<AtsApplicableChange> atChanges, VirtualAttributeType virtualAttributeType) {
    for (TypeOfBuildingBlock tobb : virtualAttributeType.getAssociatedToBB()) {
      BuildingBlockType bbt = buildingBlockTypeService.getBuildingBlockTypeByType(tobb);
      atChanges.add(new AssociateAttributeTypeWithTypeOfBuildingBlockChange(virtualAttributeType.getAtName(), bbt));
    }
  }

  private boolean isEnumAT(VirtualAttributeType virtualAttributeType, AttributeType existingAT) {
    return existingAT instanceof EnumAT && virtualAttributeType.getAtType().equals(EnumAT.class);
  }

  private boolean isResponsibilityAT(VirtualAttributeType virtualAttributeType, AttributeType existingAT) {
    return existingAT.getClass() != virtualAttributeType.getAtType() && ResponsibilityAT.class == existingAT.getClass()
        && TextAT.class == virtualAttributeType.getAtType();
  }

  private boolean isInvalidAT(VirtualAttributeType virtualAttributeType, AttributeType existingAT) {
    return existingAT.getClass() != virtualAttributeType.getAtType()
        && !(ResponsibilityAT.class == existingAT.getClass() && TextAT.class == virtualAttributeType.getAtType());
  }

  private boolean tobbIsAlreadyAssigned(AttributeType existingAT, TypeOfBuildingBlock tobb) {
    for (BuildingBlockType bbt : existingAT.getBuildingBlockTypes()) {
      if (bbt.getTypeOfBuildingBlock().equals(tobb)) {
        return true;
      }
    }
    return false;
  }

  Optional<String> enumLiteralExists(EnumAT existingAT, String literal) {
    for (EnumAV existingLiteral : existingAT.getAttributeValues()) {
      if (literal.equalsIgnoreCase(existingLiteral.getName())) {
        return Optional.of(existingLiteral.getName());
      }
    }
    return Optional.absent();
  }

  boolean isForbiddenAttributeTypeName(String atName) {
    for (String forbiddenName : AttributeType.BLACKLIST_FOR_AT_NAME) {
      if (forbiddenName.equalsIgnoreCase(atName)) {
        return true;
      }
    }
    return false;
  }
}
