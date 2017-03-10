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
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessObjectTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessProcessTypeQ;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * Test cases for attribute leaf nodes of enum attribute types (with database).
 */
public class EnumAttributeLeafNodeDBTest extends AbstractNodeTestBase {
  private static final String   WRONG_ELEMENT_NR_ERROR_MSG = "Wrong number of elements in result set!";
  @Autowired
  private AttributeTypeGroupDAO attributeTypeGroupDAO;
  @Autowired
  private BuildingBlockTypeDAO  buildingBlockTypeDAO;
  @Autowired
  private TestDataHelper2  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }


  /**
   * Create data for queries with result type Ipurelease.
   */
  private void createData1() {
    InformationSystem informationSystem = testDataHelper.createInformationSystem("I");
    InformationSystemRelease release1 = testDataHelper.createInformationSystemRelease(informationSystem, "i1");
    InformationSystemRelease release2 = testDataHelper.createInformationSystemRelease(informationSystem, "i2");
    InformationSystemRelease release3 = testDataHelper.createInformationSystemRelease(informationSystem, "i3");
    testDataHelper.createInformationSystemRelease(informationSystem, "i4");

    testDataHelper.addChildToIsr(release1, release2);
    testDataHelper.addChildToIsr(release2, release3);

    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType type = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

    EnumAT enumAT1 = testDataHelper.createEnumAttributeType("a1", "", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT1, type);
    EnumAV enumAV11 = testDataHelper.createEnumAV("A", "", enumAT1);
    EnumAV enumAV12 = testDataHelper.createEnumAV("B", "", enumAT1);
    testDataHelper.createEnumAV("C", "", enumAT1);

    testDataHelper.createAVA(release1, enumAV11);
    testDataHelper.createAVA(release2, enumAV12);

    EnumAT enumAT2 = testDataHelper.createEnumAttributeType("a2", "", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT2, type);
    testDataHelper.createEnumAV("A", "", enumAT2);
    testDataHelper.createEnumAV("B", "", enumAT2);
    testDataHelper.createEnumAV("C", "", enumAT2);

    EnumAT enumAT3 = testDataHelper.createEnumAttributeType("a3", "", Boolean.TRUE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT3, type);
    EnumAV enumAV31 = testDataHelper.createEnumAV("A", "", enumAT3);
    EnumAV enumAV32 = testDataHelper.createEnumAV("B", "", enumAT3);
    EnumAV enumAV33 = testDataHelper.createEnumAV("C", "", enumAT3);
    testDataHelper.createEnumAV("D", "", enumAT3);

    testDataHelper.createAVA(release2, enumAV31);
    testDataHelper.createAVA(release2, enumAV32);
    testDataHelper.createAVA(release1, enumAV33);

    EnumAT enumAT4 = testDataHelper.createEnumAttributeType("a4", "", Boolean.TRUE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT4, type);
    testDataHelper.createEnumAV("A", "", enumAT4);
    testDataHelper.createEnumAV("B", "", enumAT4);
    testDataHelper.createEnumAV("C", "", enumAT4);
    testDataHelper.createEnumAV("D", "", enumAT4);
  }

  @Test
  public void testEnumForInformationSystemReleaseSingle() {
    createData1();
    commit();

    beginTransaction();

    InformationSystemReleaseTypeQu resultType = InformationSystemReleaseTypeQu.getInstance();
    AbstractLeafNode aln = null;
    Set<BuildingBlock> results = new HashSet<BuildingBlock>();

    aln = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.LIKE, "B");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(aln);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    aln = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NOT_LIKE, "B");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(aln);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    aln = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NO_ASSIGNMENT, "");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(aln);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }
  @Test
  public void testEnumForInformationSystemReleaseMulti() {
    createData1();
    commit();
    beginTransaction();

    InformationSystemReleaseTypeQu resultType = InformationSystemReleaseTypeQu.getInstance();
    AbstractLeafNode aln = null;
    Set<BuildingBlock> results = new HashSet<BuildingBlock>();

    aln = EnumAttributeLeafNode.createNode(resultType, null, 9, Comparator.LIKE, "B");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(aln);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    aln = EnumAttributeLeafNode.createNode(resultType, null, 9, Comparator.NOT_LIKE, "B");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(aln);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    aln = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NO_ASSIGNMENT, "");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(aln);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }

  /**
   * Create data for queries with result type Ipurelease and leaf type BusinessProcess.
   */
  private void createData2() {
    InformationSystem informationSystem = testDataHelper.createInformationSystem("I");
    InformationSystemRelease release1 = testDataHelper.createInformationSystemRelease(informationSystem, "i1");
    InformationSystemRelease release2 = testDataHelper.createInformationSystemRelease(informationSystem, "i2");
    InformationSystemRelease release3 = testDataHelper.createInformationSystemRelease(informationSystem, "i3");
    InformationSystemRelease release4 = testDataHelper.createInformationSystemRelease(informationSystem, "i4");

    testDataHelper.addChildToIsr(release1, release2);
    testDataHelper.addChildToIsr(release2, release3);

    BusinessProcess p1 = testDataHelper.createBusinessProcess("p1", "");
    BusinessProcess p2 = testDataHelper.createBusinessProcess("p2", "");
    BusinessProcess p3 = testDataHelper.createBusinessProcess("p3", "");
    BusinessProcess p4 = testDataHelper.createBusinessProcess("p4", "");

    testDataHelper.createBusinessMappingToProcess(release1, p1);
    testDataHelper.createBusinessMappingToProcess(release2, p2);
    testDataHelper.createBusinessMappingToProcess(release3, p3);
    testDataHelper.createBusinessMappingToProcess(release4, p4);

    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType type = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS);

    EnumAT enumAT1 = testDataHelper.createEnumAttributeType("a1", "", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT1, type);
    EnumAV enumAV11 = testDataHelper.createEnumAV("A", "", enumAT1);
    EnumAV enumAV12 = testDataHelper.createEnumAV("B", "", enumAT1);
    testDataHelper.createEnumAV("C", "", enumAT1);

    testDataHelper.createAVA(p1, enumAV11);
    testDataHelper.createAVA(p2, enumAV12);

    EnumAT enumAT2 = testDataHelper.createEnumAttributeType("a2", "", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT2, type);
    testDataHelper.createEnumAV("A", "", enumAT2);
    testDataHelper.createEnumAV("B", "", enumAT2);
    testDataHelper.createEnumAV("C", "", enumAT2);

    EnumAT enumAT3 = testDataHelper.createEnumAttributeType("a3", "", Boolean.TRUE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT3, type);
    EnumAV enumAV31 = testDataHelper.createEnumAV("A", "", enumAT3);
    EnumAV enumAV32 = testDataHelper.createEnumAV("B", "", enumAT3);
    EnumAV enumAV33 = testDataHelper.createEnumAV("C", "", enumAT3);
    testDataHelper.createEnumAV("D", "", enumAT3);

    testDataHelper.createAVA(p2, enumAV31);
    testDataHelper.createAVA(p2, enumAV32);
    testDataHelper.createAVA(p1, enumAV33);

    EnumAT enumAT4 = testDataHelper.createEnumAttributeType("a4", "", Boolean.TRUE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT4, type);
    testDataHelper.createEnumAV("A", "", enumAT4);
    testDataHelper.createEnumAV("B", "", enumAT4);
    testDataHelper.createEnumAV("C", "", enumAT4);
    testDataHelper.createEnumAV("D", "", enumAT4);
  }

  @Test
  public void testEnumForBusinessProcessSingle() {
    createData2();
    commit();
    beginTransaction();

    Type<InformationSystemRelease> releaseType = InformationSystemReleaseTypeQu.getInstance();
    Type<BusinessMapping> supportType = BusinessMappingTypeQu.getInstance();
    Type<BusinessProcess> resultType = BusinessProcessTypeQ.getInstance();

    Extension ext1 = releaseType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_BUSINESSMAPPINGS);
    Extension ext2 = supportType.getExtension(BusinessMappingTypeQu.EXTENSION_BUSINESSPROCESS);
    ExtensionNode root = new ExtensionNode(releaseType, ext1);
    ExtensionNode intermediary = new ExtensionNode(supportType, ext2);
    root.setChild(intermediary);
    AbstractLeafNode leaf = null;
    Set<? extends BuildingBlock> results = new HashSet<BuildingBlock>();

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.LIKE, "B");
    intermediary.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NOT_LIKE, "B");
    intermediary.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NO_ASSIGNMENT, "");
    intermediary.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }
  @Test
  public void testEnumForBusinessProcessMulti() {
    createData2();
    commit();

    beginTransaction();
    Type<InformationSystemRelease> releaseType = InformationSystemReleaseTypeQu.getInstance();
    Type<BusinessMapping> supportType = BusinessMappingTypeQu.getInstance();
    Type<BusinessProcess> resultType = BusinessProcessTypeQ.getInstance();

    Extension ext1 = releaseType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_BUSINESSMAPPINGS);
    Extension ext2 = supportType.getExtension(BusinessMappingTypeQu.EXTENSION_BUSINESSPROCESS);
    ExtensionNode root = new ExtensionNode(releaseType, ext1);
    ExtensionNode intermediary = new ExtensionNode(supportType, ext2);
    root.setChild(intermediary);
    AbstractLeafNode leaf = null;
    Set<? extends BuildingBlock> results = new HashSet<BuildingBlock>();

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 9, Comparator.LIKE, "B");
    intermediary.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 9, Comparator.NOT_LIKE, "B");
    intermediary.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NO_ASSIGNMENT, "");
    intermediary.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }

  /**
   * Create data for queries with result type Ittask and leaf type Ipurlease.
   */
  private void createData3() {
    Project task1 = testDataHelper.createProject("it1", "");
    Project task2 = testDataHelper.createProject("it2", "");
    Project task3 = testDataHelper.createProject("it3", "");
    Project task4 = testDataHelper.createProject("it4", "");

    testDataHelper.addElementOf(task1, task2);
    testDataHelper.addElementOf(task2, task3);

    InformationSystem informationSystem = testDataHelper.createInformationSystem("I");
    InformationSystemRelease release1 = testDataHelper.createInformationSystemRelease(informationSystem, "i1");
    InformationSystemRelease release2 = testDataHelper.createInformationSystemRelease(informationSystem, "i2");
    InformationSystemRelease release3 = testDataHelper.createInformationSystemRelease(informationSystem, "i3");
    InformationSystemRelease release4 = testDataHelper.createInformationSystemRelease(informationSystem, "i4");

    testDataHelper.addChildToIsr(release1, release2);
    testDataHelper.addChildToIsr(release2, release3);

    testDataHelper.addIsrToProject(release1, task1);
    testDataHelper.addIsrToProject(release2, task2);
    testDataHelper.addIsrToProject(release3, task3);
    testDataHelper.addIsrToProject(release4, task4);

    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType releaseType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

    EnumAT enumAT1 = testDataHelper.createEnumAttributeType("a1", "", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT1, releaseType);
    EnumAV enumAV11 = testDataHelper.createEnumAV("A", "", enumAT1);
    EnumAV enumAV12 = testDataHelper.createEnumAV("B", "", enumAT1);
    testDataHelper.createEnumAV("C", "", enumAT1);

    testDataHelper.createAVA(release1, enumAV11);
    testDataHelper.createAVA(release2, enumAV12);

    EnumAT enumAT2 = testDataHelper.createEnumAttributeType("a2", "", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT2, releaseType);
    testDataHelper.createEnumAV("A", "", enumAT2);
    testDataHelper.createEnumAV("B", "", enumAT2);
    testDataHelper.createEnumAV("C", "", enumAT2);

    EnumAT enumAT3 = testDataHelper.createEnumAttributeType("a3", "", Boolean.TRUE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT3, releaseType);
    EnumAV enumAV31 = testDataHelper.createEnumAV("A", "", enumAT3);
    EnumAV enumAV32 = testDataHelper.createEnumAV("B", "", enumAT3);
    EnumAV enumAV33 = testDataHelper.createEnumAV("C", "", enumAT3);
    testDataHelper.createEnumAV("D", "D desc", enumAT3);

    testDataHelper.createAVA(release1, enumAV33);
    testDataHelper.createAVA(release2, enumAV31);
    testDataHelper.createAVA(release2, enumAV32);

    EnumAT enumAT4 = testDataHelper.createEnumAttributeType("a4", "", Boolean.TRUE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT4, releaseType);
    testDataHelper.createEnumAV("A", "", enumAT4);
    testDataHelper.createEnumAV("B", "", enumAT4);
    testDataHelper.createEnumAV("C", "", enumAT4);
    testDataHelper.createEnumAV("D", "", enumAT4);
  }
  @Test
  public void testEnumForTaskSingle() {
    createData3();
    commit();

    beginTransaction();
    Type<Project> taskType = ProjectQueryType.getInstance();
    Type<InformationSystemRelease> resultType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = taskType.getExtension(ProjectQueryType.EXTENSION_INFORMATIONSYSTEMRELEASES);
    ExtensionNode root = new ExtensionNode(taskType, ex);
    AbstractLeafNode leaf = null;
    Set<? extends BuildingBlock> results = new HashSet<BuildingBlock>();

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.LIKE, "B");
    root.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NOT_LIKE, "B");
    root.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NO_ASSIGNMENT, "");
    root.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }
  @Test
  public void testEnumForTaskMulti() {
    createData3();
    commit();
    beginTransaction();

    Type<Project> taskType = ProjectQueryType.getInstance();
    Type<InformationSystemRelease> resultType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = taskType.getExtension(ProjectQueryType.EXTENSION_INFORMATIONSYSTEMRELEASES);
    ExtensionNode root = new ExtensionNode(taskType, ex);
    AbstractLeafNode leaf = null;
    Set<? extends BuildingBlock> results = new HashSet<BuildingBlock>();

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 9, Comparator.LIKE, "B");
    root.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 9, Comparator.NOT_LIKE, "B");
    root.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    leaf = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NO_ASSIGNMENT, "");
    root.setChild(leaf);
    results = getQueryDAO().evaluateQueryTree(root);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }

  /**
   * Create data for queries with result type BusinessObject.
   */
  private void createData4() {
    BusinessObject object1 = testDataHelper.createBusinessObject("b1", "");
    BusinessObject object2 = testDataHelper.createBusinessObject("b2", "");
    BusinessObject object3 = testDataHelper.createBusinessObject("b3", "");
    testDataHelper.createBusinessObject("b4", "");

    testDataHelper.addElementOf(object1, object2);
    testDataHelper.addElementOf(object2, object3);

    AttributeTypeGroup group = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType type = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSOBJECT);

    EnumAT enumAT1 = testDataHelper.createEnumAttributeType("a1", "", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT1, type);
    EnumAV enumAV11 = testDataHelper.createEnumAV("A", "", enumAT1);
    EnumAV enumAV12 = testDataHelper.createEnumAV("B", "", enumAT1);
    testDataHelper.createEnumAV("C", "", enumAT1);

    testDataHelper.createAVA(object1, enumAV11);
    testDataHelper.createAVA(object2, enumAV12);

    EnumAT enumAT2 = testDataHelper.createEnumAttributeType("a2", "", Boolean.FALSE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT2, type);
    testDataHelper.createEnumAV("A", "", enumAT2);
    testDataHelper.createEnumAV("B", "", enumAT2);
    testDataHelper.createEnumAV("C", "", enumAT2);

    EnumAT enumAT3 = testDataHelper.createEnumAttributeType("a3", "", Boolean.TRUE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT3, type);
    EnumAV enumAV31 = testDataHelper.createEnumAV("A", "", enumAT3);
    EnumAV enumAV32 = testDataHelper.createEnumAV("B", "", enumAT3);
    EnumAV enumAV33 = testDataHelper.createEnumAV("C", "", enumAT3);
    testDataHelper.createEnumAV("D", "", enumAT3);

    testDataHelper.createAVA(object1, enumAV33);
    testDataHelper.createAVA(object2, enumAV31);
    testDataHelper.createAVA(object2, enumAV32);

    EnumAT enumAT4 = testDataHelper.createEnumAttributeType("a4", "", Boolean.TRUE, group);
    testDataHelper.assignAttributeTypeToBuildingBlockType(enumAT4, type);
    testDataHelper.createEnumAV("A", "", enumAT4);
    testDataHelper.createEnumAV("B", "", enumAT4);
    testDataHelper.createEnumAV("C", "", enumAT4);
    testDataHelper.createEnumAV("D", "", enumAT4);
  }
  @Test
  public void testEnumForBusinessObjectSingle() {
    createData4();
    commit();
    beginTransaction();

    Type<BusinessObject> resultType = BusinessObjectTypeQu.getInstance();
    AbstractLeafNode leafNode = null;
    Set<BuildingBlock> results = new HashSet<BuildingBlock>();

    leafNode = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.LIKE, "B");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    // Regarding the next two assertions: The top-level element is returned from the queries as well
    // (thus, the result size is 3). This should usually not happen because top-level elements are
    // filtered out by a post-processing strategy.

    leafNode = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NOT_LIKE, "B");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 4, results.size());

    leafNode = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NO_ASSIGNMENT, "");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    commit();
  }
  @Test
  public void testEnumForBusinessObjectMulti() {
    createData4();
    commit();
    beginTransaction();

    Type<BusinessObject> resultType = BusinessObjectTypeQu.getInstance();
    AbstractLeafNode leafNode = null;
    Set<BuildingBlock> results = new HashSet<BuildingBlock>();

    leafNode = EnumAttributeLeafNode.createNode(resultType, null, 9, Comparator.LIKE, "B");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    // Regarding the next two assertions: The top-level element is returned from the queries as well
    // (thus, the result size is 3). This should usually not happen because top-level elements are
    // filtered out by a post-processing strategy.

    leafNode = EnumAttributeLeafNode.createNode(resultType, null, 9, Comparator.NOT_LIKE, "B");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 4, results.size());

    leafNode = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.NO_ASSIGNMENT, "");
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    commit();
  }
}