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

import java.math.BigDecimal;
import java.util.Locale;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import de.iteratec.iteraplan.common.util.BigDecimalConverter;


/**
 * The model class for {@link AttributeValue}s of type {@link NumberAT}.
 */
@Entity
@Audited
public class NumberAV extends AttributeValue {

  private static final long serialVersionUID = -250935979126530827L;

  private BigDecimal        value;

  private NumberAT          attributeType;

  public NumberAV() {
    super();
  }

  public NumberAV(NumberAT attributeType, BigDecimal value) {
    super();
    this.attributeType = attributeType;
    this.value = value;
  }

  @Override
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
      at.getAttributeValues().add(this);
      setAttributeType(at);
    }
  }

  @Override
  public AttributeType getAbstractAttributeType() {
    return this.getAttributeType();
  }

  @Override
  public AttributeValue getCopy() {
    return new NumberAV(attributeType, value);
  }

  @Override
  public String getValueString() {
    return this.getValue().toString();
  }

  @Override
  public String getLocalizedValueString(Locale locale) {
    return BigDecimalConverter.format(this.getValue(), true, locale);
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

    StringBuilder result = new StringBuilder(40);
    result.append(super.toString());
    result.append("\tValue: ").append(value.toString());
    if (attributeType != null) {
      result.append("\tID of attribute type: ").append(attributeType.getId());
    }

    return result.toString();
  }

  public int compareTo(AttributeValue o) {
    return this.getValue().compareTo(((NumberAV) o).getValue());
  }

}
