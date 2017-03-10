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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.WMetamodelExport;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.MiImportProcessMessages;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model.SimpleModelDiff.AssignmentChanges;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyChange;
import de.iteratec.iteraplan.elasticmi.diff.model.SingleRelEndChangeFactory;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.FullMergeStrategy;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class MiIteraplanDiffWriterDeleteDbTest extends MiIteraplanDiffWriterDbTestBase {

  /**
   * Test delete with a simple delete diff,
   * on an element with a large scope.
   */
  @Test
  public void testDeleteReleaseBySingleDeleteDiff() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    ObjectExpression oeBI = object(isType, model, "IS1 # 1");
    modelDiff.addDelete(isType, oeBI);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1"));
    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEM, "IS1"));
    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS2 # 1"));
    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEM, "IS2"));
    assertEquals(0, ((BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP2")).getBusinessMappings().size());
    assertEquals(0, ((BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP3")).getBusinessMappings().size());
    assertEquals(0, bbServiceLocator.getIsiService().loadElementList().size());
  }

  /**
   * Test delete relase with a number of diffs
   * describing all changes in the context.
   */
  @Test
  public void testDeleteReleaseByFullDiffSet() {
    //load data
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    //types
    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RStructuredTypeExpression bmType = type(rMetamodel, "BusinessMapping");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");
    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    RStructuredTypeExpression buType = type(rMetamodel, "BusinessUnit");
    RStructuredTypeExpression prodType = type(rMetamodel, "Product");

    //objects
    ObjectExpression is1 = object(isType, model, "IS1 # 1");
    ObjectExpression is2 = object(isType, model, "IS2 # 1");
    ObjectExpression bp2 = object(bpType, model, "BP2");
    ObjectExpression bp3 = object(bpType, model, "BP3");
    ObjectExpression bu1 = object(buType, model, "BU1");
    ObjectExpression prod1 = object(prodType, model, "PROD1");
    ObjectExpression bm1 = relEnd(bpType, "businessMappings").apply(bp2).getOne();
    ObjectExpression bm2 = relEnd(bpType, "businessMappings").apply(bp3).getOne();
    ElasticValue<ObjectExpression> infoFlows = ifType.apply(model);

    //create diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());

    modelDiff.addDelete(isType, is1);
    modelDiff.addDelete(bmType, bm1);
    modelDiff.addDelete(bmType, bm2);
    for (ObjectExpression infoFlow : infoFlows.getMany()) {
      modelDiff.addDelete(ifType, infoFlow);
    }

    //disconnect parent fo is2
    AssignmentChanges disconnects = AssignmentChanges.create(
        isType.findRelationshipEndByPersistentName(ElasticMiConstants.PERSISTENT_NAME_HIERARCHY_PARENT), ElasticValue.one(is1));
    modelDiff.addUpdate(isType, is2, is2, AssignmentChanges.create(), disconnects, new HashSet<PropertyChange>());

    //bm1 disconnects
    disconnects = AssignmentChanges//
        .create(bmType.findRelationshipEndByPersistentName("informationSystemRelease"), ElasticValue.one(is1))//
        .add(bmType.findRelationshipEndByPersistentName("businessProcess"), ElasticValue.one(bp2))//
        .add(bmType.findRelationshipEndByPersistentName("businessUnit"), ElasticValue.one(bu1))//
        .add(bmType.findRelationshipEndByPersistentName("product"), ElasticValue.one(prod1));
    modelDiff.addUpdate(bmType, bm1, bm1, AssignmentChanges.create(), disconnects, new HashSet<PropertyChange>());

    //disconnects from bm2 to all
    disconnects = AssignmentChanges//
        .create(bmType.findRelationshipEndByPersistentName("informationSystemRelease"), ElasticValue.one(is1))//
        .add(bmType.findRelationshipEndByPersistentName("businessProcess"), ElasticValue.one(bp3))//
        .add(bmType.findRelationshipEndByPersistentName("businessUnit"), ElasticValue.one(bu1))//
        .add(bmType.findRelationshipEndByPersistentName("product"), ElasticValue.one(prod1));
    modelDiff.addUpdate(bmType, bm2, bm2, AssignmentChanges.create(), disconnects, new HashSet<PropertyChange>());

    //disconnects from all info flows to their respective defining ends
    for (ObjectExpression infoFlow : infoFlows.getMany()) {
      RRelationshipEndExpression boREE = ifType.findRelationshipEndByPersistentName("businessObject");
      RRelationshipEndExpression isiREE = ifType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_ISI);
      RRelationshipEndExpression is1REE = ifType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS1);
      RRelationshipEndExpression is2REE = ifType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS2);
      disconnects = AssignmentChanges//
          .create(boREE, boREE.apply(infoFlow))//
          .add(isiREE, isiREE.apply(infoFlow))//
          .add(is1REE, is1REE.apply(infoFlow))//
          .add(is2REE, is2REE.apply(infoFlow));
      modelDiff.addUpdate(ifType, infoFlow, infoFlow, AssignmentChanges.create(), disconnects, new HashSet<PropertyChange>());
    }

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    //Check delete
    commit();
    beginTransaction();

    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1"));
    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEM, "IS1"));
    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS2 # 1"));
    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEM, "IS2"));
    assertEquals(0, ((BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP2")).getBusinessMappings().size());
    assertEquals(0, ((BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP3")).getBusinessMappings().size());
    assertEquals(0, bbServiceLocator.getIsiService().loadElementList().size());
  }

  @Test
  public void testDeleteOneOfTwoInformationFlows() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RStructuredTypeExpression intType = type(rMetamodel, "InformationSystemInterface");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");

    RStructuredTypeExpression boType = type(rMetamodel, "BusinessObject");
    RStructuredTypeExpression infoFlowType = relEnd(boType, "informationFlows").getType();
    ObjectExpression bo1 = object(boType, model, "BO1");
    ObjectExpression if1 = relEnd(boType, "informationFlows").apply(bo1).getOne();

    //objects
    ObjectExpression is1 = object(isType, model, "IS1 # 1");
    ObjectExpression is2 = object(isType, model, "IS2 # 1");

    //create diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addDelete(infoFlowType, if1);

    AssignmentChanges disconnects = AssignmentChanges//
        .create(relEnd(ifType, "businessObject"), ElasticValue.one(bo1))//
        .add(relEnd(ifType, "informationSystemInterface"), ElasticValue.one(intType.apply(model).getOne()))//
        .add(relEnd(ifType, "informationSystemRelease1"), ElasticValue.one(is1))//
        .add(relEnd(ifType, "informationSystemRelease2"), ElasticValue.one(is2));
    modelDiff.addUpdate(ifType, if1, if1, AssignmentChanges.create(), disconnects, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1"));
    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEM, "IS1"));
    InformationSystemInterface isi = (InformationSystemInterface) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, "IS1-IS2");
    assertNotNull(isi);
    assertEquals(1, isi.getTransports().size());
  }

  @Test
  public void testDeleteAllInformationFlowsOfInterface() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RStructuredTypeExpression intType = type(rMetamodel, "InformationSystemInterface");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");

    RStructuredTypeExpression boType = type(rMetamodel, "BusinessObject");
    RStructuredTypeExpression infoFlowType = relEnd(boType, "informationFlows").getType();
    ObjectExpression bo1 = object(boType, model, "BO1");
    ObjectExpression bo2 = object(boType, model, "BO2");
    ObjectExpression if1 = relEnd(boType, "informationFlows").apply(bo1).getOne();
    ObjectExpression if2 = relEnd(boType, "informationFlows").apply(bo2).getOne();

    //objects
    ObjectExpression is1 = object(isType, model, "IS1 # 1");
    ObjectExpression is2 = object(isType, model, "IS2 # 1");

    //create diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addDelete(infoFlowType, if1);
    modelDiff.addDelete(infoFlowType, if2);

    AssignmentChanges disconnects = AssignmentChanges//
        .create(relEnd(ifType, "businessObject"), ElasticValue.one(bo1))//
        .add(relEnd(ifType, "informationSystemInterface"), ElasticValue.one(intType.apply(model).getOne()))//
        .add(relEnd(ifType, "informationSystemRelease1"), ElasticValue.one(is1))//
        .add(relEnd(ifType, "informationSystemRelease2"), ElasticValue.one(is2));
    modelDiff.addUpdate(ifType, if1, if1, AssignmentChanges.create(), disconnects, new HashSet<PropertyChange>());

    disconnects = AssignmentChanges//
        .create(relEnd(ifType, "businessObject"), ElasticValue.one(bo2))//
        .add(relEnd(ifType, "informationSystemInterface"), ElasticValue.one(intType.apply(model).getOne()))//
        .add(relEnd(ifType, "informationSystemRelease1"), ElasticValue.one(is1))//
        .add(relEnd(ifType, "informationSystemRelease2"), ElasticValue.one(is2));
    modelDiff.addUpdate(ifType, if2, if2, AssignmentChanges.create(), disconnects, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1"));
    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEM, "IS1"));
    InformationSystemInterface isi = (InformationSystemInterface) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, "IS1-IS2");
    assertNotNull(isi);
    assertEquals(0, isi.getTransports().size());
  }

  @Test
  public void testDeleteIsr2BoAssociation() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression isr2boType = type(rMetamodel, "Isr2BoAssociation");

    ElasticValue<ObjectExpression> isr2boAssociations = isr2boType.apply(model);
    assertTrue(isr2boAssociations.isOne());

    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addDelete(isr2boType, isr2boAssociations.getOne());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    assertEquals(0, ((InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1")).getBusinessObjectAssociations()
        .size());
    assertEquals(0, ((BusinessObject) findBb(TypeOfBuildingBlock.BUSINESSOBJECT, "BO1")).getInformationSystemReleaseAssociations().size());
  }

  @Test
  public void testDeleteInterface() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression intType = type(rMetamodel, "InformationSystemInterface");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");

    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());

    RRelationshipEndExpression boREE = ifType.findRelationshipEndByPersistentName("businessObject");
    RRelationshipEndExpression isiREE = ifType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_ISI);
    RRelationshipEndExpression is1REE = ifType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS1);
    RRelationshipEndExpression is2REE = ifType.findRelationshipEndByPersistentName(WMetamodelExport.INFORMATION_FLOW_IS2);
    //deletes and disconnects for the information flows
    for (ObjectExpression infoFlow : ifType.apply(model).getMany()) {
      modelDiff.addDelete(ifType, infoFlow);
      AssignmentChanges disconnects = AssignmentChanges//
          .create(boREE, boREE.apply(infoFlow))//
          .add(isiREE, isiREE.apply(infoFlow))//
          .add(is1REE, is1REE.apply(infoFlow))//
          .add(is2REE, is2REE.apply(infoFlow));
      modelDiff.addUpdate(ifType, infoFlow, infoFlow, AssignmentChanges.create(), disconnects, new HashSet<PropertyChange>());
    }

    //delete diff for the interface
    modelDiff.deleteDiffs.put(intType, new SimpleDeleteDiff(intType, intType.apply(model).getOne()));

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1"));
    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEM, "IS1"));
    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS2 # 1"));
    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEM, "IS2"));
    assertEquals(0, bbServiceLocator.getIsiService().loadElementList().size());
    for (BusinessObject bo : bbServiceLocator.getBoService().loadElementList()) {
      assertEquals(0, bo.getTransports().size());
    }
  }

  @Test
  public void testIgnoreUnsetOfInterfaceDefiningEndsInInfoFlow() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression boType = type(rMetamodel, "BusinessObject");
    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RRelationshipEndExpression toInfoFlows = relEnd(boType, "informationFlows");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");
    RRelationshipEndExpression toIsr1 = relEnd(ifType, WMetamodelExport.INFORMATION_FLOW_IS1);

    ObjectExpression isr1 = object(isType, model, "IS1 # 1");
    ElasticValue<ObjectExpression> isr1EV = ElasticValue.one(isr1);
    ObjectExpression if1 = toInfoFlows.apply(object(boType, model, "BO1")).getOne();
    ObjectExpression if2 = toInfoFlows.apply(object(boType, model, "BO2")).getOne();

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addUpdate(ifType, if1, if1, AssignmentChanges.create(), AssignmentChanges.create(toIsr1, isr1EV), new HashSet<PropertyChange>());
    modelDiff.addUpdate(ifType, if2, if2, AssignmentChanges.create(), AssignmentChanges.create(toIsr1, isr1EV), new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // asserts
    InformationSystemRelease bbIs1 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1");
    InformationSystemRelease bbIs2 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS2 # 1");
    assertNotNull(bbIs1);
    assertNotNull(bbIs2);
    assertEquals(2, bbServiceLocator.getTransportService().loadElementList().size());
    assertEquals(1, bbServiceLocator.getIsiService().loadElementList().size());
  }

  @Test
  public void testDeleteBusinessMapping() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression bmType = type(rMetamodel, "BusinessMapping");
    ObjectExpression bm1 = type(rMetamodel, "BusinessProcess").findRelationshipEndByPersistentName("businessMappings")
        .apply(object(type(rMetamodel, "BusinessProcess"), model, "BP2")).getOne();

    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    AssignmentChanges disconnects = AssignmentChanges.create();
    // diffs
    modelDiff.addDelete(bmType, bm1);
    for (RRelationshipEndExpression relEnd : bmType.getAllRelationshipEnds()) {
      disconnects.add(relEnd, relEnd.apply(bm1));
    }
    modelDiff.addUpdate(bmType, bm1, bm1, AssignmentChanges.create(), disconnects, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    assertEquals(1, ((InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1")).getBusinessMappings().size());
    assertEquals(0, ((BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP2")).getBusinessMappings().size());
    assertEquals(1, ((BusinessUnit) findBb(TypeOfBuildingBlock.BUSINESSUNIT, "BU1")).getBusinessMappings().size());
    assertEquals(2, ((Product) findBb(TypeOfBuildingBlock.PRODUCT, "PROD1")).getBusinessMappings().size());
  }

  @Test
  public void testDeleteProduct() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression bmType = type(rMetamodel, "BusinessMapping");
    RStructuredTypeExpression prodType = type(rMetamodel, "Product");

    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    // diffs
    for (ObjectExpression bm : bmType.apply(model).getMany()) {
      modelDiff.addDelete(bmType, bm);
      AssignmentChanges disconnects = AssignmentChanges.create();
      for (RRelationshipEndExpression relEnd : bmType.getAllRelationshipEnds()) {
        disconnects.add(relEnd, relEnd.apply(bm));
      }
      modelDiff.addUpdate(bmType, bm, bm, AssignmentChanges.create(), disconnects, new HashSet<PropertyChange>());
    }
    modelDiff.addDelete(prodType, prodType.apply(model).getOne());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    assertEquals(1, bbServiceLocator.getProductService().loadElementList().size());
    for (BusinessProcess bp : bbServiceLocator.getBpService().loadElementList()) {
      assertEquals(0, bp.getBusinessMappings().size());
    }
    for (BusinessUnit bu : bbServiceLocator.getBuService().loadElementList()) {
      assertEquals(0, bu.getBusinessMappings().size());
    }
    for (InformationSystemRelease isr : bbServiceLocator.getIsrService().loadElementList()) {
      assertEquals(0, isr.getBusinessMappings().size());
    }
    assertEquals(0, bbServiceLocator.getBusinessMappingService().loadElementList().size());
  }
}
