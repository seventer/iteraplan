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
package de.iteratec.iteraplan.presentation.dialog.Templates;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.customDashboard.CustomDashboardTemplatesDialogMemory;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateInfo;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


/**
 * Dialog Memory for the Templates page.
 */
public class TemplatesDialogMemory extends DialogMemory {

  private static final long                           serialVersionUID = -2429869464227737749L;

  private static final List<TemplateType>             AVAILABLE_TYPES  = ImmutableList.of(TemplateType.EXCEL_2007, TemplateType.EXCEL_2003,
                                                                           TemplateType.INFOFLOW);

  private final long                                  creationTime;

  private boolean                                     templateFileNull;
  private boolean                                     wrongFileType;
  private boolean                                     uploadSuccessful;

  private String                                      action;
  private String                                      targetTemplateName;
  private String                                      targetTemplateType;

  private CustomDashboardTemplatesDialogMemory        customDashboardDialogMemory;
  private Integer                                     customDashboardId;                          // current selected custom dashboard template
  private Integer                                     selectedBBTypeId;                           // id of a BBT for a new custom dashboard template

  private final Map<TemplateType, List<TemplateInfo>> templateInfos    = Maps.newTreeMap();

  public TemplatesDialogMemory() {
    super();
    this.creationTime = System.currentTimeMillis();
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getAction() {
    return action;
  }

  public List<TemplateType> getAvailableTypes() {
    return AVAILABLE_TYPES;
  }

  public boolean isTemplateFileNull() {
    return templateFileNull;
  }

  public void setTemplateFileNull(boolean xmiFileNull) {
    this.templateFileNull = xmiFileNull;
  }

  public boolean isWrongFileType() {
    return wrongFileType;
  }

  public void setWrongFileType(boolean wrongFileType) {
    this.wrongFileType = wrongFileType;
  }

  /**
   * Will return true when the upload was completed successfully.
   * 
   * @return true when successful, false if no import was done, or unsuccessful.
   */
  public boolean isUploadSuccessful() {
    return uploadSuccessful;
  }

  public void setUploadSuccessful(boolean uploadSuccessfull) {
    this.uploadSuccessful = uploadSuccessfull;
  }

  public void setTargetTemplateName(String targetTemplateName) {
    this.targetTemplateName = targetTemplateName;
  }

  public String getTargetTemplateName() {
    return targetTemplateName;
  }

  public void setTargetTemplateType(String targetTemplateType) {
    this.targetTemplateType = targetTemplateType;
  }

  public String getTargetTemplateType() {
    return this.targetTemplateType;
  }

  public CustomDashboardTemplatesDialogMemory getCustomDashboardDialogMemory() {
    if (customDashboardDialogMemory == null) {
      customDashboardDialogMemory = new CustomDashboardTemplatesDialogMemory();
    }
    return customDashboardDialogMemory;
  }

  public void setCustomDashboardDialogMemory(CustomDashboardTemplatesDialogMemory customDashboardDialogMemory) {
    this.customDashboardDialogMemory = customDashboardDialogMemory;
  }

  public void putTemplateInfos(TemplateType key, List<TemplateInfo> infos) {
    this.templateInfos.put(key, infos);
  }

  public Map<TemplateType, List<TemplateInfo>> getTemplateInfos() {
    return this.templateInfos;
  }

  @Override
  public int hashCode() {
    int prime = 31;
    int result = 1;
    // result = prime * result + ((zipArchive == null) ? 0 : zipArchive.hashCode());
    result = prime * result + Long.valueOf(creationTime).hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TemplatesDialogMemory other = (TemplatesDialogMemory) obj;
    if (this.creationTime != other.creationTime) {
      return false;
    }
    return true;
  }

  /**
   * Re-Initialize the DialogMemory, resetting all state and warnings to defaults.
   */
  public void reInit() {
    templateFileNull = false;
    wrongFileType = false;
    uploadSuccessful = false;
    targetTemplateName = null;
    targetTemplateType = null;
    action = null;
    customDashboardId = null; // current selected dashboard template
    selectedBBTypeId = null; // id of a BBT for a new custom dashboard template
  }

  /**
   * @return the id of the current selected custom dashboard template
   */
  public Integer getCustomDashboardId() {
    return customDashboardId;
  }

  /**
   * Set the the id of the current used dashboard template
   * @param customDashboardId
   */
  public void setCustomDashboardId(Integer customDashboardId) {
    this.customDashboardId = customDashboardId;
  }

  /**
   * @return the id of the selected BBType
   */
  public Integer getSelectedBBTypeId() {
    return selectedBBTypeId;
  }

  /**
   * 
   * @param selectedBBTypeId
   */
  public void setSelectedBBTypeId(Integer selectedBBTypeId) {
    this.selectedBBTypeId = selectedBBTypeId;
  }
}