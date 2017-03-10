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
import java.util.List;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model.MiIteraplanDiffWriterDbTestBase;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiAccessLevel;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiTypePermission;
import de.iteratec.iteraplan.elasticmi.permission.User;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Tests the create operation for the MiJsonMicroImportProcess.
 */
public class MiJsonMicroImportProcessCreateTest extends MiIteraplanDiffWriterDbTestBase {

  private static final String TEST_USER        = "testUser";
  private static final String STORE_IDENTIFIER = "MASTER";

  private static final String TYPE_NAME        = "InformationSystem";

  private static final String NAME_PROPERTY    = "name";
  private static final String TEST_NAME        = "IS TEST 123";

  private void initUserContextWithPermissions(boolean create, boolean update, boolean delete, boolean useLoadTaksFactory) {
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

    ElasticMiLoadTaskFactory factory = ElasticMiLoadTaskFactory.EMPTY_TASK_FACTORY;
    if (useLoadTaksFactory) {
      factory = this.loadTaskFactory;
    }
    ElasticMiContext ctx = new ElasticMiContext(user, STORE_IDENTIFIER, new ElasticMiExecution(mgr, factory));
    ElasticMiContext.setCurrentContext(ctx);
  }

  private JsonObject createNewTestJsonObject() {
    JsonObject json = new JsonObject();
    JsonArray resultArray = new JsonArray();

    JsonObject jsonResult = new JsonObject();
    addValueToJsonObject(jsonResult, NAME_PROPERTY, TEST_NAME);
    addValueToJsonObject(jsonResult, "typeOfStatus", "CURRENT");

    // add relation to an information system domain
    BuildingBlock isd = findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, TEST_ISD_NAME);
    JsonArray isdArray = new JsonArray();
    JsonObject jsonIsd = new JsonObject();
    addValueToJsonObject(jsonIsd, "id", isd.getId().toString());
    addValueToJsonObject(jsonIsd, "elementURI", "https://localhost:8080/data/InformationSystemDomain/" + isd.getId());
    isdArray.add(jsonIsd);
    jsonResult.add("informationSystemDomains", isdArray);

    resultArray.add(jsonResult);
    json.add("result", resultArray);

    return json;
  }

  private void addValueToJsonObject(JsonObject jsonObj, String entryName, String value) {
    JsonArray entryValue = new JsonArray();
    entryValue.add(new JsonPrimitive(value));
    jsonObj.add(entryName, entryValue);
  }

  @Test
  public void testCreate() {

    initUserContextWithPermissions(true, true, true, true);
    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);
    JsonObject json = createNewTestJsonObject();

    ByteArrayInputStream in = new ByteArrayInputStream(json.toString().getBytes());

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);
    assertNotNull(process);

    BigInteger id = process.create(isType, in);
    BuildingBlock createdBB = findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, TEST_NAME);

    assertNotNull(id);
    assertNotNull(createdBB);
    assertEquals(id.intValue(), createdBB.getId().intValue());

    List<Message> infoMessages = process.getImportProcessMessages().getMessages(Severity.INFO);
    List<String> actualInfoStrings = Lists.newArrayList(Lists.transform(infoMessages, new Function<Message, String>() {
      @Override
      public String apply(Message input) {
        return input.getMessage();
      }
    }));
    List<String> expectedInfoStrings = Lists.newArrayList( //
        "Following changes were applied:", //
        "Information Systems added: 1", //
        "Information System Domains changed: 1"); // due to the created element having a relation to one ISD

    assertEquals(expectedInfoStrings, actualInfoStrings);
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }

  @Test
  public void testCreateWithNoPermissions() {
    initUserContextWithPermissions(false, false, false, false);

    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);
    JsonObject json = createNewTestJsonObject();

    ByteArrayInputStream in = new ByteArrayInputStream(json.toString().getBytes());

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);
    assertNotNull(process);

    BigInteger id = process.create(isType, in);

    assertNull(id);
    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, TEST_NAME));
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.INFO).size());
    assertEquals(1, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }

  @Test
  public void testCreateWrongJson() {
    initUserContextWithPermissions(true, true, true, false);

    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);

    ByteArrayInputStream in = new ByteArrayInputStream("".getBytes());

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);
    assertNotNull(process);

    BigInteger id = process.create(isType, in);

    assertNull(id);
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.INFO).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(1, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }

  @Test
  public void testCreateEmptyStream() {
    initUserContextWithPermissions(true, true, true, false);

    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);
    assertNotNull(process);

    BigInteger id = process.create(isType, null);

    assertNull(id);
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.INFO).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(1, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }
}
