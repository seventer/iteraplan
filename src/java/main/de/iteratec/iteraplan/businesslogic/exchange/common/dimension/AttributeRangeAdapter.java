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
package de.iteratec.iteraplan.businesslogic.exchange.common.dimension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.NumberAT;


public class AttributeRangeAdapter extends AttributeAdapter {

  private List<BigDecimalRange> ranges = Lists.newArrayList();

  public AttributeRangeAdapter(Locale locale) {
    super(locale);
  }

  @Override
  public void init(AttributeType attrType, List<String> keys) {
    List<String> doubleRangeValues = keys;
    boolean uniform = ((NumberAT) attrType).isRangeUniformyDistributed();
    if (!uniform) {
      doubleRangeValues = ((NumberAT) attrType).getRangeValuesList(getLocale());
    }
    ranges = generateRangeList(doubleRangeValues, uniform);
    super.init(attrType, generateNewValueList());
  }

  @Override
  public String getResultForValue(String value) {
    if (value != null && !value.equals("")) {
      if (value.contains(BigDecimalRange.STRING_FOR_BETWEEN) || value.contains(BigDecimalRange.STRING_FOR_BIGGER)
          || value.contains(BigDecimalRange.STRING_FOR_SMALER)) {
        // this case happens, if rangekeys are taken --> return rangekey
        return value;
      }
      BigDecimal entry = BigDecimalConverter.parse(value, getLocale());

      for (BigDecimalRange range : ranges) {
        if (range.isInRange(entry)) {
          return range.getResultKey();
        }
      }
    }
    return null;
  }

  /**
   * Since AttributeRangeAdapter is only used for number attributes, and these can't have
   * multiple values assigned, there can also be only one result for an object.
   * Therefore this method essentially simply calls {@link #getResultForObject(BuildingBlock)}
   * and returns the result as single entry in a list.
   * @see #getResultForObject(BuildingBlock)
   */
  @Override
  protected List<String> getMultipleResultsForObject(BuildingBlock entity) {
    List<String> resultValues = CollectionUtils.arrayList();
    String result = getResultForObject(entity);
    if (result == null) {
      return resultValues;
    }
    else {
      resultValues.add(result);
      return resultValues;
    }
  }

  /**
   * Generates the set of values from the range list. Later the keys are used to map colors to
   * ranges.
   * 
   * @return list of keys
   */
  private List<String> generateNewValueList() {
    List<String> newValues = new ArrayList<String>();
    for (BigDecimalRange range : ranges) {
      newValues.add(range.getResultKey());
    }
    return newValues;
  }

  /**
   * Generates a range list from all input values. Total number of ranges is limited by
   * MAX_RANGELIST_SIZE.
   * 
   * @param values
   *          String values will be parsed to BigDecimal
   * @return List of BigDecimalRange objects.
   */
  private List<BigDecimalRange> generateRangeList(List<String> values, boolean uniformlyDistributed) {
    List<BigDecimalRange> newRange = new ArrayList<BigDecimalRange>();

    List<BigDecimal> decimalValues;
    if (uniformlyDistributed) {
      decimalValues = eliminateWasteElements(values);
    }
    else {
      decimalValues = new ArrayList<BigDecimal>();
      for (String value : values) {
        decimalValues.add(BigDecimalConverter.parse(value, getLocale()));
      }
    }
    // Collections.sort(decimalValues);

    BigDecimal lastValue = null;
    for (BigDecimal currentValue : decimalValues) {
      BigDecimalRange range = new BigDecimalRange(lastValue, currentValue, getLocale());
      lastValue = currentValue;
      newRange.add(range);
    }
    if (lastValue != null) {
      BigDecimalRange lastRange = new BigDecimalRange(lastValue, null, getLocale());
      newRange.add(lastRange);
    }
    Collections.sort(newRange);
    return newRange;
  }

  /**
   * Converts the input list from string to decimal values and if the MAX_RANGELIST_SIZE is reached,
   * the list is partitioned in MAX_RANGELIST_SIZE equally sized parts and only boundaries are
   * taken.
   * 
   * @param values
   *          List of String values
   * @return List of BigDecimal values with max size MAX_RANGELIST_SIZE
   */
  private List<BigDecimal> eliminateWasteElements(List<String> values) {
    List<BigDecimal> decimalValues = new ArrayList<BigDecimal>();
    for (String value : values) {
      decimalValues.add(BigDecimalConverter.parse(value, getLocale()));
    }
    Collections.sort(decimalValues);

    if (values.size() <= getMaxSize()) {
      return decimalValues;
    }

    // Eliminate values if list is bigger than MaxSize
    List<BigDecimal> keepValues = new ArrayList<BigDecimal>();
    int size = values.size();
    int parts = size / getMaxSize();
    int start = 0;
    int end = getMaxSize();
    for (int i = 1; i <= end; i++) {
      int pos = parts * i;
      List<BigDecimal> sublist = decimalValues.subList(start, pos);
      if (i != end) {
        keepValues.add(sublist.get((sublist.size() - 1)));
      }
      else {
        keepValues.add(sublist.get(0));
      }
      start = pos;
    }
    return keepValues;
  }

  public List<BigDecimalRange> getRanges() {
    return ranges;
  }

  /**
   * Defines max size of range values. There will be one more range as we add a last element for
   * values to infinity.
   * 
   * @return Integer with the max size of rage values.
   */
  public int getMaxSize() {
    return Constants.MAX_RANGELIST_SIZE;
  }

}
