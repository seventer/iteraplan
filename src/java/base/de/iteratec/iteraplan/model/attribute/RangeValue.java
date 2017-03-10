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
import java.math.BigDecimal;
import java.util.Locale;

import javax.persistence.Entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.envers.Audited;

import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


/**
 * The model class for {@link RangeValue}
 */
@Entity
@Audited
public class RangeValue implements Comparable<RangeValue>, IdentityEntity, Serializable {

  private static final long serialVersionUID = -7224111132111506703L;

  private Integer           id;
  private BigDecimal        value;
  private NumberAT          attributeType;

  public RangeValue() {
    // nothing
  }

  public RangeValue(NumberAT attributeType, BigDecimal value) {
    super();
    this.attributeType = attributeType;
    this.value = value;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getIdentityString() {
    return id.toString();
  }

  public BigDecimal getValue() {
    return value;
  }

  public NumberAT getAttributeType() {
    return attributeType;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public void setAttributeType(NumberAT attributeType) {
    this.attributeType = attributeType;
  }

  public void setAttributeTypeTwoWay(NumberAT at) {
    if (at != null) {
      at.getRangeValues().add(this);
      setAttributeType(at);
    }
  }

  public int compareTo(RangeValue other) {
    return value.compareTo(other.getValue());
  }

  public AttributeType getAbstractAttributeType() {
    return this.getAttributeType();
  }

  public RangeValue getCopy() {
    return new RangeValue(attributeType, value);
  }

  public String getValueString() {
    return this.getValue().toString();
  }

  public String getLocalizedValueString(Locale locale) {
    return BigDecimalConverter.format(this.getValue(), true, locale);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof RangeValue)) {
      return false;
    }

    RangeValue range = (RangeValue) obj;
    if (getId() == null || range.getId() == null) {
      return false;
    }

    EqualsBuilder builder = new EqualsBuilder();
    builder.append(getId(), range.getId());

    return builder.isEquals();
  }

  @Override
  public int hashCode() {
    HashCodeBuilder builder = new HashCodeBuilder();

    if (getId() == null) {
      builder.append(super.hashCode());
    }
    else {
      builder.append(getId());
    }

    return builder.toHashCode();
  }

  /**
   * @return Returns {@code true}, if the value is outside the range defined by the upper and lower
   *         bound of the associated attribute type. Otherwise, {@code false} is returned.
   */
  public boolean isOutOfRange() {
    BigDecimal upperBound = attributeType.getMaxValue();
    BigDecimal lowerBound = attributeType.getMinValue();

    if (upperBound != null && value != null && value.compareTo(upperBound) > 0) {
      return true;
    }

    if (lowerBound != null && value != null && value.compareTo(lowerBound) < 0) {
      return true;
    }

    return false;
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    builder.append("id", getId());
    builder.append("value", getValue());
    builder.append("attributeType", getAttributeType());

    return builder.toString();
  }

}
