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
package de.iteratec.iteraplan.businesslogic.reports.query.node;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessMappingTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessProcessTypeQ;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.TextAT;
import de.iteratec.iteraplan.model.attribute.TextAV;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * Test cases for attribute leaf nodes of text attribute types (with database).
 */
public class TextAttributeLeafNodeDBTest extends AbstractNodeTestBase {
  private static final String   WRONG_ELEMENT_NR_ERROR_MSG = "Wrong number of elements in result set!";
  private static final String   NO_ERROR_ERROR_MSG         = "An error should have orccured";

  @Autowired
  private AttributeTypeGroupDAO attributeTypeGroupDAO;
  @Autowired
  private BuildingBlockTypeDAO  buildingBlockTypeDAO;
  @Autowired
  private TestDataHelper2       testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Create data for queries with result type Ipurelease.
   */
  private void createData1() {
    // Create information system and releases.
    InformationSystem informationSystem = testDataHelper.createInformationSystem("I");
    InformationSystemRelease release1 = testDataHelper.createInformationSystemRelease(informationSystem, "i1");
    InformationSystemRelease release2 = testDataHelper.createInformationSystemRelease(informationSystem, "i2");
    InformationSystemRelease release3 = testDataHelper.createInformationSystemRelease(informationSystem, "i3");
    testDataHelper.createInformationSystemRelease(informationSystem, "i4");

    // Create hierarchy.
    testDataHelper.addChildToIsr(release1, release2);
    testDataHelper.addChildToIsr(release2, release3);

    // Create attribute type group.
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType type = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

    // Create text attibute type and values.
    TextAT textAttributeType = testDataHelper.createTextAttributeType("a1", "", false, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(textAttributeType, type);

    TextAV textAttributeValue1 = testDataHelper.createTextAV("A", textAttributeType);
    TextAV textAttributeValue2 = testDataHelper.createTextAV("B", textAttributeType);

    // Create attribute value assignments.
    testDataHelper.createAVA(release1, textAttributeValue1);
    testDataHelper.createAVA(release2, textAttributeValue2);
  }

  @Test
  public void testTextForInformationSystemRelease() {
    createData1();
    commit();

    beginTransaction();

    Type<InformationSystemRelease> type = InformationSystemReleaseTypeQu.getInstance();
    AbstractLeafNode leaf = null;
    Set<BuildingBlock> results = new HashSet<BuildingBlock>();

    leaf = TextAttributeLeafNode.createNode(type, null, 1, Comparator.LIKE, "B");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leaf);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    leaf = TextAttributeLeafNode.createNode(type, null, 1, Comparator.NOT_LIKE, "B");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leaf);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    try {
      leaf = TextAttributeLeafNode.createNode(type, null, 1, Comparator.LIKE, "");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    try {
      leaf = TextAttributeLeafNode.createNode(type, null, 1, Comparator.LIKE, "*");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    try {
      leaf = TextAttributeLeafNode.createNode(type, null, 1, Comparator.LIKE, "**");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    try {
      leaf = TextAttributeLeafNode.createNode(type, null, 1, Comparator.NOT_LIKE, "");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    try {
      leaf = TextAttributeLeafNode.createNode(type, null, 1, Comparator.NOT_LIKE, "*");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    try {
      leaf = TextAttributeLeafNode.createNode(type, null, 1, Comparator.NOT_LIKE, "**");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    commit();
  }

  /**
   * Create data for queries with result type Ipurelease and leaf type BusinessProcess.
   */
  private void createData2() {

    // Create information system and releases.
    InformationSystem informationSystem = testDataHelper.createInformationSystem("I");
    InformationSystemRelease release1 = testDataHelper.createInformationSystemRelease(informationSystem, "i1");
    InformationSystemRelease release2 = testDataHelper.createInformationSystemRelease(informationSystem, "i2");
    InformationSystemRelease release3 = testDataHelper.createInformationSystemRelease(informationSystem, "i3");
    InformationSystemRelease release4 = testDataHelper.createInformationSystemRelease(informationSystem, "i4");

    // Create hierarchy.
    testDataHelper.addChildToIsr(release1, release2);
    testDataHelper.addChildToIsr(release2, release3);

    // Create business processes.
    BusinessProcess p1 = testDataHelper.createBusinessProcess("p1", "");
    BusinessProcess p2 = testDataHelper.createBusinessProcess("p2", "");
    BusinessProcess p3 = testDataHelper.createBusinessProcess("p3", "");
    BusinessProcess p4 = testDataHelper.createBusinessProcess("p4", "");

    // Create business supports.
    testDataHelper.createBusinessMappingToProcess(release1, p1);
    testDataHelper.createBusinessMappingToProcess(release2, p2);
    testDataHelper.createBusinessMappingToProcess(release3, p3);
    testDataHelper.createBusinessMappingToProcess(release4, p4);

    // Create attribute type group.
    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType type = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS);

    // Create text attribute type and values.
    TextAT textAttributeType = testDataHelper.createTextAttributeType("a1", "", false, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(textAttributeType, type);

    TextAV textAttributeValue1 = testDataHelper.createTextAV("A", textAttributeType);
    TextAV textAttributeValue2 = testDataHelper.createTextAV("B", textAttributeType);

    // Create attribute value assignments.
    testDataHelper.createAVA(p1, textAttributeValue1);
    testDataHelper.createAVA(p2, textAttributeValue2);
  }

  @Test
  public void testTextForBusinessProcess() {
    createData2();
    commit();

    beginTransaction();

    Type<InformationSystemRelease> releaseType = InformationSystemReleaseTypeQu.getInstance();
    Type<BusinessMapping> supportType = BusinessMappingTypeQu.getInstance();
    Type<BusinessProcess> processType = BusinessProcessTypeQ.getInstance();

    Extension ext1 = releaseType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_BUSINESSMAPPINGS);
    Extension ext2 = supportType.getExtension(BusinessMappingTypeQu.EXTENSION_BUSINESSPROCESS);
    ExtensionNode root = new ExtensionNode(releaseType, ext1);
    ExtensionNode intermediary = new ExtensionNode(supportType, ext2);
    root.setChild(intermediary);

    AbstractLeafNode leaf = null;
    Set<? extends BuildingBlock> results = new HashSet<BuildingBlock>();

    leaf = TextAttributeLeafNode.createNode(processType, null, 1, Comparator.LIKE, "B");
    intermediary.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    leaf = TextAttributeLeafNode.createNode(processType, null, 1, Comparator.NOT_LIKE, "B");
    intermediary.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    try {
      leaf = TextAttributeLeafNode.createNode(processType, null, 1, Comparator.LIKE, "");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    try {
      leaf = TextAttributeLeafNode.createNode(processType, null, 1, Comparator.LIKE, "*");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    try {
      leaf = TextAttributeLeafNode.createNode(processType, null, 1, Comparator.LIKE, "**");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    try {
      leaf = TextAttributeLeafNode.createNode(processType, null, 1, Comparator.NOT_LIKE, "");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    try {
      leaf = TextAttributeLeafNode.createNode(processType, null, 1, Comparator.NOT_LIKE, "*");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    try {
      leaf = TextAttributeLeafNode.createNode(processType, null, 1, Comparator.NOT_LIKE, "**");
    } catch (IteraplanBusinessException ex) {
      assertEquals(NO_ERROR_ERROR_MSG, ex.getErrorCode(), IteraplanErrorMessages.QUERY_NO_VALUE_PROVIDED);
    }

    commit();
  }

}