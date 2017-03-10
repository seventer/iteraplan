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
package de.iteratec.iteraplan.businesslogic.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import de.iteratec.hibernate.criterion.IteraplanLikeExpression;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;
import de.iteratec.iteraplan.persistence.dao.UserEntityDAO;
import de.iteratec.iteraplan.persistence.util.SqlHqlStringUtils;


/**
 * Implementation of the service interface {@link UserEntityService}.
 */
public class UserEntityServiceImpl extends AbstractEntityService<UserEntity, Integer> implements UserEntityService {

  private GeneralBuildingBlockDAO generalBuildingBlockDAO;
  private UserEntityDAO           userEntityDAO;

  /** {@inheritDoc} */
  public List<BuildingBlock> loadOwnedBuildingBlocks(UserEntity userEntity, boolean considerInheritedRights) {
    if (userEntity == null) {
      throw new IllegalArgumentException("The parameter is required and must not be null.");
    }

    if (userEntity.getId() == null) {
      return new ArrayList<BuildingBlock>(0);
    }

    Set<Integer> set = new HashSet<Integer>();
    set.add(userEntity.getId());
    if (considerInheritedRights) {
      set.addAll(userEntity.getUserGroupHierarchyAsIDs());
    }

    // Load the list of owned building block IDs for the complete hierarchy of user entities.
    List<Integer> identifiers = userEntityDAO.loadOwnedBuildingBlockIDs(set);

    // Load the building blocks itself. Due to the current ORM strategy in iteraplan the objects
    // MUST be loaded separately by mapped class. If the base class was used, a database join too
    // big for MySQL would be generated.
    List<BuildingBlock> owned = generalBuildingBlockDAO.loadBuildingBlocks(identifiers);
    Collections.sort(owned, new BuildingBlockComparator());

    return owned;
  }

  public void setGeneralBuildingBlockDAO(GeneralBuildingBlockDAO generalBuildingBlockDAO) {
    this.generalBuildingBlockDAO = generalBuildingBlockDAO;
  }

  public void setUserEntityDAO(UserEntityDAO userEntityDAO) {
    this.userEntityDAO = userEntityDAO;
  }

  /** {@inheritDoc} */
  @Override
  protected DAOTemplate<UserEntity, Integer> getDao() {
    return userEntityDAO;
  }

  /** {@inheritDoc} */
  public List<UserEntity> getUserEntityBySearch(UserEntity userEntity) {
    //FIXME push this down to DAO and additionally escape T-SQL control characters
    DetachedCriteria crit = DetachedCriteria.forClass(UserEntity.class);

    if (userEntity != null && !StringUtils.isEmpty(userEntity.getIdentityString())) {
      String sqlSearchTerm = SqlHqlStringUtils.processGuiFilterForSql(userEntity.getIdentityString());

      crit.add(Restrictions.or(new IteraplanLikeExpression("name", sqlSearchTerm, true),
          new IteraplanLikeExpression("loginName", sqlSearchTerm, true)));
    }
    // don't add order constraint to count criteria!
    crit.addOrder(Order.asc("loginName"));
    crit.addOrder(Order.asc("name"));

    return this.userEntityDAO.findByCriteria(crit);
  }

}