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
import de.iteratec.iteraplan.webtests.poc.page.OverlayPopupPage;
import de.iteratec.iteraplan.webtests.poc.page.StartPage;
import de.iteratec.iteraplan.webtests.poc.page.interfaces.IFDirection;
import de.iteratec.iteraplan.webtests.poc.page.interfaces.InterfacesDetailPage;
import de.iteratec.iteraplan.webtests.poc.page.interfaces.InterfacesEditElementPage;
import de.iteratec.iteraplan.webtests.poc.page.interfaces.InterfacesOverviewPage;


/**
 *
 */
public class IFsCreateChangeDeleteTest extends AbstractUITestcase {

  /**
   * Default constructor.
   * @param browser
   * @param browserEnvironment
   */
  public IFsCreateChangeDeleteTest(Capabilities browser, String browserEnvironment) {
    super(browser, browserEnvironment);
  }

  @Before
  public void setUp() {
    WebDriverManager.getInstance().getDriver().get(baseurl + "/login.do");
  }

  /**
   * Creates two interfaces and set the relationship between them
   */
  @Test
  public void testInterfaceAndAttributes() {

    String infoSystemAName = "!Informationsystem A";
    String infoSystemAVersion = "1.0";
    String infoSystemADesc = "The description for information system A";

    String infoSystemBName = "!Informationsystem B";
    String infoSystemBVersion = "1.0";
    String infoSystemBDesc = "The description for information system B";

    String interfaceName = "Interface between A and B";

    IFDirection connection = IFDirection.RIGHT;

    login();

    StartPage start = ui.start();
    start.header().clickInformationSystem();

    //create new information system
    createInformationSystem(infoSystemAName, infoSystemAVersion, infoSystemADesc);
    createInformationSystem(infoSystemBName, infoSystemBVersion, infoSystemBDesc);

    start.header().clickInterface();
    InterfacesOverviewPage ifOverview = ui.interfacesOverview();

    //create new Interface
    ifOverview.leftMenu().clickNewElement();
    InterfacesEditElementPage ifNew = ui.interfacesNewElement();
    ifNew.setName(interfaceName).setLeftIS(infoSystemAName).setRightIS(infoSystemBName).setDirection(connection).clickSave();

    //assert the interface
    InterfacesDetailPage ifDetail = ui.interfaceDetail();
    assertEquals(connection, ifDetail.getDirection());
    assertEquals(infoSystemAName + " # " + infoSystemAVersion, ifDetail.getLeftIS());
    assertEquals(infoSystemBName + " # " + infoSystemBVersion, ifDetail.getRightIS());

    //change the interface connection
    ifDetail.clickEdit();
    ifNew = ui.interfacesNewElement();
    ifNew.setDirection(IFDirection.BIDIRECTIONAL).clickSave();

    //assert if interface has changed
    ifDetail = ui.interfaceDetail();
    assertEquals(IFDirection.BIDIRECTIONAL, ifDetail.getDirection());

    //delete the interface and the IS
    ifDetail.clickDelete();
    OverlayPopupPage popup = ui.popup();
    popup.clickOK();

    deleteInformationSystem(infoSystemAName, infoSystemAVersion);
    deleteInformationSystem(infoSystemBName, infoSystemBVersion);

  }

}
