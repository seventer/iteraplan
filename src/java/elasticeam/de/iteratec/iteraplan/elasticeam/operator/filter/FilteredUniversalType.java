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
import java.util.Locale;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.ConfigParameter;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.PropertyImpl;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.UniversalTypeImpl;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 *
 */
public abstract class FilteredUniversalType<T extends UniversalTypeExpression> extends UniversalTypeImpl {

  private T                                   baseType;
  private Predicate<UniversalModelExpression> predicate;
  private List<RelationshipEndExpression>     relationshipEnds;

  public FilteredUniversalType(T baseType, Predicate<UniversalModelExpression> predicate, boolean createFromEnd) {
    super(baseType.getPersistentName() + "[" + predicate.toString() + "]");
    this.baseType = baseType;
    this.predicate = predicate;
    if (createFromEnd) {
      //No predefined from end
      this.relationshipEnds = createFromEnds(null);
      init();
    }
  }

  private List<RelationshipEndExpression> createFromEnds(RelationshipEndExpression existingFromEnd) {
    List<RelationshipEndExpression> result = Lists.newArrayList();
    for (RelationshipEndExpression rend : this.baseType.getRelationshipEnds()) {
      if (!rend.equals(existingFromEnd)) {
        result.add(new FromFilteredRelationshipEnd(this, rend, true));
      }
    }
    return result;
  }

  final void setFromEnd(RelationshipEndExpression fromEnd) {
    this.relationshipEnds = createFromEnds(fromEnd);
    this.relationshipEnds.add(fromEnd);
    init();
  }

  @ConfigParameter
  public final Predicate<UniversalModelExpression> getPredicate() {
    return predicate;
  }

  @ConfigParameter
  public T getBaseType() {
    return this.baseType;
  }

  public String getName(Locale locale) {
    return baseType.getName(locale) + "[" + predicate.toString() + "]";
  }

  /**{@inheritDoc}**/
  public List<FeatureExpression<?>> getFeatures() {
    List<FeatureExpression<?>> result = Lists.newLinkedList();
    for (PropertyExpression<?> prop : this.baseType.getProperties()) {
      result.add(clone(prop));
    }
    result.addAll(this.relationshipEnds);
    return result;
  }

  private PropertyInFilteredUniversalType clone(PropertyExpression<?> ppe) {
    return new PropertyInFilteredUniversalType(ppe.getPersistentName(), this, ppe.getOrigin(), ppe.getLowerBound(), ppe.getUpperBound(),
        ppe.getType());
  }

  static class PropertyInFilteredUniversalType extends PropertyImpl<DataTypeExpression> {
    public PropertyInFilteredUniversalType(String persistentName, UniversalTypeExpression holder, UniversalTypeExpression origin, int lower,
        int upper, DataTypeExpression type) {
      super(persistentName, holder, origin, lower, upper, type);
    }
  }
}
