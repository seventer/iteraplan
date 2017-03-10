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
package de.iteratec.iteraplan.elasticeam.operator.nullify;

import de.iteratec.iteraplan.elasticeam.metamodel.ConfigParameter;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.RelationshipEndImpl;


public final class FromNullifiedRelationshipEnd extends RelationshipEndImpl {
  private RelationshipEndExpression opposite;

  FromNullifiedRelationshipEnd(ToNullifiedRelationshipEnd opposite) {
    this(opposite.getBaseRelationshipEnd().getRelationship().getOppositeEndFor(opposite.getBaseRelationshipEnd()), type(opposite));
    this.opposite = opposite;
  }

  FromNullifiedRelationshipEnd(NullifiedUniversalType<?> holder, RelationshipEndExpression baseRelationshipEnd, boolean createOpposite) {
    this(baseRelationshipEnd, holder);
    if (createOpposite) {
      this.opposite = new ToNullifiedRelationshipEnd(this, baseRelationshipEnd.getRelationship().getOppositeEndFor(baseRelationshipEnd));
    }
  }

  @ConfigParameter
  public RelationshipEndExpression getBaseRelationshipEnd() {
    return ((ToNullifiedRelationshipEnd) opposite).getBaseRelationshipEnd().getRelationship()
        .getOppositeEndFor(((ToNullifiedRelationshipEnd) opposite).getBaseRelationshipEnd());
  }

  private FromNullifiedRelationshipEnd(RelationshipEndExpression nullified, NullifiedUniversalType<?> holder) {
    super(nullified.getPersistentName(), holder, 0, FeatureExpression.UNLIMITED, nullified.getHolder());
  }

  private static NullifiedUniversalType<?> type(ToNullifiedRelationshipEnd opposite) {
    return (NullifiedUniversalType<?>) opposite.getType();
  }

  /**{@inheritDoc}**/
  public RelationshipExpression getRelationship() {
    return this.opposite.getRelationship();
  }
}
