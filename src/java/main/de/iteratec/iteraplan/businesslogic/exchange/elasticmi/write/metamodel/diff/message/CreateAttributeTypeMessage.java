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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.diff.message;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


public class CreateAttributeTypeMessage extends AttributeTypeImportMessage {

  private static final String CREATE_ATTRIBUTE_MESSAGE_KEY = "de.iteratec.iteraplan.elasticmi.import.attribute.change.attribute.type.create";

  private static final String MANDATORY_MESSAGE_KEY        = "de.iteratec.iteraplan.elasticmi.import.attribute.change.attribute.type.mandatory";
  private static final String MULTIVALUE_MESSAGE_KEY       = "de.iteratec.iteraplan.elasticmi.import.attribute.change.attribute.type.multivalue";
  private static final String DATA_TYPE_ENUM_MESSAGE_KEY   = "attribute.type.enum";
  private static final String DATA_TYPE_TEXT_MESSAGE_KEY   = "attribute.type.text";
  private static final String DATA_TYPE_NUMBER_MESSAGE_KEY = "attribute.type.number";
  private static final String DATA_TYPE_DATE_MESSAGE_KEY   = "attribute.type.date";

  public CreateAttributeTypeMessage(String name, boolean mandatory, boolean mutlivalue, Class<?> type) {
    super(CREATE_ATTRIBUTE_MESSAGE_KEY, getFormattedParameter(name, mandatory, mutlivalue, type));
  }

  private static String getFormattedParameter(String name, boolean mandatory, boolean mutlivalue, Class<?> type) {
    StringBuilder resultBuilder = new StringBuilder();
    if (mandatory) {
      resultBuilder.append(getMandatoryString());
      resultBuilder.append(" ");
    }
    if (mutlivalue) {
      resultBuilder.append(getMultivalueString());
      resultBuilder.append(" ");
    }
    resultBuilder.append(getDataTypeString(type));
    resultBuilder.append(" ");
    resultBuilder.append(name);

    return resultBuilder.toString();
  }

  private static String getMandatoryString() {
    return MessageAccess.getString(MANDATORY_MESSAGE_KEY);
  }

  private static String getMultivalueString() {
    return MessageAccess.getString(MULTIVALUE_MESSAGE_KEY);
  }

  private static String getDataTypeString(Class<?> clazz) {
    if (EnumAT.class.equals(clazz)) {
      return MessageAccess.getString(DATA_TYPE_ENUM_MESSAGE_KEY);
    }
    else if (TextAT.class.equals(clazz)) {
      return MessageAccess.getString(DATA_TYPE_TEXT_MESSAGE_KEY);
    }
    else if (DateAT.class.equals(clazz)) {
      return MessageAccess.getString(DATA_TYPE_DATE_MESSAGE_KEY);
    }
    else if (NumberAT.class.equals(clazz)) {
      return MessageAccess.getString(DATA_TYPE_NUMBER_MESSAGE_KEY);
    }
    else {
      return "";
    }
  }

}
