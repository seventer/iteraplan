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
package de.iteratec.iteraplan.presentation.dialog.common.model.attributes;

import java.math.BigDecimal;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * ComponentModelPart for EnumAttributes.
 */
public class NumberAttributeTypeComponentModelPart extends AbstractAttributeTypeComponentModelPartBase<NumberAT> {

  /** Serialization version. */
  private static final long serialVersionUID       = -306943195891400623L;

  private NumberAV          attributeValue;

  /** Never null. */
  private String            attributeValueAsString = "";

  public NumberAttributeTypeComponentModelPart(NumberAT numberAT, NumberAV attributeValue, ComponentMode componentMode) {
    super(numberAT, componentMode);
    this.attributeValue = attributeValue;
  }

  @Override
  public void initializeFrom(BuildingBlock source) {
    super.initializeFrom(source);
    if (attributeValue != null && attributeValue.getValue() != null) {
      attributeValueAsString = BigDecimalConverter.format(attributeValue.getValue(), true, UserContext.getCurrentLocale());
    }
  }

  public void update() {
    attributeValueAsString = attributeValueAsString.trim();
    if (attributeValueAsString.length() > 0) {
      BigDecimal number = BigDecimalConverter.parse(attributeValueAsString, true, UserContext.getCurrentLocale());
      if (attributeValue == null) {
        attributeValue = new NumberAV(getAttributeType(), number);
      }
      else if (attributeValue.getId() == null) {
        attributeValue.setValue(number);
      }
      else if (!number.equals(attributeValue.getValue()) || getComponentMode() == ComponentMode.CREATE) {
        attributeValue = new NumberAV(getAttributeType(), number);
      }
    }
    else {
      attributeValue = null;
    }

    if (attributeValue != null) {
      attributeValueAsString = BigDecimalConverter.format(attributeValue.getValue(), true, UserContext.getCurrentLocale());
    }
    else {
      attributeValueAsString = "";
    }
  }

  @Override
  public void configure(BuildingBlock target) {
    SpringServiceFactory.getAttributeValueService().setValue(target, attributeValue, getAttributeType());
  }

  public String getAttributeValueAsString() {
    return attributeValueAsString;
  }

  public BigDecimal getAttributeValueAsNumber() {
    if (attributeValue == null) {
      return null;
    }
    return attributeValue.getValue();
  }

  public void setAttributeValueAsString(String attributeValueAsString) {
    assert attributeValueAsString != null;
    this.attributeValueAsString = attributeValueAsString;
  }

  /**
   * @return Returns true, if the attributeValue is not null and outside the range allowed by the
   *         attributeType.
   */
  public boolean isOutOfRange() {
    if (attributeValue != null) {
      return attributeValue.isOutOfRange();
    }
    return false;
  }

}