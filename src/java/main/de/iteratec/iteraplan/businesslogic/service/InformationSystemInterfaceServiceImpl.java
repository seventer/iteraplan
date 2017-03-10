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
import java.util.List;
import java.util.Set;

import org.springframework.beans.support.PagedListHolder;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO;


/**
 * Implementation of the service interface {@link InformationSystemInterfaceService}.
 */
public class InformationSystemInterfaceServiceImpl extends AbstractBuildingBlockService<InformationSystemInterface> implements
    InformationSystemInterfaceService {

  private InformationSystemInterfaceDAO informationSystemInterfaceDAO;
  private InformationSystemReleaseDAO   informationSystemReleaseDAO;

  @Override
  protected void checkDelete(InformationSystemInterface buildingBlock) {
    super.checkDelete(buildingBlock);
    checkDeletePermissionsCurrentPerms(buildingBlock);
  }

  @Override
  protected InformationSystemInterface onBeforeDelete(InformationSystemInterface buildingBlock) {
    super.onBeforeDelete(buildingBlock);
    InformationSystemInterface isi = informationSystemInterfaceDAO.loadObjectById(buildingBlock.getId());

    isi.getInformationSystemReleaseA().breakSeal();
    isi.getInformationSystemReleaseB().breakSeal();

    deleteRelatedTimeseries(isi);
    return isi;
  }

  @Override
  protected void onAfterDelete(InformationSystemInterface buildingBlock) {
    getAttributeValueService().removeOrphanedAttributeValuesAndAssignments();
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> getInformationSystemReleasesWithConnections(Integer id, boolean showInactive) {
    List<InformationSystemRelease> isrs = informationSystemReleaseDAO.getInformationSystemReleasesWithConnections(showInactive);
    // check if selected IS release is contained in the result list
    InformationSystemRelease isRelease = informationSystemReleaseDAO.loadObjectById(id);
    boolean contained = false;
    for (InformationSystemRelease isr : isrs) {
      if (isr.getId().equals(isRelease.getId())) {
        contained = true;
        break;
      }
    }
    if (!contained) {
      isrs.add(0, isRelease);
    }
    return isrs;
  }

  /** {@inheritDoc} */
  public List<NamedId> getNamedIdsForConnectionsOfInformationSystemRelease(Integer id) {
    Set<InformationSystemInterface> connections = informationSystemReleaseDAO.loadObjectById(id).getAllConnections();
    boolean showInactive = UserContext.getCurrentUserContext().isShowInactiveStatus();
    List<NamedId> namedIds = new ArrayList<NamedId>();
    for (InformationSystemInterface connection : connections) {

      if (!showInactive
          && ((connection.getInformationSystemReleaseA().getTypeOfStatus() == TypeOfStatus.INACTIVE) || (connection.getInformationSystemReleaseB()
              .getTypeOfStatus() == TypeOfStatus.INACTIVE))) {
        continue;
      }
      String connectionDescription = connection.getDescription();
      if ((connectionDescription != null) && (connectionDescription.length() > 0)) {
        if (connectionDescription.length() > Constants.COMBOBOX_ENTRY_MAX_LENGTH) {
          connectionDescription = connectionDescription.substring(0, Constants.COMBOBOX_ENTRY_MAX_LENGTH);
          connectionDescription = connectionDescription + Constants.COMBOBOX_ENTRY_TAIL;
        }
        connectionDescription = "  (" + connectionDescription + ")";
      }
      else {
        connectionDescription = "";
      }
      NamedId namedId = new NamedId();
      namedId.setId(connection.getId());
      if (connection.getInformationSystemReleaseA().getId().equals(id)) {
        namedId.setName(connection.getInformationSystemReleaseB().getHierarchicalName() + connectionDescription);
        namedId.setDescription(connection.getInformationSystemReleaseB().getDescription());
      }
      if (connection.getInformationSystemReleaseB().getId().equals(id)) {
        namedId.setName(connection.getInformationSystemReleaseA().getHierarchicalName() + connectionDescription);
        namedId.setDescription(connection.getInformationSystemReleaseA().getDescription());
      }
      namedIds.add(namedId);
    }
    Collections.sort(namedIds);
    return namedIds;
  }

  public void setInformationSystemInterfaceDAO(InformationSystemInterfaceDAO dao) {
    this.informationSystemInterfaceDAO = dao;
  }

  public void setInformationSystemReleaseDAO(InformationSystemReleaseDAO dao) {
    this.informationSystemReleaseDAO = dao;
  }

  /** {@inheritDoc} */
  @Override
  protected DAOTemplate<InformationSystemInterface, Integer> getDao() {
    return this.informationSystemInterfaceDAO;
  }

  /** {@inheritDoc} */
  public PagedListHolder<InformationSystemInterface> getInformationSystemInterfacesBySearch(InformationSystemInterface informationSystemInterface,
                                                                                            int page, int maxResults) {
    return this.informationSystemInterfaceDAO.getMatchesAndCountForISI(new String[] {
        informationSystemInterface.getInformationSystemReleaseA().getInformationSystem().getName(),
        informationSystemInterface.getInformationSystemReleaseB().getInformationSystem().getName() });
  }

  /** {@inheritDoc} */
  public InformationSystemInterface saveOrUpdate(InformationSystemInterface isi, boolean cleanup) {
    getAttributeValueService().saveOrUpdateAttributeValues(isi);

    isi.getInformationSystemReleaseA().breakSeal();
    isi.getInformationSystemReleaseB().breakSeal();

    return super.saveOrUpdate(isi, cleanup);
  }

}
