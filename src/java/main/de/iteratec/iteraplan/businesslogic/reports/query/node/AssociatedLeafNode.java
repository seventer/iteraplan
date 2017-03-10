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

import org.hibernate.criterion.DetachedCriteria;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanException;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;


/**
 * Leaf node describing queries that may have an extension, but do not restrict the properties or
 * associated attribute values of the queried elements in any way.
 */
public class AssociatedLeafNode extends AbstractLeafNode {

  /**
   * Constructor.
   * 
   * @param resultType
   * @param extension
   */
  public AssociatedLeafNode(Type<?> resultType, Extension extension) {
    super(resultType, extension, null);
  }

  public boolean isToBeRemoved(Object element) throws IteraplanException {
    return false;
  }

  public String toString() {
    return "Associated: " + getResultType().getTypeNameDB() + " " + getExtension();
  }

  /** {@inheritDoc} */
  @Override
  DetachedCriteria getWhereCriteria(DetachedCriteria criteria) {
    return criteria;
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
