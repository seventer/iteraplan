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

import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import de.iteratec.hibernate.criterion.IteraplanLikeExpression;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;


/**
 * Implementation of the DAO interface {@link AttributeTypeDAO}.
 */
public class AttributeTypeDAOImpl extends GenericBaseDAO<AttributeType, Integer> implements AttributeTypeDAO {

  /** {@inheritDoc} */
  public List<AttributeType> getAttributeTypesForTypeOfBuildingBlock(TypeOfBuildingBlock type, boolean enforceReadPermissions) {
    List<AttributeType> list = executeNamedQuery("getAttributeTypesForBuildingBlockOrdered", "typeOfBuildingBlock", type);

    if (enforceReadPermissions) {
      UserContext uc = UserContext.getCurrentUserContext();
      for (Iterator<AttributeType> it = list.iterator(); it.hasNext();) {
        AttributeType at = it.next();
        AttributeTypeGroup attributeTypeGroup = at.getAttributeTypeGroup();
        if (!uc.getPerms().userHasAttrTypeGroupPermission(attributeTypeGroup, AttributeTypeGroupPermissionEnum.READ)) {
          it.remove();
        }
      }
    }

    return list;
  }

  /** {@inheritDoc} */
  public AttributeType getAttributeTypeByName(String name) {
    DetachedCriteria c = DetachedCriteria.forClass(AttributeType.class);
    //FIXME may we use eq here instead of like?
    c.add(new IteraplanLikeExpression("name", name, true));

    return findByCriteriaUniqueResult(c);
  }

  public List<AttributeType> getResponsibilityAttributeTypesReferencingUserEntityID(Integer id) {
    return executeNamedQuery("getResponsibilityAttributeTypesReferencingUserID", "id", id);
  }

  /** {@inheritDoc} */
  public <AT extends AttributeType> AT loadObjectById(Integer attributeId, Class<AT> clazz) {
    if (attributeId == null || attributeId.intValue() <= 0) {
      return null;
    }

    AT result = getHibernateTemplate().load(clazz, attributeId);
    org.hibernate.Hibernate.initialize(result);
    return result;
  }

  /** {@inheritDoc} */
  @Override
  protected String getNameAttribute() {
    return "name";
  }

  /** {@inheritDoc} */
  @Override
  protected void onBeforeDelete(AttributeType entity) {
    super.onBeforeDelete(entity);
    entity.removeAttributeTypeGroupTwoWay();
    entity.removeAllBuildingBlockTypesTwoWay();
  }

}