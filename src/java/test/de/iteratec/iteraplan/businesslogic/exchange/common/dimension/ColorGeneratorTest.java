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
package de.iteratec.iteraplan.businesslogic.exchange.common.dimension;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.collections.StringToBigDecimalFunction;


public class ColorGeneratorTest {

  private static final double PRECISION = 10e-6;

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorGenerator#generateColor(java.lang.String)}.
   */
  @Test
  public void testGenerateColor1() {
    ColorGenerator generator = new ColorGenerator("ff0000", "00ff00", false, Lists.newArrayList(BigDecimal.ZERO, BigDecimal.valueOf(100)));
    generator.setDefaultColor("0000ff");

    assertEquals(Color.blue, generator.getDefaultColor());
    assertEquals(Color.red, generator.getLowerBoundColor());
    assertEquals(Color.green, generator.getUpperBoundColor());
    assertEquals(0f, generator.getLowerBound(), PRECISION);
    assertEquals(100f, generator.getUpperBound(), PRECISION);

    assertEquals(Color.blue, generator.generateColor(null));

    assertEquals(Color.red, generator.generateColor(BigDecimal.valueOf(-100)));
    assertEquals(Color.red, generator.generateColor(BigDecimal.ZERO));
    assertEquals(Color.green, generator.generateColor(BigDecimal.valueOf(100)));
    assertEquals(Color.green, generator.generateColor(BigDecimal.valueOf(200)));

    assertEquals(Color.yellow, generator.generateColor(BigDecimal.valueOf(50)));
    assertEquals(Color.getHSBColor(0.25f / 3f, 1f, 1f), generator.generateColor(BigDecimal.valueOf(25)));
    assertEquals(Color.getHSBColor(0.125f / 3f, 1f, 1f), generator.generateColor(BigDecimal.valueOf(12.5)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorGenerator#generateColor(java.lang.String)}.
   */
  @Test
  public void testGenerateColor2() {
    ColorGenerator generator = new ColorGenerator("000000", "000000", false, Lists.<BigDecimal> newArrayList());
    Color dColor = Color.getHSBColor(0f, 0f, 0f);
    generator.setDefaultColor(dColor);

    generator.setLowerBound(100);
    Color lColor = Color.getHSBColor(0f, 0f, .5f);
    generator.setLowerBoundColor(lColor);

    generator.setUpperBound(200);
    Color uColor = Color.getHSBColor(0f, 1f, .5f);
    generator.setUpperBoundColor(uColor);

    assertEquals(dColor, generator.generateColor(null));

    assertEquals(lColor, generator.generateColor(BigDecimal.ZERO));
    assertEquals(lColor, generator.generateColor(BigDecimal.valueOf(100)));
    assertEquals(uColor, generator.generateColor(BigDecimal.valueOf(200)));
    assertEquals(uColor, generator.generateColor(BigDecimal.valueOf(300)));

    assertEquals(Color.getHSBColor(0f, .5f, .5f), generator.generateColor(BigDecimal.valueOf(150)));
    assertEquals(Color.getHSBColor(0f, .25f, .5f), generator.generateColor(BigDecimal.valueOf(125)));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorGenerator#findBounds(java.util.List)}.
   */
  @Test
  public void testFindBounds() {
    ArrayList<String> stringArray = Lists.<String> newArrayList("0.2", "0.3", "123", "32", "-0.1", "333");
    Iterable<BigDecimal> values = Iterables.transform(stringArray, new StringToBigDecimalFunction(Locale.GERMANY));
    ColorGenerator generator = new ColorGenerator("000000", "000000", false, values);
    assertEquals(-0.1f, generator.getLowerBound(), PRECISION);
    assertEquals(333f, generator.getUpperBound(), PRECISION);
  }
}
