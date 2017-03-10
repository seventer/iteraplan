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
package de.iteratec.iteraplan.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.ImmutableList;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.InfrastructureElementService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.HashBucketMap;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;


/**
 * Testcase for ITERAPLAN-888
 */
public class ChangeAssignedEnumValueTest extends BaseTransactionalTestSupport {

  @Autowired
  private TestDataHelper2              testDataHelper;
  @Autowired
  private AttributeValueService        avService;
  @Autowired
  private InfrastructureElementService ieService;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  @Test
  public void testChangeAssignedEnumValue() {
    // setup data
    EnumAT enumAT = testDataHelper.createEnumAttributeType("Enum AT", "Enum AT desc", Boolean.FALSE, testDataHelper.getDefaultATG());
    EnumAV av1 = testDataHelper.createEnumAV("av1", "av1 desc", enumAT);
    EnumAV av2 = testDataHelper.createEnumAV("av2", "av2 desc", enumAT);

    InfrastructureElement ie1 = testDataHelper.createInfrastructureElement("IE 1", "IE 1 desc");
    InfrastructureElement ie2 = testDataHelper.createInfrastructureElement("IE 2", "IE 2 desc");

    testDataHelper.createAVA(ie1, av1);
    testDataHelper.createAVA(ie2, av1);
    commit();

    // change assigned enum value
    beginTransaction();
    ie1 = ieService.merge(ie1);
    avService.setReferenceValues(ie1, ImmutableList.of(av2), enumAT.getId());
    ieService.saveOrUpdate(ie1);
    commit();

    // load attribute values
    beginTransaction();
    ImmutableList<Integer> bbIdList = ImmutableList.of(ie1.getId(), ie2.getId());
    HashBucketMap<Integer, AttributeValue> bbsToAVs = avService.getBuildingBlockIdsToConnectedAttributeValues(bbIdList, enumAT.getId());
    commit();

    // assertion
    assertEquals(ImmutableList.of(av2), bbsToAVs.get(ie1.getId()));
    assertEquals(ImmutableList.of(av1), bbsToAVs.get(ie2.getId()));
  }

}
