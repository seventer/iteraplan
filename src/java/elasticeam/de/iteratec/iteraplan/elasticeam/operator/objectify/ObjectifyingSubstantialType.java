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
package de.iteratec.iteraplan.elasticeam.operator.objectify;

import java.util.List;
import java.util.Locale;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.metamodel.ConfigParameter;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.metamodel.derived.DerivedNamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.PrimitivePropertyImpl;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.SubstantialTypeImpl;


/**
 *
 */
public final class ObjectifyingSubstantialType extends SubstantialTypeImpl implements DerivedNamedExpression {

  private PropertyExpression<?>       objectifiedProperty;
  private PrimitivePropertyExpression id;
  private PrimitivePropertyExpression name;
  private PrimitivePropertyExpression description;
  private RelationshipEndExpression   fromEnd;

  public ObjectifyingSubstantialType(PropertyExpression<?> objectifiedProperty) {
    this(objectifiedProperty, true);
  }

  ObjectifyingSubstantialType(PropertyExpression<?> objectifiedProperty, boolean createFromEnd) {
    super(objectifiedProperty.getHolder().getPersistentName() + ".objectify(" + "@" + objectifiedProperty.getPersistentName() + ")");
    this.objectifiedProperty = objectifiedProperty;
    this.id = clone(UniversalTypeExpression.ID_PROPERTY);
    this.name = clone(MixinTypeNamed.NAME_PROPERTY);
    this.description = clone(MixinTypeNamed.DESCRIPTION_PROPERTY);
    if (createFromEnd) {
      this.fromEnd = new FromObjectifiedRelationshipEnd(this);
      init();
    }
  }

  /**{@inheritDoc}**/
  @Override
  protected void init() {
    //FIXME find better procedure to store I18n
    super.init();
    super.setName("Values of \"" + objectifiedProperty.getName(new Locale("en")) + "\"", new Locale("en"));
    super.setName("Werte von\"" + objectifiedProperty.getName(new Locale("de")) + "\"", new Locale("de"));
  }

  void setFromEnd(RelationshipEndExpression fromEnd) {
    this.fromEnd = fromEnd;
    init();
  }

  /**{@inheritDoc}**/
  public List<FeatureExpression<?>> getFeatures() {
    List<FeatureExpression<?>> result = Lists.newLinkedList();
    result.add(this.id);
    result.add(this.name);
    result.add(this.description);
    result.add(this.fromEnd);
    return result;
  }

  private PropertyInObjectifiedSubstantialType clone(PrimitivePropertyExpression ppe) {
    return new PropertyInObjectifiedSubstantialType(ppe.getPersistentName(), this, ppe.getOrigin(), ppe.getLowerBound(), ppe.getUpperBound(),
        ppe.getType());
  }

  @ConfigParameter
  public PropertyExpression<?> getObjectifiedProperty() {
    return objectifiedProperty;
  }

  static class PropertyInObjectifiedSubstantialType extends PrimitivePropertyImpl {

    public PropertyInObjectifiedSubstantialType(String persistentName, UniversalTypeExpression holder, UniversalTypeExpression origin, int lower,
        int upper, PrimitiveTypeExpression type) {
      super(persistentName, holder, origin, lower, upper, type);
    }
  }

  /**{@inheritDoc}**/
  public UniversalTypeExpression getParent() {
    return objectifiedProperty.getHolder();
  }
}
