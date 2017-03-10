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
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.NoSuchElementException;

import de.iteratec.iteraplan.webtests.poc.AbstractUITestcase;
import de.iteratec.iteraplan.webtests.poc.WebDriverManager;
import de.iteratec.iteraplan.webtests.poc.page.ConfigurationPage;
import de.iteratec.iteraplan.webtests.poc.page.GraphicalReportingOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.StartPage;
import de.iteratec.iteraplan.webtests.poc.page.UserOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockOverviewPage;


/**
 * Clicktest, that clicks various elements on the webpage
 */
public class ClickTest extends AbstractUITestcase {

  /**
   * Default constructor.
   * @param browser
   * @param browserEnvironment
   */
  public ClickTest(Capabilities browser, String browserEnvironment) {
    super(browser, browserEnvironment);
  }

  @Before
  public void setUp() {
    WebDriverManager.getInstance().getDriver().get(baseurl + "/login.do");
  }

  @Test
  /**
   * This test method does some click stuff
   */
  public void testSelectFromMenu() {

    login();

    //at first on starpage
    StartPage start = ui.start();
    start.header().clickConfiguration();

    //then configuration page
    ConfigurationPage config = ui.config();
    assertEquals("Konfiguration - iteraplan", config.getPageTitle());
    config.header().clickGraphicalReportingOverview();

    //then graphical reporting
    GraphicalReportingOverviewPage graphOverview = ui.graphicalReportingOverview();
    assertEquals("Übersicht - iteraplan", graphOverview.getPageTitle());
    graphOverview.header().clickInformationSystem();

    //then information system
    BuildingBlockOverviewPage infoSystemList = ui.buildingBlockOverview();
    assertEquals("Informationssysteme : Übersicht - iteraplan", infoSystemList.getPageTitle());
    infoSystemList.header().clickUserOverview();

    UserOverviewPage userOverview = ui.userOverview();
    assertEquals("Anwenderverwaltung : Übersicht - iteraplan", userOverview.getPageTitle());

  }

  /**
   * Clicks on an unreachable button and fails, because the user has no access rights
   * @throws Throwable
   */
  public void testUnreachableButton() {

    /*
     * If this test fail, please check first if there is an user with the given
     * username and password and if the login works
     * 
     * If not you have to create this user with iTurm
     */

    try {
      //first set the timeout for the driver
      WebDriverManager.getInstance().setDriverTimeout(5);

      login("Joe", "Passw0rd");
      StartPage start = ui.start();
      assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());

      //should throw an exception
      start.header().clickUserOverview();
      fail("Exception has to been thrown here");
    } catch (NoSuchElementException ex) {

      //

    } finally {

      //first set the timeout for the driver
      WebDriverManager.getInstance().setDriverTimeout(WebDriverManager.WAIT_TIMEOUT);
    }

  }

}
