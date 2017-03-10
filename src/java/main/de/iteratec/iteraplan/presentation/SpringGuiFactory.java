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
package de.iteratec.iteraplan.presentation;

import java.util.List;
import java.util.Map;

import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExtensionFormHelper;


/**
 * GuiFactory for Spring managed Gui-Beans.
 * 
 * @author Karsten Voges
 */
public final class SpringGuiFactory {

  private ExtensionFormHelper           extensionFormHelper;
  private String                        defaultColor;
  private List<String>                  attributeColors;
  private List<String>                  portfolioColors;
  private List<String>                  landscapeColors;
  private List<String>                  lineColors;
  private List<String>                  informationFlowColors;
  private List<String>                  masterplanColors;
  private List<String>                  clusterColors;
  private List<String>                  pieBarColors;
  private List<String>                  vbbClusterColors;
  private Integer                       defaultLineTypeKey;
  private String                        defaultLineTypePresentation;
  private Map<Integer, String>          availableLineTypes;

  private static final SpringGuiFactory INSTANCE = new SpringGuiFactory();

  private SpringGuiFactory() {
    // Singleton
  }

  public static SpringGuiFactory getInstance() {
    return INSTANCE;
  }

  public ExtensionFormHelper getExtensionFormHelper() {
    return extensionFormHelper;
  }

  public void setExtensionFormHelper(ExtensionFormHelper extensionFormHelper) {
    this.extensionFormHelper = extensionFormHelper;
  }

  public List<String> getPortfolioColors() {
    return portfolioColors;
  }

  public void setPortfolioColors(List<String> portfolioColors) {
    this.portfolioColors = portfolioColors;
  }

  public void setLandscapeColors(List<String> landscapeColors) {
    this.landscapeColors = landscapeColors;
  }

  public List<String> getLandscapeColors() {
    return landscapeColors;
  }
  
  public List<String> getLineColors() {
    return lineColors;
  }

  public void setLineColors(List<String> lineColors) {
    this.lineColors = lineColors;
  }

  public void setInformationFlowColors(List<String> informationFlowColors) {
    this.informationFlowColors = informationFlowColors;
  }

  public List<String> getInformationFlowColors() {
    return informationFlowColors;
  }

  public List<String> getMasterplanColors() {
    return masterplanColors;
  }

  public void setMasterplanColors(List<String> masterplanColors) {
    this.masterplanColors = masterplanColors;
  }

  public List<String> getClusterColors() {
    return clusterColors;
  }

  public void setClusterColors(List<String> clusterColors) {
    this.clusterColors = clusterColors;
  }

  public List<String> getPieBarColors() {
    return pieBarColors;
  }

  public void setPieBarColors(List<String> pieBarColors) {
    this.pieBarColors = pieBarColors;
  }

  public List<String> getVbbClusterColors() {
    return vbbClusterColors;
  }

  public void setVbbClusterColors(List<String> vbbClusterColors) {
    this.vbbClusterColors = vbbClusterColors;
  }

  public String getDefaultColor() {
    return defaultColor;
  }

  public void setDefaultColor(String defaultColor) {
    this.defaultColor = defaultColor;
  }

  public Integer getDefaultLineTypeKey() {
    return defaultLineTypeKey;
  }

  public void setDefaultLineTypeKey(Integer defaultLineTypeKey) {
    this.defaultLineTypeKey = defaultLineTypeKey;
  }

  public String getDefaultLineTypePresentation() {
    return defaultLineTypePresentation;
  }

  public void setDefaultLineTypePresentation(String defaultLineTypePresentation) {
    this.defaultLineTypePresentation = defaultLineTypePresentation;
  }

  public Map<Integer, String> getAvailableLineTypes() {
    return availableLineTypes;
  }

  public void setAvailableLineTypes(Map<Integer, String> availableLineTypes) {
    this.availableLineTypes = availableLineTypes;
  }

  public List<String> getAttributeColors() {
    return attributeColors;
  }

  public void setAttributeColors(List<String> attributeColors) {
    this.attributeColors = attributeColors;
  }
}
