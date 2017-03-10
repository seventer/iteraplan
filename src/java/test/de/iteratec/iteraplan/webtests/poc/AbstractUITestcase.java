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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Capabilities;

import de.iteratec.iteraplan.webtests.poc.page.IteraplanUI;
import de.iteratec.iteraplan.webtests.poc.page.LoginPage;
import de.iteratec.iteraplan.webtests.poc.page.OverlayPopupPage;
import de.iteratec.iteraplan.webtests.poc.page.StartPage;
import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesDetailPage;
import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesListEntry;
import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesNewElement1Page;
import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesNewElement1Page.AttributeType;
import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesNewElement2Page;
import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.attributesGroup.AttributesGroupEditElementPage;
import de.iteratec.iteraplan.webtests.poc.page.attributesGroup.AttributesGroupOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockDetailPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockEditElementPage;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockListEntry;
import de.iteratec.iteraplan.webtests.poc.page.buildingblock.BuildingBlockOverviewPage;
import de.iteratec.iteraplan.webtests.poc.page.common.ResultSize;


/**
 * This class contains all functional components, that are used by all UI Tests
 */
public class AbstractUITestcase extends SeleniumMultipleBrowserTest {

  /**
  * This ui object can be used in sub classes
  */
  protected IteraplanUI ui;

  /**
   * Default constructor.
   * @param browser
   * @param browserEnvironment
   */
  public AbstractUITestcase(Capabilities browser, String browserEnvironment) {
    super(browser, browserEnvironment);
    ui = new IteraplanUI(WebDriverManager.getInstance().getDriver()); // use the new Webdriver
  }

  /**
   * Performs a login
   * @param username username
   * @param password password
   */
  protected void login(String username, String password) {
    //login for iterplan
    LoginPage login = ui.login();
    login.setCredentials(username, password).login();

    //goto startpage and assert
    StartPage start = ui.start();
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());
  }

  protected void login() {
    login("system", "password");
  }

  /**
   * Helper Method for creating an Attribute Group with one single Attribute
   * In order to provide more than one attribute, there must be a method which takes a List as Parameter
   * @param group_name
   * @param singleAttribute attribute
   */
  protected void createAttributeGroup(String group_name, String singleAttribute) {
    StartPage start = ui.start();

    start.header().clickAttributesGroup();
    AttributesGroupOverviewPage attr_grp_overview = ui.attrOverviewGroup();

    //create a new group
    attr_grp_overview.leftMenu().clickNewElement();
    AttributesGroupEditElementPage attr_grp_new = ui.attrGroupNewElement();
    attr_grp_new.setName(group_name).addAttributes(Arrays.asList(singleAttribute)).clickSave();

    //assert if attribute exists
    attr_grp_overview = ui.attrOverviewGroup();
    assertEquals(group_name + " ", attr_grp_overview.getAttributesGroupName());
    assertNotNull(attr_grp_overview.getAttributesGroupMap().get(singleAttribute));

    //goto main screen
    attr_grp_overview.header().clickIteraplanLogo();
    start = ui.start();
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());
  }

  /**
   * Create IS and assign a value to an attribute in an attribute group
   * @param name name of is
   * @param version version of is
   * @param description description of is
   * @param attr_grp_name the name of the attribute group
   * @param attr_name the name of the attribute
   * @param attr_value the value of the attribute
   */
  protected void createInformationSystem(String name, String version, String description, String attr_grp_name, String attr_name, String attr_value) {
    StartPage start = ui.start();

    //goto information system view and set page result to all
    start.header().clickInformationSystem();
    BuildingBlockOverviewPage is_list = ui.buildingBlockOverview();
    is_list.setPageResult(ResultSize.ALL);

    //create a new IS element
    is_list.leftMenu().clickNewElement();
    BuildingBlockEditElementPage is_new = ui.buildingBlockNewElement();
    is_new.setISName(name)//
        .setISVersion(version)//
        .setISDescription(description)//
        .setISAttribute(attr_grp_name, attr_name, attr_value)//
        .saveElement();

    //goto detail page for the recently created entry and assert
    BuildingBlockDetailPage is_detail = ui.buildingBlockDetail();
    assertEquals(name + " # " + version + " ", is_detail.getBBTitle());
    assertEquals(description, is_detail.getBBDescription());
    assertEquals(attr_value, is_detail.getAttributesValue(attr_grp_name, attr_name));
    is_detail.closeDetailPage();

    //goto main screen
    is_list = ui.buildingBlockOverview();
    is_list.header().clickIteraplanLogo();
    start = ui.start();
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());
  }

  /**
   * Creates an information system without assigning attribute values
   * @param name name of is
   * @param version version of is
   * @param description description of is
   */
  protected void createInformationSystem(String name, String version, String description) {
    StartPage start = ui.start();

    //goto information system view and set page result to all
    start.header().clickInformationSystem();
    BuildingBlockOverviewPage is_list = ui.buildingBlockOverview();
    is_list.setPageResult(ResultSize.ALL);

    //create a new IS element
    is_list.leftMenu().clickNewElement();
    BuildingBlockEditElementPage is_new = ui.buildingBlockNewElement();
    is_new.setISName(name).setISVersion(version).setISDescription(description).saveElement();

    //goto detail page for the recently created entry and assert
    BuildingBlockDetailPage is_detail = ui.buildingBlockDetail();
    assertEquals(name + " # " + version + " ", is_detail.getBBTitle());
    assertEquals(description, is_detail.getBBDescription());
    is_detail.closeDetailPage();

    //goto main screen
    is_list = ui.buildingBlockOverview();
    is_list.header().clickIteraplanLogo();
    start = ui.start();
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());
  }

  /**
   * Creates an Information System but with an Attribute with a Combo Entry (for example enum or responsibility)
   * Combo Entries have to be treated different than plain text fields
   * @param name name of is
   * @param version version of is
   * @param description description of is
   * @param attr_grp_name the name of the attribute group
   * @param attr_name the name of the attribute
   * @param attr_value the value of the attribute
   */
  protected void createInformationSystemWithComboEntry(String name, String version, String description, String attr_grp_name, String attr_name,
                                                       String attr_value) {
    StartPage start = ui.start();

    //goto information system view and set page result to all
    start.header().clickInformationSystem();
    BuildingBlockOverviewPage is_list = ui.buildingBlockOverview();
    is_list.setPageResult(ResultSize.ALL);

    //create a new IS element
    is_list.leftMenu().clickNewElement();
    BuildingBlockEditElementPage is_new = ui.buildingBlockNewElement();
    is_new.setISName(name)//
        .setISVersion(version)//
        .setISDescription(description)//
        .setISAttributeFromComboBox(attr_grp_name, attr_name, attr_value)//
        .saveElement();

    //goto detail page for the recently created entry and assert
    BuildingBlockDetailPage is_detail = ui.buildingBlockDetail();
    assertEquals(name + " # " + version + " ", is_detail.getBBTitle());
    assertEquals(description, is_detail.getBBDescription());
    assertEquals(attr_value, is_detail.getAttributesValue(attr_grp_name, attr_name));
    is_detail.closeDetailPage();

    //goto main screen
    is_list = ui.buildingBlockOverview();
    is_list.header().clickIteraplanLogo();
    start = ui.start();
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());
  }

  /**
   * Deletes an information system for the given name and version
   * @param name name of is
   * @param version version of is
   */
  protected void deleteInformationSystem(String name, String version) {

    StartPage start = ui.start();

    //goto information system view and set page result to all
    start.header().clickInformationSystem();
    BuildingBlockOverviewPage is_list = ui.buildingBlockOverview();
    is_list.setPageResult(ResultSize.ALL);

    //goto list of all IS page and open the item again
    BuildingBlockListEntry bb_entry = is_list.getListEntriesAsMap().get(name + " # " + version);
    assertNotNull(bb_entry);
    bb_entry.openDetail();

    //delete the created entry
    BuildingBlockDetailPage is_detail = ui.buildingBlockDetail();
    is_detail = ui.buildingBlockDetail();
    is_detail.deleteBBEntry();

    //modal popup for deleting
    OverlayPopupPage del_popup = ui.popup();
    del_popup.clickOK();

    //goto information system list and assert if IS does not exist
    is_list = ui.buildingBlockOverview();
    assertNull(is_list.getListEntriesAsMap().get(name + " # " + version));

    //goto start page
    is_list.header().clickIteraplanLogo();
    start = ui.start();
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());
  }

  /**
   * Deletes an attribute by name 
   * @param attrName the name of attribute, which will be deleted
   */
  protected void deleteAttribute(String attrName) {

    StartPage start = ui.start();
    start = ui.start();
    start.header().clickAttributes();

    //Attributes Overview
    AttributesOverviewPage attrOverview = ui.attrOverview();
    attrOverview = ui.attrOverview();
    assertEquals("Merkmale : Übersicht - iteraplan", attrOverview.getPageTitle());
    int numberOfEntries_before = attrOverview.getNumberOfAttributesFromGUI();
    attrOverview.setPageResult(ResultSize.ALL);

    //Get hashmap of all attributes and open the detail page
    Map<String, AttributesListEntry> map = attrOverview.getListEntriesAsMap();
    AttributesListEntry entry = map.get(attrName);
    assertNotNull(entry);
    entry.openDetail();

    //Delete from Detail Page
    AttributesDetailPage attrDetail = ui.attrDetail();
    attrDetail = ui.attrDetail();
    attrDetail.clickDelete();
    OverlayPopupPage deletePopup = ui.popup();
    deletePopup.clickOK();

    attrOverview = ui.attrOverview();
    assertEquals(numberOfEntries_before - 1, attrOverview.getNumberOfAttributesFromGUI());
    assertNull(attrOverview.getListEntriesAsMap().get(attrName));

    //goto main screen
    attrOverview.header().clickIteraplanLogo();
    start = ui.start();
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());

  }

  /**
   * Deletes an attribute group
   * @param attributeGroupname the name of the attribute group which will be deleted
   */
  protected void deleteAttributeGroup(String attributeGroupname) {

    StartPage start = ui.start();
    start.header().clickAttributesGroup();

    AttributesGroupOverviewPage attrGroup = ui.attrOverviewGroup();
    attrGroup.selectAttributeGroupByName(attributeGroupname).clickDelete();

    OverlayPopupPage popup = ui.popup();
    popup.clickOK();

    attrGroup = ui.attrOverviewGroup();
    assertNull(attrGroup.getAttributesGroupMap().get(attributeGroupname));

    attrGroup.header().clickIteraplanLogo();

  }

  /**
   * Creates an business object
   * @param name name of bo
   * @param desc description of bo
   */
  protected void createBusinessObject(String name, String desc) {

    //goto business objects page
    StartPage start = ui.start();
    start.header().clickBusinessObjects();
    BuildingBlockOverviewPage bd_list = ui.buildingBlockOverview();

    //create new bo
    bd_list.leftMenu().clickNewElement();
    BuildingBlockEditElementPage bd_new = ui.buildingBlockNewElement();
    bd_new.setBOName(name).setBODescription(desc).saveElement();

    //assert details
    BuildingBlockDetailPage bd_detail = ui.buildingBlockDetail();
    assertEquals(name + " ", bd_detail.getBBTitle());
    assertEquals(desc, bd_detail.getBBDescription());

    bd_detail.header().clickIteraplanLogo();
    start = ui.start();
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());

  }

  /**
   * Creates a "simple" Attribute
   * Simple Attributes have one of these Types: DATE, TEXT, NUMERIC
   * @param type type of attribute (see list above)
   * @param attributeName name of attribute
   * @param attributeDescription description of attribute
   * @param attributeBBT the BBT type for which the attribute is created
   */
  protected void createSimpleAttribute(AttributeType type, String attributeName, String attributeDescription, String attributeBBT) {

    navigateToAttributeCreation(type);

    AttributesNewElement2Page attrPage2 = ui.attrNewElement2();
    attrPage2.setName(attributeName).setDescription(attributeDescription).setBBT(attributeBBT).clickSave();

    saveAndAssertAttribute(attributeName, attributeDescription);
  }

  /**
   * Creates a enumeration attribute
   * @param attributeName name of attribute
   * @param attributeDescription description of attribute
   * @param attributeBBT the BBT type for which the attribute is created
   * @param valueList list of enums (Strings)
   */
  protected void createEnumerationAttribute(String attributeName, String attributeDescription, String attributeBBT, List<String> valueList) {

    navigateToAttributeCreation(AttributeType.ENUM);

    AttributesNewElement2Page attrPage2 = ui.attrNewElement2();
    attrPage2.setName(attributeName).setDescription(attributeDescription).setBBT(attributeBBT).addEnumerations(valueList).clickSave();

    saveAndAssertAttribute(attributeName, attributeDescription);

  }

  /**
   * Creates a responsibility attribute
   * @param attributeName name of attribute
   * @param attributeDescription description of attribute
   * @param attributeBBT the BBT type for which the attribute is created
   * @param valueList list of responsibilities (Strings)
   */
  protected void createResponsibilityAttribute(String attributeName, String attributeDescription, String attributeBBT, List<String> valueList) {

    navigateToAttributeCreation(AttributeType.RESP);

    AttributesNewElement2Page attrPage2 = ui.attrNewElement2();
    attrPage2.setName(attributeName).setDescription(attributeDescription).setBBT(attributeBBT).addResponsibilities(valueList).clickSave();

    saveAndAssertAttribute(attributeName, attributeDescription);

  }

  /**
   * Navigates from the start page to the Attributes Page and open the New Attribute Page for the given Attribute Type Parameter
   * @param type type of attribute
   */
  private void navigateToAttributeCreation(AttributeType type) {

    StartPage start = ui.start();

    //Attributes Overview
    start.header().clickAttributes();
    AttributesOverviewPage attrOverview = ui.attrOverview();
    assertEquals("Merkmale : Übersicht - iteraplan", attrOverview.getPageTitle());
    attrOverview.setPageResult(ResultSize.ALL);
    attrOverview.leftMenu().clickNewElement();

    AttributesNewElement1Page attrPage1 = ui.attrNewElement1();
    attrPage1.selectAttributeType(type).clickNext();

  }

  /**
   * Creates a attribute with the given name and description and then navigates to the start screen
   * @param attributeName name of attribute 
   * @param attributeDescription description of attribute
   */
  private void saveAndAssertAttribute(String attributeName, String attributeDescription) {

    StartPage start = ui.start();

    //Attributes Detail page
    AttributesDetailPage attrDetail = ui.attrDetail();
    assertEquals(attributeName + " ", attrDetail.getAttributeName());
    assertEquals(attributeDescription, attrDetail.getAttributeDescription());
    attrDetail.clickClose();

    AttributesOverviewPage attrOverview = ui.attrOverview();
    assertNotNull(attrOverview.getListEntriesAsMap().get(attributeName));

    //goto main screen
    attrOverview.header().clickIteraplanLogo();
    start = ui.start();
    assertEquals("Willkommen bei iteraplan - iteraplan", start.getPageTitle());

  }

}
