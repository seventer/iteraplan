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
package de.iteratec.iteraplan.elasticeam.operator.filter.predicate;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.elasticeam.exception.ModelException;
import de.iteratec.iteraplan.elasticeam.metamodel.ComparisonOperatorExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.ConfigParameter;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PrimitiveTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.BuiltinPrimitiveType;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.util.ElasticeamContextUtil;


@SuppressWarnings("PMD.TooManyMethods")
@edu.umd.cs.findbugs.annotations.SuppressWarnings({ "UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS" })
public final class FilterPredicates {

  private FilterPredicates() {
    //Nothing here
  }

  public static Predicate<UniversalModelExpression> buildLeafPredicate(final ComparisonOperatorExpression operator,
                                                                       final PropertyExpression<?> onProperty, final PropertyExpression<?> refProperty) {
    if (BuiltinPrimitiveType.DATE.equals(onProperty.getType())) {
      return buildDatePredicate(operator, onProperty, refProperty);
    }
    else if (BuiltinPrimitiveType.INTEGER.equals(onProperty.getType())) {
      return buildIntegerPredicate(operator, onProperty, refProperty);
    }
    else if (BuiltinPrimitiveType.DECIMAL.equals(onProperty.getType())) {
      return buildDoublePredicate(operator, onProperty, refProperty);
    }
    else {
      return buildStringPredicate(operator, onProperty, refProperty);
    }
  }

  public static Predicate<UniversalModelExpression> buildLeafPredicate(final ComparisonOperatorExpression operator,
                                                                       final PropertyExpression<?> onProperty, final Object value) {
    if (BuiltinPrimitiveType.DATE.equals(onProperty.getType())) {
      return buildDatePredicate(operator, onProperty, parseDate(value));
    }
    else if (BuiltinPrimitiveType.INTEGER.equals(onProperty.getType())) {
      return buildIntegerPredicate(operator, onProperty, parseBigInteger(value));
    }
    else if (BuiltinPrimitiveType.DECIMAL.equals(onProperty.getType())) {
      return buildDoublePredicate(operator, onProperty, parseBigDecimal(value));
    }
    else {
      return buildStringPredicate(operator, onProperty, value.toString());
    }
  }

  /**
   * Creates an AND/OR/NOT predicate.
   * 
   * @param operation
   *    The operation of choice.
   * @param subPredicates
   *    A list with sub-predicates. In case of NOT, the list should contain only one element.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildCompositePredicate(final CompositePredicateOperation operation,
                                                                            final List<Predicate<UniversalModelExpression>> subPredicates) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        if (operation.equals(CompositePredicateOperation.AND)) {
          return Predicates.and(subPredicates).apply(modelExpression);
        }
        else if (operation.equals(CompositePredicateOperation.OR)) {
          return Predicates.or(subPredicates).apply(modelExpression);
        }
        else {
          return Predicates.not(subPredicates.get(0)).apply(modelExpression);
        }
      }

      public String toString() {
        return operation.getValue() + "(" + subPredicates + ")";
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public CompositePredicateOperation getOperation() {
        return operation;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public List<Predicate<UniversalModelExpression>> getSubPredicates() {
        return Collections.unmodifiableList(subPredicates);
      }
    };
  }

  /**
   * Creates a predicate which represents a date condition.
   * 
   * @param operator
   *    The date operator of choice.   
   * @param onProperty
   *    The property whose value(s) are to be used for the comparison.
   * @param value
   *    The value to compare with.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildDatePredicate(final ComparisonOperatorExpression operator,
                                                                       final PropertyExpression<?> onProperty, final Date value) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        if (!BuiltinPrimitiveType.DATE.equals(onProperty.getType())) {
          throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Can not create a Date predicate for a property of type "
              + onProperty.getType());
        }

        for (Object val : modelExpression.getValues(onProperty)) {
          Date propertyValue = (Date) val;
          if (operator.compare(propertyValue, value)) {
            return true;
          }
        }
        return false;
      }

      public String toString() {
        return getStringRepresentation(onProperty, operator, value.toString());
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public ComparisonOperatorExpression getComparisonOperator() {
        return operator;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getProperty() {
        return onProperty;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public Date getValue() {
        return value;
      }
    };
  }

  /**
   * Creates a predicate which represents a date condition.
   * 
   * @param operator
   *    The date operator of choice.   
   * @param onProperty
   *    The property whose value(s) are to be used for the comparison.
   * @param refProperty
   *    The reference property with whose values to compare with.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildDatePredicate(final ComparisonOperatorExpression operator,
                                                                       final PropertyExpression<?> onProperty, final PropertyExpression<?> refProperty) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        if (!BuiltinPrimitiveType.DATE.equals(onProperty.getType())) {
          throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Can not create a Date predicate for a property of type "
              + onProperty.getType());
        }

        if (!BuiltinPrimitiveType.DATE.equals(refProperty.getType())) {
          throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Can not create a Date predicate for a reference property property of type "
              + refProperty.getType());
        }

        for (Object val : modelExpression.getValues(onProperty)) {
          Date propertyValue = (Date) val;
          for (Object refVal : modelExpression.getValues(refProperty)) {
            Date refPropertyValue = (Date) refVal;
            if (operator.compare(propertyValue, refPropertyValue)) {
              return true;
            }
          }
        }
        return false;
      }

      public String toString() {
        return getStringRepresentation(onProperty, operator, refProperty);
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public ComparisonOperatorExpression getComparisonOperator() {
        return operator;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getProperty() {
        return onProperty;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getReferenceProperty() {
        return refProperty;
      }
    };
  }

  /**
   * Creates a predicate which represents a double condition.
   * 
   * @param operator
   *    The operator of choice.
   * @param onProperty
   *    The property whose values are to be used for the comparison.
   * @param value
   *    The value to compare with.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildDoublePredicate(final ComparisonOperatorExpression operator,
                                                                         final PropertyExpression<?> onProperty, final Double value) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        return decimalCompare(operator, getDecimalPropertyValues(modelExpression, onProperty), BigDecimal.valueOf(value.doubleValue()));
      }

      public String toString() {
        return getStringRepresentation(onProperty, operator, value.toString());
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public ComparisonOperatorExpression getComparisonOperator() {
        return operator;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getProperty() {
        return onProperty;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public Double getValue() {
        return value;
      }
    };
  }

  /**
   * Creates a predicate which represents a double condition.
   * 
   * @param operator
   *    The operator of choice.
   * @param onProperty
   *    The property whose values are to be used for the comparison.
   * @param refProperty
   *    The reference property with whose values to compare with.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildDoublePredicate(final ComparisonOperatorExpression operator,
                                                                         final PropertyExpression<?> onProperty,
                                                                         final PropertyExpression<?> refProperty) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        return decimalCompare(operator, getDecimalPropertyValues(modelExpression, onProperty), getDecimalPropertyValues(modelExpression, refProperty));
      }

      public String toString() {
        return getStringRepresentation(onProperty, operator, refProperty);
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public ComparisonOperatorExpression getComparisonOperator() {
        return operator;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getProperty() {
        return onProperty;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getReferenceProperty() {
        return refProperty;
      }
    };
  }

  /**
   * Creates a predicate which represents a double condition.
   * 
   * @param operator
   *    The operator of choice.
   * @param onProperty
   *    The property whose values are to be used for the comparison.
   * @param value
   *    The value to compare with.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildDoublePredicate(final ComparisonOperatorExpression operator,
                                                                         final PropertyExpression<?> onProperty, final BigDecimal value) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        return decimalCompare(operator, getDecimalPropertyValues(modelExpression, onProperty), value);
      }

      public String toString() {
        return getStringRepresentation(onProperty, operator, value.toString());
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public ComparisonOperatorExpression getComparisonOperator() {
        return operator;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getProperty() {
        return onProperty;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public BigDecimal getValue() {
        return value;
      }
    };
  }

  /**
   * Builds a predicate for integer filtering.
   * 
   * @param operator
   *    The operator of choice.
   * @param onProperty
   *    The property whose values to use for the comparison.
   * @param value
   *    The value to compare to.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildIntegerPredicate(final ComparisonOperatorExpression operator,
                                                                          final PropertyExpression<?> onProperty, final Integer value) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        return integerCompare(operator, getIntegerPropertyValues(modelExpression, onProperty), BigInteger.valueOf(value.longValue()));
      }

      public String toString() {
        return getStringRepresentation(onProperty, operator, value.toString());
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public ComparisonOperatorExpression getComparisonOperator() {
        return operator;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getProperty() {
        return onProperty;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public Integer getValue() {
        return value;
      }
    };
  }

  /**
   * Builds a predicate for integer filtering.
   * 
   * @param operator
   *    The operator of choice.
   * @param onProperty
   *    The property whose values to use for the comparison.
   * @param value
   *    The value to compare to.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildIntegerPredicate(final ComparisonOperatorExpression operator,
                                                                          final PropertyExpression<?> onProperty, final BigInteger value) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        return integerCompare(operator, getIntegerPropertyValues(modelExpression, onProperty), BigInteger.valueOf(value.longValue()));
      }

      public String toString() {
        return getStringRepresentation(onProperty, operator, value.toString());
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public ComparisonOperatorExpression getComparisonOperator() {
        return operator;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getProperty() {
        return onProperty;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public BigInteger getValue() {
        return value;
      }
    };
  }

  /**
   * Creates a predicate which represents an integer condition.
   * 
   * @param operator
   *    The operator of choice.
   * @param onProperty
   *    The property whose values are to be used for the comparison.
   * @param refProperty
   *    The reference property with whose values to compare with.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildIntegerPredicate(final ComparisonOperatorExpression operator,
                                                                          final PropertyExpression<?> onProperty,
                                                                          final PropertyExpression<?> refProperty) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        return integerCompare(operator, getIntegerPropertyValues(modelExpression, onProperty), getIntegerPropertyValues(modelExpression, refProperty));
      }

      public String toString() {
        return getStringRepresentation(onProperty, operator, refProperty);
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public ComparisonOperatorExpression getComparisonOperator() {
        return operator;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getProperty() {
        return onProperty;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getRefernceProperty() {
        return refProperty;
      }
    };
  }

  /**
   * Builds a predicate which represents a string condition.
   * 
   * @param operator
   *    The operator of choice.
   * @param onProperty
   *    The property whose values are to be compared.
   * @param value
   *    The value to compare to.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildStringPredicate(final ComparisonOperatorExpression operator,
                                                                         final PropertyExpression<?> onProperty, final String value) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        return stringCompare(operator, modelExpression, onProperty, value);
      }

      public String toString() {
        return getStringRepresentation(onProperty, operator, value);
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public ComparisonOperatorExpression getComparisonOperator() {
        return operator;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getProperty() {
        return onProperty;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public String getValue() {
        return value;
      }
    };
  }

  /**
   * Builds a predicate which represents a string condition.
   * 
   * @param operator
   *    The operator of choice.
   * @param onProperty
   *    The property whose values are to be compared.
   * @param refProperty
   *    The property whose values to compare to.
   * @return
   *    The compiled predicate which can later be used to evaluate universal model expressions.
   */
  public static Predicate<UniversalModelExpression> buildStringPredicate(final ComparisonOperatorExpression operator,
                                                                         final PropertyExpression<?> onProperty,
                                                                         final PropertyExpression<?> refProperty) {
    return new Predicate<UniversalModelExpression>() {
      public boolean apply(UniversalModelExpression modelExpression) {
        for (Object val : modelExpression.getValues(refProperty)) {
          if (val instanceof EnumerationLiteralExpression) {
            if (stringCompare(operator, modelExpression, onProperty, ((EnumerationLiteralExpression) val).getName())
                || stringCompare(operator, modelExpression, onProperty, ((EnumerationLiteralExpression) val).getPersistentName())) {
              return true;
            }
          }
          else {
            return stringCompare(operator, modelExpression, onProperty, val.toString());
          }
        }
        return false;
      }

      public String toString() {
        return getStringRepresentation(onProperty, operator, refProperty);
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public ComparisonOperatorExpression getComparisonOperator() {
        return operator;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getProperty() {
        return onProperty;
      }

      @SuppressWarnings("unused")
      @ConfigParameter
      public PropertyExpression<?> getReferenceProperty() {
        return refProperty;
      }
    };
  }

  private static boolean stringCompare(ComparisonOperatorExpression operator, UniversalModelExpression modelExpression,
                                       PropertyExpression<?> onProperty, String referenceValue) {
    List<String> propertyValues = new ArrayList<String>();
    for (Object val : modelExpression.getValues(onProperty)) {
      if (val instanceof EnumerationLiteralExpression) {
        propertyValues.add(((EnumerationLiteralExpression) val).getPersistentName());
        propertyValues.add(((EnumerationLiteralExpression) val).getName());
      }
      else {
        propertyValues.add(val.toString());
      }
    }

    for (String propertyValue : propertyValues) {
      if (operator.compare(propertyValue, referenceValue)) {
        return true;
      }
    }
    return false;
  }

  private static boolean integerCompare(ComparisonOperatorExpression operator, List<BigInteger> propertyValues, List<BigInteger> referenceValues) {
    for (BigInteger refValue : referenceValues) {
      if (integerCompare(operator, propertyValues, refValue)) {
        return true;
      }
    }
    return false;
  }

  private static boolean integerCompare(ComparisonOperatorExpression operator, List<BigInteger> propertyValues, BigInteger compareValue) {
    for (BigInteger propertyValue : propertyValues) {
      if (operator.compare(propertyValue, compareValue)) {
        return true;
      }
    }
    return false;
  }

  private static boolean decimalCompare(ComparisonOperatorExpression operator, List<BigDecimal> propertyValues, List<BigDecimal> referenceValues) {
    for (BigDecimal refValue : referenceValues) {
      if (decimalCompare(operator, propertyValues, refValue)) {
        return true;
      }
    }
    return false;
  }

  private static boolean decimalCompare(ComparisonOperatorExpression operator, List<BigDecimal> propertyValues, BigDecimal compareValue) {
    for (BigDecimal propertyValue : propertyValues) {
      if (operator.compare(propertyValue, compareValue)) {
        return true;
      }
    }
    return false;
  }

  private static List<BigInteger> getIntegerPropertyValues(UniversalModelExpression modelExpression, PropertyExpression<?> property) {
    List<BigInteger> result = new ArrayList<BigInteger>();

    if (PrimitiveTypeExpression.class.isInstance(property.getType())) {
      if (BuiltinPrimitiveType.DECIMAL.equals(property.getType())) {
        for (Object val : modelExpression.getValues(property)) {
          try {
            result.add(BigInteger.valueOf(((BigDecimal) val).longValueExact()));
          } catch (ArithmeticException e) {
            throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Property type " + property.getType()
                + " can not be converted to BigInteger for comparison.");
          }
        }
        return result;
      }
      else if (BuiltinPrimitiveType.INTEGER.equals(property.getType())) {
        for (Object val : modelExpression.getValues(property)) {
          BigInteger biValue = (BigInteger) val;
          result.add(biValue);
        }
        return result;
      }
      else if (BuiltinPrimitiveType.STRING.equals(property.getType())) {
        for (Object val : modelExpression.getValues(property)) {
          try {
            Integer value = Integer.valueOf(val.toString());
            result.add(BigInteger.valueOf(value.longValue()));
          } catch (NumberFormatException e) {
            throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Property type " + property.getType()
                + " can not be converted to BigInteger for comparison.");
          }
        }
        return result;
      }

      Class<?> propClass = ((PrimitiveTypeExpression) property.getType()).getEncapsulatedType();
      if (Integer.class.equals(propClass)) {
        for (Object val : modelExpression.getValues(property)) {
          result.add(BigInteger.valueOf(((Integer) val).longValue()));
        }
        return result;
      }
      else if (Double.class.equals(propClass)) {
        for (Object val : modelExpression.getValues(property)) {
          result.add(BigInteger.valueOf(((Double) val).longValue()));
        }
        return result;
      }
      else if (Long.class.equals(propClass)) {
        for (Object val : modelExpression.getValues(property)) {
          result.add(BigInteger.valueOf(((Long) val).longValue()));
        }
        return result;
      }
      //Try converting from anything
      boolean allParse = true;
      for (Object val : modelExpression.getValues(property)) {
        try {
          parseBigInteger(val);
        } catch (ModelException e) {
          allParse = false;
          break;
        }
      }
      if (allParse) {
        for (Object val : modelExpression.getValues(property)) {
          result.add(parseBigInteger(val));
        }
      }
    }
    else {
      //Conversion of enumeration literals (locale specific) to numeric values
      Collection<Object> objVals = modelExpression.getValues(property);
      boolean allParse = true;
      for (Object val : objVals) {
        String currentValue = ((EnumerationLiteralExpression) val).getName();
        try {
          parseBigDecimal(currentValue);
        } catch (ModelException e) {
          allParse = false;
          break;
        }
      }
      if (allParse) {
        for (Object val : objVals) {
          result.add(parseBigInteger(((EnumerationLiteralExpression) val).getName()));
        }
      }
    }
    throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Property type " + property.getType()
        + " can not be converted to BigDecimal for comparison.");
  }

  @SuppressWarnings("boxing")
  private static List<BigDecimal> getDecimalPropertyValues(UniversalModelExpression modelExpression, PropertyExpression<?> property) {
    List<BigDecimal> result = new ArrayList<BigDecimal>();

    if (PrimitiveTypeExpression.class.isInstance(property.getType())) {
      if (BuiltinPrimitiveType.DECIMAL.equals(property.getType())) {
        for (Object val : modelExpression.getValues(property)) {
          result.add((BigDecimal) val);
        }
        return result;
      }
      else if (BuiltinPrimitiveType.INTEGER.equals(property.getType())) {
        for (Object val : modelExpression.getValues(property)) {
          BigInteger biValue = (BigInteger) val;
          result.add(BigDecimal.valueOf(biValue.longValue()));
        }
        return result;
      }
      else if (BuiltinPrimitiveType.STRING.equals(property.getType())) {
        for (Object val : modelExpression.getValues(property)) {
          try {
            Double value = Double.valueOf(val.toString());
            result.add(BigDecimal.valueOf(value));
          } catch (NumberFormatException e) {
            //ignore value
          }
        }
        return result;
      }

      Class<?> propClass = ((PrimitiveTypeExpression) property.getType()).getEncapsulatedType();
      if (Integer.class.equals(propClass)) {
        for (Object val : modelExpression.getValues(property)) {
          result.add(BigDecimal.valueOf(((Integer) val).longValue()));
        }
        return result;
      }
      else if (Double.class.equals(propClass)) {
        for (Object val : modelExpression.getValues(property)) {
          result.add(BigDecimal.valueOf(((Double) val).doubleValue()));
        }
        return result;
      }
      else if (Long.class.equals(propClass)) {
        for (Object val : modelExpression.getValues(property)) {
          result.add(BigDecimal.valueOf(((Long) val).longValue()));
        }
        return result;
      }
      //Try converting from anything
      boolean allParse = true;
      for (Object val : modelExpression.getValues(property)) {
        try {
          parseBigDecimal(val);
        } catch (ModelException e) {
          allParse = false;
          break;
        }
      }
      if (allParse) {
        for (Object val : modelExpression.getValues(property)) {
          result.add(parseBigDecimal(val));
        }
      }
    }
    else {
      //Conversion of enumeration literals (locale specific) to numeric values
      Collection<Object> objVals = modelExpression.getValues(property);
      boolean allParse = true;
      for (Object val : objVals) {
        String currentValue = ((EnumerationLiteralExpression) val).getName();
        try {
          parseBigDecimal(currentValue);
        } catch (ModelException e) {
          allParse = false;
          break;
        }
      }
      if (allParse) {
        for (Object val : objVals) {
          result.add(parseBigDecimal(((EnumerationLiteralExpression) val).getName()));
        }
      }
    }
    throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "Property type " + property.getType()
        + " can not be converted to BigDecimal for comparison.");
  }

  private static Date parseDate(Object dateValue) {
    if (dateValue instanceof Date) {
      return (Date) dateValue;
    }
    Date date = DateUtils.parseAsDate(dateValue.toString(), ElasticeamContextUtil.getCurrentContext().getLocale());
    if (date == null) {
      throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "The value " + dateValue + " can not be parsed to a date in the current locale.");
    }
    return date;
  }

  private static BigInteger parseBigInteger(Object integerValue) {
    if (integerValue instanceof BigInteger) {
      return (BigInteger) integerValue;
    }
    if (integerValue instanceof Integer) {
      return BigInteger.valueOf(((Integer) integerValue).longValue());
    }
    BigInteger val = null;
    try {
      val = new BigInteger(integerValue.toString());
    } catch (NumberFormatException e) {
      throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "The value " + integerValue + " can not be parsed to an integer.");
    }
    return val;
  }

  private static BigDecimal parseBigDecimal(Object doubleValue) {
    if (doubleValue instanceof BigDecimal) {
      return (BigDecimal) doubleValue;
    }
    if (doubleValue instanceof Double) {
      return BigDecimal.valueOf(((Double) doubleValue).longValue());
    }
    BigDecimal val = null;
    try {
      val = new BigDecimal(doubleValue.toString());
    } catch (NumberFormatException e) {
      throw new ModelException(ModelException.INCONVERTIBLE_TYPES, "The value " + doubleValue + " can not be parsed to a decimal.");
    }
    return val;
  }

  private static String getStringRepresentation(PropertyExpression<?> onProperty, ComparisonOperatorExpression operator, String value) {
    return "@" + onProperty.getPersistentName() + operator.getPersistentName() + value;
  }

  private static String getStringRepresentation(PropertyExpression<?> onProperty, ComparisonOperatorExpression operator,
                                                PropertyExpression<?> refProperty) {
    return "@" + onProperty.getPersistentName() + operator.getPersistentName() + "@" + refProperty.getPersistentName();
  }

}
