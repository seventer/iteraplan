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
package de.iteratec.iteraplan.elasticeam.model.validator;

import java.text.MessageFormat;

import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 * Represents a violation of a lower or upper bound.
 */
public class CardinalityConstraintViolation extends FeatureConsistencyViolation {

  private UniversalModelExpression violatingExpression;
  private int                      numberOfValues;

  CardinalityConstraintViolation(FeatureExpression<?> onFeature, UniversalModelExpression violaingExpression, int numberOfValues) {
    super(onFeature);
    this.violatingExpression = violaingExpression;
    this.numberOfValues = numberOfValues;
  }

  public UniversalModelExpression getViolatingExpression() {
    return this.violatingExpression;
  }

  /**
   * @return numberOfValues the numberOfValues
   */
  public int getNumberOfValues() {
    return numberOfValues;
  }

  public String getInfoString() {
    String featureName = getFeature().getPersistentName();
    if (getFeature().getLowerBound() == 1 && getFeature().getUpperBound() == 1 && numberOfValues == 0) {
      String format = "Cardinality constraint violation: \"{0}\" of {1} has not been set, but needs a value assigned.";
      return MessageFormat.format(format, featureName, getViolatingExpressionName());
    }
    else {
      String format = "Cardinality constraint violation: \"{0}\" of {1} has {2} values assigned, but must be between {3} and {4}.";
      Integer lowerBound = Integer.valueOf(getFeature().getLowerBound());
      Integer upperBound = Integer.valueOf(getFeature().getUpperBound());
      return MessageFormat.format(format, featureName, getViolatingExpressionName(), Integer.valueOf(numberOfValues), lowerBound, upperBound);
    }
  }

  private String getViolatingExpressionName() {
    if (isNamedType()) {
      return getUniversalType().getPersistentName() + " \"" + violatingExpression.getValue(MixinTypeNamed.NAME_PROPERTY) + "\"";
    }
    else {
      return "\"" + violatingExpression.toString() + "\"";
    }
  }
}
