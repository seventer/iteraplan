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
package de.iteratec.iteraplan.webtests.poc.page;

import org.openqa.selenium.WebDriver;

import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesDetailPage;
import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesNewElement1Page;
import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesNewElement2Page;
import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.attributesGroup.AttributesGroupEditElementPage;
import de.iteratec.iteraplan.webtests.poc.page.attributesGroup.AttributesGroupOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockDetailPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockEditElementPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.consistencycheck.ConsistencyCheckPage;
import de.iteratec.iteraplan.webtests.poc.page.interfaces.InterfacesDetailPage;
import de.iteratec.iteraplan.webtests.poc.page.interfaces.InterfacesEditElementPage;
import de.iteratec.iteraplan.webtests.poc.page.interfaces.InterfacesOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.tabularreport.AddColumnPopupPage;
import de.iteratec.iteraplan.webtests.poc.page.tabularreport.TabularReportPage;
import de.iteratec.iteraplan.webtests.poc.page.visualization.cluster.ClusterDiagram1Page;
import de.iteratec.iteraplan.webtests.poc.page.visualization.cluster.ClusterDiagram2Page;


/**
 * This class represents the whole UI of the iteraplan application.
 * For testing the application, create an instance of this class. Then call the specific methods to create the different 
 * "views" or different states of the application.
 * 
 * If you for example start your test and open the login page, the application state is now "login", so you invoke the login()
 * method to create an instance of the login view. With this View-Object, you could access the login name and password text field and
 * the login button. 
 * 
 * After a  login, the application state changed to "start". You have to call the start() method from this class to get an instance
 * of the start page. 
 * 
 * You don't have to instantiate the different Page Objects, because this class will do it for you.
 */
public class IteraplanUI {

  private WebDriver                      driver;

  private LoginPage                      loginPage                      = null;
  private StartPage                      startPage                      = null;
  private BuildingBlockOverviewPage      buildingBlockOverviewPage      = null;
  private BuildingBlockEditElementPage   buildingBlockNewElementPage    = null;
  private BuildingBlockDetailPage        buildingBlockDetailPage        = null;
  private OverlayPopupPage               deletePopupPage                = null;
  private AttributesOverviewPage         attrOverviewPage               = null;
  private AttributesGroupOverviewPage    attrOverviewGroupPage          = null;
  private AttributesNewElement1Page      attrNewElement1Page            = null;
  private AttributesNewElement2Page      attrNewElement2Page            = null;
  private AttributesDetailPage           attrDetailPage                 = null;
  private AttributesGroupEditElementPage attrGroupNewElementPage        = null;
  private ConfigurationPage              configPage                     = null;
  private GraphicalReportingOverviewPage graphicalReportingOverviewPage = null;
  private UserOverviewPage               userOverviewPage               = null;
  private ConsistencyCheckPage           ccPage                         = null;
  private InterfacesOverviewPage         interfacesOverviewPage         = null;
  private InterfacesEditElementPage      interfacesNewElementPage       = null;
  private InterfacesDetailPage           interfaceDetailPage            = null;
  private ProfilePage                    profilePage                    = null;
  private ConfigurationOverviewPage      configOverview                 = null;
  private ClusterDiagram1Page            cluster1Page                   = null;
  private ClusterDiagram2Page            cluster2Page                   = null;
  private TabularReportPage              tabularReportPage              = null;
  private ExportImportPage               exportImportPage               = null;
  private AddColumnPopupPage             addColumnPopup                 = null;

  public IteraplanUI(WebDriver driver) {
    this.driver = driver;
  }

  /*
   * Methods for single pages
   */

  /**
   * Returns the Login Page
   * @return login page
   */
  public LoginPage login() {

    if (loginPage == null) {
      loginPage = new LoginPage(driver);
    }

    return loginPage;

  }

  /**
   * Returns the Start Page
   * @return start page
   */
  public StartPage start() {
    if (startPage == null) {
      startPage = new StartPage(driver);
    }

    return startPage;
  }

  /**
   * Returns the Building Block List Page
   * @return building block list page
   */
  public BuildingBlockOverviewPage buildingBlockOverview() {
    if (buildingBlockOverviewPage == null) {
      buildingBlockOverviewPage = new BuildingBlockOverviewPage(driver);
    }

    return buildingBlockOverviewPage;
  }

  /**
   * Returns the Building block new element Page
   * @return building block new element page
   */
  public BuildingBlockEditElementPage buildingBlockNewElement() {
    if (buildingBlockNewElementPage == null) {
      buildingBlockNewElementPage = new BuildingBlockEditElementPage(driver);
    }

    return buildingBlockNewElementPage;
  }

  /**
   * Returns the Building Block Detail Page
   * @return building block detail page
   */
  public BuildingBlockDetailPage buildingBlockDetail() {
    if (buildingBlockDetailPage == null) {
      buildingBlockDetailPage = new BuildingBlockDetailPage(driver);
    }

    return buildingBlockDetailPage;
  }

  public OverlayPopupPage popup() {
    if (deletePopupPage == null) {
      deletePopupPage = new OverlayPopupPage(driver);
    }

    return deletePopupPage;
  }

  public AttributesOverviewPage attrOverview() {
    if (attrOverviewPage == null) {
      attrOverviewPage = new AttributesOverviewPage(driver);
    }

    return attrOverviewPage;

  }

  public AttributesGroupOverviewPage attrOverviewGroup() {

    if (attrOverviewGroupPage == null) {
      attrOverviewGroupPage = new AttributesGroupOverviewPage(driver);
    }

    return attrOverviewGroupPage;

  }

  public AttributesNewElement1Page attrNewElement1() {
    if (attrNewElement1Page == null) {
      attrNewElement1Page = new AttributesNewElement1Page(driver);
    }

    return attrNewElement1Page;
  }

  public AttributesNewElement2Page attrNewElement2() {
    if (attrNewElement2Page == null) {
      attrNewElement2Page = new AttributesNewElement2Page(driver);
    }

    return attrNewElement2Page;
  }

  public AttributesDetailPage attrDetail() {
    if (attrDetailPage == null) {
      attrDetailPage = new AttributesDetailPage(driver);
    }

    return attrDetailPage;
  }

  public AttributesGroupEditElementPage attrGroupNewElement() {

    if (attrGroupNewElementPage == null) {
      attrGroupNewElementPage = new AttributesGroupEditElementPage(driver);
    }

    return attrGroupNewElementPage;

  }

  public ConfigurationPage config() {

    if (configPage == null) {
      configPage = new ConfigurationPage(driver);
    }

    return configPage;

  }

  public GraphicalReportingOverviewPage graphicalReportingOverview() {
    if (graphicalReportingOverviewPage == null) {
      graphicalReportingOverviewPage = new GraphicalReportingOverviewPage(driver);
    }
    return graphicalReportingOverviewPage;
  }

  public UserOverviewPage userOverview() {
    if (userOverviewPage == null) {
      userOverviewPage = new UserOverviewPage(driver);
    }
    return userOverviewPage;
  }

  public ConsistencyCheckPage consistencCheck() {
    if (ccPage == null) {
      ccPage = new ConsistencyCheckPage(driver);
    }

    return ccPage;
  }

  public InterfacesOverviewPage interfacesOverview() {
    if (interfacesOverviewPage == null) {
      interfacesOverviewPage = new InterfacesOverviewPage(driver);
    }

    return interfacesOverviewPage;
  }

  public InterfacesEditElementPage interfacesNewElement() {

    if (interfacesNewElementPage == null) {
      interfacesNewElementPage = new InterfacesEditElementPage(driver);
    }

    return interfacesNewElementPage;
  }

  public InterfacesDetailPage interfaceDetail() {

    if (interfaceDetailPage == null) {
      interfaceDetailPage = new InterfacesDetailPage(driver);
    }

    return interfaceDetailPage;

  }

  public ProfilePage profile() {
    if (profilePage == null) {
      profilePage = new ProfilePage(driver);
    }

    return profilePage;
  }

  public ConfigurationOverviewPage configurationOverview() {
    if (configOverview == null) {
      configOverview = new ConfigurationOverviewPage(driver);
    }

    return configOverview;
  }

  public ClusterDiagram1Page cluster1() {
    if (cluster1Page == null) {
      cluster1Page = new ClusterDiagram1Page(driver);
    }

    return cluster1Page;
  }

  public ClusterDiagram2Page cluster2() {
    if (cluster2Page == null) {
      cluster2Page = new ClusterDiagram2Page(driver);
    }

    return cluster2Page;
  }

  public TabularReportPage tabularReport() {
    if (tabularReportPage == null) {
      tabularReportPage = new TabularReportPage(driver);
    }

    return tabularReportPage;
  }

  public ExportImportPage exportImport() {
    if (exportImportPage == null) {
      exportImportPage = new ExportImportPage(driver);
    }

    return exportImportPage;
  }

  public AddColumnPopupPage addColumn() {
    if (addColumnPopup == null) {
      addColumnPopup = new AddColumnPopupPage(driver);
    }

    return addColumnPopup;
  }

}
