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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import de.iteratec.iteraplan.webtests.poc.page.AbstractContentPage;
import de.iteratec.iteraplan.webtests.poc.page.WaitUtils;


/**
 * Detail Page for a specific Building Block
 */
public class BuildingBlockDetailPage extends AbstractContentPage {

  private By bbTitleLocator       = By.xpath("//*[@id=\"mainContent\"]/div[1]/h1");
  private By bbDescriptionLocator = By.xpath("//*[@id=\"mainContent\"]/div[1]/div[2]");

  private By closeButtonLocator   = By.id("transactionClose");
  private By editButtonLocator    = By.id("transactionEdit");

  private By menu_MoreLocator     = By.xpath("//*[@id=\"transactionbar\"]/div/*//i[@class=\"icon-cog\"]/..");

  private By menu_DeleteLocator   = By.id("transactionDelete");

  private By attributesTabLocator = By.xpath("//*[@id=\"tab_TabAttributes\"]/a");

  private By subsytemLocator      = By.xpath("//*[@id=\"componentModel.childrenModel.htmlId_table\"]/tbody/tr/td[1]/a");

  /*
   * BO
   */
  private By parentBOLocator      = By.xpath("//*[@id=\"TabHierarchyChildrenWithParentModul\"]/div/div/div/div/div/a");

  public BuildingBlockDetailPage(WebDriver driver) {
    super(driver);
  }

  public String getBBTitle() {
    return driver.findElement(bbTitleLocator).getText();
  }

  public String getBBDescription() {
    return driver.findElement(bbDescriptionLocator).getText();
  }

  public void closeDetailPage() {
    driver.findElement(closeButtonLocator).click();
  }

  public void editBBEntry() {
    driver.findElement(editButtonLocator).click();
  }

  public void deleteBBEntry() {
    driver.findElement(menu_MoreLocator).click();
    driver.findElement(menu_DeleteLocator).click();
  }

  /**
   * Returns the attribute value for a building block for its given attribute group and attribute name
   * @param attributeGroup the name of the attribute group
   * @param attributeName the name of the attribute
   * @return value of attribute
   */
  public String getAttributesValue(String attributeGroup, String attributeName) {

    //first click the attributes tab
    driver.findElement(attributesTabLocator).click();

    //wait for tab change (wait probably not necessary here)
    WaitUtils.wait(2000);

    //click the attributes tab
    String attributeGroupXpath = "//a/text()[contains(., '" + attributeGroup + "')]/..";
    By attributesGroupLocator = By.xpath(attributeGroupXpath);
    driver.findElement(attributesGroupLocator).click();

    //wait for sliding menu
    WaitUtils.wait(2000);

    //find the specific label by the attributes name

    String labelXpath = "//*/label/a[contains(text(), '" + attributeName + "')]/../../label/../div";
    By spanLocator = By.xpath(labelXpath);

    return driver.findElement(spanLocator).getText();

  }

  /**
   * Returns the parent Business Object
   * @return parent business object
   */
  public String getParentBO() {
    return driver.findElement(parentBOLocator).getText();
  }

  /**
   * Returns all child elements from a Business Object as Map 
   * Key = name, value = description
   * @return map
   */
  public Map<String, String> getChildBOAsMap() {

    Map<String, String> map = new HashMap<String, String>();

    By boListLocator = By.xpath("//*[@id=\"componentModel.childrenModel.htmlId\"]/tbody/tr");

    By nameLocator = By.xpath("td[2]");
    By descriptionLocator = By.xpath("td[3]");

    List<WebElement> table = driver.findElements(boListLocator);

    //iterate through all tr elements (= rows) of the table
    for (WebElement tr : table) {

      String name = tr.findElement(nameLocator).getText();
      String description = tr.findElement(descriptionLocator).getText();

      map.put(name, description);
    }

    return map;

  }

  /**
   * Returns only the first subsystem for a information system
   * @return name of information system
   */
  public String getSubsystem() {
    return driver.findElement(subsytemLocator).getText();
  }

}
