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
package de.iteratec.iteraplan.elasticeam.operator.join;

import static de.iteratec.iteraplan.elasticeam.util.CardinalityUtil.multiply;

import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.ConfigParameter;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.RelationshipEndImpl;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.RelationshipImpl;


/**
 *
 */
public class JoinedRelationshipEnd extends RelationshipEndImpl {

  public static RelationshipEndExpression join(List<RelationshipEndExpression> relationshipEnds) {
    if (relationshipEnds == null) {
      return null;
    }
    else if (relationshipEnds.size() < 2) {
      return relationshipEnds.get(0);
    }
    else if (relationshipEnds.size() == 2) {
      return new JoinedRelationshipEnd(relationshipEnds.get(0), relationshipEnds.get(1));
    }
    else {
      return new JoinedRelationshipEnd(relationshipEnds.remove(0), join(relationshipEnds));
    }
  }

  private RelationshipEndExpression end0;
  private RelationshipEndExpression end1;

  private RelationshipEndExpression opposite;

  /**
   * Default constructor.
   */
  public JoinedRelationshipEnd(RelationshipEndExpression end0, RelationshipEndExpression end1) {
    super(persistentName(end0, end1), end0.getHolder(), multiply(end0.getLowerBound(), end1.getLowerBound()), multiply(end0.getUpperBound(),
        end1.getUpperBound()), end1.getType());
    this.end0 = end0;
    this.end1 = end1;
    this.opposite = new JoinedRelationshipEnd(this);
  }

  private JoinedRelationshipEnd(JoinedRelationshipEnd opposite) {
    super(oppositePN(opposite), opposite.getType(), multiply(opp(opposite.end1).getLowerBound(), opp(opposite.end0).getLowerBound()), multiply(
        opp(opposite.end1).getUpperBound(), opp(opposite.end0).getUpperBound()), opposite.getHolder());
    this.opposite = opposite;
    this.end0 = opp(opposite.end1);
    this.end1 = opp(opposite.end0);
  }

  private static String oppositePN(JoinedRelationshipEnd opposite) {
    return persistentName(opp(opposite.end1), opp(opposite.end0));
  }

  private static String persistentName(RelationshipEndExpression end0, RelationshipEndExpression end1) {
    StringBuffer result = new StringBuffer();
    result.append(end0.getPersistentName());
    if (!end1.getPersistentName().startsWith("/")) {
      result.append('/');
    }
    result.append(end1.getPersistentName());
    return result.toString();
  }

  private static RelationshipEndExpression opp(RelationshipEndExpression end) {
    return end.getRelationship().getOppositeEndFor(end);
  }

  /**{@inheritDoc}**/
  public RelationshipExpression getRelationship() {
    return new RelationshipImpl("join(" + end0.getRelationship().getPersistentName() + "," + end1.getRelationship().getPersistentName() + ")") {

      public List<RelationshipEndExpression> getRelationshipEnds() {
        return Lists.newArrayList(JoinedRelationshipEnd.this, JoinedRelationshipEnd.this.opposite);
      }
    };
  }

  @ConfigParameter
  public RelationshipEndExpression getEnd0() {
    return end0;
  }

  @ConfigParameter
  public RelationshipEndExpression getEnd1() {
    return end1;
  }
}