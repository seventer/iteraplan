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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;

import de.iteratec.iteraplan.webtests.poc.AbstractUITestcase;
import de.iteratec.iteraplan.webtests.poc.WebDriverManager;
import de.iteratec.iteraplan.webtests.poc.page.OverlayPopupPage;
import de.iteratec.iteraplan.webtests.poc.page.StartPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockDetailPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockEditElementPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockListEntry;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.common.ResultSize;


/**
 *
 */
public class BOsCreateChangeDeleteTest extends AbstractUITestcase {

  /**
   * Default constructor.
   * @param browser
   * @param browserEnvironment
   */
  public BOsCreateChangeDeleteTest(Capabilities browser, String browserEnvironment) {
    super(browser, browserEnvironment);
  }

  @Before
  public void setUp() {
    WebDriverManager.getInstance().getDriver().get(baseurl + "/login.do");
  }

  /*
   * TODO: Test with specialization
   */

  /**
   * This tests creates several Business Objects and sets an hierarchical connection between them
   * BO Parent -> BO -> BO Child
   * Then the Parent gets deleted and the two children must be also deleted
   */
  @Test
  public void testBusinessObjectsCreateChangeDelete() {

    String bo_name = "Business Object " + (int) (Math.random() * 1000);
    String bo_desc = "This is the BO";

    String bo_parent_name = "Business Object Parent " + (int) (Math.random() * 1000);
    String bo_parent_desc = "This is the parent BO";

    String bo_child_name = "Business Object Child " + (int) (Math.random() * 1000);
    String bo_child_desc = "This is the child BO";

    login();

    //Create BO
    createBusinessObject(bo_name, bo_desc);

    //create a new element with same name and assert
    StartPage start = ui.start();
    start.header().clickBusinessObjects();
    BuildingBlockOverviewPage bd_list = ui.buildingBlockOverview();

    bd_list.leftMenu().clickNewElement();
    BuildingBlockEditElementPage bd_new = ui.buildingBlockNewElement();
    bd_new.setBOName(bo_name).saveElement();
    assertTrue(bd_new.getErrorMessage().length() > 0);
    bd_new.cancel();
    OverlayPopupPage pop = ui.popup();
    pop.clickOK();
    bd_list.header().clickIteraplanLogo();

    //create a BO which will become parent
    createBusinessObject(bo_parent_name, bo_parent_desc);

    //create a BO which will become child
    createBusinessObject(bo_child_name, bo_child_desc);

    //now open the first entry ...
    start = ui.start();
    start.header().clickBusinessObjects();
    bd_list = ui.buildingBlockOverview();
    bd_list.setPageResult(ResultSize.ALL);
    Map<String, BuildingBlockListEntry> map = bd_list.getListEntriesAsMap();
    BuildingBlockListEntry entry = map.get(bo_name);
    assertNotNull(entry);
    entry.openDetail();

    //... and set parent and child
    BuildingBlockDetailPage bd_detail = ui.buildingBlockDetail();
    bd_detail.editBBEntry();
    bd_new = ui.buildingBlockNewElement();
    bd_new.setParentBO(bo_parent_name).addChildBOs(Arrays.asList(bo_child_name)).saveElement();

    //assert the changes
    bd_detail = ui.buildingBlockDetail();
    assertEquals(bo_parent_name, bd_detail.getParentBO());
    assertNotNull(bd_detail.getChildBOAsMap().get(bo_child_name));
    assertEquals(bo_child_desc, bd_detail.getChildBOAsMap().get(bo_child_name));

    //delete the parent
    bd_detail.closeDetailPage();
    bd_list = ui.buildingBlockOverview();
    bd_list.setPageResult(ResultSize.ALL);
    BuildingBlockListEntry bbentry = bd_list.getListEntriesAsMap().get(bo_parent_name);
    assertNotNull(bbentry);
    bbentry.openDetail();
    bd_detail = ui.buildingBlockDetail();
    bd_detail.deleteBBEntry();

    pop = ui.popup();
    pop.clickOK();

    bd_list = ui.buildingBlockOverview();

    Map<String, BuildingBlockListEntry> map2 = bd_list.getListEntriesAsMap();

    assertNull(map2.get(bo_parent_name));
    assertNull(map2.get(bo_name));
    assertNull(map2.get(bo_child_name));

  }

}
