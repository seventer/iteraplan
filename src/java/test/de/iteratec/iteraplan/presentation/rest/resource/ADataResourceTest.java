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
package de.iteratec.iteraplan.presentation.rest.resource;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.service.ElasticMiContextAndStakeholderManagerInitializationService;
import de.iteratec.iteraplan.businesslogic.service.ElasticeamService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.PermissionFunctional;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.Role2BbtPermission;
import de.iteratec.iteraplan.model.user.Role2BbtPermission.EditPermissionType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.presentation.rest.IteraplanRestRouter;


public abstract class ADataResourceTest extends BaseTransactionalTestSupport {
  @Autowired
  protected ElasticeamService                                        elasticEAMService;

  @Autowired
  protected IteraplanRestRouter                                      router;

  @Autowired
  private ElasticMiContextAndStakeholderManagerInitializationService miService;

  @Override
  @Before
  public void setUp() {
    super.setUp();
    setupUserContext();
    UserContext userContext = UserContext.getCurrentUserContext();
    ElasticMiContext.setCurrentContext(miService.initializeMiContextAndStakeholderManager(userContext.getLoginName(), userContext.getDataSource()));
  }

  protected void setupUserContext() {

    Role restRole = new Role();

    restRole.setRoleName("RestRole");

    Set<Role2BbtPermission> r2bps = Sets.newHashSet();

    r2bps.add(new Role2BbtPermission(restRole, new BuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS), EditPermissionType.UPDATE));
    r2bps.add(new Role2BbtPermission(restRole, new BuildingBlockType(TypeOfBuildingBlock.BUSINESSPROCESS), EditPermissionType.DELETE));
    r2bps.add(new Role2BbtPermission(restRole, new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEM), EditPermissionType.UPDATE));
    r2bps.add(new Role2BbtPermission(restRole, new BuildingBlockType(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE), EditPermissionType.UPDATE));

    restRole.setPermissionsBbt(r2bps);
    restRole.setPermissionsFunctional(Sets.newHashSet(new PermissionFunctional(TypeOfFunctionalPermission.REST), new PermissionFunctional(
        TypeOfFunctionalPermission.BUSINESSPROCESS)));

    User alice = new User();

    UserContext context = new UserContext("system", Collections.singleton(restRole), Locale.ENGLISH, alice);

    UserContext.setCurrentUserContext(context);
  }

  protected void setupUserContextWithoutPermissions() {
    Role someRole = new Role();

    someRole.setRoleName("SomeRole");

    User alice = new User();

    UserContext context = new UserContext("test", Collections.singleton(someRole), Locale.ENGLISH, alice);

    UserContext.setCurrentUserContext(context);
  }

  protected Response getResponse(String url, Method method) {
    Request request = new Request(method, url);
    request.setRootRef(new Reference("https://localhost:8080/iteraplan"));

    Response response = new Response(request);
    router.handle(request, response);

    return response;
  }

  protected Response getResponse(String url, Method method, Map<String, Object> attributes) {
    Request request = new Request(method, url);
    request.setRootRef(new Reference("https://localhost:8080/iteraplan"));
    for (Entry<String, Object> param : attributes.entrySet()) {
      request.getResourceRef().addQueryParameter(param.getKey(), param.getValue().toString());
    }

    Response response = new Response(request);
    router.handle(request, response);

    return response;
  }

  @Ignore
  @Test
  public void test() {
    //please junit
  }
}
