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

import java.util.Locale;

import javax.persistence.Entity;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.envers.Audited;

import de.iteratec.iteraplan.common.util.StringUtil;


/**
 * The model class for {@link AttributeValue}s of type {@link EnumAT}.
 */
@Entity
@Audited
public class EnumAV extends AttributeValue implements DefaultColorAttributeValue {

  private static final long serialVersionUID = -5524564723546648114L;

  private String            name;

  /** dummy value, is not persisted in db. For EnumAV the value is equal to the name **/
  private String            value;

  private String            description;

  private EnumAT            attributeType;

  private String            defaultColorHex;

  private int               position;

  public EnumAV() {
    super();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = StringUtils.trim(StringUtil.removeIllegalXMLChars(name));
    this.value = this.name;
  }

  @Override
  public String getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDefaultColorHex() {
    return defaultColorHex;
  }

  public void setDefaultColorHex(String defaultColorHex) {
    this.defaultColorHex = defaultColorHex;
  }

  public EnumAT getAttributeType() {
    return attributeType;
  }

  public void setAttributeType(EnumAT attributeType) {
    this.attributeType = attributeType;
  }

  /**
   * Sets relation between this attribute value and the given attribute type on both sides.
   * Also initializes the position of this attribute value to be inserted after the existing values.
   * @param at the EnumAT this value is to be assigned to
   */
  public void setAttributeTypeTwoWay(EnumAT at) {
    if (at != null) {
      at.addAttribueValueTwoWay(this);
    }
  }

  @Override
  public AttributeType getAbstractAttributeType() {
    return this.getAttributeType();
  }

  @Override
  public AttributeValue getCopy() {
    return this;
  }

  /**
   * Get a new AV with the same value and no AVAs.
   * 
   * @return A new AV with a copy of this and no AVAs.
   */
  public AttributeValue getCopyWithoutConnection() {
    EnumAV av = new EnumAV();
    av.setAttributeType(this.getAttributeType());
    av.setDescription(this.getDescription());
    av.setDefaultColorHex(this.getDefaultColorHex());
    av.setName(this.getName());
    av.setOlVersion(this.getOlVersion());
    return av;
  }

  @Override
  public String getValueString() {
    return this.getName();
  }

  @Override
  public String getLocalizedValueString(Locale locale) {
    return this.getName();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(35);
    sb.append(super.toString());
    sb.append(" Name: ").append(name);
    if (attributeType != null) {
      sb.append(" Attribute type ID: ").append(attributeType.getId());
    }

    return sb.toString();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
    hashCodeBuilder.appendSuper(super.hashCode());
    hashCodeBuilder.append(name);
    return hashCodeBuilder.toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof EnumAV)) {
      return false;
    }
    EnumAV other = (EnumAV) obj;
    EqualsBuilder equalsBuilder = new EqualsBuilder();
    equalsBuilder.append(this.name, other.name);
    return equalsBuilder.isEquals();
  }

  /** Enum attribute are sorted by the explicitly stored order */
  public int compareTo(AttributeValue o) {
    EnumAV e = ((EnumAV) o);

    int indexOfOther = e.getPosition();
    int indexOfThis = this.getPosition();
    return indexOfThis - indexOfOther;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

}
