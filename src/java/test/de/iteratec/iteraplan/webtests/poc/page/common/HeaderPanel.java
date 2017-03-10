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
package de.iteratec.iteraplan.webtests.poc.page.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import de.iteratec.iteraplan.webtests.poc.page.AbstractPanel;


/**
 * Panel which represents the Header of the page.
 * This includes the menu panel, the iteraplan logo and the searchbar. 
 */
@SuppressWarnings("PMD.TooManyMethods")
public class HeaderPanel extends AbstractPanel {

  private By startPageLocator              = By.id("iteraplan_header_image");

  /*
   * Main Menu Selectors
   */
  private By eaData                        = By.xpath("//*[@id=\"repository\"]/a");
  private By administration                = By.xpath("//*[@id=\"administration\"]/a");
  private By graphicalReporting            = By.xpath("//*[@id=\"diagram\"]/a");
  private By governance                    = By.xpath("//*[@id=\"users\"]/a");
  private By system                        = By.xpath("//*[@id=\"user\"]/a");
  private By reporting                     = By.xpath("//*[@id=\"reporting\"]/a");
  private By bulkEdit                      = By.xpath("//*[@id=\"bulkedit\"]/a");
  private By languate                      = By.xpath("//*[@id=\"language\"]/a");
  private By about                         = By.xpath("//*[@id=\"about\"]/a");

  /*
   * EA Menu
   */
  private By menu_overviewLocator          = By.xpath("//*[@id=\"menu.Overview\"]");
  private By menu_searchLocator            = By.xpath("//*[@id=\"menu.Search\"]");
  private By menu_bdLocator                = By.xpath("//*[@id=\"menu.BusinessDomain\"]");
  private By menu_bpLocator                = By.xpath("//*[@id=\"menu.BusinessProcess\"]");
  private By menu_buLocator                = By.xpath("//*[@id=\"menu.BusinessUnit\"]");
  private By menu_productLocator           = By.xpath("//*[@id=\"menu.Product\"]");
  private By menu_bfLocator                = By.xpath("//*[@id=\"menu.BusinessFunction\"]");
  private By menu_boLocator                = By.xpath("//*[@id=\"menu.BusinessObject\"]");
  private By menu_bmLocator                = By.xpath("//*[@id=\"menu.BusinessMapping\"]");
  private By menu_isdLocator               = By.xpath("//*[@id=\"menu.InformationSystemDomain\"]");
  private By menu_isLocator                = By.xpath("//*[@id=\"menu.InformationSystem\"]");
  private By menu_interfacesLocator        = By.xpath("//*[@id=\"menu.Interface\"]");
  private By menu_adLocator                = By.xpath("//*[@id=\"menu.ArchitecturalDomain\"]");
  private By menu_tcLocator                = By.xpath("//*[@id=\"menu.TechnicalComponent\"]");
  private By menu_ieLocator                = By.xpath("//*[@id=\"menu.InfrastructureElement\"]");
  private By menu_projects                 = By.xpath("//*[@id=\"menu.Project\"]");

  /*
   * Reporting menu
   */
  private By menu_tabularReporting         = By.xpath("//*[@id=\"menu.TabularReporting\"]");
  private By menu_iteraQL                  = By.xpath("//*[@id=\"menu.IteraQl\"]");
  private By menu_successorReport          = By.xpath("//*[@id=\"menu.SuccessorReport\"]");
  private By menu_consistencyCheck2        = By.xpath("//*[@id=\"menu.ConsistencyCheck\"]");

  /*
   * Admin Menu
   */

  private By menu_attributesOverview       = By.id("menu.AttributeType");
  private By menu_attributesGroupOverview  = By.id("menu.AttributeTypeGroup");
  private By menu_configurationLocator     = By.id("menu.Configuration");
  private By menu_dateInterval             = By.xpath("//*[@id=\"menu.DateInterval\"]");
  private By menu_templates                = By.xpath("//*[@id=\"menu.Templates\"]");

  /*
   * Graphical Reporting
   */
  private By menu_graphRepOverview         = By.id("menu.GraphicalReporting");
  private By menu_savedQueries             = By.xpath("//*[@id=\"menu.SavedQueries\"]");
  private By menu_customDashboard          = By.xpath("//*[@id=\"menu.CustomDashboardInstancesOverview\"]");
  private By menu_landscapeDiagram         = By.xpath("//*[@id=\"menu.graphicalExport.landscapeDiagram\"]");
  private By menu_clusterDiagram           = By.xpath("//*[@id=\"menu.graphicalExport.clusterDiagram\"]");
  private By menu_vbbClusterDiagram        = By.xpath("//*[@id=\"menu.graphicalExport.vbbClusterDiagram\"]");
  private By menu_informationFlowDiagram   = By.xpath("//*[@id=\"menu.graphicalExport.informationFlowDiagram\"]");
  private By menu_portfolioDiagram         = By.xpath("//*[@id=\"menu.graphicalExport.portfolioDiagram\"]");
  private By menu_masterplanDiagram        = By.xpath("//*[@id=\"menu.graphicalExport.masterplanDiagram\"]");
  private By menu_dashboard                = By.xpath("//*[@id=\"menu.Dashboard\"]");
  private By menu_pieBarDiagram            = By.xpath("//*[@id=\"menu.graphicalExport.pieBarDiagram\"]");
  private By menu_compositeDiagram         = By.xpath("//*[@id=\"menu.graphicalExport.compositeDiagram\"]");
  private By menu_lineDiagram              = By.xpath("//*[@id=\"menu.graphicalExport.lineDiagram\"]");

  /*
   * Bulk Edit
   */
  private By menu_massUpdate               = By.xpath("//*[@id=\"menu.MassUpdate\"]");
  private By menu_Import                   = By.xpath("//*[@id=\"menu.Import\"]");

  /*
   * Governance
   */
  private By menu_UserOverview             = By.id("menu.User");
  private By menu_usergroup                = By.xpath("//*[@id=\"menu.UserGroup\"]");
  private By menu_role                     = By.xpath("//*[@id=\"menu.Role\"]");
  private By menu_objectRelatedPermissions = By.xpath("//*[@id=\"menu.ObjectRelatedPermission\"]");
  private By menu_supportingQuery          = By.xpath("//*[@id=\"menu.SupportingQuery\"]");
  //  By menu_consitencyCheck         = By.id("menu.ConsistencyCheck"); // error because of double id usage???
  private By menu_consitencyCheck          = By.xpath("//*[@id=\"users\"]/ul/li[7]/a");

  /*
   * About Menu
   */
  private By menu_definitions              = By.xpath("//*[@id=\"about\"]/ul/li[2]/a");

  /*
   * System Menu
   */
  private By menu_logout                   = By.id("menu.Logout");
  private By menu_clearSession             = By.id("menu.Restart");
  private By menu_profile                  = By.xpath("//*[@id=\"user\"]/ul/li[1]/a");

  /**
   * Default constructor.
   * @param driver
   */
  public HeaderPanel(WebDriver driver) {
    super(driver);
  }

  public void clickIteraplanLogo() {
    driver.findElement(startPageLocator).click();
  }

  /*
   * EA Menu
   * 
   */

  /**
   * Clicks the Overview Menu 
   */
  public void clickOverview() {
    clickEADataMenuButton();
    driver.findElement(menu_overviewLocator).click();
  }

  /**
   * Clicks the Search Menu
   */
  public void clickSearch() {
    clickEADataMenuButton();
    driver.findElement(menu_searchLocator).click();
  }

  /**
   * Clicks the Business Domain
   */
  public void clickBusinessDomain() {
    clickEADataMenuButton();
    driver.findElement(menu_bdLocator).click();
  }

  /**
   * Clicks the Business Object
   */
  public void clickBusinessObjects() {
    clickEADataMenuButton();
    driver.findElement(menu_boLocator).click();
  }

  public void clickBusinessProcess() {
    clickEADataMenuButton();
    driver.findElement(menu_bpLocator).click();
  }

  public void clickBusinessUnit() {
    clickEADataMenuButton();
    driver.findElement(menu_buLocator).click();
  }

  public void clickProduct() {
    clickEADataMenuButton();
    driver.findElement(menu_productLocator).click();
  }

  public void clickBusinessFunction() {
    clickEADataMenuButton();
    driver.findElement(menu_bfLocator).click();
  }

  public void clickBusinessMapping() {
    clickEADataMenuButton();
    driver.findElement(menu_bmLocator).click();
  }

  public void clickInformationSystemDomain() {
    clickEADataMenuButton();
    driver.findElement(menu_isdLocator).click();
  }

  public void clickInformationSystem() {
    clickEADataMenuButton();
    driver.findElement(menu_isLocator).click();
  }

  public void clickInterface() {
    clickEADataMenuButton();
    driver.findElement(menu_interfacesLocator).click();
  }

  public void clickArchitechturalDomain() {
    clickEADataMenuButton();
    driver.findElement(menu_adLocator).click();
  }

  public void clickTechnicalComponent() {
    clickEADataMenuButton();
    driver.findElement(menu_tcLocator).click();
  }

  public void clickInfrastructureElement() {
    clickEADataMenuButton();
    driver.findElement(menu_ieLocator).click();
  }

  public void clickProjects() {
    clickEADataMenuButton();
    driver.findElement(menu_projects).click();
  }

  /*
   *  Reporting Menu
   */
  public void clickTabularReporting() {
    clickReportingButton();
    driver.findElement(menu_tabularReporting).click();
  }

  public void clickIteraQL() {
    clickReportingButton();
    driver.findElement(menu_iteraQL).click();
  }

  public void clickSuccessorReport() {
    clickReportingButton();
    driver.findElement(menu_successorReport).click();
  }

  /**
   * Clicks the consistency check menu from tabular reporting
   */
  public void clickConsitencyCheck2() {
    clickReportingButton();
    driver.findElement(menu_consistencyCheck2).click();
  }

  /*
   * Administration Menu
   */

  public void clickAttributes() {
    clickAdministrationButton();
    driver.findElement(menu_attributesOverview).click();
  }

  public void clickAttributesGroup() {
    clickAdministrationButton();
    driver.findElement(menu_attributesGroupOverview).click();
  }

  public void clickConfiguration() {
    clickAdministrationButton();
    driver.findElement(menu_configurationLocator).click();
  }

  public void clickDateInterval() {
    clickAdministrationButton();
    driver.findElement(menu_dateInterval).click();
  }

  public void clickTemplate() {
    clickAdministrationButton();
    driver.findElement(menu_templates).click();
  }

  /*
   * Graphical Report Menu
   */
  public void clickGraphicalReportingOverview() {
    clickGraphicalReportingButton();
    driver.findElement(menu_graphRepOverview).click();
  }

  public void clickSavedQueries() {
    clickGraphicalReportingButton();
    driver.findElement(menu_savedQueries).click();
  }

  public void clickCustomDashboard() {
    clickGraphicalReportingButton();
    driver.findElement(menu_customDashboard).click();
  }

  public void clickLandscapeDiagram() {
    clickGraphicalReportingButton();
    driver.findElement(menu_landscapeDiagram).click();
  }

  public void clickClusterDiagram() {
    clickGraphicalReportingButton();
    driver.findElement(menu_clusterDiagram).click();
  }

  public void clickVBBClusterDiagram() {
    clickGraphicalReportingButton();
    driver.findElement(menu_vbbClusterDiagram).click();
  }

  public void clickInformationFlowDiagram() {
    clickGraphicalReportingButton();
    driver.findElement(menu_informationFlowDiagram).click();
  }

  public void clickPortfolioDiagram() {
    clickGraphicalReportingButton();
    driver.findElement(menu_portfolioDiagram).click();
  }

  public void clickMasterplanDiagram() {
    clickGraphicalReportingButton();
    driver.findElement(menu_masterplanDiagram).click();
  }

  public void clickDashboard() {
    clickGraphicalReportingButton();
    driver.findElement(menu_dashboard).click();
  }

  public void clickPieBarDiaram() {
    clickGraphicalReportingButton();
    driver.findElement(menu_pieBarDiagram).click();
  }

  public void clickCompositeDiagram() {
    clickGraphicalReportingButton();
    driver.findElement(menu_compositeDiagram).click();
  }

  public void clickLineDiagram() {
    clickGraphicalReportingButton();
    driver.findElement(menu_lineDiagram).click();
  }

  /*
   * Bulk Edit / Mass Data
   */
  public void clickMassUpdate() {
    clickBulkEdit();
    driver.findElement(menu_massUpdate).click();
  }

  public void clickExportImport() {
    clickBulkEdit();
    driver.findElement(menu_Import).click();
  }

  /*
   * Governance
   */
  public void clickUserOverview() {
    clickGovernance();
    driver.findElement(menu_UserOverview).click();
  }

  public void clickUserGroup() {
    clickGovernance();
    driver.findElement(menu_usergroup).click();
  }

  public void clickRole() {
    clickGovernance();
    driver.findElement(menu_role).click();
  }

  public void clickObjectRelatedPermissions() {
    clickGovernance();
    driver.findElement(menu_objectRelatedPermissions).click();
  }

  public void clickSupportingQuery() {
    clickGovernance();
    driver.findElement(menu_supportingQuery).click();
  }

  /**
   * Clicks the consistency check menu from governance
   */
  public void clickConsistencyCheck() {
    clickGovernance();
    driver.findElement(menu_consitencyCheck).click();
  }

  /*
   * Languae
   */

  /**
   * Switches the language for iteraplan
   * @param lang the language enum
   */
  public void switchLanguage(Language lang) {

    //click the menu
    clickLanguage();

    int languageId = 3;

    switch (lang) {

      case BULGARIAN:
        languageId = 1;
        break;

      case ENGLISH:
        languageId = 2;
        break;
      case GERMAN:
        languageId = 3;
        break;

      case FRENCH:
        languageId = 4;
        break;

      case HUNGARIAN:
        languageId = 5;
        break;

      case SPANISH:
        languageId = 6;
        break;
      case SWEDISH:
        languageId = 7;
        break;
      default:

    }

    By menu_language = By.xpath("//*[@id=\"language\"]/ul/li[" + languageId + "]/a");

    driver.findElement(menu_language).click();

  }

  /*
   * About Menu
   */

  public void clickDefinition() {
    driver.findElement(about).click();
    driver.findElement(menu_definitions).click();
  }

  /*
   * System Menu
   */
  public void logout() {
    clickSystem();
    driver.findElement(menu_logout).click();
  }

  public void clearSession() {
    clickSystem();
    driver.findElement(menu_clearSession).click();
  }

  public void clickProfile() {
    clickSystem();
    driver.findElement(menu_profile).click();
  }

  /*
   * Helper
   */
  private void clickEADataMenuButton() {
    driver.findElement(eaData).click();
  }

  private void clickAdministrationButton() {
    driver.findElement(administration).click();
  }

  private void clickGraphicalReportingButton() {
    driver.findElement(graphicalReporting).click();
  }

  private void clickGovernance() {
    driver.findElement(governance).click();
  }

  private void clickSystem() {
    driver.findElement(system).click();
  }

  private void clickReportingButton() {
    driver.findElement(reporting).click();
  }

  private void clickBulkEdit() {
    driver.findElement(bulkEdit).click();
  }

  private void clickLanguage() {
    driver.findElement(languate).click();
  }

}
