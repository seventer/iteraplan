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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAV;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;


/**
 * This test covers issue ITERAPLAN-1457 with test methods to change a responsibility attribute type
 * in various ways.
 * Class under test is the {@link AttributeTypeComponentModel} which contains the component models
 * responsible for the issue.
 */
public class ChangeResponsibilityAtTest extends AbstractChangeAttributeTypeTestBase {

  @Test
  public void testRenameAT() {
    TestData data = new TestData();
    commit();

    renameAtAndAssert(data.respAT);
  }

  @Test
  public void testAddAV() {
    TestData data = new TestData();
    commit();

    AttributeType attributeType = data.respAT;

    AttributeTypeComponentModel classUnderTest = initComponentModel(attributeType);

    ResponsibilityAttributeTypeComponentModel respAtCM = (ResponsibilityAttributeTypeComponentModel) classUnderTest
        .getAttributeTypeSpecializationComponentModel();
    ResponsibilityAttributeValuesComponentModel respAvsCM = respAtCM.getResponsibilityAttributeValuesModel();

    List<UserEntity> availableUserEntities = respAvsCM.getAvailableElementsPresentation();
    assertEquals(3, availableUserEntities.size()); // three entries: null (for empty selection), system-user, user3
    assertTrue(availableUserEntities.contains(data.user3));

    respAvsCM.setElementIdToAdd(data.user3.getId());
    classUnderTest.update();

    AttributeType savedAT = configureAndSaveAT(attributeType, classUnderTest);

    // assertions
    List<ResponsibilityAV> actualAVs = ((ResponsibilityAT) savedAT).getSortedAttributeValues();
    List<User> expectedUsers = ImmutableList.of(data.user1, data.user2, data.user3);
    assertEquals(expectedUsers.size(), actualAVs.size());
    for (int i = 0; i < 3; i++) {
      assertEquals(expectedUsers.get(i), actualAVs.get(i).getUserEntity());
    }
  }

  @Test
  public void testRemoveAV() {
    TestData data = new TestData();
    commit();

    AttributeType attributeType = data.respAT;

    AttributeTypeComponentModel classUnderTest = initComponentModel(attributeType);

    ResponsibilityAttributeTypeComponentModel respAtCM = (ResponsibilityAttributeTypeComponentModel) classUnderTest
        .getAttributeTypeSpecializationComponentModel();
    ResponsibilityAttributeValuesComponentModel respAvsCM = respAtCM.getResponsibilityAttributeValuesModel();

    assertEquals(ImmutableList.of(data.user1, data.user2), respAvsCM.getConnectedElements());

    respAvsCM.setElementIdToRemove(data.user1.getId());
    classUnderTest.update();

    AttributeType savedAT = configureAndSaveAT(attributeType, classUnderTest);

    // assertions
    List<ResponsibilityAV> actualAVs = ((ResponsibilityAT) savedAT).getSortedAttributeValues();
    assertEquals(1, actualAVs.size());
    assertEquals(data.user2, actualAVs.get(0).getUserEntity());
  }

  @Test
  public void testAddBBT() {
    TestData data = new TestData();
    commit();

    addBbtAndAssert(data.respAT);
  }

  @Test
  public void testRemoveBBT() {
    TestData data = new TestData();
    commit();

    removeBbtAndAssert(data.respAT);
  }

  private final class TestData {
    private final ResponsibilityAT       respAT;
    private final User                   user1;
    private final User                   user2;
    private final User                   user3;
    private final List<ResponsibilityAV> respAVs;

    public TestData() {
      user1 = getDataHelper().createUser("user1");
      user2 = getDataHelper().createUser("user2");
      user3 = getDataHelper().createUser("user3");

      respAT = getDataHelper().createResponsibilityAttributeType("Resp AT", "resp AT desc", Boolean.TRUE, getDataHelper().getDefaultATG());
      respAVs = getDataHelper().createResponsibilityAV(respAT, user1, user2);

      respAT.addBuildingBlockTypeTwoWay(getDataHelper().getBuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS));
      respAT.addBuildingBlockTypeTwoWay(getDataHelper().getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));

      // there need to be AVAs for the original bug(s) to appear
      BusinessProcess bp = getDataHelper().createBusinessProcess("BP", "bp desc");
      getDataHelper().createAVA(bp, respAVs.get(0));
      getDataHelper().createAVA(bp, respAVs.get(1));

      InformationSystemRelease isr = getDataHelper().createInformationSystemRelease(getDataHelper().createInformationSystem("IS"), "test");
      getDataHelper().createAVA(isr, respAVs.get(1));
    }
  }

}
