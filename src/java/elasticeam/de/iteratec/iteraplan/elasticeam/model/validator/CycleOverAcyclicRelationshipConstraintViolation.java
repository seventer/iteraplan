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
import java.util.List;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


public class CycleOverAcyclicRelationshipConstraintViolation extends StructuralConstraintViolation {

  private RelationshipExpression        relationship;
  private Set<UniversalModelExpression> expressions;

  CycleOverAcyclicRelationshipConstraintViolation(RelationshipExpression relationship, Set<UniversalModelExpression> expressions) {
    this.relationship = relationship;
    this.expressions = expressions;
  }

  /**
   * @return relationship the relationship
   */
  public RelationshipExpression getRelationship() {
    return relationship;
  }

  /**
   * @return expressions the expressions
   */
  public Set<UniversalModelExpression> getExpressions() {
    return expressions;
  }

  public String getInfoString() {
    String format = "Cycle detected regarding the relationship ({0}) of the type \"{1}\". Affected elements are: {2}";
    return MessageFormat.format(format, getRelationshipDescription(), getUniversalType().getPersistentName(), getConcatenatedExpressionsNames());
  }

  private String getRelationshipDescription() {
    List<String> names = Lists.newArrayList();
    for (RelationshipEndExpression relEnd : relationship.getRelationshipEnds()) {
      names.add(relEnd.getPersistentName());
    }
    return Joiner.on("-").join(names);
  }

  private String getConcatenatedExpressionsNames() {
    List<String> names = Lists.newArrayList();
    for (UniversalModelExpression ume : expressions) {
      names.add(getExpressionName(ume));
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

  private UniversalTypeExpression getUniversalType() {
    // Since, when using only one relationship, cycles can only appear if the relationship is between elements of the same type,
    // we can use the first relationship end to determine the type of all ends.
    RelationshipEndExpression firestRelationshipEnd = relationship.getRelationshipEnds().get(0);
    return firestRelationshipEnd.getType();
  }

  private boolean isNamedType() {
    return getUniversalType().findPropertyByPersistentName(MixinTypeNamed.NAME_PROPERTY.getPersistentName()) != null;
  }

}
