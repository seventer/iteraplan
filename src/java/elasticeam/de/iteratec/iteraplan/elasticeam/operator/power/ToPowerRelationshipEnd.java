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
package de.iteratec.iteraplan.elasticeam.operator.power;

import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.ConfigParameter;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.RelationshipEndImpl;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.RelationshipImpl;


public class ToPowerRelationshipEnd extends RelationshipEndImpl {
  private RelationshipEndExpression opposite;
  private SubstantialTypeExpression baseType;

  public ToPowerRelationshipEnd(SubstantialTypeExpression baseType) {
    this(baseType, new PowerSubstantialType(baseType, false));
    this.opposite = new FromPowerRelationshipEnd((PowerSubstantialType) getType());
    ((PowerSubstantialType) getType()).setFromEnd(this.opposite);
  }

  ToPowerRelationshipEnd(FromPowerRelationshipEnd opposite) {
    this(holder(opposite).getBaseType(), holder(opposite));
    this.opposite = opposite;
  }

  private ToPowerRelationshipEnd(SubstantialTypeExpression baseType, PowerSubstantialType type) {
    super("containerOf(" + baseType.getPersistentName() + ")", baseType, 1, 1, type);
    this.baseType = baseType;
  }

  private static PowerSubstantialType holder(FromPowerRelationshipEnd opposite) {
    return (PowerSubstantialType) opposite.getHolder();
  }

  /**{@inheritDoc}**/
  public RelationshipExpression getRelationship() {
    return new RelationshipImpl("power(" + baseType.getPersistentName() + ")") {

      public List<RelationshipEndExpression> getRelationshipEnds() {
        return Lists.newArrayList(ToPowerRelationshipEnd.this, ToPowerRelationshipEnd.this.opposite);
      }
    };
  }

  @ConfigParameter
  public SubstantialTypeExpression getBaseType() {
    return this.baseType;
  }

}
