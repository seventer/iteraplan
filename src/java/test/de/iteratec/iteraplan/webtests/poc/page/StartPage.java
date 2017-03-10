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
 * Represents the start page after you have performed a login ("The page with the tiles")
 */
public class StartPage extends AbstractContentPage {

  private By businessdomainLocator          = By.id("businessdomain");
  private By businessprocessdomainLocator   = By.id("businessprocess");
  private By businessUnitLocator            = By.id("businessunit");
  private By productLocator                 = By.id("product");
  private By businessMappingLocator         = By.id("businessmapping");
  private By businessFunctionLocator        = By.id("businessfunction");
  private By projectLocator                 = By.id("project");
  private By businessObjectLocator          = By.id("businessobject");
  private By informationSystemLocator       = By.id("informationsystem");
  private By informationSystemDomainLocator = By.id("informationsystemdomain");
  private By interfaceLocator               = By.id("interface");
  private By architecturalDomainLocator     = By.id("architecturaldomain");
  private By technicalComponentLocator      = By.id("technicalcomponent");
  private By infrastructureElementLocator   = By.id("infrastructureelement");

  public StartPage(WebDriver driver) {
    super(driver);
  }

  /*
   * Functions for Start Page
   */

  public void clickBusinessDomain() {
    driver.findElement(businessdomainLocator).click();
  }

  public void clickBusinessProcess() {
    driver.findElement(businessprocessdomainLocator).click();
  }

  public void clickBusinessUnit() {
    driver.findElement(businessUnitLocator).click();
  }

  public void clickProduct() {
    driver.findElement(productLocator).click();
  }

  public void clickBusinessMapping() {
    driver.findElement(businessMappingLocator).click();
  }

  public void clickBusinessFunction() {
    driver.findElement(businessFunctionLocator).click();
  }

  public void clickProject() {
    driver.findElement(projectLocator).click();
  }

  public void clickBusinessObject() {
    driver.findElement(businessObjectLocator).click();
  }

  public void clickInformationSystem() {
    driver.findElement(informationSystemLocator).click();
  }

  public void clickInformationSystemDomain() {
    driver.findElement(informationSystemDomainLocator).click();
  }

  public void clickInterface() {
    driver.findElement(interfaceLocator).click();
  }

  public void clickArchitecturalDomain() {
    driver.findElement(architecturalDomainLocator).click();
  }

  public void clickTechnicalComponent() {
    driver.findElement(technicalComponentLocator).click();
  }

  public void clickInfrastructureElement() {
    driver.findElement(infrastructureElementLocator).click();
  }
}
