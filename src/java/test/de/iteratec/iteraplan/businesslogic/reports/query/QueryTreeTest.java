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
package de.iteratec.iteraplan.businesslogic.reports.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.query.node.AbstractLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.AssociatedLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Comparator;
import de.iteratec.iteraplan.businesslogic.reports.query.node.EnumAttributeLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.ExtensionNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.Operation;
import de.iteratec.iteraplan.businesslogic.reports.query.node.OperationNode;
import de.iteratec.iteraplan.businesslogic.reports.query.node.PropertyLeafNode;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.AbstractPostprocessingStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.postprocessing.RemoveTopLevelElementStrategy;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ArchitecturalDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessDomainQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessFunctionQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessMappingTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessObjectTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessProcessTypeQ;
import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessUnitQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.IPresentationExtension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemDomainTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemInterfaceTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InfrastructureElementTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProductQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.TechnicalComponentReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.businesslogic.service.QueryService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.BBAttribute;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeGroupDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * Test cases to check the correct evaluation of the query tree.
 */
public class QueryTreeTest extends BaseTransactionalTestSupport {

  private static final Logger   LOGGER = Logger.getIteraplanLogger(QueryTreeTest.class);

  @Autowired
  private QueryService          queryService;
  @Autowired
  private AttributeTypeGroupDAO attributeTypeGroupDAO;
  @Autowired
  private BuildingBlockTypeDAO  buildingBlockTypeDAO;
  @Autowired
  private TestDataHelper2       testDataHelper;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  public void testEvaluateQueryTreeSimple() {
    arrangeDatabase();
    commit();

    // Assert.
    beginTransaction();

    InformationSystemReleaseTypeQu resultType = InformationSystemReleaseTypeQu.getInstance();
    AbstractLeafNode leafNode1, leafNode2, leafNode3;

    leafNode1 = new PropertyLeafNode(resultType, null, InformationSystemReleaseTypeQu.PROPERTY_DESCRIPTION, Comparator.LIKE, "*i3*",
        BBAttribute.FIXED_ATTRIBUTE_TYPE);
    leafNode2 = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.LIKE, "A");
    leafNode3 = EnumAttributeLeafNode.createNode(resultType, null, 1, Comparator.LIKE, "B");

    OperationNode or = new OperationNode(Operation.OR).addChild(leafNode2).addChild(leafNode3);
    OperationNode root = new OperationNode(Operation.AND).addChild(or).addChild(leafNode1);

    // Evaluate tree.
    List<BuildingBlock> results = queryService.evaluateQueryTree(root, null, null);
    assertEquals("Wrong number of elements in result set!", 0, results.size());

    commit();
  }

  public void testEvaluateQueryTreeLastModified() {
    arrangeDatabase();
    commit();
    beginTransaction();

    final InformationSystemReleaseTypeQu resultType = InformationSystemReleaseTypeQu.getInstance();
    AbstractLeafNode leafNode1;
    AbstractLeafNode leafNode2;

    leafNode1 = new PropertyLeafNode(resultType, null, InformationSystemReleaseTypeQu.PROPERTY_LAST_MODIFICATION_DATE, Comparator.ANY_ASSIGNMENT, "",
        BBAttribute.FIXED_ATTRIBUTE_DATETYPE);
    leafNode2 = new PropertyLeafNode(resultType, null, InformationSystemReleaseTypeQu.PROPERTY_LAST_MODIFICATION_DATE, Comparator.NO_ASSIGNMENT, "",
        BBAttribute.FIXED_ATTRIBUTE_DATETYPE);

    OperationNode root = new OperationNode(Operation.AND).addChild(leafNode1);
    List<BuildingBlock> results = queryService.evaluateQueryTree(root, null, null);
    assertEquals("Wrong number of elements in result set for any assignment!", 3, results.size());

    root = new OperationNode(Operation.AND).addChild(leafNode2);
    results = queryService.evaluateQueryTree(root, null, null);
    assertEquals("Wrong number of elements in result set for no assignment!", 0, results.size());

    root = new OperationNode(Operation.AND).addChild(leafNode1).addChild(leafNode2);
    results = queryService.evaluateQueryTree(root, null, null);
    assertEquals("Wrong number of elements in result set for 'any and no' assignment!", 0, results.size());

    commit();
  }

  /**
   * Tests a query for Information Systems which are connected to Technical Components 
   * with certain properties
   */
  public void testEvaluateQueryTreeWithExtension() {

    arrangeDatabase();
    commit();

    // Assert.
    beginTransaction();

    InformationSystemReleaseTypeQu isrType = InformationSystemReleaseTypeQu.getInstance();
    TechnicalComponentReleaseTypeQu tcrType = TechnicalComponentReleaseTypeQu.getInstance();
    Extension ex = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_TECHNICALCOMPONENTRELEASES);

    OperationNode root = new OperationNode(Operation.AND);

    ExtensionNode extensionNode = new ExtensionNode(isrType, ex);
    root.addChild(extensionNode);

    OperationNode andNode = new OperationNode(Operation.AND);
    extensionNode.setChild(andNode);

    AbstractLeafNode leafNode1 = new PropertyLeafNode(tcrType, null, TechnicalComponentReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TechnicalComponentRelease.TypeOfStatus.CURRENT, BBAttribute.FIXED_ATTRIBUTE_TYPE);
    andNode.addChild(leafNode1);
    OperationNode orNode = new OperationNode(Operation.OR);
    andNode.addChild(orNode);

    AbstractLeafNode leafNode2 = new PropertyLeafNode(tcrType, null, TechnicalComponentReleaseTypeQu.PROPERTY_DESCRIPTION, Comparator.LIKE,
        "t1 desc", BBAttribute.FIXED_ATTRIBUTE_TYPE);
    orNode.addChild(leafNode2);
    AbstractLeafNode leafNode3 = new PropertyLeafNode(tcrType, null, TechnicalComponentReleaseTypeQu.PROPERTY_DESCRIPTION, Comparator.LIKE,
        "t2 desc", BBAttribute.FIXED_ATTRIBUTE_TYPE);
    orNode.addChild(leafNode3);

    LOGGER.debug("act & assert: evaluate sub query tree:");
    List<BuildingBlock> subResults = queryService.evaluateQueryTree(andNode, null, null);
    assertNotNull(subResults);
    assertEquals("sub query result set has wrong size.", 1, subResults.size());
    TechnicalComponentRelease tcr = (TechnicalComponentRelease) subResults.get(0);
    assertEquals("Wrong Technical Component found in sub result set.", "T # t1", tcr.getReleaseName());

    LOGGER.debug("act & assert: evaluate whole query tree:");
    List<BuildingBlock> completeResults = queryService.evaluateQueryTree(root, null, null);
    assertNotNull(completeResults);
    assertEquals("full query result set has wrong size.", 1, subResults.size());
    InformationSystemRelease isr = (InformationSystemRelease) completeResults.get(0);
    assertEquals("Wrong IS release name found in full result set.", "I # i1 : I # i2", isr.getHierarchicalName());

    commit();

  }

  /**
   * Test for IS releases query which has query extensions for technical components and projects
   */
  public void testEvaluateQueryTreeWithTwoExtensions() {

    arrangeDatabase();
    commit();

    // Assert.
    beginTransaction();

    InformationSystemReleaseTypeQu isrType = InformationSystemReleaseTypeQu.getInstance();
    TechnicalComponentReleaseTypeQu tcrType = TechnicalComponentReleaseTypeQu.getInstance();
    Extension ex1 = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_TECHNICALCOMPONENTRELEASES);
    ProjectQueryType projectType = ProjectQueryType.getInstance();
    Extension ex2 = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_PROJECTS);

    ExtensionNode extensionNode1 = new ExtensionNode(isrType, ex1);
    OperationNode andNodeEx1 = new OperationNode(Operation.AND);
    extensionNode1.setChild(andNodeEx1);
    AbstractLeafNode leafNode1 = new PropertyLeafNode(tcrType, null, TechnicalComponentReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
        TechnicalComponentRelease.TypeOfStatus.CURRENT, BBAttribute.FIXED_ATTRIBUTE_TYPE);
    andNodeEx1.addChild(leafNode1);

    ExtensionNode extensionNode2 = new ExtensionNode(isrType, ex2);
    OperationNode andNodeEx2 = new OperationNode(Operation.AND);
    extensionNode2.setChild(andNodeEx2);
    AbstractLeafNode leafNode2 = new PropertyLeafNode(projectType, null, ProjectQueryType.PROPERTY_NAME, Comparator.NOT_LIKE, "Other*",
        BBAttribute.FIXED_ATTRIBUTE_TYPE);
    andNodeEx2.addChild(leafNode2);

    OperationNode root = new OperationNode(Operation.AND);
    root.addChild(extensionNode1);
    root.addChild(extensionNode2);

    LOGGER.debug("act & assert: evaluate sub query tree 1");
    List<BuildingBlock> subResults1 = queryService.evaluateQueryTree(andNodeEx1, null, null);
    assertNotNull(subResults1);
    assertEquals("sub query result set has wrong size.", 1, subResults1.size());
    TechnicalComponentRelease tcr1 = (TechnicalComponentRelease) subResults1.get(0);
    assertEquals("Wrong Technical Component found in sub result set.", "T # t1", tcr1.getReleaseName());

    LOGGER.debug("act & assert: evaluate whole query tree 1");
    List<BuildingBlock> fullResults1 = queryService.evaluateQueryTree(extensionNode1, null, null);
    assertNotNull(fullResults1);
    assertEquals("whole query result set has wrong size.", 1, fullResults1.size());
    InformationSystemRelease isr1 = (InformationSystemRelease) fullResults1.get(0);
    assertEquals("Wrong IS release name found in sub result set.", "I # i1 : I # i2", isr1.getHierarchicalName());

    LOGGER.debug("act & assert: evaluate sub query tree 2");
    List<AbstractPostprocessingStrategy<Project>> strategies = new ArrayList<AbstractPostprocessingStrategy<Project>>();
    strategies.add(new RemoveTopLevelElementStrategy<Project>());
    List<Project> subResults2 = queryService.evaluateQueryTree(andNodeEx2, null, strategies);
    assertNotNull(subResults2);
    assertEquals("sub query result set has wrong size.", 1, subResults2.size());
    Project pr2 = subResults2.get(0);
    assertEquals("Wrong project name found in sub result set.", "Project 1", pr2.getName());

    LOGGER.debug("act & assert: evaluate whole query tree 2");
    List<BuildingBlock> fullResults2 = queryService.evaluateQueryTree(extensionNode2, null, null);
    assertNotNull(fullResults2);
    assertEquals("whole query result set has wrong size.", 1, fullResults2.size());
    InformationSystemRelease isr2 = (InformationSystemRelease) fullResults2.get(0);
    assertEquals("Wrong IS release name found in sub result set.", "I # i1 : I # i2", isr2.getHierarchicalName());

    LOGGER.debug("act & assert: evaluate complete query tree (2 extensions):");
    List<BuildingBlock> completeResults = queryService.evaluateQueryTree(root, null, null);
    assertNotNull(completeResults);
    assertEquals("full query result set has wrong size.", 1, completeResults.size());
    InformationSystemRelease isrComplete = (InformationSystemRelease) completeResults.get(0);
    assertEquals("Wrong IS release name found in full result set.", "I # i1 : I # i2", isrComplete.getHierarchicalName());

    commit();
  }

  @Test
  public void testEvaluateAttributableAssociation() {
    arrangeDatabase();
    commit();

    List<Type<?>> types = new ArrayList<Type<?>>();
    types.add(ArchitecturalDomainTypeQu.getInstance());
    types.add(BusinessDomainQueryType.getInstance());
    types.add(BusinessFunctionQueryType.getInstance());
    types.add(BusinessMappingTypeQu.getInstance());
    types.add(BusinessObjectTypeQu.getInstance());
    types.add(BusinessProcessTypeQ.getInstance());
    types.add(BusinessUnitQueryType.getInstance());
    types.add(InformationSystemDomainTypeQu.getInstance());
    types.add(InformationSystemInterfaceTypeQu.getInstance());
    types.add(InformationSystemReleaseTypeQu.getInstance());
    types.add(InfrastructureElementTypeQu.getInstance());
    types.add(ProductQueryType.getInstance());
    types.add(ProjectQueryType.getInstance());
    types.add(TechnicalComponentReleaseTypeQu.getInstance());

    new QueryTreeGenerator(Locale.GERMAN, null);

    for (Type<?> startType : types) {
      // Assert.
      beginTransaction();

      Map<String, IPresentationExtension> typeExtensions = startType.getExtensions();
      for (Map.Entry<String, IPresentationExtension> extensionEntry : typeExtensions.entrySet()) {
        Extension ext = (Extension) extensionEntry.getValue();

        ext.getRequestedType();
        OperationNode root = new OperationNode(Operation.AND);

        OperationNode andNode0 = new OperationNode(Operation.AND);
        root.addChild(andNode0);

        if (startType.isHasStatus()) {
          AbstractLeafNode leafNode1 = new PropertyLeafNode(startType, null, TechnicalComponentReleaseTypeQu.PROPERTY_TYPEOFSTATUS, Comparator.LIKE,
              TechnicalComponentRelease.TypeOfStatus.CURRENT, BBAttribute.FIXED_ATTRIBUTE_TYPE);
          andNode0.addChild(leafNode1);
        }

        AssociatedLeafNode assocNode = new AssociatedLeafNode(startType, ext);
        andNode0.addChild(assocNode);

        LOGGER
            .debug("attempting to evaluate " + startType.getTypeNamePresentationKey() + " with an extension for " + ext.getNameKeyForPresentation());
        queryService.evaluateQueryTree(root, null, null);
      }

      rollback();
    }
  }

  /**
   * Creates the test database.
   */
  private void arrangeDatabase() {

    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", "i # i1", null, null,
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", "i # i2", null, null,
        InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", "i # i3", null, null,
        InformationSystemRelease.TypeOfStatus.CURRENT);

    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    // ///////////////////////////////

    AttributeTypeGroup atg = attributeTypeGroupDAO.getStandardAttributeTypeGroup();
    BuildingBlockType type = buildingBlockTypeDAO.getBuildingBlockTypeByType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE);

    EnumAT a1 = testDataHelper.createEnumAttributeType("a1", "a1 desc", Boolean.FALSE, atg);
    testDataHelper.assignAttributeTypeToBuildingBlockType(a1, type);

    EnumAV av1a = testDataHelper.createEnumAV("A", "A desc", a1);
    testDataHelper.createEnumAV("B", "B desc", a1);
    testDataHelper.createEnumAV("C", "C desc", a1);

    testDataHelper.createAVA(i1, av1a);

    // ///////////////////////////////

    TechnicalComponent t = testDataHelper.createTechnicalComponent("T", true, true);
    TechnicalComponentRelease t1 = testDataHelper.createTCRelease(t, "t1", "t1 desc", null, null, TechnicalComponentRelease.TypeOfStatus.CURRENT,
        true);
    TechnicalComponentRelease t2 = testDataHelper.createTCRelease(t, "t2", "t2 desc", null, null, TechnicalComponentRelease.TypeOfStatus.PLANNED,
        true);
    TechnicalComponentRelease t3 = testDataHelper
        .createTCRelease(t, "t3", "t3 desc", null, null, TechnicalComponentRelease.TypeOfStatus.TARGET, true);

    testDataHelper.addTcrToIsr(i2, t1);
    testDataHelper.addTcrToIsr(i2, t2);
    testDataHelper.addTcrToIsr(i2, t3);

    // ///////////////////////////////

    Project p1 = testDataHelper.createProject("Project 1", "IT1 desc", "1.7.2005", "31.12.2005");
    Project p2 = testDataHelper.createProject("Other Project 2", "IT2 desc");
    Project p3 = testDataHelper.createProject("Other Project 3", "IT3 desc", "1.1.2006", null);

    testDataHelper.addIsrToProject(i2, p1);
    testDataHelper.addIsrToProject(i2, p2);
    testDataHelper.addIsrToProject(i2, p3);

    // //////////////////////////////
    InfrastructureElement ie1 = testDataHelper.createInfrastructureElement("IE1", "Ie1 desc");
    InfrastructureElement ie2 = testDataHelper.createInfrastructureElement("IE2", "Ie2 desc");
    InfrastructureElement ie3 = testDataHelper.createInfrastructureElement("IE3", "Ie3 desc");

    testDataHelper.addIeToTcr(t1, ie1);
    testDataHelper.addIeToTcr(t2, ie2);
    testDataHelper.addIeToTcr(t3, ie3);
  }

}