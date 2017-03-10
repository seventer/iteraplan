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
package de.iteratec.iteraplan.businesslogic.service.diffs;

import org.joda.time.DateTime;

import de.iteratec.iteraplan.common.util.Preconditions;
import de.iteratec.iteraplan.diffs.model.ArchitecturalDomainChangeset;
import de.iteratec.iteraplan.diffs.model.BusinessDomainChangeset;
import de.iteratec.iteraplan.diffs.model.BusinessFunctionChangeset;
import de.iteratec.iteraplan.diffs.model.BusinessObjectChangeset;
import de.iteratec.iteraplan.diffs.model.BusinessProcessChangeset;
import de.iteratec.iteraplan.diffs.model.BusinessUnitChangeset;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.diffs.model.InformationSystemDomainChangeset;
import de.iteratec.iteraplan.diffs.model.InformationSystemReleaseChangeset;
import de.iteratec.iteraplan.diffs.model.InfrastructureElementChangeset;
import de.iteratec.iteraplan.diffs.model.InterfaceChangeset;
import de.iteratec.iteraplan.diffs.model.ProductChangeset;
import de.iteratec.iteraplan.diffs.model.ProjectChangeset;
import de.iteratec.iteraplan.diffs.model.TechnicalComponentReleaseChangeset;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
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


public final class BBChangesetFactory {

  private BBChangesetFactory() {
    // prevent instantiation
  }

  public static HistoryBBChangeset createChangeset(BuildingBlock fromBb, BuildingBlock toBb, String author, DateTime timestamp) {
    if (toBb == null) {
      return null;
    }
    if (fromBb != null) {
      Preconditions.checkArgument(fromBb.getId().equals(toBb.getId()), "Param fromBb and toBb should have the same id");
    }

    switch (toBb.getTypeOfBuildingBlock()) {
      case ARCHITECTURALDOMAIN:
        return createADchangeset((ArchitecturalDomain) fromBb, (ArchitecturalDomain) toBb, author, timestamp);
      case BUSINESSDOMAIN:
        return createBDchangeset((BusinessDomain) fromBb, (BusinessDomain) toBb, author, timestamp);
      case BUSINESSFUNCTION:
        return createBFchangeset((BusinessFunction) fromBb, (BusinessFunction) toBb, author, timestamp);
      case BUSINESSOBJECT:
        return createBOchangeset((BusinessObject) fromBb, (BusinessObject) toBb, author, timestamp);
      case BUSINESSPROCESS:
        return createBPchangeset((BusinessProcess) fromBb, (BusinessProcess) toBb, author, timestamp);
      case BUSINESSUNIT:
        return createBUchangeset((BusinessUnit) fromBb, (BusinessUnit) toBb, author, timestamp);
      case INFORMATIONSYSTEMDOMAIN:
        return createISDchangeset((InformationSystemDomain) fromBb, (InformationSystemDomain) toBb, author, timestamp);
      case INFORMATIONSYSTEMRELEASE:
        return createISRchangeset((InformationSystemRelease) fromBb, (InformationSystemRelease) toBb, author, timestamp);
      case INFRASTRUCTUREELEMENT:
        return createIEchangeset((InfrastructureElement) fromBb, (InfrastructureElement) toBb, author, timestamp);
      case INFORMATIONSYSTEMINTERFACE:
        return createISIchangeset((InformationSystemInterface) fromBb, (InformationSystemInterface) toBb, author, timestamp);
      case PRODUCT:
        return createProductChangeset((Product) fromBb, (Product) toBb, author, timestamp);
      case PROJECT:
        return createProjectChangeset((Project) fromBb, (Project) toBb, author, timestamp);
      case TECHNICALCOMPONENTRELEASE:
        return createTCRchangeset((TechnicalComponentRelease) fromBb, (TechnicalComponentRelease) toBb, author, timestamp);

      default:
        break;
    }
    return null;
  }

  private static HistoryBBChangeset createTCRchangeset(TechnicalComponentRelease fromBb, TechnicalComponentRelease toBb, String author,
                                                       DateTime timestamp) {
    TechnicalComponentReleaseChangeset changeset = new TechnicalComponentReleaseChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author,
        timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    CorePropertyDiffer.addRuntimeDiffs(changeset, fromBb, toBb);
    CorePropertyDiffer.addStatusDiff(changeset, fromBb, toBb);
    CorePropertyDiffer.addAvailableForInterfacesDiff(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addReleaseSequenceDiffs(changeset, fromBb, toBb);

    RelationDiffer.addUsageDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getArchitecturalDomains(), fromBb.getArchitecturalDomains(), changeset.getArchitecturalDomainsAdded());
    RelationDiffer.addChange(fromBb.getArchitecturalDomains(), toBb.getArchitecturalDomains(), changeset.getArchitecturalDomainsRemoved());

    RelationDiffer.addChange(toBb.getInfrastructureElements(), fromBb.getInfrastructureElements(), changeset.getInfrastructureElementsAdded());
    RelationDiffer.addChange(fromBb.getInfrastructureElements(), toBb.getInfrastructureElements(), changeset.getInfrastructureElementsRemoved());

    RelationDiffer.addChange(toBb.getInformationSystemInterfaces(), fromBb.getInformationSystemInterfaces(), changeset.getInterfacesAdded());
    RelationDiffer.addChange(fromBb.getInformationSystemInterfaces(), toBb.getInformationSystemInterfaces(), changeset.getInterfacesRemoved());

    RelationDiffer.addChange(toBb.getInformationSystemReleases(), fromBb.getInformationSystemReleases(),
        changeset.getInformationSystemReleasesAdded());
    RelationDiffer.addChange(fromBb.getInformationSystemReleases(), toBb.getInformationSystemReleases(),
        changeset.getInformationSystemReleasesRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createProjectChangeset(Project fromBb, Project toBb, String author, DateTime timestamp) {
    ProjectChangeset changeset = new ProjectChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    CorePropertyDiffer.addRuntimeDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addHierarchyDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getInformationSystemReleases(), fromBb.getInformationSystemReleases(),
        changeset.getInformationSystemReleasesAdded());
    RelationDiffer.addChange(fromBb.getInformationSystemReleases(), toBb.getInformationSystemReleases(),
        changeset.getInformationSystemReleasesRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createProductChangeset(Product fromBb, Product toBb, String author, DateTime timestamp) {
    ProductChangeset changeset = new ProductChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addHierarchyDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getBusinessDomains(), fromBb.getBusinessDomains(), changeset.getBusinessDomainsAdded());
    RelationDiffer.addChange(fromBb.getBusinessDomains(), toBb.getBusinessDomains(), changeset.getBusinessDomainsRemoved());

    RelationDiffer.addChange(toBb.getBusinessMappings(), fromBb.getBusinessMappings(), changeset.getBusinessMappingsAdded());
    RelationDiffer.addChange(fromBb.getBusinessMappings(), toBb.getBusinessMappings(), changeset.getBusinessMappingsRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createISIchangeset(InformationSystemInterface fromBb, InformationSystemInterface toBb, String author,
                                                       DateTime timestamp) {
    InterfaceChangeset changeset = new InterfaceChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    CorePropertyDiffer.addDirectionDiff(changeset, fromBb, toBb);
    CorePropertyDiffer.addConnectedIsrDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getTechnicalComponentReleases(), fromBb.getTechnicalComponentReleases(),
        changeset.getTechnicalComponentReleasesAdded());
    RelationDiffer.addChange(fromBb.getTechnicalComponentReleases(), toBb.getTechnicalComponentReleases(),
        changeset.getTechnicalComponentReleasesRemoved());

    RelationDiffer.addChange(toBb.getTransports(), fromBb.getTransports(), changeset.getTransportsAdded());
    RelationDiffer.addChange(fromBb.getTransports(), toBb.getTransports(), changeset.getTransportsRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createIEchangeset(InfrastructureElement fromBb, InfrastructureElement toBb, String author, DateTime timestamp) {
    InfrastructureElementChangeset changeset = new InfrastructureElementChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addHierarchyDiffs(changeset, fromBb, toBb);

    RelationDiffer.addUsageDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getInformationSystemReleases(), fromBb.getInformationSystemReleases(),
        changeset.getInformationSystemReleasesAdded());
    RelationDiffer.addChange(fromBb.getInformationSystemReleases(), toBb.getInformationSystemReleases(),
        changeset.getInformationSystemReleasesRemoved());

    RelationDiffer.addChange(toBb.getTechnicalComponentReleases(), fromBb.getTechnicalComponentReleases(),
        changeset.getTechnicalComponentReleasesAdded());
    RelationDiffer.addChange(fromBb.getTechnicalComponentReleases(), toBb.getTechnicalComponentReleases(),
        changeset.getTechnicalComponentReleasesRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createISRchangeset(InformationSystemRelease fromBb, InformationSystemRelease toBb, String author,
                                                       DateTime timestamp) {
    InformationSystemReleaseChangeset changeset = new InformationSystemReleaseChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author,
        timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    // version differences are already captured by name comparison
    CorePropertyDiffer.addRuntimeDiffs(changeset, fromBb, toBb);
    CorePropertyDiffer.addStatusDiff(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addIsrCompositionDiffs(changeset, fromBb, toBb);

    RelationDiffer.addReleaseSequenceDiffs(changeset, fromBb, toBb);

    RelationDiffer.addUsageDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getInformationSystemDomains(), fromBb.getInformationSystemDomains(), changeset.getInformationSystemDomainsAdded());
    RelationDiffer
        .addChange(fromBb.getInformationSystemDomains(), toBb.getInformationSystemDomains(), changeset.getInformationSystemDomainsRemoved());

    RelationDiffer.addChange(toBb.getTechnicalComponentReleases(), fromBb.getTechnicalComponentReleases(),
        changeset.getTechnicalComponentReleasesAdded());
    RelationDiffer.addChange(fromBb.getTechnicalComponentReleases(), toBb.getTechnicalComponentReleases(),
        changeset.getTechnicalComponentReleasesRemoved());

    RelationDiffer.addChange(toBb.getInfrastructureElements(), fromBb.getInfrastructureElements(), changeset.getInfrastructureElementsAdded());
    RelationDiffer.addChange(fromBb.getInfrastructureElements(), toBb.getInfrastructureElements(), changeset.getInfrastructureElementsRemoved());

    RelationDiffer.addChange(toBb.getProjects(), fromBb.getProjects(), changeset.getProjectsAdded());
    RelationDiffer.addChange(fromBb.getProjects(), toBb.getProjects(), changeset.getProjectsRemoved());

    RelationDiffer.addChange(toBb.getBusinessObjects(), fromBb.getBusinessObjects(), changeset.getBusinessObjectsAdded());
    RelationDiffer.addChange(fromBb.getBusinessObjects(), toBb.getBusinessObjects(), changeset.getBusinessObjectsRemoved());

    RelationDiffer.addChange(toBb.getBusinessFunctions(), fromBb.getBusinessFunctions(), changeset.getBusinessFunctionsAdded());
    RelationDiffer.addChange(fromBb.getBusinessFunctions(), toBb.getBusinessFunctions(), changeset.getBusinessFunctionsRemoved());

    RelationDiffer.addChange(toBb.getBusinessMappings(), fromBb.getBusinessMappings(), changeset.getBusinessMappingsAdded());
    RelationDiffer.addChange(fromBb.getBusinessMappings(), toBb.getBusinessMappings(), changeset.getBusinessMappingsRemoved());

    RelationDiffer.addChange(toBb.getInterfacesReleaseA(), fromBb.getInterfacesReleaseA(), changeset.getInterfaceAdded());
    RelationDiffer.addChange(toBb.getInterfacesReleaseB(), fromBb.getInterfacesReleaseB(), changeset.getInterfaceAdded());

    RelationDiffer.addChange(fromBb.getInterfacesReleaseA(), toBb.getInterfacesReleaseA(), changeset.getInterfaceRemoved());
    RelationDiffer.addChange(fromBb.getInterfacesReleaseB(), toBb.getInterfacesReleaseB(), changeset.getInterfaceRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createISDchangeset(InformationSystemDomain fromBb, InformationSystemDomain toBb, String author, DateTime timestamp) {
    InformationSystemDomainChangeset changeset = new InformationSystemDomainChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addHierarchyDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getInformationSystemReleases(), fromBb.getInformationSystemReleases(),
        changeset.getInformationSystemReleasesAdded());
    RelationDiffer.addChange(fromBb.getInformationSystemReleases(), toBb.getInformationSystemReleases(),
        changeset.getInformationSystemReleasesRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createBUchangeset(BusinessUnit fromBb, BusinessUnit toBb, String author, DateTime timestamp) {
    BusinessUnitChangeset changeset = new BusinessUnitChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addHierarchyDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getBusinessDomains(), fromBb.getBusinessDomains(), changeset.getBusinessDomainsAdded());
    RelationDiffer.addChange(fromBb.getBusinessDomains(), toBb.getBusinessDomains(), changeset.getBusinessDomainsRemoved());

    RelationDiffer.addChange(toBb.getBusinessMappings(), fromBb.getBusinessMappings(), changeset.getBusinessMappingsAdded());
    RelationDiffer.addChange(fromBb.getBusinessMappings(), toBb.getBusinessMappings(), changeset.getBusinessMappingsRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createBPchangeset(BusinessProcess fromBb, BusinessProcess toBb, String author, DateTime timestamp) {
    BusinessProcessChangeset changeset = new BusinessProcessChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addHierarchyDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getBusinessDomains(), fromBb.getBusinessDomains(), changeset.getBusinessDomainsAdded());
    RelationDiffer.addChange(fromBb.getBusinessDomains(), toBb.getBusinessDomains(), changeset.getBusinessDomainsRemoved());

    RelationDiffer.addChange(toBb.getBusinessMappings(), fromBb.getBusinessMappings(), changeset.getBusinessMappingsAdded());
    RelationDiffer.addChange(fromBb.getBusinessMappings(), toBb.getBusinessMappings(), changeset.getBusinessMappingsRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createBOchangeset(BusinessObject fromBb, BusinessObject toBb, String author, DateTime timestamp) {
    BusinessObjectChangeset changeset = new BusinessObjectChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addHierarchyDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getBusinessDomains(), fromBb.getBusinessDomains(), changeset.getBusinessDomainsAdded());
    RelationDiffer.addChange(fromBb.getBusinessDomains(), toBb.getBusinessDomains(), changeset.getBusinessDomainsRemoved());

    RelationDiffer.addChange(toBb.getBusinessFunctions(), fromBb.getBusinessFunctions(), changeset.getBusinessFunctionsAdded());
    RelationDiffer.addChange(fromBb.getBusinessFunctions(), toBb.getBusinessFunctions(), changeset.getBusinessFunctionsRemoved());

    RelationDiffer.addChange(toBb.getInformationSystemReleases(), fromBb.getInformationSystemReleases(),
        changeset.getInformationSystemReleasesAdded());
    RelationDiffer.addChange(fromBb.getInformationSystemReleases(), toBb.getInformationSystemReleases(),
        changeset.getInformationSystemReleasesRemoved());

    RelationDiffer.addChange(toBb.getInformationSystemInterfaces(), fromBb.getInformationSystemInterfaces(),
        changeset.getInformationSystemInterfacesAdded());
    RelationDiffer.addChange(fromBb.getInformationSystemInterfaces(), toBb.getInformationSystemInterfaces(),
        changeset.getInformationSystemInterfacesRemoved());

    if (fromBb.getGeneralisation() == null) {
      changeset.setGeneralisationTo(toBb.getGeneralisation());
    }
    else if (toBb.getGeneralisation() == null) {
      changeset.setGeneralisationFrom(fromBb.getGeneralisation());
    }
    else if (!fromBb.getGeneralisation().equals(toBb.getGeneralisation())) {
      changeset.setGeneralisationFrom(fromBb.getGeneralisation());
      changeset.setGeneralisationTo(toBb.getGeneralisation());
    }

    RelationDiffer.addChange(toBb.getSpecialisations(), fromBb.getSpecialisations(), changeset.getSpecialisationsAdded());
    RelationDiffer.addChange(fromBb.getSpecialisations(), toBb.getSpecialisations(), changeset.getSpecialisationsRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createBFchangeset(BusinessFunction fromBb, BusinessFunction toBb, String author, DateTime timestamp) {
    BusinessFunctionChangeset changeset = new BusinessFunctionChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addHierarchyDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getBusinessObjects(), fromBb.getBusinessObjects(), changeset.getBusinessObjectsAdded());
    RelationDiffer.addChange(fromBb.getBusinessObjects(), toBb.getBusinessObjects(), changeset.getBusinessObjectsRemoved());

    RelationDiffer.addChange(toBb.getBusinessDomains(), fromBb.getBusinessDomains(), changeset.getBusinessDomainsAdded());
    RelationDiffer.addChange(fromBb.getBusinessDomains(), toBb.getBusinessDomains(), changeset.getBusinessDomainsRemoved());

    RelationDiffer.addChange(toBb.getInformationSystems(), fromBb.getInformationSystems(), changeset.getInformationSystemAdded());
    RelationDiffer.addChange(fromBb.getInformationSystems(), toBb.getInformationSystems(), changeset.getInformationSystemRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createBDchangeset(BusinessDomain fromBb, BusinessDomain toBb, String author, DateTime timestamp) {
    BusinessDomainChangeset changeset = new BusinessDomainChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addHierarchyDiffs(changeset, fromBb, toBb);

    RelationDiffer.addChange(toBb.getBusinessFunctions(), fromBb.getBusinessFunctions(), changeset.getBusinessFunctionsAdded());
    RelationDiffer.addChange(fromBb.getBusinessFunctions(), toBb.getBusinessFunctions(), changeset.getBusinessFunctionsRemoved());

    RelationDiffer.addChange(toBb.getBusinessProcesses(), fromBb.getBusinessProcesses(), changeset.getBusinessProcessesAdded());
    RelationDiffer.addChange(fromBb.getBusinessProcesses(), toBb.getBusinessProcesses(), changeset.getBusinessProcessesRemoved());

    RelationDiffer.addChange(toBb.getBusinessUnits(), fromBb.getBusinessUnits(), changeset.getBusinessUnitsAdded());
    RelationDiffer.addChange(fromBb.getBusinessUnits(), toBb.getBusinessUnits(), changeset.getBusinessUnitsRemoved());

    RelationDiffer.addChange(toBb.getBusinessObjects(), fromBb.getBusinessObjects(), changeset.getBusinessObjectsAdded());
    RelationDiffer.addChange(fromBb.getBusinessObjects(), toBb.getBusinessObjects(), changeset.getBusinessObjectsRemoved());

    RelationDiffer.addChange(toBb.getProducts(), fromBb.getProducts(), changeset.getProductsAdded());
    RelationDiffer.addChange(fromBb.getProducts(), toBb.getProducts(), changeset.getProductsRemoved());

    return changeset;
  }

  private static HistoryBBChangeset createADchangeset(ArchitecturalDomain fromBb, ArchitecturalDomain toBb, String author, DateTime timestamp) {
    ArchitecturalDomainChangeset changeset = new ArchitecturalDomainChangeset(toBb.getId(), toBb.getTypeOfBuildingBlock(), author, timestamp);
    if (fromBb == null) {
      // there is nothing to compare with, so just return with the initial state
      changeset.setInitialChangeset(true);
      return changeset;
    }

    CorePropertyDiffer.addNameDescriptionDiffs(changeset, fromBb, toBb);
    AttributesDiffer.addAttributeDiffs(changeset, fromBb, toBb);

    RelationDiffer.addHierarchyDiffs(changeset, fromBb, toBb);

    // diff AD <-> TCR relation
    RelationDiffer.addChange(toBb.getTechnicalComponentReleases(), fromBb.getTechnicalComponentReleases(),
        changeset.getTechnicalComponentReleasesAdded());
    RelationDiffer.addChange(fromBb.getTechnicalComponentReleases(), toBb.getTechnicalComponentReleases(),
        changeset.getTechnicalComponentReleasesRemoved());

    return changeset;
  }
}
