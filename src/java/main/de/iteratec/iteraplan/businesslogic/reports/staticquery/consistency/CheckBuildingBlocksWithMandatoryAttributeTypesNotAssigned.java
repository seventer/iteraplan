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
package de.iteratec.iteraplan.businesslogic.reports.staticquery.consistency;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.Domain;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Parameter;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ParameterMultipleOption;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.Result;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultColumn.DataType;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.ResultRow;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;

public class CheckBuildingBlocksWithMandatoryAttributeTypesNotAssigned extends BuildingBlockConsistencyCheck {

  private AttributeTypeService              attributeTypeService;
  private BuildingBlockTypeService          buildingBlockTypeService;
  private BuildingBlockServiceLocator       buildingBlockServiceLocator;

  public CheckBuildingBlocksWithMandatoryAttributeTypesNotAssigned() {
    super("check_general_bbWithMandatoryAttributeValuesNotAssigned", Domain.GENERAL);
  }

  @Override
  public Result executeCheck(Map<String, Parameter> parameters) {
    if (!parameters.containsKey(TYPE_PARAMETER)) {
      throw new IllegalArgumentException("A consistency check parameter is not available.");
    }

    //Get the object for the parameter named 'type'.
    Parameter parameter = parameters.get(TYPE_PARAMETER);

    // Get the value of the parameter and the first option in the list.
    String value = parameter.getValue();
    String firstOption = ((ParameterMultipleOption) parameter).getFirstOption();

    List<BuildingBlock> buildingBlocks = new ArrayList<BuildingBlock>();
    List<AttributeType> attributeTypes;

    // The user selected all building block types to check.
    if (value.equalsIgnoreCase(firstOption)) {
      for (BuildingBlockType type : buildingBlockTypeService.getBuildingBlockTypesEligibleForAttributes()) {
        buildingBlocks.addAll(buildingBlockServiceLocator.getService(type.getTypeOfBuildingBlock()).loadElementList());
      }
      attributeTypes = attributeTypeService.loadElementList();
    }
    //The user selected a particular building block type to check.
    else {
      TypeOfBuildingBlock type = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(value);
      buildingBlocks = buildingBlockServiceLocator.getService(type).loadElementList();
      attributeTypes = attributeTypeService.getAttributeTypesForTypeOfBuildingBlock(type, true);
    }

    List<AttributeType> mandatoryAttributeTypes = new ArrayList<AttributeType>();
    for (AttributeType at : attributeTypes) {
      if (at.isMandatory()) {
        mandatoryAttributeTypes.add(at);
      }
    }

    if (mandatoryAttributeTypes.isEmpty()) {
      return getResult();
    }

    for (BuildingBlock block : buildingBlocks) {
      if (block instanceof HierarchicalEntity && ((HierarchicalEntity) block).isTopLevelElement()) {
        continue;
      }
      List<AttributeType> notAssignedAttributeTypes = new ArrayList<AttributeType>();
      for (AttributeType at : mandatoryAttributeTypes) {

        // Check if an attribute type is available for the building block.
        if (!(at.getBuildingBlockTypes().contains(block.getBuildingBlockType()))) {
          continue;
        }

        verifyAttributesNotSet(notAssignedAttributeTypes, block, at);
      }

      if (notAssignedAttributeTypes.size() > 0) {
        ResultRow resultRow = new ResultRow();
        resultRow.setElements(createRow(notAssignedAttributeTypes, block));

        getResult().getRows().add(resultRow);
      }
    }

    return getResult();
  }

  private List<Object> createRow(List<AttributeType> list, BuildingBlock block) {

    List<Object> row = new ArrayList<Object>();
    row.add(block.getBuildingBlockType());
    row.add(block);

    StringBuilder builder = new StringBuilder();
    for (AttributeType at : list) {
      builder.append(at.getIdentityString()).append("; ");
    }
    row.add(builder.toString());

    return row;
  }

  private void verifyAttributesNotSet(List<AttributeType> list, BuildingBlock bb, AttributeType at) {
    boolean isValueAssigned = false;

    for (AttributeValue av : at.getAllAttributeValues()) {
      AttributeValueAssignment ava = bb.getAssignmentForId(at.getId(), av);
      if (ava != null) {
        isValueAssigned = true;
        break;
      }
    }

    if (!isValueAssigned) {
      list.add(at);
    }
  }

  @Override
  protected List<ResultColumn> configureColumns() {
    List<ResultColumn> columns = new ArrayList<ResultColumn>();

    columns.add(new ResultColumn("check.column.buildingblock.type", DataType.OBJECT, "name", Boolean.FALSE, Boolean.TRUE));
    columns.add(new ResultColumn("check.column.buildingblock.name", DataType.OBJECT, "identityString", Boolean.TRUE, Boolean.FALSE));
    columns.add(new ResultColumn("check.column.attributetype.missing"));

    return columns;
  }
  
  public void setAttributeTypeService(AttributeTypeService attributeTypeService) {
    this.attributeTypeService = attributeTypeService;
  }
  
  public void setBuildingBlockTypeService(BuildingBlockTypeService buildingBlockTypeService) {
    this.buildingBlockTypeService = buildingBlockTypeService;
  }

  public void setBuildingBlockServiceLocator(BuildingBlockServiceLocator buildingBlockServiceLocator) {
    this.buildingBlockServiceLocator = buildingBlockServiceLocator;
  }
}