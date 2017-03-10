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

import org.springframework.context.ApplicationContext;

import de.iteratec.iteraplan.common.DefaultSpringApplicationContext;


/**
 * Central Factory for Spring managed services. Whenever a service is needed in a Command class,
 * this factory should be used. The only access to the Spring ApplicationContext is hence
 * encapsulated by this class.
 * 
 * @author Gunnar Giesinger, iteratec GmbH, 2007
 */
public final class SpringServiceFactory {

  private static ApplicationContext context = DefaultSpringApplicationContext.getSpringApplicationContext();

  /** empty private constructor */
  private SpringServiceFactory() {
    // hide constructor
  }

  public static ArchitecturalDomainService getArchitecturalDomainService() {
    return (ArchitecturalDomainService) context.getBean("architecturalDomainService");
  }

  public static SearchService getSearchService() {
    return (SearchService) context.getBean("searchService");
  }

  public static AttributeTypeGroupService getAttributeTypeGroupService() {
    return (AttributeTypeGroupService) context.getBean("attributeTypeGroupService");
  }

  public static AttributeTypeService getAttributeTypeService() {
    return (AttributeTypeService) context.getBean("attributeTypeService");
  }

  public static AttributeValueAssignmentService getAttributeValueAssignmentService() {
    return (AttributeValueAssignmentService) context.getBean("attributeValueAssignmentService");
  }

  public static AttributeValueService getAttributeValueService() {
    return (AttributeValueService) context.getBean("attributeValueService");
  }

  public static BuildingBlockTypeService getBuildingBlockTypeService() {
    return (BuildingBlockTypeService) context.getBean("buildingBlockTypeService");
  }

  public static BusinessDomainService getBusinessDomainService() {
    return (BusinessDomainService) context.getBean("businessDomainService");
  }

  public static BusinessFunctionService getBusinessFunctionService() {
    return (BusinessFunctionService) context.getBean("businessFunctionService");
  }

  public static BusinessMappingService getBusinessMappingService() {
    return (BusinessMappingService) context.getBean("businessMappingService");
  }

  public static BusinessObjectService getBusinessObjectService() {
    return (BusinessObjectService) context.getBean("businessObjectService");
  }

  public static BusinessProcessService getBusinessProcessService() {
    return (BusinessProcessService) context.getBean("businessProcessService");
  }

  public static BusinessUnitService getBusinessUnitService() {
    return (BusinessUnitService) context.getBean("businessUnitService");
  }

  public static ConsistencyCheckService getConsistencyCheckService() {
    return (ConsistencyCheckService) context.getBean("consistencyCheckService");
  }

  public static DataSourceService getDataSourceService() {
    return (DataSourceService) context.getBean("dataSourceService");
  }

  public static ExportService getExportService() {
    return (ExportService) context.getBean("exportService");
  }

  public static GeneralBuildingBlockService getGeneralBuildingBlockService() {
    return (GeneralBuildingBlockService) context.getBean("generalBuildingBlockService");
  }

  public static InstancePermissionService getIinstancePermissionService() {
    return (InstancePermissionService) context.getBean("instancePermissionService");
  }

  public static InformationSystemDomainService getInformationSystemDomainService() {
    return (InformationSystemDomainService) context.getBean("informationSystemDomainService");
  }

  public static InformationSystemInterfaceService getInformationSystemInterfaceService() {
    return (InformationSystemInterfaceService) context.getBean("informationSystemInterfaceService");
  }

  public static InformationSystemReleaseService getInformationSystemReleaseService() {
    return (InformationSystemReleaseService) context.getBean("informationSystemReleaseService");
  }

  public static Tcr2IeAssociationService getTcr2IeAssociationService() {
    return (Tcr2IeAssociationService) context.getBean("tcr2IeAssociationService");
  }

  public static TimeseriesService getTimeseriesService() {
    return (TimeseriesService) context.getBean("timeseriesService");
  }

  public static Isr2BoAssociationService getIsr2BoAssociationService() {
    return (Isr2BoAssociationService) context.getBean("isr2BoAssociationService");
  }

  public static ReleaseSuccessorService getReleaseSuccessorService() {
    return (ReleaseSuccessorService) context.getBean("releaseSuccessorService");
  }

  public static InformationSystemService getInformationSystemService() {
    return (InformationSystemService) context.getBean("informationSystemService");
  }

  public static InfrastructureElementService getInfrastructureElementService() {
    return (InfrastructureElementService) context.getBean("infrastructureElementService");
  }

  public static InitFormHelperService getInitFormHelperService() {
    return (InitFormHelperService) context.getBean("initFormHelperService");
  }

  public static MassUpdateService getMassUpdateService() {
    return (MassUpdateService) context.getBean("massUpdateService");
  }

  public static PermissionQueryService getPermissionQueryService() {
    return (PermissionQueryService) context.getBean("permissionQueryService");
  }

  public static ProductService getProductService() {
    return (ProductService) context.getBean("productService");
  }

  public static ProjectService getProjectService() {
    return (ProjectService) context.getBean("projectService");
  }

  public static QueryService getQueryService() {
    return (QueryService) context.getBean("queryService");
  }

  public static RefreshHelperService getRefreshHelperService() {
    return (RefreshHelperService) context.getBean("refreshHelperService");
  }

  public static RoleService getRoleService() {
    return (RoleService) context.getBean("roleService");
  }

  public static SavedQueryService getSavedQueryService() {
    return (SavedQueryService) context.getBean("savedQueryService");
  }

  public static TechnicalComponentReleaseService getTechnicalComponentReleaseService() {
    return (TechnicalComponentReleaseService) context.getBean("technicalComponentReleaseService");
  }

  public static TechnicalComponentService getTechnicalComponentService() {
    return (TechnicalComponentService) context.getBean("technicalComponentService");
  }

  public static TransportService getTransportService() {
    return (TransportService) context.getBean("transportService");
  }

  public static UserEntityService getUserEntityService() {
    return (UserEntityService) context.getBean("userEntityService");
  }

  public static UserGroupService getUserGroupService() {
    return (UserGroupService) context.getBean("userGroupService");
  }

  public static UserService getUserService() {
    return (UserService) context.getBean("userService");
  }

  public static FastExportService getFastExportService() {
    return (FastExportService) context.getBean("fastExportService");
  }

  public static ElasticeamService getElasticeamService() {
    return (ElasticeamService) context.getBean("elasticeamService");
  }

  public static ElasticMiService getElasticMiService() {
    return (ElasticMiService) context.getBean("elasticMiService");
  }

  public static HistoryService getHistoryService() {
    return (HistoryService) context.getBean("historyService");
  }

  public static BuildingBlockServiceLocator getBuildingBlockServiceLocator() {
    return (BuildingBlockServiceLocator) context.getBean("bbServiceLocator");
  }
}
