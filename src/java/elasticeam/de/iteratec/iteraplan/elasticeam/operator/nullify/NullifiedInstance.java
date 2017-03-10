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

import java.math.BigInteger;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;


public class NullifiedInstance extends NullifiedModelExpression implements InstanceExpression {

  protected NullifiedInstance(Model model, NullifiedSubstantialType representedTypeNull) {
    super(model, representedTypeNull);
  }

  /**{@inheritDoc}**/
  public Object getValue(PropertyExpression<?> property) {
    if (MixinTypeNamed.NAME_PROPERTY.equals(property)) {
      return "NullInstance(" + nullType + ")";
    }
    else if (UniversalTypeExpression.ID_PROPERTY.equals(property)) {
      return BigInteger.valueOf(this.hashCode() ^ nullType.hashCode());
    }
    else if (MixinTypeNamed.DESCRIPTION_PROPERTY.equals(property)) {
      return "Null instance of type " + nullType.getBaseType();
    }
    return null;
  }

  /**{@inheritDoc}**/
  public Collection<Object> getValues(PropertyExpression<?> property) {
    Object val = getValue(property);
    Set<Object> result = new HashSet<Object>();
    if (val == null) {
      return result;
    }
    result.add(val);
    return result;
  }

}
