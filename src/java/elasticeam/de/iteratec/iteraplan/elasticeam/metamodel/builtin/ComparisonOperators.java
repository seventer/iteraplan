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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.elasticeam.exception.MetamodelException;
import de.iteratec.iteraplan.elasticeam.metamodel.ComparisonOperatorExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.impl.ComparisonOperatorImpl;


/**
 * Provides the definitions for the different comparison operators per data type.
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class ComparisonOperators {

  /**
   * The set of all comparison operators.
   */
  public static final Set<ComparisonOperatorExpression> COMPARISON_OPERATORS           = new HashSet<ComparisonOperatorExpression>();

  /**
   * The equality comparison operator. Its compare method will return <b>true</b> in accordance with the
   * equality implementation provided for each according type.
   */
  public static final ComparisonOperatorExpression      EQUALS                         = equalsComparisonOperator();

  /**
   * The 'greater than' comparison operator. Its compare method will return <b>true</b> in accordance with
   * the implementation provided for each according type.
   */
  public static final ComparisonOperatorExpression      GREATER                        = greaterComparisonOperator();

  /**
   * The 'less than' comparison operator. Its compare method will return <b>true</b> in accordance with
   * the implementation provided for each according type.
   */
  public static final ComparisonOperatorExpression      LESS                           = lessComparisonOperator();

  /**
   * The 'greater than or equal to' comparison operator. Its compare method will return <b>true</b> in accordance with
   * the implementation provided for each according type.
   */
  public static final ComparisonOperatorExpression      GREATER_EQUALS                 = greaterEqualsComparisonOperator();

  /**
   * The 'less than or equal' to comparison operator. Its compare method will return <b>true</b> in accordance with
   * the implementation provided for each according type.
   */
  public static final ComparisonOperatorExpression      LESS_EQUALS                    = lessEqualsComparisonOperator();

  /**
   * The 'not equal to' comparison operator. Its compare method will return <b>true</b> in accordance with
   * the implementation provided for each according type.
   */
  public static final ComparisonOperatorExpression      NOT_EQUALS                     = notEqualsComparisonOperator();

  /**
   * The 'contains' comparison operator. Its compare method will return <b>true</b> in accordance with
   * the implementation provided for each according type.
   */
  public static final ComparisonOperatorExpression      CONTAINS                       = containsComparisonOperator();

  /**
   * The 'begins with' comparison operator. Its compare method will return <b>true</b> in accordance with
   * the implementation provided for each according type.
   */
  public static final ComparisonOperatorExpression      BEGINS_WITH                    = beginsWithComparisonOperator();

  /**
   * The 'ends with' comparison operator. Its compare method will return <b>true</b> in accordance with
   * the implementation provided for each according type.
   */
  public static final ComparisonOperatorExpression      ENDS_WITH                      = endsWithComparisonOperator();

  private static final String                           PERSISTENT_NAME_EQUALS         = "=";
  private static final String                           PERSISTENT_NAME_GREATER        = ">";
  private static final String                           PERSISTENT_NAME_LESS           = "<";
  private static final String                           PERSISTENT_NAME_GREATER_EQUALS = ">=";
  private static final String                           PERSISTENT_NAME_LESS_EQUALS    = "<=";
  private static final String                           PERSISTENT_NAME_NOT_EQUALS     = "!=";
  private static final String                           PERSISTENT_NAME_CONTAINS       = ".contains";
  private static final String                           PERSISTENT_NAME_BEGINS_WITH    = ".startsWith";
  private static final String                           PERSISTENT_NAME_ENDS_WITH      = ".endsWith";

  private static final Class<?>[]                       TYPES_EQUALS                   = new Class<?>[] { String.class, BigInteger.class,
      BigDecimal.class, Date.class, Boolean.class                                     };
  private static final Class<?>[]                       TYPES_GREATER                  = new Class<?>[] { BigInteger.class, BigDecimal.class,
      Date.class                                                                      };
  private static final Class<?>[]                       TYPES_LESS                     = new Class<?>[] { BigInteger.class, BigDecimal.class,
      Date.class                                                                      };
  private static final Class<?>[]                       TYPES_GREATER_EQUALS           = new Class<?>[] { BigInteger.class, BigDecimal.class,
      Date.class                                                                      };
  private static final Class<?>[]                       TYPES_LESS_EQUALS              = new Class<?>[] { BigInteger.class, BigDecimal.class,
      Date.class                                                                      };
  private static final Class<?>[]                       TYPES_NOT_EQUALS               = new Class<?>[] { BigInteger.class, BigDecimal.class,
      Date.class, Boolean.class                                                       };
  private static final Class<?>[]                       TYPES_CONTAINS                 = new Class<?>[] { String.class };
  private static final Class<?>[]                       TYPES_BEGINS_WITH              = new Class<?>[] { String.class };
  private static final Class<?>[]                       TYPES_ENDS_WITH                = new Class<?>[] { String.class };

  private ComparisonOperators() {
    //Nothing here
  }

  /**
   * Retrieves a comparison operator by its locale-specific name.
   * @param name
   *    The name of the comparison operator in the current locale.
   * @return
   *    The comparison operator with the given lolace-specific name, or <b>null</b> if
   *    it can not be found.
   */
  public ComparisonOperatorExpression findByName(String name) {
    for (ComparisonOperatorExpression co : COMPARISON_OPERATORS) {
      if (co.getName().equals(name)) {
        return co;
      }
    }
    return null;
  }

  /**
   * Retrieves a comparison operator by its persistent name.
   * @param name
   *    The persistent name of the comparison operator.
   * @return
   *    The comparison operator instance with the given persistent name, or <b>null</b> if
   *    it can not be found.
   */
  public ComparisonOperatorExpression findByPersistentName(String name) {
    for (ComparisonOperatorExpression co : COMPARISON_OPERATORS) {
      if (co.getPersistentName().equals(name)) {
        return co;
      }
    }
    return null;
  }

  /**
   * Retrieves the set of comparison operators which support a given type.
   * @param type
   *    The class to support.
   * @return
   *    The set of operators which can compare instances of the given type(class).
   */
  protected static Set<ComparisonOperatorExpression> getAvailableComparisonOperators(Class<?> type) {
    Set<ComparisonOperatorExpression> result = Sets.newHashSet();
    for (ComparisonOperatorExpression co : COMPARISON_OPERATORS) {
      if (co.supportsType(type)) {
        result.add(co);
      }
    }
    return result;
  }

  private static void validateArguments(Set<Class<?>> supportedTypes, Object o1, Object o2) {
    if (o1 == null || o2 == null) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "Comparison operators do not support null values.");
    }
    if (!o1.getClass().equals(o2.getClass())) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "A comparison operator can only compare instances of the same class.");
    }
    if (!supportedTypes.contains(o1.getClass())) {
      throw new MetamodelException(MetamodelException.GENERAL_ERROR, "The objects to compare are of a type not supported by the current operator.");
    }
  }

  private static boolean numericCompare(ComparisonOperatorExpression operator, BigDecimal value1, BigDecimal value2) {

    if ((operator.equals(ComparisonOperators.EQUALS) && value1.compareTo(value2) == 0)
        || (operator.equals(ComparisonOperators.GREATER) && value1.compareTo(value2) == 1)
        || (operator.equals(ComparisonOperators.GREATER_EQUALS) && value1.compareTo(value2) >= 0)
        || (operator.equals(ComparisonOperators.LESS) && value1.compareTo(value2) == -1)
        || (operator.equals(ComparisonOperators.LESS_EQUALS) && value1.compareTo(value2) <= 0)
        || (operator.equals(ComparisonOperators.NOT_EQUALS) && value1.compareTo(value2) != 0)) {
      return true;
    }
    return false;
  }

  private static boolean stringCompare(ComparisonOperatorExpression operator, String value1, String value2) {
    if ((ComparisonOperators.CONTAINS.equals(operator) && value1.toLowerCase().contains(value2.toLowerCase()))
        || (ComparisonOperators.EQUALS.equals(operator) && value1.equalsIgnoreCase(value2))
        || (ComparisonOperators.BEGINS_WITH.equals(operator) && value1.toLowerCase().startsWith(value2.toLowerCase()))
        || (ComparisonOperators.ENDS_WITH.equals(operator) && value1.toLowerCase().endsWith(value2.toLowerCase()))) {
      return true;
    }
    return false;
  }

  private static boolean booleanCompare(ComparisonOperatorExpression operator, Boolean value1, Boolean value2) {
    if ((ComparisonOperators.EQUALS.equals(operator) && value1.equals(value2))
        || (ComparisonOperators.NOT_EQUALS.equals(operator) && !value1.equals(value2))) {
      return true;
    }
    return false;
  }

  private static boolean dateCompare(ComparisonOperatorExpression operator, Date value1, Date value2) {
    if (operator.equals(ComparisonOperators.EQUALS) || operator.equals(ComparisonOperators.NOT_EQUALS)) {
      //Note: Since the query language specifies the date with day accuracy, this
      // accuracy should also be used for the comparison of the two dates.

      Calendar valCalendar = Calendar.getInstance();
      valCalendar.setTime(value1);
      Calendar condCalendar = Calendar.getInstance();
      condCalendar.setTime(value2);

      if (ComparisonOperators.EQUALS.equals(operator) && valCalendar.get(Calendar.YEAR) == condCalendar.get(Calendar.YEAR)
          && valCalendar.get(Calendar.MONTH) == condCalendar.get(Calendar.MONTH)
          && valCalendar.get(Calendar.DAY_OF_MONTH) == condCalendar.get(Calendar.DAY_OF_MONTH)) {
        return true;
      }
      if (ComparisonOperators.NOT_EQUALS.equals(operator)
          && !(valCalendar.get(Calendar.YEAR) == condCalendar.get(Calendar.YEAR)
              && valCalendar.get(Calendar.MONTH) == condCalendar.get(Calendar.MONTH) && valCalendar.get(Calendar.DAY_OF_MONTH) == condCalendar
              .get(Calendar.DAY_OF_MONTH))) {
        return true;
      }
    }
    else if ((operator.equals(ComparisonOperators.LESS) && value1.before(value2))
        || (operator.equals(ComparisonOperators.GREATER) && value1.after(value2))
        || (operator.equals(ComparisonOperators.LESS_EQUALS) && (value1.before(value2) || value1.equals(value2)))
        || (operator.equals(ComparisonOperators.GREATER_EQUALS) && (value1.after(value2) || value1.equals(value2)))) {
      return true;
    }
    return false;
  }

  private static boolean rootCompare(ComparisonOperatorExpression operator, Object o1, Object o2) {
    if (BigInteger.class.equals(o1.getClass())) {
      return numericCompare(operator, BigDecimal.valueOf(((BigInteger) o1).longValue()), BigDecimal.valueOf(((BigInteger) o2).longValue()));
    }
    else if (BigDecimal.class.equals(o1.getClass())) {
      return numericCompare(operator, (BigDecimal) o1, (BigDecimal) o2);
    }
    else if (String.class.equals(o1.getClass())) {
      return stringCompare(operator, (String) o1, (String) o2);
    }
    else if (Boolean.class.equals(o1.getClass())) {
      return booleanCompare(operator, (Boolean) o1, (Boolean) o2);
    }
    else if (Date.class.equals(o1.getClass())) {
      return dateCompare(operator, (Date) o1, (Date) o2);
    }

    //should never happen
    throw new MetamodelException(MetamodelException.GENERAL_ERROR, "No comparison implementation exists for class " + o1.getClass());
  }

  private static ComparisonOperatorExpression equalsComparisonOperator() {
    return new ComparisonOperatorImpl(PERSISTENT_NAME_EQUALS) {
      public Set<Class<?>> getSupportedTypes() {
        return Sets.newHashSet(TYPES_EQUALS);
      }

      public boolean supportsType(Class<?> type) {
        return getSupportedTypes().contains(type);
      }

      public boolean compare(Object o1, Object o2) {
        validateArguments(getSupportedTypes(), o1, o2);
        return rootCompare(EQUALS, o1, o2);
      }

    };
  }

  private static ComparisonOperatorExpression greaterComparisonOperator() {
    return new ComparisonOperatorImpl(PERSISTENT_NAME_GREATER) {
      public Set<Class<?>> getSupportedTypes() {
        return Sets.newHashSet(TYPES_GREATER);
      }

      public boolean supportsType(Class<?> type) {
        return getSupportedTypes().contains(type);
      }

      public boolean compare(Object o1, Object o2) {
        validateArguments(getSupportedTypes(), o1, o2);
        return rootCompare(GREATER, o1, o2);
      }
    };
  }

  private static ComparisonOperatorExpression lessComparisonOperator() {
    return new ComparisonOperatorImpl(PERSISTENT_NAME_LESS) {
      public Set<Class<?>> getSupportedTypes() {
        return Sets.newHashSet(TYPES_LESS);
      }

      public boolean supportsType(Class<?> type) {
        return getSupportedTypes().contains(type);
      }

      public boolean compare(Object o1, Object o2) {
        validateArguments(getSupportedTypes(), o1, o2);
        return rootCompare(LESS, o1, o2);
      }

    };
  }

  private static ComparisonOperatorExpression greaterEqualsComparisonOperator() {
    return new ComparisonOperatorImpl(PERSISTENT_NAME_GREATER_EQUALS) {
      public Set<Class<?>> getSupportedTypes() {
        return Sets.newHashSet(TYPES_GREATER_EQUALS);
      }

      public boolean supportsType(Class<?> type) {
        return getSupportedTypes().contains(type);
      }

      public boolean compare(Object o1, Object o2) {
        validateArguments(getSupportedTypes(), o1, o2);
        return rootCompare(GREATER_EQUALS, o1, o2);
      }

    };
  }

  private static ComparisonOperatorExpression lessEqualsComparisonOperator() {
    return new ComparisonOperatorImpl(PERSISTENT_NAME_LESS_EQUALS) {
      public Set<Class<?>> getSupportedTypes() {
        return Sets.newHashSet(TYPES_LESS_EQUALS);
      }

      public boolean supportsType(Class<?> type) {
        return getSupportedTypes().contains(type);
      }

      public boolean compare(Object o1, Object o2) {
        validateArguments(getSupportedTypes(), o1, o2);
        return rootCompare(LESS_EQUALS, o1, o2);
      }

    };
  }

  private static ComparisonOperatorExpression notEqualsComparisonOperator() {
    return new ComparisonOperatorImpl(PERSISTENT_NAME_NOT_EQUALS) {
      public Set<Class<?>> getSupportedTypes() {
        return Sets.newHashSet(TYPES_NOT_EQUALS);
      }

      public boolean supportsType(Class<?> type) {
        return getSupportedTypes().contains(type);
      }

      public boolean compare(Object o1, Object o2) {
        validateArguments(getSupportedTypes(), o1, o2);
        return rootCompare(NOT_EQUALS, o1, o2);
      }
    };
  }

  private static ComparisonOperatorExpression containsComparisonOperator() {
    return new ComparisonOperatorImpl(PERSISTENT_NAME_CONTAINS) {
      public Set<Class<?>> getSupportedTypes() {
        return Sets.newHashSet(TYPES_CONTAINS);
      }

      public boolean supportsType(Class<?> type) {
        return getSupportedTypes().contains(type);
      }

      public boolean compare(Object o1, Object o2) {
        validateArguments(getSupportedTypes(), o1, o2);
        return rootCompare(CONTAINS, o1, o2);
      }
    };
  }

  private static ComparisonOperatorExpression beginsWithComparisonOperator() {
    return new ComparisonOperatorImpl(PERSISTENT_NAME_BEGINS_WITH) {
      public Set<Class<?>> getSupportedTypes() {
        return Sets.newHashSet(TYPES_BEGINS_WITH);
      }

      public boolean supportsType(Class<?> type) {
        return getSupportedTypes().contains(type);
      }

      public boolean compare(Object o1, Object o2) {
        validateArguments(getSupportedTypes(), o1, o2);
        return rootCompare(BEGINS_WITH, o1, o2);
      }
    };
  }

  private static ComparisonOperatorExpression endsWithComparisonOperator() {
    return new ComparisonOperatorImpl(PERSISTENT_NAME_ENDS_WITH) {
      public Set<Class<?>> getSupportedTypes() {
        return Sets.newHashSet(TYPES_ENDS_WITH);
      }

      public boolean supportsType(Class<?> type) {
        return getSupportedTypes().contains(type);
      }

      public boolean compare(Object o1, Object o2) {
        validateArguments(getSupportedTypes(), o1, o2);
        return rootCompare(ENDS_WITH, o1, o2);
      }
    };
  }

}
