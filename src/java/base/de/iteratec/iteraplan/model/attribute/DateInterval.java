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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


/**
 * Represents a Date Interval.
 */
@javax.persistence.Entity
public class DateInterval implements IdentityEntity {

  private static final long serialVersionUID = -3671865815002780654L;

  private Integer           id;

  private String            name;
  private String            defaultColorHex;
  private DateAT            startDate;
  private DateAT            endDate;

  public DateInterval() {
    super();
  }

  /**{@inheritDoc}**/
  public String getIdentityString() {
    return this.getName();
  }

  /**{@inheritDoc}**/
  public Integer getId() {
    return id;
  }

  /**{@inheritDoc}**/
  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDefaultColorHex() {
    return defaultColorHex;
  }

  public void setDefaultColorHex(String defaultColorHex) {
    this.defaultColorHex = defaultColorHex;
  }

  public DateAT getStartDate() {
    return startDate;
  }

  public void setStartDate(DateAT startDate) {
    this.startDate = startDate;
  }

  public DateAT getEndDate() {
    return endDate;
  }

  public void setEndDate(DateAT endDate) {
    this.endDate = endDate;
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
    DateInterval other = (DateInterval) obj;
    EqualsBuilder builder = new EqualsBuilder();
    builder.append(this.id, other.id);
    builder.append(this.name, other.name);
    builder.append(this.defaultColorHex, other.defaultColorHex);
    builder.append(this.startDate, other.startDate);
    builder.append(this.endDate, other.endDate);
    return builder.isEquals();
  }
  
  /**{@inheritDoc}**/
  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder(117, 51);
    builder.append(id).append(name).append(defaultColorHex).append(startDate).append(endDate);
    return builder.toHashCode();
  }
}
