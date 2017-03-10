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

import java.util.Date;

import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * ComponentModelPart for DateAttributes.
 */
public class DateAttributeTypeComponentModelPart extends AbstractAttributeTypeComponentModelPartBase<DateAT> {

  /** Serialization version. */
  private static final long serialVersionUID       = -5021360566548287982L;
  private DateAV            attributeValue         = null;
  private String            attributeValueAsString = "";

  public DateAttributeTypeComponentModelPart(DateAT dateAT, DateAV attributeValue, ComponentMode componentMode) {
    super(dateAT, componentMode);
    this.attributeValue = attributeValue;
  }

  @Override
  public void initializeFrom(BuildingBlock source) {
    super.initializeFrom(source);
    if (attributeValue != null && attributeValue.getValue() != null) {
      attributeValueAsString = DateUtils.formatAsString(attributeValue.getValue(), UserContext.getCurrentLocale());
    }
  }

  public void update() {
    Date date = DateUtils.parseAsDate(this.attributeValueAsString, UserContext.getCurrentLocale());
    attributeValueAsString = attributeValueAsString.trim();
    if (attributeValueAsString.length() > 0) {
      if (attributeValue == null) {
        attributeValue = new DateAV(getAttributeType(), date);
      }
      else if (attributeValue.getId() == null) {
        attributeValue.setValue(DateUtils.parseAsDate(this.attributeValueAsString, UserContext.getCurrentLocale()));
      }
      else if (!date.equals(attributeValue.getValue()) || getComponentMode() == ComponentMode.CREATE) {
        attributeValue = new DateAV(getAttributeType(), date);
      }
    }
    else {
      attributeValue = null;
    }

    if (attributeValue != null) {
      attributeValueAsString = DateUtils.formatAsString(attributeValue.getValue(), UserContext.getCurrentLocale());
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

  public Date getAttributeValueAsDate() {
    if (attributeValue == null) {
      return null;
    }
    return attributeValue.getValue();
  }

  public void setAttributeValueAsString(String attributeValueAsString) {
    assert attributeValueAsString != null;
    this.attributeValueAsString = attributeValueAsString;
  }

}