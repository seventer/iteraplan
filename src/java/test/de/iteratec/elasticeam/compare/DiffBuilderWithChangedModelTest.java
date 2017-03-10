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
package de.iteratec.elasticeam.compare;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.BiMap;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.TestHelper;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.ExcelExportService;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.ExcelExportTestUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMetamodelLoaderImpl;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.ModelLoader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.validator.InterfaceInformationFlowConsistencyViolation;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.validator.IteraplanModelValidator;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationLiteralExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.PropertyExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.InstanceExpression;
import de.iteratec.iteraplan.elasticeam.model.LinkExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.ModelFactory;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilderResult;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffPart;
import de.iteratec.iteraplan.elasticeam.model.compare.LeftSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.merge.ModelMerger;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelConsistencyViolation;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelValidator;
import de.iteratec.iteraplan.elasticeam.model.validator.ModelValidatorResult;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TransportInfo;


@SuppressWarnings("PMD.TooManyMethods")
public class DiffBuilderWithChangedModelTest extends BaseTransactionalTestSupport {

  @Autowired
  private ExcelExportService                      excelExportService;

  @Autowired
  private TestDataHelper2                         testDataHelper;

  @Autowired
  private IteraplanMetamodelLoaderImpl            metamodelLoader;

  @Autowired
  private ModelLoader                             modelLoader;

  @Autowired
  private AttributeValueService                   avService;

  @Autowired
  private BuildingBlockServiceLocator             bbServiceLocator;

  private Metamodel                               metamodel;
  private Model                                   model;

  private IteraplanMapping                        referenceMapping;
  private BiMap<Object, UniversalModelExpression> instanceMapping;

  //Needed for the creation of example data
  private static final String                     DESCRIPTION_SUFFIX                = " description";
  private static final String                     STANDARD_LEFT_IS_NAME             = "IS_1";
  private static final String                     STANDARD_LEFT_ISR_RELEASE_NUMBER  = "1";
  private static final String                     STANDARD_RIGHT_IS_NAME            = "IS_2";
  private static final String                     STANDARD_RIGHT_ISR_RELEASE_NUMBER = "2";

  @Before
  public void init() {
    // Hint: There is a beginTransaction() statement in the setUp-Method of the base class. So we have a valid transaction here.

    UserContext.setCurrentUserContext(testDataHelper.createUserContext());
    referenceMapping = metamodelLoader.loadConceptualMetamodelMapping();
    metamodel = referenceMapping.getMetamodel();
  }

  @After
  public void tearDown() {
    commit();
  }

  @Test
  public void testChangeInformationFlowDirectionNamedIFWithBO() throws IOException {
    createTestDataNamedIFWithBO();
    testChangeDirectionWithBO();
  }

  @Test
  public void testChangeInformationFlowDirectionNamedIFWithoutBO() throws IOException {
    createTestDataAnonymousIFWithoutBO();
    testChangeDirectionWithoutBO();
  }

  @Test
  public void testChangeInformationFlowDirectionAnonymousIFWithBO() throws IOException {
    createTestDataNamedIFWithBO();
    testChangeDirectionWithBO();
  }

  @Test
  public void testChangeInformationFlowDirectionAnonymousIFWithoutBO() throws IOException {
    createTestDataAnonymousIFWithoutBO();
    testChangeDirectionWithoutBO();
  }

  @Test
  public void testChangeInterfaceNamedToAnonymousWithBO() throws IOException {
    createTestDataNamedIFWithBO();
    testChangeIFName(true);
  }

  @Test
  public void testChangeInterfaceNamedToAnonymousWithoutBO() throws IOException {
    createTestDataNamedIFWithoutBO();
    testChangeIFName(true);
  }

  @Test
  public void testChangeInterfaceAnonymousToNamedWithBO() throws IOException {
    createTestDataAnonymousIFWithBO();
    testChangeIFName(false);
  }

  @Test
  public void testChangeInterfaceAnonymousToNamedWithoutBO() throws IOException {
    createTestDataAnonymousIFWithoutBO();
    testChangeIFName(false);
  }

  @Test
  public void testChangeNamedIFAddBOToInformationFlow() throws IOException {
    createTestDataNamedIFWithoutBO();
    createUnconnectedBO();
    testAddBO();
  }

  @Test
  public void testChangeAnonymousIFAddBOToInformationFlow() throws IOException {
    createTestDataAnonymousIFWithoutBO();
    createUnconnectedBO();
    testAddBO();
  }

  @Test
  public void testChangeConnectedBOFromNamedIF() throws IOException {
    createTestDataNamedIFWithBO();
    createUnconnectedBO();
    testChangeBO();
  }

  @Test
  public void testChangeConnectedBOFromAnonymousIF() throws IOException {
    createTestDataAnonymousIFWithBO();
    createUnconnectedBO();
    testChangeBO();
  }

  @Test
  public void testChangeNamedIFAddNewBOToInformationFlow() throws IOException {
    createTestDataNamedIFWithoutBO();
    testAddNewBO();
  }

  @Test
  public void testChangeHierarchicalElementToRoot() throws IOException {
    createTestDataHierarchicalElements();
    testChangeHierarchy();
  }

  @Test
  public void testAddNewInformationFlowToInterface() throws IOException {
    createTestDataNamedIFWithoutBO();
    testAddNewInformationFlow();
  }

  /**
   * @throws IOException  
   */
  private void testAddNewInformationFlow() throws IOException {
    Model diffModel = setupStandardAndDiffModel();

    // get the previously created interface
    InstanceExpression interfaceInstanceExpression = getFirstInterface(diffModel);

    // get the corresponding information flow
    LinkExpression infoFlowCreatedInStaticModel = getFirstConnectedInfoFlow(interfaceInstanceExpression);

    // get the left connected information system 
    InstanceExpression leftConnectedInformationSystem = (InstanceExpression) infoFlowCreatedInStaticModel
        .getConnected(getRelationshipEndByPersistentName(getInformationFlowType(), "informationSystemRelease1"));

    // Steps to create a new InformationFlow in the elasticModel
    LinkExpression infoFlowCreatedInElasticModel = diffModel.create(getInformationFlowType());
    PropertyExpression<?> directionProperty = getPropertyByPersistentName(getInformationFlowType(), "direction");
    changeDirection(infoFlowCreatedInElasticModel, directionProperty, Direction.BOTH_DIRECTIONS);

    // Steps to create a new InformationSystem in the elastic model
    InstanceExpression informationSystemCreatedInElasticModel = diffModel.create(getInformationSystemType());
    PropertyExpression<?> property = getPropertyByPersistentName(getInformationSystemType(), "name");
    informationSystemCreatedInElasticModel.setValue(property, "IS_3 # 3");
    property = getPropertyByPersistentName(getInformationSystemType(), "typeOfStatus");
    changeTypeOfStatus(informationSystemCreatedInElasticModel, property, TypeOfStatus.CURRENT);

    // linking the newly created information flow to the interface
    RelationshipEndExpression informationFlowInterfaceRelationshipEnd = getRelationshipEndByPersistentName(getInformationFlowType(),
        "informationSystemInterface");
    infoFlowCreatedInElasticModel.connect(informationFlowInterfaceRelationshipEnd, interfaceInstanceExpression);

    // linking the left connected information system and the newly created information system to the information flow
    infoFlowCreatedInElasticModel.connect(getRelationshipEndByPersistentName(getInformationFlowType(), "informationSystemRelease1"),
        leftConnectedInformationSystem);
    infoFlowCreatedInElasticModel.connect(getRelationshipEndByPersistentName(getInformationFlowType(), "informationSystemRelease2"),
        informationSystemCreatedInElasticModel);

    DiffBuilderResult diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    // remove the comment from the next line to see the diffModel in an exported Excel file in the %TEMP% directory
    writeModelToExcelFile(diffModel, "exceldata_testChangedModel");

    // dry run
    ModelMerger modelMerger = new ModelMerger(model, metamodel, diffResult);
    modelMerger.writeDifferences();

    ModelValidatorResult validatorResult = validateModel(model);

    Set<ModelConsistencyViolation> violations = validatorResult.getViolations();
    boolean foundViolation = false;
    for (ModelConsistencyViolation violation : violations) {
      if (violation instanceof InterfaceInformationFlowConsistencyViolation) {
        foundViolation = true;
        break;
      }
    }

    assertTrue(foundViolation);
  }

  /**
   * @throws IOException  
   */
  private void testAddNewBO() throws IOException {

    Model diffModel = setupStandardAndDiffModel();

    // Steps to create a new BusinessObject in the elasticModel and set the name
    InstanceExpression boCreatedInElasticModel = diffModel.create(getBusinessObjectType());
    PropertyExpression<?> nameProperty = getPropertyByPersistentName(getBusinessObjectType(), "name");
    boCreatedInElasticModel.setValue(nameProperty, "NEW ELASTIC BO_1");

    InstanceExpression interfaceInstanceExpression = getFirstInterface(diffModel);
    LinkExpression infoFlow = getFirstConnectedInfoFlow(interfaceInstanceExpression);

    RelationshipEndExpression informationFlowBusinessObjectRelationshipEnd = getRelationshipEndByPersistentName(getInformationFlowType(),
        "businessObject");

    infoFlow.connect(informationFlowBusinessObjectRelationshipEnd, boCreatedInElasticModel);

    // remove the comment from the next line to see the diffModel in an exported Excel file in the %TEMP% directory
    // writeModelToExcelFile(diffModel, "exceldata_testChangedModel");

    DiffBuilderResult diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    assertTrue(checkExistenceRightDiff(diffResult, getBusinessObjectType(), boCreatedInElasticModel));
    assertTrue(checkExistenceTwoSideDiff(diffResult, getInformationFlowType(), informationFlowBusinessObjectRelationshipEnd));
    assertTrue(checkExpectedDiffCount(diffResult, 0, 1, 1));

    writeChangesToDatabase(diffResult);

    reloadStandardModel();

    diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    RelationshipEndExpression informationSystemInformationFlowRelationshipEnd1 = getRelationshipEndByPersistentName(getInformationSystemType(),
        "informationFlows1");
    RelationshipEndExpression informationSystemInformationFlowRelationshipEnd2 = getRelationshipEndByPersistentName(getInformationSystemType(),
        "informationFlows2");
    RelationshipEndExpression informationSystemInterfaceInformationFlowRelationshipEnd = getRelationshipEndByPersistentName(
        getInformationSystemInterfaceType(), "informationFlows");

    assertTrue(checkExistenceTwoSideDiff(diffResult, getInformationSystemType(), informationSystemInformationFlowRelationshipEnd1));
    assertTrue(checkExistenceTwoSideDiff(diffResult, getInformationSystemType(), informationSystemInformationFlowRelationshipEnd2));
    assertTrue(checkExistenceTwoSideDiff(diffResult, getInformationSystemInterfaceType(), informationSystemInterfaceInformationFlowRelationshipEnd));
    assertTrue(checkExistenceRightDiff(diffResult, getBusinessObjectType(), boCreatedInElasticModel));
    assertTrue(checkExistenceRightDiff(diffResult, getInformationFlowType(), infoFlow));

    assertTrue(checkExpectedDiffCount(diffResult, 0, 2, 3));
  }

  /**
   * @throws IOException
   */
  private void testChangeBO() throws IOException {

    Model diffModel = setupStandardAndDiffModel();

    InstanceExpression interfaceInstanceExpression = getFirstInterface(diffModel);
    LinkExpression infoFlow = getFirstConnectedInfoFlow(interfaceInstanceExpression);

    RelationshipEndExpression informationFlowBusinessObjectRelationshipEnd = getRelationshipEndByPersistentName(getInformationFlowType(),
        "businessObject");
    RelationshipEndExpression businessObjectInformationFlowRelationshipEnd = getRelationshipEndByPersistentName(getBusinessObjectType(),
        "informationFlows");

    InstanceExpression connectedBO = (InstanceExpression) infoFlow.getConnected(informationFlowBusinessObjectRelationshipEnd);

    Collection<InstanceExpression> allBO = diffModel.findAll(getBusinessObjectType());

    InstanceExpression toBeConnectedBO = null;

    for (InstanceExpression in : allBO) {
      if (connectedBO.equals(in)) {
        continue;
      }
      else {
        toBeConnectedBO = in;
        break;
      }
    }

    assertNotNull(toBeConnectedBO);

    infoFlow.connect(informationFlowBusinessObjectRelationshipEnd, toBeConnectedBO);

    // remove the comment from the next line to see the diffModel in an exported Excel file in the %TEMP% directory
    // writeModelToExcelFile(diffModel, "exceldata_testChangedModel");

    DiffBuilderResult diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    assertTrue(checkExistenceTwoSideDiff(diffResult, getBusinessObjectType(), businessObjectInformationFlowRelationshipEnd));
    assertTrue(checkExistenceTwoSideDiff(diffResult, getInformationFlowType(), informationFlowBusinessObjectRelationshipEnd));
    assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 3));

    writeChangesToDatabase(diffResult);

    reloadStandardModel();

    diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 0));
  }

  /**
   * @throws IOException
   */
  private void testAddBO() throws IOException {

    Model diffModel = setupStandardAndDiffModel();

    InstanceExpression interfaceInstanceExpression = getFirstInterface(diffModel);
    LinkExpression infoFlow = getFirstConnectedInfoFlow(interfaceInstanceExpression);

    InstanceExpression businessObject = diffModel.findAll(getBusinessObjectType()).iterator().next();

    RelationshipEndExpression informationFlowBusinessObjectRelationshipEnd = getRelationshipEndByPersistentName(getInformationFlowType(),
        "businessObject");
    RelationshipEndExpression businessObjectInformationFlowRelationshipEnd = getRelationshipEndByPersistentName(getBusinessObjectType(),
        "informationFlows");

    infoFlow.connect(informationFlowBusinessObjectRelationshipEnd, businessObject);

    // remove the comment from the next line to see the diffModel in an exported Excel file in the %TEMP% directory
    // writeModelToExcelFile(diffModel, "exceldata_testChangedModel");

    DiffBuilderResult diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    assertTrue(checkExistenceTwoSideDiff(diffResult, getInformationFlowType(), informationFlowBusinessObjectRelationshipEnd));
    assertTrue(checkExistenceTwoSideDiff(diffResult, getBusinessObjectType(), businessObjectInformationFlowRelationshipEnd));
    assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 2));

    writeChangesToDatabase(diffResult);

    reloadStandardModel();

    diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    RelationshipEndExpression informationSystemInformationFlowRelationshipEnd1 = getRelationshipEndByPersistentName(getInformationSystemType(),
        "informationFlows1");
    RelationshipEndExpression informationSystemInformationFlowRelationshipEnd2 = getRelationshipEndByPersistentName(getInformationSystemType(),
        "informationFlows2");
    RelationshipEndExpression informationSystemInterfaceInformationFlowRelationshipEnd = getRelationshipEndByPersistentName(
        getInformationSystemInterfaceType(), "informationFlows");

    assertTrue(checkExistenceTwoSideDiff(diffResult, getInformationSystemType(), informationSystemInformationFlowRelationshipEnd1));
    assertTrue(checkExistenceTwoSideDiff(diffResult, getInformationSystemType(), informationSystemInformationFlowRelationshipEnd2));
    assertTrue(checkExistenceTwoSideDiff(diffResult, getInformationSystemInterfaceType(), informationSystemInterfaceInformationFlowRelationshipEnd));
    assertTrue(checkExistenceTwoSideDiff(diffResult, getBusinessObjectType(), businessObjectInformationFlowRelationshipEnd));
    assertTrue(checkExistenceRightDiff(diffResult, getInformationFlowType(), infoFlow));
    assertTrue(checkExpectedDiffCount(diffResult, 0, 1, 4));

  }

  /**
   * @throws IOException
   */
  private void testChangeIFName(boolean setToAnonymous) throws IOException {
    Model diffModel = setupStandardAndDiffModel();

    InstanceExpression interfaceInstanceExpression = getFirstInterface(diffModel);

    SubstantialTypeExpression interfaceTypeExpression = getInformationSystemInterfaceType();
    PropertyExpression<?> nameProperty = getPropertyByPersistentName(interfaceTypeExpression, "name");

    String currentName = (String) interfaceInstanceExpression.getValue(nameProperty);
    String newName;
    if (setToAnonymous) {
      newName = currentName.substring(currentName.lastIndexOf('['));
    }
    else {
      PropertyExpression<?> idProperty = getPropertyByPersistentName(interfaceTypeExpression, "id");
      newName = "INTERFACE_NAME[" + STANDARD_LEFT_IS_NAME + " # " + STANDARD_LEFT_ISR_RELEASE_NUMBER + "," + STANDARD_RIGHT_IS_NAME + " # "
          + STANDARD_RIGHT_ISR_RELEASE_NUMBER + "," + interfaceInstanceExpression.getValue(idProperty) + "]";
    }
    interfaceInstanceExpression.setValue(nameProperty, newName);

    // remove the comment from the next line to see the diffModel in an exported Excel file in the %TEMP% directory
    // writeModelToExcelFile(diffModel, "exceldata_testChangedModel");

    DiffBuilderResult diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    assertTrue(checkExistenceTwoSideDiff(diffResult, interfaceTypeExpression, nameProperty));
    assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 1));

    writeChangesToDatabase(diffResult);

    reloadStandardModel();

    diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 0));
  }

  /**
   * @throws IOException  
   */
  private void testChangeDirectionWithBO() throws IOException {
    Model diffModel = setupStandardAndDiffModel();

    InstanceExpression interfaceInstanceExpression = getFirstInterface(diffModel);
    LinkExpression infoFlow = getFirstConnectedInfoFlow(interfaceInstanceExpression);

    RelationshipTypeExpression infoFlowTypeExpression = getInformationFlowType();
    PropertyExpression<?> directionProperty = getPropertyByPersistentName(infoFlowTypeExpression, "direction");

    changeDirection(infoFlow, directionProperty, Direction.BOTH_DIRECTIONS);

    // remove the comment from the next line to see the diffModel in an exported Excel file in the %TEMP% directory
    // writeModelToExcelFile(diffModel, "exceldata_testChangedModel");

    DiffBuilderResult diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    assertTrue(checkExistenceTwoSideDiff(diffResult, infoFlowTypeExpression, directionProperty));
    assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 1));

    writeChangesToDatabase(diffResult);

    reloadStandardModel();

    diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 0));
  }

  /**
   * @throws IOException  
   */
  private void testChangeDirectionWithoutBO() throws IOException {

    Model diffModel = setupStandardAndDiffModel();

    InstanceExpression interfaceInstanceExpression = getFirstInterface(diffModel);
    LinkExpression infoFlow = getFirstConnectedInfoFlow(interfaceInstanceExpression);

    RelationshipTypeExpression infoFlowTypeExpression = getInformationFlowType();
    PropertyExpression<?> directionProperty = getPropertyByPersistentName(infoFlowTypeExpression, "direction");

    changeDirection(infoFlow, directionProperty, Direction.BOTH_DIRECTIONS);

    // remove the comment from the next line to see the diffModel in an exported Excel file in the %TEMP% directory
    // writeModelToExcelFile(diffModel, "exceldata_testChangedModel");

    DiffBuilderResult diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    assertTrue(checkExistenceTwoSideDiff(diffResult, infoFlowTypeExpression, directionProperty));
    assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 1));

    writeChangesToDatabase(diffResult);

    reloadStandardModel();

    diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    PropertyExpression<?> interfaceDirectionProperty = getPropertyByPersistentName(getInformationSystemInterfaceType(), "interfaceDirection");

    assertTrue(checkExistenceTwoSideDiff(diffResult, getInformationSystemInterfaceType(), interfaceDirectionProperty));
    assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 1));
  }

  private void testChangeHierarchy() {
    Model diffModel = setupStandardAndDiffModel();

    Collection<InstanceExpression> businessDomains = diffModel.findAll(getBusinessDomainType());

    RelationshipEndExpression parentExpression = getRelationshipEndByPersistentName(getBusinessDomainType(), "parent");

    for (InstanceExpression businessDomain : businessDomains) {
      if ("child".equals(businessDomain.getValue(MixinTypeNamed.NAME_PROPERTY))) {
        businessDomain.disconnect(parentExpression, businessDomain.getConnected(parentExpression));
      }
    }

    DiffBuilderResult diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);

    Assert.assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 2));
    Assert.assertTrue(checkExistenceTwoSideDiff(diffResult, getBusinessDomainType(), parentExpression));

    writeChangesToDatabase(diffResult);

    reloadStandardModel();

    diffResult = TestHelper.getDiffBuilderResults(metamodel, model, diffModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    Assert.assertTrue(checkExpectedDiffCount(diffResult, 0, 0, 0));
  }

  /**
   * @param modelToExport - the model to be exported
   * @param excelFileName - the desired name of the Excel file
   * @throws IOException 
   */
  @SuppressWarnings("unused")
  private void writeModelToExcelFile(Model modelToExport, String excelFileName) throws IOException {
    WorkbookContext context = excelExportService.exportExcel2007(modelToExport, metamodel);
    File tempFile = File.createTempFile(excelFileName, "xlsx");
    ExcelExportTestUtils.persistWorkbook(context.getWb(), tempFile);
  }

  /**
   * 
   */
  private void reloadStandardModel() {
    model = getNewModel();
    instanceMapping = fillModel(model);
  }

  /**
   * @return Model to be manipulated
   */
  private Model setupStandardAndDiffModel() {
    model = getNewModel();
    instanceMapping = fillModel(model);
    // create the model to manipulate
    Model diffModel = getNewModel();
    fillModel(diffModel);

    return diffModel;
  }

  /**
   * @param LinkExpression informationFlow InformationFlow to be changed
   * @param PropertyExpression Property to be changed
   * @param Direction newDirection Direction to be set
   */
  private void changeDirection(LinkExpression informationFlow, PropertyExpression<?> directionProperty, Direction newDirection) {
    EnumerationExpression directionEnum = (EnumerationExpression) directionProperty.getType();
    EnumerationLiteralExpression directionLiteral = directionEnum.findLiteralByPersistentName(newDirection.name());
    informationFlow.setValue(directionProperty, directionLiteral);
  }

  /**
   * @param LinkExpression informationFlow InformationFlow to be changed
   * @param PropertyExpression Property to be changed
   * @param typeOfStatus new status to be set
   */
  private void changeTypeOfStatus(InstanceExpression informationSystem, PropertyExpression<?> typeOfStatusProperty, TypeOfStatus newStatus) {
    EnumerationExpression typeOfStatusEnum = (EnumerationExpression) typeOfStatusProperty.getType();
    EnumerationLiteralExpression typeOfStatusLiteral = typeOfStatusEnum.findLiteralByPersistentName(newStatus.name());
    informationSystem.setValue(typeOfStatusProperty, typeOfStatusLiteral);
  }

  /**
   * @param typeExpression
   * @param persistentName
   * @return PropertyExpression with the given name of the given typeExpression 
   */
  private PropertyExpression<?> getPropertyByPersistentName(UniversalTypeExpression typeExpression, String persistentName) {
    return typeExpression.findPropertyByPersistentName(persistentName);
  }

  /**
   * @param typeExpression
   * @param persistentName
   * @return RelationshipEndExpression with the given name of the given typeExpression 
   */
  private RelationshipEndExpression getRelationshipEndByPersistentName(UniversalTypeExpression typeExpression, String persistentName) {
    return typeExpression.findRelationshipEndByPersistentName(persistentName);
  }

  /**
   * @return RelationshipTypeExpression of InformationFlow
   */
  private RelationshipTypeExpression getInformationFlowType() {
    return (RelationshipTypeExpression) this.metamodel.findTypeByPersistentName("InformationFlow");
  }

  /**
   * @return SubstantialTypeExpression of Interface
   */
  private SubstantialTypeExpression getInformationSystemInterfaceType() {
    return (SubstantialTypeExpression) this.metamodel.findTypeByPersistentName("InformationSystemInterface");
  }

  /**
   * @return SubstantialTypeExpression of BusinessObject
   */
  private SubstantialTypeExpression getBusinessObjectType() {
    return (SubstantialTypeExpression) this.metamodel.findTypeByPersistentName("BusinessObject");
  }

  /**
   * @return SubstantialTypeExpression of InformationSystem
   */
  private SubstantialTypeExpression getInformationSystemType() {
    return (SubstantialTypeExpression) this.metamodel.findTypeByPersistentName("InformationSystem");
  }

  /**
   * @return SubstantialTypeExpression of BusinessDomain
   */
  private SubstantialTypeExpression getBusinessDomainType() {
    return (SubstantialTypeExpression) this.metamodel.findTypeByPersistentName("BusinessDomain");
  }

  /**
   * @param interfaceInstanceExpression
   * @return LinkExpression - the first InformationFlow connected to the given InstanceExpression (of the type InformationSystemInterface)
   */
  private LinkExpression getFirstConnectedInfoFlow(InstanceExpression interfaceInstanceExpression) {
    SubstantialTypeExpression interfaceTypeExpression = (SubstantialTypeExpression) this.metamodel
        .findTypeByPersistentName("InformationSystemInterface");

    Collection<UniversalModelExpression> infoFlows = interfaceInstanceExpression.getConnecteds(interfaceTypeExpression
        .findRelationshipEndByPersistentName("informationFlows"));
    return (LinkExpression) infoFlows.iterator().next();
  }

  /**
   * @param modelInUse
   * @return InstanceExpression - the first InformationSystemInterface found in the model
   */
  private InstanceExpression getFirstInterface(Model modelInUse) {
    SubstantialTypeExpression interfaceTypeExpression = (SubstantialTypeExpression) this.metamodel
        .findTypeByPersistentName("InformationSystemInterface");

    Collection<InstanceExpression> interfaces = modelInUse.findAll(interfaceTypeExpression);
    return interfaces.iterator().next();
  }

  /**
   * @return Model
   */
  private Model getNewModel() {
    return ModelFactory.INSTANCE.createModel(metamodel);
  }

  /**
   * @param modelToFilled
   * @return BiMap<Object, UniversalModelExpression>
   */
  private BiMap<Object, UniversalModelExpression> fillModel(Model modelToBeFilled) {
    return modelLoader.load(modelToBeFilled, referenceMapping);
  }

  /**
   * @param diffResult
   * @param expectedLeftSideDiffs
   * @param expectedRightSideDiffs
   * @param expectedTwoSideDiffs
   */
  private boolean checkExpectedDiffCount(DiffBuilderResult diffResult, int expectedLeftSideDiffs, int expectedRightSideDiffs, int expectedTwoSideDiffs) {
    int leftSideDiffCount = 0;
    int rightSideDiffCount = 0;
    int twoSideDiffCount = 0;

    for (UniversalTypeExpression type : metamodel.getUniversalTypes()) {
      for (BaseDiff bDiff : diffResult.getDiffsByType(type)) {

        if (LeftSidedDiff.class.isInstance(bDiff)) {
          leftSideDiffCount++;
        }
        else if (RightSidedDiff.class.isInstance(bDiff)) {
          rightSideDiffCount++;
        }
        else {
          twoSideDiffCount++;
        }
      }
    }
    return (leftSideDiffCount == expectedLeftSideDiffs && rightSideDiffCount == expectedRightSideDiffs && twoSideDiffCount == expectedTwoSideDiffs);
  }

  /**
    * @param diffResult
    * @param univTypeExpression
    * @param universalModelExpression
    * @return true, if the result includes a difference in a given modelExpression of a given type. else false.
    */
  private boolean checkExistenceRightDiff(DiffBuilderResult diffResult, UniversalTypeExpression univTypeExpression,
                                          UniversalModelExpression universalModelExpression) {
    Set<RightSidedDiff> rsdSet = diffResult.getRightSidedDiffsByType(univTypeExpression);
    for (RightSidedDiff rsd : rsdSet) {
      if (universalModelExpression.equals(rsd.getExpression())) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param diffResult
   * @param univTypeExpression
   * @param feature
   * @param expectedLeftValue
   * @param expectedRightValue
   * @return true, if the result includes a difference in the given relation type on a property with the given name. else false.
   */
  private boolean checkExistenceTwoSideDiff(DiffBuilderResult diffResult, UniversalTypeExpression univTypeExpression, FeatureExpression<?> feature) {
    Set<TwoSidedDiff> tsdSet = diffResult.getTwoSidedDiffsByType(univTypeExpression);
    for (TwoSidedDiff tsd : tsdSet) {
      for (DiffPart part : tsd.getDiffParts()) {
        if (part.getFeature().equals(feature)) {
          return true;
        }
      }
    }
    return false;
  }

  protected Object getInstanceForExpression(UniversalModelExpression modelExpression) {
    return instanceMapping.inverse().get(modelExpression);
  }

  protected final Object checkValue(Object value) {
    if (value instanceof BigInteger) {
      return Integer.valueOf(((BigInteger) value).intValue());
    }
    else if (value instanceof EnumerationLiteralExpression) {
      return referenceMapping.getEnumerationLiterals().get(value);
    }
    return value;
  }

  private void createTestDataNamedIFWithBO() {
    InformationSystemInterface isi = createInformationSystemInterface();

    String isiName = "NAMED IF 1 WITH BO";
    String boDescription = isiName;
    createTransportWithNewBO("BO_1", boDescription, Direction.SECOND_TO_FIRST, isi);
    isi.setName(isiName);
  }

  private void createTestDataNamedIFWithoutBO() {
    InformationSystemInterface isi = createInformationSystemInterface();

    String isiName = "NAMED IF 1 WITHOUT BO";
    isi.setName(isiName);
  }

  private void createTestDataAnonymousIFWithBO() {
    InformationSystemInterface isi = createInformationSystemInterface();

    String isiName = "";
    String boDescription = "ANONYMOUS IF (ID: " + isi.getId().toString() + ") WITH BO";
    createTransportWithNewBO("BO_1", boDescription, Direction.SECOND_TO_FIRST, isi);
    isi.setName(isiName);
  }

  private void createTestDataAnonymousIFWithoutBO() {
    InformationSystemInterface isi = createInformationSystemInterface();

    String isiName = "";
    isi.setName(isiName);
  }

  private void createTestDataHierarchicalElements() {
    BusinessDomain bdParent = testDataHelper.createBusinessDomain("parent", "");
    BusinessDomain bdChild = testDataHelper.createBusinessDomain("child", "");

    bdChild.addParent(bdParent);
  }

  /**
   * @return InformationSystemInterface
   */
  private InformationSystemInterface createInformationSystemInterface() {
    InformationSystem is1 = testDataHelper.createInformationSystem(STANDARD_LEFT_IS_NAME);
    InformationSystemRelease isr1 = testDataHelper.createInformationSystemRelease(is1, STANDARD_LEFT_ISR_RELEASE_NUMBER);

    InformationSystem is2 = testDataHelper.createInformationSystem(STANDARD_RIGHT_IS_NAME);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is2, STANDARD_RIGHT_ISR_RELEASE_NUMBER);

    String desc = "ISI " + isr1.getId() + "-" + isr2.getId() + DESCRIPTION_SUFFIX;

    InformationSystemInterface isi = testDataHelper.createInformationSystemInterface(isr1, isr2, null, desc);

    isi.setDirection(TransportInfo.FIRST_TO_SECOND.getTextRepresentation());

    return isi;
  }

  /**
   * @param boName The Name of the BusinessObject to be created
   * @param boDescription The Description of the BusinessObject to be created
   * @param transportDirection The direction in which the BusinessObject is transported through the interface 
   * @param isi The Interface the BusinessObject is transported through
   * @return BusinessObject the newly created BusinessObject
   */
  private BusinessObject createTransportWithNewBO(String boName, String boDescription, Direction transportDirection, InformationSystemInterface isi) {
    BusinessObject bo = testDataHelper.createBusinessObject(boName, boDescription);
    testDataHelper.createTransport(bo, isi, transportDirection);
    return bo;
  }

  /**
   * Create a new BusinessObject with a randomized name
   */
  private void createUnconnectedBO() {
    String boName = "INITIAL UNCONNECTED BO_" + Double.toString(Math.ceil(Math.random() * 100));
    testDataHelper.createBusinessObject(boName, "This BO was created unconnected.");
  }

  /**
   * @param diffResult
   */
  private void writeChangesToDatabase(DiffBuilderResult diffResult) {
    TestHelper.writeChangesToDatabase(diffResult, referenceMapping, instanceMapping, avService, bbServiceLocator);

  }

  protected ModelValidatorResult validateModel(Model modelToValidate) {
    ModelValidator validator = new IteraplanModelValidator(metamodel);
    validator.setIgnoreNullId(true);
    validator.setIgnoreIdUniqueness(true);
    validator.setCheckInterfaceConsistency(true);
    validator.setIgnoreLowerCardinalityBound(true);

    return validator.validate(modelToValidate);
  }

}
