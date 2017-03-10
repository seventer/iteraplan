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
package de.iteratec.iteraplan.businesslogic.exchange.elasticeam.validator;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelConsistencyViolation;


public class DuplicateBusinessMappingsViolation implements ModelConsistencyViolation {

  private final Set<UniversalModelExpression> duplicateBMs;
  private final String                        bmDescription;
  private final boolean                       partialBusinessMapping;

  public DuplicateBusinessMappingsViolation(Set<UniversalModelExpression> duplicates, String bmDescription, boolean partial) {
    this.duplicateBMs = duplicates;
    this.bmDescription = bmDescription;
    this.partialBusinessMapping = partial;
  }

  public Set<UniversalModelExpression> getDuplicateBMExpressions() {
    return this.duplicateBMs;
  }

  public String getInfoString() {
    String format = null;
    if (!partialBusinessMapping) {
      format = "The BusinessMapping {0} exists several times with the following IDs: {1}";
    }
    else {
      format = "Due to permission restrictions it cannot be determined whether the BusinessMappings of the format {0} are duplicates. Affected IDs: {1}";
    }
    Set<Integer> ids = getIdSetFromExpressions(duplicateBMs);
    return MessageFormat.format(format, bmDescription, ids);
  }

  private Set<Integer> getIdSetFromExpressions(Collection<UniversalModelExpression> expressions) {
    Function<UniversalModelExpression, Integer> expressionToIdFunction = new Function<UniversalModelExpression, Integer>() {
      public Integer apply(UniversalModelExpression ume) {
        BigInteger bigInteger = (BigInteger) ume.getValue(UniversalTypeExpression.ID_PROPERTY);
        if (bigInteger == null) {
          return null;
        }
        else {
          return Integer.valueOf(bigInteger.intValue());
        }
      }
    };

    return Sets.newHashSet(Iterables.transform(expressions, expressionToIdFunction));
  }
}
