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
package de.iteratec.iteraplan.presentation.dialog.timeseries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.TimeseriesService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.Timeseries;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;
import de.iteratec.iteraplan.presentation.dialog.BuildingBlockFrontendService;
import de.iteratec.iteraplan.presentation.dialog.CMBaseFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.common.CanHaveTimeseriesBaseMemBean;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.BuildingBlockComponentModel;
import de.iteratec.iteraplan.presentation.dialog.common.model.timeseries.TimeseriesAttributeComponentModel;


/**
 * Base class for common functionality of all frontendservices for building blocks which can have timeseries attributes.
 * For most building block types it is enough to implement the abstract methods and use the public methods here as they are.
 * In some special cases those methods may have to be overriden which is why they are not final.
 *
 * @param <B> The building block class the frontend service implementation is supposed to handle
 * @param <M> The memory bean class for this building block
 */
public abstract class TimeseriesCMBaseFrontendService<B extends BuildingBlock, M extends CanHaveTimeseriesBaseMemBean<B, ? extends BuildingBlockComponentModel<B>>>
    extends CMBaseFrontendServiceImpl<M, BuildingBlockService<B, Integer>> implements BuildingBlockFrontendService<M> {

  @Autowired
  private TimeseriesService               timeseriesService;
  @Autowired
  private TimeseriesUpdateFrontendService timeseriesUpdateService;

  /**
   * Only for tests
   */
  public void setTimeseriesService(TimeseriesService timeseriesService) {
    this.timeseriesService = timeseriesService;
  }

  /**
   * Only for tests
   */
  public void setTimeseriesUpdateService(TimeseriesUpdateFrontendService timeseriesUpdateService) {
    this.timeseriesUpdateService = timeseriesUpdateService;
  }

  public boolean updateComponentModel(M memBean, RequestContext context, FlowExecutionContext flowContext) {
    timeseriesUpdateService.updateTimeseriesComponentModels(memBean);
    super.updateComponentModel(memBean, context, flowContext);
    return true;
  }

  public M getCreateMemBean(FlowExecutionContext flowContext) {
    M memBean = createNewMemBean();

    B buildingBlock = createNewModelObject();

    BuildingBlockComponentModel<B> cm = createComponentModel(memBean, ComponentMode.CREATE);
    cm.initializeFrom(buildingBlock);

    initializeTimeseriesComponentModels(buildingBlock, memBean, ComponentMode.CREATE);

    return memBean;
  }

  public M getCreateMemBeanWithDataFromId(Integer currentId) {
    M memBean = createNewMemBean();

    B buildingBlock = getModelObjectById(currentId);
    prepareBuildingBlockForCopy(buildingBlock);

    BuildingBlockComponentModel<B> cm = createComponentModel(memBean, ComponentMode.CREATE);
    cm.initializeFrom(buildingBlock);

    initializeTimeseriesComponentModels(buildingBlock, memBean, ComponentMode.CREATE);
    return memBean;
  }

  public M getEditMemBean(M memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    B buildingBlock = getModelObjectById(entityId);

    BuildingBlockComponentModel<B> cm = createComponentModel(memBean, ComponentMode.EDIT);
    cm.setEntity(buildingBlock);
    cm.initializeFrom(buildingBlock);

    initializeTimeseriesComponentModels(buildingBlock, memBean, ComponentMode.EDIT);

    enterEditMode(flowContext);

    return memBean;
  }

  public M getMemBean(Integer currentId) {
    M memBean = createNewMemBean();

    B buildingBlock = getModelObjectById(currentId);

    BuildingBlockComponentModel<B> cm = createComponentModel(memBean, ComponentMode.READ);
    cm.initializeFrom(buildingBlock);

    initializeTimeseriesComponentModels(buildingBlock, memBean, ComponentMode.READ);
    return memBean;
  }

  public boolean saveComponentModel(M memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    BuildingBlockComponentModel<B> cm = memBean.getComponentModel();
    B buildingBlock = getService().merge(cm.getEntity());

    cm.update();
    cm.configure(buildingBlock);
    validateOnSave(buildingBlock, getService());

    getService().saveOrUpdate(buildingBlock);

    saveTimeseriesComponentModels(buildingBlock, memBean);

    leaveEditMode(flowContext);

    return true;
  }

  public Integer saveNewComponentModel(M memBean, RequestContext context, FlowExecutionContext flowContext) {
    BuildingBlockComponentModel<B> cm = memBean.getComponentModel();
    cm.update();

    B newBuildingBlock = createNewModelObject();

    cm.configure(newBuildingBlock);
    validateOnSave(newBuildingBlock, getService());

    newBuildingBlock = getService().saveOrUpdate(newBuildingBlock);

    saveTimeseriesComponentModels(newBuildingBlock, memBean);

    leaveEditMode(flowContext);

    cm.setEntity(newBuildingBlock);

    return newBuildingBlock.getId();
  }

  public boolean deleteEntity(Integer currentId, RequestContext context, FlowExecutionContext flowContext) {
    B currentBB = getModelObjectById(currentId);
    getService().deleteEntity(currentBB);
    return true;
  }

  protected void initializeTimeseriesComponentModels(B bb, M memBean, ComponentMode proposedComponentMode) {
    memBean.getTimeseriesComponentModels().clear();
    for (AttributeType at : bb.getBuildingBlockType().getAttributeTypes()) {
      if (isToBeIncluded(at)) {
        Timeseries timeseries = timeseriesService.loadTimeseriesByBuildingBlockAndAttributeType(bb, at);
        if (timeseries == null) {
          timeseries = new Timeseries();
          timeseries.setAttribute(at);
          timeseries.setBuildingBlock(bb);
        }

        ComponentMode checkedComponentMode = checkForWritePermissions(at, proposedComponentMode);
        TimeseriesAttributeComponentModel cm = new TimeseriesAttributeComponentModel(checkedComponentMode, "timeseriesAttr_" + at.getId());
        cm.initializeFrom(timeseries);
        memBean.getTimeseriesComponentModels().put(at.getId(), cm);
      }
    }
  }

  private boolean isToBeIncluded(AttributeType at) {
    Permissions perms = UserContext.getCurrentUserContext().getPerms();
    boolean hasAtgReadPermission = perms.userHasAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ);
    boolean isTimeseries = at instanceof TimeseriesType && ((TimeseriesType) at).isTimeseries();

    return hasAtgReadPermission && isTimeseries;
  }

  private ComponentMode checkForWritePermissions(AttributeType at, ComponentMode proposedComponentMode) {
    if (ComponentMode.READ != proposedComponentMode) {
      // check for write permissions unless you only want to read anyway
      Permissions perms = UserContext.getCurrentUserContext().getPerms();
      if (perms.userHasAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ_WRITE)) {
        return proposedComponentMode;
      }
    }

    return ComponentMode.READ;
  }

  protected void saveTimeseriesComponentModels(B bb, M memBean) {
    for (AttributeType at : bb.getBuildingBlockType().getAttributeTypes()) {
      if (memBean.getTimeseriesComponentModels().containsKey(at.getId())) {
        Timeseries timeseries = timeseriesService.loadTimeseriesByBuildingBlockAndAttributeType(bb, at);
        if (timeseries == null) {
          timeseries = new Timeseries();
          timeseries.setAttribute(at);
          timeseries.setBuildingBlock(bb);
        }
        memBean.getTimeseriesComponentModels().get(at.getId()).configure(timeseries);

        timeseriesService.updateBuildingBlockAttribute(timeseries, bb);

        if (!timeseries.getEntries().isEmpty()) {
          timeseries.setBuildingBlock(bb); // set the current, up-to-date building block into the timeseries.
          timeseriesService.saveOrUpdateWithoutBbUpdate(timeseries);
        }
        else if (timeseries.getId() != null) {
          timeseriesService.deleteTimeseries(timeseries);
        }
      }
    }
  }

  /**
   * Loads and returns the model object with the given ID.
   * @param id
   */
  protected B getModelObjectById(Integer id) {
    B modelObject = getService().loadObjectById(id);
    if (modelObject == null) {
      throw new IteraplanTechnicalException(IteraplanErrorMessages.ENTITY_NOT_FOUND_READ);
    }

    return modelObject;
  }

  /**
   * @return A freshly created building block of the appropriate type.
   */
  protected abstract B createNewModelObject();

  /**
   * @return A freshly created memory bean of the appropriate type
   */
  protected abstract M createNewMemBean();

  /**
   * Creates a component model and adds it to the given memory bean.
   * @param componentMode
   * @return the newly created component model of the appropriate type with the given component mode.
   */
  protected abstract BuildingBlockComponentModel<B> createComponentModel(M memBean, ComponentMode componentMode);

  /**
   * Called in {@link #getCreateMemBeanWithDataFromId(Integer)} to modify the building block
   * before initializing the copy component model with it.
   * These modifications will not be persisted.
   * @param buildingBlock
   */
  protected abstract void prepareBuildingBlockForCopy(B buildingBlock);

}
