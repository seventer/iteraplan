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
package de.iteratec.iteraplan.businesslogic.reports.query.node;

import java.math.BigDecimal;
import java.util.Locale;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.BigDecimalConverter;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.NumberAV;


/**
 * Leaf node for number attribute types.
 */
public final class NumberAttributeLeafNode extends AttributeLeafNode {

  private static final Logger LOGGER = Logger.getIteraplanLogger(NumberAttributeLeafNode.class);

  /**
   * Factory method for creating an instance of this class.
   * 
   * @param resultType The overall result type of the query.
   * @param extension The extension that the query is part of.
   * @param id The ID of the attribute this node represents.
   * @param comparator The comparator literal to use for the database query.
   * @param pattern The pattern to query for.
   * @param locale The locale to use for parsing the pattern.
   * @return An instance of {@code NumberAttributeLeafNode}.
   */
  public static NumberAttributeLeafNode createNode(Type<?> resultType, Extension extension, int id, Comparator comparator, String pattern, Locale locale) {
    LOGGER.debug("Creating a leaf node for a number attribute type.");

    if (comparator != Comparator.NO_ASSIGNMENT && comparator != Comparator.ANY_ASSIGNMENT) {
      LOGGER.debug("Pattern used for parsing: {1}", pattern);

      BigDecimal number = BigDecimalConverter.parse(pattern, locale);
      LOGGER.debug("Number parsed: {1}", number);

      return new NumberAttributeLeafNode(resultType, extension, id, comparator, number);
    } 
    else {
      return new NumberAttributeLeafNode(resultType, extension, id, comparator, null);
    }
  }

  private NumberAttributeLeafNode(Type<?> resultType, Extension extension, int id, Comparator comparator, Object pattern) {
    super(resultType, extension, id, comparator, pattern);
  }

  /** {@inheritDoc} */
  @Override
  protected Criterion getCriterionForComparator() {
    String value = "value";
    
    Criterion criterion = null;
    switch (getComparator()) {
      case GT:
        criterion = Restrictions.gt(value, getPattern());
        break;
      case GEQ:
        criterion = Restrictions.ge(value, getPattern());
        break;
      case EQ:
        criterion = Restrictions.eq(value, getPattern());
        break;
      case LEQ:
        criterion = Restrictions.le(value, getPattern());
        break;
      case LT:        
        criterion = Restrictions.lt(value, getPattern());
        break;
      case ANY_ASSIGNMENT:
        criterion = Restrictions.naturalId();
        break;      
      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    
    return criterion;
  }
  
  /** {@inheritDoc} */
  @Override
  protected Class<? extends AttributeValue> getAttributeValueClass() {    
    return NumberAV.class;
  }
}