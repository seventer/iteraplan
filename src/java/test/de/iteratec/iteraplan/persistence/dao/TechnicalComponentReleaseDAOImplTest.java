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

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * @author mma
 */
public class TechnicalComponentReleaseDAOImplTest extends BaseTransactionalTestSupport {

  @Autowired
  private TechnicalComponentReleaseDAOImpl technicalComponentReleaseDAO;
  @Autowired
  private TestDataHelper2                  testDataHelper;

  private static final String              TEST_DESCRIPTION = "testDescription";
  private static final String              VERSION          = "version";

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.persistence.dao.TechnicalComponentReleaseDAOImpl#onBeforeDelete(de.iteratec.iteraplan.model.TechnicalComponentRelease)}
   */
  @Test
  public void testOnBeforeDelete() {
    ArchitecturalDomain domain = testDataHelper.createArchitecturalDomain("testAD", TEST_DESCRIPTION);

    InformationSystem isA = testDataHelper.createInformationSystem("testISA");
    InformationSystemRelease a = testDataHelper.createInformationSystemRelease(isA, VERSION);

    InformationSystem isB = testDataHelper.createInformationSystem("testISB");
    InformationSystemRelease b = testDataHelper.createInformationSystemRelease(isB, VERSION);

    TechnicalComponent firstTC = testDataHelper.createTechnicalComponent("firstTC", true, true);
    TechnicalComponentRelease entity = testDataHelper.createTCRelease(firstTC, VERSION, true);

    InformationSystemInterface isi = testDataHelper.createInformationSystemInterface(a, b, entity, TEST_DESCRIPTION);

    InfrastructureElement ie = testDataHelper.createInfrastructureElement("testIE", TEST_DESCRIPTION);

    TechnicalComponentRelease successor = testDataHelper.createTCRelease(firstTC, "successor", true);
    TechnicalComponentRelease predecessor = testDataHelper.createTCRelease(firstTC, "predecessor", true);
    TechnicalComponentRelease baseComp = testDataHelper.createTCRelease(firstTC, "base", true);
    TechnicalComponentRelease parent = testDataHelper.createTCRelease(firstTC, "parent", true);
    commit();

    Set<InformationSystemInterface> setISI = hashSet();
    setISI.add(isi);

    beginTransaction();
    entity.addArchitecturalDomain(domain);
    entity.addInformationSystemRelease(a);
    entity.addInformationSystemRelease(b);
    entity.setInformationSystemInterfaces(setISI);
    BuildingBlockFactory.createTcr2IeAssociation(entity, ie).connect();

    entity.addSuccessor(successor);
    entity.addPredecessor(predecessor);
    entity.addBaseComponent(baseComp);
    entity.addParentComponent(parent);
    commit();

    technicalComponentReleaseDAO.onBeforeDelete(entity);

    Set<ArchitecturalDomain> actualADs = entity.getArchitecturalDomains();
    Set<ArchitecturalDomain> expectedADs = hashSet();

    Set<InformationSystemRelease> actualISRs = entity.getInformationSystemReleases();
    Set<InformationSystemRelease> expectedISRs = hashSet();

    Set<InformationSystemInterface> actualISIs = entity.getInformationSystemInterfaces();
    Set<InformationSystemInterface> expectedISIs = hashSet();

    Set<InfrastructureElement> actualIEs = entity.getInfrastructureElements();
    Set<InfrastructureElement> expectedIEs = Sets.newHashSet(ie); // the tcr2IeAssociations will not be deleted

    Set<TechnicalComponentRelease> actualSuccessor = entity.getSuccessors();
    Set<TechnicalComponentRelease> expectedSuccessor = hashSet();

    Set<TechnicalComponentRelease> actualPredecessors = entity.getPredecessors();
    Set<TechnicalComponentRelease> expectedPredecessors = hashSet();

    Set<TechnicalComponentRelease> actualBaseComponents = entity.getBaseComponents();
    Set<TechnicalComponentRelease> expectedBaseComponents = hashSet();

    Set<TechnicalComponentRelease> actualParentComponents = entity.getParentComponents();
    Set<TechnicalComponentRelease> expectedParentComponents = hashSet();

    // after the delete operation the entity should not have any associations.
    assertEquals(expectedADs, actualADs);
    assertEquals(expectedISRs, actualISRs);
    assertEquals(expectedISIs, actualISIs);
    assertEquals(expectedIEs, actualIEs);

    assertEquals(expectedSuccessor, actualSuccessor);
    assertEquals(expectedPredecessors, actualPredecessors);
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
      technicalComponentReleaseDAO.getNameAttribute();
    } catch (UnsupportedOperationException e) {
      // Nothing to do.
    }
  }

}
