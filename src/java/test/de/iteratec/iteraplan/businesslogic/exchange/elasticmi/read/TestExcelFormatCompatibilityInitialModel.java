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

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess.CheckPoint;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.io.mapper.json.wmetamodel.WMetamodelMapper;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMixinTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WRelationshipExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WUniversalTypeExpression;
import de.iteratec.iteraplan.elasticmi.model.impl.ModelImpl;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiAccessLevel;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiFeatureGroupPermission;
import de.iteratec.iteraplan.elasticmi.permission.ElasticMiTypePermission;
import de.iteratec.iteraplan.elasticmi.permission.Role;
import de.iteratec.iteraplan.elasticmi.permission.User;
import de.iteratec.iteraplan.elasticmi.permission.impl.StakeholderManagerImpl;
import de.iteratec.iteraplan.presentation.dialog.ExcelImport.ImportStrategy;


/**
 *
 */
public class TestExcelFormatCompatibilityInitialModel {
  private static final Logger        LOGGER = Logger.getIteraplanLogger(TestExcelFormatCompatibilityInitialModel.class);

  private IteraplanMiLoadTaskFactory loadTaskFactory;
  private WMetamodel                 metamodel;
  private boolean                    ignoreIEUsage;

  @Before
  public void setUp() throws JsonSyntaxException, IOException {
    InputStream in = getClass().getResourceAsStream("/initial_3_3_0_metamodel.json");
    metamodel = new WMetamodelMapper().read(new JsonParser().parse(IOUtils.toString(in, "UTF-8")));

    IteraplanMiLoadTask loadTask = EasyMock.createMock(IteraplanMiLoadTask.class);
    EasyMock.expect(loadTask.loadWMetamodel()).andReturn(metamodel).anyTimes();
    EasyMock.expect(loadTask.loadModel(EasyMock.anyObject(WMetamodel.class), EasyMock.anyObject(RMetamodel.class))).andReturn(new ModelImpl())
        .anyTimes();
    EasyMock.expect(loadTask.getMetamodelMapping()).andReturn(null).anyTimes();
    EasyMock.expect(loadTask.getInstanceMapping()).andReturn(null).anyTimes();

    loadTaskFactory = EasyMock.createMock(IteraplanMiLoadTaskFactory.class);
    EasyMock.expect(loadTaskFactory.create("MASTER")).andReturn(loadTask).anyTimes();

    EasyMock.replay(loadTaskFactory);
    EasyMock.replay(loadTask);

    StakeholderManagerImpl mgr = new StakeholderManagerImpl();
    Role role = mgr.createRole("testRole");
    for (WUniversalTypeExpression t : metamodel.getUniversalTypes()) {
      ElasticMiTypePermission perm = new ElasticMiTypePermission(t.getPersistentName(), ElasticMiAccessLevel.DELETE);
      mgr.addTypePermission(perm);
      role.grantTypePermission(perm);
    }
    for (WMixinTypeExpression t : metamodel.getMixins()) {
      ElasticMiFeatureGroupPermission perm = new ElasticMiFeatureGroupPermission(t.getPersistentName(), ElasticMiAccessLevel.UPDATE);
      mgr.addFeatureGroupPermission(perm);
      role.grantFeatureGroupPermission(perm);
    }
    User user = mgr.createUser("testUser");
    user.addRole(role);
    ElasticMiContext ctx = new ElasticMiContext(user, "MASTER", new ElasticMiExecution(mgr, loadTaskFactory));
    ElasticMiContext.setCurrentContext(ctx);
  }

  @Test
  public void test304Additive() throws Exception {
    ignoreIEUsage = true;
    testData(getClass().getResourceAsStream("/excel/compatibility/304_initial.xls"), ImportStrategy.ADDITIVE);
  }

  @Test
  public void test304CUD() throws Exception {
    ignoreIEUsage = true;
    testData(getClass().getResourceAsStream("/excel/compatibility/304_initial.xls"), ImportStrategy.CUD);
  }

  @Test
  public void test310XlsAdditive() throws Exception {
    ignoreIEUsage = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/310_initial.xls"), ImportStrategy.ADDITIVE);
  }

  @Test
  public void test310XlsCUD() throws Exception {
    ignoreIEUsage = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/310_initial.xls"), ImportStrategy.CUD);
  }

  @Test
  public void test310XlsxAdditive() throws Exception {
    ignoreIEUsage = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/310_initial.xlsx"), ImportStrategy.ADDITIVE);
  }

  @Test
  public void test310XlsxCUD() throws Exception {
    ignoreIEUsage = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/310_initial.xlsx"), ImportStrategy.CUD);
  }

  @Test
  public void test320XlsAdditive() throws Exception {
    ignoreIEUsage = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/320_initial.xls"), ImportStrategy.ADDITIVE);
  }

  @Test
  public void test320XlsCUD() throws Exception {
    ignoreIEUsage = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/320_initial.xls"), ImportStrategy.CUD);
  }

  @Test
  public void test320XlsxAdditive() throws Exception {
    ignoreIEUsage = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/320_initial.xlsx"), ImportStrategy.ADDITIVE);
  }

  @Test
  public void test320XlsxCUD() throws Exception {
    ignoreIEUsage = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/320_initial.xlsx"), ImportStrategy.CUD);
  }

  private void testData(InputStream in, ImportStrategy strategy) throws Exception {
    MiImportProcess process = new MiExcelImportProcess(null, null, null, null, null, strategy, loadTaskFactory, in);
    Assert.assertTrue(process.importAndCheckFile());

    checkMetamodel(process);

    //skip metamodel checkPoints
    process.getCurrentCheckList().pending(CheckPoint.METAMODEL_COMPARE);
    process.getCurrentCheckList().done(CheckPoint.METAMODEL_COMPARE);
    process.getCurrentCheckList().pending(CheckPoint.METAMODEL_MERGE);
    process.getCurrentCheckList().done(CheckPoint.METAMODEL_MERGE);

    Assert.assertTrue(process.dryRun());

    MiImportProcessMessages messages = process.getImportProcessMessages();
    Assert.assertEquals(0, messages.getMessages(Severity.ERROR).size());
    Assert.assertEquals("No changes.", messages.getMessages().get(messages.getMessages().size() - 1).getMessage());
  }

  private void checkMetamodel(MiImportProcess process) {
    for (WUniversalTypeExpression t : metamodel.getUniversalTypes()) {
      Assert.assertNotNull(t.getPersistentName() + " not found",
          process.getImportMetamodel().findStructuredTypeByPersistentName(t.getPersistentName()));
    }
    Assert.assertEquals(metamodel.getUniversalTypes().size(), process.getImportMetamodel().getStructuredTypes().size());
    for (WRelationshipExpression rel : metamodel.getRelationships()) {
      checkRelationship(rel, process);
    }
    Assert.assertEquals(metamodel.getRelationships().size() - (ignoreIEUsage ? 1 : 0), process.getImportMetamodel().getRelationships().size());
  }

  private void checkRelationship(WRelationshipExpression rel, MiImportProcess process) {
    WRelationshipEndExpression rel1 = rel.getRelationshipEnds().get(1);
    WRelationshipEndExpression rel0 = rel.getRelationshipEnds().get(0);
    for (RRelationshipExpression candidate : process.getImportMetamodel().getRelationships()) {
      RRelationshipEndExpression candidate1 = candidate.getRelationshipEnds().get(1);
      RRelationshipEndExpression candidate0 = candidate.getRelationshipEnds().get(0);
      if (candidate1.getType().getPersistentName().equals(rel1.getType().getPersistentName())
          && candidate0.getType().getPersistentName().equals(rel0.getType().getPersistentName())
          && candidate0.getPersistentName().equals(rel0.getPersistentName()) && candidate1.getPersistentName().equals(rel1.getPersistentName())
          || candidate1.getType().getPersistentName().equals(rel0.getType().getPersistentName())
          && candidate0.getType().getPersistentName().equals(rel1.getType().getPersistentName())
          && candidate0.getPersistentName().equals(rel1.getPersistentName()) && candidate1.getPersistentName().equals(rel0.getPersistentName())) {
        return;
      }
    }

    if (ignoreIEUsage && rel1.getType().getPersistentName().equals("InfrastructureElement") && rel1.isSelfRelationship()
        && (rel0.getPersistentName().equals("baseComponents") || rel0.getPersistentName().equals("parentComponents"))) {
      LOGGER.debug("ignoring usage relationship between InfrastructureElement");
    }
    else {
      Assert.fail(rel + " (" + rel1.getType().getPersistentName() + "-" + rel0.getType().getPersistentName() + ") not found");
    }
  }
}
