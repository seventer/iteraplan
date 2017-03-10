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
package de.iteratec.iteraplan.common.util;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;


@RunWith(Parameterized.class)
public class BigDecimalConverterTest {
  /** The locale to test with. */
  private Locale locale;

  public BigDecimalConverterTest(Locale locale) {
    this.locale = locale;
  }

  /** Method returning the parameter list. */
  @Parameters
  public static Collection<?> getConverterParameters() {
    return Arrays.asList(new Object[][] { { Locale.GERMAN }, { Locale.GERMANY }, { Locale.ENGLISH }, { Locale.FRENCH }, { new Locale("es") } });
  }

  @Test
  public void testFormatCorrectPositiveWithRounding() {
    testFormatCorrectPositive(true);
  }

  @Test
  public void testFormatCorrectPositiveWithoutRounding() {
    testFormatCorrectPositive(false);
  }

  @Test
  public void testFormatCorrectNegativeWithRounding() {
    testFormatCorrectNegative(true);
  }

  @Test
  public void testFormatCorrectNegativeWithoutRounding() {
    testFormatCorrectNegative(false);
  }

  private void testFormatCorrectPositive(boolean activateRounding) {
    String[] numbers = new String[] { "999.990", "999.999", "999.99", "999.90" };
    testFormatCorrect(numbers, activateRounding);
  }

  private void testFormatCorrectNegative(boolean activateRounding) {
    String[] numbers = new String[] { "-999.990", "-999.999", "-999.99" };
    testFormatCorrect(numbers, activateRounding);
  }

  private void testFormatCorrect(String[] numbers, boolean activateRounding) {
    for (String number : numbers) {
      String localizedNumber = BigDecimalConverter.format(new BigDecimal(number), activateRounding, locale);
      assertEquals("Unexpected formatting.", getLocalizedNumberWithoutConverter(number, activateRounding), localizedNumber);
    }
  }

  /**
   * Takes a decimal point separated number representation and returns the expected localized value
   * without using the converter.
   * 
   * @return the localized number
   */
  private String getLocalizedNumberWithoutConverter(String number, boolean activateRounding) {
    // Assume only well-formated inputs - cut the String if rounding is activated
    String tmp = number;    
    if (activateRounding) {
      tmp = tmp.substring(0, tmp.indexOf('.') + 3);
    }
    return BigDecimalConverter.isUsingDecimalComma(locale) ? tmp.replace('.', ',') : tmp;
  }

  @Test
  public void testParseCorrectNegative() {
    BigDecimal original = new BigDecimal("-123.45");
    String[] numbers = new String[] { "-123.450", "-123.459", "-123.45" };
    testParseCorrect(original, numbers);
  }

  @Test
  public void testParseCorrectPositive() {
    BigDecimal original = new BigDecimal("123.45");
    String[] numbers = new String[] { "123.450", "123.459", "123.45" };
    testParseCorrect(original, numbers);
  }

  private void testParseCorrect(BigDecimal original, String[] numbers) {
    for (String number : numbers) {
      String localizedNumber = getLocalizedNumberWithoutConverter(number, false);
      BigDecimal parsedNumber = BigDecimalConverter.parse(localizedNumber, true, locale);
      assertEquals(original, parsedNumber);
    }
  }

  @Test(expected = IteraplanBusinessException.class)
  public void testParseEmptyString() {
    BigDecimalConverter.parse("  ", locale);
  }

  @Test(expected = IteraplanBusinessException.class)
  public void testParseLiteralString() {
    BigDecimalConverter.parse("ABCD", locale);
  }

  @Test(expected = IteraplanBusinessException.class)
  public void testParseIncorrectFormatString() {
    BigDecimalConverter.parse("1,2,3", locale);
  }
}
