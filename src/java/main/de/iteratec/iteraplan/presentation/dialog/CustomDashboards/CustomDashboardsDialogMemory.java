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
package de.iteratec.iteraplan.presentation.dialog.CustomDashboards;

import java.util.List;

import de.iteratec.iteraplan.model.queries.CustomDashboardInstance;
import de.iteratec.iteraplan.model.queries.CustomDashboardTemplate;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


public class CustomDashboardsDialogMemory extends DialogMemory {
  private static final long             serialVersionUID = 1L;

  private List<CustomDashboardInstance> dashboards;
  private List<SavedQuery>              savedQueries;
  private List<CustomDashboardTemplate> templates;

  private Integer                       templateId;
  private Integer                       savedQueryId;
  private CustomDashboardInstance       customDashboardInstance;
  private Integer                       customDashboardInstanceId;

  private String                        action;

  private boolean                       createDashboard  = false; // flag, if the user is creating a new Dashboard instance 

  /**
   * @return dashboards the dashboards
   */
  public List<CustomDashboardInstance> getDashboards() {
    return dashboards;
  }

  public void setDashboards(List<CustomDashboardInstance> dashboards) {
    this.dashboards = dashboards;
  }

  /**
   * @return savedQueries the savedQueries
   */
  public List<SavedQuery> getSavedQueries() {
    return savedQueries;
  }

  public void setSavedQueries(List<SavedQuery> savedQueries) {
    this.savedQueries = savedQueries;
  }

  /**
   * @return savedQueryId the savedQueryId
   */
  public Integer getSavedQueryId() {
    return savedQueryId;
  }

  public void setSavedQueryId(Integer savedQueryId) {
    this.savedQueryId = savedQueryId;
  }

  /**
   * @return customDashboardInstance the customDashboardInstance
   */
  public CustomDashboardInstance getCustomDashboardInstance() {
    return customDashboardInstance;
  }

  public void setCustomDashboardInstance(CustomDashboardInstance customDashboardInstance) {
    this.customDashboardInstance = customDashboardInstance;
  }

  /**
   * @return customDashboardInstanceId the customDashboardInstanceId
   */
  public Integer getCustomDashboardInstanceId() {
    return customDashboardInstanceId;
  }

  public void setCustomDashboardInstanceId(Integer customDashboardInstanceId) {
    this.customDashboardInstanceId = customDashboardInstanceId;
  }

  /**
   * @return action the action
   */
  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public boolean isCreateDashboard() {
    return createDashboard;
  }

  public void setCreateDashboard(boolean createDashboard) {
    this.createDashboard = createDashboard;
  }

  public List<CustomDashboardTemplate> getTemplates() {
    return templates;
  }

  public void setTemplates(List<CustomDashboardTemplate> templates) {
    this.templates = templates;
  }

  public Integer getTemplateId() {
    return templateId;
  }

  public void setTemplateId(Integer templateId) {
    this.templateId = templateId;
  }

}
