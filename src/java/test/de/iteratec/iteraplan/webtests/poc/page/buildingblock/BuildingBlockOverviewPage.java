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
package de.iteratec.iteraplan.webtests.poc.page.buildingblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import de.iteratec.iteraplan.webtests.poc.helper.SeleniumHelper;
import de.iteratec.iteraplan.webtests.poc.page.AbstractContentPage;
import de.iteratec.iteraplan.webtests.poc.page.common.ResultSize;


/**
 * Represents the Building Block List Page, which lists all Building Blocks
 */
public class BuildingBlockOverviewPage extends AbstractContentPage {

  private By comboPageSizeLocator = By.id("pageSize");
  private By bbNumberLocator      = By.xpath("//*[@id=\"ResultTableModule\"]/div[1]/div[1]");

  /**
   * Default constructor.
   * @param driver
   */
  public BuildingBlockOverviewPage(WebDriver driver) {
    super(driver);
  }

  /**
   * Set the page results for the list with building blocks
   * @param result
   */
  public void setPageResult(ResultSize result) {

    switch (result) {

      case TEN:
        driver.findElement(comboPageSizeLocator).sendKeys("1");
        break;
      case TWENTY:
        driver.findElement(comboPageSizeLocator).sendKeys("2");
        break;
      case FIFTY:
        driver.findElement(comboPageSizeLocator).sendKeys("5");
        break;
      case ALL:
        driver.findElement(comboPageSizeLocator).sendKeys("a");
        break;
      default:
        break;

    }

    driver.findElement(comboPageSizeLocator).sendKeys(Keys.RETURN);
  }

  /**
   * Returns all entries for a Building Block as List
   * Attention: There must be attention to "information system" because they have an extra column with status
   * @return list of Building Block Entries
   */
  public List<BuildingBlockListEntry> getListEntriesAsList() {
    return new ArrayList<BuildingBlockListEntry>(getListEntriesAsMap().values());
  }

  /**
   * Returns all entries for a Building Block as Map
   * Attention: There must be attention to "information system" because they have an extra column with status
   * @return map of building block entries
   */
  public Map<String, BuildingBlockListEntry> getListEntriesAsMap() {

    Map<String, BuildingBlockListEntry> map = new HashMap<String, BuildingBlockListEntry>();

    By resultListLocator = By.xpath("//*[@id=\"resultTable\"]/tbody/tr");
    By nameLocator = By.xpath("td[1]");
    By hierarchicalNameLocator = By.xpath("td[2]");
    By descriptionLocator = By.xpath("td[3]");
    By elementsLinkLocator = By.xpath("td[1]/span/a");
    int counter = 1;

    List<WebElement> table = driver.findElements(resultListLocator);

    //iterate through all tr elements (= rows) of the table
    for (WebElement tr : table) {

      String name = tr.findElement(nameLocator).getText();
      String hierarchicalName = tr.findElement(hierarchicalNameLocator).getText();
      String description = tr.findElement(descriptionLocator).getText();

      BuildingBlockListEntry entry = new BuildingBlockListEntry(driver);

      entry.setId(counter);
      entry.setName(name);
      entry.setHierarchicalName(hierarchicalName);
      entry.setDescription(description);
      entry.setDetailLinkLocator(tr.findElement(elementsLinkLocator));

      //add entry to map
      map.put(name, entry);

      counter++;
    }

    return map;

  }

  /**
   * Returns the number of BB from a label in gui
   * This method does not count the elements
   * @return number of elements
   */
  public int getNumberOfBBElementsFromGUI() {

    String numberString = driver.findElement(bbNumberLocator).getText();
    SeleniumHelper helper = SeleniumHelper.getInstance();
    return helper.getIntFromString(numberString);
  }

}
