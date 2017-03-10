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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.PropertiesFacade;
import de.iteratec.iteraplan.businesslogic.reports.staticquery.StaticQuery;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.error.IteraplanBusinessException;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;


/**
 *
 */
public class ConsistencyCheckServiceImplTest extends BaseTransactionalTestSupport {

  @Autowired
  private ConsistencyCheckService consistencyCheckService;
  @Autowired
  @Qualifier("BuildingBlocksWithMandatoryAttributeTypesNotAssigned")
  private StaticQuery             exp;
  @Autowired
  private TestDataHelper2         testDataHelper;

  @Before
  public void setUp() {
    super.setUp();
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.ConsistencyCheckServiceImpl#getConsistencyCheck(java.lang.String)}.
   */
  @Test
  public void testGetConsistencyCheck() {
    String beanName = "BuildingBlocksWithMandatoryAttributeTypesNotAssigned";

    StaticQuery res = consistencyCheckService.getConsistencyCheck(beanName);
    assertEquals(exp, res);
  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.ConsistencyCheckServiceImpl#getAllConsistencyChecks()}.
   */
  @Test
  public void testGetAllConsistencyChecks() {
    List<String> properties = PropertiesFacade.getInstance().getAllProperties();

    List<StaticQuery> res = consistencyCheckService.getAllConsistencyChecks();
    int expSize = 0;

    for (String key : properties) {
      if (key.startsWith("check")) {
        expSize++;
        String value = PropertiesFacade.getInstance().getProperty(key);
        assertTrue(res.contains(consistencyCheckService.getConsistencyCheck(value)));
      }
    }
    assertEquals(expSize, res.size());

  }

  /**
   * Test method for {@link de.iteratec.iteraplan.businesslogic.service.ConsistencyCheckServiceImpl#getAllConsistencyChecks()}.
   */
  @Test
  public void testGetAllConsistencyChecksNoPerms() {
    Set<Role> roles = Sets.newHashSet();
    UserContext.setCurrentUserContext(new UserContext("myLogin", roles, Locale.getDefault(), new User()));

    try {
      consistencyCheckService.getAllConsistencyChecks();

      fail("Expected exception not thrown.");
    } catch (IteraplanBusinessException e) {
      assertEquals(e.getErrorCode(), IteraplanErrorMessages.AUTHORISATION_REQUIRED);

    }
  }
}
