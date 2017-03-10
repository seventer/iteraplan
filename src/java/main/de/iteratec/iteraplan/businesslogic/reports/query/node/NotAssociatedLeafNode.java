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
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Leaf node describing queries for building block instances that 
 * are not linked to other building block instances.
 */
public class NotAssociatedLeafNode extends AbstractLeafNode {

  private static final Logger LOGGER                     = Logger.getIteraplanLogger(NotAssociatedLeafNode.class);

  private int                 indexOfLastAssociationType = -1;

  /**
   * Constructor.
   */
  public NotAssociatedLeafNode(Type<?> resultType, Extension extension) {
    super(resultType, extension, null);
    if (getExtension().isWithAssociationType()) {
      indexOfLastAssociationType = getExtension().getLastAssociationTypeIndex();
      LOGGER.debug("indexOfLastAssociationType: {1}", Integer.valueOf(indexOfLastAssociationType));
    }
  }

  /**
   * Returns the distance between result type and leaf type.
   */
  @Override
  public int getExtensionSize() {
    if (getExtension() == null) {
      return 0;
    }
    return getExtension().getTypesWithJoinProperties().size();
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getFromCriteria(DetachedCriteria criteria) {
    String previousTypeNameShort = getResultTypeDBNameShortWithSuffix();
    
    int lastIndex = getExtensionSize() - 1;
    if (indexOfLastAssociationType >= 0) {
      lastIndex = indexOfLastAssociationType;
    }
    
    for (int i = 0; i < lastIndex; i++) {
      String currentTypeNameShort = getIntermediaryTypeDBNameShortWithSuffix(i);
      String path = String.format("%s.%s", previousTypeNameShort, getExtension().getIntermediaryTypeJoinProperty(i));
      criteria.createAlias(path, currentTypeNameShort, Criteria.INNER_JOIN);
      previousTypeNameShort = currentTypeNameShort;
    }

    return criteria;
  }
  
  /** {@inheritDoc} */
  @Override
  DetachedCriteria getWhereCriteria(DetachedCriteria criteria) {
    int lastIndex = getExtensionSize() - 1;
    if (indexOfLastAssociationType >= 0) {
      lastIndex = indexOfLastAssociationType;
    }
    
    if (getExtension().isIntermediaryTypeMultiEnd(lastIndex)) {
      final String intermediaryTypeNameDBShortWithSuffix = getIntermediaryTypeDBNameShortWithSuffix(lastIndex - 1);
      final String associationPath = String.format("%s.%s", intermediaryTypeNameDBShortWithSuffix, getExtension().getIntermediaryTypeJoinProperty(lastIndex));
      final String alias = String.format("%sAlias", getIntermediaryTypeDBNameShortWithSuffix(lastIndex));
      criteria.createAlias(associationPath, alias, Criteria.LEFT_JOIN);
      final String id = alias + ".id";
      criteria.add(Restrictions.isNull(id));
    }
    else {
      final String intermediaryTypeNameDBShortWithSuffix = getIntermediaryTypeDBNameShortWithSuffix(lastIndex - 1);
      final String joinProperty = getExtension().getIntermediaryTypeJoinProperty(lastIndex);
      final String associationPath = String.format("%s.%s", intermediaryTypeNameDBShortWithSuffix, joinProperty);
      criteria.add(Restrictions.isNull(associationPath));
    }
    
    return criteria;
  }

  public boolean isToBeRemoved(Object element) throws IteraplanException {
    return false;
  }

  public String toString() {
    return "Not associated: " + getResultType().getTypeNameDB() + " " + getExtension();
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getFromCriteriaForInheritance(DetachedCriteria criteria) {
    throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getWhereCriteriaFromInheritance(DetachedCriteria criteria) {
    throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
  }
}
