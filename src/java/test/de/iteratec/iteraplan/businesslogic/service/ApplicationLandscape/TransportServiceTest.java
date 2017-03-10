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
package de.iteratec.iteraplan.businesslogic.service.ApplicationLandscape;

import static de.iteratec.iteraplan.common.util.CollectionUtils.arrayList;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.TransportService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.sorting.HierarchicalEntityCachingComparator;
import de.iteratec.iteraplan.persistence.dao.BusinessObjectDAO;


/**
 * Implementation of the service interface {@link TransportService}.
 * 
 * @author mma
 */
public class TransportServiceTest extends BaseTransactionalTestSupport {

  @Autowired
  private TransportService    classUnderTest;
  @Autowired
  private BusinessObjectDAO   businessObjectDAO;
  @Autowired
  private TestDataHelper2 testDataHelper;

  private static final String VERSION_1_0      = "1.0";
  private static final String VERSION_2_0      = "2.0";
  private static final String VERSION_15_0     = "15.0";
  private static final String TEST_DESCRIPTION = "testDescription";

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }
  
  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.TransportServiceImpl#getAvailableTransports()}
   * The method tests if the getAvailableTransports() returns correct list with all the available
   * BusinessObjects as Transport objects.
   */
  @Test
  public void testGetAvailableTransportsCaseNotEmptyList() {
    // create data
    // A
    InformationSystem firstTestIS = testDataHelper.createInformationSystem("firstTestIS");
    InformationSystemRelease firstTestISR = testDataHelper.createInformationSystemRelease(firstTestIS, VERSION_1_0);
    InformationSystemRelease secondTestISR = testDataHelper.createInformationSystemRelease(firstTestIS, VERSION_2_0);

    TechnicalComponent firstTestTC = testDataHelper.createTechnicalComponent("firstTestTC", true, true);
    TechnicalComponentRelease firstTestTCR = testDataHelper.createTCRelease(firstTestTC, VERSION_15_0, true);

    BusinessObject firstTestBO = testDataHelper.createBusinessObject("firstTestBO", TEST_DESCRIPTION);
    InformationSystemInterface firstTestISI = testDataHelper.createInformationSystemInterface(firstTestISR, secondTestISR, firstTestTCR,
        TEST_DESCRIPTION);

    testDataHelper.createTransport(firstTestBO, firstTestISI, Direction.BOTH_DIRECTIONS);

    // B
    InformationSystem secondTestIS = testDataHelper.createInformationSystem("secondTestIS");
    InformationSystemRelease thirdTestISR = testDataHelper.createInformationSystemRelease(secondTestIS, VERSION_1_0);
    InformationSystemRelease fourthTestISR = testDataHelper.createInformationSystemRelease(secondTestIS, VERSION_2_0);

    TechnicalComponent secondTestTC = testDataHelper.createTechnicalComponent("secondTestTC", true, true);
    TechnicalComponentRelease secondTestTCR = testDataHelper.createTCRelease(secondTestTC, VERSION_15_0, true);

    BusinessObject secondTestBO = testDataHelper.createBusinessObject("secondTestBO", TEST_DESCRIPTION);
    InformationSystemInterface secondTestISI = testDataHelper.createInformationSystemInterface(thirdTestISR, fourthTestISR, secondTestTCR,
        TEST_DESCRIPTION);

    testDataHelper.createTransport(secondTestBO, secondTestISI, Direction.BOTH_DIRECTIONS);

    // C
    InformationSystem thirdTestIS = testDataHelper.createInformationSystem("thirdTestIS");
    InformationSystemRelease fifthTestISR = testDataHelper.createInformationSystemRelease(thirdTestIS, VERSION_1_0);
    InformationSystemRelease sixthTestISR = testDataHelper.createInformationSystemRelease(thirdTestIS, VERSION_2_0);

    TechnicalComponent thirdTestTC = testDataHelper.createTechnicalComponent("thirdTestTC", true, true);
    TechnicalComponentRelease thirdTestTCR = testDataHelper.createTCRelease(thirdTestTC, VERSION_15_0, true);

    BusinessObject thirdTestBO = testDataHelper.createBusinessObject("thirdTestBO", TEST_DESCRIPTION);
    InformationSystemInterface thirdTestISI = testDataHelper.createInformationSystemInterface(fifthTestISR, sixthTestISR, thirdTestTCR,
        TEST_DESCRIPTION);

    testDataHelper.createTransport(thirdTestBO, thirdTestISI, Direction.SECOND_TO_FIRST);

    // D
    InformationSystem fourthTestIS = testDataHelper.createInformationSystem("fourthTestIS");
    InformationSystemRelease seventhTestISR = testDataHelper.createInformationSystemRelease(fourthTestIS, VERSION_1_0);
    InformationSystemRelease eigthTestISR = testDataHelper.createInformationSystemRelease(fourthTestIS, VERSION_2_0);

    TechnicalComponent fourthTestTC = testDataHelper.createTechnicalComponent("fourthTestTC", true, true);
    TechnicalComponentRelease fourthTestTCR = testDataHelper.createTCRelease(fourthTestTC, VERSION_15_0, true);

    BusinessObject fourthTestBO = testDataHelper.createBusinessObject("fourthTestBO", TEST_DESCRIPTION);
    InformationSystemInterface fourthTestISI = testDataHelper.createInformationSystemInterface(seventhTestISR, eigthTestISR, fourthTestTCR,
        TEST_DESCRIPTION);

    testDataHelper.createTransport(fourthTestBO, fourthTestISI, Direction.NO_DIRECTION);
    commit();

    BusinessObject root = businessObjectDAO.getFirstElement();

    List<BusinessObject> bos = arrayList();
    bos.add(firstTestBO);
    bos.add(secondTestBO);
    bos.add(thirdTestBO);
    bos.add(fourthTestBO);
    bos.add(root);

    Collections.sort(bos, new HierarchicalEntityCachingComparator<BusinessObject>());

    beginTransaction();
    List<Transport> expected = arrayList();

    for (BusinessObject elem : bos) {
      Transport t = BuildingBlockFactory.createTransport();
      t.setBusinessObject(elem);
      t.setId(elem.getId());
      expected.add(t);
    }

    List<Transport> actual = classUnderTest.getAvailableTransports();
    assertEquals(expected, actual);
    commit();
  }

}
