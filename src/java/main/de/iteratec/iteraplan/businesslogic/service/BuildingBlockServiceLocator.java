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

import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * A locator for getting the {@link BuildingBlockService} instances using getters or by {@link TypeOfBuildingBlock}
 * (see {@link #getService(TypeOfBuildingBlock)}).
 */
public interface BuildingBlockServiceLocator {

  /**
   * Returns the {@link BuildingBlockService} for the specified {@link TypeOfBuildingBlock}.
   * If the service can not be found, {@code null} will be returned.
   * 
   * @param tobb the type of building block
   * @return the {@link BuildingBlockService} instance or {@code null}, if the service can not be found
   */
  BuildingBlockService<BuildingBlock, Integer> getService(TypeOfBuildingBlock tobb);

  /**
   * Returns the {@link ProjectService} instance.
   * 
   * @return the {@link ProjectService} instance.
   */
  ProjectService getProjectService();

  /**
   * Returns the {@link InfrastructureElementService} instance.
   * 
   * @return the {@link InfrastructureElementService} instance.
   */
  InfrastructureElementService getIeService();

  /**
   * Returns the {@link TechnicalComponentReleaseService} instance.
   * 
   * @return the {@link TechnicalComponentReleaseService} instance.
   */
  TechnicalComponentReleaseService getTcrService();

  /**
   * Returns the {@link ArchitecturalDomainService} instance.
   * 
   * @return the {@link ArchitecturalDomainService} instance.
   */
  ArchitecturalDomainService getAdService();

  /**
   * Returns the {@link InformationSystemInterfaceService} instance.
   * 
   * @return the {@link InformationSystemInterfaceService} instance.
   */
  InformationSystemInterfaceService getIsiService();

  /**
   * Returns the {@link InformationSystemReleaseService} instance.
   * 
   * @return the {@link InformationSystemReleaseService} instance.
   */
  InformationSystemReleaseService getIsrService();

  /**
   * Returns the {@link InformationSystemDomainService} instance.
   * 
   * @return the {@link InformationSystemDomainService} instance.
   */
  InformationSystemDomainService getIsdService();

  /**
   * Returns the {@link BusinessObjectService} instance.
   * 
   * @return the {@link BusinessObjectService} instance.
   */
  BusinessObjectService getBoService();

  /**
   * Returns the {@link BusinessUnitService} instance.
   * 
   * @return the {@link BusinessUnitService} instance.
   */
  BusinessUnitService getBuService();

  /**
   * Returns the {@link ProductService} instance.
   * 
   * @return the {@link ProductService} instance.
   */
  ProductService getProductService();

  /**
   * Returns the {@link BusinessFunctionService} instance.
   * 
   * @return the {@link BusinessFunctionService} instance.
   */
  BusinessFunctionService getBfService();

  /**
   * Returns the {@link BusinessProcessService} instance.
   * 
   * @return the {@link BusinessProcessService} instance.
   */
  BusinessProcessService getBpService();

  /**
   * Returns the {@link BusinessDomainService} instance.
   * 
   * @return the {@link BusinessDomainService} instance.
   */
  BusinessDomainService getBdService();

  /**
   * Returns the {@link TechnicalComponentService} instance.
   * 
   * @return the {@link TechnicalComponentService} instance.
   */
  TechnicalComponentService getTcService();

  /**
   * Returns the {@link InformationSystemService} instance.
   * 
   * @return the {@link InformationSystemService} instance.
   */
  InformationSystemService getInformationSystemService();

  /**
   * Returns the {@link TransportService} instance.
   * 
   * @return the {@link TransportService} instance.
   */
  TransportService getTransportService();

  /**
   * Returns the {@link BusinessMappingService} instance.
   * 
   * @return the {@link BusinessMappingService} instance.
   */
  BusinessMappingService getBusinessMappingService();

  /**
   * Returns the {@link Tcr2IeAssociationService} instance.
   * 
   * @return the {@link Tcr2IeAssociationService} instance.
   */
  Tcr2IeAssociationService getTcr2IeAssociationService();

  /**
   * Returns the {@link Isr2BoAssociationService} instance.
   * 
   * @return the {@link Isr2BoAssociationService} instance.
   */
  Isr2BoAssociationService getIsr2BoAssociationService();

  /**
   * Returns the {@link AllBuildingBlockService} instance.
   * 
   * @return the {@link AllBuildingBlockService} instance.
   */
  AllBuildingBlockService getAllBBService();
}