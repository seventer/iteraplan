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

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.DesiredCapabilities;


/**
* This class is used for running multiple selenium tests on a cluster
 */
@RunWith(Parameterized.class)
public abstract class SeleniumMultipleBrowserTest {

  private static final Logger LOGGER                 = Logger.getLogger(SeleniumMultipleBrowserTest.class);
  private static final String jenkins_url            = "https://iteraplan.iteratec.de/hudson/job/nightly_webtests/ws/";
  protected final String      baseurl;
  private static final String BROWSER_DELIMITER      = ",";
  private static final String VERSION_DELIMITER      = "-";
  private static final String BROWSER_PROPERTY_NAME  = "browserEnvironment";                                           //Name of the property which browsers and versions should be used.
  private static final String BASE_URL_PROPERTY_NAME = "baseUrlSelenium";                                              //Name of the property where the iteraplan distribution is available is at

  private final String        browserEnvironment;

  public SeleniumMultipleBrowserTest(Capabilities browser, String browserEnvironment) {
    String url = System.getProperty(BASE_URL_PROPERTY_NAME);
    if (url == null || url.equals("")) {
      baseurl = "https://iteraplan.iteratec.de/iteraplan_nightly_build";
    }
    else {
      baseurl = url;
    }
    this.browserEnvironment = browserEnvironment;

    //configure the Singleton WebDriver in Constructor
    WebDriverManager.getInstance().configure(browser);
  }

  /**
   * This method reads the browser configuration out of the system properties.
   * System property for browser = browserEnvironment
   * Multiple browsers are separated by a "," (no spaces).
   * If no browser configuration exists, Firefox is chosen as default browser.
   * Throws {@link NullPointerException} if the browserEnvironment is not set
   * @return A list of browsers to test against
   */
  @Parameters(name = "{1}")
  public static List<Object[]> browserConfiguration() {
    List<Object[]> browsers = new ArrayList<Object[]>();

    String capabilities = System.getProperty(BROWSER_PROPERTY_NAME);
    if (capabilities != null) {
      StringTokenizer browserTokenizer = new StringTokenizer(capabilities, BROWSER_DELIMITER);
      while (browserTokenizer.hasMoreTokens()) {
        StringTokenizer versionTokenizer = new StringTokenizer(browserTokenizer.nextToken(), VERSION_DELIMITER);
        String browser = versionTokenizer.nextToken();
        String version = versionTokenizer.hasMoreTokens() ? versionTokenizer.nextToken() : "";
        browsers.add(new Object[] { new DesiredCapabilities(browser, version, Platform.ANY), browser + version });
      }
    }
    if (browsers.size() == 0) {
      //If no environment was found use firefox as default.
      browsers.add(new Object[] { DesiredCapabilities.firefox(), "firefox" });
    }

    return browsers;
  }

  /**
   * The browser environment is the browser name and the browser version
   * @return browserEnvironment the browserEnvironment
   */
  public String getBrowserEnvironment() {
    return browserEnvironment;
  }

  public String getBaseURL() {
    return baseurl;
  }

  /**
   * This is used be the selenium page object tests
   * Webdriver Instance gets closed after a complete run of all tests in a testclass
   */
  @AfterClass
  public static void shutdownDriverAfterClass() {
    WebDriverManager.getInstance().stopDriver();
  }

  /**
   * Testrule for capturing screenshots if an test fails
   * This method also prints out a log message which contains the link to the screenshot. But this link is only valid, if 
   * the jenkins server executes this tests
   */
  @Rule
  public TestRule testWatcher = new TestWatcher() {

                                @Override
                                public void failed(Throwable e, Description d) {

                                  try {
                                    new File("target/surefire-reports/").mkdirs(); // Insure directory is there
                                    FileOutputStream out = new FileOutputStream("target/surefire-reports/screenshot-" + d.getMethodName() + ".png");
                                    out.write(((TakesScreenshot) WebDriverManager.getInstance().getDriver()).getScreenshotAs(OutputType.BYTES));
                                    out.close();

                                    //print the link
                                    String url = "target/surefire-reports/screenshot-" + d.getMethodName() + ".png";

                                    LOGGER.error(jenkins_url + url);

                                  } catch (Exception ex) {
                                    // No need to crash the tests if the screenshot fails
                                    ex.printStackTrace();
                                  }

                                }
                              };

}
