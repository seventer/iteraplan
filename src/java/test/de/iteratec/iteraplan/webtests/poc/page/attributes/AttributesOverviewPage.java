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
package de.iteratec.iteraplan.webtests.poc.page.attributes;

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
 * Overview for Attributes
 * Shows the list with all attributes
 */
public class AttributesOverviewPage extends AbstractContentPage {

  private By comboPageSizeLocator     = By.id("pageSize");
  private By attributesCounterLocator = By.xpath("//*[@id=\"ResultTableModule\"]/div[1]/div[1]");

  /**
   * Default constructor.
   * @param driver
   */
  public AttributesOverviewPage(WebDriver driver) {
    super(driver);
  }

  /**
   * Sets the result size to the given enum value
   * @param result size of list
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
   * Returns all Attributes as HashMap from the Attributes Overview Page
   * @return map of all attributes
   */
  public Map<String, AttributesListEntry> getListEntriesAsMap() {

    Map<String, AttributesListEntry> map = new HashMap<String, AttributesListEntry>();

    By resultListLocator = By.xpath("//*[@id=\"resultTable\"]/tbody/tr");
    By nameLocator = By.xpath("td[1]");
    By descriptionLocator = By.xpath("td[2]");
    By attrGroupLocator = By.xpath("td[3]");
    By attrTypeLocator = By.xpath("td[4]");
    By elementsLinkLocator = By.xpath("td[1]/span/a");

    int counter = 1;

    List<WebElement> table = driver.findElements(resultListLocator);

    //iterate through all tr elements (= rows) of the table
    for (WebElement tr : table) {

      String name = tr.findElement(nameLocator).getText();
      String description = tr.findElement(descriptionLocator).getText();
      String attrGroup = tr.findElement(attrGroupLocator).getText();
      String attrType = tr.findElement(attrTypeLocator).getText();

      AttributesListEntry entry = new AttributesListEntry(driver);

      entry.setId(counter);
      entry.setDescription(description);
      entry.setAttributeGroup(attrGroup);
      entry.setAttributeType(attrType);
      entry.setDetailLink(tr.findElement(elementsLinkLocator));

      map.put(name, entry);

      counter++;

    }

    return map;
  }

  /**
   * Get the number of elements that are shown in the list
   * This method does not count the elements in the list, but get the number from a label in the gui
   * @return number of attributes
   */
  public int getNumberOfAttributesFromGUI() {

    String numberString = driver.findElement(attributesCounterLocator).getText();

    SeleniumHelper helper = SeleniumHelper.getInstance();
    return helper.getIntFromString(numberString);

  }

}
