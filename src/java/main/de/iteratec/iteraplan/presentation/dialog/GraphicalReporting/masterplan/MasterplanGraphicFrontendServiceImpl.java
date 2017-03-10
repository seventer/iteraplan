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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.masterplan;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ColumnEntry;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.MasterplanRowTypeOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Masterplan.TimelineFeature;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.service.FastExportService;
import de.iteratec.iteraplan.businesslogic.service.FastExportService.DiagramVariant;
import de.iteratec.iteraplan.businesslogic.service.GeneralBuildingBlockService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.IteraplanProperties;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.model.sorting.BuildingBlockComparator;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicExportBean;
import de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.GraphicalReportBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;


@Service("masterplanGraphicFrontendService")
public class MasterplanGraphicFrontendServiceImpl extends GraphicalReportBaseFrontendServiceImpl<ManageReportMemoryBean> implements
    MasterplanGraphicFrontendService {

  @Autowired
  private FastExportService           fastExportService;

  @Autowired
  private GeneralBuildingBlockService generalBuildingBlockService;

  public MasterplanGraphicFrontendServiceImpl() {
    super();
  }

  public ManageReportMemoryBean getInitialMemBean() {
    ManageReportMemoryBean memBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL,
        Constants.ALL_TYPES_FOR_DISPLAY);

    MasterplanOptionsBean options = new MasterplanOptionsBean();
    memBean.setGraphicalOptions(options);

    memBean.setReportType(getReportType());
    memBean.setSavedQueries(getSavedQueryService().getSavedQueriesWithoutContent(getReportType()));
    memBean.resetPostProcessingStrategies();

    MasterplanRowTypeOptions row0 = getInitFormHelperService().createMasterplanRowType(null, "", memBean.getSelectedBuildingBlock(), 0);
    options.setLevel0Options(row0);

    return memBean;
  }

  @Override
  protected String getFlowId() {
    return Dialog.GRAPHICAL_REPORTING_MASTERPLAN.getFlowId();
  }

  public ManageReportMemoryBean fromInterchange(String bbType, String idList) {
    if (idList == null || bbType == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    List<Integer> idsAsList = parseSelectedIds(idList);

    return fromInterchange(TypeOfBuildingBlock.getTypeOfBuildingBlockByString(bbType), idsAsList.toArray(new Integer[idsAsList.size()]));
  }

  private ManageReportMemoryBean fromInterchange(TypeOfBuildingBlock tobb, Integer[] ids) {
    ManageReportMemoryBean resultMemBean = getInitialMemBean();
    GraphicalOptionsGetter.getMasterplanOptions(resultMemBean).setSelectedBbType(tobb.getPluralValue());
    resultMemBean = getMemBeanForChangedQueryType(resultMemBean);
    getInitFormHelperService().dropRestrictionsFromQueryForm(resultMemBean.getQueryResult().getQueryForms().get(0));

    List<BuildingBlock> allEntities = generalBuildingBlockService.getBuildingBlocksByType(tobb);
    removeVirtualElement(allEntities);
    Collections.sort(allEntities, new BuildingBlockComparator());

    List<DynamicQueryFormData<?>> queryForms = resultMemBean.getQueryResult().getQueryForms();
    resultMemBean.setQueryResult(new QueryResult(ManageReportBeanBase.MAIN_QUERY, queryForms, resultMemBean.getQueryResult().getTimeseriesQuery(),
        allEntities, ids, getReportType().getValue()));
    refreshReport(resultMemBean, null, null, false);

    return stepOneToStepTwo(resultMemBean, null, null);
  }

  public ManageReportMemoryBean stepOneToStepTwo(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    if (getReportType().equals(memBean.getReportType()) && memBean.getGraphicalOptions() != null) {
      memBean.getGraphicalOptions().setDialogStep(2);
    }
    return memBean;
  }

  public ManageReportMemoryBean directlyToStepTwo(Integer id, String bbType, String diagramVariant) {
    if (id == null || bbType == null || diagramVariant == null) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NOT_NULL_EXCEPTION);
    }

    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.fromInitialCapString(bbType);

    if (DiagramVariant.MASTERPLAN_TECHNICAL_COMPONENTS.equals(DiagramVariant.fromName(diagramVariant))) {
      // Even when MasterPlanTech comes from an ISR, it should display TechCompRels
      tobb = TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE;
    }
    else if (DiagramVariant.MASTERPLAN_PROJECTS.equals(DiagramVariant.fromName(diagramVariant))) {
      tobb = TypeOfBuildingBlock.PROJECT;
    }

    BuildingBlock startElement = fastExportService.getStartElement(id, bbType);
    List<BuildingBlock> listReleases = fastExportService.retrieveBuildingBlockListForMasterplanFastExport(startElement, null, diagramVariant);

    Integer[] selectedResultIds = GeneralHelper.createIdArrayFromIdEntities(listReleases);

    return fromInterchange(tobb, selectedResultIds);
  }

  public ManageReportMemoryBean stepTwoToStepOne(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {

    if (getReportType().equals(memBean.getReportType()) && memBean.getGraphicalOptions() != null) {
      memBean.getGraphicalOptions().setDialogStep(1);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.GRAPHIC_UNKNOWN_RESULT_TYPE);
    }

    return memBean;
  }

  @Override
  protected ManageReportMemoryBean getMemBeanForChangedQueryType(ManageReportMemoryBean memBean) {
    ManageReportMemoryBean newMemBean = null;
    MasterplanOptionsBean masterplanOptions = new MasterplanOptionsBean();
    if (memBean.getGraphicalOptions() instanceof MasterplanOptionsBean) {
      masterplanOptions = (MasterplanOptionsBean) memBean.getGraphicalOptions();
    }

    String selectedType = memBean.getGraphicalOptions().getSelectedBbType();

    List<String> allowedMasterplanTypes = Constants.ALL_TYPES_FOR_DISPLAY;

    if (allowedMasterplanTypes.contains(selectedType)) {
      newMemBean = getInitFormHelperService().getInitializedReportMemBeanByViewPerms(selectedType, Constants.ALL_TYPES_FOR_DISPLAY);
    }
    else {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }

    newMemBean.setSelectedBuildingBlock(selectedType);

    List<SavedQuery> savedQueries = getSavedQueryService().getSavedQueriesWithoutContent(getReportType());
    newMemBean.setSavedQueries(savedQueries);

    newMemBean.setGraphicalOptions(masterplanOptions);

    MasterplanRowTypeOptions row0 = getInitFormHelperService().createMasterplanRowType(null, "", selectedType, 0);
    masterplanOptions.setLevel0Options(row0);
    masterplanOptions.setLevel1Options(null);
    masterplanOptions.setSelectedLevel1Relation("");
    masterplanOptions.setLevel2Options(null);
    masterplanOptions.setSelectedLevel2Relation("");

    newMemBean.setReportType(getReportType());

    return newMemBean;
  }

  @Override
  protected void refreshGraphicalOptions(ManageReportMemoryBean memBean) {
    super.refreshGraphicalOptions(memBean);
    //masterplan-specific refresh
    TypeOfBuildingBlock tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(memBean.getSelectedBuildingBlock());

    if (memBean.getGraphicalOptions() instanceof MasterplanOptionsBean) {
      MasterplanOptionsBean masterplanOptions = (MasterplanOptionsBean) memBean.getGraphicalOptions();

      MasterplanRowTypeOptions row0 = masterplanOptions.getLevel0Options();
      if (row0 != null) {
        if (row0.isUseDefaultColoring()) {
          row0.getColorOptions().setDimensionAttributeId(Integer.valueOf(-1));
        }
        getInitFormHelperService().refreshGraphicalExportColorOptions(row0.getColorOptions(), tobb);
      }
      MasterplanRowTypeOptions row1 = masterplanOptions.getLevel1Options();
      if (row1 != null) {
        tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(row1.getSelectedBbType());
        if (row1.isUseDefaultColoring()) {
          row1.getColorOptions().setDimensionAttributeId(Integer.valueOf(-1));
        }
        getInitFormHelperService().refreshGraphicalExportColorOptions(row1.getColorOptions(), tobb);
      }
      MasterplanRowTypeOptions row2 = masterplanOptions.getLevel2Options();
      if (row2 != null) {
        tobb = TypeOfBuildingBlock.getTypeOfBuildingBlockByString(row2.getSelectedBbType());
        if (row2.isUseDefaultColoring()) {
          row2.getColorOptions().setDimensionAttributeId(Integer.valueOf(-1));
        }
        getInitFormHelperService().refreshGraphicalExportColorOptions(row2.getColorOptions(), tobb);
      }
    }
  }

  @Override
  public void generateGraphicFileResponse(ManageReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response) {

    MasterplanOptionsBean options = GraphicalOptionsGetter.getMasterplanOptions(memBean);

    GraphicExportBean vBean = null;

    // Retrieve the application url
    String srvAddr = URLBuilder.getApplicationURL(request);
    options.setServerUrl(srvAddr);

    // Generate response according to the chosen graphic type
    List<? extends BuildingBlock> selectedResults = memBean.getQueryResult().getSelectedResults();
    if (options.getSelectedGraphicFormat().equalsIgnoreCase(Constants.REPORTS_EXPORT_GRAPHICAL_VISIO)) {

      de.iteratec.visio.model.Document doc = getExportService().generateVisioMasterplanExport(selectedResults, options);
      vBean = new GraphicExportBean(doc);
    }
    else {
      de.iteratec.svg.model.Document doc = getExportService().generateSvgMasterplanExport(selectedResults, options);
      vBean = new GraphicExportBean(doc);
    }

    if (vBean != null) {
      GraphicsResponseGenerator respGen = getExportService().getResponseGenerator(options.getSelectedGraphicFormat());
      // have the report file written to the HttpServletResponse
      respGen.generateResponse(response, vBean, GraphicsResponseGenerator.GraphicalReport.MASTERPLAN, memBean.getContent());
    }
  }

  public ManageReportMemoryBean selectLevelType(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext, int rowIndex) {
    MasterplanOptionsBean masterplanOptions = GraphicalOptionsGetter.getMasterplanOptions(memBean);

    String selectedRelationship = rowIndex == 1 ? masterplanOptions.getSelectedLevel1Relation() : masterplanOptions.getSelectedLevel2Relation();

    if (selectedRelationship == null || selectedRelationship.isEmpty()
        || Constants.REPORTS_EXPORT_SELECT_RELATION.equalsIgnoreCase(selectedRelationship.trim().toLowerCase())) {
      if (rowIndex == 1) {
        masterplanOptions.setLevel1Options(null);
        masterplanOptions.setLevel2Options(null);
        masterplanOptions.setSelectedLevel1Relation("");
        masterplanOptions.setSelectedLevel2Relation("");
      }
      else if (rowIndex == 2) {
        masterplanOptions.setLevel2Options(null);
        masterplanOptions.setSelectedLevel2Relation("");
      }
    }
    else {
      String rowBbType = getInitFormHelperService().getRelatedTypeOfBuildingBlock(selectedRelationship);

      if (rowIndex == 1) {
        masterplanOptions.setLevel1Options(getInitFormHelperService().createMasterplanRowType(
            masterplanOptions.getLevel0Options().getTypeOfBuildingBlock(), selectedRelationship, rowBbType, rowIndex));
        masterplanOptions.setLevel2Options(null);
        masterplanOptions.setSelectedLevel2Relation("");
      }
      else if (rowIndex == 2) {
        masterplanOptions.setLevel2Options(getInitFormHelperService().createMasterplanRowType(
            masterplanOptions.getLevel1Options().getTypeOfBuildingBlock(), selectedRelationship, rowBbType, rowIndex));
      }
    }

    return memBean;
  }

  public ManageReportMemoryBean addCustomColumn(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext, int level) {
    MasterplanOptionsBean masterplanOptions = GraphicalOptionsGetter.getMasterplanOptions(memBean);

    MasterplanRowTypeOptions row = null;
    switch (level) {
      case 0:
        row = masterplanOptions.getLevel0Options();
        break;
      case 1:
        row = masterplanOptions.getLevel1Options();
        break;
      case 2:
        row = masterplanOptions.getLevel2Options();
        break;
      default:
        row = masterplanOptions.getLevel0Options();
        break;
    }

    String colName = row.getCurrentCustomColumn();
    ColumnEntry col = null;

    for (ColumnEntry entry : row.getAvailableCustomColumns()) {
      if (entry.getHead().equals(colName)) {
        col = entry;
        break;
      }
    }

    if (col != null) {
      row.getSelectedCustomColumns().add(col);
      row.getAvailableCustomColumns().remove(col);
      if (row.getSelectedCustomColumns().size() >= IteraplanProperties
          .getIntProperty(IteraplanProperties.EXPORT_GRAPHICAL_MASTERPLAN_MAXCUSTOMCOLUMNS)) {
        row.setAdditionalCustomColumnsAllowed(false);
      }
    }

    return memBean;
  }

  public ManageReportMemoryBean removeCustomColumn(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext, int level) {
    MasterplanOptionsBean masterplanOptions = GraphicalOptionsGetter.getMasterplanOptions(memBean);

    MasterplanRowTypeOptions row = null;
    switch (level) {
      case 0:
        row = masterplanOptions.getLevel0Options();
        break;
      case 1:
        row = masterplanOptions.getLevel1Options();
        break;
      case 2:
        row = masterplanOptions.getLevel2Options();
        break;
      default:
        row = masterplanOptions.getLevel0Options();
        break;
    }

    String colName = row.getColumnToRemove();
    ColumnEntry col = null;

    for (ColumnEntry entry : row.getSelectedCustomColumns()) {
      if (entry.getHead().equals(colName)) {
        col = entry;
        break;
      }
    }

    if (col != null) {
      row.getSelectedCustomColumns().remove(col);
      row.getAvailableCustomColumns().add(col);
      row.setAdditionalCustomColumnsAllowed(true);
    }

    return memBean;
  }

  public ManageReportMemoryBean addDateInterval(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext, int level) {
    MasterplanOptionsBean masterplanOptions = GraphicalOptionsGetter.getMasterplanOptions(memBean);

    MasterplanRowTypeOptions row = null;
    switch (level) {
      case 0:
        row = masterplanOptions.getLevel0Options();
        break;
      case 1:
        row = masterplanOptions.getLevel1Options();
        break;
      case 2:
        row = masterplanOptions.getLevel2Options();
        break;
      default:
        row = masterplanOptions.getLevel0Options();
        break;
    }
    Integer diId = row.getCurrentDateInterval();

    TimelineFeature tlf = null;
    for (TimelineFeature entry : row.getAvailableTimeLines()) {
      if (entry.getPosition() == diId.intValue()) {
        tlf = entry;
        break;
      }
    }

    if (tlf != null) {
      row.addTimeline(tlf);
    }

    return memBean;
  }

  public ManageReportMemoryBean removeDateInterval(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext, int level) {
    MasterplanOptionsBean masterplanOptions = GraphicalOptionsGetter.getMasterplanOptions(memBean);

    Integer position = masterplanOptions.getDateIntervalToRemove();

    MasterplanRowTypeOptions row = null;
    switch (level) {
      case 0:
        row = masterplanOptions.getLevel0Options();
        break;
      case 1:
        row = masterplanOptions.getLevel1Options();
        break;
      case 2:
        row = masterplanOptions.getLevel2Options();
        break;
      default:
        row = masterplanOptions.getLevel0Options();
        break;
    }

    row.removeTimeLineByPosition(position.intValue());

    return memBean;
  }

  @Override
  protected ReportType getReportType() {
    return ReportType.MASTERPLAN;
  }

}
