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

import java.util.Map;
import java.util.Observable;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.elasticeam.metamodel.MixinTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;


/**
 * A mixin which describes a name and a description property.
 */
public final class MixinTypeNamed extends BuiltinUniversalType {

  public static final MixinTypeNamed              INSTANCE;
  public static final PrimitivePropertyExpression DESCRIPTION_PROPERTY;
  public static final PrimitivePropertyExpression NAME_PROPERTY;
  //public static final PropertyExpression FQN_PROPERTY;

  static {
    INSTANCE = new MixinTypeNamed();
    NAME_PROPERTY = new BuiltinPrimitiveProperty("name", 1, 1, BuiltinPrimitiveType.STRING, INSTANCE);
    INSTANCE.add(NAME_PROPERTY);
    DESCRIPTION_PROPERTY = new BuiltinPrimitiveProperty("description", 0, 1, BuiltinPrimitiveType.STRING, INSTANCE);
    INSTANCE.add(DESCRIPTION_PROPERTY);
    //FQN_PROPERTY = new BuiltinPrimitiveProperty("fully qualified name", 1, 1, String.class, INSTANCE);
    //INSTANCE.add(FQN_PROPERTY);
  }

  private MixinTypeNamed() {
    super("NamedElement");
  }

  /**{@inheritDoc}**/
  public void update(Observable o, Object arg) {
    // TODO Does this make sense?
  }

  /**{@inheritDoc}**/
  public Class<? extends NamedExpression> getMetaType() {
    return MixinTypeExpression.class;
  }

  /**{@inheritDoc}**/
  public Map<RelationshipEndExpression, RelationshipEndExpression> getRelationshipEndPairs() {
    //this mixin doesn't define any relationshipEnds
    return Maps.newLinkedHashMap();
  }
}
