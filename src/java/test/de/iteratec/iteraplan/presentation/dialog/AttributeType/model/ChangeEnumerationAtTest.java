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
package de.iteratec.iteraplan.presentation.dialog.AttributeType.model;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;


/**
 * This test covers issue ITERAPLAN-1457 with test methods to change an enumeration attribute type
 * in various ways.
 * Class under test is the {@link AttributeTypeComponentModel} which contains the component models
 * responsible for the issue.
 */
public class ChangeEnumerationAtTest extends AbstractChangeAttributeTypeTestBase {

  @Test
  public void testRenameAT() {
    TestData data = new TestData();
    commit();

    renameAtAndAssert(data.enumAT);
  }

  @Test
  public void testAddAV() {
    TestData data = new TestData();
    commit();

    AttributeType attributeType = data.enumAT;

    AttributeTypeComponentModel classUnderTest = initComponentModel(attributeType);

    EnumAttributeTypeComponentModel enumAtCM = (EnumAttributeTypeComponentModel) classUnderTest.getAttributeTypeSpecializationComponentModel();
    EnumAttributeValuesComponentModel enumAvsCM = enumAtCM.getEnumAttributeValuesModel();

    enumAvsCM.setNameToAdd("av3");
    enumAvsCM.setSelectedPosition(Integer.valueOf(0));
    enumAvsCM.setAction("new");
    classUnderTest.update();

    AttributeType savedAT = configureAndSaveAT(attributeType, classUnderTest);

    // assertion
    List<EnumAV> actualAVs = ((EnumAT) savedAT).getSortedAttributeValues();
    List<String> expectedNames = ImmutableList.of(data.enumAVs.get(0).getName(), data.enumAVs.get(1).getName(), "av3");
    assertEquals(expectedNames.size(), actualAVs.size());
    for (int i = 0; i < 3; i++) {
      assertEquals(expectedNames.get(i), actualAVs.get(i).getName());
    }
  }

  @Test
  public void testRemoveAV() {
    TestData data = new TestData();
    commit();

    AttributeType attributeType = data.enumAT;

    AttributeTypeComponentModel classUnderTest = initComponentModel(attributeType);

    EnumAttributeTypeComponentModel enumAtCM = (EnumAttributeTypeComponentModel) classUnderTest.getAttributeTypeSpecializationComponentModel();
    EnumAttributeValuesComponentModel enumAvsCM = enumAtCM.getEnumAttributeValuesModel();

    // remove first attribute value
    enumAvsCM.setSelectedPosition(Integer.valueOf(1));
    enumAvsCM.setAction("delete");
    classUnderTest.update();

    AttributeType savedAT = configureAndSaveAT(attributeType, classUnderTest);

    // assertion
    List<EnumAV> avList = ((EnumAT) savedAT).getSortedAttributeValues();
    assertEquals(1, avList.size());
    assertEquals("av2", avList.get(0).getName());
  }

  @Test
  public void testAddBBT() {
    TestData data = new TestData();
    commit();

    addBbtAndAssert(data.enumAT);
  }

  @Test
  public void testRemoveBBT() {
    TestData data = new TestData();
    commit();

    removeBbtAndAssert(data.enumAT);
  }

  private final class TestData {
    private final EnumAT       enumAT;
    private final List<EnumAV> enumAVs = Lists.newArrayList();

    public TestData() {
      enumAT = getDataHelper().createEnumAttributeType("Enum AT", "enum AT desc", Boolean.FALSE, getDataHelper().getDefaultATG());
      enumAVs.add(getDataHelper().createEnumAV("av1", "av1 desc", enumAT));
      enumAVs.add(getDataHelper().createEnumAV("av2", "av2 desc", enumAT));

      enumAT.addBuildingBlockTypeTwoWay(getDataHelper().getBuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS));
      enumAT.addBuildingBlockTypeTwoWay(getDataHelper().getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));

      // there need to be AVAs for the original bug(s) to appear
      BusinessProcess bp = getDataHelper().createBusinessProcess("BP", "bp desc");
      getDataHelper().createAVA(bp, enumAVs.get(0));

      InformationSystemRelease isr = getDataHelper().createInformationSystemRelease(getDataHelper().createInformationSystem("IS"), "test");
      getDataHelper().createAVA(isr, enumAVs.get(1));
    }
  }

}
