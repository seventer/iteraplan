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
package de.iteratec.iteraplan.model;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockTypeService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;


@SuppressWarnings("deprecation")
public final class BuildingBlockFactory {

  private BuildingBlockFactory() {
    // private Constructor, only static methods
  }

  private static BuildingBlockTypeService    bbTypeService    = null;
  private static BuildingBlockServiceLocator bbServiceLocator = null;

  /**
   * Re-sets the BuildingBlockTypeService instance used. <b>Only intended for tests!</b>
   * In normal operation, the service is properly initialised and should not be changed. 
   */
  public static void setBbTypeService(BuildingBlockTypeService bbTypeService) {
    BuildingBlockFactory.bbTypeService = bbTypeService;
  }

  public static BuildingBlockTypeService getBbTypeService() {
    synchronized (BuildingBlockFactory.class) {
      if (bbTypeService == null) {
        bbTypeService = SpringServiceFactory.getBuildingBlockTypeService();
      }
    }
    return bbTypeService;
  }

  /**
   * Re-sets the BuildingBlockServiceLocator instance used. <b>Only intended for tests!</b>
   * In normal operation, the service is properly initialised and should not be changed. 
   */
  public static void setBbServiceLocator(BuildingBlockServiceLocator bbServiceLocator) {
    BuildingBlockFactory.bbServiceLocator = bbServiceLocator;
  }

  public static BuildingBlockServiceLocator getBbServiceLocator() {
    synchronized (BuildingBlockFactory.class) {
      if (bbServiceLocator == null) {
        bbServiceLocator = SpringServiceFactory.getBuildingBlockServiceLocator();
      }
    }
    return bbServiceLocator;
  }

  public static ArchitecturalDomain createArchitecturalDomain() {
    ArchitecturalDomain bb = new ArchitecturalDomain();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.ARCHITECTURALDOMAIN));
    bb.addParent(getBbServiceLocator().getAdService().getFirstElement());
    return bb;
  }

  public static BusinessDomain createBusinessDomain() {
    BusinessDomain bb = new BusinessDomain();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSDOMAIN));
    bb.addParent(getBbServiceLocator().getBdService().getFirstElement());
    return bb;
  }

  public static BusinessFunction createBusinessFunction() {
    BusinessFunction bb = new BusinessFunction();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSFUNCTION));
    bb.addParent(getBbServiceLocator().getBfService().getFirstElement());
    return bb;
  }

  public static BusinessMapping createBusinessMapping() {
    BusinessMapping bb = new BusinessMapping();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSMAPPING));
    return bb;
  }

  public static BusinessObject createBusinessObject() {
    BusinessObject bb = new BusinessObject();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSOBJECT));
    bb.addParent(getBbServiceLocator().getBoService().getFirstElement());
    return bb;
  }

  public static BusinessProcess createBusinessProcess() {
    BusinessProcess bb = new BusinessProcess();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS));
    bb.addParent(getBbServiceLocator().getBpService().getFirstElement());
    return bb;
  }

  public static BusinessUnit createBusinessUnit() {
    BusinessUnit bb = new BusinessUnit();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSUNIT));
    bb.addParent(getBbServiceLocator().getBuService().getFirstElement());
    return bb;
  }

  public static InformationSystem createInformationSystem() {
    InformationSystem bb = new InformationSystem();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEM));
    return bb;
  }

  public static InformationSystemDomain createInformationSystemDomain() {
    InformationSystemDomain bb = new InformationSystemDomain();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN));
    bb.addParent(getBbServiceLocator().getIsdService().getFirstElement());
    return bb;
  }

  public static InformationSystemInterface createInformationSystemInterface() {
    InformationSystemInterface bb = new InformationSystemInterface();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE));
    return bb;
  }

  public static InformationSystemRelease createInformationSystemRelease() {
    InformationSystemRelease bb = new InformationSystemRelease();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    return bb;
  }

  public static InfrastructureElement createInfrastructureElement() {
    InfrastructureElement bb = new InfrastructureElement();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT));
    bb.addParent(getBbServiceLocator().getIeService().getFirstElement());
    return bb;
  }

  public static Product createProduct() {
    Product bb = new Product();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.PRODUCT));
    bb.addParent(getBbServiceLocator().getProductService().getFirstElement());
    return bb;
  }

  public static Project createProject() {
    Project bb = new Project();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.PROJECT));
    bb.addParent(getBbServiceLocator().getProjectService().getFirstElement());
    return bb;
  }

  public static TechnicalComponent createTechnicalComponent() {
    TechnicalComponent bb = new TechnicalComponent();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.TECHNICALCOMPONENT));
    return bb;
  }

  public static TechnicalComponentRelease createTechnicalComponentRelease() {
    TechnicalComponentRelease bb = new TechnicalComponentRelease();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE));
    return bb;
  }

  public static Transport createTransport() {
    Transport bb = new Transport();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.TRANSPORT));
    return bb;
  }

  public static Tcr2IeAssociation createTcr2IeAssociation() {
    Tcr2IeAssociation bb = new Tcr2IeAssociation();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.TCR2IEASSOCIATION));
    return bb;
  }

  public static Tcr2IeAssociation createTcr2IeAssociation(TechnicalComponentRelease tcr, InfrastructureElement ie) {
    Tcr2IeAssociation bb = createTcr2IeAssociation();
    bb.setInfrastructureElement(ie);
    bb.setTechnicalComponentRelease(tcr);
    return bb;
  }

  public static Isr2BoAssociation createIsr2BoAssociation() {
    Isr2BoAssociation bb = new Isr2BoAssociation();
    bb.setBuildingBlockType(getBbTypeService().getBuildingBlockTypeByType(TypeOfBuildingBlock.ISR2BOASSOCIATION));
    return bb;
  }

  public static Isr2BoAssociation createIsr2BoAssociation(InformationSystemRelease isr, BusinessObject bo) {
    Isr2BoAssociation bb = createIsr2BoAssociation();
    bb.setBusinessObject(bo);
    bb.setInformationSystemRelease(isr);
    return bb;
  }

}
