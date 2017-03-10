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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.MiImportProcessMessages;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model.SimpleModelDiff.AssignmentChanges;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyChange;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyInit;
import de.iteratec.iteraplan.elasticmi.diff.model.SingleRelEndChangeFactory;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.FullMergeStrategy;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.common.base.DateInterval;
import de.iteratec.iteraplan.elasticmi.metamodel.common.impl.atomic.AtomicDataType;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.Isr2BoAssociation;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeValueAssignment;


public class MiIteraplanDiffWriterCreateDbTest extends MiIteraplanDiffWriterDbTestBase {

  @Test
  public void testCreateInformationSystem() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    // create objects
    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    ObjectExpression newIs = isType.create(model);
    newIs.setValue(isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME), evVe("newIS # 1"));
    newIs.setValue(isType.findPropertyByPersistentName("typeOfStatus"), evVe(isType.findPropertyByPersistentName("typeOfStatus").getType()
        .fromObject("CURRENT")));

    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyInit> propInits = Sets.newHashSet();
    propInits.add(new PropertyInit(isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME), evVe("newIS # 1")));
    propInits.add(new PropertyInit(isType.findPropertyByPersistentName("typeOfStatus"), evVe(isType.findPropertyByPersistentName("typeOfStatus")
        .getType().fromObject("CURRENT"))));
    modelDiff.addCreate(isType, newIs, propInits, AssignmentChanges.create());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease newIsBb = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "newIS # 1");
    assertNotNull(newIsBb);
    assertNotNull(newIsBb.getId());
    assertNotNull(newIsBb.getLastModificationTime());
    assertNotNull(newIsBb.getLastModificationUser());
  }

  @Test
  public void testCreateInformationSystemWithAllAttributes() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    // create objects
    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    ObjectExpression newIs = isType.create(model);
    ElasticValue<ObjectExpression> isToSetVals = ElasticValue.one(newIs);

    RPropertyExpression nameProp = isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    ElasticValue<ValueExpression> nameVal = evVe("newIS # 1");
    nameProp.set(isToSetVals, nameVal);

    RPropertyExpression descrProp = isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_DESCRIPTION);
    ElasticValue<ValueExpression> descrVal = evVe("descrOf newIs # 1");
    descrProp.set(isToSetVals, descrVal);

    RPropertyExpression typeOfStatusProp = isType.findPropertyByPersistentName("typeOfStatus");
    ElasticValue<ValueExpression> statusVal = evVe(typeOfStatusProp.getType().fromObject("PLANNED"));
    typeOfStatusProp.set(isToSetVals, statusVal);

    RPropertyExpression runtimePeriodProp = isType.findPropertyByPersistentName("runtimePeriod");
    ElasticValue<ValueExpression> rpVal = evVe(new DateInterval(new Date(), new Date()));
    runtimePeriodProp.set(isToSetVals, rpVal);

    RPropertyExpression enumAtProp = isType.findPropertyByPersistentName("enumAT");
    ElasticValue<ValueExpression> enumAtVal = evVe(enumAtProp.getType().fromObject("enumAV1"));
    enumAtProp.set(isToSetVals, enumAtVal);

    RPropertyExpression numerAtProp = isType.findPropertyByPersistentName("numberAT");
    ElasticValue<ValueExpression> numberAtVal = AtomicDataType.DECIMAL.type().normalize(evVe(BigDecimal.valueOf(210)));
    numerAtProp.set(isToSetVals, numberAtVal);

    RPropertyExpression dateAtProp = isType.findPropertyByPersistentName("dateAT");
    ElasticValue<ValueExpression> dateAtVal = evVe(new Date());
    dateAtProp.set(isToSetVals, dateAtVal);

    RPropertyExpression textAtProp = isType.findPropertyByPersistentName("textAT");
    ElasticValue<ValueExpression> textAtVal = evVe("textAtVal");
    textAtProp.set(isToSetVals, textAtVal);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyInit> propInits = Sets.newHashSet();
    for (RPropertyExpression prop : isType.getAllProperties()) {
      if (prop.apply(newIs).isSome()) {
        propInits.add(new PropertyInit(prop, prop.apply(newIs)));
      }
    }
    modelDiff.addCreate(isType, newIs, propInits, AssignmentChanges.create());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease newIsBb = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "newIS # 1");
    assertNotNull(newIsBb);

    assertEquals(descrVal.getOne().getValue(), newIsBb.getDescription());
    assertEquals(TypeOfStatus.PLANNED, newIsBb.getTypeOfStatus());
    assertEquals(rpVal.getOne().asDuration().getStart(), newIsBb.getRuntimePeriod().getStart());
    assertEquals(rpVal.getOne().asDuration().getEnd(), newIsBb.getRuntimePeriod().getEnd());
    Set<AttributeValueAssignment> avas = newIsBb.getAttributeValueAssignments();
    boolean enumAvFound = false;
    boolean textAvFound = false;
    boolean dateAvFound = false;
    boolean numberAvFound = false;
    for (AttributeValueAssignment ava : avas) {
      if (ava.getAttributeValue().getAbstractAttributeType().getName().equals("enumAT")) {
        enumAvFound = true;
        assertEquals("enumAV1", ava.getAttributeValue().getValue());
      }
      else if (ava.getAttributeValue().getAbstractAttributeType().getName().equals("dateAT")) {
        dateAvFound = true;
        assertTrue(AtomicDataType.DATE.type().findComparisonOperator("=")
            .compare(AtomicDataType.DATE.type().normalize(dateAtVal), ElasticValue.one(ValueExpression.create(ava.getAttributeValue().getValue()))));
      }
      else if (ava.getAttributeValue().getAbstractAttributeType().getName().equals("numberAT")) {
        numberAvFound = true;
        assertEquals(numberAtVal.getOne().asDecimal(), ava.getAttributeValue().getValue());
      }
      else if (ava.getAttributeValue().getAbstractAttributeType().getName().equals("textAT")) {
        textAvFound = true;
        assertEquals(textAtVal.getOne().getValue(), ava.getAttributeValue().getValue());
      }
    }
    assertTrue(numberAvFound);
    assertTrue(enumAvFound);
    assertTrue(textAvFound);
    assertTrue(dateAvFound);
  }

  @Test
  public void testCreateBusinessProcess() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    // create objects
    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    ObjectExpression newBp = bpType.create(model);
    ElasticValue<ObjectExpression> bps = ElasticValue.one(newBp);

    RPropertyExpression nameProp = bpType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    ElasticValue<ValueExpression> nameVal = evVe("newBP");
    nameProp.set(bps, nameVal);

    RPropertyExpression descrProp = bpType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_DESCRIPTION);
    ElasticValue<ValueExpression> descrVal = evVe("newBP descr");
    descrProp.set(bps, descrVal);

    RPropertyExpression enumAtProp = bpType.findPropertyByPersistentName("enumAT");
    ElasticValue<ValueExpression> enumAtVal = evVe(enumAtProp.getType().fromObject("enumAV2"));
    enumAtProp.set(bps, enumAtVal);

    RPropertyExpression numberAtProp = bpType.findPropertyByPersistentName("numberAT");
    ElasticValue<ValueExpression> numberAtVal = evVe(BigDecimal.valueOf(13.44));
    numberAtProp.set(bps, numberAtVal);

    RPropertyExpression dateAtProp = bpType.findPropertyByPersistentName("dateAT");
    ElasticValue<ValueExpression> dateAtVal = evVe(new Date());
    dateAtProp.set(bps, dateAtVal);

    RPropertyExpression textAtProp = bpType.findPropertyByPersistentName("textAt");
    ElasticValue<ValueExpression> textAtVal = evVe("textAtVal");
    textAtProp.set(bps, textAtVal);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyInit> propInits = Sets.newHashSet();
    for (RPropertyExpression prop : bpType.getAllProperties()) {
      if (prop.apply(newBp).isSome()) {
        propInits.add(new PropertyInit(prop, prop.apply(newBp)));
      }
    }
    modelDiff.addCreate(bpType, newBp, propInits, AssignmentChanges.create());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    BusinessProcess newBpBb = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "newBP");
    assertNotNull(newBpBb);

    assertEquals(descrVal.getOne().asString(), newBpBb.getDescription());
    Set<AttributeValueAssignment> avas = newBpBb.getAttributeValueAssignments();
    boolean enumAvFound = false;
    boolean textAvFound = false;
    boolean dateAvFound = false;
    boolean numberAvFound = false;
    for (AttributeValueAssignment ava : avas) {
      if (ava.getAttributeValue().getAbstractAttributeType().getName().equals("enumAT")) {
        enumAvFound = true;
        assertEquals("enumAV2", ava.getAttributeValue().getValue());
      }
      else if (ava.getAttributeValue().getAbstractAttributeType().getName().equals("dateAT")) {
        dateAvFound = true;
        assertTrue(AtomicDataType.DATE.type().findComparisonOperator("=")
            .compare(AtomicDataType.DATE.type().normalize(dateAtVal), ElasticValue.one(ValueExpression.create(ava.getAttributeValue().getValue()))));
      }
      else if (ava.getAttributeValue().getAbstractAttributeType().getName().equals("numberAT")) {
        numberAvFound = true;
        assertEquals(numberAtVal.getOne().asDecimal(), ava.getAttributeValue().getValue());
      }
      else if (ava.getAttributeValue().getAbstractAttributeType().getName().equals("textAT")) {
        textAvFound = true;
        assertEquals(textAtVal.getOne().getValue(), ava.getAttributeValue().getValue());
      }
    }
    assertTrue(numberAvFound);
    assertTrue(enumAvFound);
    assertTrue(textAvFound);
    assertTrue(dateAvFound);
  }

  @Test
  public void testCreateBusinessProcessWithExistingParent() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    // create objects
    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    ObjectExpression bp1 = object(bpType, model, "BP1");
    ObjectExpression newBp = bpType.create(model);
    ElasticValue<ObjectExpression> bps = ElasticValue.one(newBp);

    RPropertyExpression nameProp = bpType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    ElasticValue<ValueExpression> nameVal = evVe("newBP");
    nameProp.set(bps, nameVal);

    // diffs    
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyInit> propInits = Sets.newHashSet(new PropertyInit(nameProp, nameVal));
    RRelationshipEndExpression parentREE = bpType.findRelationshipEndByPersistentName(ElasticMiConstants.PERSISTENT_NAME_HIERARCHY_PARENT);
    AssignmentChanges connects = AssignmentChanges.create(parentREE, ElasticValue.one(bp1));
    modelDiff.addCreate(bpType, newBp, propInits, connects);

    RRelationshipEndExpression childrenREE = bpType.findRelationshipEndByPersistentName(ElasticMiConstants.PERSISTENT_NAME_HIERARCHY_CHILDREN);
    connects = AssignmentChanges.create(childrenREE, bps);
    modelDiff.addUpdate(bpType, bp1, bp1, connects, AssignmentChanges.create(), new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessProcess bp1Bb = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP1");
    BusinessProcess newBpBb = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "newBP");

    assertNotNull(bp1Bb);
    assertNotNull(newBpBb);

    assertEquals(bp1Bb, newBpBb.getParent());
    assertTrue(bp1Bb.getChildren().contains(newBpBb));
  }

  @Test
  public void testCreateBusinessProcessWithNewParent() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    // create objects
    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    ObjectExpression newParentBp = bpType.create(model);
    ObjectExpression newChildBp = bpType.create(model);
    ElasticValue<ObjectExpression> parentEV = ElasticValue.one(newParentBp);
    ElasticValue<ObjectExpression> childEV = ElasticValue.one(newChildBp);

    RPropertyExpression nameProp = bpType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    ElasticValue<ValueExpression> parentName = evVe("newParentBp");
    ElasticValue<ValueExpression> childName = evVe("newChildBp");
    nameProp.set(parentEV, parentName);
    nameProp.set(childEV, childName);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());

    Set<PropertyInit> propInits = Sets.newHashSet(new PropertyInit(nameProp, parentName));
    RRelationshipEndExpression childrenREE = bpType.findRelationshipEndByPersistentName(ElasticMiConstants.PERSISTENT_NAME_HIERARCHY_CHILDREN);
    AssignmentChanges connects = AssignmentChanges.create(childrenREE, childEV);
    modelDiff.addCreate(bpType, newParentBp, propInits, connects);

    propInits = Sets.newHashSet(new PropertyInit(nameProp, childName));
    RRelationshipEndExpression parentREE = bpType.findRelationshipEndByPersistentName(ElasticMiConstants.PERSISTENT_NAME_HIERARCHY_PARENT);
    connects = AssignmentChanges.create(parentREE, parentEV);
    modelDiff.addCreate(bpType, newChildBp, propInits, connects);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessProcess newParentBb = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "newParentBp");
    BusinessProcess newChildBb = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "newChildBp");

    assertNotNull(newParentBb);
    assertNotNull(newChildBp);

    assertEquals(newParentBb, newChildBb.getParent());
    assertEquals(1, newParentBb.getChildren().size());
    assertEquals(newChildBb, newParentBb.getChildren().iterator().next());
  }

  @Test
  public void testCreateBusinessMappingWithExistingEnds() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    // create objects
    RStructuredTypeExpression bmType = type(rMetamodel, "BusinessMapping");
    RRelationshipEndExpression toIsRelEnd = bmType.findRelationshipEndByPersistentName("informationSystemRelease");
    RRelationshipEndExpression toBpRelEnd = bmType.findRelationshipEndByPersistentName("businessProcess");
    RRelationshipEndExpression toBuRelEnd = bmType.findRelationshipEndByPersistentName("businessUnit");
    RRelationshipEndExpression toProdRelEnd = bmType.findRelationshipEndByPersistentName("product");

    ObjectExpression bp2 = object(toBpRelEnd.getType(), model, "BP2");
    ObjectExpression bu2 = object(toBuRelEnd.getType(), model, "BU2");
    ObjectExpression prod1 = object(toProdRelEnd.getType(), model, "PROD1");
    ObjectExpression is2 = object(toIsRelEnd.getType(), model, "IS2 # 1");

    ObjectExpression newBm = bmType.create(model);
    ElasticValue<ObjectExpression> bmEV = ElasticValue.one(newBm);
    toBpRelEnd.connect(model, bmEV, ElasticValue.one(bp2));
    toBuRelEnd.connect(model, bmEV, ElasticValue.one(bu2));
    toIsRelEnd.connect(model, bmEV, ElasticValue.one(is2));
    toProdRelEnd.connect(model, bmEV, ElasticValue.one(prod1));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());

    AssignmentChanges connBp2Bm = AssignmentChanges.create(toBpRelEnd.getOpposite(), bmEV);
    modelDiff.addUpdate(toBpRelEnd.getType(), bp2, bp2, connBp2Bm, AssignmentChanges.create(), new HashSet<PropertyChange>());
    AssignmentChanges connBu2Bm = AssignmentChanges.create(toBuRelEnd.getOpposite(), bmEV);
    modelDiff.addUpdate(toBuRelEnd.getType(), bu2, bu2, connBu2Bm, AssignmentChanges.create(), new HashSet<PropertyChange>());
    AssignmentChanges connIs2Bm = AssignmentChanges.create(toIsRelEnd.getOpposite(), bmEV);
    modelDiff.addUpdate(toIsRelEnd.getType(), is2, is2, connIs2Bm, AssignmentChanges.create(), new HashSet<PropertyChange>());
    AssignmentChanges connProd1Bm = AssignmentChanges.create(toProdRelEnd.getOpposite(), bmEV);
    modelDiff.addUpdate(toProdRelEnd.getType(), prod1, prod1, connProd1Bm, AssignmentChanges.create(), new HashSet<PropertyChange>());

    AssignmentChanges connects = AssignmentChanges.create();
    for (RRelationshipEndExpression relEnd : bmType.getAllRelationshipEnds()) {
      connects.add(relEnd, relEnd.apply(newBm));
    }
    modelDiff.addCreate(bmType, newBm, new HashSet<PropertyInit>(), connects);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease bbIs2 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS2 # 1");
    BusinessProcess bbBp2 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP2");
    BusinessUnit bbBu2 = (BusinessUnit) findBb(TypeOfBuildingBlock.BUSINESSUNIT, "BU2");
    Product bbProd1 = (Product) findBb(TypeOfBuildingBlock.PRODUCT, "PROD1");

    boolean foundNewBm = false;
    for (BusinessMapping bm : bbServiceLocator.getBusinessMappingService().loadElementList()) {
      if (bbIs2.equals(bm.getInformationSystemRelease()) && bbBp2.equals(bm.getBusinessProcess()) && bbBu2.equals(bm.getBusinessUnit())
          && bbProd1.equals(bm.getProduct())) {
        foundNewBm = true;
        break;
      }
    }
    assertTrue(foundNewBm);
  }

  @Test
  public void testCreateBusinessMappingWithNewEnds() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    // create objects
    RStructuredTypeExpression bmType = type(rMetamodel, "BusinessMapping");
    RRelationshipEndExpression toIsRelEnd = bmType.findRelationshipEndByPersistentName("informationSystemRelease");
    RRelationshipEndExpression toBpRelEnd = bmType.findRelationshipEndByPersistentName("businessProcess");
    RRelationshipEndExpression toBuRelEnd = bmType.findRelationshipEndByPersistentName("businessUnit");
    RRelationshipEndExpression toProdRelEnd = bmType.findRelationshipEndByPersistentName("product");
    RStructuredTypeExpression isType = toIsRelEnd.getType();
    RStructuredTypeExpression bpType = toBpRelEnd.getType();
    RStructuredTypeExpression buType = toBuRelEnd.getType();
    RStructuredTypeExpression prodType = toProdRelEnd.getType();

    ObjectExpression oeNewIs = isType.create(model);
    ElasticValue<ObjectExpression> oeNewIsEV = ElasticValue.one(oeNewIs);
    RPropertyExpression isNameProp = isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    ElasticValue<ValueExpression> newIsName = evVe("newIS # 1");
    isNameProp.set(oeNewIsEV, newIsName);

    ObjectExpression oeNewBp = bpType.create(model);
    ElasticValue<ObjectExpression> oeNewBpEV = ElasticValue.one(oeNewBp);
    RPropertyExpression bpNameProp = bpType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    ElasticValue<ValueExpression> newBpName = evVe("newBP");
    bpNameProp.set(oeNewBpEV, newBpName);

    ObjectExpression oeNewBu = buType.create(model);
    ElasticValue<ObjectExpression> oeNewBuEV = ElasticValue.one(oeNewBu);
    RPropertyExpression buNameProp = buType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    ElasticValue<ValueExpression> newBuName = evVe("newBU");
    buNameProp.set(oeNewBuEV, newBuName);

    ObjectExpression oeNewProd = prodType.create(model);
    ElasticValue<ObjectExpression> oeNewProdEV = ElasticValue.one(oeNewProd);
    RPropertyExpression prodNameProp = prodType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    ElasticValue<ValueExpression> newProdName = evVe("newPROD");
    prodNameProp.set(oeNewProdEV, newProdName);

    ObjectExpression oeNewBm = bmType.create(model);
    ElasticValue<ObjectExpression> oeNewBmEV = ElasticValue.one(oeNewBm);
    toBpRelEnd.connect(model, oeNewBmEV, oeNewBpEV);
    toBuRelEnd.connect(model, oeNewBmEV, oeNewBuEV);
    toIsRelEnd.connect(model, oeNewBmEV, oeNewIsEV);
    toProdRelEnd.connect(model, oeNewBmEV, oeNewProdEV);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyInit> propInits = Sets.newHashSet(new PropertyInit(bpNameProp, newBpName));
    AssignmentChanges connects = AssignmentChanges.create(toBpRelEnd.getOpposite(), oeNewBmEV);
    modelDiff.addCreate(bpType, oeNewBp, propInits, connects);

    propInits = Sets.newHashSet(new PropertyInit(buNameProp, newBuName));
    connects = AssignmentChanges.create(toBuRelEnd.getOpposite(), oeNewBmEV);
    modelDiff.addCreate(buType, oeNewBu, propInits, connects);

    propInits = Sets.newHashSet(new PropertyInit(isNameProp, newIsName));
    connects = AssignmentChanges.create(toIsRelEnd.getOpposite(), oeNewBmEV);
    modelDiff.addCreate(isType, oeNewIs, propInits, connects);

    propInits = Sets.newHashSet(new PropertyInit(prodNameProp, newProdName));
    connects = AssignmentChanges.create(toProdRelEnd.getOpposite(), oeNewBmEV);
    modelDiff.addCreate(prodType, oeNewProd, propInits, connects);

    propInits = Sets.newHashSet();
    connects = AssignmentChanges//
        .create(toProdRelEnd, oeNewProdEV)//
        .add(toBpRelEnd, oeNewBpEV)//
        .add(toBuRelEnd, oeNewBuEV)//
        .add(toIsRelEnd, oeNewIsEV);
    modelDiff.addCreate(bmType, oeNewBm, propInits, connects);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease bbNewIs = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "newIS # 1");
    BusinessProcess bbNewBp = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "newBP");
    BusinessUnit bbNewBu = (BusinessUnit) findBb(TypeOfBuildingBlock.BUSINESSUNIT, "newBU");
    Product bbNewProd = (Product) findBb(TypeOfBuildingBlock.PRODUCT, "newPROD");

    boolean foundNewBm = false;
    for (BusinessMapping bm : bbServiceLocator.getBusinessMappingService().loadElementList()) {
      if (bbNewIs.equals(bm.getInformationSystemRelease()) && bbNewBp.equals(bm.getBusinessProcess()) && bbNewBu.equals(bm.getBusinessUnit())
          && bbNewProd.equals(bm.getProduct())) {
        foundNewBm = true;
        break;
      }
    }
    assertTrue(foundNewBm);
  }

  @Test
  public void testCreateInterfaceWithoutTransports() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    //create an inteface and a virtual information flow (no business object)
    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RStructuredTypeExpression intType = type(rMetamodel, "InformationSystemInterface");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");
    RRelationshipEndExpression toInterface = ifType.findRelationshipEndByPersistentName("informationSystemInterface");
    RRelationshipEndExpression toIsr1 = ifType.findRelationshipEndByPersistentName("informationSystemRelease1");
    RRelationshipEndExpression toIsr2 = ifType.findRelationshipEndByPersistentName("informationSystemRelease2");
    ObjectExpression is1 = object(toIsr1.getType(), model, "IS1 # 1");
    ObjectExpression is2 = object(toIsr2.getType(), model, "IS2 # 1");

    ObjectExpression oeNewInt = intType.create(model);
    ElasticValue<ObjectExpression> oeNewIntEV = ElasticValue.one(oeNewInt);
    RPropertyExpression intDirection = intType.findPropertyByPersistentName("interfaceDirection");
    intDirection.set(oeNewIntEV, evVe(intDirection.getType().fromObject("FIRST_TO_SECOND")));
    intType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME).set(oeNewIntEV, evVe("newINT"));

    ObjectExpression oeNewIf = ifType.create(model);
    ElasticValue<ObjectExpression> oeNewIfEV = ElasticValue.one(oeNewIf);
    RPropertyExpression ifDirection = ifType.findPropertyByPersistentName("direction");
    ifDirection.set(oeNewIfEV, evVe(ifDirection.getType().fromObject("NO_DIRECTION")));
    toInterface.connect(model, oeNewIfEV, oeNewIntEV);
    toIsr1.connect(model, oeNewIfEV, ElasticValue.one(is1));
    toIsr2.connect(model, oeNewIfEV, ElasticValue.one(is2));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());

    Set<PropertyInit> propInits = Sets.newHashSet();
    for (RPropertyExpression prop : intType.getAllProperties()) {
      if (prop.apply(oeNewInt).isSome()) {
        propInits.add(new PropertyInit(prop, prop.apply(oeNewInt)));
      }
    }
    AssignmentChanges connects = AssignmentChanges.create(toInterface.getOpposite(), oeNewIfEV);
    modelDiff.addCreate(intType, oeNewInt, propInits, connects);

    propInits = Sets.newHashSet();
    connects = AssignmentChanges.create();
    for (RPropertyExpression prop : ifType.getAllProperties()) {
      if (prop.apply(oeNewIf).isSome()) {
        propInits.add(new PropertyInit(prop, prop.apply(oeNewIf)));
      }
    }
    for (RRelationshipEndExpression relEnd : ifType.getAllRelationshipEnds()) {
      if (relEnd.apply(oeNewIf).isSome()) {
        connects.add(relEnd, relEnd.apply(oeNewIf));
      }
    }
    modelDiff.addCreate(ifType, oeNewIf, propInits, connects);

    modelDiff.addUpdate(isType, is1, is1, AssignmentChanges.create(toIsr1.getOpposite(), oeNewIfEV), AssignmentChanges.create(),
        new HashSet<PropertyChange>());
    modelDiff.addUpdate(isType, is2, is2, AssignmentChanges.create(toIsr2.getOpposite(), oeNewIfEV), AssignmentChanges.create(),
        new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemInterface isi = (InformationSystemInterface) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, "newINT");
    assertNotNull(isi);
    assertEquals(0, isi.getTransports().size());
  }

  @Test
  public void testCreateTransportForExistingInterface() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    //create objects
    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RStructuredTypeExpression boType = type(rMetamodel, "BusinessObject");
    RStructuredTypeExpression intType = type(rMetamodel, "InformationSystemInterface");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");
    RRelationshipEndExpression toInterface = ifType.findRelationshipEndByPersistentName("informationSystemInterface");
    RRelationshipEndExpression toIsr1 = ifType.findRelationshipEndByPersistentName("informationSystemRelease1");
    RRelationshipEndExpression toIsr2 = ifType.findRelationshipEndByPersistentName("informationSystemRelease2");
    RRelationshipEndExpression toBo = ifType.findRelationshipEndByPersistentName("businessObject");
    ObjectExpression is1 = object(toIsr1.getType(), model, "IS1 # 1");
    ObjectExpression is2 = object(toIsr2.getType(), model, "IS2 # 1");

    ObjectExpression oeInterface = intType.apply(model).getOne();
    ElasticValue<ObjectExpression> oeInterfaceEV = ElasticValue.one(oeInterface);

    ObjectExpression oeNewBo = boType.create(model);
    ElasticValue<ObjectExpression> oeNewBoEV = ElasticValue.one(oeNewBo);
    boType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME).set(oeNewBoEV, evVe("newBO"));

    ObjectExpression oeNewIf = model.create(ifType);
    ElasticValue<ObjectExpression> oeNewIfEV = ElasticValue.one(oeNewIf);
    RPropertyExpression ifDirection = ifType.findPropertyByPersistentName("direction");
    ifDirection.set(oeNewIfEV, evVe(ifDirection.getType().fromObject("FIRST_TO_SECOND")));
    toInterface.connect(model, oeNewIfEV, oeInterfaceEV);
    toIsr1.connect(model, oeNewIfEV, ElasticValue.one(is1));
    toIsr2.connect(model, oeNewIfEV, ElasticValue.one(is2));
    toBo.connect(model, oeNewIfEV, oeNewBoEV);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyInit> propInits = Sets.newHashSet(new PropertyInit(boType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME),
        boType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME).apply(oeNewBo)));
    AssignmentChanges connects = AssignmentChanges.create(toBo.getOpposite(), oeNewIfEV);
    modelDiff.addCreate(boType, oeNewBo, propInits, connects);

    propInits = Sets.newHashSet(new PropertyInit(ifDirection, ifDirection.apply(oeNewIf)));
    connects = AssignmentChanges.create();
    for (RRelationshipEndExpression relEnd : ifType.getAllRelationshipEnds()) {
      if (relEnd.apply(oeNewIf).isSome()) {
        connects.add(relEnd, relEnd.apply(oeNewIf));
      }
    }
    modelDiff.addCreate(ifType, oeNewIf, propInits, connects);

    //updates for the two is, and the interface
    modelDiff.addUpdate(intType, oeInterface, oeInterface, AssignmentChanges.create(toInterface.getOpposite(), oeNewIfEV),
        AssignmentChanges.create(), new HashSet<PropertyChange>());
    modelDiff.addUpdate(isType, is1, is1, AssignmentChanges.create(toIsr1.getOpposite(), oeNewIfEV), AssignmentChanges.create(),
        new HashSet<PropertyChange>());
    modelDiff.addUpdate(isType, is2, is2, AssignmentChanges.create(toIsr2.getOpposite(), oeNewIfEV), AssignmentChanges.create(),
        new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessObject bbNewBo = (BusinessObject) findBb(TypeOfBuildingBlock.BUSINESSOBJECT, "newBO");
    assertNotNull(bbNewBo);
    assertEquals(1, bbNewBo.getTransports().size());
    Transport bbNewTransport = bbNewBo.getTransports().iterator().next();
    assertEquals(Direction.FIRST_TO_SECOND, bbNewTransport.getDirection());

    InformationSystemInterface bbIsi = bbNewTransport.getInformationSystemInterface();
    assertEquals(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1"), bbIsi.getInformationSystemReleaseA());
    assertEquals(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS2 # 1"), bbIsi.getInformationSystemReleaseB());

    assertEquals(1, bbServiceLocator.getIsiService().loadElementList().size());
  }

  @Test
  public void testCreateTransportForNewInterface() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    //create an inteface and a virtual information flow (no business object)
    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RStructuredTypeExpression boType = type(rMetamodel, "BusinessObject");
    RStructuredTypeExpression intType = type(rMetamodel, "InformationSystemInterface");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");
    RRelationshipEndExpression toInterface = ifType.findRelationshipEndByPersistentName("informationSystemInterface");
    RRelationshipEndExpression toIsr1 = ifType.findRelationshipEndByPersistentName("informationSystemRelease1");
    RRelationshipEndExpression toIsr2 = ifType.findRelationshipEndByPersistentName("informationSystemRelease2");
    RRelationshipEndExpression toBo = ifType.findRelationshipEndByPersistentName("businessObject");
    ObjectExpression is2 = object(toIsr1.getType(), model, "IS1 # 1");
    ObjectExpression is1 = object(toIsr2.getType(), model, "IS2 # 1");
    ObjectExpression bo1 = object(boType, model, "BO1");

    ObjectExpression oeNewInt = intType.create(model);
    ElasticValue<ObjectExpression> oeNewIntEV = ElasticValue.one(oeNewInt);
    RPropertyExpression intDirection = intType.findPropertyByPersistentName("interfaceDirection");
    intDirection.set(oeNewIntEV, evVe(intDirection.getType().fromObject("FIRST_TO_SECOND")));
    intType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME).set(oeNewIntEV, evVe("newINT"));

    ObjectExpression oeNewIf = ifType.create(model);
    ElasticValue<ObjectExpression> oeNewIfEV = ElasticValue.one(oeNewIf);
    RPropertyExpression ifDirection = ifType.findPropertyByPersistentName("direction");
    ifDirection.set(oeNewIfEV, evVe(ifDirection.getType().fromObject("NO_DIRECTION")));
    toInterface.connect(model, oeNewIfEV, oeNewIntEV);
    toIsr1.connect(model, oeNewIfEV, ElasticValue.one(is1));
    toIsr2.connect(model, oeNewIfEV, ElasticValue.one(is2));
    toBo.connect(model, oeNewIfEV, ElasticValue.one(bo1));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());

    Set<PropertyInit> propInits = Sets.newHashSet();
    for (RPropertyExpression prop : intType.getAllProperties()) {
      if (prop.apply(oeNewInt).isSome()) {
        propInits.add(new PropertyInit(prop, prop.apply(oeNewInt)));
      }
    }
    AssignmentChanges connects = AssignmentChanges.create(toInterface.getOpposite(), oeNewIfEV);
    modelDiff.addCreate(intType, oeNewInt, propInits, connects);

    propInits = Sets.newHashSet();
    connects = AssignmentChanges.create();
    for (RPropertyExpression prop : ifType.getAllProperties()) {
      if (prop.apply(oeNewIf).isSome()) {
        propInits.add(new PropertyInit(prop, prop.apply(oeNewIf)));
      }
    }
    for (RRelationshipEndExpression relEnd : ifType.getAllRelationshipEnds()) {
      if (relEnd.apply(oeNewIf).isSome()) {
        connects.add(relEnd, relEnd.apply(oeNewIf));
      }
    }
    modelDiff.addCreate(ifType, oeNewIf, propInits, connects);

    modelDiff.addUpdate(isType, is1, is1, AssignmentChanges.create(toIsr1.getOpposite(), oeNewIfEV), AssignmentChanges.create(),
        new HashSet<PropertyChange>());
    modelDiff.addUpdate(isType, is2, is2, AssignmentChanges.create(toIsr2.getOpposite(), oeNewIfEV), AssignmentChanges.create(),
        new HashSet<PropertyChange>());
    modelDiff.addUpdate(boType, bo1, bo1, AssignmentChanges.create(toBo.getOpposite(), oeNewIfEV), AssignmentChanges.create(),
        new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessObject bbBo1 = (BusinessObject) findBb(TypeOfBuildingBlock.BUSINESSOBJECT, "BO1");
    assertEquals(2, bbBo1.getTransports().size());
    assertEquals(2, bbServiceLocator.getIsiService().loadElementList().size());
    boolean foundNewIsi = false;
    for (InformationSystemInterface isi : bbServiceLocator.getIsiService().loadElementList()) {
      if (findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS2 # 1").equals(isi.getInformationSystemReleaseA())
          && findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1").equals(isi.getInformationSystemReleaseB())) {
        foundNewIsi = true;
        assertEquals(1, isi.getTransports().size());
        Transport tr = isi.getTransports().iterator().next();
        assertEquals(bbBo1, tr.getBusinessObject());
      }
    }
    assertTrue(foundNewIsi);
  }

  @Test
  public void testCreateIsr2BoAssociation() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    //create instance
    RStructuredTypeExpression isr2boType = type(rMetamodel, "Isr2BoAssociation");
    RRelationshipEndExpression toIsr = isr2boType.findRelationshipEndByPersistentName("informationSystemRelease");
    RRelationshipEndExpression toBo = isr2boType.findRelationshipEndByPersistentName("businessObject");

    ObjectExpression is1 = object(toIsr.getType(), model, "IS1 # 1");
    ElasticValue<ObjectExpression> is1EV = ElasticValue.one(is1);
    ObjectExpression bo2 = object(toBo.getType(), model, "BO2");
    ElasticValue<ObjectExpression> bo2EV = ElasticValue.one(bo2);

    ObjectExpression oeIsr2Bo = isr2boType.create(model);
    ElasticValue<ObjectExpression> oeIsr2BoEV = ElasticValue.one(oeIsr2Bo);
    toBo.connect(model, oeIsr2BoEV, bo2EV);
    toIsr.connect(model, oeIsr2BoEV, is1EV);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    AssignmentChanges connects = AssignmentChanges.create(toBo, bo2EV).add(toIsr, is1EV);
    modelDiff.addCreate(isr2boType, oeIsr2Bo, new HashSet<PropertyInit>(), connects);

    connects = AssignmentChanges.create(toBo.getOpposite(), oeIsr2BoEV);
    modelDiff.addUpdate(toBo.getType(), bo2, bo2, connects, AssignmentChanges.create(), new HashSet<PropertyChange>());

    connects = AssignmentChanges.create(toIsr.getOpposite(), oeIsr2BoEV);
    modelDiff.addUpdate(toIsr.getType(), is1, is1, connects, AssignmentChanges.create(), new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessObject bbBo2 = (BusinessObject) findBb(TypeOfBuildingBlock.BUSINESSOBJECT, "BO2");
    assertNotNull(bbBo2);
    assertEquals(1, bbBo2.getInformationSystemReleaseAssociations().size());
    Isr2BoAssociation association = bbBo2.getInformationSystemReleaseAssociations().iterator().next();
    assertEquals(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1"), association.getInformationSystemRelease());
  }

  @Test
  public void testCreateTechnicalComponentRelease() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    RStructuredTypeExpression tcrType = type(rMetamodel, "TechnicalComponent");
    ObjectExpression newTcr = tcrType.create(model);
    ElasticValue<ObjectExpression> newTcrEV = ElasticValue.one(newTcr);

    RPropertyExpression nameProp = property(tcrType, ElasticMiConstants.PERSISTENT_NAME_NAME);
    nameProp.set(newTcrEV, evVe("newTCR # 1"));
    RPropertyExpression statusProp = property(tcrType, "typeOfStatus");
    statusProp.set(newTcrEV, evVe(statusProp.getType().fromObject("PLANNED")));

    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    HashSet<PropertyInit> propInits = Sets.newHashSet(//
        new PropertyInit(statusProp, statusProp.apply(newTcr)),//
        new PropertyInit(nameProp, nameProp.apply(newTcr)));
    modelDiff.addCreate(tcrType, newTcr, propInits, AssignmentChanges.create());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    TechnicalComponentRelease newTcrBb = (TechnicalComponentRelease) findBb(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, "newTCR # 1");
    assertNotNull(newTcrBb);
    assertNotNull(newTcrBb.getId());
    assertNotNull(newTcrBb.getLastModificationTime());
    assertNotNull(newTcrBb.getLastModificationUser());
    assertEquals(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.PLANNED, newTcrBb.getTypeOfStatus());
  }
}
