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

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Release;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO.SuccessionContainer;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;
import de.iteratec.iteraplan.model.sorting.ReleaseComparator;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO;
import de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO;


/**
 * A service for searching the successors for the {@link InformationSystemRelease}s and {@link TechnicalComponentRelease}s
 * building blocks.
 */
public class ReleaseSuccessorServiceImpl implements ReleaseSuccessorService {

  private static ReleaseComparator     releaseComparator = new ReleaseComparator();
  private InformationSystemReleaseDAO  informationSystemReleaseDao;
  private TechnicalComponentReleaseDAO technicalComponentReleaseDao;

  
  /** {@inheritDoc} */
  public ReleaseSuccessorDTO<InformationSystemRelease> getIsReleaseSuccessorDTO(Integer id, boolean showSuccessor) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.SUCCESSORREPORT);
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.INFORMATIONSYSTEMRELEASE);

    ReleaseSuccessorDTO<InformationSystemRelease> dto = new ReleaseSuccessorDTO<InformationSystemRelease>();
    boolean showInactive = UserContext.getCurrentUserContext().isShowInactiveStatus();

    List<InformationSystemRelease> availableReleases = informationSystemReleaseDao.getReleasesWithSuccessors(showInactive);
    dto.setAvailableReleases(availableReleases);

    if (id == null) {
      dto.setNoQueryExecuted(true);
      return dto;
    }

    List<SuccessionContainer<InformationSystemRelease>> succession;
    if (id.intValue() == -1) {
      succession = new ArrayList<SuccessionContainer<InformationSystemRelease>>();
      for (InformationSystemRelease tcr : availableReleases) {
        List<SuccessionContainer<InformationSystemRelease>> singleSuccession = generateSingleIsReleaseSuccession(tcr.getId(), showSuccessor,
            showInactive);
        succession.addAll(singleSuccession);
      }

    }
    else {
      succession = generateSingleIsReleaseSuccession(id, showSuccessor, showInactive);
    }
    dto.setSuccession(succession);

    return dto;
  }

  private List<SuccessionContainer<InformationSystemRelease>> generateSingleIsReleaseSuccession(Integer id, boolean showSuccessor,
                                                                                                boolean showInactive) {
    InformationSystemRelease firstRelease = informationSystemReleaseDao.loadObjectById(id);
    List<SuccessionContainer<InformationSystemRelease>> succession = new ArrayList<SuccessionContainer<InformationSystemRelease>>();
    if (showSuccessor) {
      findSuccessorsForRelease(firstRelease, succession, 0, showInactive);
    }
    else {
      findPredecessorsForRelease(firstRelease, succession, 0, showInactive);
    }
    return succession;
  }

  /** {@inheritDoc} */
  public ReleaseSuccessorDTO<TechnicalComponentRelease> getTcReleaseSuccessorDTO(Integer id, boolean showSuccessor) {
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.SUCCESSORREPORT);
    UserContext.getCurrentPerms().assureFunctionalPermission(TypeOfFunctionalPermission.TECHNICALCOMPONENTRELEASES);

    ReleaseSuccessorDTO<TechnicalComponentRelease> dto = new ReleaseSuccessorDTO<TechnicalComponentRelease>();
    boolean showInactive = UserContext.getCurrentUserContext().isShowInactiveStatus();

    List<TechnicalComponentRelease> availableReleases = technicalComponentReleaseDao.getReleasesWithSuccessors(showInactive);
    dto.setAvailableReleases(availableReleases);

    if (id == null) {
      dto.setNoQueryExecuted(true);
      return dto;
    }

    List<SuccessionContainer<TechnicalComponentRelease>> succession;
    if (id.intValue() == -1) {
      succession = new ArrayList<SuccessionContainer<TechnicalComponentRelease>>();
      for (TechnicalComponentRelease tcr : availableReleases) {
        List<SuccessionContainer<TechnicalComponentRelease>> singleSuccession = generateSingleTcReleaseSuccession(tcr.getId(), showSuccessor,
            showInactive);
        succession.addAll(singleSuccession);
      }

    }
    else {
      succession = generateSingleTcReleaseSuccession(id, showSuccessor, showInactive);
    }
    dto.setSuccession(succession);

    return dto;
  }

  private List<SuccessionContainer<TechnicalComponentRelease>> generateSingleTcReleaseSuccession(Integer id, boolean showSuccessor,
                                                                                                 boolean showInactive) {
    TechnicalComponentRelease firstRelease = technicalComponentReleaseDao.loadObjectById(id);

    List<SuccessionContainer<TechnicalComponentRelease>> succession = new ArrayList<SuccessionContainer<TechnicalComponentRelease>>();
    if (showSuccessor) {
      findSuccessorsForRelease(firstRelease, succession, 0, showInactive);
    }
    else {
      findPredecessorsForRelease(firstRelease, succession, 0, showInactive);
    }
    return succession;
  }

  /**
   * Recursively finds the list of successor releases of the given {@link InformationSystemRelease}
   * and stores them in the given list of {@link SuccessionContainer}s.
   * 
   * @param release
   *          The release for which its successors are to be found.
   * @param succession
   *          The list containing the succession information found so far. Acts as an output
   *          parameter.
   * @param currentSuccessionLevel
   *          The current succession level of the given release.
   * @param showInactive
   *          If {@code true}, inactive releases are considered as well.
   */
  private <T extends Sequence<T> & Release> void findSuccessorsForRelease(T release, List<SuccessionContainer<T>> succession,
                                                                          int currentSuccessionLevel, boolean showInactive) {

    int level = currentSuccessionLevel;

    SuccessionContainer<T> container = new SuccessionContainer<T>();
    container.setRelease(release);
    container.setLevel(level);
    succession.add(container);

    if (!release.getSuccessors().isEmpty()) {
      level++;

      // iteration over a Set does not return the elements in a particular order.
      // Hence it is saved in a list and then sorted. This ensures a constant order of the returned
      // elements.

      List<T> list = new ArrayList<T>(release.getSuccessors());
      Collections.sort(list, releaseComparator);

      for (T successor : list) {
        if (!showInactive && isReleaseInactive(successor)) {
          break;
        }
        findSuccessorsForRelease(successor, succession, level, showInactive);
      }
    }
  }

  /**
   * Recursively finds the list of successor releases of the given {@link InformationSystemRelease}
   * and stores them in the given list of {@link SuccessionContainer}s.
   * 
   * @param release
   *          The release for which its successors are to be found.
   * @param succession
   *          The list containing the succession information found so far. Acts as an output
   *          parameter.
   * @param currentSuccessionLevel
   *          The current succession level of the given release.
   * @param showInactive
   *          If {@code true}, inactive releases are considered as well.
   */
  private <T extends Sequence<T> & Release> void findPredecessorsForRelease(T release, List<SuccessionContainer<T>> succession,
                                                                            int currentSuccessionLevel, boolean showInactive) {

    int level = currentSuccessionLevel;

    SuccessionContainer<T> container = new SuccessionContainer<T>();
    container.setRelease(release);
    container.setLevel(level);
    succession.add(container);

    if (!release.getPredecessors().isEmpty()) {
      level++;

      // iteration over a Set does not return the elements in a particular order.
      // Hence it is saved in a list and then sorted. This ensures a constant order of the returned
      // elements.

      List<T> list = new ArrayList<T>(release.getPredecessors());
      Collections.sort(list, releaseComparator);

      for (T predecessor : list) {
        if (!showInactive && isReleaseInactive(predecessor)) {
          break;
        }
        findPredecessorsForRelease(predecessor, succession, level, showInactive);
      }
    }
  }

  private boolean isReleaseInactive(IdentityEntity entity) {
    if (entity instanceof InformationSystemRelease) {
      InformationSystemRelease isr = (InformationSystemRelease) entity;
      return isr.getTypeOfStatus() == InformationSystemRelease.TypeOfStatus.INACTIVE;
    }
    else if (entity instanceof TechnicalComponentRelease) {
      TechnicalComponentRelease tcr = (TechnicalComponentRelease) entity;
      return tcr.getTypeOfStatus() == TechnicalComponentRelease.TypeOfStatus.INACTIVE;
    }
    else {
      throw new IllegalArgumentException("Only InformationSystemRelease and TechnicalComponentRelease are supported");
    }
  }

  public void setInformationSystemReleaseDAO(InformationSystemReleaseDAO dao) {
    this.informationSystemReleaseDao = dao;
  }

  public void setTechnicalComponentReleaseDAO(TechnicalComponentReleaseDAO technicalComponentReleaseDao) {
    this.technicalComponentReleaseDao = technicalComponentReleaseDao;
  }

}