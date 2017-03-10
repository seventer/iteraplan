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
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import de.iteratec.iteraplan.businesslogic.reports.staticquery.PropertiesFacade;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.StaticQuery;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * Provides methods for consistency checks.
 */
public class ConsistencyCheckServiceImpl implements ConsistencyCheckService, ApplicationContextAware {

  private static final Logger  LOGGER = Logger.getIteraplanLogger(ConsistencyCheckServiceImpl.class);

  private BuildingBlockTypeDAO buildingBlockTypeDAO;
  private ApplicationContext applicationContext;

  /** 
   * {@inheritDoc} 
   */
  public StaticQuery getConsistencyCheck(String beanName) {
    try {
      return (StaticQuery) applicationContext.getBean(beanName);
    } catch (NoSuchBeanDefinitionException ex) {
      LOGGER.error("The spring bean definition with the ID '" + beanName + "' could not be found.");
      throw new IteraplanTechnicalException(IteraplanErrorMessages.CONSISTENCY_CHECK_NOT_FOUND, ex);
    }
  }

  /** {@inheritDoc} */
  public List<StaticQuery> getAllConsistencyChecks() {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.CONSISTENCY_CHECK);

    List<StaticQuery> list = new ArrayList<StaticQuery>();
    List<String> properties = PropertiesFacade.getInstance().getAllProperties();
    for (String key : properties) {
      if (key.startsWith("check")) {
        String value = PropertiesFacade.getInstance().getProperty(key);
        list.add(getConsistencyCheck(value));
      }
    }

    return list;
  }

  /** {@inheritDoc} */
  public List<TypeOfBuildingBlock> getTypeOfBuildingBlocksEligibleForAttributes() {
    List<TypeOfBuildingBlock> list = new ArrayList<TypeOfBuildingBlock>();

    for (BuildingBlockType type : buildingBlockTypeDAO.getBuildingBlockTypesEligibleForAttributes()) {
      list.add(type.getTypeOfBuildingBlock());
    }

    return list;
  }

  public void setBuildingBlockTypeDAO(BuildingBlockTypeDAO buildingBlockTypeDAO) {
    this.buildingBlockTypeDAO = buildingBlockTypeDAO;
  }

  /**{@inheritDoc}**/
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }
}
