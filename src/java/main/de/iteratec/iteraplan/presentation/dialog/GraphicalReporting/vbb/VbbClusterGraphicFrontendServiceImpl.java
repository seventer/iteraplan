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
package de.iteratec.iteraplan.presentation.dialog.GraphicalReporting.vbb;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.MixedColorCodingDecorator;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.MixedColorCodingDecorator.ContinuousColorCodingDecorator;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.MixedColorCodingDecorator.DiscreteColorCodingDecorator;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.RecursiveCluster;
import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalOptionsGetter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.vbb.VbbOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QFirstLevel;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.QPart;
import de.iteratec.iteraplan.businesslogic.reports.query.type.QueryTypeHelper;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.ElasticeamService;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.common.util.NamedId;
import de.iteratec.iteraplan.elasticeam.iteraql2.compile.UniversalTypeCompilationUnit;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.presentation.PresentationHelper;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator;
import de.iteratec.iteraplan.presentation.responsegenerators.GraphicsResponseGenerator.GraphicalReport;


@Service("vbbClusterGraphicFrontendService")
public class VbbClusterGraphicFrontendServiceImpl extends VbbGraphicFrontendService {

  @Autowired
  private QueryService      queryService;

  @Autowired
  private ElasticeamService elasticService;

  @Override
  protected String getFlowId() {
    return Dialog.GRAPHICAL_REPORTING_VBBCLUSTER.getFlowId();
  }

  @Override
  protected ReportType getReportType() {
    return ReportType.VBBCLUSTER;
  }

  /**{@inheritDoc}**/
  @Override
  protected GraphicalReport getGraphicalReport() {
    return GraphicsResponseGenerator.GraphicalReport.VBBCLUSTER;
  }

  /**{@inheritDoc}**/
  @Override
  protected String getVbbDiagramName() {
    return "RecursiveCluster";
  }

  public ManageReportMemoryBean filterOuterResults(ManageReportMemoryBean vbbClusterMemBean, RequestContext context, FlowExecutionContext flowContext) {
    vbbClusterMemBean.setQueryResultName(RecursiveCluster.CLASS_OUTER);
    return filterElements(vbbClusterMemBean);
  }

  public ManageReportMemoryBean filterInnerResults(ManageReportMemoryBean vbbClusterMemBean, RequestContext context, FlowExecutionContext flowContext) {
    vbbClusterMemBean.setQueryResultName(RecursiveCluster.CLASS_INNER);
    return filterElements(vbbClusterMemBean);
  }

  public ManageReportMemoryBean switchTypes(ManageReportMemoryBean vbbClusterMemBean, RequestContext context, FlowExecutionContext flowContext) {
    QueryResult outer = vbbClusterMemBean.getQueryResult(RecursiveCluster.CLASS_OUTER);
    QueryResult inner = vbbClusterMemBean.getQueryResult(RecursiveCluster.CLASS_INNER);
    outer.setQueryName(RecursiveCluster.CLASS_INNER);
    inner.setQueryName(RecursiveCluster.CLASS_OUTER);
    vbbClusterMemBean.setQueryResult(inner);
    vbbClusterMemBean.setQueryResult(outer);

    Map<String, String> vpConfigMap = GraphicalOptionsGetter.getVbbOptions(vbbClusterMemBean).getViewpointConfigMap();
    String outer2outer = vpConfigMap.get(RecursiveCluster.CLASS_OUTER + "." + RecursiveCluster.REFERENCE_OUTER2OUTER);
    vpConfigMap.put(RecursiveCluster.CLASS_OUTER + "." + RecursiveCluster.REFERENCE_OUTER2OUTER,
        vpConfigMap.get(RecursiveCluster.CLASS_INNER + "." + RecursiveCluster.REFERENCE_INNER2INNER));
    vpConfigMap.put(RecursiveCluster.CLASS_INNER + "." + RecursiveCluster.REFERENCE_INNER2INNER, outer2outer);

    vpConfigMap.put(RecursiveCluster.DISPLAY_ALL_INNER, Boolean.FALSE.toString());

    swapValues(vpConfigMap, "filterHint");

    swapValues(vpConfigMap, RecursiveCluster.VV_FILL_COLOR);
    swapValues(vpConfigMap, MixedColorCodingDecorator.VV_COLORING_OPTIONAL);
    swapValues(vpConfigMap, MixedColorCodingDecorator.VV_DECORATION_MODE);
    swapValues(vpConfigMap, DiscreteColorCodingDecorator.VV_COLOR_MAPPING);
    swapValues(vpConfigMap, ContinuousColorCodingDecorator.VV_MIN_COLOR);
    swapValues(vpConfigMap, ContinuousColorCodingDecorator.VV_MAX_COLOR);
    swapValues(vpConfigMap, ContinuousColorCodingDecorator.VV_MIN_VALUE);
    swapValues(vpConfigMap, ContinuousColorCodingDecorator.VV_MAX_VALUE);
    swapValues(vpConfigMap, ContinuousColorCodingDecorator.VV_UNDEFINED_COLOR);
    swapValues(vpConfigMap, ContinuousColorCodingDecorator.VV_OUT_OF_BOUNDS_COLOR);

    return vbbClusterMemBean;
  }

  private static void swapValues(Map<String, String> map, String visualVariableKeySuffix) {
    String outerKey = RecursiveCluster.CLASS_OUTER + "." + visualVariableKeySuffix;
    String innerKey = RecursiveCluster.CLASS_INNER + "." + visualVariableKeySuffix;
    String outerVal = map.get(outerKey);
    map.put(outerKey, map.get(innerKey));
    map.put(innerKey, outerVal);
  }

  public ManageReportMemoryBean resetOuter(ManageReportMemoryBean vbbClusterMemBean, RequestContext context, FlowExecutionContext flowContext) {
    vbbClusterMemBean.setQueryResultName(RecursiveCluster.CLASS_OUTER);
    GraphicalOptionsGetter.getVbbOptions(vbbClusterMemBean).getViewpointConfigMap()
        .put(RecursiveCluster.CLASS_OUTER + "." + RecursiveCluster.VV_FILL_COLOR, RecursiveCluster.DEFAULT_OUTER_FILL_COLOR);
    resetColorOptions(vbbClusterMemBean, RecursiveCluster.CLASS_OUTER + ".");
    return resetFilter(vbbClusterMemBean);
  }

  public ManageReportMemoryBean resetInner(ManageReportMemoryBean vbbClusterMemBean, RequestContext context, FlowExecutionContext flowContext) {
    vbbClusterMemBean.setQueryResultName(RecursiveCluster.CLASS_INNER);
    Map<String, String> vpConfigMap = GraphicalOptionsGetter.getVbbOptions(vbbClusterMemBean).getViewpointConfigMap();
    vpConfigMap.put(RecursiveCluster.CLASS_INNER + "." + RecursiveCluster.VV_FILL_COLOR, RecursiveCluster.DEFAULT_INNER_FILL_COLOR);
    vpConfigMap.put(RecursiveCluster.DISPLAY_ALL_INNER, Boolean.FALSE.toString());
    resetColorOptions(vbbClusterMemBean, RecursiveCluster.CLASS_INNER + ".");
    return resetFilter(vbbClusterMemBean);
  }

  private void resetColorOptions(ManageReportMemoryBean memBean, String keyPrefix) {
    VbbOptionsBean options = (VbbOptionsBean) memBean.getGraphicalOptions();

    options.getViewpointConfigMap().put(keyPrefix + MixedColorCodingDecorator.VV_COLORING_OPTIONAL, "");
    options.getViewpointConfigMap().put(keyPrefix + MixedColorCodingDecorator.VV_DECORATION_MODE, "");
    options.getViewpointConfigMap().put(keyPrefix + DiscreteColorCodingDecorator.VV_COLOR_MAPPING, "");
    options.getViewpointConfigMap().put(keyPrefix + ContinuousColorCodingDecorator.VV_MIN_COLOR, ContinuousColorCodingDecorator.DEFAULT_MIN_COLOR);
    options.getViewpointConfigMap().put(keyPrefix + ContinuousColorCodingDecorator.VV_MAX_COLOR, ContinuousColorCodingDecorator.DEFAULT_MAX_COLOR);
    options.getViewpointConfigMap().put(keyPrefix + ContinuousColorCodingDecorator.VV_MIN_VALUE, "");
    options.getViewpointConfigMap().put(keyPrefix + ContinuousColorCodingDecorator.VV_MAX_VALUE, "");
    options.getViewpointConfigMap().put(keyPrefix + ContinuousColorCodingDecorator.VV_UNDEFINED_COLOR,
        "#" + Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);
    options.getViewpointConfigMap().put(keyPrefix + ContinuousColorCodingDecorator.VV_OUT_OF_BOUNDS_COLOR,
        ContinuousColorCodingDecorator.DEFAULT_OUT_OF_BOUNDS_COLOR);
  }

  private ManageReportMemoryBean resetFilter(ManageReportMemoryBean memBean) {
    //only reset if filters are initialized
    if (memBean.getQueryResult().getQueryForms().size() > 0) {
      memBean.getQueryResult().getQueryForms().clear();
      initializeQueryResult(memBean);
    }
    String filterHint = MessageAccess.getString("reports.selectAll") + " " + MessageAccess.getString("graphicalExport.pieBar.bar.assignedElements");
    Map<String, String> vpConfigMap = GraphicalOptionsGetter.getVbbOptions(memBean).getViewpointConfigMap();
    vpConfigMap.put(memBean.getQueryResult().getQueryName() + ".filterHint", filterHint);
    return memBean;
  }

  private ManageReportMemoryBean filterElements(ManageReportMemoryBean memBean) {
    QueryResult queryResult = memBean.getQueryResult();
    Type<?> type = QueryTypeHelper.getTypeObject(TypeOfBuildingBlock.fromInitialCapString(GraphicalOptionsGetter.getVbbOptions(memBean)
        .getViewpointConfigMap().get(queryResult.getQueryName())));
    if (queryResult.getQueryForms().isEmpty() || !queryResult.getResultType().getTypeOfBuildingBlock().equals(type.getTypeOfBuildingBlock())) {
      queryResult.getQueryForms().clear();
      queryResult.getQueryForms().add(getInitFormHelperService().getReportForm(type));
      getInitFormHelperService().dropRestrictionsFromQueryForm(queryResult.getQueryForms().get(0));
      queryResult.setTimeseriesQuery(getInitFormHelperService().getTimeseriesQuery(type));
      queryResult.resetAvailableReportExtensions();
      queryService.requestEntityList(memBean, getReportType());
    }

    QueryResult dimensionQuery = memBean.getQueryResult();
    if (dimensionQuery == null || dimensionQuery.getQueryForms().isEmpty()) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INTERNAL_ERROR);
    }
    ManageReportMemoryBean result = new ManageReportMemoryBean();
    result.setReportType(getReportType());

    result.setQueryResult(dimensionQuery);

    for (DynamicQueryFormData<?> queryForm : dimensionQuery.getQueryForms()) {
      Map<String, List<NamedId>> availableAttributeValues = Maps.newHashMap();
      for (QFirstLevel qfl : queryForm.getQueryUserInput().getQueryFirstLevels()) {
        for (QPart qp : qfl.getQuerySecondLevels()) {
          String attrId = qp.getChosenAttributeStringId();
          BBAttribute attribute = queryForm.getBBAttributeByStringId(attrId);
          if (attribute == null) {
            continue;
          }

          if (!availableAttributeValues.containsKey(attribute.getStringId())) {
            List<String> newAttrValList = getQueryService().getAttributeValuesForAttribute(queryForm.getType(), attribute);
            List<NamedId> newAttrValIdList = PresentationHelper.convertStringsToNamedIds(newAttrValList);
            availableAttributeValues.put(attribute.getStringId(), newAttrValIdList);
            if (newAttrValIdList.isEmpty() || (qp.getFreeTextCriteria() != null && !qp.getFreeTextCriteria().isEmpty())) {
              qp.setFreeTextCriteriaSelected(Boolean.TRUE);
            }
            else {
              qp.setFreeTextCriteriaSelected(Boolean.FALSE);
            }
          }
        }
      }
      queryForm.setAvailableAttributeValues(availableAttributeValues);
    }

    getInitFormHelperService().switchQuery(result, dimensionQuery.getQueryName());

    return result;
  }

  public ManageReportMemoryBean resumeFromFilter(ManageReportMemoryBean reportMemBean, ManageReportMemoryBean memBean, RequestContext context,
                                                 FlowExecutionContext flowContext) {
    memBean.setQueryResult(reportMemBean.getQueryResult());
    initializeQueryResults(memBean);
    return memBean;
  }

  public ManageReportMemoryBean resetReport(ManageReportMemoryBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    memBean.resetResults();
    return memBean;
  }

  /**{@inheritDoc}**/
  @Override
  protected void refreshGraphicalOptions(ManageReportMemoryBean memBean) {
    //not relevant for the NCG, since it has no conventional color or line option beans
  }

  /**{@inheritDoc}**/
  @Override
  public void generateGraphicFileResponse(ManageReportMemoryBean memBean, HttpServletRequest request, HttpServletResponse response) {
    initializeQueryResults(memBean);
    super.generateGraphicFileResponse(memBean, request, response);
  }

  private void initializeQueryResults(ManageReportMemoryBean memBean) {
    String queryResultName = memBean.getQueryResultName();
    for (QueryResult qr : memBean.getQueryResults().values()) {
      memBean.setQueryResultName(qr.getQueryName());
      initializeQueryResult(memBean);
    }
    memBean.setQueryResultName(queryResultName);
  }

  private void initializeQueryResult(ManageReportMemoryBean memBean) {
    QueryResult qr = memBean.getQueryResult();
    Map<String, String> vpConfigMap = GraphicalOptionsGetter.getVbbOptions(memBean).getViewpointConfigMap();
    String filterHint = MessageAccess.getString("reports.selectAll") + " " + MessageAccess.getString("graphicalExport.pieBar.bar.assignedElements");
    String typeName = vpConfigMap.get(qr.getQueryName());
    if (typeName.contains(".objectify(")) {
      typeName = typeName.substring(0, typeName.indexOf(".objectify("));
    }
    if (StringUtils.isNotBlank(typeName)) {
      TypeOfBuildingBlock tobb = TypeOfBuildingBlock.fromInitialCapString(typeName);
      Type<?> type = QueryTypeHelper.getTypeObject(tobb);
      if (qr.getQueryForms().isEmpty() || !qr.getResultType().getTypeOfBuildingBlock().equals(type.getTypeOfBuildingBlock())) {
        qr.getQueryForms().clear();
        qr.getQueryForms().add(getInitFormHelperService().getReportForm(type));
        getInitFormHelperService().dropRestrictionsFromQueryForm(qr.getQueryForms().get(0));
        qr.setTimeseriesQuery(getInitFormHelperService().getTimeseriesQuery(type));
        qr.resetAvailableReportExtensions();
        memBean.setQueryResultName(qr.getQueryName());
        queryService.requestEntityList(memBean, getReportType());
      }
      int allElementsCount = elasticService.getModel()
          .findAll(((UniversalTypeCompilationUnit) elasticService.compile(typeName + ";")).getCompilationResult()).size();
      if (qr.getSelectedResultIds().length < allElementsCount) {
        filterHint = qr.getSelectedResultIds().length + "/" + allElementsCount + " "
            + MessageAccess.getString("graphicalExport.pieBar.bar.assignedElements");
      }
    }
    vpConfigMap.put(qr.getQueryName() + ".filterHint", filterHint);
  }

  /**{@inheritDoc}**/
  @Override
  public ManageReportMemoryBean saveQuery(ManageReportMemoryBean memBean, RequestContext requestContext) {
    initializeQueryResults(memBean);
    return super.saveQuery(memBean, requestContext);
  }

  /**{@inheritDoc}**/
  @Override
  public ManageReportMemoryBean loadSavedQuery(ManageReportMemoryBean memBean) {
    for (QueryResult qr : memBean.getQueryResults().values()) {
      qr.getQueryForms().clear();
    }

    ManageReportMemoryBean result = super.loadSavedQuery(memBean);
    initializeQueryResults(result);

    return result;
  }
}
