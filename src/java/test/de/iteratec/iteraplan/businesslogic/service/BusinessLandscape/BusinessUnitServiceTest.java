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
import static de.iteratec.iteraplan.common.util.CollectionUtils.hashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.BusinessDomainService;
import de.iteratec.iteraplan.businesslogic.service.BusinessUnitService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * Tests the service methods of the {@link BusinessUnitService} interface.
 */
public class BusinessUnitServiceTest extends BaseTransactionalTestSupport {
  @Autowired
  private BusinessUnitService             businessUnitService;
  @Autowired
  private BusinessDomainService           businessDomainService;
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
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessUnitServiceImpl#deleteEntity(de.iteratec.iteraplan.model.BusinessUnit)}
   * The method tests if deleteEntity() deletes correctly a businessUnit from the database.
   */
  @Test
  public void testDeleteEntity() {
    InformationSystem is = testDataHelper.createInformationSystem("CI");
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, "1.0");

    BusinessDomain bd = testDataHelper.createBusinessDomain("BD", "");
    Set<BusinessDomain> set = hashSet();
    set.add(bd);

    BusinessUnit unitA = testDataHelper.createBusinessUnit("Unit-A", "-");
    BusinessUnit unitB = testDataHelper.createBusinessUnit("Unit-B", "-");
    BusinessUnit unitC = testDataHelper.createBusinessUnit("Unit-C", "-");
    commit();
    beginTransaction();

    unitA = businessUnitService.loadObjectById(unitA.getId());
    unitB = businessUnitService.loadObjectById(unitB.getId());
    unitC = businessUnitService.loadObjectById(unitC.getId());

    BusinessUnit root = businessUnitService.getFirstElement();
    unitA.addParent(root);
    unitB.addParent(unitA);
    unitC.addParent(unitB);
    businessUnitService.saveOrUpdate(unitA);
    businessUnitService.saveOrUpdate(unitB);
    businessUnitService.saveOrUpdate(unitC);
    testDataHelper.createBusinessMapping(release, null, unitA, null);
    testDataHelper.addBusinessDomainsToBusinessUnit(unitA, set);
    commit();

    // test association BU - BD
    beginTransaction();
    if (!bd.getBusinessUnits().contains(unitA) && !unitA.getBusinessDomains().contains(bd)) {
      fail();
    }
    commit();

    // delete organizational units
    beginTransaction();
    Integer id = unitA.getId();
    unitA = businessUnitService.loadObjectById(id);
    businessUnitService.deleteEntity(unitA);
    commit();

    beginTransaction();

    // object doesn't exist anymore
    unitA = businessUnitService.loadObjectByIdIfExists(id);
    if (unitA != null) {
      fail();
    }

    // virtual element has no more children
    root = businessUnitService.getFirstElement();
    assertEquals(root.getChildren().size(), 0);

    // information system release has no more associated business supports
    release = informationSystemReleaseService.loadObjectById(release.getId());
    assertEquals(release.getBusinessMappings().size(), 0);

    // business domain has no more associated business units
    bd = businessDomainService.loadObjectById(bd.getId());
    assertEquals(bd.getBusinessUnits().size(), 0);
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessUnitServiceImpl#deleteEntity(de.iteratec.iteraplan.model.BusinessUnit)}
   * The method tests if deleteEntity() throws correctly IllegalStateException if the element has no parent.
   */@Test
  public void testDeleteEntityCaseIllegalArgumentException() {
    try {
      BusinessUnit testBU = testDataHelper.createBusinessUnit("testBU", "testDescription");
      commit();

      beginTransaction();
      Integer id = testBU.getId();
      testBU = businessUnitService.loadObjectById(id);
      businessUnitService.deleteEntity(testBU);
      commit();
    } catch (IllegalStateException e) {
      // do noting, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessUnitServiceImpl#deleteEntity(de.iteratec.iteraplan.model.BusinessUnit)}
   * The method tests if deleteEntity() throws correctly IteraplanBusinessException if the given id
   * is the root id.
   */@Test
  public void testDeleteEntityCaseIteraplanBusinessException() {
    try {
      BusinessUnit root = businessUnitService.getFirstElement();
      businessUnitService.deleteEntity(root);
    } catch (IteraplanBusinessException e) {
      // do noting, it's OK
    }
  }


  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessUnitServiceImpl#getBusinessUnitsBySearch(de.iteratec.iteraplan.model.BusinessUnit, int, int)}
   * The method tests if the getBusinessUnitsBySearch() returns correct list with BusinessUnits.
   */@Test
  public void testGetBusinessUnitsBySearch() {
    BusinessUnit testBU = testDataHelper.createBusinessUnit("testBU", "testDescription");
    commit();
    beginTransaction();

    testBU = businessUnitService.loadObjectById(testBU.getId());
    BusinessUnit root = businessUnitService.getFirstElement();
    testBU.addParent(root);

    businessUnitService.saveOrUpdate(testBU);
    commit();

    // delete rootBD
    beginTransaction();

    // get Unit by search
    // search for UnitA
    List<BusinessUnit> actualList = businessUnitService.getEntityResultsBySearch(testBU);
    commit();

    List<BusinessUnit> expected = arrayList();
    expected.add(testBU);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }

}
