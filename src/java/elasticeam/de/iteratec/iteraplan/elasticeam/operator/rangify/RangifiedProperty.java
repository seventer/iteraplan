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
package de.iteratec.iteraplan.elasticeam.operator.rangify;

import java.util.Collections;
import java.util.List;

import de.iteratec.iteraplan.elasticeam.metamodel.ConfigParameter;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitivePropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.EnumerationPropertyImpl;


public class RangifiedProperty extends EnumerationPropertyImpl {

  private PrimitivePropertyExpression baseProperty;
  private List<Range>                 ranges;

  public RangifiedProperty(PrimitivePropertyExpression baseProperty, List<Range> ranges) {
    super(pName(baseProperty, ranges), baseProperty.getHolder(), 0, baseProperty.getUpperBound(), new RangifyingEnumeration(baseProperty, ranges));
    this.baseProperty = baseProperty;
    this.ranges = ranges;
  }

  private static String pName(PrimitivePropertyExpression baseProperty, List<Range> ranges) {
    StringBuffer result = new StringBuffer();
    result.append("rangify(");
    result.append(baseProperty.getPersistentName());
    for (Range range : RangifyingEnumeration.validateAndSortRanges(ranges)) {
      result.append(',');
      result.append(range);
    }
    result.append(')');
    return result.toString();
  }

  @ConfigParameter
  public PrimitivePropertyExpression getBaseProperty() {
    return this.baseProperty;
  }

  @ConfigParameter
  public List<Range> getRanges() {
    return Collections.unmodifiableList(this.ranges);
  }
}
