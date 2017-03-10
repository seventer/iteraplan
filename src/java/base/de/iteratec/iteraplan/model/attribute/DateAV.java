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

import java.util.Date;
import java.util.Locale;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;


/**
 * The model class for {@link AttributeValue}s of type {@link DateAT}.
 */
@Entity
@Audited
public class DateAV extends AttributeValue {

  private static final long serialVersionUID = 5321809143902207190L;

  private Date              value;

  private DateAT            attributeType;

  public DateAV() {
    super();
  }

  public DateAV(DateAT attributeType, Date value) {
    super();
    this.attributeType = attributeType;
    this.value = value;
  }

  @Override
  public Date getValue() {
    return value;
  }

  public DateAT getAttributeType() {
    return attributeType;
  }

  public void setValue(Date value) {
    this.value = value;
  }

  public void setAttributeType(DateAT attributeType) {
    this.attributeType = attributeType;
  }

  public void setAttributeTypeTwoWay(DateAT at) {
    if ((at != null) && (this.getValue() != null)) {
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
    return new DateAV(attributeType, value);
  }

  @Override
  public String getValueString() {
    return this.getLocalizedValueString(UserContext.getCurrentLocale());
  }

  @Override
  public String getLocalizedValueString(Locale locale) {
    return DateUtils.formatAsString(value, locale);
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(super.toString());
    result.append("\tValue: ").append(value);
    if (attributeType != null) {
      result.append("\tID of attribute type: ").append(attributeType.getId());
    }

    return result.toString();
  }

  public int compareTo(AttributeValue o) {
    return this.getValue().compareTo(((DateAV) o).getValue());
  }

}
