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

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemInterfaceTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Type;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Test class for queries regarding non-associated building block instances.
 */
public class NotAssociatedLeafNodeDBTest extends AbstractNodeTestBase {

  private static final Logger LOGGER                     = Logger.getIteraplanLogger(NotAssociatedLeafNodeDBTest.class);

  private static final String STANDARD_START_DATE_2005   = "1.1.2005";
  private static final String STANDARD_START_DATE_2005_B = "1.7.2005";
  private static final String STANDARD_START_DATE_2000   = "1.1.2000";
  private static final String STANDARD_END_DATE_2005     = "31.12.2005";
  private static final String STANDARD_END_DATE_2005_B   = "30.9.2005";
  private static final String STANDARD_END_DATE_2000     = "31.12.2001";
  private static final String STANDARD_END_DATE_2000_B   = "30.6.2000";

  private static final String WRONG_ELEMENT_NR_ERROR_MSG = "Wrong number of elements in result set!";

  private static final String TEST_DESCRIPTION           = "testDescription";
  @Autowired
  private TestDataHelper2     testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testIsrTcrNAArchitecturalDomainCorrect() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);

    TechnicalComponent t = testDataHelper.createTechnicalComponent("T", true, true);
    TechnicalComponentRelease t1 = testDataHelper.createTCRelease(t, "t1", "t1 desc", STANDARD_START_DATE_2005, STANDARD_END_DATE_2005,
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);

    testDataHelper.addTcrToIsr(i1, t1);

    commit();

    // act and assert:
    beginTransaction();
    InformationSystemReleaseTypeQu isrType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_ARCHITECTURALDOMAINS_VIA_TECHNICALCOMPONENT);

    // act & assert:
    AbstractLeafNode leafNode = new NotAssociatedLeafNode(isrType, ex);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    commit();
  }

  @Test
  public void testIsrNATcrCorrect() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, STANDARD_END_DATE_2005_B,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    InformationSystem j = testDataHelper.createInformationSystem("J");
    InformationSystemRelease j1 = testDataHelper.createInformationSystemRelease(j, "j1", TEST_DESCRIPTION, STANDARD_START_DATE_2000,
        STANDARD_END_DATE_2000, InformationSystemRelease.TypeOfStatus.INACTIVE);

    TechnicalComponent t = testDataHelper.createTechnicalComponent("T", false, true);
    TechnicalComponentRelease t1 = testDataHelper.createTCRelease(t, "t1", TEST_DESCRIPTION, null, "31.12.2007",
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);

    testDataHelper.addTcrToIsr(i3, t1);
    testDataHelper.addTcrToIsr(j1, t1);

    commit();

    // act and assert:
    beginTransaction();
    InformationSystemReleaseTypeQu isrType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_TECHNICALCOMPONENTRELEASES);

    // act & assert:
    AbstractLeafNode leafNode = new NotAssociatedLeafNode(isrType, ex);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }

  @Test
  public void testIttaskNAIpureleaseCorrect() {
    Project it1 = testDataHelper.createProject("it1", "it1 desc", STANDARD_START_DATE_2000, "31.12.2000");
    Project it2 = testDataHelper.createProject("it2", "it2 desc", null, STANDARD_END_DATE_2000_B);
    Project it3 = testDataHelper.createProject("it3", "it3 desc", "1.3.2000", null);

    testDataHelper.addElementOf(it1, it2);
    testDataHelper.addElementOf(it2, it3);

    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, STANDARD_END_DATE_2005_B,
        InformationSystemRelease.TypeOfStatus.PLANNED);

    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    testDataHelper.addIsrToProject(i2, it2);

    commit();

    // act and assert:
    beginTransaction();
    ProjectQueryType projectQueryType = ProjectQueryType.getInstance();
    Extension ex = projectQueryType.getExtension(ProjectQueryType.EXTENSION_INFORMATIONSYSTEMRELEASES);

    // Assert.
    // The top-level element is returned from the query as well (thus the result size if 3).
    // This should usually not happen because top-level elements get filtered out by a
    // post-processing strategy.
    AbstractLeafNode leafNode = new NotAssociatedLeafNode(projectQueryType, ex);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    commit();
  }

  @Test
  public void testIsrNAProjectCorrect() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, STANDARD_END_DATE_2005_B,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    InformationSystem j = testDataHelper.createInformationSystem("J");
    testDataHelper.createInformationSystemRelease(j, "j1", TEST_DESCRIPTION, STANDARD_START_DATE_2000, STANDARD_END_DATE_2000,
        InformationSystemRelease.TypeOfStatus.INACTIVE);

    Project project = testDataHelper.createProject("it", "it desc", STANDARD_START_DATE_2000, "1.1.2003");

    testDataHelper.addIsrToProject(i2, project);

    commit();

    // act and assert:
    beginTransaction();

    InformationSystemReleaseTypeQu isrType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_PROJECTS);

    // act & assert:
    AbstractLeafNode leafNode = new NotAssociatedLeafNode(isrType, ex);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    commit();
  }

  @Test
  public void testConnectionNAIsrCorrect() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, STANDARD_END_DATE_2005_B,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    InformationSystem j = testDataHelper.createInformationSystem("J");
    InformationSystemRelease j1 = testDataHelper.createInformationSystemRelease(j, "j1", TEST_DESCRIPTION, STANDARD_START_DATE_2000,
        STANDARD_END_DATE_2000, InformationSystemRelease.TypeOfStatus.INACTIVE);

    TechnicalComponent t = testDataHelper.createTechnicalComponent("T", false, true);
    TechnicalComponentRelease t1 = testDataHelper.createTCRelease(t, "t1", TEST_DESCRIPTION, null, "31.12.2007",
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);
    t1.getTechnicalComponent().setAvailableForInterfaces(true);

    testDataHelper.createInformationSystemInterface(i1, j1, t1, "desc c1");
    testDataHelper.createInformationSystemInterface(i2, j1, null, "desc c2");
    testDataHelper.createInformationSystemInterface(i3, j1, null, "desc c3");

    commit();

    // act and assert:
    beginTransaction();

    InformationSystemInterfaceTypeQu connType = InformationSystemInterfaceTypeQu.getInstance();
    Extension ex = connType.getExtension(InformationSystemInterfaceTypeQu.EXTENSION_TECHNICALCOMPONENTRELEASE);
    commit();
    beginTransaction();
    // act & assert:
    AbstractLeafNode leafNode = new NotAssociatedLeafNode(connType, ex);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }

  @Test
  public void testConnectionNAFromIsrCorrect() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, STANDARD_END_DATE_2005_B,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    InformationSystem j = testDataHelper.createInformationSystem("J");
    InformationSystemRelease j1 = testDataHelper.createInformationSystemRelease(j, "j1", TEST_DESCRIPTION, STANDARD_START_DATE_2000,
        STANDARD_END_DATE_2000, InformationSystemRelease.TypeOfStatus.INACTIVE);

    testDataHelper.createInformationSystemInterface(i1, j1, null, "desc c1");
    testDataHelper.createInformationSystemInterface(i2, j1, null, "desc c2");
    testDataHelper.createInformationSystemInterface(i3, j1, null, "desc c3");

    commit();

    // act and assert:
    beginTransaction();

    InformationSystemInterfaceTypeQu connType = InformationSystemInterfaceTypeQu.getInstance();
    Extension ex = connType.getExtension(InformationSystemInterfaceTypeQu.EXTENSION_INFORMATIONSYSTEMRELEASE_A);

    // act & assert:
    AbstractLeafNode leafNode = new NotAssociatedLeafNode(connType, ex);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 0, results.size());

    commit();

  }

  /**
   * Specially interesting as the two extensions have to be connected with OR.
   */
  @Test
  public void testIpureleaseConnectionNABusinessObject() {
    arrangeIsrConnectionDb();
    commit();
    beginTransaction();

    // act and assert:

    Type<InformationSystemRelease> isrType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex1 = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_BUSINESSOBJECT_VIA_INTERFACES_A);
    Extension ex2 = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_BUSINESSOBJECS_VIA_INTERFACES_B);
    // caution: the extensions have to be connected with an "or" here:
    OperationNode root = new OperationNode(Operation.OR);

    LOGGER.debug("act & assert: ipurelease with connection without business objects");
    AbstractLeafNode leaf1 = new NotAssociatedLeafNode(isrType, ex1);
    root.addChild(leaf1);
    AbstractLeafNode leaf2 = new NotAssociatedLeafNode(isrType, ex2);
    root.addChild(leaf2);
    Set<BuildingBlock> results = getQueryDAO().evaluateQueryTree(root);
    for (BuildingBlock isr : results) {
      LOGGER.debug("result: " + ((InformationSystemRelease) isr).getHierarchicalName());
    }
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 3, results.size());

    commit();
  }

  /**
   * Specially interesting as the two extensions have to be connected with AND.
   * 
   * @throws Exception
   */
  @Test
  public void testIsrNAConnection() {
    arrangeIsrConnectionDb();
    commit();
    beginTransaction();

    // act and assert:

    Type<InformationSystemRelease> isrType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex1 = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_INTERFACES_A);
    Extension ex2 = isrType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_INTERFACES_B);
    // caution: the extensions have to be connected with an "and" here:
    OperationNode root = new OperationNode(Operation.AND);

    LOGGER.debug("act & assert: ipurelease with connection without business objects");
    AbstractLeafNode leaf1 = new NotAssociatedLeafNode(isrType, ex1);
    root.addChild(leaf1);
    AbstractLeafNode leaf2 = new NotAssociatedLeafNode(isrType, ex2);
    root.addChild(leaf2);
    Set<BuildingBlock> results = getQueryDAO().evaluateQueryTree(root);
    for (BuildingBlock isr : results) {
      LOGGER.debug("result: " + ((InformationSystemRelease) isr).getHierarchicalName());
    }
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 0, results.size());

    commit();
  }

  @Test
  public void testConnectionNABusinessObject() {
    arrangeIsrConnectionDb();
    commit();
    beginTransaction();

    // act and assert:

    Type<InformationSystemInterface> connType = InformationSystemInterfaceTypeQu.getInstance();
    Extension ex = connType.getExtension(InformationSystemInterfaceTypeQu.EXTENSION_BUSINESSOBJECT);

    LOGGER.debug("act & assert: connection without business objects");
    AbstractLeafNode leaf = new NotAssociatedLeafNode(connType, ex);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leaf);
    for (BuildingBlock isr : results) {
      LOGGER.debug("result: " + ((InformationSystemInterface) isr).getInterfaceInformation());
    }

    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }

  private void arrangeIsrConnectionDb() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, STANDARD_END_DATE_2005_B,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    InformationSystem j = testDataHelper.createInformationSystem("J");
    InformationSystemRelease j1 = testDataHelper.createInformationSystemRelease(j, "j1", TEST_DESCRIPTION, STANDARD_START_DATE_2000,
        STANDARD_END_DATE_2000, InformationSystemRelease.TypeOfStatus.INACTIVE);

    testDataHelper.createInformationSystemInterface(i1, j1, null, "desc c1");
    testDataHelper.createInformationSystemInterface(i2, j1, null, "desc c2");
    InformationSystemInterface c3 = testDataHelper.createInformationSystemInterface(i3, j1, null, "desc c3");

    BusinessObject bo3 = testDataHelper.createBusinessObject("bo3", "bo3 desc");
    testDataHelper.createTransport(bo3, c3, Direction.BOTH_DIRECTIONS);

    commit();
  }

}