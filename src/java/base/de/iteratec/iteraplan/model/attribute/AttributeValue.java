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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.ContainedIn;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Store;

import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


/**
 * The abstract model class for attribute values. <br/>
 * Note that Comparable is not implemented as the following precondition does not hold:
 * (x.compareTo(y)==0) == (x.equals(y)) Hence implementing Comparable may cause severe problems with
 * hash collections or the like. Use TypeOfAttribute.getValueComparator() instead.
 */
@Entity
@Audited
public abstract class AttributeValue implements IdentityEntity, Comparable<AttributeValue>, Serializable {

  private static final long             serialVersionUID          = 3849249405502124974L;

  @DocumentId
  private Integer                       id;

  private Integer                       olVersion;

  @ContainedIn
  private Set<AttributeValueAssignment> attributeValueAssignments = new HashSet<AttributeValueAssignment>();

  public AttributeValue() {
    super();
  }

  public Integer getId() {
    return id;
  }

  public Integer getOlVersion() {
    return olVersion;
  }

  /**
   * Caution: This method must NOT be called because the current mapping strategy causes problems
   * with MySQL. Calling this method results in a database join over each and every subclass of
   * {@link de.iteratec.iteraplan.model.BuildingBlock}.
   * 
   * @return The set of {@link AttributeValueAssignment}s where this attribute value is referenced
   *         by each element in the set.
   */
  public Set<AttributeValueAssignment> getAttributeValueAssignments() {
    return attributeValueAssignments;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setOlVersion(Integer olVersion) {
    this.olVersion = olVersion;
  }

  public void setAttributeValueAssignments(Set<AttributeValueAssignment> attributeValueAssignments) {
    this.attributeValueAssignments = attributeValueAssignments;
  }

  /**
   * Returns the concrete {@link AttributeType} of this attribute value.
   * 
   * @return See method description.
   */
  public abstract AttributeType getAbstractAttributeType();

  /**
   * Returns a copy of this attribute value. Since the implementation of this functionality depends
   * on the concrete type of the attribute value, the method is abstract and must be implemented by
   * all subclasses.
   * 
   * @return See method description.
   */
  public abstract AttributeValue getCopy();

  public String getIdentityString() {
    return getValueString();
  }

  /**
   * Returns the localized value of this attribute value depending on the given locale.
   * 
   * @param locale
   *          The locale to perform the localization for.
   * @return See method description.
   */
  public abstract String getLocalizedValueString(Locale locale);

  /**
   * Returns a string, free of non-word characters, of the attribute's value. This is primarily used
   * as part of a HTML ID.
   * 
   * @return See method description.
   */
  public String getValueForHtmlId() {
    return this.getValueString().replaceAll("\\W", "");
  }

  /**
   * Returns the attribute value as object.
   * 
   * @return the attribute value as object
   */
  public abstract Object getValue();

  /**
   * Returns the non-localized value of this attribute value.
   */
  @Field(store = Store.YES)
  // needs to be indexed as it determines the value of a specific attribute instance
  public abstract String getValueString();

  @Override
  public String toString() {
    return "Attribute value ID: " + id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof AttributeValue)) {
      return false;
    }
    final AttributeValue other = (AttributeValue) obj;

    EqualsBuilder equalsBuilder = new EqualsBuilder();
    equalsBuilder.append(getId(), other.getId());

    return equalsBuilder.isEquals();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();

    if (getId() == null) {
      hashCodeBuilder.appendSuper(super.hashCode());
    }
    else {
      hashCodeBuilder.append(getId());
    }

    return hashCodeBuilder.toHashCode();
  }
}