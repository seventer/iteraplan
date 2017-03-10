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

import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.persistence.dao.BusinessProcessDAO;
import de.iteratec.iteraplan.persistence.dao.DAOTemplate;


/**
 * Implementation of the service interface {@link BusinessProcessService}.
 */
public class BusinessProcessServiceImpl extends AbstractHierarchicalBuildingBlockService<BusinessProcess> implements BusinessProcessService {

  private BusinessProcessDAO     businessProcessDAO;
  private BusinessMappingService businessMappingService;

  @Override
  protected void checkDelete(BusinessProcess buildingBlock) {
    super.checkDelete(buildingBlock);
    checkDeletePermissionsCurrentPerms(buildingBlock);
  }

  @Override
  protected void onAfterDelete(BusinessProcess buildingBlock) {
    getAttributeValueService().removeOrphanedAttributeValuesAndAssignments();
  }

  /** {@inheritDoc} */
  @Override
  protected DAOTemplate<BusinessProcess, Integer> getDao() {
    return businessProcessDAO;
  }

  public void setBusinessProcessDAO(BusinessProcessDAO businessProcessDAO) {
    this.businessProcessDAO = businessProcessDAO;
  }

  public void setBusinessMappingService(BusinessMappingService businessMappingService) {
    this.businessMappingService = businessMappingService;
  }

  /**
   * Checks, if the given business process contains duplicate business mappings.
   * 
   * @param businessProcess The business process to check.
   * @throws IteraplanBusinessException If the business process contains duplicate business mappings.
   */
  private void checkForDuplicateBusinessMapping(BusinessProcess businessProcess) {
    final Set<String> set = Sets.newHashSet();
    StringBuilder sb = new StringBuilder(20);

    for (BusinessMapping mapping : businessProcess.getBusinessMappings()) {

      sb.append("[");
      sb.append(mapping.getInformationSystemRelease().getName());
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

  /** {@inheritDoc} */
  @Override
  public BusinessProcess saveOrUpdate(BusinessProcess businessProcess, boolean cleanup) {
    checkForDuplicateBusinessMapping(businessProcess);

    for (BusinessMapping businessMapping : businessProcess.getBusinessMappings()) {
      getAttributeValueService().saveOrUpdateAttributeValues(businessMapping);
      businessMappingService.saveOrUpdate(businessMapping);
    }
    getAttributeValueService().saveOrUpdateAttributeValues(businessProcess);
    BusinessProcess savedBusinessProcess = super.saveOrUpdate(businessProcess, cleanup);

    if (cleanup) {
      businessMappingService.deleteOrphanedBusinessMappings();
    }

    return savedBusinessProcess;
  }
}
