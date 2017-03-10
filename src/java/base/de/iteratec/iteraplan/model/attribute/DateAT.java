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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import de.iteratec.iteraplan.common.util.Preconditions;


/**
 * This is the model class for date attribute types.
 */
@Entity
@Audited
public class DateAT extends AttributeType implements Serializable {

  private static final long serialVersionUID = -6576047507259662206L;
  private Set<DateAV>       attributeValues  = new HashSet<DateAV>(0);

  public DateAT() {
    super();
  }

  @Override
  public Collection<? extends AttributeValue> getAllAttributeValues() {
    return getAttributeValues();
  }

  public Set<DateAV> getAttributeValues() {
    return attributeValues;
  }

  public void setAttributeValues(Set<DateAV> attributeValues) {
    this.attributeValues = attributeValues;
  }

  /**
   * Adds the given {@link DateAV} to this attribute type. Updates both sides of the association.
   * 
   * @throws IllegalArgumentException
   *           If the given attribute value is {@code null}.
   */
  public void addAttributeValue(DateAV av) {
    Preconditions.checkNotNull(av);
    attributeValues.add(av);
    av.setAttributeType(this);
  }

  @Override
  public Class<? extends AttributeValue> getClassOfAttributeValue() {
    return DateAV.class;
  }

  @Override
  public TypeOfAttribute getTypeOfAttribute() {
    return TypeOfAttribute.DATE;
  }

}