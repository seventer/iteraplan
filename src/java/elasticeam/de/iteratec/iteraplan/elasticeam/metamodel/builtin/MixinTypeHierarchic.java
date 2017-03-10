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
package de.iteratec.iteraplan.elasticeam.metamodel.builtin;

import java.util.Collections;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.MixinTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;


/**
 * A mixin which describes the hierarchy relationship.
 */
public final class MixinTypeHierarchic extends BuiltinUniversalType implements SubstantialTypeExpression {

  public static final MixinTypeHierarchic                                       INSTANCE;
  public static final RelationshipEndExpression                                 PARENT;
  public static final RelationshipEndExpression                                 CHILDREN;
  public static final Map<RelationshipEndExpression, RelationshipEndExpression> PARENT_CHILDREN;

  static {
    INSTANCE = new MixinTypeHierarchic();
    PARENT = new BuiltinRelationshipEnd("parent", 0, 1, INSTANCE, INSTANCE);
    INSTANCE.add(PARENT);
    CHILDREN = new BuiltinRelationshipEnd("children", 0, FeatureExpression.UNLIMITED, INSTANCE, INSTANCE);
    INSTANCE.add(CHILDREN);
    PARENT_CHILDREN = Maps.newLinkedHashMap();
    PARENT_CHILDREN.put(PARENT, CHILDREN);
  }

  private MixinTypeHierarchic() {
    super("HierarchicElement");
  }

  /**{@inheritDoc}**/
  public Set<MixinTypeExpression> getMixins() {
    return Collections.emptySet();
  }

  /**{@inheritDoc}**/
  public void update(Observable o, Object arg) {
    // TODO does this make sense?
  }

  /**{@inheritDoc}**/
  public Class<? extends NamedExpression> getMetaType() {
    return MixinTypeExpression.class;
  }

  /**{@inheritDoc}**/
  public Map<RelationshipEndExpression, RelationshipEndExpression> getRelationshipEndPairs() {
    return PARENT_CHILDREN;
  }
}
