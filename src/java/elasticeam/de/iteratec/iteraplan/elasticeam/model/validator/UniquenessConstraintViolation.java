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
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 * Represents the violation of a uniqueness constraint over a set of instances with respect to a given feature.
 */
public class UniquenessConstraintViolation extends FeatureConsistencyViolation {

  private Set<UniversalModelExpression> violatingExpressions;
  private Object                        violatingValue;

  UniquenessConstraintViolation(FeatureExpression<?> onFeature, Set<UniversalModelExpression> violatingExpressions, Object violatingValue) {
    super(onFeature);
    this.violatingExpressions = violatingExpressions;
    this.violatingValue = violatingValue;
  }

  public Set<UniversalModelExpression> getViolatingExpressions() {
    return violatingExpressions;
  }

  public Object getViolatingValue() {
    return violatingValue;
  }

  public String getInfoString() {
    if (!getFeature().getPersistentName().equals(UniversalTypeExpression.ID_PROPERTY.getPersistentName())) {
      String format = "Uniqueness constraint violation: \"{0}\" set to \"{1}\" in all following objects of type {2}: {3}";
      return MessageFormat.format(format, getFeature().getPersistentName(), violatingValue, getUniversalType().getPersistentName(),
          getConcatenatedViolatingExpressionsNames());
    }
    else {
      String format = "Uniqueness constraint violation: ID \"{0}\" set for all following objects: {1}";
      return MessageFormat.format(format, violatingValue, getConcatenatedViolatingExpressionsNames());
    }
  }

  private String getConcatenatedViolatingExpressionsNames() {
    Map<String, Integer> countedNames = Maps.newHashMap();
    for (UniversalModelExpression ume : violatingExpressions) {
      String expressionName = getExpressionName(ume);
      if (!countedNames.containsKey(expressionName)) {
        countedNames.put(expressionName, Integer.valueOf(1));
      }
      else {
        Integer number = countedNames.get(expressionName);
        countedNames.put(expressionName, Integer.valueOf(number.intValue() + 1));
      }
    }

    Set<String> names = Sets.newHashSet();
    for (Map.Entry<String, Integer> entry : countedNames.entrySet()) {
      if (entry.getValue().intValue() == 1) {
        names.add(entry.getKey());
      }
      else {
        names.add(entry.getKey() + " (" + entry.getValue() + "x)");
      }
    }
    return Joiner.on(", ").join(names);
  }

  private String getExpressionName(UniversalModelExpression ume) {
    if (isNamedType()) {
      return String.valueOf(ume.getValue(MixinTypeNamed.NAME_PROPERTY));
    }
    else {
      return ume.toString();
    }
  }
}
