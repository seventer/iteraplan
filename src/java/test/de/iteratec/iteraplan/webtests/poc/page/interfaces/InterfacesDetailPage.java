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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import de.iteratec.iteraplan.webtests.poc.page.AbstractContentPage;


/**
 * Represents the detail page for interfaces
 */
public class InterfacesDetailPage extends AbstractContentPage {

  private By nameLocator         = By.xpath("//*[@id=\"mainContent\"]/div[1]/h1");
  private By leftISLocator       = By.xpath("//*[@id=\"InfoInterfaceModul\"]/div/div/div/table/tbody/tr[1]/td[1]/a");
  private By rightISLocator      = By.xpath("//*[@id=\"InfoInterfaceModul\"]/div/div/div/table/tbody/tr[1]/td[3]/a");

  private By directionLocator    = By.xpath("//*[@id=\"InfoInterfaceModul\"]/div/div/div/table/tbody/tr[1]/td[2]/img");

  private By editButtonLocator   = By.id("transactionEdit");

  private By moreButtonLocator   = By.xpath("//*[@id=\"transactionbar\"]/div/a[2]");
  private By deleteButtonLocator = By.id("transactionDelete");

  /**
   * Default constructor.
   * @param driver
   */
  public InterfacesDetailPage(WebDriver driver) {
    super(driver);
  }

  public String getName() {
    return driver.findElement(nameLocator).getText();
  }

  public String getLeftIS() {
    return driver.findElement(leftISLocator).getText();
  }

  public String getRightIS() {
    return driver.findElement(rightISLocator).getText();
  }

  /**
   * Returns the direction of a relationship between two interfaces
   * As the relationship is represented by images, the src of the images must be checked 
   * @return direction of relationship
   */
  public IFDirection getDirection() {

    List<WebElement> imgList = driver.findElements(directionLocator);

    //first check if list has two elements -> then return bidirectional
    if (imgList.size() == 2) {
      return IFDirection.BIDIRECTIONAL;
    }

    String imgSrc = imgList.get(0).getAttribute("src");

    if (imgSrc.contains("icon-left.png")) {
      return IFDirection.LEFT;
    }

    //then it must be right
    return IFDirection.RIGHT;

  }

  public void clickEdit() {
    driver.findElement(editButtonLocator).click();
  }

  public void clickDelete() {
    driver.findElement(moreButtonLocator).click();
    driver.findElement(deleteButtonLocator).click();
  }

}
