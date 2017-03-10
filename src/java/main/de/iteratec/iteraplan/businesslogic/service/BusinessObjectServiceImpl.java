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

import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityComparator;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;
import de.iteratec.iteraplan.model.sorting.OrderedHierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.persistence.dao.BusinessObjectDAO;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;


/**
 * Implementataion of the service interface {@link BusinessObjectService}.
 */
public class BusinessObjectServiceImpl extends AbstractHierarchicalBuildingBlockService<BusinessObject> implements BusinessObjectService {

  private Isr2BoAssociationService isr2BoAssociationService;
  private BusinessObjectDAO        businessObjectDAO;

  @Override
  protected void checkDelete(BusinessObject buildingBlock) {
    super.checkDelete(buildingBlock);
    checkDeletePermissionsCurrentPerms(buildingBlock);
  }

  @Override
  protected void onAfterDelete(BusinessObject buildingBlock) {
    getAttributeValueService().removeOrphanedAttributeValuesAndAssignments();
  }

  /** {@inheritDoc} */
  public List<BusinessObject> getAvailableGeneralisations(Integer id) {
    List<BusinessObject> list = loadElementList();

    if (id != null) {
      BusinessObject item = loadObjectById(id);
      list.remove(item);
      HierarchyHelper.removeCycleElementsFromGeneralisationList(list, item);
    }

    Collections.sort(list, new HierarchicalEntityComparator<BusinessObject>());

    return list;
  }

  /** {@inheritDoc} */
  public List<BusinessObject> getBusinessObjectsWithoutGeneralisation() {
    return businessObjectDAO.getBusinessObjectsWithoutGeneralisation();
  }

  /** {@inheritDoc} */
  @Override
  protected DAOTemplate<BusinessObject, Integer> getDao() {
    return businessObjectDAO;
  }

  public void setIsr2BoAssociationService(Isr2BoAssociationService isr2BoAssociationService) {
    this.isr2BoAssociationService = isr2BoAssociationService;
  }

  public void setBusinessObjectDAO(BusinessObjectDAO businessObjectDAO) {
    this.businessObjectDAO = businessObjectDAO;
  }

  /** {@inheritDoc} */
  public List<BusinessObject> getAvailableSpecialisations(Integer id, List<BusinessObject> elementsToExclude, boolean includeRoot) {
    // Filter elements.
    Set<Integer> set = GeneralHelper.createIdSetFromIdEntities(elementsToExclude);
    List<BusinessObject> list = super.loadFilteredElementList(set);

    // If the root is not be included, remove it from the list.
    if (!includeRoot) {
      BusinessObject root = getFirstElement();
      if (root == null) {
        throw new IllegalStateException("Undefined state: The virtual element does not exist.");
      }

      if (list.contains(root)) {
        list.remove(root);
      }
      else {
        throw new IllegalStateException("The virtual element must not be connected to any building block.");
      }
    }

    if (id != null) {
      BusinessObject bo = loadObjectById(id);
      list.remove(bo);
      list = removeHierarchyViolation(list, bo);
    }

    Collections.sort(list, new OrderedHierarchicalEntityCachingComparator<BusinessObject>());

    return list;
  }

  /**
   * Remove elements from the list of available specialisations if there is hierarchy violation (e.g
   * b specialises c, a specialises b, because of transitivity a can't be specialisation of c)
   */
  private List<BusinessObject> removeHierarchyViolation(List<BusinessObject> list, BusinessObject bo) {
    List<BusinessObject> availableElements = new ArrayList<BusinessObject>(list);
    List<BusinessObject> specialisationsOfElement = new ArrayList<BusinessObject>(bo.getSpecialisations());

    for (BusinessObject specialisation : specialisationsOfElement) {
      for (BusinessObject element : availableElements) {
        if (specialisation.equals(element.getGeneralisation())) {
          list.remove(element);
        }
        if (element.getSpecialisations() != null) {
          removeHierarchyViolation(list, specialisation);
        }
      }
    }

    return list;
  }

  /** {@inheritDoc} */
  @Override
  public BusinessObject saveOrUpdate(BusinessObject entity, boolean cleanup) {

    getAttributeValueService().saveOrUpdateAttributeValues(entity);
    BusinessObject businessObject = super.saveOrUpdate(entity, cleanup);
    isr2BoAssociationService.saveAssociations(entity.getInformationSystemReleaseAssociations(), cleanup);

    return businessObject;
  }
}