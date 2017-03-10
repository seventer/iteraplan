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
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import org.hibernate.envers.AuditMappedBy;
import org.hibernate.envers.Audited;

import com.google.common.collect.Lists;


/**
 * The model class for responsibility attribute types.
 */
@Entity
@Audited
public class ResponsibilityAT extends AttributeType implements MultiassignementType, Serializable {

  private static final long     serialVersionUID = -7457788290632337318L;

  private boolean               multiassignmenttype;

  // this is a set for it to work with hibernate envers for our history functionality
  private Set<ResponsibilityAV> attributeValues  = new HashSet<ResponsibilityAV>(0);

  public ResponsibilityAT() {
    super();
  }

  public boolean isMultiassignmenttype() {
    return multiassignmenttype;
  }

  @AuditMappedBy(mappedBy = "attributeType")
  public Set<ResponsibilityAV> getAttributeValues() {
    return attributeValues;
  }

  public List<ResponsibilityAV> getSortedAttributeValues() {
    return TypeOfAttribute.RESPONSIBILITY.sort(Lists.newArrayList(attributeValues));
  }

  @Override
  public Collection<? extends AttributeValue> getAllAttributeValues() {
    return getAttributeValues();
  }

  public void setMultiassignmenttype(boolean multiassignmenttype) {
    this.multiassignmenttype = multiassignmenttype;
  }

  public void setAttributeValues(Set<ResponsibilityAV> attributeValues) {
    this.attributeValues = attributeValues;
  }

  @Override
  public Class<? extends AttributeValue> getClassOfAttributeValue() {
    return ResponsibilityAV.class;
  }

  @Override
  public TypeOfAttribute getTypeOfAttribute() {
    return TypeOfAttribute.RESPONSIBILITY;
  }

  public void addAttributeValuesTwoWay(List<ResponsibilityAV> elementsToAdd) {
    for (ResponsibilityAV av : elementsToAdd) {
      av.setAttributeType(this);
    }
    attributeValues.addAll(elementsToAdd);
  }

  public void removeAttributeValuesTwoWay() {
    for (ResponsibilityAV av : attributeValues) {
      av.setAttributeType(null);
    }
    attributeValues.clear();
  }

}