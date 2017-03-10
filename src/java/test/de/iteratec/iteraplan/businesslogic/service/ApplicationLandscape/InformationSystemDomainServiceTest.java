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
/*
 * iteraplan is an IT Governance web application developed by iteratec, GmbH

 * Copyright (C) 2008 iteratec, GmbH
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
 * You can contact iteratec GmbH headquarters at Inselkammerstraße 4
 * 82008 München - Unterhaching, Germany, or at email address info@iteratec.de.
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
package de.iteratec.iteraplan.businesslogic.service.ApplicationLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;


/**
 * Test class for the service interface {@link InformationSystemDomainService}.
 */
public class InformationSystemDomainServiceTest extends BaseTransactionalTestSupport {
  private static final String             TEST_DESCRIPTION = "testDescription";

  @Autowired
  private InformationSystemDomainService  isdService;
  @Autowired
  private InformationSystemReleaseService informationSystemReleaseService;
  @Autowired
  private TestDataHelper2                 testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainServiceImpll#deleteEntity(de.iteratec.iteraplan.model.InformationSystemDomain)}
   * The method tests if the deleteEntity() throws correctly an IteraplanBusinessException if the
   * user tries to delete the root element.
   */
  @Test
  public void testDeleteEntityCaseException() throws Exception {
    try {
      // create data
      InformationSystemDomain firstTestISD = testDataHelper.createInformationSystemDomain("firstTestISD", TEST_DESCRIPTION);
      InformationSystemDomain secondTestISD = testDataHelper.createInformationSystemDomain("secondTestISD", TEST_DESCRIPTION);
      InformationSystemDomain thirdTestISD = testDataHelper.createInformationSystemDomain("thirdTestISD", TEST_DESCRIPTION);
      commit();
      beginTransaction();

      firstTestISD = isdService.loadObjectById(firstTestISD.getId());
      secondTestISD = isdService.loadObjectById(secondTestISD.getId());
      thirdTestISD = isdService.loadObjectById(thirdTestISD.getId());

      InformationSystemDomain root = isdService.getFirstElement();
      firstTestISD.addParent(root);
      secondTestISD.addParent(root);
      thirdTestISD.addParent(root);

      isdService.saveOrUpdate(firstTestISD);
      isdService.saveOrUpdate(secondTestISD);
      isdService.saveOrUpdate(thirdTestISD);
      commit();

      // delete rootISD
      beginTransaction();
      isdService.deleteEntity(isdService.getFirstElement());
      fail("Expected IteraplanBusinessException");
    } catch (IteraplanBusinessException e) {
      // do nothing, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainServiceImpll#deleteEntity(de.iteratec.iteraplan.model.InformationSystemDomain)}
   * The method tests if the deleteEntity() deletes correctly an InformationSystemDomain from the database.
   */
  @Test
  public void testDeleteEntity() {
    InformationSystem ipu = testDataHelper.createInformationSystem("IS");
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(ipu, "1.0");

    InformationSystemDomain domainA = testDataHelper.createInformationSystemDomain("Domain-A", "-");
    InformationSystemDomain domainB = testDataHelper.createInformationSystemDomain("Domain-B", "-");
    commit();
    beginTransaction();

    domainA = isdService.loadObjectById(domainA.getId());
    domainB = isdService.loadObjectById(domainB.getId());

    InformationSystemDomain root = isdService.getFirstElement();
    domainA.addParent(root);
    domainB.addParent(domainA);
    isdService.saveOrUpdate(domainA);
    isdService.saveOrUpdate(domainB);
    testDataHelper.addIsrToIsd(release, domainA);
    testDataHelper.addIsrToIsd(release, domainB);
    commit();

    // delete domain
    beginTransaction();
    Integer id = domainA.getId();
    domainA = isdService.loadObjectById(id);
    isdService.deleteEntity(domainA);
    commit();

    // object doesn't exist anymore
    beginTransaction();
    domainA = isdService.loadObjectByIdIfExists(id);
    if (domainA != null) {
      fail();
    }

    // virtual element has no more children
    root = isdService.getFirstElement();
    assertEquals(0, root.getChildren().size());

    // information system release has no more associated domains
    release = informationSystemReleaseService.loadObjectById(release.getId());
    assertEquals(0, release.getInformationSystemDomains().size());
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemServiceImpl#getInformationSystemDomainById(java.lang.Integer)}
   * The method tests if getInformationSystemDomainById() returns correct InformationSystemDomain
   * with a given id.
   */
  @Test
  public void testGetInformationSystemDomainById() {
    InformationSystemDomain testISD = testDataHelper.createInformationSystemDomain("testISD", TEST_DESCRIPTION);
    commit();

    // get all the BuildingBlockTypes in association with the given id
    beginTransaction();
    InformationSystemDomain actual = isdService.loadObjectById(testISD.getId());
    assertEquals(testISD, actual);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemDomainServiceImpl#getInformationSystemDomainsBySearch(de.iteratec.iteraplan.model.InformationSystemDomain, int, int)}
   * The method tests if the getInformationSystemDomainsBySearch() returns correct list with
   * InformationSystemDomains.
   */
  @Test
  public void testGetInformationSystemDomainsBySearch() {
    InformationSystemDomain testISD = testDataHelper.createInformationSystemDomain("testISD", TEST_DESCRIPTION);
    commit();

    beginTransaction();
    // get Unit by search
    // search for InformationSystemDomain
    List<InformationSystemDomain> actualList = isdService.getEntityResultsBySearch(testISD);
    commit();

    List<InformationSystemDomain> expected = arrayList();
    expected.add(testISD);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }
  
  @Test
  public void testSaveOrUpdateInformationSystemDomainWithoutPermissions() {
    try{
      User user = new User();
      user.setDataSource("MASTER");
      user.setLoginName("user");
      UserContext userContext = new UserContext("user", new HashSet<Role>(), Locale.UK, user);
      UserContext.setCurrentUserContext(userContext);
      
      InformationSystemDomain firstTestISD = testDataHelper.createInformationSystemDomain("firstTestISD", TEST_DESCRIPTION);
      commit();
      beginTransaction();
  
      firstTestISD = isdService.loadObjectById(firstTestISD.getId());
  
      InformationSystemDomain root = isdService.getFirstElement();
      firstTestISD.addParent(root);
  
      isdService.saveOrUpdate(firstTestISD);

    }
    catch (IteraplanBusinessException e) {
      assertEquals(e.getErrorCode(), IteraplanErrorMessages.AUTHORISATION_REQUIRED);
    }
  }
}
