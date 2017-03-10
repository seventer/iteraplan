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
package de.iteratec.iteraplan.presentation.dialog.DateInterval;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.webflow.execution.FlowExecutionContext;
import org.springframework.webflow.execution.RequestContext;

import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.DateIntervalService;
import de.iteratec.iteraplan.common.Dialog;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateInterval;
import de.iteratec.iteraplan.presentation.dialog.CommonFrontendServiceImpl;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


@Service("dateIntervalFrontendService")
public class DateIntervalFrontendServiceImpl extends CommonFrontendServiceImpl implements DateIntervalFrontendService {

  @Autowired
  private DateIntervalService  dateIntervalService;

  @Autowired
  private AttributeTypeService attributeTypeService;

  /**{@inheritDoc}**/
  public DateIntervalMemBean getMemBean(Integer currentId) {
    if (dateIntervalService == null) {
      throw new NullPointerException();
    }
    DateIntervalComponentModel cm = new DateIntervalComponentModel(ComponentMode.READ);
    DateIntervalMemBean memBean = new DateIntervalMemBean();

    DateInterval di = getModelObjectById(currentId);

    cm.initializeFrom(di);
    memBean.setComponentModel(cm);

    memBean.getDates().clear();
    memBean.getDates().addAll(allAvailableDateATs());

    return memBean;
  }

  public DateIntervalMemBean getCreateMemBean(FlowExecutionContext flowContext) {
    checkPermission();

    // empty new date interval
    DateInterval dateInterval = new DateInterval();
    dateInterval.setDefaultColorHex("#ffffff");

    DateIntervalComponentModel cm = new DateIntervalComponentModel(ComponentMode.CREATE);

    cm.initializeFrom(dateInterval);

    DateIntervalMemBean memBean = new DateIntervalMemBean();

    memBean.getDates().clear();
    memBean.getDates().addAll(allAvailableDateATs());

    memBean.setComponentModel(cm);

    enterEditMode(flowContext);

    return memBean;
  }

  public DateIntervalMemBean getEditMemBean(DateIntervalMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    checkPermission();

    DateInterval dateInterval = getModelObjectById(entityId);
    DateIntervalComponentModel cm = new DateIntervalComponentModel(ComponentMode.EDIT);
    cm.setDateInterval(dateInterval);
    cm.initializeFrom(dateInterval);
    memBean.setComponentModel(cm);

    memBean.getDates().clear();
    memBean.getDates().addAll(allAvailableDateATsForDateInterval(dateInterval));

    enterEditMode(flowContext);

    return memBean;
  }

  public Integer saveNewComponentModel(DateIntervalMemBean memBean, RequestContext context, FlowExecutionContext flowContext) {
    checkPermission();

    DateIntervalComponentModel cm = memBean.getComponentModel();
    cm.update();

    DateAT start = ((DateAT) attributeTypeService.loadObjectById(Integer.valueOf(cm.getSelectedStartDate())));
    DateAT endat = ((DateAT) attributeTypeService.loadObjectById(Integer.valueOf(cm.getSelectedEndDate())));

    cm.getDateInterval().setStartDate(start);
    cm.getDateInterval().setEndDate(endat);
    validateDateIntervalsForDuplicity(null, start, endat);
    validateNewDIName(cm.getDateInterval().getName(), null);
    validateDatesCongruency(start, endat);
    //  cm.configure(newAT);

    DateInterval newDI = dateIntervalService.saveOrUpdate(cm.getDateInterval());

    leaveEditMode(flowContext);

    return newDI.getId();
  }

  private void validateDateIntervalsForDuplicity(Integer entityId, DateAT startDate, DateAT enddate) {
    Set<Integer> dateAtIds = new HashSet<Integer>();
    dateAtIds.add(startDate.getId());
    dateAtIds.add(enddate.getId());
    if (dateIntervalService.findDateIntervalsByDateATs(dateAtIds).size() > 0
        && (dateIntervalService.findDateIntervalsByDateATs(dateAtIds).size() == 1 && !dateIntervalService.findDateIntervalsByDateATs(dateAtIds)
            .contains(dateIntervalService.findDateIntervalById(entityId)))) {
        throw new IteraplanBusinessException(IteraplanErrorMessages.DATE_AT_ALREADY_IN_USE_FOR_DATE_INTERVAL);
    }
  }

  public boolean saveComponentModel(DateIntervalMemBean memBean, Integer entityId, RequestContext context, FlowExecutionContext flowContext) {
    checkPermission();

    DateIntervalComponentModel cm = memBean.getComponentModel();
    cm.update();

    DateAT start = ((DateAT) attributeTypeService.loadObjectById(Integer.valueOf(cm.getSelectedStartDate())));
    DateAT endat = ((DateAT) attributeTypeService.loadObjectById(Integer.valueOf(cm.getSelectedEndDate())));

    cm.getDateInterval().setStartDate(start);
    cm.getDateInterval().setEndDate(endat);

    validateDateIntervalsForDuplicity(entityId, start, endat);
    validateNewDIName(cm.getDateInterval().getName(), cm.getDateInterval().getId());
    validateDatesCongruency(start, endat);

    dateIntervalService.saveOrUpdate(cm.getDateInterval());

    leaveEditMode(flowContext);

    return true;
  }

  public boolean deleteEntity(Integer currentId, RequestContext context, FlowExecutionContext flowContext) {
    checkPermission();

    DateInterval dateInterval = getModelObjectById(currentId);
    dateIntervalService.deleteEntity(dateInterval);

    return true;
  }

  private DateInterval getModelObjectById(Integer entityId) {
    return dateIntervalService.findDateIntervalById(entityId);
  }

  private List<DateAT> allAvailableDateATs() {
    List<DateAT> dateAttributeTypes = attributeTypeService.getAllDateAT();
    for (DateInterval dateInterval : dateIntervalService.findAllDateIntervals()) {
      dateAttributeTypes.remove(dateInterval.getStartDate());
      dateAttributeTypes.remove(dateInterval.getEndDate());
    }
    return dateAttributeTypes;
  }

  private List<DateAT> allAvailableDateATsForDateInterval(DateInterval dateInterval) {
    List<DateAT> dateAttributeTypes = allAvailableDateATs();
    dateAttributeTypes.add(dateInterval.getStartDate());
    dateAttributeTypes.add(dateInterval.getEndDate());
    return dateAttributeTypes;
  }

  private void validateNewDIName(String name, Integer id) {
    if (name == null || name.equals("")) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.NAME_CANNOT_BE_EMPTY);
    }
    // check if an entity with the same name already exists
    if (dateIntervalService.existsDateIntervalByName(id, name)) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.DUPLICATE_ENTRY, name);
    }
  }

  private void validateDatesCongruency(DateAT start, DateAT end) {
    boolean hasAMatch = false;

    mainLoop: for (BuildingBlockType bbtStart : start.getBuildingBlockTypes()) {
      for (BuildingBlockType bbtEnd : end.getBuildingBlockTypes()) {
        if (bbtStart.getId().equals(bbtEnd.getId())) {
          hasAMatch = true;
          break mainLoop;
        }
      }
    }

    if (!hasAMatch) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.DATE_AT_NOT_SHARING_BBTYPE);
    }
  }

  public void setDateIntervalService(DateIntervalService dateIntervalService) {
    this.dateIntervalService = dateIntervalService;
  }

  /**{@inheritDoc}**/
  @Override
  protected String getFlowId() {
    return Dialog.DATE_INTERVAL.getFlowId();
  }

  private void checkPermission() {
    Permissions perms = UserContext.getCurrentUserContext().getPerms();
    if (!perms.getUserHasFuncPermAttributes()) {
      throw new IteraplanBusinessException(IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }
  }
}
