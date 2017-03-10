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
package de.iteratec.iteraplan.elasticeam.util;

import de.iteratec.iteraplan.common.util.EqualsUtils;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.MixinTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.TypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;


/**
 * A utility which describes the equality criteria for metamodel elements.
 */
public final class CompareUtil {

  @SuppressWarnings("rawtypes")
  private static final Class[] ORDER_OF_KINDS_OF_TYPES = { PrimitiveTypeExpression.class, EnumerationExpression.class,
      SubstantialTypeExpression.class, RelationshipTypeExpression.class, MixinTypeExpression.class };

  private CompareUtil() {
    //Nothing here
  }

  /**
   * Checks whether two metamodel elements are equal.
   * 
   * @param o1
   *    The first metamodel element.
   * @param o2
   *    The second metamodel element.
   * @param superInterface
   *    The reference type against which equality should be checked.
   * @return
   *    <b>true</b> if the two elements are equal with respect to the given super interface.
   */
  public static boolean equals(Object o1, Object o2, Class<? extends NamedExpression> superInterface) {
    if (o1 instanceof NamedExpression ^ o2 instanceof NamedExpression) {
      return false;
    }
    if (!superInterface.isInstance(o1) || !superInterface.isInstance(o2)) {
      return false;
    }
    NamedExpression n1 = (NamedExpression) o1;
    NamedExpression n2 = (NamedExpression) o2;
    if (EnumerationLiteralExpression.class.isAssignableFrom(superInterface)) {
      return equalsByName(n1, n2) && equalsByName(((EnumerationLiteralExpression) n1).getOwner(), ((EnumerationLiteralExpression) n2).getOwner());
    }
    else if (FeatureExpression.class.isAssignableFrom(superInterface)) {
      return equalsByName(n1, n2) && equalsByName(((FeatureExpression<?>) n1).getOrigin(), ((FeatureExpression<?>) n2).getOrigin());
    }
    else if (RelationshipExpression.class.isAssignableFrom(superInterface)) {
      RelationshipEndExpression r1e1 = ((RelationshipExpression) n1).getRelationshipEnds().get(0);
      RelationshipEndExpression r1e2 = ((RelationshipExpression) n1).getRelationshipEnds().get(1);
      RelationshipEndExpression r2e1 = ((RelationshipExpression) n2).getRelationshipEnds().get(0);
      RelationshipEndExpression r2e2 = ((RelationshipExpression) n2).getRelationshipEnds().get(1);

      return equalsByName(n1, n2) && ((r1e1.equals(r2e1) && r1e2.equals(r2e2)) || (r1e1.equals(r2e2) && r1e2.equals(r2e1)));
    }
    else {
      return equalsByName(n1, n2);
    }
  }

  /**
   * Compares two metamodel elements.
   * 
   * @param n1
   *    The first metamodel element.
   * @param n2
   *    The second metamodel element.
   * @return
   *    -1 if the first element is smaller than the second, 0 if the elements are equal and 1 if the first element is greater than the second.
   */
  public static int compareTo(NamedExpression n1, NamedExpression n2) {
    if (n1 instanceof EnumerationLiteralExpression && n2 instanceof EnumerationLiteralExpression) {
      int ownerCompare = compareToByName(((EnumerationLiteralExpression) n1).getOwner(), ((EnumerationLiteralExpression) n2).getOwner());
      return ownerCompare == 0 ? compareToByName(n1, n2) : ownerCompare;
    }
    else if (n1 instanceof FeatureExpression && n2 instanceof FeatureExpression) {
      return compareToBySourceAndName((FeatureExpression<?>) n1, (FeatureExpression<?>) n2);
    }
    else if (n1 instanceof TypeExpression && n2 instanceof TypeExpression) {
      return compareTypes((TypeExpression) n1, (TypeExpression) n2);
    }
    else {
      return compareToByName(n1, n2);
    }
  }

  /**
   * Computes the hash code of a meta-model element with respect to a given super type.
   * 
   * @param n
   *    The metamodel element.
   * @param superInterface
   *    The super interface.
   * @return
   *    The generated hash code.
   */
  public static int hashCode(NamedExpression n, Class<? extends NamedExpression> superInterface) {
    int result = n.getPersistentName().hashCode();
    if (EnumerationLiteralExpression.class.equals(superInterface)) {
      result ^= ((EnumerationLiteralExpression) n).getOwner().getPersistentName().hashCode();
    }
    else if (FeatureExpression.class.equals(superInterface) && ((FeatureExpression<?>) n).getOrigin() != null) {
      result ^= ((FeatureExpression<?>) n).getOrigin().getPersistentName().hashCode();
    }
    return result ^ superInterface.hashCode();
  }

  private static boolean equalsByName(NamedExpression n1, NamedExpression n2) {
    if (n1 == null) {
      return n2 == null;
    }
    else {
      return n2 != null && EqualsUtils.areEqual(n1.getPersistentName(), n2.getPersistentName());
    }
  }

  private static int compareTypes(TypeExpression t1, TypeExpression t2) {
    int t1Pos = Integer.MAX_VALUE;
    for (int i = 0; i < ORDER_OF_KINDS_OF_TYPES.length; i++) {
      if (ORDER_OF_KINDS_OF_TYPES[i].equals(t1.getClass())) {
        t1Pos = i;
        i = ORDER_OF_KINDS_OF_TYPES.length;
      }
    }
    int t2Pos = Integer.MAX_VALUE;
    for (int i = 0; i < ORDER_OF_KINDS_OF_TYPES.length; i++) {
      if (ORDER_OF_KINDS_OF_TYPES[i].equals(t2.getClass())) {
        t2Pos = i;
        i = ORDER_OF_KINDS_OF_TYPES.length;
      }
    }
    if (t1Pos > t2Pos) {
      return 1;
    }
    else if (t1Pos < t2Pos) {
      return -1;
    }
    else {
      return compareToByName(t1, t2);
    }
  }

  private static int compareToBySourceAndName(FeatureExpression<?> f1, FeatureExpression<?> f2) {
    if (f1 instanceof PropertyExpression ^ f2 instanceof PropertyExpression) {
      return f1 instanceof PropertyExpression ? -1 : 1;
    }
    else if (f1.getOrigin() == null ^ f2.getOrigin() == null) {
      return f1.getOrigin() == null ? -1 : 1;
    }
    else if (f1.getOrigin() instanceof MixinTypeNamed ^ f2.getOrigin() instanceof MixinTypeNamed) {
      return f1.getOrigin() instanceof MixinTypeNamed ? -1 : 1;
    }
    else {
      int sourceCompare = compareToByName(f1.getOrigin(), f2.getOrigin());
      return sourceCompare == 0 ? compareToByName(f1, f2) : sourceCompare;
    }
  }

  private static int compareToByName(NamedExpression n1, NamedExpression n2) {
    if (n1 == null) {
      return n2 == null ? 0 : -1;
    }
    if (n2 == null) {
      return 1;
    }
    if (n1.getPersistentName() == null) {
      return n2.getPersistentName() == null ? 0 : -1;
    }
    else {
      return n2.getPersistentName() == null ? 1 : n1.getPersistentName().compareTo(n2.getPersistentName());
    }
  }
}
