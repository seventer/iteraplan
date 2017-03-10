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
package de.iteratec.iteraplan.businesslogic.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Comparator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Node;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Operation;
import de.iteratec.iteraplan.businesslogic.reports.query.node.OperationNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.PropertyLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.HideSubInformationSystemReleasesStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.HideSubInformationSystemReleasesWithConnectionMergingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.IncludeConnectedInformationSystemReleasesStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessProcessTypeQ;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InfrastructureElementTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * Test class to check the correct evaluation of the query tree as a whole.
 */
public class QueryServiceTest extends BaseTransactionalTestSupport {

  @Autowired
  private QueryService          classUnderTest;
  @Autowired
  private AttributeTypeGroupDAO attributeTypeGroupDAO;
  @Autowired
  private BuildingBlockTypeDAO  buildingBlockTypeDAO;
  @Autowired
  private TestDataHelper2       testDataHelper;

  private static final Logger   LOGGER               = Logger.getIteraplanLogger(QueryServiceTest.class);
  private static final String   WRONG_NAME_ERROR_MSG = "wrong release name";
  private static final String   TEST_DESCRIPTION     = "testDescription";

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testEvaluateQueryTreeCorrect() throws Exception {
    Integer[] enumATIds = createTestDataEnumAttributes();

    commit();
    beginTransaction();

    LOGGER.info("checking BBAttributes for IS release");
    List<?> attributes = classUnderTest.getFixedAndUserdefAttributesForBBType(InformationSystemReleaseTypeQu.getInstance());

    assertEquals(8, attributes.size());
    BBAttribute attr = (BBAttribute) attributes.get(0);
    assertEquals(BBAttribute.FIXED_ATTRIBUTE_TYPE, attr.getType());
    assertEquals(InformationSystemReleaseTypeQu.PROPERTY_NAME, attr.getDbName());
    attr = (BBAttribute) attributes.get(1);
    assertEquals(BBAttribute.FIXED_ATTRIBUTE_TYPE, attr.getType());
    assertEquals(InformationSystemReleaseTypeQu.PROPERTY_VERSION, attr.getDbName());
    attr = (BBAttribute) attributes.get(2);
    assertEquals(BBAttribute.FIXED_ATTRIBUTE_TYPE, attr.getType());
    assertEquals(InformationSystemReleaseTypeQu.PROPERTY_DESCRIPTION, attr.getDbName());
    attr = (BBAttribute) attributes.get(3);
    assertEquals(BBAttribute.FIXED_ATTRIBUTE_TYPE, attr.getType());
    attr = (BBAttribute) attributes.get(4);
    assertEquals(BBAttribute.FIXED_ATTRIBUTE_DATETYPE, attr.getType());
    attr = (BBAttribute) attributes.get(5);
    assertEquals(BBAttribute.FIXED_ATTRIBUTE_SET, attr.getType());
    assertEquals(InformationSystemReleaseTypeQu.PROPERTY_SUBSCRIBED_USERS, attr.getDbName());

    attr = (BBAttribute) attributes.get(6);
    assertEquals(BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, attr.getType());
    assertEquals(enumATIds[0], attr.getId());
    attr = (BBAttribute) attributes.get(7);
    assertEquals(BBAttribute.USERDEF_ENUM_ATTRIBUTE_TYPE, attr.getType());
    assertEquals(enumATIds[1], attr.getId());

    BBAttribute bbAttr = (BBAttribute) attributes.get(7);

    LOGGER.info("checking BBAttributes for Business Process");
    attributes = classUnderTest.getFixedAndUserdefAttributesForBBType(BusinessProcessTypeQ.getInstance());
    assertEquals(7, attributes.size());

    LOGGER.info("checking BBAttributes for Infrastructure Element");
    attributes = classUnderTest.getFixedAndUserdefAttributesForBBType(InfrastructureElementTypeQu.getInstance());
    assertEquals(5, attributes.size());

    LOGGER.info("checking available value Strings for Ipurelease");
    List<?> attrVals = classUnderTest.getAttributeValuesForAttribute(InformationSystemReleaseTypeQu.getInstance(), bbAttr);
    assertEquals(3, attrVals.size());
    assertEquals(attrVals.get(0), "D");
    assertEquals(attrVals.get(1), "E");
    assertEquals(attrVals.get(2), "F");

    commit();
    beginTransaction();

  }

  private Integer[] createTestDataEnumAttributes() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, "1.1.2005", "31.12.2005",
        InformationSystemRelease.TypeOfStatus.CURRENT);
    testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, "1.7.2005", null, InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, "30.9.2005", InformationSystemRelease.TypeOfStatus.INACTIVE);

    BusinessProcess bp1 = testDataHelper.createBusinessProcess("bp1", "bp desc");
    testDataHelper.createBusinessProcess("bp2", "bp desc");

    AttributeTypeGroup atgStandard = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType ipurType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);
    BuildingBlockType bpType = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.BUSINESSPROCESS);

    EnumAT a1 = testDataHelper.createEnumAttributeType("AppOwner", "a1 desc", Boolean.FALSE, atgStandard);
    testDataHelper.assignAttributeTypeToBuildingBlockType(a1, ipurType);
    testDataHelper.assignAttributeTypeToBuildingBlockType(a1, bpType);

    EnumAV av1a = testDataHelper.createEnumAV("A", "A desc", a1);
    testDataHelper.createEnumAV("B", "B desc", a1);
    testDataHelper.createEnumAV("C", "C desc", a1);

    testDataHelper.createAVA(i1, av1a);

    EnumAT a2 = testDataHelper.createEnumAttributeType("TechOwner", "a2 desc", Boolean.FALSE, atgStandard);
    testDataHelper.assignAttributeTypeToBuildingBlockType(a2, ipurType);
    testDataHelper.assignAttributeTypeToBuildingBlockType(a2, bpType);

    EnumAV av2d = testDataHelper.createEnumAV("D", "D desc", a2);
    testDataHelper.createEnumAV("E", "E desc", a2);
    testDataHelper.createEnumAV("F", "F desc", a2);

    testDataHelper.createAVA(bp1, av2d);

    return new Integer[] { a1.getId(), a2.getId() };
  }

  @Test
  public void testConvertToBBAttributes() {
    QueryServiceImpl queryService = new QueryServiceImpl();
    List<BBAttribute> result = null;
    List<AttributeType> attributeTypes = new ArrayList<AttributeType>();

    AttributeType test = getDummyEnumAT("Test1", "Not Multivalue", false);
    attributeTypes.add(test);
    AttributeType test2 = getDummyEnumAT("Test2", "Multivalue", true);
    attributeTypes.add(test2);
    AttributeType test3 = getDummyRespAT("Test2", "Multivalue", true);
    attributeTypes.add(test3);
    AttributeType test4 = getDummyNumberAT("Test2", "Multivalue");
    attributeTypes.add(test4);

    result = queryService.convertToBBAttributes(attributeTypes);

    LOGGER.info("checking BBAttributes for multivalue attribute");
    assertEquals(attributeTypes.size(), result.size());
    assertFalse(result.get(0).isMultiValue());
    assertTrue(result.get(1).isMultiValue());
    assertTrue(result.get(2).isMultiValue());
    assertFalse(result.get(3).isMultiValue());
  }

  /**
   * Test evaluation of query tree using postprocessing strategy "show only top-level elements".
   * 
   * @throws Exception
   */
  @Test
  public void testEvaluateQueryTreeShowOnlyTopLevels() throws Exception {
    createMoreTestData();
    commit();
    beginTransaction();

    Type<?> isrType = InformationSystemReleaseTypeQu.getInstance();
    checkHideSubISRwithConnMerge(isrType);

    OperationNode root = new OperationNode(Operation.OR);
    Node leaf = new PropertyLeafNode(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_VERSION, Comparator.LIKE, "*i*",
        BBAttribute.FIXED_ATTRIBUTE_TYPE);
    root.addChild(leaf);

    checkShowOnlyTopLevelElements(root);

    checkShowConnectedElements(root);

    checkNoStrategy(root);
  }

  /**
   * check if no strategy works as well:
   * @param root
   */
  private void checkNoStrategy(OperationNode root) {
    List<AbstractPostprocessingStrategy<InformationSystemRelease>> strategies = new ArrayList<AbstractPostprocessingStrategy<InformationSystemRelease>>();

    List<InformationSystemRelease> result = classUnderTest.evaluateQueryTree(root, null, strategies);
    Collections.sort(result, new HierarchicalEntityCachingComparator<InformationSystemRelease>());

    assertEquals("Incorrect number of IS releases found!", 3, result.size());

    InformationSystemRelease isr1 = result.get(0);
    assertEquals(WRONG_NAME_ERROR_MSG, "I # i1", isr1.getHierarchicalName());

    isr1 = result.get(1);
    assertEquals(WRONG_NAME_ERROR_MSG, "I # i1 : I # i2", isr1.getHierarchicalName());

    isr1 = result.get(2);
    assertEquals(WRONG_NAME_ERROR_MSG, "I # i1 : I # i2 : I # i3", isr1.getHierarchicalName());
  }

  /**
   * check if "show connected elements" strategy works:
   * @param root
   */
  private void checkShowConnectedElements(OperationNode root) {
    List<AbstractPostprocessingStrategy<InformationSystemRelease>> strategies = new ArrayList<AbstractPostprocessingStrategy<InformationSystemRelease>>();
    strategies.add(new IncludeConnectedInformationSystemReleasesStrategy(Integer.valueOf(0)));

    List<InformationSystemRelease> result = classUnderTest.evaluateQueryTree(root, null, strategies);
    Collections.sort(result, new HierarchicalEntityCachingComparator<InformationSystemRelease>());

    assertEquals("Incorrect number of IS releases found!", 4, result.size());

    InformationSystemRelease isr1 = result.get(0);
    assertEquals(WRONG_NAME_ERROR_MSG, "I # i1", isr1.getHierarchicalName());

    isr1 = result.get(1);
    assertEquals(WRONG_NAME_ERROR_MSG, "I # i1 : I # i2", isr1.getHierarchicalName());

    isr1 = result.get(2);
    assertEquals(WRONG_NAME_ERROR_MSG, "I # i1 : I # i2 : I # i3", isr1.getHierarchicalName());

    InformationSystemRelease isr2 = result.get(3);
    assertEquals(WRONG_NAME_ERROR_MSG, "J # j1 : J # j2", isr2.getHierarchicalName());
  }

  /**
   * check if "show only top level elements" strategy works:
   * @param root
   */
  private void checkShowOnlyTopLevelElements(OperationNode root) {
    List<AbstractPostprocessingStrategy<InformationSystemRelease>> strategies = new ArrayList<AbstractPostprocessingStrategy<InformationSystemRelease>>();
    strategies.add(new HideSubInformationSystemReleasesStrategy(Integer.valueOf(0)));

    List<InformationSystemRelease> result = classUnderTest.evaluateQueryTree(root, null, strategies);
    Collections.sort(result, new HierarchicalEntityCachingComparator<InformationSystemRelease>());

    assertEquals("Incorrect number of IS releases found!", 1, result.size());

    InformationSystemRelease isr1 = result.get(0);
    assertEquals(WRONG_NAME_ERROR_MSG, "I # i1", isr1.getHierarchicalName());
  }

  /**
   * Check if "hide sub information system release with connection merging" strategy works:
   * @param isrType
   */
  private void checkHideSubISRwithConnMerge(Type<?> isrType) {
    OperationNode root = new OperationNode(Operation.OR);
    Node leaf = new PropertyLeafNode(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_VERSION, Comparator.LIKE, "*i*",
        BBAttribute.FIXED_ATTRIBUTE_TYPE);
    root.addChild(leaf);
    leaf = new PropertyLeafNode(isrType, null, InformationSystemReleaseTypeQu.PROPERTY_VERSION, Comparator.LIKE, "*j*",
        BBAttribute.FIXED_ATTRIBUTE_TYPE);
    root.addChild(leaf);

    List<AbstractPostprocessingStrategy<InformationSystemRelease>> strategies = new ArrayList<AbstractPostprocessingStrategy<InformationSystemRelease>>();
    strategies.add(new HideSubInformationSystemReleasesWithConnectionMergingStrategy(Integer.valueOf(0)));

    List<InformationSystemRelease> result = classUnderTest.evaluateQueryTree(root, null, strategies);
    for (InformationSystemRelease element : result) {
      LOGGER.info(element.getNonHierarchicalName());
    }
    Collections.sort(result, new HierarchicalEntityCachingComparator<InformationSystemRelease>());

    assertEquals("Incorrect number of IS releases found!", 2, result.size());

    InformationSystemRelease isr1 = result.get(0);
    assertEquals(WRONG_NAME_ERROR_MSG, "I # i1", isr1.getHierarchicalName());

    InformationSystemRelease isr2 = result.get(1);
    assertEquals(WRONG_NAME_ERROR_MSG, "J # j1", isr2.getHierarchicalName());

    LOGGER.info(String.valueOf(isr1.getInterfacesReleaseA().size()));
  }

  private void createMoreTestData() {

    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, "1.1.2005", "31.12.2005",
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, "1.7.2005", null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, "30.9.2005",
        InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    InformationSystem j = testDataHelper.createInformationSystem("J");
    InformationSystemRelease j1 = testDataHelper.createInformationSystemRelease(j, "j1", TEST_DESCRIPTION, "1.1.2005", "31.12.2005",
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease j2 = testDataHelper.createInformationSystemRelease(j, "j2", TEST_DESCRIPTION, "1.7.2005", null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease j3 = testDataHelper.createInformationSystemRelease(j, "j3", TEST_DESCRIPTION, null, "30.9.2005",
        InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.addChildToIsr(j1, j2);
    testDataHelper.addChildToIsr(j2, j3);

    testDataHelper.createInformationSystemInterface(i2, j2, null, "c1: i2-j2");
    testDataHelper.createInformationSystemInterface(j2, i2, null, "c2: j2-i2");

    Project ittask1 = testDataHelper.createProject("V-Task", "V-Task desc", "01.01.2003", "01.01.2007");

    testDataHelper.addIsrToProject(i1, ittask1);
    testDataHelper.addIsrToProject(j1, ittask1);
    testDataHelper.addIsrToProject(j2, ittask1);

    TechnicalComponent oTcr = testDataHelper.createTechnicalComponent("O-tbb", true, true);
    TechnicalComponent pTcr = testDataHelper.createTechnicalComponent("P-tbb", true, true);
    TechnicalComponentRelease tcRelease1 = testDataHelper.createTCRelease(oTcr, "o-r1", "o-r1 desc", "01.01.2003", "01.01.2005",
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    TechnicalComponentRelease tcRelease2 = testDataHelper.createTCRelease(pTcr, "p-r1", "p-r1 desc", "01.01.2003", "01.01.2005",
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);

    testDataHelper.addTcrToIsr(i1, tcRelease1);
    testDataHelper.addTcrToIsr(j1, tcRelease2);
    testDataHelper.addTcrToIsr(j2, tcRelease2);

  }

  private EnumAT getDummyEnumAT(String name, String descr, boolean multival) {
    EnumAT dummy = new EnumAT();
    dummy.setName(name);
    dummy.setDescription(descr);
    dummy.setMultiassignmenttype(multival);
    return dummy;
  }

  private ResponsibilityAT getDummyRespAT(String name, String descr, boolean multival) {
    ResponsibilityAT dummy = new ResponsibilityAT();
    dummy.setName(name);
    dummy.setDescription(descr);
    dummy.setMultiassignmenttype(multival);
    return dummy;
  }

  private NumberAT getDummyNumberAT(String name, String descr) {
    NumberAT dummy = new NumberAT();
    dummy.setName(name);
    dummy.setDescription(descr);
    return dummy;
  }
}
