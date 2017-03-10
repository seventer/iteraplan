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
package de.iteratec.iteraplan.presentation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import de.iteratec.iteraplan.businesslogic.service.DataSourceService;
import de.iteratec.iteraplan.businesslogic.service.ElasticMiContextAndStakeholderManagerInitializationService;
import de.iteratec.iteraplan.businesslogic.service.ElasticeamService;
import de.iteratec.iteraplan.businesslogic.service.RoleService;
import de.iteratec.iteraplan.businesslogic.service.UserService;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;


public class UserContextInitializationServiceTest {

  private User                                 user;

  private UserContextInitializationServiceImpl userContextService;
  private RoleService                          roleService;
  private UserService                          userService;

  private PlatformTransactionManager           transactionManager;
  private MockHttpServletRequest               request;
  private Authentication                       auth;

  @Before
  public void onSetUp() {

    DataSourceService dataSourceService = EasyMock.createMock(DataSourceService.class);

    roleService = EasyMock.createMock(RoleService.class);
    userService = EasyMock.createMock(UserService.class);
    transactionManager = EasyMock.createNiceMock(PlatformTransactionManager.class);
    ElasticeamService elasticeamService = EasyMock.createNiceMock(ElasticeamService.class);
    ElasticMiContextAndStakeholderManagerInitializationService miCtxAndStakeholderMgrSrv = EasyMock
        .createNiceMock(ElasticMiContextAndStakeholderManagerInitializationService.class);

    userContextService = new UserContextInitializationServiceImpl();
    userContextService.setRoleService(roleService);
    userContextService.setTransactionManager(transactionManager);
    userContextService.setUserService(userService);
    userContextService.setDataSourceService(dataSourceService);
    userContextService.setElasticService(elasticeamService);
    userContextService.setElasticMiContextAndStakeholderManagerInitializationService(miCtxAndStakeholderMgrSrv);

    auth = EasyMock.createMock(Authentication.class);
    EasyMock.expect(auth.getPrincipal()).andReturn(new Object()); // return a dummy object to avoid NPE on getPrincipal within UCISI
    request = new MockHttpServletRequest();

    user = new User();
    user.setDataSource("MASTER");
    user.setLoginName("testUser");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testUserRoles() {

    Role role = new Role();
    role.setRoleName("testRole");

    Role subOrdinateRole = new Role();
    subOrdinateRole.setRoleName("subOrdinateRole");

    Set<Role> roleSet = new HashSet<Role>();
    roleSet.add(subOrdinateRole);

    role.setConsistsOfRoles(roleSet);

    List<Role> listOfRoles = new ArrayList<Role>();
    listOfRoles.add(role);

    request.addUserRole("testRole");
    auth.setAuthenticated(true);

    SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("testRole");
    @SuppressWarnings("rawtypes")
    Set grantedAuthorities = new HashSet();
    grantedAuthorities.add(grantedAuthority);

    EasyMock.expect(transactionManager.getTransaction(new TransactionTemplate(transactionManager))).andReturn(null);
    EasyMock.expect(roleService.getRoleByName("testRole")).andReturn(role);
    EasyMock.expect(roleService.getRoleByName("iteraplan_Supervisor")).andReturn(role);
    EasyMock.expect(userService.getUserByLoginIfExists("testUser")).andReturn(user);
    EasyMock.expect(userService.saveOrUpdate(user)).andReturn(user);
    EasyMock.expect(userService.saveOrUpdate(user)).andReturn(user);
    EasyMock.expect(roleService.getAllRolesFiltered()).andReturn(listOfRoles);

    EasyMock.expect(auth.getName()).andReturn(user.getLoginName());
    EasyMock.expect(auth.getAuthorities()).andReturn(grantedAuthorities);

    EasyMock.replay(roleService, transactionManager, auth, userService);

    userContextService.initializeUserContext(request, auth);

    EasyMock.verify(roleService);

  }
}
