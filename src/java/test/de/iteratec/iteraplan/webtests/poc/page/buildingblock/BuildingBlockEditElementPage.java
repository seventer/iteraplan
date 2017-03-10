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

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

import de.iteratec.iteraplan.webtests.poc.page.AbstractContentPage;
import de.iteratec.iteraplan.webtests.poc.page.WaitUtils;


/**
 * Represents the Page for Creating a new Building Block
 * Because there are differences between the single building blocks, there must be extra methods for creating  
 * different building blocks
 * 
 * 
 */
public class BuildingBlockEditElementPage extends AbstractContentPage {

  /*
   * General
   */
  private By saveButtonLocator         = By.id("transactionSave");
  private By attributesTabLocator      = By.xpath("//*[@id=\"tab_TabAttributes\"]/a");
  private By errorStringLocator        = By.id("memBean.errors");
  private By cancelButtonLocator       = By.id("transactionCancel");

  /*
   * Information System
   */
  private By releaseNameLocator        = By.id("releaseName_element_text");
  private By releaseVersionLocator     = By.id("releaseName_release_text");
  private By releaseDescriptionLocator = By.id("description_textarea");
  private By subsystemLocator          = By.xpath("//*[@id=\"componentModel.childrenModel.htmlId_table\"]/tbody/tr/td[2]/input");
  private By subsystemAddLocator       = By.xpath("//*[@id=\"componentModel.childrenModel.htmlId_add\"]");

  /*
   * Business Object
   */
  private By boNameLocator             = By.id("nameField");
  private By boDescLocator             = By.id("desc_textarea");
  private By boParentLocator           = By.xpath("//*[@id=\"TabHierarchyChildrenWithParentModul\"]/div/div/div/div/div/input");
  private By boAddChildButtonLocator   = By.id("componentModel.childrenModel.htmlId_add");

  public BuildingBlockEditElementPage(WebDriver driver) {
    super(driver);
  }

  /**
   * Fills the form for a new Information System
   * @param name
   * @param release
   * @param description
   */
  public void createNewInformationSystem(String name, String release, String description) {
    setISName(name).setISVersion(release).setISDescription(description).saveElement();
  }

  public BuildingBlockEditElementPage setISName(String name) {
    driver.findElement(releaseNameLocator).sendKeys(name);
    return this;
  }

  public BuildingBlockEditElementPage setISVersion(String version) {
    driver.findElement(releaseVersionLocator).sendKeys(version);
    return this;
  }

  public BuildingBlockEditElementPage setISDescription(String description) {
    driver.findElement(releaseDescriptionLocator).sendKeys(description);
    return this;
  }

  /**
   * Sets Attributes for an Information System (Only Text, Numeric, Date)
   * @param attributeGroup name of attribute group
   * @param attributeName name of attribute
   * @param attributeValue the value of attribute
   * @return the current building block new element page
   */
  public BuildingBlockEditElementPage setISAttribute(String attributeGroup, String attributeName, String attributeValue) {

    //first click the attributes tab
    driver.findElement(attributesTabLocator).click();

    //click the attributes tab
    String attributeGroupXpath = "//a/text()[contains(., '" + attributeGroup + "')]/..";
    By attributesGroupLocator = By.xpath(attributeGroupXpath);
    driver.findElement(attributesGroupLocator).click();

    //find the specific label by the attributes name
    String labelXpath = "//*/label/a[contains(text(), '" + attributeName + "')]/../../label/..//input";
    By inputLocator = By.xpath(labelXpath);

    driver.findElement(inputLocator).click();
    driver.findElement(inputLocator).sendKeys(attributeValue);

    return this;
  }

  /**
   * This method is used to set a value for a IS in a combo box. The difference between this method and the setISAttribute method is, 
   * that setISAttribute doesn't have the logic to select an entry from a popup. This popup is shown, if an accepted value is insert in the combo box.
   * @param attributeGroup name of attribute group
   * @param attributeName name of attribute
   * @param attributeValue the value of attribute
   * @return the Building Block New Element Page
   */
  public BuildingBlockEditElementPage setISAttributeFromComboBox(String attributeGroup, String attributeName, String attributeValue) {
    //first click the attributes tab
    driver.findElement(attributesTabLocator).click();

    //click the attributes tab
    String attributeGroupXpath = "//a/text()[contains(., '" + attributeGroup + "')]/..";
    By attributesGroupLocator = By.xpath(attributeGroupXpath);
    driver.findElement(attributesGroupLocator).click();

    //find the specific label by the attributes name
    String labelXpath = "//*/label/a[contains(text(), '" + attributeName + "')]/../../label/..//input";
    By inputLocator = By.xpath(labelXpath);

    driver.findElement(inputLocator).click();
    driver.findElement(inputLocator).sendKeys(attributeValue);

    /*
     * These wait conditions are bad. But there must be a waiting time for showing the popup and selecting an item
     * This could be solved much better if there are conditional waitings ("is popup visible, otherwise wait")
     * Will be fixed soon (hopefully)
     * 
     */

    WaitUtils.wait(2000);

    driver.findElement(inputLocator).sendKeys(Keys.ARROW_DOWN);

    WaitUtils.wait(2000);

    driver.findElement(inputLocator).sendKeys(Keys.RETURN);

    return this;

  }

  public BuildingBlockEditElementPage setBOName(String name) {
    driver.findElement(boNameLocator).sendKeys(name);
    return this;
  }

  public BuildingBlockEditElementPage setBODescription(String desc) {
    driver.findElement(boDescLocator).sendKeys(desc);
    return this;
  }

  /**
   * Sets a parent Business Object for a new Business Object
   * @param parent
   * @return current building block new element page
   */
  public BuildingBlockEditElementPage setParentBO(String parent) {

    //first clear the textfield
    driver.findElement(boParentLocator).clear();

    driver.findElement(boParentLocator).sendKeys(parent);

    /*
     * These wait conditions are bad. But there must be a waiting time for showing the popup and selecting an item
     * This could be solved much better if there are conditional waitings ("is popup visible, otherwise wait")
     * Will be fixed soon (hopefully)
     * 
     */

    WaitUtils.wait(2000);

    driver.findElement(boParentLocator).sendKeys(Keys.ARROW_DOWN);

    WaitUtils.wait(2000);

    driver.findElement(boParentLocator).sendKeys(Keys.RETURN);

    return this;

  }

  /**
   * Adds childs of Business Objects to a new BusinessObject
   * @param childs
   * @return current building block new element page
   */
  public BuildingBlockEditElementPage addChildBOs(List<String> childs) {

    int counter = 1;

    for (String str : childs) {

      String childInputXPath = "//*[@id=\"componentModel.childrenModel.htmlId\"]/tbody/tr[" + counter + "]/td[2]/input";

      By childInputLocator = By.xpath(childInputXPath);

      driver.findElement(childInputLocator).click();
      driver.findElement(childInputLocator).sendKeys(str);

      /*
       * The usage of the wait construct is bad. This should be replaced by conditional waiting. It is used at this place,
       * because the popup list must be loaded from the server and after THAT an entry can be selected
       * will be fixed soon (hopefully) 
       */

      WaitUtils.wait(2000);

      driver.findElement(childInputLocator).sendKeys(Keys.ARROW_DOWN);
      driver.findElement(childInputLocator).sendKeys(Keys.RETURN);

      WaitUtils.wait(2000);

      clickAddBOChild();

      counter++;

    }

    return this;

  }

  /**
   * Sets a subsystem for a Information System
   * This method only accepts one subsystem. It must be extended to use a list as parameter
   * @param sub the name of the subsystem
   * @return current building block new element page
   */
  public BuildingBlockEditElementPage setContainingSubsystem(String sub) {

    driver.findElement(subsystemLocator).click();
    driver.findElement(subsystemLocator).sendKeys(sub);

    /*
     * The usage of the wait construct is bad. This should be replaced by conditional waiting. It is used at this place,
     * because the popup list must be loaded from the server and after THAT an entry can be selected
     * will be fixed soon (hopefully) 
     */

    WaitUtils.wait(2000);

    driver.findElement(subsystemLocator).sendKeys(Keys.ARROW_DOWN);
    driver.findElement(subsystemLocator).sendKeys(Keys.RETURN);

    WaitUtils.wait(2000);

    clickAddSubsystem();

    return this;

  }

  /**
   * Returns the error text if an error has occured
   * @return error string
   */
  public String getErrorMessage() {
    return driver.findElement(errorStringLocator).getText();
  }

  public void saveElement() {
    WaitUtils.wait(300); //safe wait for fields
    driver.findElement(saveButtonLocator).click();
  }

  public void cancel() {
    driver.findElement(cancelButtonLocator).click();
  }

  private void clickAddBOChild() {
    driver.findElement(boAddChildButtonLocator).click();
  }

  private void clickAddSubsystem() {
    driver.findElement(subsystemAddLocator).click();
  }

}
