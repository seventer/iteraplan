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

import java.util.Collection;
import java.util.List;

import org.springframework.orm.hibernate3.HibernateObjectRetrievalFailureException;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.GeneralBuildingBlockDAO;


/**
 * Implementation of the service interface {@link GeneralBuildingBlockService}.
 */
public class GeneralBuildingBlockServiceImpl extends AbstractBuildingBlockService<BuildingBlock> implements GeneralBuildingBlockService {

  private static final Logger     LOGGER = Logger.getIteraplanLogger(GeneralBuildingBlockServiceImpl.class);

  private GeneralBuildingBlockDAO generalBuildingBlockDAO;

  public void setGeneralBuildingBlockDAO(GeneralBuildingBlockDAO generalBuildingBlockDAO) {
    this.generalBuildingBlockDAO = generalBuildingBlockDAO;
  }

  @Override
  protected DAOTemplate<BuildingBlock, Integer> getDao() {
    return generalBuildingBlockDAO;
  }

  /**
   * TODO agu remove this method. This method can be replaced with {@link BuildingBlockServiceLocator#getService(TypeOfBuildingBlock)}
   */
  public List<BuildingBlock> getBuildingBlocksByType(TypeOfBuildingBlock type) {
    return generalBuildingBlockDAO.getBuildingBlocksByType(type);
  }

  /** {@inheritDoc} */
  public <T extends BuildingBlock> List<BuildingBlock> refreshBuildingBlocks(Collection<T> buildingBlocks) {
    List<BuildingBlock> refreshedList = CollectionUtils.arrayList();
    for (BuildingBlock bb : buildingBlocks) {
      try {
        refreshedList.add(generalBuildingBlockDAO.getBuildingBlock(bb.getId(), bb.getClass()));
      } catch (HibernateObjectRetrievalFailureException e) {
        LOGGER.warn("selected building block not found in database: {0}", bb.getHierarchicalName(), e);
      }
    }
    return refreshedList;
  }

}