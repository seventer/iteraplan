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
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;

import de.iteratec.iteraplan.webtests.poc.AbstractUITestcase;
import de.iteratec.iteraplan.webtests.poc.WebDriverManager;
import de.iteratec.iteraplan.webtests.poc.page.StartPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockDetailPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockEditElementPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.common.ResultSize;


/**
 *
 */
public class ISsCreateChangeDeleteTest extends AbstractUITestcase {

  /**
   * Default constructor.
   * @param browser
   * @param browserEnvironment
   */
  public ISsCreateChangeDeleteTest(Capabilities browser, String browserEnvironment) {
    super(browser, browserEnvironment);
  }

  @Before
  public void setUp() {
    WebDriverManager.getInstance().getDriver().get(baseurl + "/login.do");
  }

  @Test
  /**
   * Creates three information system, that are connected together in a hierarchical relationship
   * Then the first (parent) gets deleted and the child ISs will be deleted to
   */
  public void testInformationSystemsCreateChangeDelete() {

    String overallISDescription = "This is the description for the IS created by Selenium Test";

    String infoSystemName = "!InformationSystem";
    String infoSystemVersion = "1.1";

    String infoSystemParentName = "!InformationSystem Parent";
    String infoSystemParentVersion = "1.0";

    String infoSystemChildName = "!InformationSystem Child";
    String infoSystemChildVersion = "1.2";

    //login and create 3 information systems
    login();
    createInformationSystem(infoSystemName, infoSystemVersion, overallISDescription);
    createInformationSystem(infoSystemParentName, infoSystemParentVersion, overallISDescription);
    createInformationSystem(infoSystemChildName, infoSystemChildVersion, overallISDescription);

    //set the parent child structure

    StartPage start = ui.start();
    start.header().clickInformationSystem();

    BuildingBlockOverviewPage isOverview = ui.buildingBlockOverview();
    isOverview.setPageResult(ResultSize.ALL);
    isOverview.getListEntriesAsMap().get(infoSystemParentName + " # " + infoSystemParentVersion).openDetail();

    /*
     * set subsystem for parent
     */
    BuildingBlockDetailPage isDetail = ui.buildingBlockDetail();
    isDetail.editBBEntry();

    BuildingBlockEditElementPage isNew = ui.buildingBlockNewElement();
    isNew.setContainingSubsystem(infoSystemName).saveElement();

    //assert the relationship
    isDetail = ui.buildingBlockDetail();
    assertEquals(infoSystemName + " # " + infoSystemVersion, isDetail.getSubsystem());
    isDetail.closeDetailPage();

    /*
     * set subsystem for child
     */
    isOverview = ui.buildingBlockOverview();
    isOverview.setPageResult(ResultSize.ALL);
    isOverview.getListEntriesAsMap().get(infoSystemName + " # " + infoSystemVersion).openDetail();

    isDetail = ui.buildingBlockDetail();
    isDetail.editBBEntry();

    isNew = ui.buildingBlockNewElement();
    isNew.setContainingSubsystem(infoSystemChildName).saveElement();

    //assert the relationship
    isDetail = ui.buildingBlockDetail();
    assertEquals(infoSystemChildName + " # " + infoSystemChildVersion, isDetail.getSubsystem());
    isDetail.closeDetailPage();

    //delete the first parent information system and check if its children gets also deleted
    deleteInformationSystem(infoSystemParentName, infoSystemParentVersion);
    //we are now on the startpage so navigate to IS
    start = ui.start();
    start.header().clickInformationSystem();
    isOverview = ui.buildingBlockOverview();

    assertNull(isOverview.getListEntriesAsMap().get(infoSystemParentName + " # " + infoSystemParentVersion));
    assertNull(isOverview.getListEntriesAsMap().get(infoSystemName + " # " + infoSystemVersion));
    assertNull(isOverview.getListEntriesAsMap().get(infoSystemChildName + " # " + infoSystemChildVersion));

  }
}
