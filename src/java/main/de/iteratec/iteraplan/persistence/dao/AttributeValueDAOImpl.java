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
package de.iteratec.iteraplan.persistence.dao;

import java.util.List;

import de.iteratec.iteraplan.model.attribute.AttributeValue;


public class AttributeValueDAOImpl extends GenericBaseDAO<AttributeValue, Integer> implements AttributeValueDAO {

  private static final String ATTRIBUTE_TYPE_ID_LABEL = "attributeTypeId";

  /** {@inheritDoc} */
  public boolean checkForBuildingBlocksWithMoreThanOneEnumAVs(Integer enumAttributeTypeId) {
    List<?> result = getHibernateTemplate().findByNamedQueryAndNamedParam("getBuildingBlocksWithMoreThanOneAVsForOneAT", ATTRIBUTE_TYPE_ID_LABEL,
        enumAttributeTypeId);
    if (result.size() > 0) {
      return true;
    }
    return false;
  }

  /** {@inheritDoc} */
  public Integer deleteOrphanedDateAttributeValues() {
    int updatedEntities = getHibernateTemplate().bulkUpdate("delete DateAV dav where dav.attributeValueAssignments is empty");
    return Integer.valueOf(updatedEntities);
  }

  /** {@inheritDoc} */
  public Integer deleteOrphanedNumberAttributeValues() {
    int updatedEntities = getHibernateTemplate().bulkUpdate("delete NumberAV nav where nav.attributeValueAssignments is empty");
    return Integer.valueOf(updatedEntities);
  }

  /** {@inheritDoc} */
  public Integer deleteOrphanedTextAttributeValues() {
    int updatedEntities = getHibernateTemplate().bulkUpdate("delete TextAV tav where tav.attributeValueAssignments is empty");
    return Integer.valueOf(updatedEntities);
  }

  /** {@inheritDoc} */
  public <AV extends AttributeValue> AV loadObjectById(Integer id, Class<AV> clazz) {
    if (id == null) {
      return null;
    }
    if (id.intValue() <= 0) {
      return null;
    }

    AV result = getHibernateTemplate().load(clazz, id);
    org.hibernate.Hibernate.initialize(result);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    throw new UnsupportedOperationException("This operation is not supported for this type.");
  }
}
