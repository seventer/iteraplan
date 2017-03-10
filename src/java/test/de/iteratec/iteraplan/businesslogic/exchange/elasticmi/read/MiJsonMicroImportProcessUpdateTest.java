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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;

import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model.MiIteraplanDiffWriterDbTestBase;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.io.mapper.json.JsonQueryMapper;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQl2Compiler;
import de.iteratec.iteraplan.elasticmi.iteraql2.IteraQlQuery;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiAccessLevel;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiTypePermission;
import de.iteratec.iteraplan.elasticmi.permission.User;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 *
 */
public class MiJsonMicroImportProcessUpdateTest extends MiIteraplanDiffWriterDbTestBase {

  private static final String TEST_USER            = "testUser";
  private static final String STORE_IDENTIFIER     = "MASTER";

  private static final String TYPE_NAME            = "InformationSystem";
  private static final String IS_NAME              = "IS1 # 1";

  private static final String DESCRIPTION_PROPERTY = "description";
  private static final String TEST_DESCRIPTION     = "new description";

  private void initUserContextWithPermissions(boolean create, boolean update, boolean delete, boolean useLoadTaskFactory) {
    StakeholderManagerImpl mgr = new StakeholderManagerImpl();

    mgr.addTypePermission(new ElasticMiTypePermission(TYPE_NAME, ElasticMiAccessLevel.READ));

    if (create) {
      mgr.addTypePermission(new ElasticMiTypePermission(TYPE_NAME, ElasticMiAccessLevel.CREATE));
    }

    if (update) {
      mgr.addTypePermission(new ElasticMiTypePermission(TYPE_NAME, ElasticMiAccessLevel.UPDATE));
    }

    if (delete) {
      mgr.addTypePermission(new ElasticMiTypePermission(TYPE_NAME, ElasticMiAccessLevel.DELETE));
    }

    User user = mgr.createUser(TEST_USER);

    de.iteratec.iteraplan.elasticmi.permission.Role miRestRole = mgr.createRole("RestRole");
    for (ElasticMiTypePermission typePermission : mgr.getTypePermissions()) {
      miRestRole.grantTypePermission(typePermission);
    }
    user.addRole(miRestRole);

    ElasticMiLoadTaskFactory actualFactory = ElasticMiLoadTaskFactory.EMPTY_TASK_FACTORY;
    if (useLoadTaskFactory) {
      actualFactory = super.loadTaskFactory;
    }
    ElasticMiContext ctx = new ElasticMiContext(user, STORE_IDENTIFIER, new ElasticMiExecution(mgr, actualFactory));
    ElasticMiContext.setCurrentContext(ctx);
  }

  private JsonObject createTestJsonObject(RMetamodel rMetamodel, Model model) {
    IteraQl2Compiler compiler = new IteraQl2Compiler(rMetamodel);
    IteraQlQuery query = compiler.compile("InformationSystem[@name.contains(\"" + IS_NAME + "\")];");

    JsonQueryMapper mapper = new JsonQueryMapper(rMetamodel, "https://localhost:8080/", query);

    JsonObject json = mapper.write(model);

    JsonObject jsonOE = json.get("result").getAsJsonArray().get(0).getAsJsonObject();
    jsonOE.remove("runtimePeriod");
    jsonOE.remove(DESCRIPTION_PROPERTY);

    JsonArray description = new JsonArray();
    description.add(new JsonPrimitive(TEST_DESCRIPTION));

    jsonOE.add(DESCRIPTION_PROPERTY, description);

    return json;
  }

  private BigInteger getID(JsonObject json) {
    JsonObject jsonOE = json.get("result").getAsJsonArray().get(0).getAsJsonObject();
    return new BigInteger(jsonOE.get("id").getAsJsonArray().get(0).getAsString());
  }

  @Test
  public void testUpdate() {
    initUserContextWithPermissions(true, true, true, true);

    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);
    JsonObject json = createTestJsonObject(rMetamodel, model);

    ByteArrayInputStream in = new ByteArrayInputStream(json.toString().getBytes());

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);

    process.update(getID(json), isType, in);
    //check change in ctx model
    assertEquals(TEST_DESCRIPTION, findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, IS_NAME).getDescription());
    assertEquals(2, process.getImportProcessMessages().getMessages(Severity.INFO).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }

  @Test
  public void testUpdateWithoutPermissions() {
    initUserContextWithPermissions(false, false, false, false);
    ElasticMiContext.getCurrentContext().synchronizeAndGetMetamodelAndModelContainer();

    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);
    JsonObject json = createTestJsonObject(rMetamodel, model);

    ByteArrayInputStream in = new ByteArrayInputStream(json.toString().getBytes());

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);

    process.update(getID(json), isType, in);

    // old description was not set, should be unchanged
    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, IS_NAME).getDescription());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.INFO).size());
    assertEquals(1, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }

  @Test
  public void testUpdateWithEmptyStream() {
    initUserContextWithPermissions(true, true, true, false);

    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);
    JsonObject json = createTestJsonObject(rMetamodel, model);

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);
    assertNotNull(process);

    process.update(getID(json), isType, null);

    // old description was not set, should be unchanged
    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, IS_NAME).getDescription());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.INFO).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(1, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }

  @Test
  public void testUpdateWithWrongID() {
    initUserContextWithPermissions(true, true, true, false);

    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);
    JsonObject json = createTestJsonObject(rMetamodel, model);

    ByteArrayInputStream in = new ByteArrayInputStream(json.toString().getBytes());

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);
    assertNotNull(process);

    process.update(new BigInteger("99999999"), isType, in);

    // old description was not set, should be unchanged
    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, IS_NAME).getDescription());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.INFO).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(1, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }
}
