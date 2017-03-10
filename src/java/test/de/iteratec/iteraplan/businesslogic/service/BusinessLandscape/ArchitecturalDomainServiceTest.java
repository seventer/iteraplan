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
package de.iteratec.iteraplan.businesslogic.service.BusinessLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainService;
import de.iteratec.iteraplan.businesslogic.service.TechnicalComponentReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Class for testing the service methods of the {@link ArchitecturalDomainService} interface.
 */
public class ArchitecturalDomainServiceTest extends BaseTransactionalTestSupport {

  private static final String              TEST_AD_NAME_A = "Domain-A";
  private static final String              TEST_AD_NAME_B = "Domain-B";
  @Autowired
  private ArchitecturalDomainService       architecturalDomainService;
  @Autowired
  private TechnicalComponentReleaseService technicalComponentReleaseService;
  @Autowired
  private TestDataHelper2                  testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainServiceImpl#deleteEntity(de.iteratec.iteraplan.model.ArchitecturalDomain)}
   * The method tests if the deleteEntity() throws correctly an IteraplanBusinessException if the
   * user tries to delete the root element.
   */
  @Test
  public void testDeleteEntityException() {
    try {
      TechnicalComponent ci = testDataHelper.createTechnicalComponent("CI", true, true);
      TechnicalComponentRelease release = testDataHelper.createTCRelease(ci, "1.0", true);

      ArchitecturalDomain domainA = testDataHelper.createArchitecturalDomain(TEST_AD_NAME_A, "-");
      ArchitecturalDomain domainB = testDataHelper.createArchitecturalDomain(TEST_AD_NAME_B, "-");
      commit();
      beginTransaction();

      domainA = architecturalDomainService.loadObjectById(domainA.getId());
      domainB = architecturalDomainService.loadObjectById(domainB.getId());

      ArchitecturalDomain root = architecturalDomainService.getFirstElement();
      domainA.addParent(root);
      domainB.addParent(domainA);
      architecturalDomainService.saveOrUpdate(domainA);
      architecturalDomainService.saveOrUpdate(domainB);
      testDataHelper.addADToTCRelease(release, domainA);
      testDataHelper.addADToTCRelease(release, domainB);
      commit();

      // delete domain
      beginTransaction();
      architecturalDomainService.deleteEntity(architecturalDomainService.getFirstElement());
      fail("Expected IteraplanBusinessException");

    } catch (IteraplanBusinessException e) {
      // do noting, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainServiceImpl#getArchitecturalDomainBySearch(de.iteratec.iteraplan.model.ArchitecturalDomain, int, int)}
   * The method tests if the DeleteEntity() deletes correctly ArchitecturalDomains.
   */
  @Test
  public void testDeleteEntity() {
    TechnicalComponent ci = testDataHelper.createTechnicalComponent("CI", true, true);
    TechnicalComponentRelease release = testDataHelper.createTCRelease(ci, "1.0", true);

    ArchitecturalDomain domainA = testDataHelper.createArchitecturalDomain(TEST_AD_NAME_A, "-");
    ArchitecturalDomain domainB = testDataHelper.createArchitecturalDomain(TEST_AD_NAME_B, "-");
    commit();
    beginTransaction();

    domainA = architecturalDomainService.loadObjectById(domainA.getId());
    domainB = architecturalDomainService.loadObjectById(domainB.getId());

    ArchitecturalDomain root = architecturalDomainService.getFirstElement();
    domainA.addParent(root);
    domainB.addParent(domainA);
    architecturalDomainService.saveOrUpdate(domainA);
    architecturalDomainService.saveOrUpdate(domainB);
    testDataHelper.addADToTCRelease(release, domainA);
    testDataHelper.addADToTCRelease(release, domainB);
    commit();

    // delete domain
    beginTransaction();
    Integer id = domainA.getId();
    domainA = architecturalDomainService.loadObjectById(id);
    architecturalDomainService.deleteEntity(domainA);
    commit();

    beginTransaction();

    // object doesn't exist anymore
    domainA = architecturalDomainService.loadObjectByIdIfExists(id);
    if (domainA != null) {
      fail();
    }

    // virtual element has no more children
    root = architecturalDomainService.getFirstElement();
    assertEquals(0, root.getChildren().size());

    // catalog item release has no more associated domains
    release = technicalComponentReleaseService.loadObjectById(release.getId());
    assertEquals(0, release.getArchitecturalDomains().size());
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainServiceImpl#getArchitecturalDomainBySearch(de.iteratec.iteraplan.model.ArchitecturalDomain, int, int)}
   * The method tests if the getArchitecturalDomainBySearch() returns correct list with
   * ArchitecturalDomains.
   */
  @Test
  public void testGetArchitecturalDomainBySearch() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent("CI", true, true);
    TechnicalComponentRelease release = testDataHelper.createTCRelease(tc, "beta", true);

    ArchitecturalDomain domainA = testDataHelper.createArchitecturalDomain(TEST_AD_NAME_A, "-");
    ArchitecturalDomain domainB = testDataHelper.createArchitecturalDomain(TEST_AD_NAME_B, "-");
    commit();
    beginTransaction();

    domainA = architecturalDomainService.loadObjectById(domainA.getId());
    domainB = architecturalDomainService.loadObjectById(domainB.getId());

    ArchitecturalDomain root = architecturalDomainService.getFirstElement();
    domainA.addParent(root);
    domainB.addParent(domainA);
    architecturalDomainService.saveOrUpdate(domainA);
    architecturalDomainService.saveOrUpdate(domainB);

    testDataHelper.addADToTCRelease(release, domainA);
    testDataHelper.addADToTCRelease(release, domainB);
    commit();

    // get domain by search
    // search for domainA
    beginTransaction();
    List<ArchitecturalDomain> actualList = architecturalDomainService.getEntityResultsBySearch(domainA);
    commit();

    List<ArchitecturalDomain> expected = arrayList();
    expected.add(domainA);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainServiceImpl#getArchitecturalDomainBySearch(de.iteratec.iteraplan.model.ArchitecturalDomain, int, int)}
   * The method tests if the getArchitecturalDomainByID returns correct ArchitecturalDomain when the
   * user search one with given id.
   */
  @Test
  public void testGetArchitecturalDomainByID() {
    TechnicalComponent tc = testDataHelper.createTechnicalComponent("CI", true, true);
    TechnicalComponentRelease release = testDataHelper.createTCRelease(tc, "beta", true);

    ArchitecturalDomain domainA = testDataHelper.createArchitecturalDomain(TEST_AD_NAME_A, "-");
    ArchitecturalDomain domainB = testDataHelper.createArchitecturalDomain(TEST_AD_NAME_B, "-");
    commit();
    beginTransaction();

    domainA = architecturalDomainService.loadObjectById(domainA.getId());
    domainB = architecturalDomainService.loadObjectById(domainB.getId());

    ArchitecturalDomain root = architecturalDomainService.getFirstElement();
    domainA.addParent(root);
    domainB.addParent(domainA);
    architecturalDomainService.saveOrUpdate(domainA);
    architecturalDomainService.saveOrUpdate(domainB);

    testDataHelper.addADToTCRelease(release, domainA);
    testDataHelper.addADToTCRelease(release, domainB);
    commit();

    // get domain by search
    beginTransaction();
    Integer id = domainA.getId();
    ArchitecturalDomain actual = architecturalDomainService.loadObjectById(id);

    assertEquals(domainA, actual);
  }

}