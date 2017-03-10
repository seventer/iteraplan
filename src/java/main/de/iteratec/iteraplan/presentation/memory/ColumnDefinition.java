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
package de.iteratec.iteraplan.presentation.memory;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;


/**
 * A class to describe how a column in a search result table shall be rendered from the result
 * list.
 * <p>
 * This class is used by JSPs (Expression Language) AND the controller (nested bean path) to determine column headers, path to cell contents data, and to link
 * targets (optional).
 * </p>
 */
public class ColumnDefinition implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID  = 8724005076164577462L;

  private final String      tableHeaderKey;
  private final String      modelPath;
  private final String      pathToLinkedElement;
  private final boolean     visibleOnInit;
  private String            beanPropertyPath;
  private boolean           internationalized = false;
  private AttributeType     attributeType;

  /**
   * Creates a result column descriptor.
   * 
   * @param tableHeaderKey
   *          A message key which can be dereferenced to a short string for the column header
   * @param modelPath
   *          EL expression that must be used to find the result item property to display
   * @param pathToLinkedElement
   *          EL expression to an object, to which that call shall be linked. Use the empty
   *          string to link to the result item itself. <code>null</code> indicates that not link
   *          shall be created.
   * @param visibleOnInit
   *          If true, the column will be visible on startup.
   */
  public ColumnDefinition(String tableHeaderKey, String modelPath, String pathToLinkedElement, boolean visibleOnInit) {
    this.attributeType = null;
    this.tableHeaderKey = tableHeaderKey;
    this.modelPath = modelPath;
    this.beanPropertyPath = modelPath; // same path in JSP EL and Spring Beans
    this.pathToLinkedElement = pathToLinkedElement;
    this.visibleOnInit = visibleOnInit;
  }

  /**
   * Creates a result column descriptor for attribute type columns.
   * 
   * @param attributeType
   *    AttributeType for this column.
   * @param visibleOnInit
   *     If true, the column will be visible on startup.
   */
  public ColumnDefinition(AttributeType attributeType, boolean visibleOnInit) {

    // Because (Spring) bean paths and JSP EL paths don't treat map operators in the same way,
    // attribute columns need a seperation between the two paths.
    // see  https://jira.springsource.org/browse/SPR-4712

    this.tableHeaderKey = attributeType.getName();
    this.modelPath = "attributeTypeIdToValues." + attributeType.getId();
    this.beanPropertyPath = "attributeTypeIdToValues[" + attributeType.getId() + "]";
    this.pathToLinkedElement = "";
    this.visibleOnInit = visibleOnInit;
    this.attributeType = attributeType;
  }

  public String getTableHeaderKey() {
    return tableHeaderKey;
  }

  public String getModelPath() {
    return modelPath;
  }

  public String getPathToLinkedElement() {
    return pathToLinkedElement;
  }

  public String getBeanPropertyPath() {
    return beanPropertyPath;
  }

  public AttributeType getAttributeType() {
    return attributeType;
  }

  public boolean isVisibleOnInit() {
    return visibleOnInit;
  }

  public boolean isInternationalized() {
    return internationalized;
  }

  public void setInternationalized(boolean internationalized) {
    this.internationalized = internationalized;
  }

  public boolean isRightAligned() {
    return attributeType != null && ((attributeType instanceof NumberAT) || (attributeType instanceof DateAT));
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(31, 1).append(tableHeaderKey).append(modelPath).append(pathToLinkedElement).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }

    ColumnDefinition colDef = (ColumnDefinition) obj;
    return new EqualsBuilder().appendSuper(super.equals(obj)).append(tableHeaderKey, colDef.getTableHeaderKey())
        .append(modelPath, colDef.getModelPath()).append(pathToLinkedElement, colDef.getPathToLinkedElement()).isEquals();
  }
}