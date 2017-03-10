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
package de.iteratec.iteraplan.businesslogic.exchange.dimension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.TestCase;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.PositionDimension;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;


public class AttributePositionTest extends TestCase {

  public void testInitialization() {
    AttributeAdapter adapter = new AttributeAdapter(Locale.GERMAN);

    NumberAT numberAt = new NumberAT();
    numberAt.setName("Test");

    adapter.init(numberAt, getNumberKeyList());

    PositionDimension dimension = new PositionDimension(adapter);

    dimension.initNumberAt(numberAt, getNumberValueList());
    assertEquals(dimension.getValue("0,00").doubleValue(), (0.00), 0.000001);
    assertEquals(dimension.getValue("1,00").doubleValue(), (1.00 / 3.00), 0.000001);
    assertEquals(dimension.getValue("2,00").doubleValue(), (2.00 / 3.00), 0.000001);
    assertEquals(dimension.getValue("3,00").doubleValue(), (1.00), 0.000001);
    assertEquals(dimension.getValue("Whatever").doubleValue(), (-1.00), 0.000001);

    dimension.initNotNumberAt(getKeyList());

    assertEquals(dimension.getValue("A").doubleValue(), (0.00), 0.000001);
    assertEquals(dimension.getValue("B").doubleValue(), (1.00 / 3.00), 0.000001);
    assertEquals(dimension.getValue("C").doubleValue(), (2.00 / 3.00), 0.000001);
    assertEquals(dimension.getValue("D").doubleValue(), (1.00), 0.000001);
    assertEquals(dimension.getValue("Whatever").doubleValue(), (-1.00), 0.000001);

  }

  private List<String> getNumberKeyList() {
    List<String> result = new ArrayList<String>();
    result.add("0.0");
    result.add("1.0");
    result.add("2.0");
    result.add("3.0");
    return result;
  }

  private List<String> getKeyList() {
    List<String> result = new ArrayList<String>();
    result.add("A");
    result.add("B");
    result.add("C");
    result.add("D");
    return result;
  }

  private List<NumberAV> getNumberValueList() {
    List<NumberAV> result = new ArrayList<NumberAV>();
    result.add(getNumberAV("0.0"));
    result.add(getNumberAV("1.0"));
    result.add(getNumberAV("2.0"));
    result.add(getNumberAV("3.0"));
    return result;
  }

  private NumberAV getNumberAV(String value) {
    NumberAV number = new NumberAV();
    number.setValue(new BigDecimal(value));
    return number;
  }
}
