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
package de.iteratec.iteraplan.webtests.poc.pagetests;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Capabilities;

import de.iteratec.iteraplan.webtests.poc.AbstractUITestcase;
import de.iteratec.iteraplan.webtests.poc.WebDriverManager;
import de.iteratec.iteraplan.webtests.poc.page.attributes.AttributesNewElement1Page.AttributeType;


/**
 * This class creates new attributes and new Information Systems with the recently created attributes
 * Every test method has mainly the same logic. In some places there a differences. For Example in the selection of the attribute
 * which should be created (Text, Date, ...) and in assertion logic.
 * 
 * Be careful if you split these long methods in smaller helper methods. Helper methods must be parameterizable, e.g. name, group and value
 * of an attribute, .. etc
 * 
 * This test should substitute the AttributesTest without Page Object Design 
 */
public class AttributesTest extends AbstractUITestcase {

  /**
   * Default constructor.
   * @param browser
   * @param browserEnvironment
   */
  public AttributesTest(Capabilities browser, String browserEnvironment) {
    super(browser, browserEnvironment);
  }

  @Before
  public void setUp() {
    //navigate to login page
    WebDriverManager.getInstance().getDriver().get(baseurl + "/login.do");
  }

  @Test
  /**
  * Creates a new Date Attribute under the Default Attribute Group and add these attribute to an IS
   * After assertions, IS and Attribute will be deleted
   */
  public void testCreateAndDeleteISwithDateAttribute() {

    // local constants definition
    String attributeDateName = "!DateAttribute";
    String attributeDescription = "This is a attribute created by a selenium test.";
    String attributeDateValue = "01.04.1989";

    String attributeBBT = "Informationssystem";

    String attributeDateGroupName = "!DateGroup";

    String informationSystemName = "Information System";
    String informationSystemVersion = "1.0";
    String informationSystemDescription = "This is an information system created by a selenium test.";

    // steps to create an IS with Date Attribute in a new Attribute Group
    login();
    createSimpleAttribute(AttributeType.DATE, attributeDateName, attributeDescription, attributeBBT);
    createAttributeGroup(attributeDateGroupName, attributeDateName);
    createInformationSystem(informationSystemName, informationSystemVersion, informationSystemDescription, attributeDateGroupName, attributeDateName,
        attributeDateValue);
    deleteInformationSystem(informationSystemName, informationSystemVersion);
    deleteAttribute(attributeDateName);
    deleteAttributeGroup(attributeDateGroupName);

  }

  @Test
  /**
   * Creates a new Text Attribute under the Default Attribute Group and add these attribute to an IS
   * After assertions, IS and Attribute will be deleted
   */
  public void testCreateAndDeleteISwithTextAttribute() {

    String attributeBBT = "Informationssystem";

    String informationSystemName = "Information System";
    String informationSystemVersion = "1.0";
    String informationSystemDescription = "This is an information system created by a selenium test.";
    String attributeDescription = "This is a attribute created by a selenium test.";

    String attributeTextGroupName = "!TextGroup";

    String attributeTextName = "!TextAttribute";
    String attributeTextValue = "filled by selenium";

    login();
    createSimpleAttribute(AttributeType.TEXT, attributeTextName, attributeDescription, attributeBBT);
    createAttributeGroup(attributeTextGroupName, attributeTextName);
    createInformationSystem(informationSystemName, informationSystemVersion, informationSystemDescription, attributeTextGroupName, attributeTextName,
        attributeTextValue);
    deleteInformationSystem(informationSystemName, informationSystemVersion);
    deleteAttribute(attributeTextName);
    deleteAttributeGroup(attributeTextGroupName);

  }

  @Test
  /**
   * Creates a new numeric Attribute under the Default Attribute Group and add these attribute to an IS
   * After assertions, IS and Attribute will be deleted
   */
  public void testCreateAndDeleteISwithNumericAttribute() {

    String attributeBTT = "Informationssystem";

    String informationSystemName = "Information System";
    String informationSystemVersion = "1.0";
    String informationSystemDescription = "This is an information system created by a selenium test.";
    String attributeDescription = "This is a attribute created by a selenium test.";

    String attributeNumericName = "!NumberAttribute";

    String attributeNumericGroupName = "!NumberGroup";
    String attributeNumericValue = "42,00";

    login();
    createSimpleAttribute(AttributeType.NUMERIC, attributeNumericName, attributeDescription, attributeBTT);
    createAttributeGroup(attributeNumericGroupName, attributeNumericName);
    createInformationSystem(informationSystemName, informationSystemVersion, informationSystemDescription, attributeNumericGroupName,
        attributeNumericName, attributeNumericValue);
    deleteInformationSystem(informationSystemName, informationSystemVersion);
    deleteAttribute(attributeNumericName);
    deleteAttributeGroup(attributeNumericGroupName);

  }

  @Test
  /**
   * Creates a new enum Attribute under the Default Attribute Group and add these attribute to an IS
   * After assertions, IS and Attribute will be deleted
   */
  public void testCreateAndDeleteISwithEnumAttribute() {

    String attributeEnumGroupName = "!EnumGroup";
    String attributeEnumName = "!EnumAttribute";

    String attributeBBT = "Informationssystem";
    String informationSystemName = "Information System";
    String informationSystemVersion = "1.0";
    String informationSystemDescription = "This is an information system created by a selenium test.";
    String attributeDescription = "This is a attribute created by a selenium test.";

    String[] enums = { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY" };
    List<String> attributeEnumtList = Arrays.asList(enums);

    String singleEnumValue = attributeEnumtList.get(2);

    login();
    createEnumerationAttribute(attributeEnumName, attributeDescription, attributeBBT, attributeEnumtList);
    createAttributeGroup(attributeEnumGroupName, attributeEnumName);
    createInformationSystemWithComboEntry(informationSystemName, informationSystemVersion, informationSystemDescription, attributeEnumGroupName,
        attributeEnumName, singleEnumValue);
    deleteInformationSystem(informationSystemName, informationSystemVersion);
    deleteAttribute(attributeEnumName);
    deleteAttributeGroup(attributeEnumGroupName);
  }

  @Test
  /**
   * Creates a new Responsibility Attribute under the Default Attribute Group and add these attribute to an IS
   * After assertions, IS and Attribute will be deleted
   */
  public void testCreateAndDeleteISwithResponsibilityAttribute() {

    String attributeResponsibilityGroupName = "!ResponsibilityGroup";
    String attributeResponsibilityName = "!ResponsibilityAttribute";

    String attributeBBT = "Informationssystem";
    String informationSystemName = "Information System";
    String informationSystemVersion = "1.0";
    String informationSystemDescription = "This is an information system created by a selenium test.";
    String attributeDescription = "This is a attribute created by a selenium test.";

    String[] responsibilities = { "joe", "sue", "cio" };
    List<String> attributeResponsibilityList = Arrays.asList(responsibilities);

    String singleResponsibilityValue = attributeResponsibilityList.get(1);

    login();
    createResponsibilityAttribute(attributeResponsibilityName, attributeDescription, attributeBBT, attributeResponsibilityList);
    createAttributeGroup(attributeResponsibilityGroupName, attributeResponsibilityName);
    createInformationSystemWithComboEntry(informationSystemName, informationSystemVersion, informationSystemDescription,
        attributeResponsibilityGroupName, attributeResponsibilityName, singleResponsibilityValue);
    deleteInformationSystem(informationSystemName, informationSystemVersion);
    deleteAttribute(attributeResponsibilityName);
    deleteAttributeGroup(attributeResponsibilityGroupName);

  }

}
