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

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import junit.framework.TestCase;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.AttributeRangeAdapter;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.BigDecimalRange;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorDimension;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.RangeValue;


public class AttributeRangeColorTest extends TestCase {

  private static final Logger LOGGER     = Logger.getIteraplanLogger(AttributeRangeColorTest.class);
  private static List<String> testValues = new ArrayList<String>();

  static {
    testValues.add("4,0");
    testValues.add("5,0");
    testValues.add("6,0");
    testValues.add("7,0");
    testValues.add("8,0");
    testValues.add("14,0");
    testValues.add("15,0");
    testValues.add("18,0");
    testValues.add("22,0");
    testValues.add("24,0");
    testValues.add("34,0");
    testValues.add("44,0");

  }

  public void testSetValues() {
    AttributeRangeAdapter adapter = new AttributeRangeAdapter(Locale.GERMAN);
    NumberAT attrType = new NumberAT();
    attrType.setRangeUniformyDistributed(true);
    adapter.init(attrType, testValues);

    List<BigDecimalRange> range = adapter.getRanges();
    // Max size defines values that are considered. There is always one more range for the last
    // value till infinity.
    assertEquals(adapter.getMaxSize() + 1, range.size());

    if (LOGGER.isDebugEnabled()) {
      for (BigDecimalRange value : range) {
        LOGGER.debug("Values: " + value.toString());
      }
    }

    List<String> newValues = adapter.getValues();
    assertEquals(adapter.getMaxSize() + 1, range.size());

    if (LOGGER.isDebugEnabled()) {
      for (String value : newValues) {
        LOGGER.debug("Values: " + value);
      }
    }
  }

  public void testUserDefinedRage() {
    AttributeRangeAdapter adapter = new AttributeRangeAdapter(Locale.GERMAN);
    NumberAT attrType = new NumberAT();
    attrType.setRangeUniformyDistributed(false);
    attrType.setRangeValues(getRangeValue(attrType));
    adapter.init(attrType, testValues);

    ColorDimension dimension = new ColorDimension(adapter);
    dimension.init(getColorList(5));

    runTestWithDimension(dimension);
  }

  private Set<RangeValue> getRangeValue(NumberAT type) {
    // Values_ < 6.0
    // Values_ 6.0 - 14.0
    // Values_ 14.0 - 22.0
    // Values_ 22.0 - 24.0
    // Values_ > 24.0

    Set<RangeValue> rangeValues = new HashSet<RangeValue>();
    rangeValues.add(new RangeValue(type, new BigDecimal("6.0")));
    rangeValues.add(new RangeValue(type, new BigDecimal("14.0")));
    rangeValues.add(new RangeValue(type, new BigDecimal("22.0")));
    rangeValues.add(new RangeValue(type, new BigDecimal("24.0")));
    return rangeValues;
  }

  private void runTestWithDimension(ColorDimension dimension) {
    Color newColor = dimension.getValue("5,0");
    String newColorDebugMsg = "New Color: {0}";

    LOGGER.debug(newColorDebugMsg, newColor);
    assertEquals(Color.decode("#0"), newColor);

    newColor = dimension.getValue("6,0");

    LOGGER.debug(newColorDebugMsg, newColor);
    assertEquals(Color.decode("#0"), newColor);

    newColor = dimension.getValue("7,7");

    LOGGER.debug(newColorDebugMsg, newColor);
    assertEquals(Color.decode("#1"), newColor);

    newColor = dimension.getValue("19,7");

    LOGGER.debug(newColorDebugMsg, newColor);
    assertEquals(Color.decode("#2"), newColor);

    newColor = dimension.getValue("23,54");

    LOGGER.debug(newColorDebugMsg, newColor);
    assertEquals(Color.decode("#3"), newColor);

    newColor = dimension.getValue("100,54");

    LOGGER.debug(newColorDebugMsg, newColor);
    assertEquals(Color.decode("#4"), newColor);
  }

  public void testUniformRange() {
    // Values_ < 6.0
    // Values_ 6.0 - 14.0
    // Values_ 14.0 - 22.0
    // Values_ 22.0 - 24.0
    // Values_ > 24.0

    AttributeRangeAdapter adapter = new AttributeRangeAdapter(Locale.GERMAN);
    NumberAT attrType = new NumberAT();
    attrType.setRangeUniformyDistributed(true);
    adapter.init(attrType, testValues);
    ColorDimension dimension = new ColorDimension(adapter);
    dimension.init(getColorList(5));

    runTestWithDimension(dimension);
  }

  List<Color> getColorList(int size) {
    List<Color> result = new ArrayList<Color>(size);
    int counter = 0;
    while (counter < size) {
      result.add(Color.decode("#" + counter));
      counter++;
    }
    return result;
  }
}
