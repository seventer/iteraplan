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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import de.iteratec.iteraplan.businesslogic.exchange.common.ImportProcess.CheckPoint;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTaskFactory;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticmi.ElasticMiContext;
import de.iteratec.iteraplan.elasticmi.ElasticMiExecution;
import de.iteratec.iteraplan.elasticmi.io.mapper.json.wmetamodel.WMetamodelMapper;
import de.iteratec.iteraplan.elasticmi.messages.Message;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.messages.merge.AccumulatedCreateMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.AccumulatedDeleteMessage;
import de.iteratec.iteraplan.elasticmi.messages.merge.AccumulatedUpdateMessage;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
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
public class TestExcelFormatCompatibilityBankModel {
  private static final Logger        LOGGER           = Logger.getIteraplanLogger(TestExcelFormatCompatibilityBankModel.class);

  private IteraplanMiLoadTaskFactory loadTaskFactory;
  private WMetamodel                 metamodel;
  private MiImportProcess            process;

  private boolean                    ignoreIEUsage;
  private boolean                    doCheckMetamodel = true;

  private Map<String, Integer>       countsEmpty      = ImmutableMap.of();

  private Map<String, Integer>       counts304        = ImmutableMap.<String, Integer> builder().put("BusinessDomain", Integer.valueOf(8))
                                                          .put("BusinessProcess", Integer.valueOf(21)).put("BusinessUnit", Integer.valueOf(13))
                                                          .put("Product", Integer.valueOf(8)).put("BusinessFunction", Integer.valueOf(9))
                                                          .put("BusinessObject", Integer.valueOf(20)).put("BusinessMapping", Integer.valueOf(147))
                                                          .put("InformationSystemDomain", Integer.valueOf(6))
                                                          .put("InformationSystem", Integer.valueOf(52)).put("InformationFlow", Integer.valueOf(54))
                                                          .put("InformationSystemInterface", Integer.valueOf(33))
                                                          .put("ArchitecturalDomain", Integer.valueOf(7))
                                                          .put("TechnicalComponent", Integer.valueOf(38))
                                                          .put("InfrastructureElement", Integer.valueOf(12)).put("Project", Integer.valueOf(17))
                                                          .put("Isr2BoAssociation", Integer.valueOf(53)).put("Tcr2IeAssociation", Integer.valueOf(2))
                                                          .build();

  private Map<String, Integer>       counts310        = ImmutableMap.<String, Integer> builder().put("BusinessDomain", Integer.valueOf(8))
                                                          .put("BusinessProcess", Integer.valueOf(21)).put("BusinessUnit", Integer.valueOf(13))
                                                          .put("Product", Integer.valueOf(9)).put("BusinessFunction", Integer.valueOf(9))
                                                          .put("BusinessObject", Integer.valueOf(20)).put("BusinessMapping", Integer.valueOf(149))
                                                          .put("InformationSystemDomain", Integer.valueOf(6))
                                                          .put("InformationSystem", Integer.valueOf(52)).put("InformationFlow", Integer.valueOf(54))
                                                          .put("InformationSystemInterface", Integer.valueOf(45))
                                                          .put("ArchitecturalDomain", Integer.valueOf(7))
                                                          .put("TechnicalComponent", Integer.valueOf(38))
                                                          .put("InfrastructureElement", Integer.valueOf(12)).put("Project", Integer.valueOf(17))
                                                          .put("Isr2BoAssociation", Integer.valueOf(53)).put("Tcr2IeAssociation", Integer.valueOf(2))
                                                          .build();

  private Map<String, Integer>       counts320        = ImmutableMap.<String, Integer> builder().put("BusinessDomain", Integer.valueOf(8))
                                                          .put("BusinessProcess", Integer.valueOf(21)).put("BusinessUnit", Integer.valueOf(13))
                                                          .put("Product", Integer.valueOf(8)).put("BusinessFunction", Integer.valueOf(9))
                                                          .put("BusinessObject", Integer.valueOf(20)).put("BusinessMapping", Integer.valueOf(147))
                                                          .put("InformationSystemDomain", Integer.valueOf(6))
                                                          .put("InformationSystem", Integer.valueOf(52)).put("InformationFlow", Integer.valueOf(54))
                                                          .put("InformationSystemInterface", Integer.valueOf(45))
                                                          .put("ArchitecturalDomain", Integer.valueOf(7))
                                                          .put("TechnicalComponent", Integer.valueOf(38))
                                                          .put("InfrastructureElement", Integer.valueOf(12)).put("Project", Integer.valueOf(17))
                                                          .put("Isr2BoAssociation", Integer.valueOf(53)).put("Tcr2IeAssociation", Integer.valueOf(2))
                                                          .build();

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
    ctx.setLocale(Locale.ENGLISH);
    ElasticMiContext.setCurrentContext(ctx);

    for (WUniversalTypeExpression ute : metamodel.getUniversalTypes()) {
      ute.setName(ute.getPersistentName(), Locale.ENGLISH);
      ute.setPluralName(ute.getPersistentName(), Locale.ENGLISH);
    }

    ignoreIEUsage = false;
  }

  @Test
  public void test304Additive() throws Exception {
    ignoreIEUsage = true;
    testData(getClass().getResourceAsStream("/excel/compatibility/304_bank.xls"), ImportStrategy.ADDITIVE, counts304, countsEmpty, countsEmpty);
    doCheckMetamodel = false;
    //FIXME expect 2 changed informationsystems!
    testData(getClass().getResourceAsStream("/excel/compatibility/304_bank_mod.xls"), ImportStrategy.ADDITIVE, countsEmpty,
        ImmutableMap.of("InformationSystem", Integer.valueOf(1)), countsEmpty);
  }

  @Test
  public void test304CUD() throws Exception {
    ignoreIEUsage = true;
    testData(getClass().getResourceAsStream("/excel/compatibility/304_bank.xls"), ImportStrategy.CUD, counts304, countsEmpty, countsEmpty);
    doCheckMetamodel = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/304_bank_mod.xls"), ImportStrategy.CUD, countsEmpty,
        ImmutableMap.of("InformationSystem", Integer.valueOf(2)), ImmutableMap.of("InformationSystem", Integer.valueOf(50)));
  }

  @Test
  public void test310XlsAdditive() throws Exception {
    testData(getClass().getResourceAsStream("/excel/compatibility/310_bank.xls"), ImportStrategy.ADDITIVE, counts310, countsEmpty, countsEmpty);
    doCheckMetamodel = false;
  }

  @Test
  public void test310XlsCUD() throws Exception {
    testData(getClass().getResourceAsStream("/excel/compatibility/310_bank.xls"), ImportStrategy.CUD, counts310, countsEmpty, countsEmpty);
    doCheckMetamodel = false;
  }

  @Test
  public void test310XlsxAdditive() throws Exception {
    testData(getClass().getResourceAsStream("/excel/compatibility/310_bank.xlsx"), ImportStrategy.ADDITIVE, counts310, countsEmpty, countsEmpty);
    doCheckMetamodel = false;
    //FIXME expect 2 changed informationsystems!
    testData(getClass().getResourceAsStream("/excel/compatibility/310_bank_mod.xlsx"), ImportStrategy.ADDITIVE, countsEmpty,
        ImmutableMap.of("InformationSystem", Integer.valueOf(1)), countsEmpty);
  }

  @Test
  public void test310XlsxCUD() throws Exception {
    testData(getClass().getResourceAsStream("/excel/compatibility/310_bank.xlsx"), ImportStrategy.CUD, counts310, countsEmpty, countsEmpty);
    doCheckMetamodel = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/310_bank_mod.xlsx"), ImportStrategy.CUD, countsEmpty,
        ImmutableMap.of("InformationSystem", Integer.valueOf(2)), ImmutableMap.of("InformationSystem", Integer.valueOf(50)));
  }

  @Test
  public void test320XlsAdditive() throws Exception {
    testData(getClass().getResourceAsStream("/excel/compatibility/320_bank.xls"), ImportStrategy.ADDITIVE, counts320, countsEmpty, countsEmpty);
    doCheckMetamodel = false;
  }

  @Test
  public void test320XlsCUD() throws Exception {
    testData(getClass().getResourceAsStream("/excel/compatibility/320_bank.xls"), ImportStrategy.CUD, counts320, countsEmpty, countsEmpty);
    doCheckMetamodel = false;
  }

  @Test
  public void test320XlsxAdditive() throws Exception {
    testData(getClass().getResourceAsStream("/excel/compatibility/320_bank.xlsx"), ImportStrategy.ADDITIVE, counts320, countsEmpty, countsEmpty);
    doCheckMetamodel = false;
    //FIXME expect 2 changed informationsystems!
    testData(getClass().getResourceAsStream("/excel/compatibility/320_bank_mod.xlsx"), ImportStrategy.ADDITIVE, countsEmpty,
        ImmutableMap.of("InformationSystem", Integer.valueOf(1)), countsEmpty);
  }

  @Test
  public void test320XlsxCUD() throws Exception {
    testData(getClass().getResourceAsStream("/excel/compatibility/320_bank.xlsx"), ImportStrategy.CUD, counts320, countsEmpty, countsEmpty);
    doCheckMetamodel = false;
    testData(getClass().getResourceAsStream("/excel/compatibility/320_bank_mod.xlsx"), ImportStrategy.CUD, countsEmpty,
        ImmutableMap.of("InformationSystem", Integer.valueOf(2)), ImmutableMap.of("InformationSystem", Integer.valueOf(50)));
  }

  private void testData(InputStream in, ImportStrategy strategy, Map<String, Integer> createCounts, Map<String, Integer> updateCounts,
                        Map<String, Integer> deleteCounts) throws Exception {
    process = new MiExcelImportProcess(null, null, null, null, null, strategy, loadTaskFactory, in);

    if (!process.importAndCheckFile()) {
      failWithMessages();
    }

    if (doCheckMetamodel) {
      checkMetamodel();
    }

    //skip metamodel checkPoints
    process.getCurrentCheckList().pending(CheckPoint.METAMODEL_COMPARE);
    process.getCurrentCheckList().done(CheckPoint.METAMODEL_COMPARE);
    process.getCurrentCheckList().pending(CheckPoint.METAMODEL_MERGE);
    process.getCurrentCheckList().done(CheckPoint.METAMODEL_MERGE);

    if (!process.dryRun()) {
      failWithMessages();
    }

    MiImportProcessMessages messages = process.getImportProcessMessages();
    Assert.assertEquals(0, messages.getMessages(Severity.ERROR).size());

    List<String> messageStrings = Lists.transform(messages.getMessages(), new Function<Message, String>() {
      public String apply(Message input) {
        return input.getMessage();
      }
    });

    //apart from merge messages there are 3 additional messages issued
    Assert.assertEquals(messageStrings.toString(), createCounts.size() + updateCounts.size() + deleteCounts.size() + 3, messageStrings.size());

    for (String key : createCounts.keySet()) {
      RStructuredTypeExpression t = process.getCanonicMetamodel().findStructuredTypeByPersistentName(key);
      AccumulatedCreateMessage expectedMessage = new AccumulatedCreateMessageMock(t, createCounts.get(t.getPersistentName()).intValue());
      Assert.assertTrue(expectedMessage.getMessage() + "\n" + messageStrings, messageStrings.contains(expectedMessage.getMessage()));
    }
    for (String key : updateCounts.keySet()) {
      RStructuredTypeExpression t = process.getCanonicMetamodel().findStructuredTypeByPersistentName(key);
      AccumulatedUpdateMessage expectedMessage = new AccumulatedUpdateMessageMock(t, updateCounts.get(t.getPersistentName()).intValue());
      Assert.assertTrue(expectedMessage.getMessage() + "\n" + messageStrings, messageStrings.contains(expectedMessage.getMessage()));
    }
    for (String key : deleteCounts.keySet()) {
      RStructuredTypeExpression t = process.getCanonicMetamodel().findStructuredTypeByPersistentName(key);
      AccumulatedDeleteMessage expectedMessage = new AccumulatedDeleteMessageMock(t, deleteCounts.get(t.getPersistentName()).intValue());
      Assert.assertTrue(expectedMessage.getMessage() + "\n" + messageStrings, messageStrings.contains(expectedMessage.getMessage()));
    }
  }

  private void checkMetamodel() {
    for (WUniversalTypeExpression t : metamodel.getUniversalTypes()) {
      Assert.assertNotNull(t.getPersistentName() + " not found",
          process.getImportMetamodel().findStructuredTypeByPersistentName(t.getPersistentName()));
    }
    Assert.assertEquals(metamodel.getUniversalTypes().size(), process.getImportMetamodel().getStructuredTypes().size());
    for (WRelationshipExpression rel : metamodel.getRelationships()) {
      checkRelationship(rel);
    }
    Assert.assertEquals(metamodel.getRelationships().size() - (ignoreIEUsage ? 1 : 0), process.getImportMetamodel().getRelationships().size());
  }

  private void failWithMessages() {
    List<String> messageStrings = Lists.transform(process.getImportProcessMessages().getMessages(), new Function<Message, String>() {
      public String apply(Message input) {
        return input.getMessage();
      }
    });
    Assert.fail(messageStrings.toString());
  }

  private void checkRelationship(WRelationshipExpression rel) {
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

  private static final class AccumulatedCreateMessageMock extends AccumulatedCreateMessage {
    private final int subMessageCount;

    public AccumulatedCreateMessageMock(RStructuredTypeExpression type, int subMessageCount) {
      super(type, null);
      this.subMessageCount = subMessageCount;
    }

    /**{@inheritDoc}**/
    @Override
    protected int getSubMessageCount() {
      return subMessageCount;
    }
  }

  private static final class AccumulatedUpdateMessageMock extends AccumulatedUpdateMessage {
    private final int subMessageCount;

    public AccumulatedUpdateMessageMock(RStructuredTypeExpression type, int subMessageCount) {
      super(type, null);
      this.subMessageCount = subMessageCount;
    }

    /**{@inheritDoc}**/
    @Override
    protected int getSubMessageCount() {
      return subMessageCount;
    }
  }

  private static final class AccumulatedDeleteMessageMock extends AccumulatedDeleteMessage {
    private final int subMessageCount;

    public AccumulatedDeleteMessageMock(RStructuredTypeExpression type, int subMessageCount) {
      super(type, null);
      this.subMessageCount = subMessageCount;
    }

    /**{@inheritDoc}**/
    @Override
    protected int getSubMessageCount() {
      return subMessageCount;
    }
  }
}
