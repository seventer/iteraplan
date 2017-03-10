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
package de.iteratec.iteraplan.model.attribute;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.attribute.util.TimeseriesHelper;


/**
 * The model class for number attribute types.
 */
@Entity
@Audited
public class NumberAT extends AttributeType implements Serializable, TimeseriesType {

  /** Serialization version */
  private static final long serialVersionUID         = -2069792726247172102L;

  private BigDecimal        maxValue;

  private BigDecimal        minValue;

  private String            unit;

  private Set<NumberAV>     attributeValues          = new HashSet<NumberAV>(0);

  @SuppressWarnings("unused")
  private boolean           timeseries;

  /**
   * Attribute is only used for graphical diagrams. If true, the range values are not used and the
   * diagram uses uniform distributed values to categorize the values.
   **/
  private boolean           rangeUniformyDistributed = true;

  /**
   * Attribute is only used for graphical diagrams. Range values are used to categorize attribute
   * values and apply colors and line formats.
   **/
  private Set<RangeValue>   rangeValues              = new HashSet<RangeValue>(0);

  public NumberAT() {
    super();
  }

  public BigDecimal getMaxValue() {
    return maxValue;
  }

  public BigDecimal getMinValue() {
    return minValue;
  }

  public String getUnit() {
    return unit;
  }

  @Override
  public Collection<? extends AttributeValue> getAllAttributeValues() {
    return getAttributeValues();
  }

  public Set<NumberAV> getAttributeValues() {
    return attributeValues;
  }

  public void setMaxValue(BigDecimal maxValue) {
    this.maxValue = maxValue;
  }

  public void setMinValue(BigDecimal minValue) {
    this.minValue = minValue;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public void setAttributeValues(Set<NumberAV> attributeValues) {
    this.attributeValues = attributeValues;
  }

  public void addAttribueValueTwoWay(NumberAV av) {
    if (this.getAttributeValues() == null) {
      this.setAttributeValues(new HashSet<NumberAV>());
    }
    this.getAttributeValues().add(av);
    av.setAttributeType(this);
  }

  public List<Integer> getRangeValueIDs() {
    List<Integer> idList = new ArrayList<Integer>();
    for (RangeValue range : getRangeValues()) {
      idList.add(range.getId());
    }
    return idList;
  }

  public boolean isRangeUniformyDistributed() {
    return rangeUniformyDistributed;
  }

  public void setRangeUniformyDistributed(boolean rangeUniformyDistributed) {
    this.rangeUniformyDistributed = rangeUniformyDistributed;
  }

  public Set<RangeValue> getRangeValues() {
    Set<RangeValue> tmp = Sets.newHashSet(rangeValues);

    for (RangeValue value : tmp) {
      if (value.getValue() == null || "".equals(value.getValue().toString())) {
        rangeValues.remove(value);
      }
    }

    return rangeValues;
  }

  /**
   * All ranges are converted to strings, sorted and returned in a list.
   * 
   * @param locale
   *          Locale is used to get localized double values.
   * @return List of localized double values from all ranges.
   */
  public List<String> getRangeValuesList(Locale locale) {
    List<RangeValue> sortedRangeValues = new ArrayList<RangeValue>(getRangeValues());
    Collections.sort(sortedRangeValues);
    List<String> valueList = new ArrayList<String>();
    for (RangeValue range : sortedRangeValues) {
      valueList.add(range.getLocalizedValueString(locale));
    }
    return valueList;
  }

  public List<String> getRangeValuesAsString(Locale locale) {
    int count = 0;

    List<RangeValue> sortedRangeValues = new ArrayList<RangeValue>(getRangeValues());
    List<String> valueList = new ArrayList<String>();

    String lastValue = null;
    String stringForSmaller = "<";
    String stringForBigger = ">";
    String stringForEqual = "=";
    String stringForEmpty = " ";
    String stringForBetween = "-";

    Collections.sort(sortedRangeValues);

    for (RangeValue range : sortedRangeValues) {
      String currentValue = range.getLocalizedValueString(locale);
      if (lastValue == null) {
        valueList.add(stringForSmaller + stringForEqual + stringForEmpty + currentValue);
      }
      else if (lastValue != null) {
        valueList.add(lastValue + stringForEmpty + stringForBetween + stringForEmpty + currentValue);
      }
      lastValue = range.getLocalizedValueString(locale);
      count++;
    }
    if (lastValue != null && count != 1) {
      valueList.add(stringForBigger + stringForEmpty + lastValue);
    }

    return valueList;
  }

  public void setRangeValues(Set<RangeValue> rangeValues) {
    this.rangeValues = rangeValues;
  }

  public void addRangeValueTwoWay(RangeValue rv) {
    if (this.getRangeValues() == null) {
      this.setRangeValues(new HashSet<RangeValue>());
    }
    this.getRangeValues().add(rv);
    rv.setAttributeType(this);
  }

  public void addRangeValuesTwoWay(List<RangeValue> elementsToAdd) {
    for (RangeValue rangeValue : elementsToAdd) {
      rangeValue.setAttributeType(this);
    }

    rangeValues.addAll(Sets.newHashSet(elementsToAdd));
  }

  public RangeValue getRangeValueById(Integer id) {
    for (RangeValue rv : this.getRangeValues()) {
      if (id.equals(rv.getId())) {
        return rv;
      }
    }
    return null;
  }

  public void removeRangeValueTwoWays(Integer id) {
    RangeValue range = getRangeValueById(id);
    rangeValues.remove(range);
    range.setAttributeType(null);
  }

  @Override
  public String getNameWithGroupForExport() {
    String nameWithGroupAndUnit = super.getNameWithGroupForExport();
    if (unit != null && unit.length() > 0) {
      nameWithGroupAndUnit += " in [" + unit + "]";
    }
    return nameWithGroupAndUnit;
  }

  @Override
  public Class<? extends AttributeValue> getClassOfAttributeValue() {
    return NumberAV.class;
  }

  @Override
  public TypeOfAttribute getTypeOfAttribute() {
    return TypeOfAttribute.NUMBER;
  }

  @Override
  public void validate() {

    super.validate();

    // Check, if the range of {@link BigDecimal}s specified by the given lower and upper bounds is
    // valid.
    if (minValue != null && maxValue != null && minValue.compareTo(maxValue) > 0) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.LOWER_BOUND_GREATER_UPPER_BOUND);
    }

    if (timeseries) {
      TimeseriesHelper.validateAssignedBuildingBlockTypes(this);
    }
  }

  public BigDecimal calculateUpperBoundForNumberAt(List<BigDecimal> bigDecimalList) {
    if (bigDecimalList == null || bigDecimalList.isEmpty()) {
      return null;
    }
    List<BigDecimal> decimals = new ArrayList<BigDecimal>(bigDecimalList);
    // assure that existing NumberAT bounds are always included:
    BigDecimal lowerBound = this.getMinValue();
    if (lowerBound != null) {
      decimals.add(lowerBound);
    }
    BigDecimal upperBound = this.getMaxValue();
    if (upperBound != null) {
      decimals.add(upperBound);
    }
    return Collections.max(decimals);
  }

  public BigDecimal calculateLowerBoundForNumberAt(List<BigDecimal> bigDecimalList) {
    if (bigDecimalList == null || bigDecimalList.isEmpty()) {
      return null;
    }
    List<BigDecimal> decimals = new ArrayList<BigDecimal>(bigDecimalList);
    // assure that existing NumberAT bounds are always included:
    BigDecimal lowerBound = this.getMinValue();
    if (lowerBound != null) {
      decimals.add(lowerBound);
    }
    BigDecimal upperBound = this.getMaxValue();
    if (upperBound != null) {
      decimals.add(upperBound);
    }
    return Collections.min(decimals);
  }

  /**{@inheritDoc}**/
  public boolean isTimeseries() {
    return timeseries;
  }

  /**{@inheritDoc}**/
  public void setTimeseries(boolean isTimeseries) {
    this.timeseries = isTimeseries;
  }

}