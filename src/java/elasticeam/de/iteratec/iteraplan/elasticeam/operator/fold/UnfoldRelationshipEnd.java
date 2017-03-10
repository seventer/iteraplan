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
package de.iteratec.iteraplan.elasticeam.operator.fold;

import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.ConfigParameter;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.RelationshipEndImpl;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.RelationshipImpl;


public final class UnfoldRelationshipEnd extends RelationshipEndImpl {

  private RelationshipEndExpression baseRelationshipEnd;
  private RelationshipEndExpression opposite;

  public UnfoldRelationshipEnd(RelationshipEndExpression baseRelationshipEnd) {
    this(baseRelationshipEnd, true);
  }

  private UnfoldRelationshipEnd(UnfoldRelationshipEnd opposite) {
    this(opposite.baseRelationshipEnd.getRelationship().getOppositeEndFor(opposite.baseRelationshipEnd), false);
    this.opposite = opposite;
  }

  private UnfoldRelationshipEnd(RelationshipEndExpression baseRelationshipEnd, boolean createOpposite) {
    super("unfold(" + baseRelationshipEnd.getPersistentName() + ")", baseRelationshipEnd.getHolder(), baseRelationshipEnd.getLowerBound(),
        FeatureExpression.UNLIMITED, baseRelationshipEnd.getType());
    //FIXME enable again, when compatibility relationship between universal types is defined.
    //    if (!baseRelationshipEnd.getOrigin().equals(baseRelationshipEnd.getType())) {
    //      throw new ModelException(ModelException.GENERAL_ERROR, "Can not create an unfold relationship end for non-cyclic relationship end "
    //          + baseRelationshipEnd);
    //    }
    this.baseRelationshipEnd = baseRelationshipEnd;
    if (createOpposite) {
      this.opposite = new UnfoldRelationshipEnd(this);
    }
  }

  /**{@inheritDoc}**/
  public RelationshipExpression getRelationship() {
    return new RelationshipImpl("unfoldClosure(" + baseRelationshipEnd.getRelationship().getPersistentName() + ")") {

      public List<RelationshipEndExpression> getRelationshipEnds() {
        return Lists.newArrayList(UnfoldRelationshipEnd.this, UnfoldRelationshipEnd.this.opposite);
      }
    };
  }

  @ConfigParameter
  public RelationshipEndExpression getBaseRelationshipEnd() {
    return baseRelationshipEnd;
  }
}
