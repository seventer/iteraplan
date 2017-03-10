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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A dimension describes a characteristic for an element in a graphic. There are several
 * specifications implemented: LinePattern, Color, Position, Size The input is evaluated with a
 * adapter @see de.iteratec.iteraplan.businesslogic.exchange.common.dimension.DimensionAdapter .
 * 
 * @param <T>
 *          Class for dimension output. For example ColorDimension -> Color
 */
public abstract class Dimension<T> {

  /** The adapter handles the value extraction from a given input. **/
  private DimensionAdapter<?> adapter;

  private Map<String, T>      valueMapping = new HashMap<String, T>();

  public Dimension(DimensionAdapter<?> adapter) {
    this.adapter = adapter;
  }

  public T getValue(String value) {
    return getValueFromMapping(this.adapter.getResultForValue(value));
  }

  public T getValue(Object obj) {
    return getValueFromMapping(this.adapter.getResultForValue(obj));
  }

  public List<T> getMultipleValues(Object obj) {
    return getMultipleValuesFromMapping(this.adapter.getMultipleResultsForValue(obj));
  }

  public List<String> getValues() {
    return Collections.unmodifiableList(this.adapter.getValues());
  }

  private T getValueFromMapping(String key) {
    if (key != null) {
      T result = valueMapping.get(key);
      if (result != null) {
        return result;
      }
    }
    return getDefaultValue();
  }

  private List<T> getMultipleValuesFromMapping(List<String> keysList) {

    List<T> resultList = new ArrayList<T>();

    if (keysList != null) {
      for (String key : keysList) {
        if (key != null) {
          T result = valueMapping.get(key);
          if (result != null) {
            resultList.add(result);
          }
        }
      }
    }
    if (resultList.size() == 0) {
      resultList.add(getDefaultValue());
    }
    return resultList;
  }

  public void addMapping(String key, T value) {
    valueMapping.put(key, value);
  }

  public Map<String, T> getMapping() {
    return this.valueMapping;
  }

  public void setMapping(Map<String, T> mapping) {
    this.valueMapping = mapping;
  }

  /**
   * Creates a mapping from all attribute values to possible key.
   * 
   * @param values
   *          List of possible values of the attribute.
   */
  public void init(List<T> values) {
    List<String> keys = this.adapter.getValues();
    int size = Math.min(keys.size(), values.size());
    for (int i = 0; i < size; i++) {
      addMapping(keys.get(i), values.get(i));
    }
  }

  public DimensionAdapter<?> getAdapter() {
    return this.adapter;
  }

  public boolean hasUnspecificValue() {
    return adapter.hasUnspecificValue();
  }

  abstract T getDefaultValue();

  public String getName() {
    return adapter.getName();
  }

  /**
   * Creates a Map that maps Attribute Value Strings to Weight doubles.
   * 
   * @param values
   *          List of attribute value strings.
   * @return Map (String, Double)
   */
  protected Map<String, Double> calculateValueToWeightMap(List<String> values) {
    Map<String, Double> map = new HashMap<String, Double>();
    int listSize = values.size();
    if (listSize == 1) {
      map.put(values.get(0), Double.valueOf(0.5));
    }
    else if (listSize > 1) {
      double offset = ((double) 1 / (listSize - 1));
      for (int i = 0; i < listSize; i++) {
        map.put(values.get(i), Double.valueOf(i * offset));
      }
    }
    return map;
  }
}
