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
package de.iteratec.iteraplan.webtests.poc.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 * Represents the login page
 */
public class LoginPage extends AbstractPage {

  private By usernameLocator    = By.id("j_username");
  private By passwordLocator    = By.id("j_password");
  private By loginButtonLocator = By.id("loginButtonCursor");
  private By errorMsgLocater    = By.className("errorMsg");

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  /**
   * Set credentials for LoginPage
   * @param username 
   * @param password
   * @return current LoginPage
   */
  public LoginPage setCredentials(String username, String password) {

    driver.findElement(usernameLocator).clear();
    driver.findElement(usernameLocator).sendKeys(username);

    driver.findElement(passwordLocator).clear();
    driver.findElement(passwordLocator).sendKeys(password);
    return this;
  }

  /**
   * Performs a login action
   */
  public void login() {
    driver.findElement(loginButtonLocator).click();
  }

  /**
   * Returns an error message after failed login attempt
   * @return error message
   */
  public String getErrorMessage() {
    return driver.findElement(errorMsgLocater).getText();
  }

}
