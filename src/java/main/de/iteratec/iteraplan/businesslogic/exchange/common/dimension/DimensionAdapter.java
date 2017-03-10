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

import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * DimensionAdapter are used by dimensions for interpretation of a given input. There are several
 * adapters implemented: Attribute, AttributeRange, Status and Empty
 * 
 * @param <T>
 *          Type of input. Only this class will be handled by the adapter.
 */
public abstract class DimensionAdapter<T> {

  private static final Logger LOGGER = Logger.getIteraplanLogger(DimensionAdapter.class);

  private final Locale        locale;
  private List<String>        values = Lists.newArrayList();

  /**
   * Creates a new instance of this class. 
   * 
   * @param locale the currently selected locale
   */
  public DimensionAdapter(Locale locale) {
    this.locale = locale;
  }

  void setValues(List<String> values) {
    this.values = values;
  }

  public List<String> getValues() {
    return this.values;
  }

  public Locale getLocale() {
    return this.locale;
  }

  @SuppressWarnings("unchecked")
  public String getResultForValue(Object type) {
    try {
      return getResultForObject((T) type);
    } catch (Exception e) {
      LOGGER.error("Wrong adapter used or wrong object selected for adapter!");
      throw new IteraplanTechnicalException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public List<String> getMultipleResultsForValue(Object type) {
    try {
      return getMultipleResultsForObject((T) type);
    } catch (Exception e) {
      LOGGER.error("Wrong adapter used or wrong object selected for adapter while trying to retrieve multiple value assignments!");
      throw new IteraplanTechnicalException(e);
    }
  }

  protected abstract String getResultForObject(T obj);

  /**
   * Retrieves a list with the assigned values for the AttributeType of this dimension. This method
   * is useful in the case of multiple value assignments.
   * 
   * @param obj
   *          The object whose assignments shall be fetched.
   * @return A list with the values of all assignments of this attribute type for this object. If
   *         there is just a single AttributeValueAssignment present, then the list will have only
   *         one element.
   */
  protected abstract List<String> getMultipleResultsForObject(T obj);

  abstract String getResultForValue(String value);

  /**
   * Returns the localized name for this dimension.
   * 
   * @return the localized name for this dimension
   */
  abstract String getName();

  /**
   * Returns {@code true} if the dimension has the unspecified value.
   * 
   * @return {@code true} if the dimension has the unspecified value, 
   *    otherwise returns {@code false}
   */
  abstract boolean hasUnspecificValue();
}
