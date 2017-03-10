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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.masterplan;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;


public final class RelatedBBTypeHelper {
  
  private static final Logger LOGGER = Logger.getIteraplanLogger(RelatedBBTypeHelper.class);
  
  private RelatedBBTypeHelper() {
  }

  public static String getBBType(String relatedType) {
    if (relatedType.contains("technicalComponentRelease")) {
      return Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL;
    } else if (relatedType.contains("architecturalDomain")) {
      return Constants.BB_ARCHITECTURALDOMAIN_PLURAL;
    } else if (relatedType.contains("informationSystemRelease")) {
      return Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL;
    } else if (relatedType.contains("businessDomain")) {
      return Constants.BB_BUSINESSDOMAIN_PLURAL;
    } else if (relatedType.contains("business_function")) {
      return Constants.BB_BUSINESSFUNCTION_PLURAL;
    } else if (relatedType.contains("businessUnit")) {
      return Constants.BB_BUSINESSUNIT_PLURAL;
    } else if (relatedType.contains("businessObject")) {
      return Constants.BB_BUSINESSOBJECT_PLURAL;
    } else if (relatedType.contains("businessProcess")) {
      return Constants.BB_BUSINESSPROCESS_PLURAL;
    } else if (relatedType.contains("infrastructureElement")) {
      return Constants.BB_INFRASTRUCTUREELEMENT_PLURAL;
    } else if (relatedType.contains("informationSystemDomain")) {
      return Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL;
    } else if (relatedType.contains("product")) {
      return Constants.BB_PRODUCT_PLURAL;
    } else if (relatedType.contains("project")) {
      return Constants.BB_PROJECT_PLURAL;
    } 
    LOGGER.error("no association found for related type: " + relatedType);
    return null;
  }
}
