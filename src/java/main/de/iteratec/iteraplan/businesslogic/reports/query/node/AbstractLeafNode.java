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
package de.iteratec.iteraplan.businesslogic.reports.query.node;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import de.iteratec.hibernate.criterion.IteraplanLikeExpression;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.RuntimePeriodDelegate;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.persistence.util.SqlHqlStringUtils;


/**
 * Base class for all leaf nodes in the query condition tree.
 */
public abstract class AbstractLeafNode extends Node {

  private static final Logger LOGGER = Logger.getIteraplanLogger(AbstractLeafNode.class);
  
  /** The building block type of the result. */
  private Type<?>             resultType;

  /** The extension to use for this leaf node. Is null if the queried property belongs to resultType. */
  private Extension           extension;

  /** The pattern for which a match should be tried. */
  private Object              pattern;

  /** Test strategy to decide whether an instance should be part of the result set */
  private Test                test;

  /** This leaf might require an extra query due to inheritance or other issues. */
  private boolean             additionalQueryRequired;

  
  AbstractLeafNode(Type<?> resultType, Extension extension, Object pattern) {
    this.resultType = resultType;
    this.extension = extension;
    this.pattern = pattern;

    init();
  }

  //@SuppressWarnings("boxing")
  public final Object getProcessedPattern() {
    if (getPattern() instanceof Enum) {
      return ((Enum<?>) getPattern()).toString();
    }
    else if (getPattern() instanceof String && !("false".equals(getPattern()) || "true".equals(getPattern()))) {
      String processedPattern = SqlHqlStringUtils.processFilterForSql(((String) getPattern()).toLowerCase());
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("Processed pattern for SQL: " + processedPattern);
      }
      return processedPattern;
    }
    else if("false".equals(getPattern()) || "true".equals(getPattern())) {
      return Boolean.valueOf(getPattern().toString());
    }
    else if (null == getPattern()) {
      return "";
    }

    return getPattern();
  }

  /**
   * Returns the query extension used for this leaf node.
   *
   * @return See method description.
   */
  public Extension getExtension() {
    return extension;
  }

  /**
   * Returns the number of joins between the result type and the leaf type.
   *
   * @return See method description.
   */
  public int getExtensionSize() {
    if (extension == null) {
      return 0;
    }

    return extension.getTypesWithJoinProperties().size();
  }

  private Class<?> getCriteriaClass() {
    try {
      return Class.forName("de.iteratec.iteraplan.model." + getResultType().getTypeNameDB());
    } catch (ClassNotFoundException e) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR, e);
    }
  }

  /**
   * Returns the {@link DetachedCriteria} instance with the restrictions, required to filter the data, 
   * loaded from Database. The extending classes must implement the {@link #getSelectCriteria(Class)}, 
   * {@link #getFromCriteria(DetachedCriteria)} and {@link #getWhereCriteria(DetachedCriteria)} methods, 
   * in order to create valid query.
   * 
   * @return the {@link DetachedCriteria} instance
   */
  public final DetachedCriteria getCriteria() {
    DetachedCriteria criteria = getSelectCriteria(getCriteriaClass());
    criteria = getFromCriteria(criteria);
    criteria = getWhereCriteria(criteria);

    return criteria;
  }

  public final DetachedCriteria getCriteriaForInheritance() {
    DetachedCriteria criteria = getSelectCriteria(getCriteriaClass());
    criteria = getFromCriteriaForInheritance(criteria);
    criteria = getWhereCriteriaFromInheritance(criteria);

    return criteria;
  }

  /**
   * Returns the search pattern used for the query.
   *
   * @return See method description.
   */
  public Object getPattern() {
    return pattern;
  }

  /**
   * Returns the result type of the query.
   *
   * @return See method description.
   */
  public Type<?> getResultType() {
    return resultType;
  }

  /**
   * Returns true, if an additional query is required to perfom the complete query. Otherwise, false
   * is returned.
   *
   * @return See method description.
   */
  public boolean isAdditionalQueryRequired() {
    return additionalQueryRequired;
  }

  /**
   * Returns true, if the given object does not pass the query conditions stored in the leaf.
   * Otherwise, false is returned.
   *
   * @param resultElement
   *          Potential result of the query.
   * @return See method description.
   */
  public abstract boolean isToBeRemoved(Object resultElement);

  /**
   * Initialize Leaf Node Class with appropriate Test and property settings.
   */
  private void init() {
    additionalQueryRequired = false;
    setTest(new ConstantTrueTest());
  }

  /**
   * Checks whether at least one of the given Iterator elements matches the test
   *
   * @param leafElements
   *          The iterator with building block instances.
   * @return true if and only if at least one instance passes the test.
   */
  boolean containsMatch(Set<?> leafElements) {
    Iterator<?> it = leafElements.iterator();
    while (it.hasNext()) {
      Object current = it.next();
      if (isMatch(current)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the {@link Criterion} for the specified {@code effectivePropertyName} and {@code comparator}.
   * 
   * @param effectivePropertyName the property name path
   * @param comparator the comparator describing the compare operation
   * @param attrType string representation of the property's attribute type as in {@link BBAttribute#getTypeOfAttribute(String)} 
   * @return the newly created {@link Criterion} for the specified {@code comparator} or {@code null} if the 
   *    comparator is not supported
   */
  protected Criterion getCriterionForComparator(String effectivePropertyName, Comparator comparator, String attrType) {
    Criterion criterion = null;
    switch (comparator) {
      case EQ:
        criterion = Restrictions.eq(effectivePropertyName, getProcessedPattern());
        break;
      case GEQ:
        criterion = Restrictions.ge(effectivePropertyName, getProcessedPattern());
        break;
      case LEQ:
        criterion = Restrictions.le(effectivePropertyName, getProcessedPattern());
        break;
      case GT:
        criterion = Restrictions.gt(effectivePropertyName, getProcessedPattern());
        break;
      case LT:
        criterion = Restrictions.lt(effectivePropertyName, getProcessedPattern());
        break;
      case LIKE:
        criterion = new IteraplanLikeExpression(effectivePropertyName, getProcessedPattern().toString(), true);
        break;
      case NOT_LIKE:
        criterion = Restrictions.not(new IteraplanLikeExpression(effectivePropertyName, getProcessedPattern().toString(), true));
        break;
      case IS:
        // see Type#getSpecialPropertyHQLStrings
        criterion = "null".equals(getPattern()) ? Restrictions.isNull(effectivePropertyName) : Restrictions.isNotNull(effectivePropertyName);
        break;
      case ANY_ASSIGNMENT:
        criterion = getAnyAssignmentCriterion(effectivePropertyName, attrType);
        break;
      case NO_ASSIGNMENT:
        criterion = getNoAssignmentCriterion(effectivePropertyName, attrType);
        break;
      case NEQ:
        criterion = Restrictions.ne(effectivePropertyName, getProcessedPattern());
        break;
      default:
        break;
    }

    return criterion;
  }

  private Criterion getAnyAssignmentCriterion(String effectivePropertyName, String attrType) {
    Criterion criterion;
    criterion = Restrictions.not(Restrictions.isNull(effectivePropertyName));
    if (!BBAttribute.FIXED_ATTRIBUTE_DATETYPE.equals(attrType)) {
      criterion = Restrictions.and(criterion, Restrictions.not(Restrictions.eq(effectivePropertyName, Constants.DB_NU1L)));
    }
    return criterion;
  }

  private Criterion getNoAssignmentCriterion(String effectivePropertyName, String attrType) {
    Criterion criterion = Restrictions.isNull(effectivePropertyName);
    if (!BBAttribute.FIXED_ATTRIBUTE_DATETYPE.equals(attrType)) {
      criterion = Restrictions.or(criterion, Restrictions.eq(effectivePropertyName, Constants.DB_NU1L));
    }
    return criterion;
  }

  /**
   * Returns the short name of an intermediary type concatenated with a positional suffix.
   *
   * @param index
   *          The index of the intermediary type in the chain from result to leaf type.
   * @return The suffixed type name.
   */
  final String getIntermediaryTypeDBNameShortWithSuffix(int index) {
    if (index == -1) {
      return getResultTypeDBNameShortWithSuffix();
    }

    int suffix = index + 1;
    return getExtension().getIntermediaryType(index).getTypeNameDBShort() + suffix;
  }

  /**
   * @return The type of the query leaf.
   */
  final Type<?> getLeafType() {
    if (getExtensionSize() > 0) {
      return getExtension().getRequestedType();
    }
    
    return getResultType();
  }

  /**
   * Returns the short name of the leaf type concatenated with a positional suffix.
   *
   * @return The suffixed type name.
   */
  final String getLeafTypeDBNameShortWithSuffix() {
    return getLeafType().getTypeNameDBShort() + getExtensionSize();
  }

  /**
   * Returns the short name of the result type concatenated with a positional suffix.
   *
   * @return The suffixed type name.
   */
  final String getResultTypeDBNameShortWithSuffix() {
    return resultType.getTypeNameDBShort() + "0";
  }

  /**
   * @return Returns the test.
   */
  Test getTest() {
    return test;
  }

  /**
   * Creates the newly created instance of the {@link DetachedCriteria} for the specified {@code resultTypeClass} and 
   * alias from {@link #getResultTypeDBNameShortWithSuffix()}.
   * 
   * @param resultTypeClass the class to be selected in a query
   * @return the newly created instance of the {@link DetachedCriteria}
   */
  final DetachedCriteria getSelectCriteria(Class<?> resultTypeClass) {
    return DetachedCriteria.forClass(resultTypeClass, getResultTypeDBNameShortWithSuffix() /* alias */);
  }

  /**
   * Adds the restrictions to the specified {@code criteria} in order to filter the data.
   *
   * @param criteria to be ammended
   * @return criteria to be executed within session scope
   */
  abstract DetachedCriteria getWhereCriteria(DetachedCriteria criteria);

  /**
   * Adds the restrictions to the specified {@code criteria} in order to filter the data.
   *
   * @param criteria to be ammended
   * @return criteria to be executed within session scope
   */
  abstract DetachedCriteria getWhereCriteriaFromInheritance(DetachedCriteria criteria);
  
  /**
   * Adds the alias's to the specified {@code criteria} and specifies the join types, used for those 
   * alias. The alias can be later used in {@link #getWhereCriteria(DetachedCriteria)} method to filter
   * the data.
   *
   * @param criteria to be ammended
   * @return criteria to be executed within session scope
   */
  DetachedCriteria getFromCriteria(DetachedCriteria criteria) {
    String previousTypeNameShort = getResultTypeDBNameShortWithSuffix();
    for (int i = 0; i < getExtensionSize(); i++) {
      String currentTypeNameShort = getIntermediaryTypeDBNameShortWithSuffix(i);
      String path = String.format("%s.%s", previousTypeNameShort, getExtension().getIntermediaryTypeJoinProperty(i));
      criteria.createAlias(path, currentTypeNameShort, Criteria.INNER_JOIN);
      previousTypeNameShort = currentTypeNameShort;
    }

    return criteria;
  }
  
  /**
   * Adds the alias's to the specified {@code criteria} and specifies the join types, used for those 
   * alias. The alias can be later used in {@link #getWhereCriteria(DetachedCriteria)} method to filter
   * the data.
   *
   * @param criteria to be ammended
   * @return criteria to be executed within session scope
   */
  abstract DetachedCriteria getFromCriteriaForInheritance(DetachedCriteria criteria);

  /**
   * Checks whether the given element matches the test.
   *
   * @param leafElement
   *          The building block instance to check.
   * @return true if and only if instance passes test.
   */
  boolean isMatch(Object leafElement) {
    return getTest().match(leafElement);
  }

  /**
   * @param additionalQueryRequired the additionalQueryRequired to set.
   */
  void setAdditionalQueryRequired(boolean additionalQueryRequired) {
    this.additionalQueryRequired = additionalQueryRequired;
  }

  /**
   * @param test
   *          The test to set.
   */
  final void setTest(Test test) {
    this.test = test;
  }

  protected void setPattern(Object pattern) {
    this.pattern = pattern;
  }

  /**
   * Constant test that returns false in every case.
   */
  protected static final class ConstantFalseTest implements Test {

    public boolean match(Object objectToTest) {
      return false;
    }
  }

  /**
   * Constant test that returns true in every case.
   */
  static final class ConstantTrueTest implements Test {

    public boolean match(Object objectToTest) {
      boolean match = true;
      // logger.debug("ConstantTrueTest:match() successful? " + match);
      return match;
    }
  }

  /**
   * Test that checks if a given end date is somewhen inside the inherited date range of the object.
   */
  static final class DateEntityEndDateTest implements Test {

    private final Comparator comparator;
    private final Date       endDate;

    public DateEntityEndDateTest(Comparator comparator, Date endDate) {
      this.comparator = comparator;
      this.endDate = endDate;
    }

    public boolean match(Object objectToTest) {
      checkForValidComparator();
      RuntimePeriodDelegate entity = (RuntimePeriodDelegate) objectToTest;
      boolean match = false;

      boolean insideDateRange = entity.runtimeOverlapsPeriod(new RuntimePeriod(null, this.endDate));
      if (insideDateRange) {
        match = true;
      }
      return match;
    }

    /**
     * Checks whether the test can be carried out correctly regarding to comparators.
     */
    private void checkForValidComparator() {
      if (!(comparator == Comparator.GEQ)) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
    }
  }

  /**
   * Test that checks if a given start date is somewhen inside the inherited date range of the
   * object.
   */
  static final class DateEntityStartDateTest implements Test {

    private final Comparator comparator;
    private final Date       startDate;

    public DateEntityStartDateTest(Comparator comparator, Date startDate) {
      this.comparator = comparator;
      this.startDate = startDate;
    }

    public boolean match(Object objectToTest) {
      checkForValidComparator();
      RuntimePeriodDelegate entity = (RuntimePeriodDelegate) objectToTest;
      boolean match = false;

      boolean insideDateRange = entity.runtimeOverlapsPeriod(new RuntimePeriod(startDate, null));
      if (insideDateRange) {
        match = true;
      }
      return match;
    }

    /**
     * Checks whether the test can be carried out correctly regarding to comparators.
     */
    private void checkForValidComparator() {
      if (!(comparator == Comparator.LEQ)) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }
    }
  }

  /**
   * Interface for all programmatic tests that are concerned with inheritance (strategy pattern).
   */
  interface Test {
    boolean match(Object objectToTest);
  }

}