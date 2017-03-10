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
import de.iteratec.iteraplan.businesslogic.service.BusinessProcessService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;


/**
 * Class for testing the service methods of the {@link BusinessProcessService} interface.
 * 
 * @author mma
 */
public class BusinessProcessServiceTest extends BaseTransactionalTestSupport {
  @Autowired
  private BusinessProcessService          businessProcessService;
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
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessProcessServiceImpl#deleteEntity(de.iteratec.iteraplan.model.BusinessProcess)}
   * The method tests if deleteEntity() deletes correctly a businessObject from the database.
   */
  @Test
  public void testDeleteEntity() {
    InformationSystem is = testDataHelper.createInformationSystem("CI");
    InformationSystemRelease release = testDataHelper.createInformationSystemRelease(is, "1.0");

    BusinessProcess processA = testDataHelper.createBusinessProcess("Process-A", "-");
    BusinessProcess processB = testDataHelper.createBusinessProcess("Process-B", "-");
    BusinessProcess processC = testDataHelper.createBusinessProcess("Process-C", "-");
    commit();
    beginTransaction();

    processA = businessProcessService.loadObjectById(processA.getId());
    processB = businessProcessService.loadObjectById(processB.getId());
    processC = businessProcessService.loadObjectById(processC.getId());

    BusinessProcess root = businessProcessService.getFirstElement();
    processA.addParent(root);
    processB.addParent(processA);
    processC.addParent(processB);
    businessProcessService.saveOrUpdate(processA);
    businessProcessService.saveOrUpdate(processB);
    businessProcessService.saveOrUpdate(processC);
    testDataHelper.createBusinessMapping(release, processA, null, null);
    commit();

    // delete organizational units
    beginTransaction();
    Integer id = processA.getId();
    processA = businessProcessService.loadObjectById(id);
    businessProcessService.deleteEntity(processA);
    commit();

    beginTransaction();

    // object doesn't exist anymore
    processA = businessProcessService.loadObjectByIdIfExists(id);
    if (processA != null) {
      fail();
    }

    // virtual element has no more children
    root = businessProcessService.getFirstElement();
    assertEquals(0, root.getChildren().size());

    // information system release has no more associated business supports
    release = informationSystemReleaseService.loadObjectById(release.getId());
    assertEquals(0, release.getBusinessMappings().size());
    commit();
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessProcessServiceImpl#deleteEntity(de.iteratec.iteraplan.model.BusinessProcess)}
   * The method tests if deleteEntity() throws correctly IllegalStateException if the element has no parent.
   */
  @Test
  public void testDeleteEntityCaseIllegalArgumentException() {
    try {
      BusinessProcess testBP = testDataHelper.createBusinessProcess("testBP", "testDescription");
      commit();

      beginTransaction();
      Integer id = testBP.getId();
      testBP = businessProcessService.loadObjectById(id);
      businessProcessService.deleteEntity(testBP);
      commit();
    } catch (IllegalStateException e) {
      // do noting, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessProcessServiceImpl#deleteEntity(de.iteratec.iteraplan.model.BusinessProcess)}
   * The method tests if deleteEntity() throws correctly IteraplanBusinessException if the given element is the root.
   */
  @Test
  public void testDeleteEntityCaseIteraplanBusinessException() {
    try {
      BusinessProcess root = businessProcessService.getFirstElement();
      businessProcessService.deleteEntity(root);
    } catch (IteraplanBusinessException e) {
      // do noting, it's OK
    }
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.BusinessProcessServiceImpl#getBusinessProcesssBySearch(de.iteratec.iteraplan.model.BusinessProcess, int, int)}
   * The method tests if the getBusinessProcesssBySearch() returns correct list with
   * BusinessProcesss.
   */
  @Test
  public void testGetBusinessProcesssBySearch() {
    BusinessProcess testBP = testDataHelper.createBusinessProcess("testBP", "testDescription");
    commit();
    beginTransaction();

    // get all the BuildingBlockTypes in association with the given id
    testBP = businessProcessService.loadObjectById(testBP.getId());
    BusinessProcess root = businessProcessService.getFirstElement();
    testBP.addParent(root);

    businessProcessService.saveOrUpdate(testBP);
    commit();

    // delete rootBD
    beginTransaction();

    // get Process by search
    // search for ProcessA
    List<BusinessProcess> actualList = businessProcessService.getEntityResultsBySearch(testBP);
    commit();

    List<BusinessProcess> expected = arrayList();
    expected.add(testBP);

    assertEquals(expected.size(), actualList.size());
    assertEquals(expected, actualList);
  }
}
