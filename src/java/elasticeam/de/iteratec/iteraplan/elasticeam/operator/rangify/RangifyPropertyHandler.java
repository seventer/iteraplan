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
package de.iteratec.iteraplan.elasticeam.operator.rangify;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.PropertyHandler;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedHandler;
import de.iteratec.iteraplan.elasticeam.operator.rangify.RangifyingEnumeration.RangifyingEnumerationLiteral;


public final class RangifyPropertyHandler extends ADerivedHandler implements PropertyHandler {

  public RangifyPropertyHandler(Model model) {
    super(model);
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("unchecked")
  public Object getValue(UniversalModelExpression expression, PropertyExpression<?> property) {
    RangifiedProperty rProperty = (RangifiedProperty) property;

    if (property.getUpperBound() == 1) {
      return findLiteral(getModel().getValue(expression, rProperty.getBaseProperty()), rProperty.getType().getLiterals());
    }
    else {
      return findLiterals((Collection<Object>) getModel().getValue(expression, rProperty.getBaseProperty()), rProperty.getType().getLiterals());
    }
  }

  /**{@inheritDoc}**/
  public void setValue(UniversalModelExpression expression, PropertyExpression<?> property, Object value) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public boolean isHandlerFor(PropertyExpression<?> property) {
    return RangifiedProperty.class.isInstance(property);
  }

  private static List<EnumerationLiteralExpression> findLiterals(Collection<Object> values, List<EnumerationLiteralExpression> literals) {
    List<EnumerationLiteralExpression> result = Lists.newLinkedList();
    for (Object value : values) {
      EnumerationLiteralExpression literal = findLiteral(value, literals);
      if (literal != null) {
        result.add(literal);
      }
    }
    return result;
  }

  private static EnumerationLiteralExpression findLiteral(Object value, List<EnumerationLiteralExpression> literals) {
    return findCorrespondingLiteral(getBigDecimalRepresentation(value), literals);
  }

  private static EnumerationLiteralExpression findCorrespondingLiteral(BigDecimal value, List<EnumerationLiteralExpression> literals) {
    for (EnumerationLiteralExpression candidate : literals) {
      if (((RangifyingEnumerationLiteral) candidate).getRange().contains(value)) {
        return candidate;
      }
    }
    return null;
  }

  private static BigDecimal getBigDecimalRepresentation(Object val) {
    if (BigDecimal.class.isInstance(val)) {
      return (BigDecimal) val;
    }
    else if (BigInteger.class.isInstance(val)) {
      return BigDecimal.valueOf(((BigInteger) val).longValue());
    }
    else {
      throw new ModelException(ModelException.INCONSISTENT_RANGES,
          "The value of a property which is rangified is not numeric, or the data type is not supported.");
    }
  }
}
