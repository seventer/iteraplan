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
package de.iteratec.iteraplan.elasticeam.metamodel.builtin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import de.iteratec.iteraplan.elasticeam.metamodel.ComparisonOperatorExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.FinalNamedExpressionImpl;
import de.iteratec.iteraplan.model.RuntimePeriod;


/**
 * A primitive type available in elasticEAM.
 */
public class BuiltinPrimitiveType extends FinalNamedExpressionImpl implements PrimitiveTypeExpression {

  public static final Set<PrimitiveTypeExpression> BUILTIN_PRIMITIVE_TYPES = new HashSet<PrimitiveTypeExpression>();

  public static final PrimitiveTypeExpression      STRING                  = new BuiltinPrimitiveType(String.class,
                                                                               ComparisonOperators.getAvailableComparisonOperators(String.class));
  public static final PrimitiveTypeExpression      INTEGER                 = new BuiltinPrimitiveType(BigInteger.class,
                                                                               ComparisonOperators.getAvailableComparisonOperators(BigInteger.class));
  public static final PrimitiveTypeExpression      DECIMAL                 = new BuiltinPrimitiveType(BigDecimal.class,
                                                                               ComparisonOperators.getAvailableComparisonOperators(BigDecimal.class));
  public static final PrimitiveTypeExpression      DATE                    = new BuiltinPrimitiveType(Date.class,
                                                                               ComparisonOperators.getAvailableComparisonOperators(Date.class));
  public static final PrimitiveTypeExpression      BOOLEAN                 = new BuiltinPrimitiveType(Boolean.class,
                                                                               ComparisonOperators.getAvailableComparisonOperators(Boolean.class));
  public static final PrimitiveTypeExpression      DURATION                = new BuiltinPrimitiveType(RuntimePeriod.class,
                                                                               ComparisonOperators
                                                                                   .getAvailableComparisonOperators(RuntimePeriod.class));
  //datetime, money  

  private Class<?>                                 dataType;

  private Set<ComparisonOperatorExpression>        comparsionOperators;

  public BuiltinPrimitiveType(Class<?> clazz, Set<ComparisonOperatorExpression> comparisonOperators) {
    this.dataType = clazz;
    this.comparsionOperators = comparisonOperators;
    BUILTIN_PRIMITIVE_TYPES.add(this);
  }

  /**{@inheritDoc}**/
  public String getPersistentName() {
    return dataType.getName();
  }

  /**{@inheritDoc}**/
  public int compareTo(DataTypeExpression o) {
    if (o instanceof PrimitiveTypeExpression) {
      return this.getPersistentName().compareTo(o.getPersistentName());
    }
    else {
      return -1;
    }
  }

  /**{@inheritDoc}**/
  public Class<?> getEncapsulatedType() {
    return dataType;
  }

  /**{@inheritDoc}**/
  public Class<? extends NamedExpression> getMetaType() {
    return PrimitiveTypeExpression.class;
  }

  /**{@inheritDoc}**/
  public Set<ComparisonOperatorExpression> getComparisonOperators() {
    return comparsionOperators;
  }
}
