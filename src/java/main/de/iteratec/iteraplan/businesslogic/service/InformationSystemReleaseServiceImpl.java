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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import com.google.common.collect.Lists;

import de.iteratec.hibernate.criterion.IteraplanLikeExpression;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Release;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;
import de.iteratec.iteraplan.persistence.dao.InformationSystemDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemInterfaceDAO;
import de.iteratec.iteraplan.persistence.dao.InformationSystemReleaseDAO;


/**
 * Implementation of the service interface {@link InformationSystemReleaseService}.
 */
public class InformationSystemReleaseServiceImpl extends AbstractBuildingBlockService<InformationSystemRelease> implements
    InformationSystemReleaseService {

  private InformationSystemService      informationSystemService;
  private BusinessMappingService        businessMappingService;
  private Isr2BoAssociationService      isr2BoAssociationService;
  private InformationSystemDAO          informationSystemDAO;
  private InformationSystemInterfaceDAO informationSystemInterfaceDAO;
  private InformationSystemReleaseDAO   informationSystemReleaseDAO;

  @Override
  protected void checkDelete(InformationSystemRelease buildingBlock) {
    super.checkDelete(buildingBlock);
    checkDeletePermissionsCurrentPerms(buildingBlock);
  }

  @Override
  protected InformationSystemRelease onBeforeDelete(InformationSystemRelease isr) {
    super.onBeforeDelete(isr);

    Set<InformationSystemRelease> children = new HashSet<InformationSystemRelease>();
    for (InformationSystemRelease isRelease : isr.getChildren()) {
      children.add(isRelease);
    }

    children.add(isr);

    //First delete all self-reference isi and children's self-reference isi using isRelease 
    //in order to prevent ConstraintViolationException
    for (InformationSystemRelease tmp : children) {
      List<InformationSystemInterface> selfRefIsi = informationSystemInterfaceDAO.getSelfReferencedInterface(tmp.getId());

      if (selfRefIsi != null) {
        for (InformationSystemInterface isi : selfRefIsi) {
          informationSystemInterfaceDAO.delete(isi);
        }
      }
    }

    deleteRelatedTimeseries(isr);
    return isr;
  }

  @Override
  protected void onAfterDelete(InformationSystemRelease buildingBlock) {
    this.deleteOrphanedInformationSystems();
    getAttributeValueService().removeOrphanedAttributeValuesAndAssignments();
  }

  /**
   * check if there are any InformationSystems that have no Information System Releases
   * any more (as those could be deleted by cascade) and delete the orphan InformationSystems:
   */
  private void deleteOrphanedInformationSystems() {
    for (InformationSystem orphanIS : informationSystemDAO.getOrphanInformationSystems()) {
      informationSystemDAO.delete(orphanIS);
    }
  }

  /** {@inheritDoc} */
  public boolean doesDuplicateReleaseExist(Release isr) {
    return informationSystemReleaseDAO.doesObjectWithDifferentIdExist(isr.getId(), isr.getReleaseName());
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> getAvailableParentReleases(Integer id, boolean showInactive) {
    List<InformationSystemRelease> list = informationSystemReleaseDAO.filter(null, showInactive);

    if (id != null) {
      InformationSystemRelease release = informationSystemReleaseDAO.loadObjectById(id);
      list.remove(release);
      HierarchyHelper.removeCycleElementsFromElementOfList(list, release);
    }

    return list;
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> getInformationSystemsFiltered(List<InformationSystemRelease> elementsToExclude, boolean showInactive) {
    List<InformationSystemRelease> list = informationSystemReleaseDAO.filter(elementsToExclude, showInactive);
    Collections.sort(list, new HierarchicalEntityCachingComparator<InformationSystemRelease>());

    return list;
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> loadElementList(boolean showInactive) {
    return informationSystemReleaseDAO.filter(null, showInactive);
  }

  /** {@inheritDoc} */
  @Override
  public List<InformationSystemRelease> findByNames(Set<String> names) {
    if (names.isEmpty()) {
      return Collections.emptyList();
    }

    DetachedCriteria criteria = DetachedCriteria.forClass(InformationSystemRelease.class);
    criteria.createAlias("informationSystem", "informationSystem");

    Disjunction disjunction = Restrictions.disjunction();
    for (String name : names) {
      String[] partsOfReleaseName = GeneralHelper.getPartsOfReleaseName(name);

      //FIXME will eq do the trick here too or do we need like?
      //if like is needed we should use the IteraplanLikeExpression
      //      SimpleExpression nameExpression = Restrictions.like("informationSystem.name", partsOfReleaseName[0], MatchMode.EXACT).ignoreCase();
      Criterion nameExpression = new IteraplanLikeExpression("informationSystem.name", partsOfReleaseName[0], true);
      String version = "version";
      if (partsOfReleaseName[1] != null) {
        //FIXME will eq do the trick here too or do we need like?
        //if like is needed we should use the IteraplanLikeExpression
        //        SimpleExpression versionExpression = Restrictions.like(version, partsOfReleaseName[1], MatchMode.EXACT).ignoreCase();
        Criterion versionExpression = new IteraplanLikeExpression(version, partsOfReleaseName[1], true);
        disjunction.add(Restrictions.and(nameExpression, versionExpression));
      }
      else {
        Criterion versionExpression = Restrictions.or(Restrictions.isNull(version), Restrictions.eq(version, Constants.DB_NU1L));
        disjunction.add(Restrictions.and(nameExpression, versionExpression));
      }
    }
    criteria.add(disjunction);

    return informationSystemReleaseDAO.findByCriteria(criteria);
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> getOutermostInformationSystemReleases() {
    return informationSystemReleaseDAO.getOutermostInformationSystemReleases();
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> getValidPredecessors(Integer id, List<InformationSystemRelease> elementsToExclude, boolean showInactive) {
    List<InformationSystemRelease> successors = Lists.newArrayList(elementsToExclude);

    if (id != null) {
      InformationSystemRelease release = informationSystemReleaseDAO.loadObjectById(id);
      gatherAllSuccessors(release, successors);
      successors.add(release);
    }

    return informationSystemReleaseDAO.filter(successors, showInactive);
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> getValidSuccessors(Integer id, List<InformationSystemRelease> elementsToExclude, boolean showInactive) {
    List<InformationSystemRelease> predecessors = Lists.newArrayList(elementsToExclude);

    if (id != null) {
      InformationSystemRelease release = informationSystemReleaseDAO.loadObjectById(id);
      gatherAllPredecessors(release, predecessors);
      predecessors.add(release);
    }

    return informationSystemReleaseDAO.filter(predecessors, showInactive);
  }

  /** {@inheritDoc} */
  public boolean isDuplicateInformationSystem(String name, Integer identifier) {
    return informationSystemReleaseDAO.isDuplicateInformationSystem(name, identifier);
  }

  public void setInformationSystemService(InformationSystemService informationSystemService) {
    this.informationSystemService = informationSystemService;
  }

  public void setBusinessMappingService(BusinessMappingService businessMappingService) {
    this.businessMappingService = businessMappingService;
  }

  public void setIsr2BoAssociationService(Isr2BoAssociationService isr2BoAssociationService) {
    this.isr2BoAssociationService = isr2BoAssociationService;
  }

  public void setInformationSystemDAO(InformationSystemDAO dao) {
    this.informationSystemDAO = dao;
  }

  public void setInformationSystemInterfaceDAO(InformationSystemInterfaceDAO dao) {
    this.informationSystemInterfaceDAO = dao;
  }

  public void setInformationSystemReleaseDAO(InformationSystemReleaseDAO dao) {
    this.informationSystemReleaseDAO = dao;
  }

  /**
   * Checks, if the given information system release contains duplicate business mappings.
   * 
   * @param releaseToCheck
   *          The information system release to check.
   * @throws IteraplanBusinessException
   *           If the information system release contains duplicate business mappings.
   */
  private void checkForDuplicateBusinessMapping(InformationSystemRelease releaseToCheck) {
    Set<String> set = new HashSet<String>();
    StringBuilder sb = new StringBuilder(8);

    for (BusinessMapping mapping : releaseToCheck.getBusinessMappings()) {

      sb.append("[");
      sb.append(mapping.getBusinessProcess().getName());
      sb.append(" , ");
      sb.append(mapping.getBusinessUnit().getName());
      sb.append(" , ");
      sb.append(mapping.getProduct().getName());
      sb.append("]");

      if (!set.add(sb.toString())) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_BUSINESS_MAPPINGS_ONEDIT, sb.toString());
      }

      sb.setLength(0);
    }
  }

  /**
   * @param release
   *          The information system release for which the successors should be collected.
   * @param out
   *          A list containing all successors collected so far. Acts as an out parameter.
   */
  private void gatherAllSuccessors(InformationSystemRelease release, List<InformationSystemRelease> out) {
    for (InformationSystemRelease successor : release.getSuccessors()) {
      out.add(successor);
      gatherAllSuccessors(successor, out);
    }
  }

  /**
   * @param release
   *          The information system release for which the predecessors should be collected.
   * @param out
   *          A list containing all predecessors collected so far. Acts as an out parameter.
   */
  private void gatherAllPredecessors(InformationSystemRelease release, List<InformationSystemRelease> out) {
    for (InformationSystemRelease predecessors : release.getSuccessors()) {
      out.add(predecessors);
      gatherAllSuccessors(predecessors, out);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected DAOTemplate<InformationSystemRelease, Integer> getDao() {
    return informationSystemReleaseDAO;
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> getAvailableChildren(Integer id, List<InformationSystemRelease> elementsToExclude, boolean showInactive) {
    InformationSystemRelease source = this.getDao().loadObjectById(id);
    Set<Integer> set = new HashSet<Integer>();

    // The ID is null if and only if a new element is created.
    if (id != null) {

      // Add the ID of the current element.
      set.add(id);

      // Add the IDs of elements to exclude.
      if ((elementsToExclude != null) && (elementsToExclude.size() > 0)) {
        set.addAll(GeneralHelper.createIdSetFromIdEntities(elementsToExclude));
      }

      // Add the IDs of the element's parents (top-level element included).
      InformationSystemRelease parent = source.getParentElement();
      while (parent != null) {
        set.add(parent.getId());
        parent = parent.getParentElement();
      }
    }
    else {
      // ISRs don't have a virtual element, so there's no need to exclude it.

      // Add the IDs of elements to exclude.
      if ((elementsToExclude != null) && (elementsToExclude.size() > 0)) {
        set.addAll(GeneralHelper.createIdSetFromIdEntities(elementsToExclude));
      }
    }

    return informationSystemReleaseDAO.filterWithIds(set, showInactive);
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> getInformationSystemReleasesBySearch(InformationSystem informationSystem, boolean showInactive) {
    return informationSystemReleaseDAO.findBySearchTerm(informationSystem.getName(), showInactive,
        new String[] { "informationSystem.name", "version" });
  }

  /** {@inheritDoc} */
  public void validateDuplicate(InformationSystem informationSystem, InformationSystemRelease informationSystemRelease) {
    informationSystem.validate();
    informationSystemRelease.validate();

    String name = informationSystemRelease.getInformationSystem().getName();
    Integer isId = informationSystemRelease.getInformationSystem().getId();

    if ((name == null) || name.equals("")) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NAME_CANNOT_BE_EMPTY);
    }

    if (isDuplicateInformationSystem(name, isId)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_ENTRY, name);
    }
    if (doesDuplicateReleaseExist(informationSystemRelease)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_RELEASE, informationSystemRelease.getReleaseName());
    }
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> validBaseComponents(Integer id, List<InformationSystemRelease> toExclude, boolean showInactive) {
    List<InformationSystemRelease> usedByReleases = new ArrayList<InformationSystemRelease>();
    usedByReleases.addAll(toExclude);

    if (id != null) {
      InformationSystemRelease release = informationSystemReleaseDAO.loadObjectById(id);
      gatherAllUsedByReleases(release, usedByReleases);
      usedByReleases.add(release);
    }

    return informationSystemReleaseDAO.filter(usedByReleases, showInactive);
  }

  /** {@inheritDoc} */
  public List<InformationSystemRelease> validParentComponents(Integer id, List<InformationSystemRelease> toExclude, boolean showInactive) {
    List<InformationSystemRelease> usingReleases = new ArrayList<InformationSystemRelease>();
    usingReleases.addAll(toExclude);

    if (id != null) {
      InformationSystemRelease release = informationSystemReleaseDAO.loadObjectById(id);
      gatherAllUsingReleases(release, usingReleases);
      usingReleases.add(release);
    }

    return informationSystemReleaseDAO.filter(usingReleases, showInactive);
  }

  /**
   * @param release
   *          The information system release for which information system shall be collected that
   *          may be potentially used by this release.
   * @param out
   *          A list containing all releases collected so far. Acts as an out parameter.
   */
  private void gatherAllUsedByReleases(InformationSystemRelease release, List<InformationSystemRelease> out) {
    for (InformationSystemRelease elem : release.getParentComponents()) {
      out.add(elem);
      gatherAllUsedByReleases(elem, out);
    }
  }

  /**
   * @param release
   *          The information system release for which information system shall be collected that
   *          may potentially use this release.
   * @param out
   *          A list containing all releases collected so far. Acts as an out parameter.
   */
  private void gatherAllUsingReleases(InformationSystemRelease release, List<InformationSystemRelease> out) {
    for (InformationSystemRelease elem : release.getBaseComponents()) {
      out.add(elem);
      gatherAllUsingReleases(elem, out);
    }
  }

  /** {@inheritDoc} */
  @Override
  public InformationSystemRelease saveOrUpdate(InformationSystemRelease entity, boolean cleanup) {

    boolean funcPerm = UserContext.getCurrentUserContext().getPerms()
        .getUserHasBbTypeFunctionalPermission(entity.getTypeOfBuildingBlock().getValue());
    boolean createPerm = UserContext.getCurrentUserContext().getPerms().getUserHasBbTypeCreatePermission(entity.getTypeOfBuildingBlock().getValue());
    boolean updatePerm = UserContext.getCurrentUserContext().getPerms().getUserHasBbTypeUpdatePermission(entity.getTypeOfBuildingBlock().getValue());

    if (funcPerm && (createPerm || updatePerm)) {
      checkForDuplicateBusinessMapping(entity);

      saveBusinessMappings(entity.getBusinessMappings());
      getAttributeValueService().saveOrUpdateAttributeValues(entity);

      entity.breakSeal();

      informationSystemService.saveOrUpdate(entity.getInformationSystem(), cleanup);

      InformationSystemRelease informationSystemRelease = super.saveOrUpdate(entity, cleanup);

      // this has to be after saving entity (the entity needs an id), because not-null constraint in Isr2BoAssociation
      isr2BoAssociationService.saveAssociations(entity.getBusinessObjectAssociations(), cleanup);

      if (cleanup) {
        businessMappingService.deleteOrphanedBusinessMappings();
      }

      return informationSystemRelease;
    }
    else {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }

  }

  /**
   * Saves the specified list of business mappings. The attributes will be saved also.
   * 
   * @param businessMappings the business mappings to save
   */
  private void saveBusinessMappings(Collection<BusinessMapping> businessMappings) {
    for (BusinessMapping businessMapping : businessMappings) {
      getAttributeValueService().saveOrUpdateAttributeValues(businessMapping);
      businessMappingService.saveOrUpdate(businessMapping);
    }
  }
}