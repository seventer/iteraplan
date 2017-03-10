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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.presentation.SpringGuiFactory;


public class LineDimensionOptionsBean extends DimensionOptionsBean implements Serializable {

  /** Serialization version. */
  private static final long    serialVersionUID            = -6284630915484761628L;

  private static final Logger  LOGGER                      = Logger.getIteraplanLogger(LineDimensionOptionsBean.class);

  /**
   * Map mapping attribute values to Strings representing the selected line type ids.
   */
  private Map<String, String>  valueToLineTypeMap          = Maps.newLinkedHashMap();

  /** Maps from line type id to description. */
  private Map<Integer, String> availableLineTypes          = new HashMap<Integer, String>();

  /**
   * Numerical encoding of the default line type.
   */
  private Integer              defaultLineType             = Integer.valueOf(0);

  /**
   * Presentation key for the default line type.
   */
  private String               defaultLineTypePresentation = "cont";

  public LineDimensionOptionsBean() {

    SpringGuiFactory factory = SpringGuiFactory.getInstance();

    defaultLineType = factory.getDefaultLineTypeKey();
    defaultLineTypePresentation = factory.getDefaultLineTypePresentation();

    Map<Integer, String> predefinedLineTypes = factory.getAvailableLineTypes();

    if (predefinedLineTypes != null) {
      availableLineTypes.putAll(predefinedLineTypes);
    }
  }

  @Override
  public void switchToGenerationMode() {

    if (getDimensionAttributeId().intValue() == GraphicalExportBaseOptions.NOTHING_SELECTED
        || isCustomAttribute(getDimensionAttributeId().intValue())) {
      return;
    }

    if (!valueToLineTypeMap.isEmpty()) {
      defaultLineType = Integer.valueOf(valueToLineTypeMap.get(DEFAULT_VALUE));
      valueToLineTypeMap.remove(DEFAULT_VALUE);
    }

  }

  @Override
  public void switchToPresentationMode() {

    if (getDimensionAttributeId().intValue() == GraphicalExportBaseOptions.NOTHING_SELECTED
        || isCustomAttribute(getDimensionAttributeId().intValue())) {
      return;
    }

    if (!valueToLineTypeMap.isEmpty()) {
      valueToLineTypeMap.put(DEFAULT_VALUE, String.valueOf(defaultLineType));
      defaultLineType = SpringGuiFactory.getInstance().getDefaultLineTypeKey();
    }

  }

  @Override
  public void refresh(List<String> dimensionAttributeValues) {

    // Error checking
    if (this.getCurrentMode() != DimensionOptionsMode.PRESENTATION) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    // Remove the default value from the available attribute values if its already been added
    availableLineTypes.remove(defaultLineType);

    // Add the attribute values
    setAttributeValues(dimensionAttributeValues);

    if (getDimensionAttributeId().intValue() > 0 && !valueToLineTypeMap.isEmpty()) {
      // Add the unspecified value
      availableLineTypes.put(defaultLineType, defaultLineTypePresentation);
    }
  }

  @Override
  public void matchValuesFromSavedQuery(List<String> savedAttributeValues, List<String> savedLineValues) {
    if (savedAttributeValues == null || savedLineValues == null || savedAttributeValues.size() != savedLineValues.size()) {
      LOGGER.debug("Saved attribute values and saved selected line types don't match. Using standard mapping.");
      return;
    }

    Set<String> attributeValues = new LinkedHashSet<String>(getAttributeValues());

    for (int i = 0; i < savedAttributeValues.size(); i++) {
      String value = savedAttributeValues.get(i);
      if (attributeValues.contains(value)) {
        setSelectedLineType(value, savedLineValues.get(i));
      }
    }

  }

  @Override
  public List<String> getAttributeValues() {
    return new ArrayList<String>(valueToLineTypeMap.keySet());
  }

  /**
   * @param lineAttributeValues
   *          The list to set. If null, an empty list is set.
   */
  @Override
  protected void setAttributeValues(List<String> lineAttributeValues) {
    resetValueToLineTypeMap();
    if (lineAttributeValues != null) {
      int numAvailableLineTypes = getAvailableLineTypeIds().size();
      int dimensionAttributeId = getDimensionAttributeId().intValue();

      resetValueToLineTypeMap();
      for (int i = 0; i < lineAttributeValues.size(); i++) {
        this.valueToLineTypeMap.put(lineAttributeValues.get(i), getAvailableLineTypeIds().get(i % numAvailableLineTypes).toString());
      }

      // Add unspecified lineType
      if (!isCustomAttribute(dimensionAttributeId) && (dimensionAttributeId > 0 || !lineAttributeValues.isEmpty())) {
        valueToLineTypeMap.put(DEFAULT_VALUE, String.valueOf(defaultLineType));
      }
    }
  }

  /**
   * @return A list of Integer that represent the ids of the different line types.
   */
  public List<Integer> getAvailableLineTypeIds() {
    List<Integer> ids = new ArrayList<Integer>(availableLineTypes.keySet());
    Collections.sort(ids);
    return ids;
  }

  public Map<Integer, String> getAvailableLineTypes() {
    return availableLineTypes;
  }

  public List<String> getSelectedLineTypes() {
    return new ArrayList<String>(valueToLineTypeMap.values());
  }

  /**
   * Sets the line type on a given value
   * 
   * @param attributeValue
   *          The attribute value the line type belongs to
   * @param lineType
   *          The line type to set
   */
  public void setSelectedLineType(String attributeValue, String lineType) {
    Integer key;
    try {
      key = Integer.valueOf(lineType);
      if (availableLineTypes.containsKey(key)) {
        valueToLineTypeMap.put(attributeValue, key.toString());
      }
    } catch (NumberFormatException e) {
      // did not receive an integer key - this problem should never occur since
      // the method is called during unmarshalling saved reports and testing for
      // integer values is part of the validation process
      LOGGER.error("Invalid line type given in LineDimensionOptionBean.setSelectedLineType(String,String)", e);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
  }

  public Integer getDefaultLineType() {
    return defaultLineType;
  }

  public void setDefaultLineType(Integer defaultLineType) {
    this.defaultLineType = defaultLineType;
  }

  public String getDefaultLineTypePresentation() {
    return defaultLineTypePresentation;
  }

  public void setAvailableLineStyles(Map<Integer, String> lineStyles) {
    this.availableLineTypes = lineStyles;
  }

  public Map<Integer, String> getAvailableLineTypesMap() {
    return this.availableLineTypes;
  }

  /**
   * resets the value to color mapping
   */
  public void resetValueToLineTypeMap() {
    this.valueToLineTypeMap = Maps.newLinkedHashMap();
  }

  /**
   * @return The value to color mapping
   */
  public Map<String, String> getValueToLineTypeMap() {
    return valueToLineTypeMap;
  }

}
