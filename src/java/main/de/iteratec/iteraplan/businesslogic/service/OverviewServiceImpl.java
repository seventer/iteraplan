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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.OverviewElementLists;
import de.iteratec.iteraplan.model.OverviewElementLists.ElementList;
import de.iteratec.iteraplan.model.sorting.OrderedHierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * A default {@link DashboardService} implementation for calculating various properties and 
 * attributes, required to show dashboard graphics.
 */
public class OverviewServiceImpl implements OverviewService {

  private static final int            NUMBER_OF_TOP_NON_HIERARCHICAL_ELEMENTS = 5;

  private BuildingBlockServiceLocator buildingBlockServiceLocator;
  private DashboardService            dashboardService;

  /** {@inheritDoc} */
  public OverviewElementLists getElementLists() {
    OverviewElementLists overview = new OverviewElementLists();

    if (UserContext.getCurrentPerms().getUserHasFuncPermBP()) {
      BusinessProcessService businessProcessService = buildingBlockServiceLocator.getBpService();
      overview.addElementList(getRelevantBbsHierarchical(businessProcessService, TypeOfBuildingBlock.BUSINESSPROCESS, "businessprocess"));
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermPROD()) {
      ProductService productService = buildingBlockServiceLocator.getProductService();
      overview.addElementList(getRelevantBbsHierarchical(productService, TypeOfBuildingBlock.PRODUCT, "product"));
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermBU()) {
      BusinessUnitService businessUnitService = buildingBlockServiceLocator.getBuService();
      overview.addElementList(getRelevantBbsHierarchical(businessUnitService, TypeOfBuildingBlock.BUSINESSUNIT, "businessunit"));
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermBF()) {
      BusinessFunctionService businessFunctionService = buildingBlockServiceLocator.getBfService();
      overview.addElementList(getRelevantBbsHierarchical(businessFunctionService, TypeOfBuildingBlock.BUSINESSFUNCTION, "businessfunction"));
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermBO()) {
      BusinessObjectService businessObjectService = buildingBlockServiceLocator.getBoService();
      overview.addElementList(getRelevantBbsHierarchical(businessObjectService, TypeOfBuildingBlock.BUSINESSOBJECT, "businessobject"));
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermPROJ()) {
      ProjectService projectService = buildingBlockServiceLocator.getProjectService();
      overview.addElementList(getRelevantBbsHierarchical(projectService, TypeOfBuildingBlock.PROJECT, "project"));
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermIS()) {
      overview.addElementList(getRelevantISR());
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermBD()) {
      BusinessDomainService businessDomainService = buildingBlockServiceLocator.getBdService();
      overview.addElementList(getRelevantBbsHierarchical(businessDomainService, TypeOfBuildingBlock.BUSINESSDOMAIN, "businessdomain"));
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermISD()) {
      InformationSystemDomainService informationSystemDomainService = buildingBlockServiceLocator.getIsdService();
      overview.addElementList(getRelevantBbsHierarchical(informationSystemDomainService, TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN,
          "informationsystemdomain"));
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermAD()) {
      ArchitecturalDomainService architecturalDomainService = buildingBlockServiceLocator.getAdService();
      overview.addElementList(getRelevantBbsHierarchical(architecturalDomainService, TypeOfBuildingBlock.ARCHITECTURALDOMAIN, "architecturaldomain"));
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermTC()) {
      overview.addElementList(getRelevantTCR());
    }

    if (UserContext.getCurrentPerms().getUserHasFuncPermIE()) {
      InfrastructureElementService infrastructureElementService = buildingBlockServiceLocator.getIeService();
      overview.addElementList(getRelevantBbsHierarchical(infrastructureElementService, TypeOfBuildingBlock.INFRASTRUCTUREELEMENT,
          "infrastructureelement"));
    }

    return overview;
  }

  private <T extends AbstractHierarchicalEntity<T>> ElementList<T> getRelevantBbsHierarchical(HierarchicalBuildingBlockService<T, Integer> service,
                                                                                              TypeOfBuildingBlock tobb, String htmlId) {

    List<T> elements = Lists.newArrayList(service.getFirstElement().getChildren());
    Collections.sort(elements, new OrderedHierarchicalEntityCachingComparator<T>());
    return new ElementList<T>(htmlId, tobb.getPluralValue(), service.loadElementList().size(), elements);
  }

  private ElementList<InformationSystemRelease> getRelevantISR() {
    List<InformationSystemRelease> list = Lists.newArrayList(buildingBlockServiceLocator.getIsrService().loadElementList());
    Map<InformationSystemRelease, Integer> sortedMap = dashboardService.getTopUsedIsr(list);
    List<InformationSystemRelease> result = Lists.newArrayList();
    int count = 0;
    for (InformationSystemRelease isr : sortedMap.keySet()) {
      if (count++ >= NUMBER_OF_TOP_NON_HIERARCHICAL_ELEMENTS) {
        break;
      }
      result.add(isr);
    }
    return new ElementList<InformationSystemRelease>("informationsystem", TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getPluralValue(),
        buildingBlockServiceLocator.getIsrService().loadElementList().size(), result);
  }

  private ElementList<TechnicalComponentRelease> getRelevantTCR() {
    List<TechnicalComponentRelease> list = Lists.newArrayList(buildingBlockServiceLocator.getTcrService().loadElementList());
    Map<TechnicalComponentRelease, Integer> sortedMap = dashboardService.getTopUsedTcr(list);
    List<TechnicalComponentRelease> result = Lists.newArrayList();
    int count = 0;
    for (TechnicalComponentRelease tcr : sortedMap.keySet()) {
      if (count++ >= NUMBER_OF_TOP_NON_HIERARCHICAL_ELEMENTS) {
        break;
      }
      result.add(tcr);
    }
    return new ElementList<TechnicalComponentRelease>("technicalcomponent", TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE.getPluralValue(),
        buildingBlockServiceLocator.getTcrService().loadElementList().size(), result);
  }

  public void setDashboardService(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  public void setBuildingBlockServiceLocator(BuildingBlockServiceLocator buildingBlockServiceLocator) {
    this.buildingBlockServiceLocator = buildingBlockServiceLocator;
  }

}
