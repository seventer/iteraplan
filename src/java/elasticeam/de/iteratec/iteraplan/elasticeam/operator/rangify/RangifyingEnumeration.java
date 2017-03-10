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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.EnumerationImpl;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.EnumerationLiteralImpl;


public class RangifyingEnumeration extends EnumerationImpl {

  private List<EnumerationLiteralExpression> literals;

  public RangifyingEnumeration(PropertyExpression<?> property, List<Range> ranges) {
    super(pName(property, ranges));
    this.literals = Lists.newLinkedList();
    for (Range range : validateAndSortRanges(ranges)) {
      this.literals.add(new RangifyingEnumerationLiteral(range));
    }
    init();
  }

  private static String pName(PropertyExpression<?> property, List<Range> ranges) {
    StringBuffer result = new StringBuffer();
    result.append("rangesOf(");
    result.append(property.getHolder().getPersistentName());
    result.append('@');
    result.append(property.getPersistentName());
    for (Range range : ranges) {
      result.append(',');
      result.append(range);
    }
    result.append(')');
    return result.toString();
  }

  static List<Range> validateAndSortRanges(List<Range> inputRanges) {
    if (inputRanges == null || inputRanges.size() == 0) {
      throw new ModelException(ModelException.INCONSISTENT_RANGES, "A rangified property requires at least one range.");
    }
    List<Range> result = new ArrayList<Range>(inputRanges);
    Collections.sort(result);
    for (int i = 0; i < result.size() - 1; i++) {
      if (result.get(i).compareTo(result.get(i + 1)) == 0) {
        throw new ModelException(ModelException.INCONSISTENT_RANGES, "Ranges should not overlap.");
      }
    }
    return result;
  }

  /**{@inheritDoc}**/
  public List<EnumerationLiteralExpression> getLiterals() {
    return this.literals;
  }

  final class RangifyingEnumerationLiteral extends EnumerationLiteralImpl {
    private Range range;

    RangifyingEnumerationLiteral(Range range) {
      //Default color is used here. 
      super(RangifyingEnumeration.this, range.toString(), null);
      this.range = range;
    }

    Range getRange() {
      return this.range;
    }
  }
}
