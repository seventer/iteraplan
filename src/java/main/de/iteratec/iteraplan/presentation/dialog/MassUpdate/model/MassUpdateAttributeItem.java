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
package de.iteratec.iteraplan.presentation.dialog.MassUpdate.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;


/**
 * Holds one BuildingBlock and all mass update related data for this instance. When running a mass
 * update, an instance of this class is created for each BuildingBlock that is to be updated. It
 * holds the instance to update, the new attribute values that are set by the user and status
 * information about the update.
 */
public class MassUpdateAttributeItem implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID                = 6291071222620636575L;

  /** BuildingBlock for which the attribute value update should be done. */
  private BuildingBlock     buildingBlockToUpdate;

  /** Id of the AttributeType for which attribute values should be updated. */
  private Integer           attributeId;

  /** The user entered new AttributeValue String. May be null. */
  private String            newAttributeValue;

  /** Array of user selected AttributeValue ids to add. */
  private String[]          selectedAttributeValueStringIds = new String[] {};

  /**
   * When the user defines a standard value, this item is updated with the standard values.
   */
  private boolean           usesStandardAttributeValues     = true;

  /** This item was executed in a mass update. */
  private boolean           wasExecuted                     = false;

  /** This item was executed in a mass update and the mass update was successful. */
  private boolean           wasSuccessful                   = false;

  /** Contains an internationalized error message if the update was not successful. */
  private String            errorMessage;

  /** When a mass update is to be executed, this item should be processed. */
  private boolean           selectedForMassUpdate           = true;

  /** The type of the atttibute as String **/
  private String            type;

  public MassUpdateAttributeItem(BuildingBlock bbToUpdate) {
    this(bbToUpdate, null);
  }

  public MassUpdateAttributeItem(BuildingBlock bbToUpdate, Integer attributeId) {
    this.buildingBlockToUpdate = bbToUpdate;
    this.attributeId = attributeId;
  }

  public BuildingBlock getBuildingBlockToUpdate() {
    return buildingBlockToUpdate;
  }

  public Integer getAttributeId() {
    return attributeId;
  }

  public void setAttributeId(Integer attributeId) {
    this.attributeId = attributeId;
  }

  public String getNewAttributeValue() {
    return newAttributeValue;
  }

  /**
   * If the current attribute is a numberAT, localize the number.
   * 
   * @param newAttributeValue
   */
  public void setNewAttributeValue(String newAttributeValue) {
    if (BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE.equals(type) && !StringUtils.isEmpty(newAttributeValue)) {
      this.newAttributeValue = BigDecimalConverter.format(BigDecimalConverter.parse(newAttributeValue, true, UserContext.getCurrentLocale()), true,
          UserContext.getCurrentLocale());
    }
    else {
      this.newAttributeValue = newAttributeValue;
    }
  }

  public boolean isUsesStandardAttributeValues() {
    return usesStandardAttributeValues;
  }

  public void setUsesStandardAttributeValues(boolean usesStandardAttributeValues) {
    this.usesStandardAttributeValues = usesStandardAttributeValues;
  }

  public String[] getSelectedAttributeValueStringIds() {
    // delete element with value -1 which is only used to call the getter/setter at every request
    List<String> result = new ArrayList<String>();
    for (int i = 0; i < selectedAttributeValueStringIds.length; i++) {
      if (!selectedAttributeValueStringIds[i].equalsIgnoreCase("-1")) {
        result.add(selectedAttributeValueStringIds[i]);
      }
    }
    int size = result.size();
    int j = 0;
    String[] resultArray = new String[size];
    for (Iterator<String> it = result.iterator(); it.hasNext();) {
      resultArray[j] = it.next();
      j++;
    }
    return resultArray;
  }

  public void setSelectedAttributeValueStringIds(String[] selectedAttributeValueStringIds) {
    this.selectedAttributeValueStringIds = (selectedAttributeValueStringIds == null) ? null : selectedAttributeValueStringIds.clone();
  }

  /*
   * Returns a List of Integer. (non-Javadoc)
   * @see de.iteratec.iteraplan.businesslogic.facade.interfaces.IMassUpdateAttributeDTO#
   * getSelectedAttributeValueIds()
   */
  public List<Integer> getSelectedAttributeValueIds() {
    List<Integer> attrValIds = new ArrayList<Integer>();
    String[] attrValStringIds = getSelectedAttributeValueStringIds();
    for (int i = 0; i < attrValStringIds.length; i++) {
      String stringId = getSelectedAttributeValueStringIds()[i];
      if (stringId != null && !stringId.equals("")) {
        attrValIds.add(Integer.valueOf(stringId));
      }
    }
    return attrValIds;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public boolean isWasExecuted() {
    return wasExecuted;
  }

  public void setWasExecuted(boolean wasExecuted) {
    this.wasExecuted = wasExecuted;
  }

  public boolean isWasSuccessful() {
    return wasSuccessful;
  }

  public void setWasSuccessful(boolean wasSuccessful) {
    this.wasSuccessful = wasSuccessful;
  }

  public boolean isSelectedForMassUpdate() {
    return selectedForMassUpdate;
  }

  public void setSelectedForMassUpdate(boolean selectedForMassUpdate) {
    this.selectedForMassUpdate = selectedForMassUpdate;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  /*
   * (non-Javadoc)
   * @see
   * de.iteratec.iteraplan.businesslogic.facade.dto.massupdate.IMassUpdateAttributeDTO#getBuildingBlock
   * ()
   */
  public BuildingBlock getBuildingBlock() {
    return buildingBlockToUpdate;
  }

}
