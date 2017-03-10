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
package de.iteratec.iteraplan.webtests.poc.page.interfaces;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import de.iteratec.iteraplan.webtests.poc.page.AbstractContentPage;
import de.iteratec.iteraplan.webtests.poc.page.WaitUtils;


/**
 * Represents the new element page for interfaces
 */
public class InterfacesEditElementPage extends AbstractContentPage {

  private By nameLocator       = By.id("nameField");

  private By leftISLocator     = By.xpath("//*[@id=\"InfoNewInterfaceModul\"]/div/div/div/table/tbody/tr[1]/td[1]/input");
  private By rightISLocator    = By.xpath("//*[@id=\"InfoNewInterfaceModul\"]/div/div/div/table/tbody/tr[1]/td[3]/input");
  private By directionLocator  = By.id("isiDirection_selectDirection");

  private By saveButtonLocator = By.id("transactionSave");

  /**
   * Default constructor.
   * @param driver
   */
  public InterfacesEditElementPage(WebDriver driver) {
    super(driver);
  }

  public InterfacesEditElementPage setName(String name) {
    driver.findElement(nameLocator).sendKeys(name);
    return this;
  }

  public InterfacesEditElementPage setLeftIS(String name) {

    driver.findElement(leftISLocator).clear();
    driver.findElement(leftISLocator).click();
    driver.findElement(leftISLocator).sendKeys(name);

    WaitUtils.wait(2000);

    driver.findElement(leftISLocator).sendKeys(Keys.ARROW_DOWN);
    driver.findElement(leftISLocator).sendKeys(Keys.RETURN);

    WaitUtils.wait(2000);

    return this;
  }

  public InterfacesEditElementPage setRightIS(String name) {

    driver.findElement(rightISLocator).clear();
    driver.findElement(rightISLocator).click();
    driver.findElement(rightISLocator).sendKeys(name);

    WaitUtils.wait(2000);

    driver.findElement(rightISLocator).sendKeys(Keys.ARROW_DOWN);
    driver.findElement(rightISLocator).sendKeys(Keys.RETURN);

    WaitUtils.wait(2000);

    return this;
  }

  /**
   * Selects the direction for a interface relationship
   * @param direction direction enum
   * @return current page
   */
  public InterfacesEditElementPage setDirection(IFDirection direction) {

    driver.findElement(directionLocator).click();

    switch (direction) {

      case LEFT:
        driver.findElement(directionLocator).sendKeys("<");
        break;

      case RIGHT:
        driver.findElement(directionLocator).sendKeys(">");
        break;

      case BIDIRECTIONAL:
        driver.findElement(directionLocator).sendKeys("<");
        driver.findElement(directionLocator).sendKeys(Keys.DOWN);
        break;

      default:
        driver.findElement(directionLocator).sendKeys("-");
        break;

    }

    return this;
  }

  public void clickSave() {
    driver.findElement(saveButtonLocator).click();
  }

}
