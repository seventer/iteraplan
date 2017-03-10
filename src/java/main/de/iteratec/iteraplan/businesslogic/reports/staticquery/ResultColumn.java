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
package de.iteratec.iteraplan.businesslogic.reports.staticquery;

import java.io.Serializable;


/**
 * This class defines meta information about the column header and the column data of the result
 * table of a static query.
 */
public class ResultColumn implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 1476285178326178960L;

  /**
   * The column's header. Note that it is assumed that this property is always subject to I18N.
   */
  private String            name;

  /**
   * The column's data type. Defaults to {@link DataType#STRING}.
   */
  private DataType          type;

  /**
   * The accessor method for elements of the column to display the desired string representation.
   * This only makes sense for elements of type {@link DataType#OBJECT}. Defaults to the empty
   * string.
   */
  private String            accessor;

  /**
   * Indicates whether the column should be linked. This only makes sense for elements of type
   * {@link DataType#OBJECT}. Defaults to false.
   */
  private Boolean           linked;

  /**
   * Indicates whether the column shall be subject to I18N. Defaults to false;
   */
  private Boolean           localized;

  /**
   * This enum class defines possible values for a column's data type.
   */
  public enum DataType {

    OBJECT, OBJECTLIST, STRING, DATE;
  }

  /**
   * Constructor.
   * 
   * @param name
   *          The localized name of the header.
   */
  public ResultColumn(String name) {
    this(name, DataType.STRING, "", Boolean.FALSE, Boolean.FALSE);
  }

  /**
   * Constructor.
   * 
   * @param name
   *          The localized name of the header.
   * @param type
   *          The data type of the column.
   */
  public ResultColumn(String name, DataType type) {
    this(name, type, "", Boolean.FALSE, Boolean.FALSE);
  }

  /**
   * Constructor.
   * 
   * @param name
   *          The localized name of the header.
   * @param type
   *          The data type of the column.
   * @param accessor
   *          The accessor method for the elements in the column.
   * @param linked
   *          True, if the elements in the column shall be linked.
   * @param localized
   *          True, if the elements in the column shall be subject to I18N.
   */
  public ResultColumn(String name, DataType type, String accessor, Boolean linked, Boolean localized) {
    this.name = name;
    this.type = type;
    this.accessor = accessor;
    this.linked = linked;
    this.localized = localized;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public DataType getType() {
    return type;
  }

  public void setType(DataType type) {
    this.type = type;
  }

  public String getAccessor() {
    return accessor;
  }

  public void setAccessor(String accessor) {
    this.accessor = accessor;
  }

  public Boolean getLinked() {
    return linked;
  }

  public void setLinked(Boolean linked) {
    this.linked = linked;
  }

  public Boolean getLocalized() {
    return localized;
  }

  public void setLocalized(Boolean localized) {
    this.localized = localized;
  }

}