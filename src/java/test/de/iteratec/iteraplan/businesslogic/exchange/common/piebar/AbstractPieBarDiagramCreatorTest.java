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
package de.iteratec.iteraplan.businesslogic.exchange.common.piebar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.DimensionAdapter;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;


public class AbstractPieBarDiagramCreatorTest extends BaseTransactionalTestSupport {
  private AbstractPieBarDiagramCreator<?> abstractPieBarDiagramCreator;

  @Autowired
  private AttributeTypeService            attributeTypeService;
  @Autowired
  private AttributeValueService           attributeValueService;
  @Autowired
  private TestDataHelper2                 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    abstractPieBarDiagramCreator = new PieDiagramCreator(new PieBarDiagramOptionsBean(), null, attributeTypeService, attributeValueService);
  }

  @Test
  public final void testGetValuesAdapter() {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("test", "test description");
    AttributeType numberAT = testDataHelper.createNumberAttributeType("NumberAT", "Number AT description", atg);
    AttributeType enumAT = testDataHelper.createEnumAttributeType("EnumAT", "Enum AT description", Boolean.TRUE, atg);

    assertNull(abstractPieBarDiagramCreator.getValuesAdapter(Integer.valueOf(-1)));
    assertNotNull(abstractPieBarDiagramCreator.getValuesAdapter(numberAT.getId()));
    assertNotNull(abstractPieBarDiagramCreator.getValuesAdapter(enumAT.getId()));
  }

  @Test
  public final void testCreateValuesListFromIdentityEntities() {
    // create attribute value list
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("test", "test description");
    EnumAT enumAT = testDataHelper.createEnumAttributeType("EnumAT", "Enum AT description", Boolean.TRUE, atg);

    List<AttributeValue> enumAVs = CollectionUtils.arrayList();
    for (int i = 1; i <= 5; i++) {
      enumAVs.add(testDataHelper.createEnumAV("av" + i, "enum AV description " + i, enumAT));
    }

    // create Business Process List
    List<BusinessProcess> bpList = CollectionUtils.arrayList();
    for (int i = 1; i <= 5; i++) {
      bpList.add(testDataHelper.createBusinessProcess("bp" + i, "BP description " + i));
    }

    // assert
    List<String> expectedList = ImmutableList.of("av1", "av2", "av3", "av4", "av5");
    List<String> resultList = abstractPieBarDiagramCreator.createValuesListFromIdentityEntities(enumAVs);
    assertEquals(expectedList, resultList);

    expectedList = ImmutableList.of("bp1", "bp2", "bp3", "bp4", "bp5");
    resultList = abstractPieBarDiagramCreator.createValuesListFromIdentityEntities(bpList);
    assertEquals(expectedList, resultList);
  }

  @Test
  public final void testCreateValuesListFromBuildingBlock() {
    // create attribute value lists
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("test", "test description");
    EnumAT enumAT = testDataHelper.createEnumAttributeType("EnumAT", "Enum AT description", Boolean.TRUE, atg);
    NumberAT numberAT = testDataHelper.createNumberAttributeType("NumberAT", "Number AT description", atg);

    InformationSystem is = testDataHelper.createInformationSystem("test IS");
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, "1.0");

    for (int i = 1; i <= 5; i++) {
      EnumAV createEnumAV = testDataHelper.createEnumAV("av" + i, "enum AV description " + i, enumAT);
      testDataHelper.createAVA(isr, createEnumAV);
    }

    // create 10 number attribute values
    for (int i = 1; i <= 10; i++) {
      if (i == 5) {
        // but only assign one (25) to the building block, since number attributes are single value only
        testDataHelper.createAVA(isr, testDataHelper.createNumberAV(new BigDecimal(i * i), numberAT));
      }
      else {
        testDataHelper.createNumberAV(new BigDecimal(i * i), numberAT);
      }
    }
    commit();
    beginTransaction();

    // case EnumAT
    DimensionAdapter<?> adapter = abstractPieBarDiagramCreator.getValuesAdapter(enumAT.getId());
    List<String> expectedList = ImmutableList.of("av1", "av2", "av3", "av4", "av5");
    List<String> valuesList = abstractPieBarDiagramCreator.createValuesListFromBuildingBlock(isr, enumAT.getId(), adapter);
    assertEquals(expectedList, valuesList);

    // case NumberAT
    adapter = abstractPieBarDiagramCreator.getValuesAdapter(numberAT.getId());
    expectedList = ImmutableList.of("16,00 - 36,00");
    valuesList = abstractPieBarDiagramCreator.createValuesListFromBuildingBlock(isr, numberAT.getId(), adapter);
    assertEquals(expectedList, valuesList);
  }

  @Test
  public final void testGetAttributeValues() {
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("test", "test description");
    EnumAT enumAT = testDataHelper.createEnumAttributeType("EnumAT", "Enum AT description", Boolean.TRUE, atg);
    NumberAT numberAT = testDataHelper.createNumberAttributeType("NumberAT", "Number AT description", atg);

    List<AttributeValue> enumAVs = CollectionUtils.arrayList();
    for (int i = 1; i <= 5; i++) {
      enumAVs.add(testDataHelper.createEnumAV("av" + i, "enum AV description " + i, enumAT));
    }

    List<AttributeValue> numberAVs = CollectionUtils.arrayList();
    for (int i = 1; i <= 10; i++) {
      numberAVs.add(testDataHelper.createNumberAV(new BigDecimal(i * i), numberAT));
    }

    BusinessUnit bu = testDataHelper.createBusinessUnit("testBU", "testBU description");
    testDataHelper.createAVA(bu, enumAVs.get(1));
    testDataHelper.createAVA(bu, enumAVs.get(3));
    testDataHelper.createAVA(bu, numberAVs.get(4));

    Integer attributeId = Integer.valueOf(-1);
    List<String> expectedList = CollectionUtils.arrayList();
    assertEquals(expectedList, abstractPieBarDiagramCreator.getAttributeValues(null, attributeId));
    assertEquals(expectedList, abstractPieBarDiagramCreator.getAttributeValues(bu, attributeId));

    attributeId = enumAT.getId();
    expectedList.add(enumAVs.get(1).getValueString());
    expectedList.add(enumAVs.get(3).getValueString());
    assertEquals(expectedList, abstractPieBarDiagramCreator.getAttributeValues(bu, attributeId));

    attributeId = numberAT.getId();
    expectedList.clear();
    expectedList.add(numberAVs.get(4).getValueString());
    assertEquals(expectedList, abstractPieBarDiagramCreator.getAttributeValues(bu, attributeId));
  }

}
