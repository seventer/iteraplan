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
package de.iteratec.iteraplan.webtests.poc;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;


/**
 * Singleton which manages the Webdriver Manager
 */
public final class WebDriverManager {

  private static final Logger           LOGGER            = Logger.getLogger(WebDriverManager.class);

  private static final String           selenium_hub_url  = "iteraplan.iteratec.de";
  private static final String           selenium_hub_port = "4444";
  public static final int               WAIT_TIMEOUT      = 30;

  private static final WebDriverManager INSTANCE          = new WebDriverManager();

  private WebDriver                     driver;
  private Capabilities                  browser_capabilities;

  public static WebDriverManager getInstance() {
    return INSTANCE;
  }

  public void configure(Capabilities browser) {
    this.browser_capabilities = browser;
  }

  /**
   * Returns the webdriver instance
   * @return webdriver instance
   */
  public WebDriver getDriver() {
    if (browser_capabilities == null) {
      LOGGER.error("You need capabilities first!");
      return null;
    }
    else if (driver == null) {
      try {
        Capabilities capabilities = new DesiredCapabilities(browser_capabilities);
        driver = new RemoteWebDriver(new URL("http://" + selenium_hub_url + ":" + selenium_hub_port + "/wd/hub"), capabilities);
        driver.manage().timeouts().implicitlyWait(WAIT_TIMEOUT, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver = new Augmenter().augment(driver); //the driver can now take Screenshots
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } finally {
        LOGGER.info("Returning the instance of webdriver");
      }
    }
    return driver;
  }

  /**
   * Stops the webdriver
   */
  public void stopDriver() {
    LOGGER.info("Closing the webdriver...");
    if (driver != null) {
      driver.close();
      driver = null; //important otherwise a new driver instance won't be created!
    }

  }

  /**
   * Sets the driver timeout in seconds
   * @param timeout
   */
  public void setDriverTimeout(int timeout) {
    if (driver != null) {
      driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
    }
  }

}
