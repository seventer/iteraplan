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
package de.iteratec.iteraplan.webtests.poc.page.attributesGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import de.iteratec.iteraplan.webtests.poc.page.AbstractContentPage;


/**
 * Overview for Attributes Group
 */
public class AttributesGroupOverviewPage extends AbstractContentPage {

  private By optionGroupLocator   = By.xpath("//*[@id=\"atgSelection\"]/option");
  private By attrGroupNameLocator = By.xpath("//*[@id=\"mainContent\"]/div/h1");
  private By moreButtonLocator    = By.xpath("//*[@id=\"transactionbar\"]/div/a[2]");
  private By deleteButtonLocator  = By.xpath("//*[@id=\"transactionDelete\"]");

  /**
   * Default constructor.
   * @param driver
   */
  public AttributesGroupOverviewPage(WebDriver driver) {
    super(driver);
  }

  public String getAttributesGroupName() {
    return driver.findElement(attrGroupNameLocator).getText();
  }

  /**
   * Returns all attribute gropus as Hashmap
   * Name is Key and Description is value of hashmap
   * @return map with attribute groups
   */
  public Map<String, String> getAttributesGroupMap() {

    By attrTableLocator = By.xpath("//*[@id=\"componentModel.containedAttributeTypesModel.htmlId\"]/tbody/tr");
    By attrNameLocator = By.xpath("td[2]");
    By attrDescriptionLocator = By.xpath("td[3]");

    Map<String, String> map = new HashMap<String, String>();

    List<WebElement> table = driver.findElements(attrTableLocator);

    //iterate through all tr elements (= rows) of the table
    for (WebElement tr : table) {

      String name = tr.findElement(attrNameLocator).getText();
      String description = tr.findElement(attrDescriptionLocator).getText();

      map.put(name, description);
    }

    return map;

  }

  /**
   * Selects (and clicks) an attribute group entry from the list displayed in the attributes group overview page
   * @param name name of attribute group
   * @return the current page
   */
  public AttributesGroupOverviewPage selectAttributeGroupByName(String name) {

    //find all options entry in list
    List<WebElement> optionList = driver.findElements(optionGroupLocator);

    for (WebElement current : optionList) {

      if (current.getText().equals(name)) {
        current.click();
        break;
      }
    }

    return this;
  }

  public void clickDelete() {
    clickMore();
    driver.findElement(deleteButtonLocator).click();
  }

  private void clickMore() {
    driver.findElement(moreButtonLocator).click();
  }

}
