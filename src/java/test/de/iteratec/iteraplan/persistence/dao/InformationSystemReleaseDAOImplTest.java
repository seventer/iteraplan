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
package de.iteratec.iteraplan.persistence.dao;

import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Test class for the DAO {@link InformationSystemReleaseDAO}.
 */
public class InformationSystemReleaseDAOImplTest extends BaseTransactionalTestSupport {

  private static final String             TEST_DESCRIPTION = "testDescription";
  private static final String             VERSION          = "version";

  @Autowired
  private InformationSystemReleaseDAOImpl informationSystemReleaseDAO;
  @Autowired
  private TestDataHelper2                 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.InformationReleaseReleaseDAOImpl#onBeforeDelete(de.iteratec.iteraplan.model.InformationReleaseRelease)}
   */
  @SuppressWarnings("PMD")
  @Test
  public void testOnBeforeDelete() {
    InformationSystem is = testDataHelper.createInformationSystem("is");
    InformationSystemRelease entity = testDataHelper.createInformationSystemRelease(is, VERSION);

    InfrastructureElement ie = testDataHelper.createInfrastructureElement("testIE", TEST_DESCRIPTION);
    BusinessObject bo = testDataHelper.createBusinessObject("testBO", TEST_DESCRIPTION);

    TechnicalComponent tc = testDataHelper.createTechnicalComponent("tc", true, true);
    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, VERSION, true);

    InformationSystem secIS = testDataHelper.createInformationSystem("secIS");
    InformationSystemRelease successor = testDataHelper.createInformationSystemRelease(secIS, "successor");
    InformationSystemRelease predecessor = testDataHelper.createInformationSystemRelease(secIS, "predecessor");

    Project project = testDataHelper.createProject("testProject", TEST_DESCRIPTION);

    InformationSystemDomain domain = testDataHelper.createInformationSystemDomain("testDomain", TEST_DESCRIPTION);

    InformationSystemRelease parent = testDataHelper.createInformationSystemRelease(is, "parent");
    InformationSystemRelease child = testDataHelper.createInformationSystemRelease(is, "child");

    InformationSystemInterface isi = testDataHelper.createInformationSystemInterface(entity, entity, tcr, TEST_DESCRIPTION);

    commit();

    beginTransaction();
    entity.addInfrastructureElement(ie);
    testDataHelper.addBusinessObjectToInformationSystem(entity, bo);
    entity.addTechnicalComponentRelease(tcr);
    entity.addSuccessor(successor);
    entity.addPredecessor(predecessor);
    entity.addProject(project);
    entity.addInformationSystemDomain(domain);

    Set<InformationSystemInterface> isrA = hashSet();

    isrA.add(isi);

    entity.setInterfacesReleaseA(isrA);
    entity.setInterfacesReleaseB(isrA);

    child.addSuccessor(successor);
    entity.addChild(child);
    entity.addParent(parent);
    commit();

    informationSystemReleaseDAO.onBeforeDelete(entity);

    Set<InfrastructureElement> actualIEs = entity.getInfrastructureElements();
    Set<InfrastructureElement> expectedIEs = hashSet();

    Set<BusinessObject> actualBOs = entity.getBusinessObjects();
    Set<BusinessObject> expectedBOs = Sets.newHashSet(bo); // the isr2BoAssociations will not be deleted

    Set<TechnicalComponentRelease> actualTCRs = entity.getTechnicalComponentReleases();
    Set<TechnicalComponentRelease> expectedTCRs = hashSet();

    Set<InformationSystemRelease> actualChildren = child.getChildren();
    Set<InformationSystemRelease> expectedChildren = hashSet();

    InformationSystemRelease actualParent = entity.getParent();
    Set<InformationSystemRelease> actualChildSuccessors = child.getSuccessors();
    Set<InformationSystemRelease> actualSuccessors = entity.getSuccessors();
    Set<InformationSystemRelease> expectedSuccessors = hashSet();

    Set<InformationSystemRelease> actualPredecessors = entity.getPredecessors();
    Set<InformationSystemRelease> expectedPredecessors = hashSet();

    Set<Project> actualProjects = entity.getProjects();
    Set<Project> expectedProjects = hashSet();

    Set<InformationSystemDomain> actualISDs = entity.getInformationSystemDomains();
    Set<InformationSystemDomain> expectedISDs = hashSet();

    Set<InformationSystemInterface> actualInterfacesA = entity.getInterfacesReleaseA();
    Set<InformationSystemInterface> expectedInferfacesA = hashSet();

    Set<InformationSystemInterface> actualInterfacesB = entity.getInterfacesReleaseB();
    Set<InformationSystemInterface> expectedInferfacesB = hashSet();

    Set<InformationSystemRelease> actualISRs = is.getReleases();

    Set<InformationSystemRelease> actualBaseComponents = entity.getBaseComponents();
    Set<InformationSystemRelease> expectedBaseComponents = hashSet();

    Set<InformationSystemRelease> actualParentComponents = entity.getParentComponents();
    Set<InformationSystemRelease> expectedParentComponents = hashSet();

    // after the delete operation the entity should not have any associations.
    assertEquals(expectedIEs, actualIEs);
    assertEquals(expectedBOs, actualBOs);
    assertEquals(expectedTCRs, actualTCRs);
    assertEquals(expectedSuccessors, actualSuccessors);
    assertEquals(expectedSuccessors, actualChildSuccessors);
    assertEquals(expectedPredecessors, actualPredecessors);
    assertEquals(expectedProjects, actualProjects);
    assertEquals(expectedISDs, actualISDs);
    assertEquals(expectedInferfacesA, actualInterfacesA);
    assertEquals(expectedInferfacesB, actualInterfacesB);

    assertEquals(expectedChildren, actualChildren);
    assertNull(actualParent);
    assertFalse(actualISRs.contains(entity));
    assertEquals(expectedBaseComponents, actualBaseComponents);
    assertEquals(expectedParentComponents, actualParentComponents);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAOImpl#getNameAttribute()}
   */
  @Test
  public void testGetNameAttribute() {
    try {
      informationSystemReleaseDAO.getNameAttribute();
    } catch (UnsupportedOperationException e) {
      // Nothing to do.
    }
  }

}