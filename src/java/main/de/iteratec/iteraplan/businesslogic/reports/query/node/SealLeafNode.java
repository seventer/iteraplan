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

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.Seal;
import de.iteratec.iteraplan.model.SealState;


/**
 * Node class representing queries for filtering the building blocks by their {@link SealState}.
 */
public class SealLeafNode extends AbstractLeafNode {

  /** The name of the property. */
  private final String propertyName;

  public SealLeafNode(Type<?> resultType, Extension extension, String propertyName, SealState pattern) {
    super(resultType, extension, Preconditions.checkNotNull(pattern));
    this.propertyName = Preconditions.checkNotNull(propertyName);
  }

  /**
   * @return Returns the propertyName.
   */
  final String getPropertyName() {
    return propertyName;
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getFromCriteriaForInheritance(DetachedCriteria criteria) {
    return getFromCriteria(criteria);
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getWhereCriteriaFromInheritance(DetachedCriteria criteria) {
    return getWhereCriteria(criteria);
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getWhereCriteria(DetachedCriteria criteria) {
    String effectivePropertyName = null;

    if (getExtensionSize() == 0) {
      effectivePropertyName = String.format("%s.%s", getResultTypeDBNameShortWithSuffix(), getPropertyName());
    }
    else {
      effectivePropertyName = String.format("%s.%s", getLeafTypeDBNameShortWithSuffix(), getPropertyName());
    }

    final String[] properties = StringUtils.split(getPropertyName(), '.');
    if (!getResultType().isSpecialProperty(getPropertyName()) && properties != null && properties.length > 1) {
      final String associationPath = String.format("%s.%s", getResultTypeDBNameShortWithSuffix(), properties[0]);
      final String alias = String.format("%sAlias", properties[0]);
      criteria.createAlias(associationPath, alias);
      effectivePropertyName = String.format("%s.%s", alias, properties[1]);
    }

    SealState sealState = (SealState) getPattern();
    switch (sealState) {
      case VALID:
        Criterion validCriterion = getCriterionForComparator(effectivePropertyName, Comparator.EQ, null);
        Criterion notOutdatedCriterion = Restrictions.not(getOutdatedCriterion(effectivePropertyName));
        criteria.add(Restrictions.and(validCriterion, notOutdatedCriterion));
        break;
      case INVALID:
        criteria.add(getCriterionForComparator(effectivePropertyName, Comparator.EQ, null));
        break;
      case NOT_AVAILABLE:
        Criterion isNull = Restrictions.isNull(effectivePropertyName);
        Criterion eqCriterion = getCriterionForComparator(effectivePropertyName, Comparator.EQ, null);
        criteria.add(Restrictions.or(isNull, eqCriterion));
        break;
      case OUTDATED:
        criteria.add(getOutdatedCriterion(effectivePropertyName));
        break;
      default:
        throw new IllegalStateException("The seal state " + sealState + " is not supported!");
    }

    return criteria;
  }

  /**
   * Returns the Criterion for the outdated entities.
   * 
   * @param effectivePropertyName the field name representing the seal state
   * @return the Criterion for the outdated entities
   */
  private Criterion getOutdatedCriterion(String effectivePropertyName) {
    int expirationInDays = IteraplanProperties.getIntProperty(IteraplanProperties.SEAL_EXPIRATION_DAYS);
    Date minusDays = new DateTime().minusDays(expirationInDays).toDate();
    String idPropertyName = String.format("%s.%s", getResultTypeDBNameShortWithSuffix(), "id");
    
    final DetachedCriteria maxSealDate = DetachedCriteria.forClass(Seal.class, "seal");
    maxSealDate.setProjection(Projections.max("seal.date"));
    maxSealDate.add(Restrictions.eqProperty("seal.bb", idPropertyName));
    
    final DetachedCriteria lastSeal = DetachedCriteria.forClass(Seal.class, "lastSeal");
    lastSeal.add(Subqueries.propertyEq("lastSeal.date", maxSealDate));
    lastSeal.add(Restrictions.eqProperty("lastSeal.bb", idPropertyName));
    lastSeal.add(Restrictions.le("lastSeal.date", minusDays));
    lastSeal.setProjection(Projections.distinct(Property.forName("lastSeal.bb")));

    Criterion outdatedCriterion = Subqueries.propertyIn(idPropertyName, lastSeal);
    Criterion valid = Restrictions.eq(effectivePropertyName, SealState.VALID.toString());

    return Restrictions.and(valid, outdatedCriterion);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isToBeRemoved(Object element) {
    return false;
  }
}
