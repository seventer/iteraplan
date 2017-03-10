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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import de.iteratec.iteraplan.webtests.poc.AbstractUITestcase;
import de.iteratec.iteraplan.webtests.poc.WebDriverManager;
import de.iteratec.iteraplan.webtests.poc.page.StartPage;
import de.iteratec.iteraplan.webtests.poc.page.tabularreport.AddColumnPopupPage;
import de.iteratec.iteraplan.webtests.poc.page.tabularreport.OutputFormat;
import de.iteratec.iteraplan.webtests.poc.page.tabularreport.TabularReportPage;


/**
 *
 */
public class TabularReportingTest extends AbstractUITestcase {

  /**
   * Default constructor.
   * @param browser
   * @param browserEnvironment
   */
  public TabularReportingTest(Capabilities browser, String browserEnvironment) {
    super(browser, browserEnvironment);
  }

  @Before
  public void setUp() {
    WebDriverManager.getInstance().getDriver().get(baseurl + "/login.do");
  }

  public void testInit() {
    //empty test for suppressing the pmd warning: This class name ends with 'Test' but contains no test cases
  }

  @Test
  /**
   * Downloads a Tabular Report as excel 2007 file and asserts if download message is visible
   */
  public void downloadTabularReportAsExcelFile() {

    login();

    StartPage start = ui.start();
    start.header().clickTabularReporting();
    TabularReportPage tab = ui.tabularReport();
    assertEquals("Tabellarische Auswertungen - iteraplan", tab.getPageTitle());

    //select the output format 
    tab.selectOutputFormat(OutputFormat.EXCEL2007).clickRequestReport();

    //assert if popup has appeared
    assertTrue(tab.isDownloadStartedMessageVisible());

  }

  @Test
  /**
   * Downloads a Tabular Report as simple list and asserts if download message is NOT visible
   */
  public void downloadTabularReportAsSimpleList() {

    login();

    StartPage start = ui.start();
    start.header().clickTabularReporting();
    TabularReportPage tab = ui.tabularReport();
    assertEquals("Tabellarische Auswertungen - iteraplan", tab.getPageTitle());

    //select the output format 
    tab.selectOutputFormat(OutputFormat.SIMPLELIST).clickRequestReport();

    //assert if popup has appeared
    assertFalse(tab.isDownloadStartedMessageVisible());

  }

  @Test(expected = ElementNotFoundException.class)
  /**
   * The old csv export is depracted and should not be offered as export format in tabular reporting
   * This test asserts if the export option CSV is gone
   */
  public void downloadAsCSVisNotAvailable() {

    login();

    StartPage start = ui.start();
    start.header().clickTabularReporting();
    TabularReportPage tab = ui.tabularReport();
    assertEquals("Tabellarische Auswertungen - iteraplan", tab.getPageTitle());

    //select the output format 
    tab.selectOutputFormat(OutputFormat.CSV).clickRequestReport();
    fail("CSV Formt should not be selectable");

  }

  @Test
  /**
   * Test adds to a simple report more technical columns
   * In order to do that, the popup with columns must be shown and the "more" button must be clicked
   */
  public void addTechnicalColumnsForCSVExport() {

    login();

    StartPage start = ui.start();
    start.header().clickTabularReporting();
    TabularReportPage tab = ui.tabularReport();
    assertEquals("Tabellarische Auswertungen - iteraplan", tab.getPageTitle());

    //select the output format 
    tab.selectOutputFormat(OutputFormat.SIMPLELIST).clickRequestReport();

    //assert if popup has appeared
    assertFalse(tab.isDownloadStartedMessageVisible());

    tab.clickAddColumn();
    AddColumnPopupPage colPopup = ui.addColumn();

    //assert description of initial more/less button
    assertEquals("Mehr Spalten", colPopup.getLabelOfMoreLessButton());
    List<String> lessColumns = colPopup.getAvailableColumnsList();

    //toggle the button and assert number of entries in list
    colPopup.clickMoreLess();
    assertEquals("Weniger Spalten", colPopup.getLabelOfMoreLessButton());
    List<String> moreColumns = colPopup.getAvailableColumnsList();

    assertTrue(lessColumns.size() <= moreColumns.size());

  }

  @Test
  /**
   * This test adds the application name and the release id to the set of visible columns for tabular report
   */
  public void addTechnicalColumnsToSimpleReport() {

    String column1 = "IS ID";
    String column2 = "ISR ID";

    login();

    StartPage start = ui.start();
    start.header().clickTabularReporting();
    TabularReportPage tab = ui.tabularReport();
    assertEquals("Tabellarische Auswertungen - iteraplan", tab.getPageTitle());

    //select the output format 
    tab.selectOutputFormat(OutputFormat.SIMPLELIST).clickRequestReport();

    //assert if popup has appeared
    assertFalse(tab.isDownloadStartedMessageVisible());

    tab.clickAddColumn();
    AddColumnPopupPage colPopup = ui.addColumn();

    //assert description of initial more/less button
    assertEquals("Mehr Spalten", colPopup.getLabelOfMoreLessButton());
    List<String> lessColumns = colPopup.getAvailableColumnsList();

    //toggle the button and assert number of entries in list
    colPopup.clickMoreLess();
    assertEquals("Weniger Spalten", colPopup.getLabelOfMoreLessButton());
    List<String> moreColumns = colPopup.getAvailableColumnsList();

    assertTrue(lessColumns.size() <= moreColumns.size());

    //add the column 1 and assert the list head
    List<String> elements1 = colPopup.getAvailableColumnsList();
    assertTrue(elements1.contains(column1));
    colPopup.selectColumn(column1).clickAddColumn();
    tab = ui.tabularReport();
    List<String> head1 = tab.getTableHeadList();
    assertTrue(head1.contains(column1));

    //add the column 2 and assert the list head
    tab.clickAddColumn();
    colPopup = ui.addColumn();
    //now the list must show all columns so assert that
    assertEquals("Weniger Spalten", colPopup.getLabelOfMoreLessButton());
    List<String> elements2 = colPopup.getAvailableColumnsList();
    assertTrue(elements2.contains(column2));
    //assert that the currently added column is not displayed in the available columns
    assertFalse(elements2.contains(column1));
    colPopup.selectColumn(column2).clickAddColumn();
    tab = ui.tabularReport();
    List<String> head2 = tab.getTableHeadList();
    assertTrue(head2.contains(column2));
  }

}
