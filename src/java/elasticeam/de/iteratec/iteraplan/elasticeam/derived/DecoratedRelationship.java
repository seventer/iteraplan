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
package de.iteratec.iteraplan.elasticeam.derived;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.ElasticeamContext;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


/**
 *
 */
final class DecoratedRelationship extends DecoratedNamedElement<RelationshipExpression> implements RelationshipExpression {

  private final Function<UniversalTypeExpression, UniversalTypeExpression>     replacer;
  private final Function<RelationshipEndExpression, RelationshipEndExpression> reReplacer = new Function<RelationshipEndExpression, RelationshipEndExpression>() {
                                                                                            public RelationshipEndExpression apply(RelationshipEndExpression input) {
                                                                                              return input == null
                                                                                                  || input instanceof DecoratedRelationshipEnd ? input
                                                                                                  : new DecoratedRelationshipEnd(input, replacer);
                                                                                            }
                                                                                          };

  DecoratedRelationship(RelationshipExpression wrapped, Function<UniversalTypeExpression, UniversalTypeExpression> replacer) {
    super(wrapped);
    this.replacer = replacer;
  }

  /**{@inheritDoc}**/
  public List<RelationshipEndExpression> getRelationshipEnds() {
    return Lists.transform(getWrapped().getRelationshipEnds(), this.reReplacer);
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression findRelationshipEndByName(String name) {
    return this.reReplacer.apply(getWrapped().findRelationshipEndByName(name));
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression getOppositeEndFor(RelationshipEndExpression relationshipEnd) {
    if (relationshipEnd instanceof DecoratedRelationshipEnd) {
      return this.reReplacer.apply(getWrapped().getOppositeEndFor(((DecoratedRelationshipEnd) relationshipEnd).getWrapped()));
    }
    else {
      return this.reReplacer.apply(getWrapped().getOppositeEndFor(relationshipEnd));
    }
  }

  /**{@inheritDoc}**/
  public List<RelationshipEndExpression> getRelationshipEnds(ElasticeamContext ctx) {
    return getRelationshipEnds();
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression findRelationshipEndByName(ElasticeamContext ctx, String name) {
    return findRelationshipEndByName(name);
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression findRelationshipEndByPersistentName(String name) {
    return this.reReplacer.apply(getWrapped().findRelationshipEndByPersistentName(name));
  }

  /**{@inheritDoc}**/
  public RelationshipEndExpression getEndLeadingTo(UniversalTypeExpression type) {
    return this.reReplacer.apply(getWrapped().getEndLeadingTo(type));
  }

  /**{@inheritDoc}**/
  public Class<? extends NamedExpression> getMetaType() {
    return RelationshipExpression.class;
  }

  /**{@inheritDoc}**/
  public boolean isAcyclic() {
    return getWrapped().isAcyclic();
  }
}