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

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.IteraplanMiLoadTask;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.MiImportProcessMessages;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.model.SimpleModelDiff.AssignmentChanges;
import de.iteratec.iteraplan.businesslogic.service.AllBuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemService;
import de.iteratec.iteraplan.businesslogic.service.ProjectService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.elasticmi.diff.model.PropertyInit;
import de.iteratec.iteraplan.elasticmi.diff.model.SingleRelEndChangeFactory;
import de.iteratec.iteraplan.elasticmi.diff.model.impl.AdditiveMergeStrategy;
import de.iteratec.iteraplan.elasticmi.load.ElasticMiLoadTaskFactory;
import de.iteratec.iteraplan.elasticmi.metamodel.common.ElasticMiConstants;
import de.iteratec.iteraplan.elasticmi.metamodel.read.REnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RMetamodel;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RNominalEnumerationExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RPropertyExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RRelationshipEndExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.RStructuredTypeExpression;
import de.iteratec.iteraplan.elasticmi.metamodel.read.pojo.PojoRMetamodelCopyUtil;
import de.iteratec.iteraplan.elasticmi.metamodel.write.WMetamodel;
import de.iteratec.iteraplan.elasticmi.model.Model;
import de.iteratec.iteraplan.elasticmi.model.ObjectExpression;
import de.iteratec.iteraplan.elasticmi.model.ValueExpression;
import de.iteratec.iteraplan.elasticmi.util.ElasticValue;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.EnumAV;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


public class MiIteraplanDiffWriterMockTest extends BaseTransactionalTestSupport {

  @Autowired
  private ElasticMiLoadTaskFactory loadTaskFactory;

  @Autowired
  private TestDataHelper2          testDataHelper2;

  @Before
  public void onSetUp() {
    UserContext.setCurrentUserContext(testDataHelper2.createUserContext());
  }

  @Override
  @After
  public void onTearDown() {
    UserContext.detachCurrentUserContext();
    rollback();
  }

  /**
   * Tests if the writer deletes a ISR when called with a DeleteDiff for it. 
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void testDeleteIsr() {
    // setup
    // Create ISR in DB, load metamodel and model, 
    InformationSystem isBI = testDataHelper2.createInformationSystem("BI");
    InformationSystemRelease isrBI1 = testDataHelper2.createInformationSystemRelease(isBI, "1");
    IteraplanMiLoadTask task = (IteraplanMiLoadTask) loadTaskFactory.create("MASTER");
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    // build diff for deletion of ISR
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    RStructuredTypeExpression type = rMetamodel.findStructuredTypeByPersistentName("InformationSystem");
    ObjectExpression oeBI = findByName(type, model, "BI # 1");
    assertNotNull(oeBI); // sanity, not test
    modelDiff.addDelete(type, oeBI);

    // expect: delete the ISR 
    BuildingBlockService<BuildingBlock, Integer> isrServiceMock = (BuildingBlockService) EasyMock.createMock(InformationSystemReleaseService.class);
    EasyMock.expect(isrServiceMock.loadObjectById(isrBI1.getId())).andReturn(isrBI1); // from reload in MiIteraplanDiffWriter
    isrServiceMock.deleteEntity(isrBI1);

    // dummy, no expectations
    AllBuildingBlockService allBbServiceMock = EasyMock.createNiceMock(AllBuildingBlockService.class);

    final BuildingBlockServiceLocator bbServiceLocatorMock = EasyMock.createMock(BuildingBlockServiceLocator.class);
    EasyMock.expect(bbServiceLocatorMock.getService(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)).andReturn(isrServiceMock);
    EasyMock.expect(bbServiceLocatorMock.getAllBBService()).andReturn(allBbServiceMock);

    EasyMock.replay(isrServiceMock, bbServiceLocatorMock, allBbServiceMock);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, null, bbServiceLocatorMock, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new AdditiveMergeStrategy(null, model, model));

    // no assertions here, expectations in mocks
  }

  /**
   * Create an ISR with builtin properties only.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Test
  public void testCreateIsr() {
    //create an empty metamodel and model (db is also empty)
    IteraplanMiLoadTask task = (IteraplanMiLoadTask) loadTaskFactory.create("MASTER");
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    //create an information system object expression
    RStructuredTypeExpression isType = rMetamodel.findStructuredTypeByPersistentName("InformationSystem");
    ObjectExpression isBi22 = isType.create(model);
    RPropertyExpression name = isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    name.set(ElasticValue.one(isBi22), ElasticValue.one(ValueExpression.create("BI # 22")));
    isBi22.setLastModificationTime(new Date());
    isBi22.setLastModificationUser("miTest");
    RNominalEnumerationExpression statusEnum = (RNominalEnumerationExpression) rMetamodel
        .findValueTypeByPersistentName("de.iteratec.iteraplan.model.InformationSystemRelease$TypeOfStatus");
    REnumerationLiteralExpression current = statusEnum.findLiteralByPersistentName("current");
    RPropertyExpression statusProperty = isType.findPropertyByPersistentName("typeOfStatus");
    statusProperty.set(ElasticValue.one(isBi22), ElasticValue.one(ValueExpression.create(current)));

    //build model diff for creation of ISR
    Set<PropertyInit> propInits = Sets.newHashSet();
    for (RPropertyExpression prop : isType.getAllProperties()) {
      ElasticValue<ValueExpression> val = prop.apply(isBi22);
      if (val.isSome()) {
        propInits.add(new PropertyInit(prop, val));
      }
    }
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addCreate(isType, isBi22, propInits, AssignmentChanges.create());

    // expect: find information system by name
    InformationSystemService isServiceMock = EasyMock.createMock(InformationSystemService.class);
    InformationSystem myIs = EasyMock.createMock(InformationSystem.class);
    myIs.setName(EasyMock.anyObject(String.class));
    EasyMock.expect(isServiceMock.findByNames(EasyMock.anyObject(Set.class))).andReturn(new ArrayList<InformationSystem>());

    //expect: save or update information system release
    BuildingBlockService<BuildingBlock, Integer> isrServiceMock = (BuildingBlockService) EasyMock.createMock(InformationSystemReleaseService.class);
    InformationSystemRelease myRelease = EasyMock.createMock(InformationSystemRelease.class);
    myRelease.setVersion(EasyMock.anyObject(String.class));
    myRelease.setTypeOfStatus(TypeOfStatus.CURRENT);

    EasyMock.expect(isServiceMock.saveOrUpdate(EasyMock.anyObject(InformationSystem.class), EasyMock.anyBoolean())).andReturn(myIs);
    EasyMock.expect(isrServiceMock.saveOrUpdate(EasyMock.anyObject(InformationSystemRelease.class), EasyMock.anyBoolean())).andReturn(myRelease);

    //dummy, no expectations
    AllBuildingBlockService allBbServiceMock = EasyMock.createNiceMock(AllBuildingBlockService.class);

    final BuildingBlockServiceLocator bbServiceLocatorMock = EasyMock.createMock(BuildingBlockServiceLocator.class);
    EasyMock.expect(bbServiceLocatorMock.getInformationSystemService()).andReturn(isServiceMock);
    EasyMock.expect(bbServiceLocatorMock.getService(TypeOfBuildingBlock.INFORMATIONSYSTEM)).andReturn((BuildingBlockService) isServiceMock);
    EasyMock.expect(bbServiceLocatorMock.getService(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)).andReturn(isrServiceMock);
    EasyMock.expect(bbServiceLocatorMock.getAllBBService()).andReturn(allBbServiceMock);

    EasyMock.replay(isServiceMock, myIs);
    EasyMock.replay(isrServiceMock, myRelease);
    EasyMock.replay(bbServiceLocatorMock, allBbServiceMock);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, null, bbServiceLocatorMock, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new AdditiveMergeStrategy(writer, model, model));

    // no assertions here, expectations in mocks
  }

  @Test
  public void testCreateIsrWithNumberAT() {
    createAttributeTypesAndValues();
    testCreateIsrWithAttributeType("numberAT", Double.valueOf(10), true);
  }

  @Test
  public void testCreateIsrWithDateAT() {
    createAttributeTypesAndValues();
    testCreateIsrWithAttributeType("dateAT", new Date(), true);
  }

  @Test
  public void testCreateIsrWithTextAT() {
    createAttributeTypesAndValues();
    testCreateIsrWithAttributeType("textAT", "text", true);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void testCreateIsrWithAttributeType(String atName, Object av, boolean expectSetValue) {
    //create an empty metamodel and model (db is also empty)
    IteraplanMiLoadTask task = (IteraplanMiLoadTask) loadTaskFactory.create("MASTER");
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    //create an information system object expression
    RStructuredTypeExpression isType = rMetamodel.findStructuredTypeByPersistentName("InformationSystem");
    ObjectExpression isBi22 = isType.create(model);
    RPropertyExpression name = isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    name.set(ElasticValue.one(isBi22), ElasticValue.one(ValueExpression.create("BI # 22")));
    isBi22.setLastModificationTime(new Date());
    isBi22.setLastModificationUser("miTest");
    RNominalEnumerationExpression statusEnum = (RNominalEnumerationExpression) rMetamodel
        .findValueTypeByPersistentName("de.iteratec.iteraplan.model.InformationSystemRelease$TypeOfStatus");
    REnumerationLiteralExpression current = statusEnum.findLiteralByPersistentName("current");
    RPropertyExpression statusProperty = isType.findPropertyByPersistentName("typeOfStatus");
    statusProperty.set(ElasticValue.one(isBi22), ElasticValue.one(ValueExpression.create(current)));
    RPropertyExpression atProperty = isType.findPropertyByPersistentName(atName);
    if (atProperty.getType() instanceof RNominalEnumerationExpression) {
      REnumerationLiteralExpression literal = ((RNominalEnumerationExpression) atProperty.getType()).findLiteralByPersistentName(((EnumAV) av)
          .getName());
      atProperty.set(ElasticValue.one(isBi22), ElasticValue.one(ValueExpression.create(literal)));
    }
    else {
      atProperty.set(ElasticValue.one(isBi22), ElasticValue.one(ValueExpression.create(av)));
    }

    //build model diff for creation of ISR
    Set<PropertyInit> propInits = Sets.newHashSet();
    for (RPropertyExpression prop : isType.getAllProperties()) {
      ElasticValue<ValueExpression> val = prop.apply(isBi22);
      if (val.isSome()) {
        propInits.add(new PropertyInit(prop, val));
      }
    }
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());
    modelDiff.addCreate(isType, isBi22, propInits, AssignmentChanges.create());

    // expect: find information system by name
    InformationSystemService isServiceMock = EasyMock.createMock(InformationSystemService.class);
    InformationSystem myIs = EasyMock.createMock(InformationSystem.class);
    myIs.setName(EasyMock.anyObject(String.class));
    EasyMock.expect(isServiceMock.findByNames(EasyMock.anyObject(Set.class))).andReturn(new ArrayList<InformationSystem>());

    //expect: save or update information system release
    BuildingBlockService<BuildingBlock, Integer> isrServiceMock = (BuildingBlockService) EasyMock.createMock(InformationSystemReleaseService.class);
    InformationSystemRelease myRelease = EasyMock.createMock(InformationSystemRelease.class);
    myRelease.setVersion(EasyMock.anyObject(String.class));
    myRelease.setTypeOfStatus(TypeOfStatus.CURRENT);

    EasyMock.expect(isServiceMock.saveOrUpdate(EasyMock.anyObject(InformationSystem.class), EasyMock.anyBoolean())).andReturn(myIs);
    EasyMock.expect(isrServiceMock.saveOrUpdate(EasyMock.anyObject(InformationSystemRelease.class), EasyMock.anyBoolean())).andReturn(myRelease);

    //expect: save or update attribute value
    AttributeValueService avService = EasyMock.createMock(AttributeValueService.class);
    EasyMock.expect(avService.saveOrUpdate(EasyMock.anyObject(AttributeValue.class))).andReturn(EasyMock.createMock(AttributeValue.class));
    if (expectSetValue) {
      avService.setValue(EasyMock.anyObject(InformationSystemRelease.class), EasyMock.anyObject(AttributeValue.class),
          EasyMock.anyObject(AttributeType.class));
    }
    else {
      avService.setReferenceValues(EasyMock.anyObject(InformationSystemRelease.class), EasyMock.anyObject(Collection.class),
          EasyMock.anyObject(Integer.class));
    }

    //dummy, no expectations
    AllBuildingBlockService allBbServiceMock = EasyMock.createNiceMock(AllBuildingBlockService.class);

    final BuildingBlockServiceLocator bbServiceLocatorMock = EasyMock.createMock(BuildingBlockServiceLocator.class);
    EasyMock.expect(bbServiceLocatorMock.getInformationSystemService()).andReturn(isServiceMock);
    EasyMock.expect(bbServiceLocatorMock.getService(TypeOfBuildingBlock.INFORMATIONSYSTEM)).andReturn((BuildingBlockService) isServiceMock);
    EasyMock.expect(bbServiceLocatorMock.getService(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)).andReturn(isrServiceMock);
    EasyMock.expect(bbServiceLocatorMock.getAllBBService()).andReturn(allBbServiceMock);

    EasyMock.replay(avService);
    EasyMock.replay(isServiceMock, myIs);
    EasyMock.replay(isrServiceMock, myRelease);
    EasyMock.replay(bbServiceLocatorMock, allBbServiceMock);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, avService, bbServiceLocatorMock, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new AdditiveMergeStrategy(writer, model, model));

    // no assertions here, expectations in mocks
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Test
  public void testCreateIsrWithProject() {
    //create an empty metamodel and model (db is also empty)
    IteraplanMiLoadTask task = (IteraplanMiLoadTask) loadTaskFactory.create("MASTER");
    WMetamodel wMetamodel = task.loadWMetamodel();
    RMetamodel rMetamodel = PojoRMetamodelCopyUtil.rMetamodelFor(wMetamodel);
    Model model = task.loadModel(wMetamodel, rMetamodel);

    //create an information system object expression
    RStructuredTypeExpression isType = rMetamodel.findStructuredTypeByPersistentName("InformationSystem");
    ObjectExpression isBi22 = isType.create(model);
    RPropertyExpression name = isType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME);
    name.set(ElasticValue.one(isBi22), ElasticValue.one(ValueExpression.create("BI # 22")));
    isBi22.setLastModificationTime(new Date());
    isBi22.setLastModificationUser("miTest");
    RNominalEnumerationExpression statusEnum = (RNominalEnumerationExpression) rMetamodel
        .findValueTypeByPersistentName("de.iteratec.iteraplan.model.InformationSystemRelease$TypeOfStatus");
    REnumerationLiteralExpression current = statusEnum.findLiteralByPersistentName("current");
    RPropertyExpression statusProperty = isType.findPropertyByPersistentName("typeOfStatus");
    statusProperty.set(ElasticValue.one(isBi22), ElasticValue.one(ValueExpression.create(current)));

    RStructuredTypeExpression projectType = rMetamodel.findStructuredTypeByPersistentName("Project");
    ObjectExpression proj1 = projectType.create(model);
    projectType.findPropertyByPersistentName(ElasticMiConstants.PERSISTENT_NAME_NAME).set(ElasticValue.one(proj1),
        ElasticValue.one(ValueExpression.create("p1")));
    RRelationshipEndExpression projects = isType.findRelationshipEndByPersistentName("projects");
    projects.connect(model, ElasticValue.one(isBi22), ElasticValue.one(proj1));

    //build model diff for creation of ISR
    SimpleModelDiff modelDiff = new SimpleModelDiff(new SingleRelEndChangeFactory());

    Set<PropertyInit> propInits = Sets.newHashSet();
    for (RPropertyExpression prop : isType.getAllProperties()) {
      ElasticValue<ValueExpression> val = prop.apply(isBi22);
      if (val.isSome()) {
        propInits.add(new PropertyInit(prop, val));
      }
    }
    AssignmentChanges connects = AssignmentChanges.create(projects, ElasticValue.one(proj1));

    modelDiff.addCreate(isType, isBi22, propInits, connects);

    propInits = Sets.newHashSet();
    for (RPropertyExpression prop : projectType.getAllProperties()) {
      ElasticValue<ValueExpression> val = prop.apply(proj1);
      if (val.isSome()) {
        propInits.add(new PropertyInit(prop, val));
      }
    }
    connects = AssignmentChanges.create(projects.getOpposite(), ElasticValue.one(isBi22));
    modelDiff.addCreate(projectType, proj1, propInits, connects);

    // expect: find information system by name
    InformationSystemService isServiceMock = EasyMock.createMock(InformationSystemService.class);
    InformationSystem myIs = EasyMock.createMock(InformationSystem.class);
    myIs.setName(EasyMock.anyObject(String.class));
    EasyMock.expect(isServiceMock.findByNames(EasyMock.anyObject(Set.class))).andReturn(new ArrayList<InformationSystem>());

    //expect: save or update information system release
    BuildingBlockService<BuildingBlock, Integer> isrServiceMock = (BuildingBlockService) EasyMock.createMock(InformationSystemReleaseService.class);
    InformationSystemRelease myRelease = EasyMock.createMock(InformationSystemRelease.class);
    myRelease.setVersion(EasyMock.anyObject(String.class));
    myRelease.setTypeOfStatus(TypeOfStatus.CURRENT);

    //expect: save or update project
    Project virtualElementMock = EasyMock.createNiceMock(Project.class);
    BuildingBlockService<BuildingBlock, Integer> projServiceMock = (BuildingBlockService) EasyMock.createMock(ProjectService.class);
    EasyMock.expect(projServiceMock.getFirstElement()).andReturn(virtualElementMock);
    Project projectMock = EasyMock.createMock(Project.class);

    EasyMock.expect(isServiceMock.saveOrUpdate(EasyMock.anyObject(InformationSystem.class), EasyMock.anyBoolean())).andReturn(myIs);
    EasyMock.expect(isrServiceMock.saveOrUpdate(EasyMock.anyObject(InformationSystemRelease.class), EasyMock.anyBoolean())).andReturn(myRelease);
    EasyMock.expect(projServiceMock.saveOrUpdate(EasyMock.anyObject(Project.class), EasyMock.anyBoolean())).andReturn(projectMock);

    //dummy, no expectations
    AllBuildingBlockService allBbServiceMock = EasyMock.createNiceMock(AllBuildingBlockService.class);

    final BuildingBlockServiceLocator bbServiceLocatorMock = EasyMock.createMock(BuildingBlockServiceLocator.class);
    EasyMock.expect(bbServiceLocatorMock.getInformationSystemService()).andReturn(isServiceMock);
    EasyMock.expect(bbServiceLocatorMock.getService(TypeOfBuildingBlock.INFORMATIONSYSTEM)).andReturn((BuildingBlockService) isServiceMock);
    EasyMock.expect(bbServiceLocatorMock.getService(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE)).andReturn(isrServiceMock);
    EasyMock.expect(bbServiceLocatorMock.getService(TypeOfBuildingBlock.PROJECT)).andReturn(projServiceMock).times(2);
    EasyMock.expect(bbServiceLocatorMock.getAllBBService()).andReturn(allBbServiceMock);

    EasyMock.replay(projectMock, projServiceMock, virtualElementMock);
    EasyMock.replay(isServiceMock, myIs);
    EasyMock.replay(isrServiceMock, myRelease);
    EasyMock.replay(bbServiceLocatorMock, allBbServiceMock);

    // object under test
    MiIteraplanDiffWriter writer = new MiIteraplanDiffWriter(modelDiff, null, bbServiceLocatorMock, task.getMetamodelMapping(),
        task.getInstanceMapping(), new MiImportProcessMessages());

    // action: write differences
    writer.writeDifferences(new AdditiveMergeStrategy(writer, model, model));

    // no assertions here, expectations in mocks
  }

  private Map<AttributeType, List<AttributeValue>> createAttributeTypesAndValues() {
    Map<AttributeType, List<AttributeValue>> attributes = CollectionUtils.hashMap();

    AttributeTypeGroup defaultGroup = testDataHelper2.createAttributeTypeGroup("Default Attribute Type Group",
        "Default Attribute Type Group Description");
    AttributeTypeGroup secondGroup = testDataHelper2.createAttributeTypeGroup("Second Attribute Type Group",
        "Second Attribute Type Group Description");

    // Enum AT
    List<AttributeValue> avList = CollectionUtils.arrayList();
    EnumAT enumAT = testDataHelper2.createEnumAttributeType("enumAT", "enumAT description", Boolean.TRUE, defaultGroup);
    testDataHelper2.assignAttributeTypeToAllAvailableBuildingBlockTypes(enumAT);
    for (int i = 1; i < 6; i++) {
      avList.add(testDataHelper2.createEnumAV("enumAV" + i, "enumAV" + i + "descr", enumAT));
    }
    attributes.put(enumAT, avList);

    // Number AT
    avList = CollectionUtils.arrayList();
    NumberAT numberAT = testDataHelper2.createNumberAttributeType("numberAT", "numberAT description", defaultGroup);
    testDataHelper2.assignAttributeTypeToAllAvailableBuildingBlockTypes(numberAT);
    attributes.put(numberAT, avList);

    // Date AT
    avList = CollectionUtils.arrayList();
    DateAT dateAT = testDataHelper2.createDateAttributeType("dateAT", "dateAT description", defaultGroup);
    testDataHelper2.assignAttributeTypeToAllAvailableBuildingBlockTypes(dateAT);
    attributes.put(dateAT, avList);

    // Text AT
    avList = CollectionUtils.arrayList();
    TextAT textAT = testDataHelper2.createTextAttributeType("textAT", "textAT description", true, secondGroup);
    testDataHelper2.assignAttributeTypeToAllAvailableBuildingBlockTypes(textAT);
    attributes.put(textAT, avList);

    return attributes;
  }

  private static ObjectExpression findByName(RStructuredTypeExpression type, Model model, String name) {
    RPropertyExpression namePropery = type.findPropertyByPersistentName("name");
    if (namePropery == null) {
      return null;
    }
    for (ObjectExpression oe : type.apply(model).getMany()) {
      if (namePropery.apply(oe).getOne().asString().contains(name)) {
        return oe;
      }
    }
    return null;
  }

}
