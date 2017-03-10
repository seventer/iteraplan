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

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public abstract class MassUpdateConfig implements Comparable<MassUpdateConfig>, Serializable {

  /** Returns the text key to be used in the header of the mask to display a localized string **/
  private String headerKey;
  /** The path to the component model to be used for the underlying property or association **/
  private String pathToComponentModel;
  /** The position if placed in a list **/
  private int    position;

  public MassUpdateConfig() {
    super();
  }

  /**
   * Returns the text key to be used in the header of the mask to display a localized string
   * @return The header key
   */
  public String getHeaderKey() {
    return headerKey;
  }

  /**
   * The path to the component model to be used for the underlying property
   * @return The path to the component model
   */
  public String getPathToComponentModel() {
    return pathToComponentModel;
  }

  /**
   * The position where this element will be placed in a list. {@link #compareTo(MassUpdateConfig)}
   * uses this property.
   * @return The position
   */
  public int getPosition() {
    return position;
  }

  public void setHeaderKey(String headerKey) {
    this.headerKey = headerKey;
  }

  public void setPathToComponentModel(String pathToComponentModel) {
    this.pathToComponentModel = pathToComponentModel;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public int compareTo(MassUpdateConfig o) {
    return new CompareToBuilder().append(this.position, o.position).toComparison();
  }

  public int hashCode() {
    return new HashCodeBuilder(17, 31).
        append(position).
        append(pathToComponentModel).
        append(headerKey).
        toHashCode();
  }

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

    MassUpdateConfig rhs = (MassUpdateConfig) obj;
    return new EqualsBuilder().
        append(this.position, rhs.position).
        append(this.pathToComponentModel, rhs.pathToComponentModel).
        append(this.headerKey, rhs.headerKey).
        isEquals();
  }

}