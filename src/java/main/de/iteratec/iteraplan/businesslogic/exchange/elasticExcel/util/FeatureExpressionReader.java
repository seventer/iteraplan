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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Parser for a feature type expression, i.e an expression in the feature type header (column header) of an import/export excel sheet.
 * 
 * Example: <codename[1..1]:java.lang.String</code>
 */
public class FeatureExpressionReader {

  private static final Logger  LOGGER              = Logger.getIteraplanLogger(FeatureExpressionReader.class);

  private static final Pattern PATTERN = Pattern.compile("(.*)\\[(\\d+)\\.\\.(.*)\\]:(.*)");

  private String               persistentName;
  private int                  lowerBound;
  private int                  upperBound;
  private String               typeName;

  /**
   * Constructor.
   * @param expr feature type expression to parse.
   */
  @SuppressWarnings("boxing")
  public FeatureExpressionReader(String expr) {

    Matcher m = PATTERN.matcher(expr);

    if (m.matches()) {
      persistentName = m.group(1);
      lowerBound = Integer.valueOf(m.group(2));

      String upper = m.group(3);
      if ("*".equals(upper)) {
        upperBound = Integer.MAX_VALUE;
      }
      else {
        upperBound = Integer.valueOf(upper);
      }
      typeName = m.group(4);
    }
    else {
      LOGGER.error("Invalid feature expression: {0}", expr);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GENERAL_TECHNICAL_ERROR, "Wrong type input " + expr);
    }
  }

  /**
   * @return persistentName the persistentName
   */
  public String getPersistentName() {
    return persistentName;
  }

  /**
   * @return lowerBound the lowerBound
   */
  public int getLowerBound() {
    return lowerBound;
  }

  /**
   * @return upperBound the upperBound
   */
  public int getUpperBound() {
    return upperBound;
  }

  /**
   * @return typeName the typeName
   */
  public String getTypeName() {
    return typeName;
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("boxing")
  @Override
  public String toString() {
    String upper = (upperBound < Integer.MAX_VALUE) ? Integer.toString(upperBound) : "*";
    return String.format("%s[%d..%s]:%s", persistentName, lowerBound, upper, typeName);
  }
}
