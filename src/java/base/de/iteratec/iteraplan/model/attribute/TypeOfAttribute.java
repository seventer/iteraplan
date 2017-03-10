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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


public enum TypeOfAttribute {

  ENUM("attribute.type.enum", EnumAV.class), NUMBER("attribute.type.number", NumberAV.class), TEXT("attribute.type.text", TextAV.class), DATE(
      "attribute.type.date", DateAV.class), RESPONSIBILITY("attribute.type.responsibility", ResponsibilityAV.class);

  private static final Logger                   LOGGER = Logger.getIteraplanLogger(TypeOfAttribute.class);

  public static final List<TypeOfAttribute>     ALL_ATTRIBUTETYPES;

  private final String                          typeOf;

  private final Class<? extends AttributeValue> valueClass;

  static {
    ALL_ATTRIBUTETYPES = Collections.unmodifiableList(Arrays.asList(TypeOfAttribute.values()));
  }

  /**
   * Private Constructor.
   */
  private TypeOfAttribute(String typeOf, Class<? extends AttributeValue> valueClass) {
    this.typeOf = typeOf;
    this.valueClass = valueClass;
  }

  @Override
  public String toString() {
    return typeOf;
  }

  /**
   * @return Returns a String representation of the enum.
   */
  public String getName() {
    return typeOf;
  }

  public String getLocalizedName() {
    return MessageAccess.getString(typeOf, UserContext.getCurrentLocale());
  }

  public static TypeOfAttribute getTypeOfAttributeFromString(String typeOfAttribute) {
    if (ENUM.toString().equals(typeOfAttribute)) {
      return ENUM;
    }
    if (TEXT.toString().equals(typeOfAttribute)) {
      return TEXT;
    }
    if (NUMBER.toString().equals(typeOfAttribute)) {
      return NUMBER;
    }
    if (DATE.toString().equals(typeOfAttribute)) {
      return DATE;
    }
    if (RESPONSIBILITY.toString().equals(typeOfAttribute)) {
      return RESPONSIBILITY;
    }

    LOGGER.error("Invalid attribute type string: '" + typeOfAttribute + "'!");
    return null;
  }

  public List<AttributeValue> filterAndSort(Collection<AttributeValue> attributeValues) {
    List<AttributeValue> res = new ArrayList<AttributeValue>();
    for (AttributeValue attributeValue : attributeValues) {
      if (attributeValue.getClass().equals(this.valueClass)) {
        res.add(attributeValue);
      }
    }
    return sort(res);
  }

  public <T extends AttributeValue> List<T> sort(List<T> attributeValues) {
    try {
      Collections.sort(attributeValues);
    } catch (ClassCastException e) {
      LOGGER.error("Inproper use of TypeOfAttribute.sort", e);
      throw new IteraplanTechnicalException(e);
    }

    return attributeValues;
  }
}
