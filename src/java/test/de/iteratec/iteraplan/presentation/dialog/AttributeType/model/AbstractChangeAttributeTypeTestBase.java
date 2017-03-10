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
import java.util.Set;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.presentation.dialog.common.ComponentMode;
import de.iteratec.iteraplan.presentation.dialog.common.model.ManyAssociationSetComponentModel;


public class AbstractChangeAttributeTypeTestBase extends BaseTransactionalTestSupport {

  @Autowired
  private TestDataHelper2      testDataHelper;

  @Autowired
  private AttributeTypeService atService;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  protected void renameAtAndAssert(AttributeType attributeType) {
    AttributeTypeComponentModel classUnderTest = initComponentModel(attributeType);

    String newName = classUnderTest.getNameModel().getName() + " changed";
    classUnderTest.getNameModel().setName(newName);

    AttributeType savedAT = configureAndSaveAT(attributeType, classUnderTest);

    assertEquals(newName, savedAT.getName());
  }

  protected void addBbtAndAssert(AttributeType attributeType) {
    AttributeTypeComponentModel classUnderTest = initComponentModel(attributeType);

    ManyAssociationSetComponentModel<AttributeType, BuildingBlockType> bbtCM = classUnderTest.getBuildingBlockTypeModel();

    BuildingBlockType bbtIE = getDataHelper().getBuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT);
    assertTrue(bbtCM.getAvailableElementsPresentation().contains(bbtIE));

    bbtCM.setElementIdToAdd(bbtIE.getId());
    classUnderTest.update();

    AttributeType savedAT = configureAndSaveAT(attributeType, classUnderTest);

    // assertion
    Builder<BuildingBlockType> expectedBBTsBuilder = ImmutableSet.builder();
    expectedBBTsBuilder.add(getDataHelper().getBuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS));
    expectedBBTsBuilder.add(getDataHelper().getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE));
    expectedBBTsBuilder.add(getDataHelper().getBuildingBlockType(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT));

    Set<BuildingBlockType> actualBbtSet = savedAT.getBuildingBlockTypes();

    assertEquals(expectedBBTsBuilder.build(), actualBbtSet);
  }

  protected void removeBbtAndAssert(AttributeType attributeType) {
    AttributeTypeComponentModel classUnderTest = initComponentModel(attributeType);

    ManyAssociationSetComponentModel<AttributeType, BuildingBlockType> bbtCM = classUnderTest.getBuildingBlockTypeModel();

    List<BuildingBlockType> connecteds = bbtCM.getConnectedElements();
    BuildingBlockType bbtISR = getDataHelper().getBuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    assertEquals(2, connecteds.size());
    assertTrue(connecteds.contains(bbtISR));

    bbtCM.setElementIdToRemove(bbtISR.getId());
    classUnderTest.update();

    AttributeType savedAT = configureAndSaveAT(attributeType, classUnderTest);

    // assertion
    Set<BuildingBlockType> expectedBBTs = ImmutableSet.of(getDataHelper().getBuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS));
    Set<BuildingBlockType> actualBbtSet = savedAT.getBuildingBlockTypes();

    assertEquals(expectedBBTs, actualBbtSet);
  }

  protected AttributeTypeComponentModel initComponentModel(AttributeType attributeType) {
    AttributeTypeComponentModel classUnderTest = new AttributeTypeComponentModel(ComponentMode.EDIT);

    beginTransaction();
    classUnderTest.setAttributeType(attributeType);
    classUnderTest.initializeFrom(attributeType);
    commit();
    return classUnderTest;
  }

  protected AttributeType configureAndSaveAT(AttributeType attributeType, AttributeTypeComponentModel classUnderTest) {
    beginTransaction();

    AttributeType mergedAT = atService.merge(classUnderTest.getAttributeType());
    classUnderTest.configure(mergedAT);
    mergedAT.validate();
    AttributeType savedAT = atService.saveOrUpdate(mergedAT);

    commit();
    return savedAT;
  }

  protected TestDataHelper2 getDataHelper() {
    return testDataHelper;
  }

}
