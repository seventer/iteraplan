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
package de.iteratec.iteraplan.webtests.poc.page.tabularreport;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import de.iteratec.iteraplan.webtests.poc.WebDriverManager;
import de.iteratec.iteraplan.webtests.poc.page.AbstractContentPage;
import de.iteratec.iteraplan.webtests.poc.page.WaitUtils;


/**
 *
 */
public class TabularReportPage extends AbstractContentPage {

  private By requestReportButtonLocator    = By.id("sendQueryButton");
  private By outputFormatLocator           = By.id("tabularOptions.resultFormat");
  private By downloadStartedMessageLocator = By.id("downloadStartedMessage");
  private By addColumnLocator              = By.xpath("//*[@id=\"ReportingResultTableModule\"]/div[1]/div[2]/a");
  private By tableHeadLocator              = By.xpath("//*[@id=\"ReportingResultTable\"]/thead/tr/th");

  /**
   * Default constructor.
   * @param driver
   */
  public TabularReportPage(WebDriver driver) {
    super(driver);
  }

  public void clickRequestReport() {
    driver.findElement(requestReportButtonLocator).click();
  }

  public TabularReportPage selectOutputFormat(OutputFormat format) {

    boolean formatAvailable = false;

    WebElement select = driver.findElement(outputFormatLocator);
    List<WebElement> options = select.findElements(By.tagName("option"));

    for (WebElement option : options) {

      if (option.getText().equals(format.getGermanLabel())) {
        //mark format as availabe
        formatAvailable = true;
        option.click();
        break;
      }

    }

    //check if format wasn't available
    if (!formatAvailable) {
      throw new ElementNotFoundException(outputFormatLocator.toString(), "", format.getGermanLabel());
    }

    //safe wait for page reloading
    WaitUtils.wait(2000);

    return this;
  }

  public boolean isDownloadStartedMessageVisible() {

    //set the timeout for Webdriver to 5 seconds
    WebDriverManager.getInstance().setDriverTimeout(5);

    try {
      driver.findElement(downloadStartedMessageLocator);
      return true;
    } catch (NoSuchElementException e) {

      //set timeout to default
      WebDriverManager.getInstance().setDriverTimeout(WebDriverManager.WAIT_TIMEOUT);
      return false;
    }

  }

  public void clickAddColumn() {
    driver.findElement(addColumnLocator).click();
  }

  public List<String> getTableHeadList() {

    List<String> results = new ArrayList<String>();
    List<WebElement> headList = driver.findElements(tableHeadLocator);

    for (WebElement head : headList) {
      results.add(head.getText());
    }

    return results;
  }

}
