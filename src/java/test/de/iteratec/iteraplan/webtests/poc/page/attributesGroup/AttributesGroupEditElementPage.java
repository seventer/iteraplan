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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import de.iteratec.iteraplan.webtests.poc.page.AbstractContentPage;


/**
 * This class represents the page for creating an new Attribute Group
 */
public class AttributesGroupEditElementPage extends AbstractContentPage {

  private By nameLocator               = By.id("nameField");
  private By descriptionLocator        = By.id("description_textarea");
  private By saveButtonLocator         = By.id("transactionSave");

  private By addAttributeButtonLocator = By.id("componentModel.containedAttributeTypesModel.htmlId_add");

  /**
   * Default constructor.
   * @param driver
   */
  public AttributesGroupEditElementPage(WebDriver driver) {
    super(driver);
  }

  public AttributesGroupEditElementPage setName(String name) {
    driver.findElement(nameLocator).sendKeys(name);
    return this;
  }

  public AttributesGroupEditElementPage setDescription(String description) {
    driver.findElement(descriptionLocator).sendKeys(description);
    return this;
  }

  public void clickSave() {
    driver.findElement(saveButtonLocator).click();
  }

  /**
   * Adds a list with attributes to an attributes group
   * @param attr list with attributes
   * @return the current page
   */
  public AttributesGroupEditElementPage addAttributes(List<String> attr) {

    int counter = 1;

    for (String str : attr) {

      String respTextXPath = "//*[@id=\"componentModel.containedAttributeTypesModel.htmlId\"]/tbody/tr[" + counter + "]/td[2]/input";
      By respTextLocator = By.xpath(respTextXPath);

      driver.findElement(respTextLocator).click();
      driver.findElement(respTextLocator).sendKeys(str);

      /*
       * The usage of the wait construct is bad. This should be replaced By conditional waiting. It is used at this place,
       * because the popup list must be loaded from the server and after THAT an entry can be selected
       * will be fixed soon (hopefully) 
       */

      synchronized (driver) {
        try {
          driver.wait(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      driver.findElement(respTextLocator).sendKeys(Keys.ARROW_DOWN);
      driver.findElement(respTextLocator).sendKeys(Keys.RETURN);

      synchronized (driver) {
        try {
          driver.wait(2000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      clickAddAttribute();

      counter++;

    }

    return this;

  }

  private void clickAddAttribute() {
    driver.findElement(addAttributeButtonLocator).click();
  }

}
