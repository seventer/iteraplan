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
package de.iteratec.iteraplan.presentation.dialog.ObjectRelatedPermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.InstancePermissionService;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.BigInstancePermissionsDTO;
import de.iteratec.iteraplan.model.queries.ReportType;
import de.iteratec.iteraplan.presentation.dialog.ReportBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


/**
 * This Service Implementation offers actions used by Spring Webflow to create, edit and delete
 * Roles.
 */
@Service("objectRelatedPermissionFrontendService")
public class ObjectRelatedPermissionFrontendServiceImpl extends ReportBaseFrontendServiceImpl<ObjectRelatedPermissionMemBean> implements
    ObjectRelatedPermissionFrontendService {

  @Autowired
  private InstancePermissionService service;

  public void setService(InstancePermissionService service) {
    this.service = service;
  }

  public ObjectRelatedPermissionMemBean getMemBean(Integer currentId) {
    BigInstancePermissionsDTO dto = service.getInitialInstancePermissionDTORead(currentId);
    ObjectRelatedPermissionMemBean memBean = new ObjectRelatedPermissionMemBean(dto);
    memBean.setComponentMode(ComponentMode.READ);
    memBean.getTabularOptions().setResultFormat(Constants.REPORTS_EXPORT_HTML);
    return memBean;
  }

  public ObjectRelatedPermissionMemBean getEditMemBean(ObjectRelatedPermissionMemBean memBean, Integer entityId, RequestContext context,
                                                       FlowExecutionContext flowContext) {
    DynamicQueryFormData<?> reportForm = getInitFormHelperService().getReportForm(InformationSystemReleaseTypeQu.getInstance());

    BigInstancePermissionsDTO dto = service.getInitialInstancePermissionDTOWrite(memBean.getSelectedUserEntityId());
    ObjectRelatedPermissionMemBean editMemBean = new ObjectRelatedPermissionMemBean(dto, reportForm, getInitFormHelperService().getTimeseriesQuery(
        InformationSystemReleaseTypeQu.getInstance()));
    TypeOfBuildingBlock typeOfBB = reportForm.getType().getTypeOfBuildingBlock();

    getInitFormHelperService().setViewConfiguration(editMemBean, reportForm.getType().getTypeOfBuildingBlock());
    editMemBean.setComponentMode(ComponentMode.EDIT);
    editMemBean.setSelectedBuildingBlock(typeOfBB.getValue());
    editMemBean.getTabularOptions().setResultFormat(Constants.REPORTS_EXPORT_HTML);

    enterEditMode(flowContext);
    return editMemBean;
  }

  public ObjectRelatedPermissionMemBean addPermissions(ObjectRelatedPermissionMemBean memBean, RequestContext context,
                                                       FlowExecutionContext flowContext) {
    Integer[] selectedIdentifiers = memBean.getQueryResult().getSelectedResultIds();
    service.addBuildingBlocks(selectedIdentifiers, memBean.getDto());

    memBean.resetResults();

    return memBean;
  }

  public boolean removeBuildingBlockPermission(ObjectRelatedPermissionMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    service.removeBuildingBlock(memBean.getSelectedBuildingBlockIdToRemove(), memBean.getDto());
    return true;
  }

  public boolean saveBuildingBlockPermission(ObjectRelatedPermissionMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {

    // TODO validate?

    service.saveInstancePermissions(memBean.getDto());
    leaveEditMode(flowContext);
    return true;
  }

  /**
   * TODO can the getInitialInstancePermissionDTOWrite be omitted, and the memBean provided be
   * used? Then refactor to superclass!
   */
  @Override
  protected ObjectRelatedPermissionMemBean getMemBeanForChangedQueryType(ObjectRelatedPermissionMemBean memBean) {
    BigInstancePermissionsDTO dto = service.getInitialInstancePermissionDTOWrite(memBean.getSelectedUserEntityId());
    Type<?> selectedType = Type.getTypeByTypeNameDB(memBean.getSelectedQueryTypeNameDB());
    DynamicQueryFormData<?> reportForm = getInitFormHelperService().getReportForm(selectedType);
    dto.setAssociatedBuildingBlocks(memBean.getDto().getAssociatedBuildingBlocks());

    ObjectRelatedPermissionMemBean changedMemBean = new ObjectRelatedPermissionMemBean(dto, reportForm, getInitFormHelperService()
        .getTimeseriesQuery(selectedType));

    getInitFormHelperService().setViewConfiguration(changedMemBean, reportForm.getType().getTypeOfBuildingBlock());
    TypeOfBuildingBlock typeOfBB = TypeOfBuildingBlock.fromPropertyString(selectedType.getTypeNamePresentationKey());

    changedMemBean.setSelectedBuildingBlock(typeOfBB.getValue());
    changedMemBean.getTabularOptions().setResultFormat(Constants.REPORTS_EXPORT_HTML);
    changedMemBean.setComponentMode(ComponentMode.EDIT);
    return changedMemBean;
  }

  @Override
  protected String getFlowId() {
    return Dialog.OBJECT_RELATED_PERMISSION.getFlowId();
  }

  @Override
  protected ReportType getReportType() {
    // do nothing (inherited method)
    return null;
  }

}
