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

import org.springframework.validation.Errors;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportMemoryBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.DynamicQueryFormData;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.TimeseriesQuery;
import de.iteratec.iteraplan.model.dto.BigInstancePermissionsDTO;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;


public class ObjectRelatedPermissionMemBean extends ManageReportMemoryBean {

  /** Serialization version. */
  private static final long         serialVersionUID        = -4776170392786420082L;
  private Integer                   selectedUserEntityId;
  private String                    userEntityFilter;
  private Integer                   selectedBuildingBlockIdToRemove;

  // information for the queries.
  private String                    selectedQueryTypeNameDB = null;

  // the dto
  private BigInstancePermissionsDTO dto;

  private ComponentMode             componentMode;

  public ObjectRelatedPermissionMemBean() {
    // nothing to do
  }

  public ObjectRelatedPermissionMemBean(BigInstancePermissionsDTO dto) {
    initDTO(dto);
  }

  /**
   * @param dto The dto that contains all the data to display for the selected user entity.
   * @param form The query form for a building block.
   */
  public ObjectRelatedPermissionMemBean(BigInstancePermissionsDTO dto, DynamicQueryFormData<?> form, TimeseriesQuery tsQuery) {
    super(form, tsQuery);
    initDTO(dto);
    this.setSelectedQueryTypeNameDB(form.getType().getTypeNameDB());
  }

  private void initDTO(BigInstancePermissionsDTO dtoToInitialize) {
    this.dto = dtoToInitialize;
    this.selectedUserEntityId = dtoToInitialize.getCurrentUserEntity().getId();
  }

  public BigInstancePermissionsDTO getDto() {
    return dto;
  }

  public void setDto(BigInstancePermissionsDTO dto) {
    this.dto = dto;
  }

  public Integer getSelectedUserEntityId() {
    return selectedUserEntityId;
  }

  public void setSelectedUserEntityId(Integer selectedUserEntityId) {
    this.selectedUserEntityId = selectedUserEntityId;
  }

  public String getUserEntityFilter() {
    return userEntityFilter;
  }

  public void setUserEntityFilter(String userEntityFilter) {
    this.userEntityFilter = userEntityFilter;
  }

  public Integer getSelectedBuildingBlockIdToRemove() {
    return selectedBuildingBlockIdToRemove;
  }

  public void setSelectedBuildingBlockIdToRemove(Integer selectedBuildingBlockIdToRemove) {
    this.selectedBuildingBlockIdToRemove = selectedBuildingBlockIdToRemove;
  }

  public String getSelectedQueryTypeNameDB() {
    return selectedQueryTypeNameDB;
  }

  public final void setSelectedQueryTypeNameDB(String selectedQueryTypeNameDB) {
    this.selectedQueryTypeNameDB = selectedQueryTypeNameDB;
  }

  public ComponentMode getComponentMode() {
    return componentMode;
  }

  public void setComponentMode(ComponentMode componentMode) {
    this.componentMode = componentMode;
  }

  /**
   * Perform form validation on component model. Is automatically invoked by Spring Webflow when an
   * event occurs in state 'edit'.
   * 
   * @param errors Spring error context
   */
  public void validateEdit(Errors errors) {
    super.validateUserInput(errors);
  }
}
