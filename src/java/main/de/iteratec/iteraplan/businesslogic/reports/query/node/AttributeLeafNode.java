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

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.attribute.AttributeValue;


/**
 * This class models an attribute leaf node in a query tree. An attribute leaf node is 
 * used for textual queries that contain a query for assigned (or not assigned) attribute 
 * values of a particular building block type for which the query is performed.
 */
public abstract class AttributeLeafNode extends AbstractLeafNode {

  private static final Logger LOGGER = Logger.getIteraplanLogger(AttributeLeafNode.class);

  /** The ID of the attribute this node represents. */
  private int                 attributeId;

  /** The comparison operator to use for the query. */
  private Comparator          comparator;

  /**
   * Constructor.
   * 
   * @param resultType The result tpye of the query; corresponds to the type of the resulting building blocks.
   * @param ext the extension that the query is part of. May be {@code null}.
   * @param id the ID of the attribute that this node represents.
   * @param comparator the comparator used in the query condition.
   * @param pattern the pattern to match used in the query condition.
   */
  public AttributeLeafNode(Type<?> resultType, Extension ext, int id, Comparator comparator, Object pattern) {
    super(resultType, ext, pattern);

    this.attributeId = id;
    this.comparator = comparator;
    init();

    if (LOGGER.isDebugEnabled()) {
      StringBuilder builder = new StringBuilder();
      builder.append("Created a " + getClass().getSimpleName() + ": ");
      builder.append("\nResult Type: " + resultType.getTypeNameDB());
      builder.append("\nExtension Type: " + ((ext == null) ? null : ext.getRequestedType().getTypeNameDB()));
      builder.append("\nAttribute ID: " + id);
      builder.append("\nComparator: " + comparator);
      builder.append("\nPattern: " + pattern);

      LOGGER.debug(builder.toString());
    }
  }

  /**
   * Returns the attribute type id.
   * 
   * @return the attribute type id
   */
  public int getAttributeId() {
    return attributeId;
  }

  /**
   * Returns the comparison operator to use for the query.
   * 
   * @return the comparison operator to use for the query
   */
  public Comparator getComparator() {
    return comparator;
  }

  public boolean isToBeRemoved(Object element) {
    return false;
  }

  /**
   * Sets the appropriate test strategy and determines whether an extra inheritance query is
   * required for this leaf node.
   */
  private void init() {
    setTest(new ConstantTrueTest());
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getFromCriteriaForInheritance(DetachedCriteria criteria) {
    throw new UnsupportedOperationException("This method must not be called on this type.");
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getWhereCriteriaFromInheritance(DetachedCriteria criteria) {
    throw new UnsupportedOperationException("This method must not be called on this type.");
  }
  
  /**
   * Returns the attribute value class. For example the value class for text is the 
   * {@link de.iteratec.iteraplan.model.attribute.TextAV}.
   * 
   * @return the attribute value class
   */
  protected abstract Class<? extends AttributeValue> getAttributeValueClass();
  
  /**
   * Creates the {@link Criterion} for the comparator from {@link #getComparator()}. Each attribute 
   * class must describe this method and define correct property names for each {@link Criterion} 
   * type. 
   * 
   * @return the {@link Criterion} for the comparator
   * @throws de.iteratec.iteraplan.common.error.IteraplanTechnicalException if the comparator is not supported
   */
  protected abstract Criterion getCriterionForComparator();
  
  /**
   * Creates the {@link DetachedCriteria} for the {@link Comparator#NO_ASSIGNMENT} comparator.
   * 
   * @param criteria the criteria to add the restrictions for
   * @param criterion the additional criterion, can be {@code null}
   * @return the {@link DetachedCriteria} for the {@link Comparator#NO_ASSIGNMENT} comparator
   */
  protected DetachedCriteria createNoAssignmentCriteria(DetachedCriteria criteria, Criterion criterion) {
    final DetachedCriteria numberAvs = DetachedCriteria.forClass(getAttributeValueClass(), "numberAV");
    numberAvs.createAlias("numberAV.attributeValueAssignments", "avas");
    numberAvs.add(Restrictions.eq("attributeType.id", Integer.valueOf(getAttributeId())));
    numberAvs.setProjection(Property.forName("avas.buildingBlock"));
    
    if (criterion != null) {
      numberAvs.add(criterion);
    }
    
    final String bbId = String.format("%s.%s", getResultTypeDBNameShortWithSuffix(), "id");
    criteria.add(Subqueries.propertyNotIn(bbId, numberAvs));
    
    return criteria;
  }
  
  /** {@inheritDoc} */
  @Override
  DetachedCriteria getWhereCriteria(DetachedCriteria criteria) {
    if (getComparator() == Comparator.NO_ASSIGNMENT) {
      return createNoAssignmentCriteria(criteria, null);
    }
    
    final String associationPath = String.format("%s.%s", getResultTypeDBNameShortWithSuffix(), "attributeValueAssignments");
    final String alias = String.format("%sAlias", "attributeValueAssignments");
    criteria.createAlias(associationPath, alias, Criteria.INNER_JOIN);
    
    final String effectivePropertyName = String.format("%s.%s", alias, "attributeValue");
    
    DetachedCriteria numberAvs = DetachedCriteria.forClass(getAttributeValueClass(), "numberAV");
    numberAvs.add(Restrictions.eq("attributeType.id", Integer.valueOf(getAttributeId())));
    numberAvs.add(this.getCriterionForComparator());
    numberAvs.setProjection( Property.forName("id") );
    criteria.add(Subqueries.propertyIn(effectivePropertyName, numberAvs));
    
    return criteria;
  }
  
}
