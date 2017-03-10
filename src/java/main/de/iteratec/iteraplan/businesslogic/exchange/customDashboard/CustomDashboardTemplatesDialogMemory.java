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
package de.iteratec.iteraplan.businesslogic.exchange.customDashboard;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Ordering;

import de.iteratec.iteraplan.common.collections.BBTLocalizedNameFunction;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.queries.CustomDashboardTemplate;
import de.iteratec.iteraplan.model.queries.SavedQuery;
import de.iteratec.iteraplan.presentation.memory.DialogMemory;


/**
 *
 */
public class CustomDashboardTemplatesDialogMemory extends DialogMemory {

  private static final long             serialVersionUID = 1392814252160679471L;

  public static final String            MODE_EDIT        = "edit";
  public static final String            MODE_PREVIEW     = "preview";
  public static final String            MODE_METADATA    = "metadata";

  private CustomDashboardTemplate       customDashboardTemplate;

  private String                        selectedTab;

  private List<CustomDashboardTemplate> customDashboardTemplates;
  private List<BuildingBlockType>       bbTAvailableForNewTemplate;

  private List<SavedQuery>              savedQueries;

  /**
   * @return List of all custom dashboard tempaltes
   */
  public List<CustomDashboardTemplate> getCustomDashboardTemplates() {
    return customDashboardTemplates;
  }

  /**
   * Set List of all CustomDashboardTemplates
   * @param customDashboardTemplates
   */
  public void setCustomDashboardTemplates(List<CustomDashboardTemplate> customDashboardTemplates) {
    this.customDashboardTemplates = customDashboardTemplates;
    // Collections.sort(this.customDashboardTemplates, Ordering.natural());
  }

  public String getSelectedTab() {
    if (null == selectedTab || selectedTab.equals(" ")) {
      // Default view
      selectedTab = MODE_EDIT;
    }
    return selectedTab;
  }

  public void setSelectedTab(String selectedTab) {
    this.selectedTab = selectedTab;
  }

  public String getCustomDashboardContent() {
    return customDashboardTemplate.getContent();
  }

  public void setCustomDashboardContent(String content) {
    customDashboardTemplate.setContent(content);
  }

  public String getCustomDashboardDescription() {
    return customDashboardTemplate.getDescription();
  }

  public void setCustomDashboardDescription(String description) {
    customDashboardTemplate.setDescription(description);
  }
  
  public String getCustomDashboardName() {
    return customDashboardTemplate.getName();
  }

  public void setCustomDashboardName(String name) {
    customDashboardTemplate.setName(name);
  }

  public BuildingBlockType getCustomDashboardSelectedBBType() {
    if(this.customDashboardTemplate == null){
      return null;
    }
    return this.customDashboardTemplate.getBuildingBlockType();
  }

  public void setCustomDashboardSelectedBBType(BuildingBlockType buildingBlockType) {
    this.customDashboardTemplate.setBuildingBlockType(buildingBlockType);
  }

  public List<SavedQuery> getSavedQueries() {
    return savedQueries;
  }

  public void setSavedQueries(List<SavedQuery> savedQueries) {
    this.savedQueries = savedQueries;
  }

  public void editCustomDashboardTemplate(BuildingBlockType bbType) {
    this.customDashboardTemplate.setBuildingBlockType(bbType);
  }
  
  public Integer getCustomDashboardId() {
    return customDashboardTemplate.getId();
  }

  public void init() {
    selectedTab = MODE_EDIT;
    customDashboardTemplate = null;
  }

  /**
   * @return bbTAvailableForNewTemplate the bbTAvailableForNewTemplate
   */
  public List<BuildingBlockType> getBbTAvailableForNewTemplate() {
    return bbTAvailableForNewTemplate;
  }

  public void setBbTAvailableForNewTemplate(List<BuildingBlockType> bbTAvailableForNewTemplate) {
    this.bbTAvailableForNewTemplate = bbTAvailableForNewTemplate;
    Collections.sort(this.bbTAvailableForNewTemplate, Ordering.natural().onResultOf(new BBTLocalizedNameFunction()));
  }

  public CustomDashboardTemplate getCustomDashboardTemplate() {
    return customDashboardTemplate;
  }

  public void setCustomDashboardTemplate(CustomDashboardTemplate customDashboardTemplate) {
    this.customDashboardTemplate = customDashboardTemplate;
  }
}
