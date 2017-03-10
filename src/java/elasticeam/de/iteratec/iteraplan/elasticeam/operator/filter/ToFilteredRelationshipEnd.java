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
package de.iteratec.iteraplan.elasticeam.operator.filter;

import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.ConfigParameter;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.RelationshipEndImpl;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.RelationshipImpl;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 *
 */
public class ToFilteredRelationshipEnd extends RelationshipEndImpl {

  private RelationshipEndExpression baseRelationshipEnd;
  private RelationshipEndExpression opposite;

  public ToFilteredRelationshipEnd(RelationshipEndExpression baseRelationshipEnd, Predicate<UniversalModelExpression> predicate) {
    this(baseRelationshipEnd, filteredType(baseRelationshipEnd, predicate), predicate);
    this.baseRelationshipEnd = baseRelationshipEnd;
    this.opposite = new FromFilteredRelationshipEnd(this);
    ((FilteredUniversalType<?>) getType()).setFromEnd(this.opposite);
  }

  private static FilteredUniversalType<?> filteredType(RelationshipEndExpression baseRelationshipEnd, Predicate<UniversalModelExpression> predicate) {
    if (baseRelationshipEnd.getType() instanceof SubstantialTypeExpression) {
      return new FilteredSubstantialType((SubstantialTypeExpression) baseRelationshipEnd.getType(), predicate, false);
    }
    else {
      return new FilteredRelationshipType((RelationshipTypeExpression) baseRelationshipEnd.getType(), predicate, false);
    }
  }

  ToFilteredRelationshipEnd(FromFilteredRelationshipEnd opposite, RelationshipEndExpression baseRelationship) {
    this(baseRelationship, holder(opposite), holder(opposite).getPredicate());
    this.opposite = opposite;
  }

  private ToFilteredRelationshipEnd(RelationshipEndExpression baseRelationshipEnd, FilteredUniversalType<?> type,
      Predicate<UniversalModelExpression> predicate) {
    super(baseRelationshipEnd.getPersistentName() + "[" + predicate.toString() + "]", baseRelationshipEnd.getHolder(), baseRelationshipEnd
        .getOrigin(), baseRelationshipEnd.getLowerBound(), baseRelationshipEnd.getUpperBound(), type);
    this.baseRelationshipEnd = baseRelationshipEnd;
  }

  private static FilteredUniversalType<?> holder(FromFilteredRelationshipEnd opposite) {
    return (FilteredUniversalType<?>) opposite.getHolder();
  }

  @ConfigParameter
  public final Predicate<UniversalModelExpression> getPredicate() {
    return ((FilteredUniversalType<?>) getType()).getPredicate();
  }

  @ConfigParameter
  public final RelationshipEndExpression getBaseRelationshipEnd() {
    return this.baseRelationshipEnd;
  }

  /**{@inheritDoc}**/
  public RelationshipExpression getRelationship() {
    return new RelationshipImpl(baseRelationshipEnd.getRelationship().getPersistentName() + "[" + getPredicate().toString() + "]") {

      public List<RelationshipEndExpression> getRelationshipEnds() {
        return Lists.newArrayList(ToFilteredRelationshipEnd.this, ToFilteredRelationshipEnd.this.opposite);
      }
    };
  }
}
