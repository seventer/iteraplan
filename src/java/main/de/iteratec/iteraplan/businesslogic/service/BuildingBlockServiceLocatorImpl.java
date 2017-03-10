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

import java.util.Map;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * A {@link BuildingBlockServiceLocator} implementation for getting the {@link BuildingBlockService} 
 * instances using getters or by {@link TypeOfBuildingBlock} (see {@link #getService(TypeOfBuildingBlock)}).
 * 
 * <p>After the services are set, the {@link #initialize()} method must be called, otherwise the 
 * {@link #getService(TypeOfBuildingBlock)} returns always {@code null}.
 */
public class BuildingBlockServiceLocatorImpl implements BuildingBlockServiceLocator {
  private Map<TypeOfBuildingBlock, BuildingBlockService<BuildingBlock, Integer>> map = Maps.newHashMap();
  private ProjectService                                                         projectService;
  private InfrastructureElementService                                           ieService;
  private TechnicalComponentReleaseService                                       tcrService;
  private TechnicalComponentService                                              tcService;
  private ArchitecturalDomainService                                             adService;
  private InformationSystemInterfaceService                                      isiService;
  private InformationSystemReleaseService                                        isrService;
  private InformationSystemService                                               informationSystemService;
  private InformationSystemDomainService                                         isdService;
  private BusinessObjectService                                                  boService;
  private BusinessUnitService                                                    buService;
  private ProductService                                                         productService;
  private BusinessFunctionService                                                bfService;
  private BusinessProcessService                                                 bpService;
  private BusinessDomainService                                                  bdService;
  private TransportService                                                       transportService;
  private BusinessMappingService                                                 businessMappingService;
  private Tcr2IeAssociationService                                               tcr2IeAssociationService;
  private Isr2BoAssociationService                                               isr2BoAssociationService;

  private AllBuildingBlockService                                                allBBService;

  /**
   * Initializes the associations between the {@link TypeOfBuildingBlock} and the related 
   * {@link BuildingBlockService}. Must be called after the services are set or reset.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void initialize() {
    map.clear();
    map.put(TypeOfBuildingBlock.PROJECT, (BuildingBlockService) projectService);
    map.put(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, (BuildingBlockService) ieService);
    map.put(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, (BuildingBlockService) tcrService);
    map.put(TypeOfBuildingBlock.TECHNICALCOMPONENT, (BuildingBlockService) tcService);
    map.put(TypeOfBuildingBlock.ARCHITECTURALDOMAIN, (BuildingBlockService) adService);
    map.put(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, (BuildingBlockService) isiService);
    map.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, (BuildingBlockService) isrService);
    map.put(TypeOfBuildingBlock.INFORMATIONSYSTEM, (BuildingBlockService) informationSystemService);
    map.put(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, (BuildingBlockService) isdService);
    map.put(TypeOfBuildingBlock.BUSINESSOBJECT, (BuildingBlockService) boService);
    map.put(TypeOfBuildingBlock.BUSINESSUNIT, (BuildingBlockService) buService);
    map.put(TypeOfBuildingBlock.PRODUCT, (BuildingBlockService) productService);
    map.put(TypeOfBuildingBlock.BUSINESSFUNCTION, (BuildingBlockService) bfService);
    map.put(TypeOfBuildingBlock.BUSINESSPROCESS, (BuildingBlockService) bpService);
    map.put(TypeOfBuildingBlock.BUSINESSDOMAIN, (BuildingBlockService) bdService);
    map.put(TypeOfBuildingBlock.TRANSPORT, (BuildingBlockService) transportService);
    map.put(TypeOfBuildingBlock.BUSINESSMAPPING, (BuildingBlockService) businessMappingService);
    map.put(TypeOfBuildingBlock.TCR2IEASSOCIATION, (BuildingBlockService) tcr2IeAssociationService);
    map.put(TypeOfBuildingBlock.ISR2BOASSOCIATION, (BuildingBlockService) isr2BoAssociationService);
  }

  /** {@inheritDoc} */
  public BuildingBlockService<BuildingBlock, Integer> getService(TypeOfBuildingBlock tobb) {
    return map.get(tobb);
  }

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  /** {@inheritDoc} */
  public ProjectService getProjectService() {
    return projectService;
  }

  /** {@inheritDoc} */
  public InfrastructureElementService getIeService() {
    return ieService;
  }

  public void setIeService(InfrastructureElementService ieService) {
    this.ieService = ieService;
  }

  /** {@inheritDoc} */
  public TechnicalComponentReleaseService getTcrService() {
    return tcrService;
  }

  public void setTcrService(TechnicalComponentReleaseService tcrService) {
    this.tcrService = tcrService;
  }

  /** {@inheritDoc} */
  public ArchitecturalDomainService getAdService() {
    return adService;
  }

  public void setAdService(ArchitecturalDomainService adService) {
    this.adService = adService;
  }

  /** {@inheritDoc} */
  public InformationSystemInterfaceService getIsiService() {
    return isiService;
  }

  public void setIsiService(InformationSystemInterfaceService isiService) {
    this.isiService = isiService;
  }

  /** {@inheritDoc} */
  public InformationSystemReleaseService getIsrService() {
    return isrService;
  }

  public void setIsrService(InformationSystemReleaseService isrService) {
    this.isrService = isrService;
  }

  /** {@inheritDoc} */
  public InformationSystemDomainService getIsdService() {
    return isdService;
  }

  public void setIsdService(InformationSystemDomainService isdService) {
    this.isdService = isdService;
  }

  /** {@inheritDoc} */
  public BusinessObjectService getBoService() {
    return boService;
  }

  public void setBoService(BusinessObjectService boService) {
    this.boService = boService;
  }

  /** {@inheritDoc} */
  public BusinessUnitService getBuService() {
    return buService;
  }

  public void setBuService(BusinessUnitService buService) {
    this.buService = buService;
  }

  /** {@inheritDoc} */
  public ProductService getProductService() {
    return productService;
  }

  public void setProductService(ProductService productService) {
    this.productService = productService;
  }

  /** {@inheritDoc} */
  public BusinessFunctionService getBfService() {
    return bfService;
  }

  public void setBfService(BusinessFunctionService bfService) {
    this.bfService = bfService;
  }

  /** {@inheritDoc} */
  public BusinessProcessService getBpService() {
    return bpService;
  }

  public void setBpService(BusinessProcessService bpService) {
    this.bpService = bpService;
  }

  /** {@inheritDoc} */
  public BusinessDomainService getBdService() {
    return bdService;
  }

  public void setBdService(BusinessDomainService bdService) {
    this.bdService = bdService;
  }

  /** {@inheritDoc} */
  public TechnicalComponentService getTcService() {
    return tcService;
  }

  public void setTcService(TechnicalComponentService tcService) {
    this.tcService = tcService;
  }

  /** {@inheritDoc} */
  public InformationSystemService getInformationSystemService() {
    return informationSystemService;
  }

  public void setInformationSystemService(InformationSystemService informationSystemService) {
    this.informationSystemService = informationSystemService;
  }

  /** {@inheritDoc} */
  public TransportService getTransportService() {
    return transportService;
  }

  public void setTransportService(TransportService transportService) {
    this.transportService = transportService;
  }

  /** {@inheritDoc} */
  public BusinessMappingService getBusinessMappingService() {
    return businessMappingService;
  }

  public void setBusinessMappingService(BusinessMappingService businessMappingService) {
    this.businessMappingService = businessMappingService;
  }

  /** {@inheritDoc} */
  public Tcr2IeAssociationService getTcr2IeAssociationService() {
    return tcr2IeAssociationService;
  }

  public void setTcr2IeAssociationService(Tcr2IeAssociationService tcr2IeAssociationService) {
    this.tcr2IeAssociationService = tcr2IeAssociationService;
  }

  /** {@inheritDoc} */
  public Isr2BoAssociationService getIsr2BoAssociationService() {
    return isr2BoAssociationService;
  }

  public void setIsr2BoAssociationService(Isr2BoAssociationService isr2BoAssociationService) {
    this.isr2BoAssociationService = isr2BoAssociationService;
  }

  /** {@inheritDoc} */
  public AllBuildingBlockService getAllBBService() {
    return allBBService;
  }

  public void setAllBBService(AllBuildingBlockService allBBService) {
    this.allBBService = allBBService;
  }
}
