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
package de.iteratec.iteraplan.businesslogic.service.History;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.HistoryService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.diffs.model.HistoryBBAttributeGroupChangeset;
import de.iteratec.iteraplan.diffs.model.HistoryBBChangeset;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.DateAV;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.NumberAV;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.dialog.History.HistoryResultsPage;

public class AttributeModificationTest extends BaseTransactionalTestSupport  {
  private boolean                wasHistoryEnabledBeforeTest = false;

  private BusinessUnit           bu1;

  private DateAT                 dateAt;
  private DateAV                 dateAv;
  private TextAT                 textAt;
  private TextAV                 textAv;
  private NumberAT               numberAt;
  private NumberAV               numberAv;

  private EnumAT                 enumAT;
  private EnumAV                 enumVal1;
  private EnumAV                 enumVal2;
  private ResponsibilityAT       responsibilityAT;
  private List<ResponsibilityAV> respAvs;
  @Autowired
  private HistoryService         historyService;
  @Autowired
  private BusinessUnitService    businessUnitService;
  @Autowired
  private AttributeValueService  attributeValueService;
  @Autowired
  private TestDataHelper2        testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    wasHistoryEnabledBeforeTest = historyService.isHistoryEnabled();
    historyService.setHistoryEnabled(true);

    bu1 = testDataHelper.createBusinessUnit("BU1", "");
    AttributeTypeGroup atg = testDataHelper.createAttributeTypeGroup("My Attributes", "no desc ;)");

    dateAt = testDataHelper.createDateAttributeType("MyDateAT", "some dates", atg);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(dateAt);
    dateAv = testDataHelper.createDateAV(new LocalDate(2011, 12, 31).toDate(), dateAt);
    testDataHelper.createAVA(bu1, dateAv);

    textAt = testDataHelper.createTextAttributeType("MytextAT", "some text", true, atg);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(textAt);
    textAv = testDataHelper.createTextAV("my Text!", textAt);
    testDataHelper.createAVA(bu1, textAv);

    numberAt = testDataHelper.createNumberAttributeType("MyNumberAT", "some numbers", atg);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(numberAt);
    numberAv = testDataHelper.createNumberAV(new BigDecimal(23), numberAt);
    testDataHelper.createAVA(bu1, numberAv);

    enumAT = testDataHelper.createEnumAttributeType("MyEnumAT", "desc", Boolean.FALSE, atg);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(enumAT);
    enumVal1 = testDataHelper.createEnumAV("my Value 1", "nope", enumAT);
    enumVal2 = testDataHelper.createEnumAV("my Value 2", "nope", enumAT);
    testDataHelper.createAVA(bu1, enumVal1);

    User user1 = testDataHelper.createUser("myUser1");
    User user2 = testDataHelper.createUser("myUser2");
    responsibilityAT = testDataHelper.createResponsibilityAttributeType("MyRespAT", "desc", Boolean.FALSE, atg);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(responsibilityAT);
    respAvs = testDataHelper.createResponsibilityAV(responsibilityAT, user1, user2);
    testDataHelper.createAVA(bu1, respAvs.get(0));

    commit();
  }

  /**
   * Restores the original state of the auditing mechanism, in oder not to interfere with other tests.
   */
  @Override
  @After
  public void onTearDown() {
    super.onTearDown();
    historyService.setHistoryEnabled(wasHistoryEnabledBeforeTest);
  }

  @Test
  public void testDateModification() throws Exception {
    beginTransaction();
    // create modifications
    BusinessUnit buReloaded = businessUnitService.loadObjectById(bu1.getId());
    DateAV newDate = testDataHelper.createDateAV(new LocalDate(2012, 1, 1).toDate(), dateAt);
    attributeValueService.setValue(buReloaded, newDate, dateAt);

    businessUnitService.saveOrUpdate(buReloaded);
    commit();

    beginTransaction();

    verifyAttributeChangeset(dateAt, dateAv, newDate);
  }

  @Test
  public void testTextModification() throws Exception {
    beginTransaction();
    // create modifications
    BusinessUnit buReloaded = businessUnitService.loadObjectById(bu1.getId());
    TextAV newText = testDataHelper.createTextAV("bla bla neu", textAt);
    attributeValueService.setValue(buReloaded, newText, textAt);

    businessUnitService.saveOrUpdate(buReloaded);
    commit();

    beginTransaction();

    verifyAttributeChangeset(textAt, textAv, newText);
  }

  @Test
  public void testNumberModification() throws Exception {
    beginTransaction();
    // create modifications
    BusinessUnit buReloaded = businessUnitService.loadObjectById(bu1.getId());
    NumberAV newNum = testDataHelper.createNumberAV(new BigDecimal(65), numberAt);
    attributeValueService.setValue(buReloaded, newNum, numberAt);

    businessUnitService.saveOrUpdate(buReloaded);
    commit();

    beginTransaction();

    verifyAttributeChangeset(numberAt, numberAv, newNum);
  }

  @Test
  @Ignore
  //until Envers bug about oneToMany mapping with insert/update=false has been resolved
  public void testEnumModification() {
    beginTransaction();
    // create modifications
    BusinessUnit buReloaded = businessUnitService.loadObjectById(bu1.getId());
    attributeValueService.setReferenceValues(buReloaded, Lists.newArrayList(enumVal2), enumAT.getId());

    businessUnitService.saveOrUpdate(buReloaded);
    commit();

    beginTransaction();

    verifyAttributeChangeset(enumAT, enumVal1, enumVal2);
  }

  @Test
  @Ignore
  //until Envers bug about oneToMany mapping with insert/update=false has been resolved
  public void testResponsibilityModification() {
    beginTransaction();
    // create modifications
    BusinessUnit buReloaded = businessUnitService.loadObjectById(bu1.getId());
    attributeValueService.setReferenceValues(buReloaded, Lists.newArrayList(respAvs.get(1)), responsibilityAT.getId());

    businessUnitService.saveOrUpdate(buReloaded);
    commit();

    beginTransaction();

    verifyAttributeChangeset(responsibilityAT, respAvs.get(0), respAvs.get(1));
  }

  @SuppressWarnings("boxing")
  private void verifyAttributeChangeset(AttributeType attributeType, AttributeValue oldValue, AttributeValue newValue) {
    HistoryResultsPage page = historyService.getLocalHistoryPage(BusinessUnit.class, bu1.getId(), 0, -1, null, null);
    List<HistoryBBChangeset> changesets = page.getBbChangesets();
    // latest changes appear first, so we need to take element 0
    HistoryBBChangeset c = Iterables.get(changesets, 0);
    Assert.assertTrue(c.isAttributesChanged());

    List<HistoryBBAttributeGroupChangeset> attributeGroupChangesets = c.getAttributeGroupChangesets();
    HistoryBBAttributeGroupChangeset firstAttributesChangeset = attributeGroupChangesets.get(0);
    // skip until bug is resolved
    //    Assert.assertEquals(attributeType.getAttributeTypeGroup().getName(), firstAttributesChangeset.getGroupName());

    List<String[]> changedAttributes = firstAttributesChangeset.getChangedAttributes();
    for (String[] attributeValueStates : changedAttributes) {
      System.out.println(Arrays.toString(attributeValueStates));
      Assert.assertEquals(attributeType.getName(), attributeValueStates[0]);
      Assert.assertEquals(oldValue.getValueString(), attributeValueStates[1]);
      Assert.assertEquals(newValue.getValueString(), attributeValueStates[2]);
    }
  }
}
