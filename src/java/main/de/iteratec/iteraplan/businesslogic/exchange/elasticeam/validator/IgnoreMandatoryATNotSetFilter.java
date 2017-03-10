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

import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.validator.CardinalityConstraintViolation;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelConsistencyViolation;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelValidatorResult;


/**
 * Filter/Wrapper for {@link ModelValidatorResult}s to ignore cardinality violations caused by mandatory attributes not being set 
 */
public class IgnoreMandatoryATNotSetFilter implements ModelValidatorResult {

  private final ModelValidatorResult           wrapped;
  private final IteraplanMapping               mapping;
  private final Set<ModelConsistencyViolation> filteredViolations;

  public IgnoreMandatoryATNotSetFilter(ModelValidatorResult toWrap, IteraplanMapping iteraplanMapping) {
    this.wrapped = toWrap;
    this.mapping = iteraplanMapping;
    this.filteredViolations = Sets.newHashSet();
    for (ModelConsistencyViolation violation : wrapped.getViolations()) {
      if (isRelevantViolation(violation)) {
        filteredViolations.add(violation);
      }
    }
  }

  public Metamodel getMetamodel() {
    return wrapped.getMetamodel();
  }

  public Model getModel() {
    return wrapped.getModel();
  }

  public void addViolation(ModelConsistencyViolation violation) {
    wrapped.addViolation(violation);
    if (isRelevantViolation(violation)) {
      filteredViolations.add(violation);
    }
  }

  public Set<ModelConsistencyViolation> getViolations() {
    return filteredViolations;
  }

  private boolean isRelevantViolation(ModelConsistencyViolation violation) {
    if (violation instanceof CardinalityConstraintViolation) {
      FeatureExpression<?> feature = ((CardinalityConstraintViolation) violation).getFeature();
      int numberOfValues = ((CardinalityConstraintViolation) violation).getNumberOfValues();

      boolean isMandatoryNotSetViolation = numberOfValues == 0 && feature.getLowerBound() == 1;
      boolean isAttributeType = feature instanceof PropertyExpression && mapping.isDerivedFromAT((PropertyExpression<?>) feature);

      return !(isAttributeType && isMandatoryNotSetViolation);
    }
    else {
      return true;
    }
  }
}
