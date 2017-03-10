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

import java.math.BigInteger;

import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model.MiIteraplanDiffWriterDbTestBase;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiAccessLevel;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiTypePermission;
import de.iteratec.iteraplan.elasticmi.permission.User;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


/**
 * Tests the delete operation for the MiJsonMicroImportProcess.
 */
public class MiJsonMicroImportProcessDeleteTest extends MiIteraplanDiffWriterDbTestBase {

  private static final String TEST_USER        = "testUser";
  private static final String STORE_IDENTIFIER = "MASTER";

  private static final String TYPE_NAME        = "InformationSystem";
  private static final String IS_NAME          = "IS1 # 1";

  private void initUserContextWithPermissions(boolean create, boolean update, boolean delete) {
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

    ElasticMiContext ctx = new ElasticMiContext(user, STORE_IDENTIFIER, new ElasticMiExecution(mgr, loadTaskFactory));
    ElasticMiContext.setCurrentContext(ctx);
  }

  @Test
  public void testDelete() {

    initUserContextWithPermissions(true, true, true);

    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);
    ObjectExpression oeIS = object(isType, model, IS_NAME);
    assertNotNull(oeIS);

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);
    assertNotNull(process);

    process.delete(oeIS.getId(), isType);

    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, IS_NAME));

    // cascading changes due to relations are reported here, too
    assertEquals(10, process.getImportProcessMessages().getMessages(Severity.INFO).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }

  @Test
  public void testDeleteWithoutPermissions() {

    initUserContextWithPermissions(false, false, false);

    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);
    ObjectExpression oeIS = object(isType, model, IS_NAME);
    assertNotNull(oeIS);

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);
    assertNotNull(process);

    process.delete(oeIS.getId(), isType);

    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, IS_NAME));
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.INFO).size());
    assertEquals(1, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }

  @Test
  public void testWrongID() {

    initUserContextWithPermissions(true, true, true);

    IteraplanMiLoadTask task = loadDbAndCreateTask();

    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, TYPE_NAME);

    MiJsonMicroImportProcess process = new MiJsonMicroImportProcess(bbServiceLocator, attributeValueService,
        (IteraplanMiLoadTaskFactory) loadTaskFactory);
    assertNotNull(process);

    process.delete(new BigInteger("9999999"), isType);

    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.INFO).size());
    assertEquals(0, process.getImportProcessMessages().getMessages(Severity.WARNING).size());
    assertEquals(1, process.getImportProcessMessages().getMessages(Severity.ERROR).size());
  }
}
