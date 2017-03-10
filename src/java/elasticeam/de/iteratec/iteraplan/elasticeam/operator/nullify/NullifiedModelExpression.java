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

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.ADerivedExpression;
import de.iteratec.iteraplan.elasticeam.model.derived.DerivedUniversalModelExpression;


public abstract class NullifiedModelExpression extends ADerivedExpression implements DerivedUniversalModelExpression {

  protected final NullifiedUniversalType<UniversalTypeExpression> nullType;

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected NullifiedModelExpression(Model model, NullifiedUniversalType nullType) {
    super(model);
    this.nullType = nullType;
  }

  /**{@inheritDoc}**/
  public void setValue(PropertyExpression<?> property, Object value) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public UniversalModelExpression getConnected(RelationshipEndExpression relationshipEnd) {
    Collection<UniversalModelExpression> allConnected = getConnecteds(relationshipEnd);
    if (allConnected.size() == 0) {
      return null;
    }
    return allConnected.iterator().next();
  }

  /**{@inheritDoc}**/
  @SuppressWarnings("unchecked")
  public Collection<UniversalModelExpression> getConnecteds(RelationshipEndExpression relationshipEnd) {
    if (!nullType.equals(relationshipEnd.getHolder())) {
      throw new ModelException(ModelException.GENERAL_ERROR, "Can not evaluate relationship end " + relationshipEnd + " on the nullified type "
          + nullType);
    }

    Object val = getModel().getValue(this, relationshipEnd);
    if (val == null) {
      return Collections.EMPTY_SET;
    }
    else if (val instanceof Collection) {
      return (Collection<UniversalModelExpression>) val;
    }
    Set<UniversalModelExpression> result = Sets.newHashSet();
    result.add((UniversalModelExpression) val);
    return result;
  }

  /**{@inheritDoc}**/
  public void connect(RelationshipEndExpression relationshipEnd, Object value) {
    throw new UnsupportedOperationException();
  }

  /**{@inheritDoc}**/
  public void disconnect(RelationshipEndExpression relationshipEnd, Object value) {
    throw new UnsupportedOperationException();
  }

  protected NullifiedUniversalType<UniversalTypeExpression> getNullType() {
    return this.nullType;
  }

  public final boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof NullifiedModelExpression)) {
      return false;
    }
    return ((NullifiedModelExpression) obj).getNullType().equals(nullType);
  }

  public final int hashCode() {
    return "NullifiedInstance".hashCode() ^ nullType.hashCode();
  }

}
