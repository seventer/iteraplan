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

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * This class is the central BigDecimal to String converter in iteraplan.
 */
public final class BigDecimalConverter implements Serializable {

  private static final long   serialVersionUID = -8727888018985718810L;

  private static final Logger LOGGER           = Logger.getIteraplanLogger(BigDecimalConverter.class);

  private static final String MAX_VALUE        = "9999999999999.99";
  private static final String MIN_VALUE        = "-" + MAX_VALUE;

  // private constructor
  private BigDecimalConverter() {
    // nothing to do
  }

  /**
   * Constructor. The locale that is passed will be used by methods in this class to convert
   * {@link BigDecimal}s to a localized String representation or to parse a localized String
   * representation to create a BigDecimal object.
   * 
   * @param locale
   *          The current locale.
   */
  public static void checkLocale(Locale locale) {
    if (locale == null) {
      LOGGER.error("BigDecimalConverter called with locale = null.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }
  }

  /**
   * Checks whether the selected locale uses a comma as decimal separator.
   * 
   * @param locale
   *          The current locale.
   * @return true, if a comma is used
   */
  public static boolean isUsingDecimalComma(Locale locale) {
    return ",".equals(getLocalizedSeparator(locale));
  }

  public static String getLocalizedSeparator(Locale locale) {
    checkLocale(locale);
    DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(locale);
    return Character.toString(format.getDecimalFormatSymbols().getDecimalSeparator());
  }
  
  /**
   * Formats a given BigDecimal with regard to the current Locale.
   * 
   * @param number
   *          The BigDecimal to format.
   * @param activateRounding
   *          If true, 2 digit FLOOR rounding is activated.
   * @param locale
   *          The current locale.
   * @return A localized String representation. If number is <code>null</code>, null is returned.
   */
  public static String format(BigDecimal number, boolean activateRounding, Locale locale) {
    if (number == null) {
      return null;
    }
    BigDecimal numberCopy;
    String result;
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("BigDecimalConverter.format of '" + number.toString() + "'");
    }
    if (activateRounding) {
      numberCopy = number.setScale(2, BigDecimal.ROUND_DOWN);
      result = numberCopy.toString();
    }
    else {
      result = number.toString();
    }

    if (isUsingDecimalComma(locale)) {
      result = result.replace('.', ',');
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("... resulted in localized String '" + result + "'.");
    }
    return result;
  }

  /**
   * Formats a {@link BigDecimal} to a string representation without rounding.
   * 
   * @param number
   *          the BigDecimal to format.
   * @param locale
   *          The current locale.
   * @return localized String representation
   */
  public static String format(BigDecimal number, Locale locale) {
    return format(number, false, locale);
  }

  /**
   * Parse a given localized number String into a BigDecimal.
   * 
   * @param localizedNumberString
   *          The localized String to parse into a BigDecimal.
   * @param activateRounding
   *          If true, 2 digit FLOOR rounding is activated.
   * @param locale
   *          The current locale.
   * @return the parsed BigDecimal
   */
  public static BigDecimal parse(String localizedNumberString, boolean activateRounding, Locale locale) {

    // first remove illegal chars from the String
    String filteredLocalizedNumberString = StringUtil.removeIllegalXMLChars(localizedNumberString);

    checkForInvalidNumberString(filteredLocalizedNumberString);

    String numberString = filteredLocalizedNumberString;
    LOGGER.debug("BigDecimalConverter.parse of '{0}'.", numberString);

    if (isUsingDecimalComma(locale)) {
      numberString = filteredLocalizedNumberString.replace(',', '.');
    }

    BigDecimal result = null;

    try {
      result = new BigDecimal(numberString);
    } catch (NumberFormatException nfex) {
      LOGGER.debug("NumberFormatException occurred...", nfex);
      throw new IteraplanBusinessException(IteraplanErrorMessages.INCORRECT_BIGDECIMAL_FORMAT, nfex, numberString, nfex.getMessage());
    }
    if (activateRounding) {
      result = result.setScale(2, BigDecimal.ROUND_DOWN);
    }
    checkForTooBigNumber(result);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("... resulted in BigDecimal '" + result.toString() + "'.");
    }
    return result;
  }

  /**
   * Values with more than 13 digits may result in problems
   * @param number
   *          {@link BigDecimal} to check
   * @throws IteraplanBusinessException if {@code number} has too many digits
   */
  private static void checkForTooBigNumber(BigDecimal number) {
    if ((number.compareTo(new BigDecimal(MAX_VALUE)) > 0) || (number.compareTo(new BigDecimal(MIN_VALUE))) < 0) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INCORRECT_BIGDECIMAL_FORMAT, number.toString());
    }
  }

  private static void checkForInvalidNumberString(String filteredLocalizedNumberString) {
    if (filteredLocalizedNumberString == null || filteredLocalizedNumberString.length() == 0 || filteredLocalizedNumberString.indexOf('e') >= 0
        || filteredLocalizedNumberString.indexOf('E') >= 0) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.INCORRECT_BIGDECIMAL_FORMAT, filteredLocalizedNumberString);
    }
  }

  /**
   * Parses a localized BigDecimal string represenation without rounding.
   * 
   * @param localizedNumberString
   *          the BigDecimal string representation
   * @return the parsed BigDecimal.
   */
  public static BigDecimal parse(String localizedNumberString, Locale locale) {
    return parse(localizedNumberString, false, locale);
  }

}
