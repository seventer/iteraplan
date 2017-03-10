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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemInterfaceService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;


/**
 * Tests the service methods of the {@link InformationSystemReleaseService} interface.
 * 
 * @author sip
 */
public class InformationSystemReleaseService2Test extends BaseTransactionalTestSupport {
  private static final String               VERSION_1_0 = "1.0";
  private static final String               VERSION_2_0 = "2.0";
  private static final String               VERSION_3_0 = "3.0";
  private static final String               VERSION_4_0 = "4.0";

  @Autowired
  private InformationSystemReleaseService   isrService;
  @Autowired
  private InformationSystemInterfaceService isiService;
  @Autowired
  private TestDataHelper2                   testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#deleteEntity(de.iteratec.iteraplan.model.InformationSystemRelease)}
   */
  @Test
  public void testDeleteEntityWithChildren() {
    InformationSystem is1 = testDataHelper.createInformationSystem("is1");
    InformationSystem is2 = testDataHelper.createInformationSystem("is2");
    InformationSystemRelease isrChild1 = testDataHelper.createInformationSystemRelease(is1, VERSION_2_0);
    InformationSystemRelease isrChild2 = testDataHelper.createInformationSystemRelease(is2, VERSION_3_0);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is2, VERSION_1_0, isrChild1, isrChild2);

    TechnicalComponentRelease tcr = testDataHelper.createTCRelease(testDataHelper.createTechnicalComponent("tc", true, false), VERSION_4_0, true);
    InformationSystemInterface isi1 = testDataHelper.createInformationSystemInterfaceWithNameDirection("isi1", "->", "descr", isrChild1, isrChild2,
        tcr);
    InformationSystemInterface isi2 = testDataHelper.createInformationSystemInterfaceWithNameDirection("isi2", "->", "descr", isrChild1, isrChild1,
        tcr);
    InformationSystemInterface isi3 = testDataHelper.createInformationSystemInterfaceWithNameDirection("isi3", "->", "descr", isr, isr, tcr);

    commit();
    beginTransaction();

    isrService.deleteEntity(isr);

    commit();
    beginTransaction();

    assertFalse(isrService.doesDuplicateReleaseExist(isr));
    assertFalse(isrService.doesDuplicateReleaseExist(isrChild1));
    assertFalse(isrService.doesDuplicateReleaseExist(isrChild2));

    assertNull(isiService.loadObjectByIdIfExists(isi1.getId()));
    assertNull(isiService.loadObjectByIdIfExists(isi2.getId()));
    assertNull(isiService.loadObjectByIdIfExists(isi3.getId()));
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validBaseComponents(Integer, List, boolean)}
   */
  @Test
  public void testValidBaseComponents() {
    InformationSystem is1 = testDataHelper.createInformationSystem("is1");
    InformationSystem is2 = testDataHelper.createInformationSystem("is2");
    InformationSystem is3 = testDataHelper.createInformationSystem("is3");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is1, VERSION_1_0);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is2, VERSION_1_0);
    InformationSystemRelease isr3 = testDataHelper.createInformationSystemRelease(is3, VERSION_1_0);
    isr1.addParentComponent(isr2);

    commit();
    beginTransaction();

    List<InformationSystemRelease> res = isrService.validBaseComponents(isr1.getId(), Lists.<InformationSystemRelease> newArrayList(), false);
    assertEquals(Lists.newArrayList(isr3), res);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validBaseComponents(Integer, List, boolean)}
   */
  @Test
  public void testValidBaseComponentsExclude() {
    InformationSystem is1 = testDataHelper.createInformationSystem("is1");
    InformationSystem is2 = testDataHelper.createInformationSystem("is2");
    InformationSystem is3 = testDataHelper.createInformationSystem("is3");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is1, VERSION_1_0);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is2, VERSION_1_0);
    InformationSystemRelease isr3 = testDataHelper.createInformationSystemRelease(is3, VERSION_1_0);

    commit();
    beginTransaction();

    List<InformationSystemRelease> res = isrService.validBaseComponents(isr1.getId(), Lists.newArrayList(isr2), false);
    assertEquals(Lists.newArrayList(isr3), res);
  }

  /**
   * Test method for
   * {@link de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService#validBaseComponents(Integer, List, boolean)}
   */
  @Test
  public void testValidBaseComponentsInactive() {
    InformationSystem is1 = testDataHelper.createInformationSystem("is1");
    InformationSystem is2 = testDataHelper.createInformationSystem("is2");
    InformationSystem is3 = testDataHelper.createInformationSystem("is3");
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is1, VERSION_1_0, "descr", null, null, TypeOfStatus.CURRENT);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is2, VERSION_1_0, "descr", null, null, TypeOfStatus.CURRENT);
    InformationSystemRelease isr3 = testDataHelper.createInformationSystemRelease(is3, VERSION_1_0, "descr", null, null, TypeOfStatus.INACTIVE);

    commit();
    beginTransaction();

    List<InformationSystemRelease> res1 = isrService.validBaseComponents(isr1.getId(), Lists.<InformationSystemRelease> newArrayList(), false);
    assertEquals(Lists.newArrayList(isr2), res1);
    List<InformationSystemRelease> res2 = isrService.validBaseComponents(isr1.getId(), Lists.<InformationSystemRelease> newArrayList(), true);
    assertEquals(Lists.newArrayList(isr2, isr3), res2);
  }

}
