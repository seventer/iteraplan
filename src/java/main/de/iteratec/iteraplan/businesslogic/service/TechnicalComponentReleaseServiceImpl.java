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

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import de.iteratec.hibernate.criterion.IteraplanLikeExpression;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.Release;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.sorting.IdentityStringComparator;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO;
import de.iteratec.iteraplan.persistence.dao.TechnicalComponentDAO;
import de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAO;


/**
 * Implementation of {@link TechnicalComponentReleaseService}.
 */
public class TechnicalComponentReleaseServiceImpl extends AbstractBuildingBlockService<TechnicalComponentRelease> implements
    TechnicalComponentReleaseService {

  private Tcr2IeAssociationService      tcr2IeAssociationService;
  private InformationSystemInterfaceDAO informationSystemInterfaceDAO;
  private TechnicalComponentDAO         technicalComponentDAO;
  private TechnicalComponentReleaseDAO  technicalComponentReleaseDAO;

  private static final String           VERSION = "version";

  /**
   * {@inheritDoc}
   * This implementation additionally checks whether there are still {@link InformationSystemInterface}s
   * assigned to the TCR to delete, and if yes, throws an {@link IteraplanBusinessException}.
   */
  @Override
  protected void checkDelete(TechnicalComponentRelease tcrToDelete) {
    super.checkDelete(tcrToDelete);

    checkDeletePermissionsCurrentPerms(tcrToDelete);

    //    TechnicalComponentRelease tcr = technicalComponentReleaseDAO.loadObjectById(tcrToDelete.getId());
    TechnicalComponent tc = tcrToDelete.getTechnicalComponent();

    // check for associated connections
    if (tc.isAvailableForInterfaces()) {
      List<InformationSystemInterface> list = informationSystemInterfaceDAO.getConnectionsImplementedByTechnicalComponentRelease(tcrToDelete);
      if (list.size() > 0) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.CONNECTION_USES_TCRELEASE);
      }
    }
  }

  @Override
  protected TechnicalComponentRelease onBeforeDelete(TechnicalComponentRelease buildingBlock) {
    super.onBeforeDelete(buildingBlock);
    deleteRelatedTimeseries(buildingBlock);
    return buildingBlock;
  }

  @Override
  protected void onAfterDelete(TechnicalComponentRelease buildingBlock) {
    this.deleteOrphanedTechnicalComponents();
    getAttributeValueService().removeOrphanedAttributeValuesAndAssignments();
  }

  /**
   * check if there are exist any TechnicalComponents that have no Technical Component Releases
   * anymore (as those could be deleted by cascade) and delete these respective orphan TechnicalComponents
   */
  private void deleteOrphanedTechnicalComponents() {
    for (TechnicalComponent orphanTC : technicalComponentDAO.getOrphanTechnicalComponents()) {
      technicalComponentDAO.delete(orphanTC);
    }
  }

  /** {@inheritDoc} */
  public boolean doesDuplicateReleaseExist(Release tcr) {
    return technicalComponentReleaseDAO.doesObjectWithDifferentIdExist(tcr.getId(), tcr.getReleaseName());
  }

  /** {@inheritDoc} */
  public List<TechnicalComponentRelease> eligibleForConnections(List<TechnicalComponentRelease> toExclude, boolean showInactive) {
    return technicalComponentReleaseDAO.eligibleForConnections(toExclude, showInactive);
  }

  /** {@inheritDoc} */
  public List<TechnicalComponentRelease> filter(List<TechnicalComponentRelease> toExclude, boolean showInactive) {
    List<TechnicalComponentRelease> list = technicalComponentReleaseDAO.filter(toExclude, showInactive);
    Collections.sort(list, new IdentityStringComparator());

    return list;
  }

  /** {@inheritDoc} */
  public List<TechnicalComponentRelease> loadElementList(boolean showInactive) {
    return technicalComponentReleaseDAO.filter(null, showInactive);
  }

  /** {@inheritDoc} */
  @Override
  public List<TechnicalComponentRelease> findByNames(Set<String> names) {
    if (names.isEmpty()) {
      return Collections.emptyList();
    }

    DetachedCriteria criteria = DetachedCriteria.forClass(TechnicalComponentRelease.class);
    criteria.createAlias("technicalComponent", "technicalComponent");

    Disjunction disjunction = Restrictions.disjunction();
    for (String name : names) {
      String[] partsOfReleaseName = GeneralHelper.getPartsOfReleaseName(name);

      //FIXME will eq do the trick here too or do we need like?
      //if like is needed we should use the IteraplanLikeExpression
      //      SimpleExpression nameExpression = Restrictions.like("technicalComponent.name", partsOfReleaseName[0], MatchMode.EXACT).ignoreCase();
      Criterion nameExpression = new IteraplanLikeExpression("technicalComponent.name", partsOfReleaseName[0], true);
      if (partsOfReleaseName[1] != null) {
        //FIXME will eq do the trick here too or do we need like?
        //if like is needed we should use the IteraplanLikeExpression
        //        SimpleExpression versionExpression = Restrictions.like(VERSION, partsOfReleaseName[1], MatchMode.EXACT).ignoreCase();
        Criterion versionExpression = new IteraplanLikeExpression(VERSION, partsOfReleaseName[1], true);
        disjunction.add(Restrictions.and(nameExpression, versionExpression));
      }
      else {
        Criterion versionExpression = Restrictions.or(Restrictions.isNull(VERSION), Restrictions.eq(VERSION, Constants.DB_NU1L));
        disjunction.add(Restrictions.and(nameExpression, versionExpression));
      }
    }
    criteria.add(disjunction);

    return technicalComponentReleaseDAO.findByCriteria(criteria);
  }

  /** {@inheritDoc} */
  public boolean isDuplicateTechnicalComponent(String name, Integer identifier) {
    return technicalComponentReleaseDAO.isDuplicateTechnicalComponent(name, identifier);
  }

  public void setTcr2IeAssociationService(Tcr2IeAssociationService tcr2IeAssociationService) {
    this.tcr2IeAssociationService = tcr2IeAssociationService;
  }

  public void setInformationSystemInterfaceDAO(InformationSystemInterfaceDAO dao) {
    this.informationSystemInterfaceDAO = dao;
  }

  public void setTechnicalComponentDAO(TechnicalComponentDAO dao) {
    this.technicalComponentDAO = dao;
  }

  public void setTechnicalComponentReleaseDAO(TechnicalComponentReleaseDAO dao) {
    this.technicalComponentReleaseDAO = dao;
  }

  /** {@inheritDoc} */
  public List<TechnicalComponentRelease> validBaseComponents(Integer id, List<TechnicalComponentRelease> toExclude, boolean showInactive) {
    List<TechnicalComponentRelease> usedByReleases = new ArrayList<TechnicalComponentRelease>();
    usedByReleases.addAll(toExclude);

    if (id != null) {
      TechnicalComponentRelease release = this.getDao().loadObjectById(id);
      gatherAllUsedByReleases(release, usedByReleases);
      usedByReleases.add(release);
    }

    List<TechnicalComponentRelease> list = technicalComponentReleaseDAO.filter(usedByReleases, showInactive);
    Collections.sort(list, new IdentityStringComparator());

    return list;
  }

  /** {@inheritDoc} */
  public List<TechnicalComponentRelease> validPredecessors(Integer id, List<TechnicalComponentRelease> toExclude, boolean showInactive) {
    List<TechnicalComponentRelease> successors = new ArrayList<TechnicalComponentRelease>();
    successors.addAll(toExclude);

    if (id != null) {
      TechnicalComponentRelease release = this.getDao().loadObjectById(id);
      gatherAllSuccessors(release, successors);
      successors.add(release);
    }

    List<TechnicalComponentRelease> list = technicalComponentReleaseDAO.filter(successors, showInactive);
    Collections.sort(list, new IdentityStringComparator());

    return list;
  }

  /**
   * @param release
   *          The technical component release for which all successors should be collected.
   * @param out
   *          A list containing all successors collected so far. Acts as an out parameter.
   */
  private void gatherAllSuccessors(TechnicalComponentRelease release, List<TechnicalComponentRelease> out) {
    for (TechnicalComponentRelease elem : release.getSuccessors()) {
      out.add(elem);
      gatherAllSuccessors(elem, out);
    }
  }

  /**
   * @param release
   *          The technical component release for which technical components shall be collected that
   *          may be potentially used by this release.
   * @param out
   *          A list containing all releases collected so far. Acts as an out parameter.
   */
  private void gatherAllUsedByReleases(TechnicalComponentRelease release, List<TechnicalComponentRelease> out) {
    for (TechnicalComponentRelease elem : release.getParentComponents()) {
      out.add(elem);
      gatherAllUsedByReleases(elem, out);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected DAOTemplate<TechnicalComponentRelease, Integer> getDao() {
    return this.technicalComponentReleaseDAO;
  }

  /** {@inheritDoc} */
  public List<TechnicalComponentRelease> getTechnicalComponentReleasesBySearch(TechnicalComponent technicalComponent, boolean showInactive) {
    return this.getDao().findBySearchTerm(technicalComponent.getName(), showInactive, new String[] { "technicalComponent.name", VERSION });
  }

  /** {@inheritDoc} */
  public void validateDuplicate(TechnicalComponent technicalComponent, TechnicalComponentRelease technicalComponentRelease) {
    technicalComponent.validate();
    technicalComponentRelease.validate();

    String name = technicalComponentRelease.getTechnicalComponent().getName();
    Integer identifier = technicalComponentRelease.getTechnicalComponent().getId();

    if ((name == null) || name.equals("")) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NAME_CANNOT_BE_EMPTY);
    }

    if (isDuplicateTechnicalComponent(name, identifier)) {
      Object[] params = new Object[] { name };
      throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_ENTRY, params);
    }
    if (doesDuplicateReleaseExist(technicalComponentRelease)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_RELEASE, technicalComponentRelease.getReleaseName());
    }
  }

  /** {@inheritDoc} */
  @Override
  public TechnicalComponentRelease saveOrUpdate(TechnicalComponentRelease entity, boolean cleanup) {

    boolean funcPerm = UserContext.getCurrentUserContext().getPerms()
        .getUserHasBbTypeFunctionalPermission(entity.getTypeOfBuildingBlock().getValue());
    boolean createPerm = UserContext.getCurrentUserContext().getPerms().getUserHasBbTypeCreatePermission(entity.getTypeOfBuildingBlock().getValue());
    boolean updatePerm = UserContext.getCurrentUserContext().getPerms().getUserHasBbTypeUpdatePermission(entity.getTypeOfBuildingBlock().getValue());

    if (funcPerm && (createPerm || updatePerm)) {
      getAttributeValueService().saveOrUpdateAttributeValues(entity);

      getGeneralBuildingBlockService().saveOrUpdate(entity.getTechnicalComponent());

      TechnicalComponentRelease updatedEntity = super.saveOrUpdate(entity, cleanup);
      tcr2IeAssociationService.saveAssociations(entity.getInfrastructureElementAssociations(), cleanup);

      /*
       * needs to be after the saveOrUpdate of the TechnicalComponentRelease to avoid TransientObjectException
       * when calling informationSystemInterfaceDAO.getConnectionsImplementedByTechnicalComponent
       */
      TechnicalComponent entityTechnicalComponent = entity.getTechnicalComponent();
      if ((!entityTechnicalComponent.isAvailableForInterfaces())
          && !informationSystemInterfaceDAO.getConnectionsImplementedByTechnicalComponent(entityTechnicalComponent).isEmpty()) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.CONNECTION_ASSOCIATED_WITH_TC);
      }

      return updatedEntity;
    }
    else {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }
  }
}
