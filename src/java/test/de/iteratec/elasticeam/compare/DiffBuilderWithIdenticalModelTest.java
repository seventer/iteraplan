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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.excelimport.ExcelImporter;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.ExcelExportService;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMetamodelLoaderImpl;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.ModelLoader;
import de.iteratec.iteraplan.businesslogic.service.InformationSystemReleaseService;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.util.CollectionUtils;
import de.iteratec.iteraplan.datacreator.TestDataHelper2;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.ModelFactory;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.BaseDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilder;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilderResult;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffPart;
import de.iteratec.iteraplan.elasticeam.model.compare.LeftSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.MatchResult;
import de.iteratec.iteraplan.elasticeam.model.compare.Matcher;
import de.iteratec.iteraplan.elasticeam.model.compare.RightSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.TwoSidedDiff;
import de.iteratec.iteraplan.elasticeam.model.compare.impl.DiffBuilderImpl;
import de.iteratec.iteraplan.elasticeam.model.compare.impl.MatcherImpl;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TransportInfo;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeValue;
import de.iteratec.iteraplan.model.attribute.DateAT;
import de.iteratec.iteraplan.model.attribute.EnumAT;
import de.iteratec.iteraplan.model.attribute.NumberAT;
import de.iteratec.iteraplan.model.attribute.ResponsibilityAT;
import de.iteratec.iteraplan.model.attribute.TextAT;


@RunWith(SpringJUnit4ClassRunner.class)
@IfProfileValue(name = "junit.build", value = "nightly.only")
public class DiffBuilderWithIdenticalModelTest extends BaseTransactionalTestSupport {

  @Autowired
  private ExcelExportService              excelExportService;

  @Autowired
  private TestDataHelper2                 testDataHelper;

  @Autowired
  private InformationSystemReleaseService informationSystemReleaseService;

  @Autowired
  private IteraplanMetamodelLoaderImpl    metamodelLoader;

  @Autowired
  private ModelLoader                     modelLoader;

  private Metamodel                       metamodel;
  private Model                           model;

  //Needed for the creation of example data
  private static final String             DESCRIPTION_SUFFIX = " description";
  private List<InformationSystemRelease>  originalIsrList    = CollectionUtils.arrayList();

  @Before
  public void init() {
    UserContext.setCurrentUserContext(testDataHelper.createUserContext());

    createExampleData();

    beginTransaction();

    IteraplanMapping referenceMapping = metamodelLoader.loadConceptualMetamodelMapping();
    metamodel = referenceMapping.getMetamodel();
    model = ModelFactory.INSTANCE.createModel(referenceMapping.getMetamodel());
    BiMap<Object, UniversalModelExpression> instanceMapping = modelLoader.load(model, referenceMapping);

    commit();

    assertNotNull(instanceMapping);
    assertFalse(instanceMapping.isEmpty());
  }

  /**
   * @throws IOException  
   */
  @Test
  public void testWorkbookRoundtrip() throws IOException {
    WorkbookContext context = excelExportService.exportExcel2007(model, metamodel);

    // uncomment the next two lines to see the generated Excel file in the %TEMP% directory
    // File tempFile = File.createTempFile("exceldata_testWorkbookRoundtrip", "xlsx");
    // ExcelExportTestUtils.persistWorkbook(context.getWb(), tempFile);

    ExcelImporter excelImporter = new ExcelImporter(context.getWb());
    excelImporter.importExcel();

    Model roundtripModel = excelImporter.getModel();

    Matcher modelMatcher = new MatcherImpl(metamodel, MatcherImpl.IDCOMPARATOR);
    MatchResult matchResult = modelMatcher.match(model, roundtripModel);

    DiffBuilder diffBuilder = new DiffBuilderImpl(matchResult);
    DiffBuilderResult diffResult = diffBuilder.computeDifferences();

    boolean differenceDetected = false;

    for (UniversalTypeExpression type : metamodel.getUniversalTypes()) {
      System.out.println("======== " + type.getPersistentName() + " =======");
      for (BaseDiff bDiff : diffResult.getDiffsByType(type)) {
        differenceDetected = true;

        if (LeftSidedDiff.class.isInstance(bDiff)) {
          System.out.println("LSD: " + ((LeftSidedDiff) bDiff).getExpression());
        }
        else if (RightSidedDiff.class.isInstance(bDiff)) {
          System.out.println("RSD: " + ((RightSidedDiff) bDiff).getExpression());
        }
        else {
          TwoSidedDiff tsd = (TwoSidedDiff) bDiff;
          System.out.println("TSD. Left side: " + tsd.getLeftExpression() + " ; Right side: " + tsd.getRightExpression());
          for (DiffPart part : tsd.getDiffParts()) {
            System.out.println("-- " + part.getFeature().getPersistentName() + " left: " + part.getLeftValue() + " ; right: " + part.getRightValue());
          }
        }
      }
    }
    assertFalse(differenceDetected);
  }

  private void createExampleData() {
    int indexOfNamedIF = 1;
    int indexOfNamedIFWithBO = 2;
    int indexOfAnonymousIFWithBO = 3;

    originalIsrList.clear();

    Map<AttributeType, List<AttributeValue>> attributes = createAttributeTypesAndValues();

    for (int i = 1; i <= 10; i++) {
      originalIsrList.addAll(createFilledISRs(i, attributes));
    }

    BusinessFunction bf = testDataHelper.createBusinessFunction("testBF", "testBF description");
    Set<BusinessFunction> bfSet = new HashSet<BusinessFunction>();
    bfSet.add(bf);
    InformationSystemDomain isd = testDataHelper.createInformationSystemDomain("testISD", "testISD description");

    BusinessDomain bd = testDataHelper.createBusinessDomain("testBSD", "testBSD description");

    ArchitecturalDomain ad = testDataHelper.createArchitecturalDomain("testAD", "testAD description");

    int index = 0;
    for (InformationSystemRelease isr : originalIsrList) {
      index++;
      isr.addBusinessFunctions(bfSet);

      testDataHelper.addIsrToIsd(isr, isd);

      int partnerIndex = (originalIsrList.indexOf(isr) + 5) % originalIsrList.size();
      InformationSystemRelease isr2 = originalIsrList.get(partnerIndex);
      TechnicalComponentRelease tcr;
      if (!isr.getTechnicalComponentReleases().isEmpty()) {
        tcr = isr.getTechnicalComponentReleases().iterator().next();
        Set<TechnicalComponentRelease> tcSet = new HashSet<TechnicalComponentRelease>();
        tcSet.add(tcr);
        ad.addTechnicalComponentReleases(tcSet);
      }
      else {
        tcr = null;
      }
      String desc = "ISI " + isr.getId() + "-" + isr2.getId() + DESCRIPTION_SUFFIX;

      InformationSystemInterface isi = testDataHelper.createInformationSystemInterface(isr, isr2, tcr, desc);

      if (index == indexOfNamedIF) {
        isi.setName("NAMED IF");
      }
      else if (index == indexOfNamedIFWithBO) {
        isi.setName("NAMED IF WITH BO");


        BusinessObject bo = createTransportWithNewBO("BO_01", "NAMED IF WITH BO", Direction.BOTH_DIRECTIONS, isi);
        addBusinessObjectToBusinessDomain(bo, bd);

        isi.setDirection(TransportInfo.FIRST_TO_SECOND.getTextRepresentation());

      }
      else if (index == indexOfAnonymousIFWithBO) {
        BusinessObject bo = createTransportWithNewBO("BO_02", "ANONYMOUS IF WITH BO", Direction.SECOND_TO_FIRST, isi);
        addBusinessObjectToBusinessDomain(bo, bd);

        isi.setDirection(TransportInfo.FIRST_TO_SECOND.getTextRepresentation());
      }

      assignAttributeValuesToBuildingBlock(isi, partnerIndex, attributes);
    }

    commit();

    // Make sure that we have TechnicalComponentReleases in our data set and that they are
    // added to the ArchitecturalDomain
    assertFalse(ad.getTechnicalComponentReleases().isEmpty());

    beginTransaction();
    originalIsrList = informationSystemReleaseService.loadElementList();
    for (InformationSystemRelease isr : originalIsrList) {
      // touching the collections to load them despite lazy init, making them accessible for later asserts.
      isr.getBusinessObjects().size();
      isr.getBaseComponents().size();
      isr.getPredecessors().size();
      isr.getInformationSystemDomains().size();
      isr.getTechnicalComponentReleases().size();
      isr.getInfrastructureElements().size();
      isr.getProjects().size();
      isr.getAttributeValueAssignments().size();
      isr.getBusinessMappings().size();
    }
    commit();
  }

  private BusinessObject createTransportWithNewBO(String boName, String boDescription, Direction transportDirection, InformationSystemInterface isi) {
    BusinessObject bo = testDataHelper.createBusinessObject(boName, boDescription);
    testDataHelper.createTransport(bo, isi, transportDirection);
    return bo;
  }

  private void addBusinessObjectToBusinessDomain(BusinessObject bo, BusinessDomain bd) {
    Set<BusinessObject> boSet = new HashSet<BusinessObject>();
    boSet.add(bo);
    bd.addBusinessObjects(boSet);
  }

  private Map<AttributeType, List<AttributeValue>> createAttributeTypesAndValues() {
    Map<AttributeType, List<AttributeValue>> attributes = CollectionUtils.hashMap();

    AttributeTypeGroup defaultGroup = testDataHelper.createAttributeTypeGroup("Default Attribute Type Group",
        "Default Attribute Type Group Description");
    AttributeTypeGroup secondGroup = testDataHelper
        .createAttributeTypeGroup("Second Attribute Type Group", "Second Attribute Type Group Description");

    // Enum AT
    List<AttributeValue> avList = CollectionUtils.arrayList();
    EnumAT enumAT = testDataHelper.createEnumAttributeType("enumAT", "enumAT description", Boolean.TRUE, defaultGroup);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(enumAT);
    for (int i = 1; i < 6; i++) {
      avList.add(testDataHelper.createEnumAV("enumAV" + i, "enumAV" + i + DESCRIPTION_SUFFIX, enumAT));
    }
    attributes.put(enumAT, avList);

    // Number AT
    avList = CollectionUtils.arrayList();
    NumberAT numberAT = testDataHelper.createNumberAttributeType("numberAT", "numberAT description", defaultGroup);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(numberAT);
    attributes.put(numberAT, avList);

    // Date AT
    avList = CollectionUtils.arrayList();
    DateAT dateAT = testDataHelper.createDateAttributeType("dateAT", "dateAT description", defaultGroup);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(dateAT);
    attributes.put(dateAT, avList);

    // Text AT
    avList = CollectionUtils.arrayList();
    TextAT textAT = testDataHelper.createTextAttributeType("textAT", "textAT description", true, secondGroup);
    testDataHelper.assignAttributeTypeToAllAvailableBuildingBlockTypes(textAT);
    attributes.put(textAT, avList);

    return attributes;
  }

  private List<InformationSystemRelease> createFilledISRs(int count, Map<AttributeType, List<AttributeValue>> attributes) {
    InformationSystem is = testDataHelper.createInformationSystem("test IS " + count);
    InformationSystemRelease isr = testDataHelper.createInformationSystemRelease(is, "Test." + count);
    assignAttributeValuesToBuildingBlock(isr, count, attributes);
    //InformationSystem is2 = testDataHelper.createInformationSystem("test IS 2: " + count);
    InformationSystemRelease isr2 = testDataHelper.createInformationSystemRelease(is, "Test." + count + ".BETA");
    assignAttributeValuesToBuildingBlock(isr2, count * 2, attributes);

    testDataHelper.addChildToIsr(isr, isr2);
    testDataHelper.addSuccessorToIsr(isr2, isr);
    testDataHelper.addBaseComponentToISRelease(isr, isr2);

    createBusinessMappings(count, Lists.newArrayList(isr, isr2), attributes);

    addBOs(count, Lists.newArrayList(isr, isr2), attributes);

    addProjs(count, Collections.singletonList(isr2), attributes);

    addIEs(count, Collections.singletonList(isr2), attributes);

    addTCRs(count, Collections.singletonList(isr), attributes);

    return Lists.newArrayList(isr, isr2);
  }

  private void assignAttributeValuesToBuildingBlock(BuildingBlock bb, int count, Map<AttributeType, List<AttributeValue>> attributes) {
    for (Map.Entry<AttributeType, List<AttributeValue>> entry : attributes.entrySet()) {
      AttributeType at = entry.getKey();
      List<AttributeValue> avList = entry.getValue();
      AttributeValue av = null;

      if (at instanceof EnumAT || at instanceof ResponsibilityAT) {
        int actualCount = count % avList.size();
        av = avList.get(actualCount);
      }
      else if (at instanceof NumberAT) {
        av = testDataHelper.createNumberAV(new BigDecimal(count * count), (NumberAT) at);
        avList.add(av);
      }
      else if (at instanceof DateAT) {
        av = testDataHelper.createDateAV(new Date(count * 100000000), (DateAT) at);
        avList.add(av);
      }
      else if (at instanceof TextAT) {
        av = testDataHelper.createTextAV("text " + count + "\nnewline", (TextAT) at);
        avList.add(av);
      }

      if (av != null) {
        testDataHelper.createAVA(bb, av);
      }
    }
  }

  private void createBusinessMappings(int count, List<InformationSystemRelease> isrs, Map<AttributeType, List<AttributeValue>> attributes) {
    BusinessProcess bp = testDataHelper.createBusinessProcess("testBP" + count, "testBP" + count + DESCRIPTION_SUFFIX);
    assignAttributeValuesToBuildingBlock(bp, count, attributes);
    BusinessUnit bu = testDataHelper.createBusinessUnit("testBU" + count, "testBU" + count + DESCRIPTION_SUFFIX);
    assignAttributeValuesToBuildingBlock(bu, count, attributes);
    Product prod = testDataHelper.createProduct("testProd" + count, "testProd" + count + DESCRIPTION_SUFFIX);
    assignAttributeValuesToBuildingBlock(prod, count, attributes);
    BusinessFunction bf = testDataHelper.createBusinessFunction("testBF" + count, "testBF" + count + DESCRIPTION_SUFFIX);
    assignAttributeValuesToBuildingBlock(bf, count, attributes);

    for (InformationSystemRelease isr : isrs) {
      testDataHelper.createBusinessMapping(isr, bp, bu, prod);
      testDataHelper.createBusinessMapping(isr, bp, bu, prod.getParent());
      testDataHelper.createBusinessMapping(isr, bp, bu.getParent(), prod);
      testDataHelper.createBusinessMapping(isr, bp, bu.getParent(), prod.getParent());
      testDataHelper.createBusinessMapping(isr, bp.getParent(), bu, prod);
      testDataHelper.createBusinessMapping(isr, bp.getParent(), bu, prod.getParent());
      testDataHelper.createBusinessMapping(isr, bp.getParent(), bu.getParent(), prod);
    }
  }

  private void addBOs(int count, List<InformationSystemRelease> isrs, Map<AttributeType, List<AttributeValue>> attributes) {
    for (int i = 1; i <= 3; i++) {
      String name = "testBO" + count + "." + i;
      BusinessObject bo = testDataHelper.createBusinessObject(name, name + DESCRIPTION_SUFFIX);
      assignAttributeValuesToBuildingBlock(bo, count, attributes);
      for (InformationSystemRelease isr : isrs) {
        testDataHelper.addBusinessObjectToInformationSystem(isr, bo);
      }
    }
  }

  private void addProjs(int count, List<InformationSystemRelease> isrs, Map<AttributeType, List<AttributeValue>> attributes) {
    for (int i = 1; i <= 3; i++) {
      String name = "testProj" + count + "." + i;
      Project proj = testDataHelper.createProject(name, name + DESCRIPTION_SUFFIX);
      assignAttributeValuesToBuildingBlock(proj, count, attributes);
      for (InformationSystemRelease isr : isrs) {
        testDataHelper.addIsrToProject(isr, proj);
      }
    }
  }

  private void addIEs(int count, List<InformationSystemRelease> isrs, Map<AttributeType, List<AttributeValue>> attributes) {
    for (int i = 1; i <= 3; i++) {
      String name = "testIE" + count + "." + i;
      InfrastructureElement ie = testDataHelper.createInfrastructureElement(name, name + DESCRIPTION_SUFFIX);
      assignAttributeValuesToBuildingBlock(ie, count, attributes);
      for (InformationSystemRelease isr : isrs) {
        testDataHelper.addIeToIsr(isr, ie);
      }
    }
  }

  private void addTCRs(int count, List<InformationSystemRelease> isrs, Map<AttributeType, List<AttributeValue>> attributes) {
    for (int i = 1; i <= 3; i++) {
      TechnicalComponent tc = testDataHelper.createTechnicalComponent("testTC" + count + "." + i, true, true);
      TechnicalComponentRelease tcr = testDataHelper.createTCRelease(tc, "1." + i, true);
      assignAttributeValuesToBuildingBlock(tcr, count, attributes);
      for (InformationSystemRelease isr : isrs) {
        testDataHelper.addTcrToIsr(isr, tcr);
      }
    }
  }
}
