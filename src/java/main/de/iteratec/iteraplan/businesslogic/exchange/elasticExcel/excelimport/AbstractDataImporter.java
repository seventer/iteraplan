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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.excelimport;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;


public abstract class AbstractDataImporter {
  private static final Logger LOGGER        = Logger.getIteraplanLogger(AbstractDataImporter.class);

  private List<String>        errorMessages = new ArrayList<String>();

  /**
   * @return list of all error messages. May be empty (i.e. no errors), but never null.
   */
  public List<String> getErrorMessages() {
    return errorMessages;
  }

  protected boolean isReleaseName(UniversalTypeExpression entityType, FeatureExpression<?> featureExpression) {
    boolean isReleaseType = "InformationSystem".equals(entityType.getPersistentName()) || "TechnicalComponent".equals(entityType.getPersistentName());
    boolean isNameProperty = MixinTypeNamed.NAME_PROPERTY.getPersistentName().equals(featureExpression.getPersistentName());
    return isReleaseType && isNameProperty;
  }

  protected String getNormalizedReleaseName(Cell cell) {
    String nameString = ExcelUtils.getStringCellValue(cell);
    String[] nameParts = nameString.split("#");
    if (nameParts.length == 1) {
      return StringUtils.trim(nameParts[0]);
    }
    else if (nameParts.length == 2) {
      if ("".equals(StringUtils.trim(nameParts[1]))) {
        return StringUtils.trim(nameParts[0]);
      }
      else {
        return StringUtils.trim(nameParts[0]) + " # " + StringUtils.trim(nameParts[1]);
      }
    }
    else {
      logError("Cell {0}: Invalid name \"{1}\"", ExcelUtils.getFullCellName(cell), nameString);
      return "";
    }
  }

  protected void logError(Logger logger, String format, Object... params) {
    String message = MessageFormat.format(format, params);
    logger.error(message);
    errorMessages.add(message);
  }

  private void logError(String format, Object... params) {
    logError(LOGGER, format, params);
  }

}
