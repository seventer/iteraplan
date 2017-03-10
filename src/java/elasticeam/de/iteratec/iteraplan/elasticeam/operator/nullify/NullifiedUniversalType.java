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

import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.ConfigParameter;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.PropertyImpl;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.UniversalTypeImpl;


public abstract class NullifiedUniversalType<B extends UniversalTypeExpression> extends UniversalTypeImpl {

  private UniversalTypeExpression         baseType;
  private List<RelationshipEndExpression> fromEnds;

  public NullifiedUniversalType(UniversalTypeExpression baseType, boolean createFromEnd) {
    super("nullified(" + baseType.getPersistentName() + ")");
    this.baseType = baseType;
    if (createFromEnd) {
      this.fromEnds = createFromEnds(null);
      init();
    }
  }

  private List<RelationshipEndExpression> createFromEnds(RelationshipEndExpression existingFromEnd) {
    List<RelationshipEndExpression> result = Lists.newArrayList();
    for (RelationshipEndExpression rend : this.baseType.getRelationshipEnds()) {
      if (!rend.equals(existingFromEnd)) {
        result.add(new FromNullifiedRelationshipEnd(this, rend, true));
      }
    }
    return result;
  }

  final void setFromEnd(RelationshipEndExpression fromEnd) {
    this.fromEnds = createFromEnds(fromEnd);
    this.fromEnds.add(fromEnd);
    init();
  }

  @ConfigParameter
  @SuppressWarnings("unchecked")
  public final B getBaseType() {
    return (B) baseType;
  }

  /**{@inheritDoc}**/
  public List<FeatureExpression<?>> getFeatures() {
    List<FeatureExpression<?>> result = Lists.newLinkedList();
    for (PropertyExpression<?> property : this.baseType.getProperties()) {
      result.add(clone(property));
    }
    result.addAll(fromEnds);
    return result;
  }

  private PropertyInNullifiedUniversalType clone(PropertyExpression<?> ppe) {
    return new PropertyInNullifiedUniversalType(ppe.getPersistentName(), this, ppe.getOrigin(), ppe.getLowerBound(), ppe.getUpperBound(),
        ppe.getType());
  }

  static class PropertyInNullifiedUniversalType extends PropertyImpl<DataTypeExpression> {

    public PropertyInNullifiedUniversalType(String persistentName, UniversalTypeExpression holder, UniversalTypeExpression origin, int lower,
        int upper, DataTypeExpression type) {
      super(persistentName, holder, origin, lower, upper, type);
    }
  }
}
