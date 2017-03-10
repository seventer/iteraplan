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
package de.iteratec.iteraplan.webtests.poc.pagetests;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;

import de.iteratec.iteraplan.webtests.poc.AbstractUITestcase;
import de.iteratec.iteraplan.webtests.poc.WebDriverManager;
import de.iteratec.iteraplan.webtests.poc.page.StartPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockOverviewPage;


/**
 * Clicks all Elements on the start page
 */
public class MainscreenMetamodelImageTest extends AbstractUITestcase {

  /**
   * Default constructor.
   * @param browser
   * @param browserEnvironment
   */
  public MainscreenMetamodelImageTest(Capabilities browser, String browserEnvironment) {
    super(browser, browserEnvironment);
  }

  @Before
  public void setUp() {
    WebDriverManager.getInstance().getDriver().get(baseurl + "/login.do");
  }

  @Test
  public void testBusinessDomain() {

    login();
    StartPage start = ui.start();
    start.clickBusinessDomain();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Fachliche Domänen : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testBusinessProcess() {
    login();
    StartPage start = ui.start();
    start.clickBusinessProcess();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Geschäftsprozesse : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testBusinessUnit() {
    login();
    StartPage start = ui.start();
    start.clickBusinessUnit();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Geschäftseinheiten : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testProduct() {
    login();
    StartPage start = ui.start();
    start.clickProduct();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Produkte : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testBusinessMapping() {
    login();
    StartPage start = ui.start();
    start.clickBusinessMapping();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Fachliche Zuordnungen - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testBusinessFunction() {
    login();
    StartPage start = ui.start();
    start.clickBusinessFunction();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Fachliche Funktionen : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testBusinessObject() {
    login();
    StartPage start = ui.start();
    start.clickBusinessObject();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Geschäftsobjekte : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testInterface() {
    login();
    StartPage start = ui.start();
    start.clickInterface();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Schnittstellen : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testInformationSystem() {
    login();
    StartPage start = ui.start();
    start.clickInformationSystem();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Informationssysteme : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testInformationSystemDomain() {
    login();
    StartPage start = ui.start();
    start.clickInformationSystemDomain();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("IS-Domänen : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testProject() {
    login();
    StartPage start = ui.start();
    start.clickProject();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Projekte : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testInfrastrukturElement() {
    login();
    StartPage start = ui.start();
    start.clickInfrastructureElement();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Infrastrukturelemente : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testTechnicalComponent() {
    login();
    StartPage start = ui.start();
    start.clickTechnicalComponent();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Technische Bausteine : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

  @Test
  public void testArchitecturalDomain() {
    login();
    StartPage start = ui.start();
    start.clickArchitecturalDomain();
    BuildingBlockOverviewPage bbtOverview = ui.buildingBlockOverview();
    assertEquals("Architekturdomänen : Übersicht - iteraplan", bbtOverview.getPageTitle());
    bbtOverview.header().clickIteraplanLogo();

  }

}
