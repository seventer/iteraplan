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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;


/**
 * Base test for the enum TypeOfAttribute. */
public class TypeOfAttributeTest {

  private Collection<AttributeValue> attributeValues  = null;

  private static DateAV              dateAV           = new DateAV();
  private static EnumAV              enumAV           = new EnumAV();
  private static NumberAV            numberAV         = new NumberAV();
  private static ResponsibilityAV    responsibilityAV = new ResponsibilityAV();
  private static TextAV              textAV           = new TextAV();

  @Before
  public void prepareAttributeValues() {
    attributeValues = Arrays.asList(new AttributeValue[] { dateAV, enumAV, numberAV, textAV, responsibilityAV });
  }

  @Test
  public void testFilter() {
    Map<TypeOfAttribute, AttributeValue> filters = new HashMap<TypeOfAttribute, AttributeValue>() {
      {
        put(TypeOfAttribute.DATE, dateAV);
        put(TypeOfAttribute.ENUM, enumAV);
        put(TypeOfAttribute.NUMBER, numberAV);
        put(TypeOfAttribute.RESPONSIBILITY, responsibilityAV);
        put(TypeOfAttribute.TEXT, textAV);
      }
    };
    for (Entry<TypeOfAttribute, AttributeValue> filter : filters.entrySet()) {
      assertEquals(filter.getValue(), Iterables.getOnlyElement(filter.getKey().filterAndSort(attributeValues)));
    }
  }
}
