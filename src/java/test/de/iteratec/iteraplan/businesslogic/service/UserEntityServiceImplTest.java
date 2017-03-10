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
package de.iteratec.iteraplan.businesslogic.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.model.user.UserEntity;
import de.iteratec.iteraplan.model.user.UserGroup;


/**
 * Integration test for the {@link UserEntityServiceImpl} class.
 *
 */
public class UserEntityServiceImplTest extends BaseTransactionalTestSupport {

  @Autowired
  private UserEntityService userEntityService;
  @Autowired
  private TestDataHelper2   testDataHelper;
  
  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserEntityServiceImpl#loadOwnedBuildingBlocks(de.iteratec.iteraplan.model.user.UserEntity, boolean)}.
   */
  @Test
  public void testLoadOwnedBuildingBlocks() {
    BusinessDomain bd = testDataHelper.createBusinessDomain("bd", "desc");
    BusinessFunction bf = testDataHelper.createBusinessFunction("bf", "desc");
    BusinessProcess bp = testDataHelper.createBusinessProcess("bp", "desc");

    UserGroup ug1 = testDataHelper.createUserGroup("ug1", "", Sets.<UserEntity> newHashSet(), Sets.<BuildingBlock> newHashSet());
    UserGroup ug2 = testDataHelper.createUserGroup("ug2", "", Sets.<UserEntity> newHashSet(ug1), Sets.<BuildingBlock> newHashSet(bd));
    UserGroup ug3 = testDataHelper.createUserGroup("ug3", "", Sets.<UserEntity> newHashSet(ug2), Sets.<BuildingBlock> newHashSet(bf, bp));

    commit();
    beginTransaction();

    assertEquals(Lists.newArrayList(bd, bf, bp), userEntityService.loadOwnedBuildingBlocks(ug1, true));
    assertEquals(Lists.newArrayList(bd), userEntityService.loadOwnedBuildingBlocks(ug2, false));
    assertEquals(Lists.newArrayList(bf, bp), userEntityService.loadOwnedBuildingBlocks(ug3, false));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserEntityServiceImpl#loadOwnedBuildingBlocks(de.iteratec.iteraplan.model.user.UserEntity, boolean)}.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testLoadOwnedBuildingBlocksNull() {
    userEntityService.loadOwnedBuildingBlocks(null, true);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserEntityServiceImpl#loadOwnedBuildingBlocks(de.iteratec.iteraplan.model.user.UserEntity, boolean)}.
   */
  @Test
  public void testLoadOwnedBuildingBlocksNullId() {
    assertEquals(Lists.newArrayList(), userEntityService.loadOwnedBuildingBlocks(new User(), true));
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.UserEntityServiceImpl#getUserEntityBySearch(de.iteratec.iteraplan.model.user.UserEntity, int, int)}.
   */
  @Test
  public void testGetUserEntityBySearch() {
    User user = testDataHelper.createUser("user");

    commit();
    beginTransaction();

    List<UserEntity> userBySearch = userEntityService.getUserEntityBySearch(user);
    assertEquals(Lists.newArrayList(user), userBySearch);
  }

}
