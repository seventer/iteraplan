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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.InformationFlow.InformationFlowOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Landscape.LandscapeOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.BaseDateUtils;
import de.iteratec.iteraplan.common.util.DateUtils;
import de.iteratec.iteraplan.common.util.StringEnumReflectionHelper;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.RuntimePeriodDelegate;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelper;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelperBusinessProcessIsr;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelperBusinessUnitIsr;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelperInformationSystemIsr;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelperInformationSystemTcr;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelperInfrastructureElementTcr;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelperProductIsr;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelperProjectIsr;
import de.iteratec.iteraplan.presentation.dialog.FastExport.LandscapeDiagramHelperTechnicalComponentTcr;
import de.iteratec.iteraplan.presentation.dialog.FastExport.MasterplanDiagramHelper;
import de.iteratec.iteraplan.presentation.dialog.FastExport.MasterplanDiagramHelperInformationSystem;
import de.iteratec.iteraplan.presentation.dialog.FastExport.MasterplanDiagramHelperProject;
import de.iteratec.iteraplan.presentation.dialog.FastExport.MasterplanDiagramHelperTechnicalComponent;


public class FastExportServiceImpl implements FastExportService {
  private BusinessProcessService           businessProcessService;
  private BusinessUnitService              businessUnitService;
  private InfrastructureElementService     infrastructureElementService;
  private InformationSystemDomainService   informationSystemDomainService;
  private InformationSystemReleaseService  informationSystemReleaseService;
  private ProductService                   productService;
  private ProjectService                   projectService;
  private TechnicalComponentReleaseService technicalComponentReleaseService;

  // time span covering half a year in months
  private static final int                 HALF_YEAR_IN_MONTHS = 6;

  public void setBusinessProcessService(BusinessProcessService businessProcessService) {
    this.businessProcessService = businessProcessService;
  }

  public void setBusinessUnitService(BusinessUnitService businessUnitService) {
    this.businessUnitService = businessUnitService;
  }

  public void setInfrastructureElementService(InfrastructureElementService infrastructureElementService) {
    this.infrastructureElementService = infrastructureElementService;
  }

  public void setInformationSystemDomainService(InformationSystemDomainService informationSystemDomainService) {
    this.informationSystemDomainService = informationSystemDomainService;
  }

  public void setInformationSystemReleaseService(InformationSystemReleaseService informationSystemReleaseService) {
    this.informationSystemReleaseService = informationSystemReleaseService;
  }

  public void setProductService(ProductService productService) {
    this.productService = productService;
  }

  public void setProjectService(ProjectService projectService) {
    this.projectService = projectService;
  }

  public void setTechnicalComponentReleaseService(TechnicalComponentReleaseService technicalComponentReleaseService) {
    this.technicalComponentReleaseService = technicalComponentReleaseService;
  }

  public InformationFlowOptionsBean retrieveInformationFlowOptionsForFastExport() {
    InformationFlowOptionsBean bean = new InformationFlowOptionsBean();
    bean.getColorOptionsBean().setDimensionAttributeId(Integer.valueOf(GraphicalExportBaseOptions.STATUS_SELECTED));
    bean.getColorOptionsBean().setColorRangeAvailable(false);
    bean.getColorOptionsBean().refreshDimensionOptions(
        StringEnumReflectionHelper.getLanguageSpecificEnumValues(TypeOfStatus.class, UserContext.getCurrentLocale()));
    bean.getLineOptionsBean().setDimensionAttributeId(Integer.valueOf(GraphicalExportBaseOptions.NOTHING_SELECTED));
    bean.getLineOptionsBean().resetValueToLineTypeMap();

    return bean;
  }

  public List<InformationSystemRelease> retrieveRelatedIsForInformationFlowFastExport(BuildingBlock startElement) {
    if (!(startElement instanceof InformationSystemRelease)) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    InformationSystemRelease rel = (InformationSystemRelease) startElement;

    // Retrieve interfaces
    Set<InformationSystemInterface> connections = rel.getAllConnections();

    // Retrieve connected releases
    List<InformationSystemRelease> releases = new ArrayList<InformationSystemRelease>();
    releases.add(rel);

    for (InformationSystemInterface con : connections) {
      if (con.getInformationSystemReleaseA().equals(rel)) {
        releases.add(con.getInformationSystemReleaseB());
      }
      if (con.getInformationSystemReleaseB().equals(rel)) {
        releases.add(con.getInformationSystemReleaseA());
      }
    }

    return releases;
  }

  public List<InformationSystemRelease> retrieveRelatedIsForInformationFlowFastExportFromMultiIs(Set<InformationSystemRelease> setReleases) {
    List<InformationSystemRelease> releases = new ArrayList<InformationSystemRelease>();

    // Add all related Releases, for each of original Set of releases
    for (InformationSystemRelease curRel : setReleases) {
      List<InformationSystemRelease> subReleases = retrieveRelatedIsForInformationFlowFastExport(curRel);
      releases.addAll(subReleases);
    }
    return releases;
  }

  public RuntimePeriod getEncompassingRuntimePeriod(List<BuildingBlock> elementsToBeEncompassedInTimespan) {

    Date earliestStartDate = BaseDateUtils.MAX_DATE.toDate();
    Date latestEndDate = BaseDateUtils.MIN_DATE.toDate();

    for (BuildingBlock buildingBlock : elementsToBeEncompassedInTimespan) {

      if (!(buildingBlock instanceof RuntimePeriodDelegate)) {
        throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
      }

      RuntimePeriod runtimePeriod = ((RuntimePeriodDelegate) buildingBlock).getRuntimePeriod();

      if (runtimePeriod != null && runtimePeriod.getStart() != null && runtimePeriod.getStart().before(earliestStartDate)) {
        earliestStartDate = runtimePeriod.getStart();
      }
      if (runtimePeriod != null && runtimePeriod.getEnd() != null && runtimePeriod.getEnd().after(latestEndDate)) {
        latestEndDate = runtimePeriod.getEnd();
      }
    }

    return estimateEncompassingRuntimePeriodForDates(earliestStartDate, latestEndDate);
  }

  private RuntimePeriod estimateEncompassingRuntimePeriodForDates(Date earliestStartDate, Date latestEndDate) {

    Date startDate = earliestStartDate;
    Date endDate = latestEndDate;
    if (earliestStartDate.after(latestEndDate)) {
      // Over all elements of the list, at least one of the start or end date is not defined

      if (earliestStartDate.compareTo(BaseDateUtils.MAX_DATE.toDate()) == 0 && latestEndDate.after(BaseDateUtils.MIN_DATE.toDate())) {
        // Start date is not defined, end date is defined
        Calendar nowCalendar = Calendar.getInstance();
        Calendar oneYearBeforeEndDateCalendar = Calendar.getInstance();
        oneYearBeforeEndDateCalendar.setTime(latestEndDate);
        oneYearBeforeEndDateCalendar.roll(Calendar.YEAR, -1);

        startDate = DateUtils.earlier(nowCalendar.getTime(), oneYearBeforeEndDateCalendar.getTime());
        endDate = DateUtils.later(nowCalendar.getTime(), latestEndDate);
      }
      else if (earliestStartDate.before(BaseDateUtils.MAX_DATE.toDate()) && latestEndDate.compareTo(BaseDateUtils.MIN_DATE.toDate()) == 0) {
        // Start date is defined, end date is not defined
        Calendar nowCalendar = Calendar.getInstance();
        Calendar oneYearAfterStartDateCalendar = Calendar.getInstance();
        oneYearAfterStartDateCalendar.setTime(earliestStartDate);
        oneYearAfterStartDateCalendar.roll(Calendar.YEAR, 1);

        startDate = DateUtils.earlier(nowCalendar.getTime(), earliestStartDate);
        endDate = DateUtils.later(nowCalendar.getTime(), oneYearAfterStartDateCalendar.getTime());
      }
      else {
        // There is no start or end date defined over all elements of the list
        Calendar calendar = Calendar.getInstance();
        calendar.roll(Calendar.YEAR, false);
        calendar.roll(Calendar.MONTH, HALF_YEAR_IN_MONTHS);
        startDate = calendar.getTime();

        calendar.roll(Calendar.YEAR, 1);
        endDate = calendar.getTime();
      }
    }
    else {
      // Both start and end date are defined over the list
      Calendar nowCalendar = Calendar.getInstance();

      startDate = DateUtils.earlier(nowCalendar.getTime(), earliestStartDate);
      endDate = DateUtils.later(nowCalendar.getTime(), latestEndDate);
    }

    return new RuntimePeriod(startDate, endDate);
  }

  public MasterplanOptionsBean initMasterplanOptionsForFastExport(String serverUrl) {
    MasterplanOptionsBean options = new MasterplanOptionsBean();

    options.setServerUrl(serverUrl);
    options.setNakedExport(true);

    return options;

  }

  public void configureMasterplanOptionsForFastExport(MasterplanOptionsBean bean, RuntimePeriod encompassingTimespan) {

    bean.getLevel0Options().setHierarchicalSort(true);
    try {
      bean.setStartDateString(DateUtils.formatAsString(encompassingTimespan.getStart(), UserContext.getCurrentLocale()));
      bean.setEndDateString(DateUtils.formatAsString(encompassingTimespan.getEnd(), UserContext.getCurrentLocale()));
    } catch (Exception ex) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INVALID_DATES, ex);
    }

  }

  // Bean may be null if we solely want the list of BuildingBlocks (for configure)
  public List<BuildingBlock> retrieveBuildingBlockListForMasterplanFastExport(BuildingBlock element, MasterplanOptionsBean bean, String diagramVariant) {

    DiagramVariant variant = DiagramVariant.fromName(diagramVariant);
    MasterplanDiagramHelper masterplanHelper = getMasterplanHelper(element);

    return masterplanHelper.determineResults(element, variant, bean);
  }

  public LandscapeOptionsBean retrieveLandscapeOptionsForFastExport() {
    LandscapeOptionsBean landscapeOptions = new LandscapeOptionsBean();

    return landscapeOptions;
  }

  /**
   * loads the building block from the type bbType with the id id.
   *
   * @param id
   *          the id of the element.
   * @param bbType
   *          the building block type of the element.
   * @return See method description.
   */
  public BuildingBlock getStartElement(Integer id, String bbType) {

    TypeOfBuildingBlock tob = TypeOfBuildingBlock.fromInitialCapString(bbType);
    BuildingBlockService<?, Integer> service;

    switch (tob) {
      case INFRASTRUCTUREELEMENT:
        service = infrastructureElementService;
        break;
      case INFORMATIONSYSTEMDOMAIN:
        service = informationSystemDomainService;
        break;
      case INFORMATIONSYSTEMRELEASE:
        service = informationSystemReleaseService;
        break;
      case PROJECT:
        service = projectService;
        break;
      case PRODUCT:
        service = productService;
        break;
      case BUSINESSPROCESS:
        service = businessProcessService;
        break;
      case BUSINESSUNIT:
        service = businessUnitService;
        break;
      case TECHNICALCOMPONENTRELEASE:
        service = technicalComponentReleaseService;
        break;
      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
    return service.loadObjectById(id);
  }

  public List<InformationSystemRelease> getInformationFlowReleases(BuildingBlock startElement) {
    List<InformationSystemRelease> releases;
    switch (startElement.getBuildingBlockType().getTypeOfBuildingBlock()) {
      case INFORMATIONSYSTEMDOMAIN:
        // Get more specific type so we can use its features
        InformationSystemDomain startDomain = (InformationSystemDomain) startElement;

        // Get the Set of Releases from the Domain
        releases = this.retrieveRelatedIsForInformationFlowFastExportFromMultiIs(startDomain.getInformationSystemReleases());
        break;

      case PROJECT:
        // Get more specific type so we can use its features
        Project startProject = (Project) startElement;

        // Get the Set of Releases from the Domain
        releases = this.retrieveRelatedIsForInformationFlowFastExportFromMultiIs(startProject.getInformationSystemReleases());
        break;

      case INFORMATIONSYSTEMRELEASE:
        releases = this.retrieveRelatedIsForInformationFlowFastExport(startElement);
        break;

      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
    return releases;
  }

  public LandscapeDiagramHelper<? extends BuildingBlock> retrieveLandscapeHelper(String diagramVariant, BuildingBlock element) {
    DiagramVariant variant = DiagramVariant.fromName(diagramVariant);

    TypeOfBuildingBlock tob = element.getBuildingBlockType().getTypeOfBuildingBlock();
    switch (tob) {

      case INFORMATIONSYSTEMRELEASE:
        return getIsLandscapeDiagramHelper((InformationSystemRelease) element, variant);

      case PRODUCT:
        return getProdLandscapeDiagramHelper((Product) element, variant);

      case BUSINESSUNIT:
        return getBuLandscapeDiagramHelper((BusinessUnit) element, variant);

      case BUSINESSPROCESS:
        return getBpLandscapeDiagramHelper((BusinessProcess) element, variant);

      case PROJECT:
        return new LandscapeDiagramHelperProjectIsr((Project) element, TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.PROJECT);

      case TECHNICALCOMPONENTRELEASE:
        return new LandscapeDiagramHelperTechnicalComponentTcr((TechnicalComponentRelease) element);

      case INFRASTRUCTUREELEMENT:
        return new LandscapeDiagramHelperInfrastructureElementTcr((InfrastructureElement) element);

      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

  private LandscapeDiagramHelper<? extends BuildingBlock> getBpLandscapeDiagramHelper(BusinessProcess bp, DiagramVariant variant) {
    switch (variant) {
      case LANDSCAPE_BY_BUSINESSUNITS:
        return new LandscapeDiagramHelperBusinessProcessIsr(bp, TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.BUSINESSUNIT);

      case LANDSCAPE_BY_BUSINESSFUNCTIONS:
        return new LandscapeDiagramHelperBusinessProcessIsr(bp, TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.BUSINESSFUNCTION);

      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

  private LandscapeDiagramHelper<? extends BuildingBlock> getBuLandscapeDiagramHelper(BusinessUnit bu, DiagramVariant variant) {
    switch (variant) {
      case LANDSCAPE_BY_BUSINESSPROCESSES:
        return new LandscapeDiagramHelperBusinessUnitIsr(bu, TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.BUSINESSUNIT);

      case LANDSCAPE_BY_PRODUCTS:
        return new LandscapeDiagramHelperBusinessUnitIsr(bu, TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.PRODUCT);

      case LANDSCAPE_BY_PROJECTS:
        return new LandscapeDiagramHelperBusinessUnitIsr(bu, TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.PROJECT);

      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

  private LandscapeDiagramHelper<? extends BuildingBlock> getProdLandscapeDiagramHelper(Product prod, DiagramVariant variant) {
    switch (variant) {
      case LANDSCAPE_BY_BUSINESSUNITS:
        return new LandscapeDiagramHelperProductIsr(prod, TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.PRODUCT);

      case LANDSCAPE_BY_BUSINESSPROCESSES:
        return new LandscapeDiagramHelperProductIsr(prod, TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.PRODUCT);

      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

  private LandscapeDiagramHelper<? extends BuildingBlock> getIsLandscapeDiagramHelper(InformationSystemRelease isr, DiagramVariant variant) {
    switch (variant) {
      case LANDSCAPE_BY_BUSINESSUNITS:
        return new LandscapeDiagramHelperInformationSystemIsr(isr, TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.BUSINESSUNIT);

      case LANDSCAPE_BY_PRODUCTS:
        return new LandscapeDiagramHelperInformationSystemIsr(isr, TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.PRODUCT);

      case LANDSCAPE_BY_BUSINESSFUNCTIONS:
        return new LandscapeDiagramHelperInformationSystemIsr(isr, TypeOfBuildingBlock.BUSINESSPROCESS, TypeOfBuildingBlock.BUSINESSFUNCTION);

      case LANDSCAPE_BY_PROJECTS:
        return new LandscapeDiagramHelperInformationSystemIsr(isr, TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.PROJECT);

      case LANDSCAPE_TECHNICAL:
        return new LandscapeDiagramHelperInformationSystemTcr(isr);

      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

  /**
   * Helper method to determine subclass of MasterplanDiagramHelper for building block
   *
   * @param element
   *          The building block
   * @return See method description
   */
  public MasterplanDiagramHelper getMasterplanHelper(BuildingBlock element) {
    TypeOfBuildingBlock tob = element.getBuildingBlockType().getTypeOfBuildingBlock();

    switch (tob) {
      case INFORMATIONSYSTEMRELEASE:
        return new MasterplanDiagramHelperInformationSystem();
        // never falls through
      case PROJECT:
        return new MasterplanDiagramHelperProject();
        // never falls through
      case TECHNICALCOMPONENTRELEASE:
        return new MasterplanDiagramHelperTechnicalComponent();
        // never falls through
      default:
        throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_GENERATION_FAILED);
    }
  }

}
