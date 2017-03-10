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

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.MiImportProcessMessages;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model.SimpleModelDiff.AssignmentChanges;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyChange;
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
import de.iteratec.iteraplan.elasticmi.model.ModelUtil;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.user.User;


public class MiIteraplanDiffWriterUpdateDbTest extends MiIteraplanDiffWriterDbTestBase {

  @Test
  public void testChangeName() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RPropertyExpression nameProperty = isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);

    ObjectExpression is1Original = object(isType, model, "IS1 # 1");
    ObjectExpression is1 = object(isType, editedModel, "IS1 # 1");
    ElasticValue<ObjectExpression> is1EV = ElasticValue.one(is1);
    nameProperty.set(is1EV, evVe("IS1-new # 1"));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(nameProperty, nameProperty.apply(is1Original), nameProperty.apply(is1)));
    modelDiff.addUpdate(isType, is1Original, is1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    assertNotNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, nameProperty.apply(is1).getOne().asString()));
    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, nameProperty.apply(is1Original).getOne().asString()));
  }

  @Test
  public void testChageVersion() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RPropertyExpression nameProperty = isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);

    ObjectExpression is1Original = object(isType, model, "IS1 # 1");
    ObjectExpression is1 = object(isType, editedModel, "IS1 # 1");
    nameProperty.set(ElasticValue.one(is1), evVe("IS1 # 2"));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(nameProperty, nameProperty.apply(is1Original), nameProperty.apply(is1)));
    modelDiff.addUpdate(isType, is1Original, is1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease isr = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, nameProperty.apply(is1).getOne()
        .asString());
    assertNotNull(isr);
    assertEquals("2", isr.getVersion());
    assertNull(findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, nameProperty.apply(is1Original).getOne().asString()));
  }

  @Test
  public void testChangeDescription() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RPropertyExpression descrProperty = isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_DESCRIPTION);

    ObjectExpression is1Original = object(isType, model, "IS1 # 1");
    ObjectExpression is1 = object(isType, editedModel, "IS1 # 1");
    descrProperty.set(ElasticValue.one(is1), evVe("is1 modified description"));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(descrProperty, descrProperty.apply(is1Original), descrProperty.apply(is1)));
    modelDiff.addUpdate(isType, is1Original, is1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BuildingBlock bbIs1 = findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1");
    assertNotNull(bbIs1);
    assertEquals("is1 modified description", bbIs1.getDescription());
  }

  @Test
  public void testChangeTypeOfStatus() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RPropertyExpression typeOfStatus = isType.findPropertyByPersistentName("typeOfStatus");

    ObjectExpression is1Original = object(isType, model, "IS1 # 1");
    ObjectExpression is1 = object(isType, editedModel, "IS1 # 1");
    typeOfStatus.set(ElasticValue.one(is1), evVe(typeOfStatus.getType().fromObject("PLANNED")));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(typeOfStatus, typeOfStatus.apply(is1Original), typeOfStatus.apply(is1)));
    modelDiff.addUpdate(isType, is1Original, is1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease bbIs1 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1");
    assertNotNull(bbIs1);
    assertEquals(TypeOfStatus.PLANNED, bbIs1.getTypeOfStatus());
  }

  @Test
  public void testChangeRuntimePeriod() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RPropertyExpression runtimePeriod = isType.findPropertyByPersistentName("runtimePeriod");

    ObjectExpression is1Original = object(isType, model, "IS1 # 1");
    ObjectExpression is1 = object(isType, editedModel, "IS1 # 1");
    ElasticValue<ValueExpression> rtpValue = evVe(new DateInterval(new Date(), new Date()));
    runtimePeriod.set(ElasticValue.one(is1), rtpValue);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(runtimePeriod, runtimePeriod.apply(is1Original), runtimePeriod.apply(is1)));
    modelDiff.addUpdate(isType, is1Original, is1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease bbIs1 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1");
    assertNotNull(bbIs1);
    assertEquals(rtpValue.getOne().asDuration().getStart(), bbIs1.getRuntimePeriod().getStart());
    assertEquals(rtpValue.getOne().asDuration().getEnd(), bbIs1.getRuntimePeriod().getEnd());
  }

  @Test
  public void testRemoveTextAv() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression prodType = type(rMetamodel, "Product");
    RPropertyExpression textAt = prodType.findPropertyByPersistentName("textAT");

    ObjectExpression prod1Original = object(prodType, model, "PROD1");
    ObjectExpression prod1 = object(prodType, editedModel, "PROD1");
    textAt.set(ElasticValue.one(prod1), evVe());

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(textAt, textAt.apply(prod1Original), textAt.apply(prod1)));
    modelDiff.addUpdate(prodType, prod1Original, prod1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    Product bbProd1 = (Product) findBb(TypeOfBuildingBlock.PRODUCT, "PROD1");
    assertNotNull(bbProd1);
    AttributeType textAttributeType = attributeTypeService.getAttributeTypeByName("textAT");
    assertNotNull(textAttributeType);
    assertNull(bbProd1.getAttributeTypeToAttributeValues().get(textAttributeType));
  }

  @Test
  public void testChangeTextAv() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression prodType = type(rMetamodel, "Product");
    RPropertyExpression textAT = prodType.findPropertyByPersistentName("textAT");

    ObjectExpression prod1Original = object(prodType, model, "PROD1");
    ObjectExpression prod1 = object(prodType, editedModel, "PROD1");
    textAT.set(ElasticValue.one(prod1), evVe("changed text of prod 1"));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(textAT, textAT.apply(prod1Original), textAT.apply(prod1)));
    modelDiff.addUpdate(prodType, prod1Original, prod1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    Product bbProd1 = (Product) findBb(TypeOfBuildingBlock.PRODUCT, "PROD1");
    assertNotNull(bbProd1);
    AttributeType textAttributeType = attributeTypeService.getAttributeTypeByName("textAT");
    assertNotNull(textAttributeType);
    List<AttributeValue> avs = bbProd1.getAttributeTypeToAttributeValues().get(textAttributeType);
    assertEquals(1, avs.size());
    AttributeValue av = avs.iterator().next();
    assertEquals("changed text of prod 1", av.getValue());
  }

  @Test
  public void testRemoveDateAv() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression boType = type(rMetamodel, "BusinessObject");
    RPropertyExpression dateAt = boType.findPropertyByPersistentName("dateAT");

    ObjectExpression prod1Original = object(boType, model, "BO1");
    ObjectExpression prod1 = object(boType, editedModel, "BO1");
    dateAt.set(ElasticValue.one(prod1), evVe());

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(dateAt, dateAt.apply(prod1Original), dateAt.apply(prod1)));
    modelDiff.addUpdate(boType, prod1Original, prod1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessObject bbBo1 = (BusinessObject) findBb(TypeOfBuildingBlock.BUSINESSOBJECT, "BO1");
    assertNotNull(bbBo1);
    AttributeType dateAttributeType = attributeTypeService.getAttributeTypeByName("dateAT");
    assertNotNull(dateAttributeType);
    assertNull(bbBo1.getAttributeTypeToAttributeValues().get(dateAttributeType));
  }

  @Test
  public void testChangeDateAv() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression boType = type(rMetamodel, "BusinessObject");
    RPropertyExpression dateAt = boType.findPropertyByPersistentName("dateAT");

    ObjectExpression prod1Original = object(boType, model, "BO1");
    ObjectExpression prod1 = object(boType, editedModel, "BO1");
    dateAt.set(ElasticValue.one(prod1), evVe(parseDate("13.12.2014")));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(dateAt, dateAt.apply(prod1Original), dateAt.apply(prod1)));
    modelDiff.addUpdate(boType, prod1Original, prod1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessObject bbBo1 = (BusinessObject) findBb(TypeOfBuildingBlock.BUSINESSOBJECT, "BO1");
    assertNotNull(bbBo1);
    AttributeType dateAttributeType = attributeTypeService.getAttributeTypeByName("dateAT");
    assertNotNull(dateAttributeType);
    List<AttributeValue> avs = bbBo1.getAttributeTypeToAttributeValues().get(dateAttributeType);
    assertEquals(1, avs.size());
    AttributeValue av = avs.iterator().next();
    assertEquals(parseDate("13.12.2014"), av.getValue());
  }

  @Test
  public void testRemoveNumberAv() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression buType = type(rMetamodel, "BusinessUnit");
    RPropertyExpression numProp = buType.findPropertyByPersistentName("numberAT");
    ObjectExpression prod1Original = object(buType, model, "BU1");
    ObjectExpression prod1 = object(buType, editedModel, "BU1");
    numProp.set(ElasticValue.one(prod1), evVe());

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(numProp, numProp.apply(prod1Original), numProp.apply(prod1)));
    modelDiff.addUpdate(buType, prod1Original, prod1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessUnit bbBu1 = (BusinessUnit) findBb(TypeOfBuildingBlock.BUSINESSUNIT, "BU1");
    assertNotNull(bbBu1);
    AttributeType numberAttributeType = attributeTypeService.getAttributeTypeByName("numberAT");
    assertNotNull(numberAttributeType);
    assertNull(bbBu1.getAttributeTypeToAttributeValues().get(numberAttributeType));
  }

  @Test
  public void testChangeNumberAv() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression buType = type(rMetamodel, "BusinessUnit");
    RPropertyExpression numProp = buType.findPropertyByPersistentName("numberAT");
    ObjectExpression prod1Original = object(buType, model, "BU1");
    ObjectExpression prod1 = object(buType, editedModel, "BU1");
    numProp.set(ElasticValue.one(prod1), evVe(BigDecimal.valueOf(255)));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(numProp, numProp.apply(prod1Original), numProp.apply(prod1)));
    modelDiff.addUpdate(buType, prod1Original, prod1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessUnit bbBu1 = (BusinessUnit) findBb(TypeOfBuildingBlock.BUSINESSUNIT, "BU1");
    assertNotNull(bbBu1);
    AttributeType numberAttributeType = attributeTypeService.getAttributeTypeByName("numberAT");
    assertNotNull(numberAttributeType);
    List<AttributeValue> avs = bbBu1.getAttributeTypeToAttributeValues().get(numberAttributeType);
    assertEquals(1, avs.size());
    AttributeValue av = avs.iterator().next();
    assertEquals(AtomicDataType.DECIMAL.type().normalize(ElasticValue.one(ValueExpression.create(BigDecimal.valueOf(255)))).getOne().getValue(),
        av.getValue());
  }

  @Test
  public void testRemoveEnumAv() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    RPropertyExpression enumProp = bpType.findPropertyByPersistentName("enumAT");
    ObjectExpression prod1Original = object(bpType, model, "BP1");
    ObjectExpression prod1 = object(bpType, editedModel, "BP1");
    enumProp.set(ElasticValue.one(prod1), evVe());

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(enumProp, enumProp.apply(prod1Original), enumProp.apply(prod1)));
    modelDiff.addUpdate(bpType, prod1Original, prod1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessProcess bbBp1 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP1");
    assertNotNull(bbBp1);
    AttributeType enumAttributeType = attributeTypeService.getAttributeTypeByName("enumAT");
    assertNotNull(enumAttributeType);
    assertNull(bbBp1.getAttributeTypeToAttributeValues().get(enumAttributeType));
  }

  @Test
  public void testChangeEnumAvSingleValue() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    RPropertyExpression enumProp = bpType.findPropertyByPersistentName("enumAT");
    ObjectExpression prod1Original = object(bpType, model, "BP1");
    ObjectExpression prod1 = object(bpType, editedModel, "BP1");
    enumProp.set(ElasticValue.one(prod1), evVe(enumProp.getType().fromObject("enumAV2")));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(enumProp, enumProp.apply(prod1Original), enumProp.apply(prod1)));
    modelDiff.addUpdate(bpType, prod1Original, prod1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessProcess bbBp1 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP1");
    assertNotNull(bbBp1);
    AttributeType enumAttributeType = attributeTypeService.getAttributeTypeByName("enumAT");
    assertNotNull(enumAttributeType);
    List<AttributeValue> avs = bbBp1.getAttributeTypeToAttributeValues().get(enumAttributeType);
    assertEquals(1, avs.size());
    AttributeValue av = avs.iterator().next();
    assertEquals("enumAV2", av.getValueString());
  }

  @Test
  public void testChangeEnumAvMultiValue() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    RPropertyExpression enumProp = bpType.findPropertyByPersistentName("enumAT");
    ObjectExpression prod1Original = object(bpType, model, "BP1");
    ObjectExpression prod1 = object(bpType, editedModel, "BP1");
    enumProp.set(ElasticValue.one(prod1), evVe(enumProp.getType().fromObject("enumAV2"), enumProp.getType().fromObject("enumAV3")));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(enumProp, enumProp.apply(prod1Original), enumProp.apply(prod1)));
    modelDiff.addUpdate(bpType, prod1Original, prod1, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessProcess bbBp1 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP1");
    assertNotNull(bbBp1);
    AttributeType enumAttributeType = attributeTypeService.getAttributeTypeByName("enumAT");
    assertNotNull(enumAttributeType);
    List<AttributeValue> avs = bbBp1.getAttributeTypeToAttributeValues().get(enumAttributeType);
    assertEquals(2, avs.size());
    Set<String> avStrings = Sets.newHashSet();
    for (AttributeValue av : avs) {
      avStrings.add(av.getValueString());
    }
    assertTrue(avStrings.contains("enumAV2"));
    assertTrue(avStrings.contains("enumAV3"));
  }

  @Test
  public void testRemoveResponsibilityAV() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    ObjectExpression is1 = object(isType, editedModel, "IS1 # 1");
    ElasticValue<ObjectExpression> is1EV = ElasticValue.one(is1);
    ObjectExpression is1Original = object(isType, model, "IS1 # 1");

    RPropertyExpression respAtProp = property(isType, "respAT");
    respAtProp.set(is1EV, evVe());

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addUpdate(isType, is1Original, is1, AssignmentChanges.create(), AssignmentChanges.create(),
        Sets.newHashSet(new PropertyChange(respAtProp, respAtProp.apply(is1Original), respAtProp.apply(is1))));

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease bbIsr1 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1");
    assertNotNull(bbIsr1);
    AttributeType respAttributeType = attributeTypeService.getAttributeTypeByName("respAT");
    assertNotNull(respAttributeType);
    assertNull(bbIsr1.getAttributeTypeToAttributeValues().get(respAttributeType));
  }

  @Test
  public void testChangeResponsibilityAvSingleValue() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    ObjectExpression is1 = object(isType, editedModel, "IS1 # 1");
    ElasticValue<ObjectExpression> is1EV = ElasticValue.one(is1);
    ObjectExpression is1Original = object(isType, model, "IS1 # 1");

    RPropertyExpression respAtProp = property(isType, "respAT");
    respAtProp.set(is1EV, evVe("user2"));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addUpdate(isType, is1Original, is1, AssignmentChanges.create(), AssignmentChanges.create(),
        Sets.newHashSet(new PropertyChange(respAtProp, respAtProp.apply(is1Original), respAtProp.apply(is1))));

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease bbIsr1 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1");
    assertNotNull(bbIsr1);
    AttributeType respAttributeType = attributeTypeService.getAttributeTypeByName("respAT");
    assertNotNull(respAttributeType);
    List<AttributeValue> avs = bbIsr1.getAttributeTypeToAttributeValues().get(respAttributeType);
    assertEquals(1, avs.size());
    assertEquals("user2", ((User) avs.iterator().next().getValue()).getLoginName());
  }

  @Test
  public void testChangeReposnsibilityAvMultiValue() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    ObjectExpression is1 = object(isType, editedModel, "IS1 # 1");
    ElasticValue<ObjectExpression> is1EV = ElasticValue.one(is1);
    ObjectExpression is1Original = object(isType, model, "IS1 # 1");

    RPropertyExpression respAtProp = property(isType, "respAT");
    respAtProp.set(is1EV, evVe("user1", "user2"));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addUpdate(isType, is1Original, is1, AssignmentChanges.create(), AssignmentChanges.create(),
        Sets.newHashSet(new PropertyChange(respAtProp, respAtProp.apply(is1Original), respAtProp.apply(is1))));

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease bbIsr1 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1");
    assertNotNull(bbIsr1);
    AttributeType respAttributeType = attributeTypeService.getAttributeTypeByName("respAT");
    assertNotNull(respAttributeType);
    List<AttributeValue> avs = bbIsr1.getAttributeTypeToAttributeValues().get(respAttributeType);
    assertEquals(2, avs.size());
    Set<String> userNames = Sets.newHashSet();
    for (AttributeValue av : avs) {
      userNames.add(((User) av.getValue()).getLoginName());
    }
    assertTrue(userNames.contains("user1"));
    assertTrue(userNames.contains("user2"));
  }

  @Test
  public void testRemoveTransportDirection() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression boType = type(rMetamodel, "BusinessObject");
    RRelationshipEndExpression toInfoFlows = boType.findRelationshipEndByPersistentName("informationFlows");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");
    RPropertyExpression directionProp = ifType.findPropertyByPersistentName("direction");
    ObjectExpression infoFlowOriginal = toInfoFlows.apply(object(boType, model, "BO1")).getOne();
    ObjectExpression infoFlow = toInfoFlows.apply(object(boType, editedModel, "BO1")).getOne();

    directionProp.set(ElasticValue.one(infoFlow), evVe());

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(directionProp, directionProp.apply(infoFlowOriginal), directionProp.apply(infoFlow)));
    modelDiff.addUpdate(ifType, infoFlowOriginal, infoFlow, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessObject bbBo1 = (BusinessObject) findBb(TypeOfBuildingBlock.BUSINESSOBJECT, "BO1");
    assertNotNull(bbBo1);
    assertEquals(1, bbBo1.getTransports().size());
    Transport bbTransport = bbBo1.getTransports().iterator().next();
    assertEquals(Direction.NO_DIRECTION, bbTransport.getDirection());
  }

  @Test
  public void testChangeTransportDirection() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression boType = type(rMetamodel, "BusinessObject");
    RRelationshipEndExpression toInfoFlows = boType.findRelationshipEndByPersistentName("informationFlows");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");
    RPropertyExpression directionProp = ifType.findPropertyByPersistentName("direction");
    ObjectExpression infoFlowOriginal = toInfoFlows.apply(object(boType, model, "BO1")).getOne();
    ObjectExpression infoFlow = toInfoFlows.apply(object(boType, editedModel, "BO1")).getOne();

    directionProp.set(ElasticValue.one(infoFlow), evVe(directionProp.getType().fromObject("FIRST_TO_SECOND")));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    Set<PropertyChange> propChanges = Sets.newHashSet();
    propChanges.add(new PropertyChange(directionProp, directionProp.apply(infoFlowOriginal), directionProp.apply(infoFlow)));
    modelDiff.addUpdate(ifType, infoFlowOriginal, infoFlow, AssignmentChanges.create(), AssignmentChanges.create(), propChanges);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessObject bbBo1 = (BusinessObject) findBb(TypeOfBuildingBlock.BUSINESSOBJECT, "BO1");
    assertNotNull(bbBo1);
    assertEquals(1, bbBo1.getTransports().size());
    Transport bbTransport = bbBo1.getTransports().iterator().next();
    assertEquals(Direction.FIRST_TO_SECOND, bbTransport.getDirection());
  }

  @Test
  public void testChangeParent() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    RRelationshipEndExpression parentRelEnd = relEnd(bpType, ElasticMiConstants.PERSISTENT_NAME_HIERARCHY_PARENT);
    ObjectExpression bp2Original = object(bpType, model, "BP2");
    ObjectExpression bp2 = object(bpType, editedModel, "BP2");

    ElasticValue<ObjectExpression> bp2EV = ElasticValue.one(bp2);
    ElasticValue<ObjectExpression> oldParent = ElasticValue.one(object(bpType, editedModel, "BP1"));
    ElasticValue<ObjectExpression> newParent = ElasticValue.one(object(bpType, editedModel, "BP3"));

    parentRelEnd.connect(editedModel, bp2EV, newParent);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    AssignmentChanges connects = AssignmentChanges.create(parentRelEnd, newParent);
    AssignmentChanges disconnects = AssignmentChanges.create(parentRelEnd, oldParent);
    modelDiff.addUpdate(bpType, bp2Original, bp2, connects, disconnects, new HashSet<PropertyChange>());

    connects = AssignmentChanges.create();
    disconnects = AssignmentChanges.create(parentRelEnd.getOpposite(), bp2EV);
    modelDiff.addUpdate(bpType, object(bpType, model, "BP1"), oldParent.getOne(), connects, disconnects, new HashSet<PropertyChange>());

    connects = AssignmentChanges.create(parentRelEnd.getOpposite(), bp2EV);
    disconnects = AssignmentChanges.create();
    modelDiff.addUpdate(bpType, object(bpType, model, "BP3"), newParent.getOne(), connects, disconnects, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessProcess bbBp1 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP1");
    BusinessProcess bbBp2 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP2");
    BusinessProcess bbBp3 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP3");
    assertNotNull(bbBp1);
    assertNotNull(bbBp2);
    assertNotNull(bbBp3);

    assertEquals("-", bbBp1.getParent().getName());
    assertEquals(1, bbBp1.getChildren().size());
    assertEquals(bbBp3, bbBp1.getChildren().iterator().next());

    assertEquals(bbBp1, bbBp3.getParent());
    assertEquals(1, bbBp3.getChildren().size());
    assertEquals(bbBp2, bbBp3.getChildren().iterator().next());

    assertEquals(bbBp3, bbBp2.getParent());
    assertEquals(0, bbBp2.getChildren().size());
  }

  @Test
  public void testRemoveParent() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    RRelationshipEndExpression parentRelEnd = relEnd(bpType, ElasticMiConstants.PERSISTENT_NAME_HIERARCHY_PARENT);

    ObjectExpression bp1Original = object(bpType, model, "BP1");
    ObjectExpression bp2Original = object(bpType, model, "BP2");

    ObjectExpression bp1 = object(bpType, editedModel, "BP1");
    ObjectExpression bp2 = object(bpType, editedModel, "BP2");

    ElasticValue<ObjectExpression> bp1EV = ElasticValue.one(bp1);
    ElasticValue<ObjectExpression> bp2EV = ElasticValue.one(bp2);

    parentRelEnd.disconnect(model, bp2EV, bp1EV);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    AssignmentChanges disconnBp2Bp1 = AssignmentChanges.create(parentRelEnd, bp1EV);
    modelDiff.addUpdate(bpType, bp2Original, bp2, AssignmentChanges.create(), disconnBp2Bp1, new HashSet<PropertyChange>());
    AssignmentChanges disconnBp1Bp2 = AssignmentChanges.create(parentRelEnd.getOpposite(), bp2EV);
    modelDiff.addUpdate(bpType, bp1Original, bp1, AssignmentChanges.create(), disconnBp1Bp2, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    BusinessProcess bbBp1 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP1");
    BusinessProcess bbBp2 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP2");
    BusinessProcess bbBp3 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP3");
    assertNotNull(bbBp1);
    assertNotNull(bbBp2);
    assertNotNull(bbBp3);

    assertEquals("-", bbBp1.getParent().getName());
    assertEquals(1, bbBp1.getChildren().size());
    assertEquals(bbBp3, bbBp1.getChildren().iterator().next());

    assertEquals("-", bbBp2.getParent().getName());
    assertEquals(0, bbBp2.getChildren().size());
  }

  @Test
  public void testChangeBusinessMappingEnds() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression bmType = type(rMetamodel, "BusinessMapping");
    RRelationshipEndExpression toIsr = relEnd(bmType, "informationSystemRelease");
    RRelationshipEndExpression toBp = relEnd(bmType, "businessProcess");
    RStructuredTypeExpression isrType = toIsr.getType();
    RStructuredTypeExpression bpType = toBp.getType();

    ObjectExpression isr1 = object(isrType, editedModel, "IS1 # 1");
    ObjectExpression isr1Original = object(isrType, model, "IS1 # 1");
    ElasticValue<ObjectExpression> isr1EV = ElasticValue.one(isr1);

    ObjectExpression isr2 = object(isrType, editedModel, "IS2 # 1");
    ObjectExpression isr2Original = object(isrType, model, "IS2 # 1");
    ElasticValue<ObjectExpression> isr2EV = ElasticValue.one(isr2);

    ObjectExpression bp1 = object(bpType, editedModel, "BP1");
    ObjectExpression bp1Original = object(bpType, model, "BP1");
    ElasticValue<ObjectExpression> bp1EV = ElasticValue.one(bp1);

    ObjectExpression bp2 = object(bpType, editedModel, "BP2");
    ObjectExpression bp2Original = object(bpType, model, "BP2");
    ElasticValue<ObjectExpression> bp2EV = ElasticValue.one(bp2);

    ObjectExpression bm1Original = toBp.getOpposite().apply(bp2Original).getOne();
    ObjectExpression bm1 = toBp.getOpposite().apply(bp2).getOne();
    ElasticValue<ObjectExpression> bm1EV = ElasticValue.one(bm1);

    toBp.connect(editedModel, bm1EV, bp1EV);
    toIsr.connect(editedModel, bm1EV, isr2EV);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());

    AssignmentChanges connBp1Bm1 = AssignmentChanges.create(toBp.getOpposite(), bm1EV);
    modelDiff.addUpdate(bpType, bp1Original, bp1, connBp1Bm1, AssignmentChanges.create(), new HashSet<PropertyChange>());
    AssignmentChanges disconnBp2Bm1 = AssignmentChanges.create(toBp.getOpposite(), bm1EV);
    modelDiff.addUpdate(bpType, bp2Original, bp2, AssignmentChanges.create(), disconnBp2Bm1, new HashSet<PropertyChange>());
    AssignmentChanges disconnIsr1Bm1 = AssignmentChanges.create(toIsr.getOpposite(), bm1EV);
    modelDiff.addUpdate(isrType, isr1Original, isr1, AssignmentChanges.create(), disconnIsr1Bm1, new HashSet<PropertyChange>());
    AssignmentChanges connIsr2Bm1 = AssignmentChanges.create(toIsr.getOpposite(), bm1EV);
    modelDiff.addUpdate(isrType, isr2Original, isr2, connIsr2Bm1, AssignmentChanges.create(), new HashSet<PropertyChange>());

    AssignmentChanges bmConnects = AssignmentChanges.create(toBp, bp1EV).add(toIsr, isr2EV);
    AssignmentChanges bmDisconnects = AssignmentChanges.create(toBp, bp2EV).add(toIsr, isr1EV);
    modelDiff.addUpdate(bmType, bm1Original, bm1, bmConnects, bmDisconnects, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease bbIsr1 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1");
    InformationSystemRelease bbIsr2 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS2 # 1");
    BusinessProcess bbBp1 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP1");
    BusinessProcess bbBp2 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP2");
    BusinessUnit bbBu1 = (BusinessUnit) findBb(TypeOfBuildingBlock.BUSINESSUNIT, "BU1");
    Product bbProd1 = (Product) findBb(TypeOfBuildingBlock.PRODUCT, "PROD1");
    assertNotNull(bbBp1);
    assertNotNull(bbBp2);
    assertNotNull(bbBu1);
    assertNotNull(bbIsr1);
    assertNotNull(bbIsr2);
    assertNotNull(bbProd1);

    assertEquals(2, bbBp1.getBusinessMappings().size());
    assertEquals(0, bbBp2.getBusinessMappings().size());
    assertEquals(2, bbBu1.getBusinessMappings().size());
    assertEquals(1, bbIsr1.getBusinessMappings().size());
    assertEquals(2, bbIsr2.getBusinessMappings().size());
    assertEquals(3, bbProd1.getBusinessMappings().size());

    boolean foundModifiedBm = false;
    for (BusinessMapping bm : bbServiceLocator.getBusinessMappingService().loadElementList()) {
      if (bbIsr2.equals(bm.getInformationSystemRelease()) && bbBp1.equals(bm.getBusinessProcess()) && bbBu1.equals(bm.getBusinessUnit())
          && bbProd1.equals(bm.getProduct())) {
        foundModifiedBm = true;
        break;
      }
    }
    assertTrue(foundModifiedBm);
  }

  @Test
  public void testChangeIsr2BoEnds() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isr2BoType = type(rMetamodel, "Isr2BoAssociation");
    RRelationshipEndExpression toIsr = relEnd(isr2BoType, "informationSystemRelease");
    RRelationshipEndExpression toBo = relEnd(isr2BoType, "businessObject");
    RStructuredTypeExpression boType = toBo.getType();
    RStructuredTypeExpression isrType = toIsr.getType();

    ObjectExpression association = isr2BoType.apply(editedModel).getOne();
    ObjectExpression originalAssociation = isr2BoType.apply(model).getOne();
    ElasticValue<ObjectExpression> associationEV = ElasticValue.one(association);

    ObjectExpression isr1Original = toIsr.apply(originalAssociation).getOne();
    ElasticValue<ObjectExpression> isr1EV = toIsr.apply(association);
    ObjectExpression bo1Original = toBo.apply(originalAssociation).getOne();
    ElasticValue<ObjectExpression> bo1EV = toBo.apply(association);

    ObjectExpression bo2 = object(boType, editedModel, "BO2");
    ObjectExpression bo2Original = object(boType, model, "BO2");
    ElasticValue<ObjectExpression> bo2EV = ElasticValue.one(bo2);

    ObjectExpression isr2 = object(isrType, editedModel, "IS1 # 1");
    ObjectExpression isr2Original = object(isrType, model, "IS1 # 1");
    ElasticValue<ObjectExpression> isr2EV = ElasticValue.one(isr2);

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    AssignmentChanges disconnBo1Ass = AssignmentChanges.create(toBo.getOpposite(), associationEV);
    modelDiff.addUpdate(boType, bo1Original, bo1EV.getOne(), AssignmentChanges.create(), disconnBo1Ass, new HashSet<PropertyChange>());
    AssignmentChanges connBo2Ass = AssignmentChanges.create(toBo.getOpposite(), associationEV);
    modelDiff.addUpdate(boType, bo2Original, bo2, connBo2Ass, AssignmentChanges.create(), new HashSet<PropertyChange>());

    AssignmentChanges disconnIsr1Ass = AssignmentChanges.create(toIsr.getOpposite(), associationEV);
    modelDiff.addUpdate(isrType, isr1Original, isr1EV.getOne(), AssignmentChanges.create(), disconnIsr1Ass, new HashSet<PropertyChange>());
    AssignmentChanges connIsr2Ass = AssignmentChanges.create(toIsr.getOpposite(), associationEV);
    modelDiff.addUpdate(isrType, isr2Original, isr2, connIsr2Ass, AssignmentChanges.create(), new HashSet<PropertyChange>());

    AssignmentChanges isr2boConnects = AssignmentChanges.create(toBo, bo2EV).add(toIsr, isr2EV);
    AssignmentChanges isr2boDisconnects = AssignmentChanges.create(toBo, bo1EV).add(toIsr, isr1EV);
    modelDiff.addUpdate(isr2BoType, originalAssociation, association, isr2boConnects, isr2boDisconnects, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease bbIsr1 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1");
    InformationSystemRelease bbIsr2 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS2 # 1");
    BusinessObject bbBo1 = (BusinessObject) findBb(TypeOfBuildingBlock.BUSINESSOBJECT, "BO1");
    BusinessObject bbBo2 = (BusinessObject) findBb(TypeOfBuildingBlock.BUSINESSOBJECT, "BO2");
    assertNotNull(bbBo1);
    assertNotNull(bbBo2);
    assertNotNull(bbIsr1);
    assertNotNull(bbIsr2);
    assertEquals(0, bbBo1.getInformationSystemReleaseAssociations().size());
    assertEquals(1, bbIsr1.getBusinessObjectAssociations().size());
    assertEquals(1, bbBo2.getInformationSystemReleaseAssociations().size());
    assertEquals(0, bbIsr2.getBusinessObjectAssociations().size());
    assertEquals(bbIsr1.getBusinessObjectAssociations().iterator().next(), bbBo2.getInformationSystemReleaseAssociations().iterator().next());
  }

  @Test
  public void testChangeInterfaceDirection() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isiType = type(rMetamodel, "InformationSystemInterface");
    RPropertyExpression directionProp = property(isiType, "interfaceDirection");
    ElasticValue<ObjectExpression> isiEV = isiType.apply(editedModel);
    ObjectExpression isi1 = isiEV.getOne();
    ObjectExpression isi1Original = isiType.apply(model).getOne();

    directionProp.set(isiEV, evVe(directionProp.getType().fromObject("BOTH_DIRECTIONS")));

    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addUpdate(isiType, isi1Original, isi1, AssignmentChanges.create(), AssignmentChanges.create(),
        Sets.newHashSet(new PropertyChange(directionProp, directionProp.apply(isi1Original), directionProp.apply(isi1))));

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemInterface bbIsi1 = bbServiceLocator.getIsiService().loadElementList().iterator().next();
    assertEquals(Direction.BOTH_DIRECTIONS, bbIsi1.getInterfaceDirection());
  }

  @Test
  public void testRemoveInterfaceDirection() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isiType = type(rMetamodel, "InformationSystemInterface");
    RPropertyExpression directionProp = property(isiType, "interfaceDirection");
    ElasticValue<ObjectExpression> isiEV = isiType.apply(editedModel);
    ObjectExpression isi1 = isiEV.getOne();
    ObjectExpression isi1Original = isiType.apply(model).getOne();

    directionProp.set(isiEV, evVe());

    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addUpdate(isiType, isi1Original, isi1, AssignmentChanges.create(), AssignmentChanges.create(),
        Sets.newHashSet(new PropertyChange(directionProp, directionProp.apply(isi1Original), directionProp.apply(isi1))));

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemInterface bbIsi1 = bbServiceLocator.getIsiService().loadElementList().iterator().next();
    assertEquals(Direction.NO_DIRECTION, bbIsi1.getInterfaceDirection());
  }

  @Test
  public void testChangeInterfaceEnds() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression isType = type(rMetamodel, "InformationSystem");
    RStructuredTypeExpression ifType = type(rMetamodel, "InformationFlow");
    RRelationshipEndExpression toIsr1 = relEnd(ifType, "informationSystemRelease1");
    RStructuredTypeExpression boType = type(rMetamodel, "BusinessObject");
    RRelationshipEndExpression toInfoFlow = relEnd(boType, "informationFlows");

    ObjectExpression is1 = object(isType, editedModel, "IS1 # 1");
    ObjectExpression is1Original = object(isType, model, "IS1 # 1");
    ElasticValue<ObjectExpression> is1EV = ElasticValue.one(is1);

    ObjectExpression is2 = object(isType, editedModel, "IS2 # 1");
    ObjectExpression is2Original = object(isType, model, "IS2 # 1");
    ElasticValue<ObjectExpression> is2EV = ElasticValue.one(is2);

    ObjectExpression if1 = toInfoFlow.apply(object(boType, editedModel, "BO1")).getOne();
    ObjectExpression if1Original = toInfoFlow.apply(object(boType, model, "BO1")).getOne();

    ObjectExpression if2 = toInfoFlow.apply(object(boType, editedModel, "BO2")).getOne();
    ObjectExpression if2Original = toInfoFlow.apply(object(boType, model, "BO2")).getOne();

    ElasticValue<ObjectExpression> allIfOesEV = ElasticValue.many(Sets.newHashSet(if1, if2));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    AssignmentChanges disconnIs1Ifs = AssignmentChanges.create(toIsr1.getOpposite(), allIfOesEV);
    modelDiff.addUpdate(isType, is1Original, is1, AssignmentChanges.create(), disconnIs1Ifs, new HashSet<PropertyChange>());
    AssignmentChanges connIs2Ifs = AssignmentChanges.create(toIsr1.getOpposite(), allIfOesEV);
    modelDiff.addUpdate(isType, is2Original, is2, connIs2Ifs, AssignmentChanges.create(), new HashSet<PropertyChange>());
    AssignmentChanges connIf1Is2 = AssignmentChanges.create(toIsr1, is2EV);
    AssignmentChanges disconnIf1Is1 = AssignmentChanges.create(toIsr1, is1EV);
    modelDiff.addUpdate(ifType, if1Original, if1, connIf1Is2, disconnIf1Is1, new HashSet<PropertyChange>());
    modelDiff.addUpdate(ifType, if2Original, if2Original, connIf1Is2, disconnIf1Is1, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // assertions
    InformationSystemRelease bbIsr1 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS1 # 1");
    InformationSystemRelease bbIsr2 = (InformationSystemRelease) findBb(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, "IS2 # 1");
    assertNotNull(bbIsr1);
    assertNotNull(bbIsr2);
    assertEquals(0, bbIsr1.getInterfacesReleaseA().size());
    assertEquals(0, bbIsr1.getInterfacesReleaseB().size());
    assertEquals(1, bbIsr2.getInterfacesReleaseA().size());
    assertEquals(1, bbIsr2.getInterfacesReleaseB().size());
    assertEquals(bbIsr2.getInterfacesReleaseA().iterator().next(), bbIsr2.getInterfacesReleaseB().iterator().next());
  }

  @Test
  public void testSingleSidedConnect() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression bdType = type(rMetamodel, "BusinessDomain");
    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    RRelationshipEndExpression toBp = bdType.findRelationshipEndByPersistentName("businessProcesses");

    ObjectExpression bd1 = object(bdType, editedModel, "BD1");
    ObjectExpression bd1Original = object(bdType, model, "BD1");
    ObjectExpression bp3 = object(bpType, editedModel, "BP3");

    toBp.connect(editedModel, ElasticValue.one(bd1), ElasticValue.one(bp3));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    AssignmentChanges connBd1Bp3 = AssignmentChanges.create(toBp, ElasticValue.one(bp3));
    modelDiff.addUpdate(bdType, bd1Original, bd1, connBd1Bp3, AssignmentChanges.create(), new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // asserts
    BusinessProcess bbBp3 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP3");
    assertNotNull(bbBp3);
    BusinessDomain bbBd1 = (BusinessDomain) findBb(TypeOfBuildingBlock.BUSINESSDOMAIN, "BD1");
    assertNotNull(bbBd1);
    assertTrue(bbBp3.getBusinessDomains().contains(bbBd1));
  }

  @Test
  public void testSingleSidedDisconnectAll() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression bdType = type(rMetamodel, "BusinessDomain");
    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    RRelationshipEndExpression toBp = bdType.findRelationshipEndByPersistentName("businessProcesses");

    ObjectExpression bd1 = object(bdType, editedModel, "BD1");
    ObjectExpression bd1Original = object(bdType, model, "BD1");
    ObjectExpression bp1 = object(bpType, editedModel, "BP1");
    ObjectExpression bp2 = object(bpType, editedModel, "BP2");
    toBp.disconnect(editedModel, ElasticValue.one(bd1), ElasticValue.many(Sets.newHashSet(bp1, bp2)));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    AssignmentChanges disconnBd1Bps = AssignmentChanges.create(toBp, Sets.newHashSet(bp2, bp1));
    modelDiff.addUpdate(bdType, bd1Original, bd1, AssignmentChanges.create(), disconnBd1Bps, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // asserts
    BusinessProcess bbBp1 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP1");
    assertNotNull(bbBp1);
    BusinessProcess bbBp2 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP2");
    assertNotNull(bbBp2);
    BusinessDomain bbBd1 = (BusinessDomain) findBb(TypeOfBuildingBlock.BUSINESSDOMAIN, "BD1");
    assertNotNull(bbBd1);
    assertEquals(0, bbBd1.getBusinessProcesses().size());
    assertEquals(0, bbBp1.getBusinessDomains().size());
    assertEquals(1, bbBp2.getBusinessDomains().size());
  }

  @Test
  public void testSingleSidedDisconnectOne() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression bdType = type(rMetamodel, "BusinessDomain");
    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    RRelationshipEndExpression toBp = bdType.findRelationshipEndByPersistentName("businessProcesses");

    ObjectExpression bd1 = object(bdType, editedModel, "BD1");
    ObjectExpression bd1Original = object(bdType, model, "BD1");
    ObjectExpression bp1 = object(bpType, editedModel, "BP1");

    toBp.disconnect(editedModel, ElasticValue.one(bd1), ElasticValue.one(bp1));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    AssignmentChanges disconnBd1Bp1 = AssignmentChanges.create(toBp, ElasticValue.one(bp1));
    modelDiff.addUpdate(bdType, bd1Original, bd1, AssignmentChanges.create(), disconnBd1Bp1, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // asserts
    BusinessProcess bbBp1 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP1");
    assertNotNull(bbBp1);
    BusinessProcess bbBp2 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP2");
    assertNotNull(bbBp2);
    BusinessDomain bbBd1 = (BusinessDomain) findBb(TypeOfBuildingBlock.BUSINESSDOMAIN, "BD1");
    assertNotNull(bbBd1);
    assertEquals(1, bbBd1.getBusinessProcesses().size());
    assertEquals(0, bbBp1.getBusinessDomains().size());
    assertEquals(2, bbBp2.getBusinessDomains().size());
  }

  @Test
  public void testSingleSidedReConnect() {
    IteraplanMiLoadTask task = loadDbAndCreateTask();
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);
    Model editedModel = ModelUtil.copy(rMetamodel, model);

    RStructuredTypeExpression bpType = type(rMetamodel, "BusinessProcess");
    RRelationshipEndExpression relParent = relEnd(bpType, ElasticMiConstants.PERSISTENT_NAME_HIERARCHY_PARENT);

    ObjectExpression bp1 = object(bpType, editedModel, "BP1");
    ObjectExpression bp2 = object(bpType, editedModel, "BP2");
    ObjectExpression bp2Original = object(bpType, model, "BP2");
    ObjectExpression bp3 = object(bpType, editedModel, "BP3");

    relParent.connect(editedModel, ElasticValue.one(bp2), ElasticValue.one(bp3));

    // diffs
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    AssignmentChanges connBp2Bp3 = AssignmentChanges.create(relParent, ElasticValue.one(bp3));
    AssignmentChanges disconnBp2Bp1 = AssignmentChanges.create(relParent, ElasticValue.one(bp1));
    modelDiff.addUpdate(bpType, bp2Original, bp2, connBp2Bp3, disconnBp2Bp1, new HashSet<PropertyChange>());

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, attributeValueService, bbServiceLocator, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new FullMergeStrategy(writer));

    commit();
    beginTransaction();

    // asserts
    BusinessProcess bbBp1 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP1");
    BusinessProcess bbBp2 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP2");
    BusinessProcess bbBp3 = (BusinessProcess) findBb(TypeOfBuildingBlock.BUSINESSPROCESS, "BP3");
    assertNotNull(bbBp1);
    assertNotNull(bbBp2);
    assertNotNull(bbBp3);
    assertEquals(1, bbBp1.getChildren().size());
    assertEquals(bbBp3, bbBp1.getChildren().iterator().next());
    assertEquals(bbBp2, bbBp3.getChildren().iterator().next());
    assertEquals(bbBp3, bbBp2.getParent());

  }
}
