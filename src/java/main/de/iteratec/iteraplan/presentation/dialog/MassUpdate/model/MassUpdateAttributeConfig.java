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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.BBAttribute;


/**
 * GUI configuration class for attributes in mass update mode. Furthermore serves as Component Model
 * for the standard value dialog part
 */
public class MassUpdateAttributeConfig extends MassUpdateConfig {
  private static final long    serialVersionUID                       = 6566972288950972697L;

  private static final Logger  LOGGER                                 = Logger.getIteraplanLogger(MassUpdateAttributeConfig.class);

  /**
   * The type of the attribute as String. Currently one of [userdefEnum, userdefNumber, userdefDate,
   * userdefText, userdefResponsibility] See the constants in
   * {@link de.iteratec.iteraplan.model.attribute.BBAttribute} for possible values
   **/
  private String               type;
  /** Component Model for the standard value of Number, Text and Date Attributes **/
  private String               standardNewAttributeValue;
  /** Component Model for selected enum standard values **/
  private String[]             selectedStandardAtributeValueStringIds = new String[] {};
  /** Indicates whether a text attribute shall be displayed as multiline textbox **/
  private boolean              multiline                              = false;
  /** Component Model for possible attribute values for enums to be used for setting Standard Values **/
  private List<AttributeValue> attributeValues;
  /** The Db ID of the unterlying AttributeType **/
  private Integer              attributeTypeId;
  /**
   * Indicates if the standard values for this attribute shall be set. Defaults to false. Will be
   * set to true when clicking on the Button in the GUI. True for at most one
   * MassUpdateAttributeConfig at any time
   **/
  private boolean              setStandardValue                       = false;

  public void setType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public String getStandardNewAttributeValue() {
    return standardNewAttributeValue;
  }

  public String[] getSelectedStandardAtributeValueStringIds() {
    // delete element with value -1 which is only used to call the getter/setter at every request
    List<String> result = new ArrayList<String>();
    for (String val : selectedStandardAtributeValueStringIds) {
      if (!(val.equalsIgnoreCase("-1"))) {
        result.add(val);
      }
    }
    int size = result.size();
    int j = 0;
    String[] resultArray = new String[size];
    for (String val : result) {
      resultArray[j] = val;
      j++;
    }
    return resultArray;
  }

  /**
   * If the current attribute is a numberAT, localize the number.
   * 
   * @param standardNewAttributeValue
   */
  public void setStandardNewAttributeValue(String standardNewAttributeValue) {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Retrieved standard value: " + standardNewAttributeValue);
    }
    if (BBAttribute.USERDEF_NUMBER_ATTRIBUTE_TYPE.equals(type) && !StringUtils.isEmpty(standardNewAttributeValue)) {
      this.standardNewAttributeValue = BigDecimalConverter.format(
          BigDecimalConverter.parse(standardNewAttributeValue, true, UserContext.getCurrentLocale()), true, UserContext.getCurrentLocale());
    }
    else {
      this.standardNewAttributeValue = standardNewAttributeValue;
    }
  }

  public void setSelectedStandardAtributeValueStringIds(String[] selectedStandardAtributeValueStringIds) {
    this.selectedStandardAtributeValueStringIds = (selectedStandardAtributeValueStringIds == null) ? null : selectedStandardAtributeValueStringIds
        .clone();
  }

  public boolean isMultiline() {
    return multiline;
  }

  public void setMultiline(boolean isMultiLine) {
    this.multiline = isMultiLine;
  }

  public void setAttributeValues(List<AttributeValue> attributeValues) {
    this.attributeValues = attributeValues;
  }

  public List<AttributeValue> getAttributeValues() {
    return attributeValues;
  }

  public void setAttributeTypeId(Integer attributeTypeId) {
    this.attributeTypeId = attributeTypeId;
  }

  public Integer getAttributeTypeId() {
    return attributeTypeId;
  }

  public boolean isSetStandardValue() {
    return setStandardValue;
  }

  public void setSetStandardValue(boolean setStandardValue) {
    if (LOGGER.isDebugEnabled()) {
      if (setStandardValue) {
        LOGGER.debug("Standard Values will be set for attribute" + getAttributeTypeId());
      }
      else {
        LOGGER.debug("Standard Values will NOT be set for attribute" + getAttributeTypeId());
      }
    }
    this.setStandardValue = setStandardValue;
  }
}
