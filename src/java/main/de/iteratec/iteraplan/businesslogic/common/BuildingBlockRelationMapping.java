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
package de.iteratec.iteraplan.businesslogic.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.interfaces.IdentityEntity;


public class BuildingBlockRelationMapping {
  private final BuildingBlock                        buildingBlock;
  private Map<String, Set<? extends IdentityEntity>> mapping;

  public BuildingBlockRelationMapping(BuildingBlock buildingBlock) {
    this.buildingBlock = buildingBlock;
  }

  public BuildingBlock getBuildingBlock() {
    return buildingBlock;
  }

  public Map<String, Set<? extends IdentityEntity>> getMapping() {
    mapping = CollectionUtils.hashMap();

    switch (buildingBlock.getBuildingBlockType().getTypeOfBuildingBlock()) {
      case ARCHITECTURALDOMAIN:
        addMappingForArchitecturalDomain();
        break;
      case BUSINESSDOMAIN:
        addMappingForBusinessDomain();
        break;
      case BUSINESSFUNCTION:
        addMappingForBusinessFunction();
        break;
      case BUSINESSOBJECT:
        addMappingForBusinessObject();
        break;
      case BUSINESSPROCESS:
        addMappingForBusinessProcess();
        break;
      case TECHNICALCOMPONENTRELEASE:
        addMappingForTechnicalComponentRelease();
        break;
      case INFRASTRUCTUREELEMENT:
        addMappingForInfrastructureElement();
        break;
      case INFORMATIONSYSTEMINTERFACE:
        addMappingForInformationSystemInterface();
        break;
      case BUSINESSUNIT:
        addMappingForBusinessUnit();
        break;
      case INFORMATIONSYSTEMDOMAIN:
        addMappingForInformationSystemDomain();
        break;
      case INFORMATIONSYSTEMRELEASE:
        addMappingForInformationSystemRelease();
        break;
      case PROJECT:
        addMappingForProject();
        break;
      case PRODUCT:
        addMappingForProduct();
        break;
      default:
        // Do nothing
    }

    return mapping;
  }

  /** 
   * subclasses can override this to add a condition dependend on the type of building block,
   * whether the according mappnig should be added or not.
   * @param tobbString
   *          String representing the type of building block
   * @return true if mappings for the given type of building block should be added
   */
  protected boolean isToBeAdded(String tobbString) {
    return true;
  }

  private void add(String tobbString, Set<? extends BuildingBlock> mappedBlocks) {
    if (isToBeAdded(tobbString)) {
      mapping.put(tobbString, mappedBlocks);
    }
  }

  private void addBusinessProcesses(Collection<BusinessMapping> businessMappings) {
    if (isToBeAdded(TypeOfBuildingBlock.BUSINESSPROCESS.getValue())) {
      Set<BusinessProcess> connectedBusinessProcesses = new HashSet<BusinessProcess>();
      for (BusinessMapping businessMapping : businessMappings) {
        BusinessProcess process = businessMapping.getBusinessProcess();
        if (process != null && process.getParent() != null) {
          connectedBusinessProcesses.add(process);
        }
      }
      mapping.put(TypeOfBuildingBlock.BUSINESSPROCESS.getValue(), connectedBusinessProcesses);
    }
  }

  private void addBusinessUnits(Collection<BusinessMapping> businessMappings) {
    if (isToBeAdded(TypeOfBuildingBlock.BUSINESSUNIT.getValue())) {
      Set<BusinessUnit> connectedBusinessUnits = new HashSet<BusinessUnit>();
      for (BusinessMapping businessMapping : businessMappings) {
        BusinessUnit unit = businessMapping.getBusinessUnit();
        if (unit != null && unit.getParent() != null) {
          connectedBusinessUnits.add(unit);
        }
      }
      mapping.put(TypeOfBuildingBlock.BUSINESSUNIT.getValue(), connectedBusinessUnits);
    }
  }

  private void addInformationSystemReleases(Collection<BusinessMapping> businessMappings) {
    if (isToBeAdded(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue())) {
      Set<InformationSystemRelease> connectedInformationSystems = new HashSet<InformationSystemRelease>();
      for (BusinessMapping businessMapping : businessMappings) {
        InformationSystemRelease release = businessMapping.getInformationSystemRelease();
        if (release != null) {
          connectedInformationSystems.add(release);
        }
      }
      mapping.put(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue(), connectedInformationSystems);
    }
  }

  private void addProducts(Collection<BusinessMapping> businessMappings) {
    if (isToBeAdded(TypeOfBuildingBlock.PRODUCT.getValue())) {
      Set<Product> connectedProducts = new HashSet<Product>();
      for (BusinessMapping businessMapping : businessMappings) {
        Product product = businessMapping.getProduct();
        if (product != null && product.getParent() != null) {
          connectedProducts.add(product);
        }
      }
      mapping.put(TypeOfBuildingBlock.PRODUCT.getValue(), connectedProducts);
    }
  }

  private void addMappingForArchitecturalDomain() {
    ArchitecturalDomain ad = (ArchitecturalDomain) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.ARCHITECTURALDOMAIN.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 2) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), getSetFromSingleElement(ad.getParent()));
    add(selfReferences.get(1), ad.getChildrenAsSet());
    add(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE.getValue(), Sets.newHashSet(ad.getTechnicalComponentReleases()));
  }

  private void addMappingForBusinessDomain() {
    BusinessDomain bd = (BusinessDomain) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.BUSINESSDOMAIN.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 2) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), getSetFromSingleElement(bd.getParent()));
    add(selfReferences.get(1), bd.getChildrenAsSet());
    add(TypeOfBuildingBlock.BUSINESSOBJECT.getValue(), Sets.newHashSet(bd.getBusinessObjects()));
    add(TypeOfBuildingBlock.BUSINESSFUNCTION.getValue(), Sets.newHashSet(bd.getBusinessFunctions()));
    add(TypeOfBuildingBlock.BUSINESSPROCESS.getValue(), Sets.newHashSet(bd.getBusinessProcesses()));
    add(TypeOfBuildingBlock.BUSINESSUNIT.getValue(), Sets.newHashSet(bd.getBusinessUnits()));
    add(TypeOfBuildingBlock.PRODUCT.getValue(), Sets.newHashSet(bd.getProducts()));
  }

  private void addMappingForBusinessFunction() {
    BusinessFunction bf = (BusinessFunction) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.BUSINESSFUNCTION.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 2) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), getSetFromSingleElement(bf.getParent()));
    add(selfReferences.get(1), bf.getChildrenAsSet());
    add(TypeOfBuildingBlock.BUSINESSOBJECT.getValue(), Sets.newHashSet(bf.getBusinessObjects()));
    add(TypeOfBuildingBlock.BUSINESSDOMAIN.getValue(), Sets.newHashSet(bf.getBusinessDomains()));
    add(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue(), Sets.newHashSet(bf.getInformationSystems()));
  }

  private void addMappingForBusinessObject() {
    BusinessObject bo = (BusinessObject) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.BUSINESSOBJECT.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 4) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), getSetFromSingleElement(bo.getParent()));
    add(selfReferences.get(1), bo.getChildrenAsSet());
    add(selfReferences.get(2), getSetFromSingleElement(bo.getGeneralisation()));
    add(selfReferences.get(3), Sets.newHashSet(bo.getSpecialisations()));
    add(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE.getValue(), Sets.newHashSet(bo.getInformationSystemInterfaces()));
    add(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue(), Sets.newHashSet(bo.getInformationSystemReleases()));
    add(TypeOfBuildingBlock.BUSINESSDOMAIN.getValue(), Sets.newHashSet(bo.getBusinessDomains()));
    add(TypeOfBuildingBlock.BUSINESSFUNCTION.getValue(), Sets.newHashSet(bo.getBusinessFunctions()));
  }

  private void addMappingForBusinessProcess() {
    BusinessProcess bp = (BusinessProcess) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.BUSINESSPROCESS.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 2) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), getSetFromSingleElement(bp.getParent()));
    add(selfReferences.get(1), bp.getChildrenAsSet());
    add(TypeOfBuildingBlock.BUSINESSDOMAIN.getValue(), Sets.newHashSet(bp.getBusinessDomains()));
    addBusinessUnits(bp.getBusinessMappings());
    addProducts(bp.getBusinessMappings());
    addInformationSystemReleases(bp.getBusinessMappings());
  }

  private void addMappingForBusinessUnit() {
    BusinessUnit bu = (BusinessUnit) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.BUSINESSUNIT.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 2) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), getSetFromSingleElement(bu.getParent()));
    add(selfReferences.get(1), bu.getChildrenAsSet());
    add(TypeOfBuildingBlock.BUSINESSDOMAIN.getValue(), Sets.newHashSet(bu.getBusinessDomains()));

    addInformationSystemReleases(bu.getBusinessMappings());
    addBusinessProcesses(bu.getBusinessMappings());
    addProducts(bu.getBusinessMappings());
  }

  private void addMappingForInformationSystemDomain() {
    InformationSystemDomain isd = (InformationSystemDomain) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 2) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), getSetFromSingleElement(isd.getParent()));
    add(selfReferences.get(1), isd.getChildrenAsSet());
    add(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue(), Sets.newHashSet(isd.getInformationSystemReleases()));
  }

  private void addMappingForInformationSystemInterface() {
    InformationSystemInterface isi = (InformationSystemInterface) buildingBlock;

    Set<BusinessObject> businessObjects = new HashSet<BusinessObject>();
    for (Transport transport : isi.getTransports()) {
      businessObjects.add(transport.getBusinessObject());
    }
    add(TypeOfBuildingBlock.BUSINESSOBJECT.getValue(), businessObjects);

    Set<InformationSystemRelease> informationSystemReleases = new HashSet<InformationSystemRelease>();
    informationSystemReleases.add(isi.getInformationSystemReleaseA());
    informationSystemReleases.add(isi.getInformationSystemReleaseB());
    add(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue(), informationSystemReleases);

    add(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE.getValue(), Sets.newHashSet(isi.getTechnicalComponentReleases()));
  }

  private void addMappingForInformationSystemRelease() {
    InformationSystemRelease isr = (InformationSystemRelease) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 6) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), getSetFromSingleElement(isr.getParent()));
    add(selfReferences.get(1), isr.getChildrenAsSet());
    add(selfReferences.get(2), Sets.newHashSet(isr.getPredecessors()));
    add(selfReferences.get(3), Sets.newHashSet(isr.getSuccessors()));
    add(selfReferences.get(4), Sets.newHashSet(isr.getParentComponents()));
    add(selfReferences.get(5), Sets.newHashSet(isr.getBaseComponents()));

    Set<InformationSystemInterface> interfaces = Sets.newHashSet(isr.getInterfacesReleaseA());
    interfaces.addAll(Sets.newHashSet(isr.getInterfacesReleaseB()));
    add(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE.getValue(), interfaces);

    add(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN.getValue(), Sets.newHashSet(isr.getInformationSystemDomains()));
    add(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE.getValue(), Sets.newHashSet(isr.getTechnicalComponentReleases()));
    add(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT.getValue(), Sets.newHashSet(isr.getInfrastructureElements()));
    add(TypeOfBuildingBlock.BUSINESSOBJECT.getValue(), Sets.newHashSet(isr.getBusinessObjects()));
    add(TypeOfBuildingBlock.PROJECT.getValue(), Sets.newHashSet(isr.getProjects()));
    add(TypeOfBuildingBlock.BUSINESSFUNCTION.getValue(), Sets.newHashSet(isr.getBusinessFunctions()));

    addBusinessProcesses(isr.getBusinessMappings());
    addBusinessUnits(isr.getBusinessMappings());
    addProducts(isr.getBusinessMappings());
  }

  private void addMappingForInfrastructureElement() {
    InfrastructureElement ie = (InfrastructureElement) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.INFRASTRUCTUREELEMENT.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 4) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), getSetFromSingleElement(ie.getParent()));
    add(selfReferences.get(1), ie.getChildrenAsSet());
    add(selfReferences.get(2), Sets.newHashSet(ie.getParentComponents()));
    add(selfReferences.get(3), Sets.newHashSet(ie.getBaseComponents()));
    add(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue(), Sets.newHashSet(ie.getInformationSystemReleases()));
    add(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE.getValue(), Sets.newHashSet(ie.getTechnicalComponentReleases()));
  }

  private void addMappingForProduct() {
    Product p = (Product) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.PRODUCT.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 2) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), getSetFromSingleElement(p.getParent()));
    add(selfReferences.get(1), p.getChildrenAsSet());
    add(TypeOfBuildingBlock.BUSINESSDOMAIN.getValue(), Sets.newHashSet(p.getBusinessDomains()));
    addInformationSystemReleases(p.getBusinessMappings());
    addBusinessProcesses(p.getBusinessMappings());
    addBusinessUnits(p.getBusinessMappings());
  }

  private void addMappingForProject() {
    Project p = (Project) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.PROJECT.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 2) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(1), getSetFromSingleElement(p.getParent()));
    add(selfReferences.get(0), p.getChildrenAsSet());
    add(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue(), Sets.newHashSet(p.getInformationSystemReleases()));
  }

  private void addMappingForTechnicalComponentRelease() {
    TechnicalComponentRelease tc = (TechnicalComponentRelease) buildingBlock;
    List<String> selfReferences = TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE.getSelfReferencesPropertyKeys();

    if (selfReferences.size() != 4) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    add(selfReferences.get(0), Sets.newHashSet(tc.getPredecessors()));
    add(selfReferences.get(1), Sets.newHashSet(tc.getSuccessors()));
    add(selfReferences.get(2), Sets.newHashSet(tc.getParentComponents()));
    add(selfReferences.get(3), Sets.newHashSet(tc.getBaseComponents()));

    add(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE.getValue(), Sets.newHashSet(tc.getInformationSystemReleases()));
    add(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE.getValue(), Sets.newHashSet(tc.getInformationSystemInterfaces()));
    add(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT.getValue(), Sets.newHashSet(tc.getInfrastructureElements()));
    add(TypeOfBuildingBlock.ARCHITECTURALDOMAIN.getValue(), Sets.newHashSet(tc.getArchitecturalDomains()));
  }

  private <T extends IdentityEntity> Set<T> getSetFromSingleElement(T parent) {
    if (parent == null) {
      return new HashSet<T>();
    }
    else {
      Set<T> resultSet = CollectionUtils.hashSet();
      resultSet.add(parent);
      return resultSet;
    }
  }

}
