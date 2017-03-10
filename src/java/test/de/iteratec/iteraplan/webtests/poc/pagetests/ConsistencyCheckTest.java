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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;

import de.iteratec.iteraplan.webtests.poc.AbstractUITestcase;
import de.iteratec.iteraplan.webtests.poc.WebDriverManager;
import de.iteratec.iteraplan.webtests.poc.page.StartPage;
import de.iteratec.iteraplan.webtests.poc.page.WaitUtils;
import de.iteratec.iteraplan.webtests.poc.page.consistencycheck.ConsistencyCheckPage;


/**
 * Executes the consistency checks for IS, TC and general components
 */
public class ConsistencyCheckTest extends AbstractUITestcase {

  /**
   * Default constructor.
   * @param browser
   * @param browserEnvironment
   */
  public ConsistencyCheckTest(Capabilities browser, String browserEnvironment) {
    super(browser, browserEnvironment);
  }

  @Before
  public void setUp() {
    WebDriverManager.getInstance().getDriver().get(baseurl + "/login.do");
  }

  @Test
  /**
   * This testcase executes all Checks for the Information System
   */
  public void testISLandscape() {

    login();

    StartPage start = ui.start();
    start.header().clickConsistencyCheck();

    ConsistencyCheckPage cc = ui.consistencCheck();
    int checksCounter = cc.informationSystem().getNumberOfChecks();

    for (int i = 1; i <= checksCounter; i++) {
      cc.informationSystem().executeCheck(i);
      //safe wait
      WaitUtils.wait(2000);
      assertTrue(cc.informationSystem().isResultBoxVisible());
      assertEquals(i, cc.informationSystem().getCheckNumberFromResultBox());

    }

  }

  @Test
  /**
   * This testcase executes all checks for technical components
   */
  public void testTechnicalLandscape() {

    login();

    StartPage start = ui.start();
    start.header().clickConsistencyCheck();

    ConsistencyCheckPage cc = ui.consistencCheck();
    int checksCounter = cc.technicalComponent().getNumberOfChecks();

    for (int i = 1; i <= checksCounter; i++) {
      cc.technicalComponent().executeCheck(i);
      //safe wait
      WaitUtils.wait(2000);
      assertTrue(cc.technicalComponent().isResultBoxVisible());
      assertEquals(i, cc.technicalComponent().getCheckNumberFromResultBox());

    }

  }

  @Test
  /**
   * This testcase executes all checks for general components
   */
  public void testGeneralLandscape() {

    login();

    StartPage start = ui.start();
    start.header().clickConsistencyCheck();

    ConsistencyCheckPage cc = ui.consistencCheck();
    int checksCounter = cc.generalComponent().getNumberOfChecks();

    for (int i = 1; i <= checksCounter; i++) {
      cc.generalComponent().executeCheck(i);
      //safe wait
      WaitUtils.wait(2000);
      assertTrue(cc.generalComponent().isResultBoxVisible());
      assertEquals(i, cc.generalComponent().getCheckNumberFromResultBox());

    }

  }

}
