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
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.Capabilities;

import de.iteratec.iteraplan.webtests.poc.AbstractUITestcase;
import de.iteratec.iteraplan.webtests.poc.WebDriverManager;
import de.iteratec.iteraplan.webtests.poc.page.LoginPage;
import de.iteratec.iteraplan.webtests.poc.page.OverlayPopupPage;
import de.iteratec.iteraplan.webtests.poc.page.ProfilePage;
import de.iteratec.iteraplan.webtests.poc.page.StartPage;
import de.iteratec.iteraplan.webtests.poc.page.common.Language;


/**
 * Clicks all menu entries
 */
public class NavigationTest extends AbstractUITestcase {

  /**
   * Default constructor.
   * @param browser
   * @param browserEnvironment
   */
  public NavigationTest(Capabilities browser, String browserEnvironment) {
    super(browser, browserEnvironment);
  }

  @Before
  public void setUp() {
    WebDriverManager.getInstance().getDriver().get(baseurl + "/login.do");
  }

  @Test
  public void testclickEAMenu() {

    login();

    StartPage start = ui.start();

    //we don't use the navigator functions because every page has to be created for this test
    //so everytime the startpage is used
    //don't use any other function than getPageTitle(), otherwise it will crash
    start.header().clickOverview();
    assertEquals("Übersicht - iteraplan", start.getPageTitle());

    start.header().clickSearch();
    assertEquals("Suche - iteraplan", start.getPageTitle());

    start.header().clickBusinessDomain();
    assertEquals("Fachliche Domänen : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickBusinessProcess();
    assertEquals("Geschäftsprozesse : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickBusinessUnit();
    assertEquals("Geschäftseinheiten : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickProduct();
    assertEquals("Produkte : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickBusinessFunction();
    assertEquals("Fachliche Funktionen : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickBusinessObjects();
    assertEquals("Geschäftsobjekte : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickBusinessMapping();
    assertEquals("Fachliche Zuordnungen - iteraplan", start.getPageTitle());

    start.header().clickInformationSystemDomain();
    assertEquals("IS-Domänen : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickInformationSystem();
    assertEquals("Informationssysteme : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickInterface();
    assertEquals("Schnittstellen : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickArchitechturalDomain();
    assertEquals("Architekturdomänen : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickTechnicalComponent();
    assertEquals("Technische Bausteine : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickInfrastructureElement();
    assertEquals("Infrastrukturelemente : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickProjects();
    assertEquals("Projekte : Übersicht - iteraplan", start.getPageTitle());

  }

  @Test
  public void testReportingMenu() {

    login();

    StartPage start = ui.start();

    start.header().clickTabularReporting();
    assertEquals("Tabellarische Auswertungen - iteraplan", start.getPageTitle());

    start.header().clickIteraQL();
    assertEquals("Anfragekonsole - iteraplan", start.getPageTitle());

    start.header().clickSuccessorReport();
    assertEquals("Nachfolgerrelease-Auswertungen - iteraplan", start.getPageTitle());

    start.header().clickConsitencyCheck2();
    assertEquals("Konsistenzchecks - iteraplan", start.getPageTitle());

  }

  @Test
  public void testGraphicalReportingMenu() {

    login();

    StartPage start = ui.start();

    start.header().clickGraphicalReportingOverview();
    assertEquals("Übersicht - iteraplan", start.getPageTitle());

    start.header().clickSavedQueries();
    assertEquals("Alle gespeicherten Abfragen - iteraplan", start.getPageTitle());

    start.header().clickCustomDashboard();
    assertEquals("Benutzerdefinierte Dashboards - iteraplan", start.getPageTitle());

    start.header().clickLandscapeDiagram();
    assertEquals("Bebauungsplan-Grafik - iteraplan", start.getPageTitle());

    start.header().clickClusterDiagram();
    assertEquals("Cluster-Grafik - iteraplan", start.getPageTitle());

    start.header().clickInformationFlowDiagram();
    assertEquals("Informationsfluss-Grafik - iteraplan", start.getPageTitle());

    start.header().clickPortfolioDiagram();
    assertEquals("Portfolio-Grafik - iteraplan", start.getPageTitle());

    start.header().clickMasterplanDiagram();
    assertEquals("Masterplan-Grafik - iteraplan", start.getPageTitle());

    start.header().clickPieBarDiaram();
    assertEquals("Balken- oder Kuchen-Grafik - iteraplan", start.getPageTitle());

    start.header().clickCompositeDiagram();
    assertEquals("Zusammengesetzte Balken- und Kuchen-Grafik - iteraplan", start.getPageTitle());

  }

  @Test
  public void testBulkEditMenu() {

    login();

    StartPage start = ui.start();

    start.header().clickMassUpdate();
    assertEquals("Massenupdates - iteraplan", start.getPageTitle());

    start.header().clickExportImport();
    assertEquals("Export/Import - iteraplan", start.getPageTitle());

  }

  @Test
  public void testGovernanceMenu() {

    login();

    StartPage start = ui.start();

    start.header().clickUserOverview();
    assertEquals("Anwenderverwaltung : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickUserGroup();
    assertEquals("Anwendergruppenverwaltung : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickRole();
    assertEquals("Rollen und Rechte : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickObjectRelatedPermissions();
    assertEquals("Objektbezogene Rechte : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickSupportingQuery();
    assertEquals("Unterstützende Abfragen - iteraplan", start.getPageTitle());

    start.header().clickConsistencyCheck();
    assertEquals("Konsistenzchecks - iteraplan", start.getPageTitle());

  }

  @Test
  public void testAdministrationMenu() {

    login();

    StartPage start = ui.start();

    start.header().clickConfiguration();
    assertEquals("Konfiguration - iteraplan", start.getPageTitle());

    start.header().clickAttributesGroup();
    assertEquals("Merkmalsgruppen - iteraplan", start.getPageTitle());

    start.header().clickAttributes();
    assertEquals("Merkmale : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickDateInterval();
    assertEquals("Zeitspannen : Übersicht - iteraplan", start.getPageTitle());

    start.header().clickTemplate();
    assertEquals("Dokumentvorlagen - iteraplan", start.getPageTitle());

  }

  @Test
  public void testLanguageSelection() {

    login();

    StartPage start = ui.start();

    //at this place was the bulgarian language
    //due to unicode charset issues, the teststep and the assertions has been deleted

    start.header().switchLanguage(Language.ENGLISH);
    assertEquals("Welcome - iteraplan", start.getPageTitle());

    start.header().switchLanguage(Language.FRENCH);
    assertEquals("Bienvenue - iteraplan", start.getPageTitle());

    start.header().switchLanguage(Language.HUNGARIAN);
    assertEquals("Üdvözöljük - iteraplan", start.getPageTitle());

    start.header().switchLanguage(Language.SPANISH);
    assertEquals("Bienvenido - iteraplan", start.getPageTitle());

    start.header().switchLanguage(Language.SWEDISH);
    assertEquals("Välkommen - iteraplan", start.getPageTitle());

    start.header().switchLanguage(Language.GERMAN);
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());

  }

  @Test
  public void testAboutMenu() {

    /*
     * no more tests here because of external linking
     */

    login();
    StartPage start = ui.start();

    start.header().clickDefinition();
    assertEquals("Hilfe - iteraplan", start.getPageTitle());

  }

  @Test
  public void testUsersMenu() {

    login();
    StartPage start = ui.start();

    //profile
    start.header().clickProfile();
    ProfilePage profile = ui.profile();
    assertEquals("Loginname: system ", profile.getLoginName());

    //logout
    profile.header().logout();
    OverlayPopupPage popup = ui.popup();
    popup.clickOK();

    LoginPage login = ui.login();
    assertEquals("iteratec - iteraplan", login.getPageTitle());

    //clear session
    login.setCredentials("system", "password").login();
    start = ui.start();
    start.header().clearSession();
    popup = ui.popup();
    popup.clickOK();
    start = ui.start();
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());

  }

  @Test
  @Ignore
  public void testSearchFunction() {
    //tbd what to test here..
  }

}
