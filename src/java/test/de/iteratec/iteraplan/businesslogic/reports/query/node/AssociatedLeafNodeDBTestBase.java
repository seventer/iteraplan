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

import de.iteratec.iteraplan.businesslogic.reports.query.type.BusinessObjectTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.Extension;
import de.iteratec.iteraplan.businesslogic.reports.query.type.InformationSystemReleaseTypeQu;
import de.iteratec.iteraplan.businesslogic.reports.query.type.ProjectQueryType;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Test class for queries regarding non-associated building block instances.
 * 
 * @author Tobias Dietl (iteratec GmbH)
 * @version $Id: $
 */
public class AssociatedLeafNodeDBTestBase extends AbstractNodeTestBase {

  @Autowired
  private TestDataHelper2     testDataHelper;

  private static final String STANDARD_START_DATE_2005   = "1.1.2005";
  private static final String STANDARD_START_DATE_2005_B = "1.7.2005";
  private static final String STANDARD_START_DATE_2000   = "1.1.2000";
  private static final String STANDARD_END_DATE_2005     = "31.12.2005";
  private static final String STANDARD_END_DATE_2005_B   = "30.9.2005";
  private static final String STANDARD_END_DATE_2000     = "31.12.2001";
  private static final String STANDARD_END_DATE_2000_B   = "30.6.2000";

  private static final String WRONG_ELEMENT_NR_ERROR_MSG = "Wrong number of elements in result set!";

  private static final String TEST_DESCRIPTION           = "testDescription";

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testIpureleaseTbbreleaseArchitecturalDomainCorrect() {
    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);

    TechnicalComponent t = testDataHelper.createTechnicalComponent("T", true, true);
    TechnicalComponentRelease t1 = testDataHelper.createTCRelease(t, "t1", "t1 desc", STANDARD_START_DATE_2005, STANDARD_END_DATE_2005,
        TechnicalComponentRelease.TypeOfStatus.CURRENT, true);

    ArchitecturalDomain ad1 = testDataHelper.createArchitecturalDomain("ad1", "ad1 desc");

    testDataHelper.addTcrToIsr(i1, t1);
    testDataHelper.addTcrToIsr(i2, t1);
    testDataHelper.addADToTCRelease(t1, ad1);

    commit();

    // act and assert:
    beginTransaction();
    InformationSystemReleaseTypeQu ipurType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = ipurType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_ARCHITECTURALDOMAINS_VIA_TECHNICALCOMPONENT);
    AbstractLeafNode leafNode;
    Set<BuildingBlock> results;

    // act & assert:
    leafNode = new AssociatedLeafNode(ipurType, ex);
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }

  @Test
  public void testIpureleaseTbbreleaseCorrect() throws Exception {
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
    InformationSystemReleaseTypeQu ipurType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = ipurType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_TECHNICALCOMPONENTRELEASES);

    // act and assert:
    AbstractLeafNode leafNode = new AssociatedLeafNode(ipurType, ex);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();

  }

  @Test
  public void testIttaskIpureleaseCorrect() throws Exception {
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
    testDataHelper.addIsrToProject(i1, it2);

    commit();

    // act and assert:
    beginTransaction();
    ProjectQueryType ittaskQueryType = ProjectQueryType.getInstance();
    Extension ex = ittaskQueryType.getExtension(ProjectQueryType.EXTENSION_INFORMATIONSYSTEMRELEASES);

    // act and assert:
    AbstractLeafNode leafNode = new AssociatedLeafNode(ittaskQueryType, ex);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    commit();
  }

  @Test
  public void testIpureleaseIttaskCorrect() throws Exception {
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

    Project it = testDataHelper.createProject("it", "it desc", STANDARD_START_DATE_2000, "1.1.2003");

    testDataHelper.addIsrToProject(i2, it);

    commit();

    // act and assert:
    beginTransaction();
    InformationSystemReleaseTypeQu ipurType = InformationSystemReleaseTypeQu.getInstance();
    Extension ex = ipurType.getExtension(InformationSystemReleaseTypeQu.EXTENSION_PROJECTS);

    // act & assert:
    AbstractLeafNode leafNode = new AssociatedLeafNode(ipurType, ex);
    Set<BuildingBlock> results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 1, results.size());

    commit();
  }

  @Test
  public void testBusinessObjectIpureleaseCorrect() throws Exception {
    BusinessObject b1 = testDataHelper.createBusinessObject("b1", "b1 desc");
    BusinessObject b2 = testDataHelper.createBusinessObject("b2", "b2 desc");
    BusinessObject b3 = testDataHelper.createBusinessObject("b3", "b3 desc");
    testDataHelper.createBusinessObject("b4", "b4 desc");
    testDataHelper.addElementOf(b1, b2);
    testDataHelper.addElementOf(b2, b3);

    InformationSystem i = testDataHelper.createInformationSystem("I");
    InformationSystemRelease i1 = testDataHelper.createInformationSystemRelease(i, "i1", TEST_DESCRIPTION, STANDARD_START_DATE_2005,
        STANDARD_END_DATE_2005, InformationSystemRelease.TypeOfStatus.CURRENT);
    InformationSystemRelease i2 = testDataHelper.createInformationSystemRelease(i, "i2", TEST_DESCRIPTION, STANDARD_START_DATE_2005_B, null,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    InformationSystemRelease i3 = testDataHelper.createInformationSystemRelease(i, "i3", TEST_DESCRIPTION, null, STANDARD_END_DATE_2005_B,
        InformationSystemRelease.TypeOfStatus.PLANNED);
    testDataHelper.createInformationSystemRelease(i, "i4", TEST_DESCRIPTION, null, null, InformationSystemRelease.TypeOfStatus.CURRENT);
    testDataHelper.addChildToIsr(i1, i2);
    testDataHelper.addChildToIsr(i2, i3);

    testDataHelper.addBusinessObjectToInformationSystem(i1, b1);
    testDataHelper.addBusinessObjectToInformationSystem(i2, b1);
    testDataHelper.addBusinessObjectToInformationSystem(i2, b2);

    commit();

    // act and assert:
    beginTransaction();

    BusinessObjectTypeQu boType = BusinessObjectTypeQu.getInstance();
    Extension ex = boType.getExtension(BusinessObjectTypeQu.EXTENSION_INFORMATIONSYSTEMRELEASES);
    AbstractLeafNode leafNode;
    Set<BuildingBlock> results;

    // act & assert:
    leafNode = new AssociatedLeafNode(boType, ex);
    results = getQueryDAO().getResultSetForAbstractLeafNodeUsingCriteria(leafNode);
    assertEquals(WRONG_ELEMENT_NR_ERROR_MSG, 2, results.size());

    commit();
  }

}
