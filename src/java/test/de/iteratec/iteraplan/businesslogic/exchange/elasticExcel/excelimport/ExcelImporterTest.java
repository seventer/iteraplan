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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.excelimport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ClassPathResource;

import com.google.common.collect.BiMap;

import de.iteratec.iteraplan.BaseTransactionalTestSupport;
import de.iteratec.iteraplan.TestHelper;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.ExcelExportService;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.ExcelExportServiceImpl;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.ExcelTemplateGeneratorService;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.ExcelTemplateGeneratorServiceImpl;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMapping;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.IteraplanMetamodelLoaderImpl;
import de.iteratec.iteraplan.businesslogic.exchange.elasticeam.ModelLoader;
import de.iteratec.iteraplan.businesslogic.service.ArchitecturalDomainService;
import de.iteratec.iteraplan.businesslogic.service.AttributeValueService;
import de.iteratec.iteraplan.businesslogic.service.BuildingBlockServiceLocator;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.ModelFactory;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.iteraplan.elasticeam.model.compare.DiffBuilderResult;
import de.iteratec.iteraplan.elasticeam.model.diff.AbstractModelElementChange;
import de.iteratec.iteraplan.elasticeam.model.diff.AbstractModelElementChange.TypeOfModelElementChange;
import de.iteratec.iteraplan.elasticeam.model.diff.ModelWriter;
import de.iteratec.iteraplan.elasticeam.model.diff.TechnicalDiff;
import de.iteratec.iteraplan.model.AbstractHierarchicalEntity;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.user.Role;
import de.iteratec.iteraplan.model.user.User;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.MMetamodelComparator;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.MMetamodelComparator.MMChange;
import de.iteratec.iteraplan.persistence.elasticeam.metamodel.MMetamodelComparator.MMChangeKind;
import de.iteratec.iteraplan.persistence.elasticeam.model.diff.ModelWriterFactory;


public class ExcelImporterTest extends BaseTransactionalTestSupport {

  @Autowired
  private IteraplanMetamodelLoaderImpl metamodelLoader;

  @Autowired
  private ModelLoader                  modelLoader;

  @Autowired
  private AttributeValueService        avService;

  @Autowired
  private BuildingBlockServiceLocator  bbServiceLocator;

  /** Logger. */
  private static final Logger          LOGGER                                      = Logger.getIteraplanLogger(ExcelImporterTest.class);

  private static final String          BASE_PATH                                   = "src/java/test/";
  private static final String          EXCEL_TEST_FILES                            = "excel/testFiles/elasticExcel/";

  private static final String          completeFilenameBankdataInExcel2003         = BASE_PATH + EXCEL_TEST_FILES + "iteraplanExcelData_BankData.xls";
  private static final String          completeFilenameBankdataInExcel2007         = BASE_PATH + EXCEL_TEST_FILES
      + "iteraplanExcelData_BankData.xlsx";

  private static final String          completeFilenameNewBBInExcel2003            = BASE_PATH + EXCEL_TEST_FILES
      + "iteraplanExcelData_NewBBWithStructure.xls";
  private static final String          completeFilenameNewBBInExcel2007            = BASE_PATH + EXCEL_TEST_FILES
      + "iteraplanExcelData_NewBBWithStructure.xlsx";

  private static final String          completeFilenameNewAttributesInExcel2003    = BASE_PATH + EXCEL_TEST_FILES
      + "iteraplanExcelTemplate_NewAttributes.xls";
  private static final String          completeFilenameNewAttributesInExcel2007    = BASE_PATH + EXCEL_TEST_FILES
      + "iteraplanExcelTemplate_NewAttributes.xlsx";

  private static final String          completeFilenameChangedAttributeInExcel2003 = BASE_PATH + EXCEL_TEST_FILES
      + "iteraplanExcelTemplate_ChangedAttribute.xls";
  private static final String          completeFilenameChangedAttributeInExcel2007 = BASE_PATH + EXCEL_TEST_FILES
      + "iteraplanExcelTemplate_ChangedAttribute.xlsx";

  @Before
  public void init() {
    User user = new User();
    user.setLoginName("system");
    Role supervisorRole = new Role();
    supervisorRole.setRoleName(Role.SUPERVISOR_ROLE_NAME);
    Set<Role> roles = new HashSet<Role>();
    roles.add(supervisorRole);
    UserContext context = new UserContext(user.getLoginName(), roles, new Locale("de"), user);

    UserContext.setCurrentUserContext(context);
  }

  @Test
  public void testAndCompareWithSameModelExcel2003() {
    testAndCompareWithSameModel(completeFilenameBankdataInExcel2003);
  }

  @Test
  public void testAndCompareWithSameModelExcel2007() {
    testAndCompareWithSameModel(completeFilenameBankdataInExcel2007);
  }

  private void testAndCompareWithSameModel(String completeFilename) {
    Workbook workbook = ExcelUtils.openExcelFile(completeFilename);
    assertNotNull("Couldn't read file " + completeFilename, workbook);

    ExcelImporter imp1 = new ExcelImporter(workbook);
    imp1.importExcel();

    Workbook workbook2 = ExcelUtils.openExcelFile(completeFilename);
    assertNotNull("Couldn't read file " + completeFilename, workbook2);
    ExcelImporter imp2 = new ExcelImporter(workbook2);
    imp2.importExcel();

    Metamodel mm1 = imp1.getMetamodel();
    Metamodel mm2 = imp2.getMetamodel();
    List<MMChange<?>> diffList = MMetamodelComparator.diff(mm1, mm2);

    LOGGER.info("### Metamodel Diffs: " + diffList.size());
    for (MMChange<?> diff : diffList) {
      LOGGER.info("Diff: {0}: {1}", diff.getClass().getName(), diff.toString());
    }
    assertEquals(0, diffList.size());

    assertNotSame(imp1.getModel(), imp2.getModel());
    ModelWriter writer = ModelWriterFactory.INSTANCE.getWriter(imp1.getModel(), imp2.getModel(), mm1);
    Map<TypeOfModelElementChange, List<AbstractModelElementChange>> changes = writer.getChanges();

    Map<TypeOfModelElementChange, List<AbstractModelElementChange>> filteredChanges = filterChanges(changes);

    for (TypeOfModelElementChange type : filteredChanges.keySet()) {
      List<AbstractModelElementChange> changeList = filteredChanges.get(type);
      assertNotNull(changeList);
      assertTrue(changeList.isEmpty());
      //      for (AbstractModelElementChange change : changeList) {
      //        LOGGER.info("Change: {0}: {1}", change.getClass().getName(), change.toString());
      //      }
      //      LOGGER.info("Change type {0}: {1} changes", type.name(), changeList.size());
    }
  }

  @Test
  public void testImportAndReExportExcel2003() throws FileNotFoundException, IOException {
    testImportAndReExport(completeFilenameBankdataInExcel2003);
  }

  @Test
  public void testImportAndReExportExcel2007() throws FileNotFoundException, IOException {
    testImportAndReExport(completeFilenameBankdataInExcel2007);
  }

  private void testImportAndReExport(String completeFilename) throws FileNotFoundException, IOException {

    Workbook workbook = ExcelUtils.openExcelFile(completeFilename);
    assertNotNull("Couldn't read file " + completeFilename, workbook);

    ExcelImporter imp = new ExcelImporter(workbook);

    try {
      long start = System.currentTimeMillis();
      imp.importExcel();
      long end = System.currentTimeMillis();
      LOGGER.info("Import OK: took {0} ms", new Long(end - start));
    } catch (RuntimeException e) {
      LOGGER.error("Import failed: ", e);
      fail("Import failed: " + e.getMessage());
    }

    assertTrue("Errors while importing Excel file!", imp.getErrorMessages().isEmpty());

    LOGGER.info("Import: Done.");

    reExportModel(imp);
  }

  /**
   * @param imp
   * @throws FileNotFoundException
   * @throws IOException
   */
  private void reExportModel(ExcelImporter imp) throws FileNotFoundException, IOException {

    String excel2003TemplateFilename = "/templates/elasticExcel/ExcelWorkbook.xls";
    String excel2007TemplateFilename = "/templates/elasticExcel/ExcelWorkbook.xlsx";
    String logoFilename = "/templates/elasticExcel/iteraplanLogo670x100.PNG";

    AbstractResource excel2003TemplateResource = new ClassPathResource(excel2003TemplateFilename);
    AbstractResource excel2007TemplateResource = new ClassPathResource(excel2007TemplateFilename);
    AbstractResource logoResource = new ClassPathResource(logoFilename);
    ExcelTemplateGeneratorService etgs = new ExcelTemplateGeneratorServiceImpl(excel2003TemplateResource, excel2007TemplateResource, logoResource);
    ExcelExportService ees = new ExcelExportServiceImpl(etgs);
    WorkbookContext wbc = ees.exportExcel2003(imp.getModel(), imp.getMetamodel());

    String outDir = "./build/elasticExcelOutput";
    String filenameOut = outDir + "/iteraplanExcelData-Out.xls";

    new File(outDir).mkdirs();
    File outFile = new File(filenameOut);
    FileOutputStream out = new FileOutputStream(outFile);
    LOGGER.info("Writing output to: {0}", outFile.getAbsolutePath());
    wbc.getWb().write(out);
    out.close();
  }

  /**
   * @param changes
   * @return new map, without TechnicalDiff changes.
   */
  private Map<TypeOfModelElementChange, List<AbstractModelElementChange>> filterChanges(Map<TypeOfModelElementChange, List<AbstractModelElementChange>> changes) {
    Map<TypeOfModelElementChange, List<AbstractModelElementChange>> result = new HashMap<TypeOfModelElementChange, List<AbstractModelElementChange>>();

    for (TypeOfModelElementChange type : changes.keySet()) {
      List<AbstractModelElementChange> filteredList = new ArrayList<AbstractModelElementChange>();
      for (AbstractModelElementChange change : changes.get(type)) {
        if (!(change instanceof TechnicalDiff)) {
          filteredList.add(change);
        }
      }
      result.put(type, filteredList);
    }

    return result;
  }

  @Test
  public void testImportNewBuildingBlocks2003() {
    testImportNewBuildingBlocks(completeFilenameNewBBInExcel2003);
  }

  @Test
  public void testImportNewBuildingBlocks2007() {
    testImportNewBuildingBlocks(completeFilenameNewBBInExcel2007);
  }

  private void testImportNewBuildingBlocks(String completeFileName) {

    IteraplanMapping referenceMapping = metamodelLoader.loadConceptualMetamodelMapping();
    Metamodel metamodel = referenceMapping.getMetamodel();

    Model model = ModelFactory.INSTANCE.createModel(referenceMapping.getMetamodel());

    Workbook workbook = ExcelUtils.openExcelFile(completeFileName);
    assertNotNull("Couldn't read file " + completeFileName, workbook);

    ExcelImporter importer = new ExcelImporter(workbook);
    importer.importExcel();

    Metamodel importerMetaModel = importer.getMetamodel();
    Model importerModel = importer.getModel();

    List<MMChange<?>> diffList = MMetamodelComparator.diff(metamodel, importerMetaModel);

    LOGGER.info("### Metamodel Diffs: " + diffList.size());
    for (MMChange<?> diff : diffList) {
      LOGGER.info("Diff: {0}: {1}", diff.getClass().getName(), diff.toString());
    }
    assertEquals(0, diffList.size());

    DiffBuilderResult diffResult = TestHelper.getDiffBuilderResults(metamodel, model, importerModel);
    TestHelper.printDiffBuilderResultDifferences(metamodel, diffResult);

    assertEquals(0, importer.getErrorMessages().size());

    BiMap<Object, UniversalModelExpression> instanceMapping = modelLoader.load(model, referenceMapping);
    TestHelper.writeChangesToDatabase(diffResult, referenceMapping, instanceMapping, avService, bbServiceLocator);

    commit();
    beginTransaction();

    ArchitecturalDomainService adService = bbServiceLocator.getAdService();
    List<ArchitecturalDomain> entityList = adService.loadElementList();

    assertEquals(entityList.size(), 5);

    for (ArchitecturalDomain ad : entityList) {
      LOGGER.info("name: " + ad.getName());
      if (ad.getName().equals(AbstractHierarchicalEntity.TOP_LEVEL_NAME)) {
        LOGGER.info("Ignoring virtual root element");
        continue;
      }
      ArchitecturalDomain parent = ad.getParent();
      if (parent == null) {
        LOGGER.info("parent: null");
      }
      else {
        LOGGER.info("parent: " + parent.getName());
      }
      Integer pos = ad.getPosition();
      if (pos == null) {
        LOGGER.info("position: null");
      }
      else {
        LOGGER.info("position: " + ad.getPosition());
      }
      assertNotNull(ad.getPosition());
    }

    /*
    WorkbookContext context = excelExportService.exportExcel2007(model, metamodel);
    File tempFile = File.createTempFile("ThisIsMyExcelFile", "xlsx");
    ExcelExportTestUtils.persistWorkbook(context.getWb(), tempFile);
     */
  }

  @Test
  public void testNewAttributesExcel2003() {
    testNewAttributes(completeFilenameNewAttributesInExcel2003, 4);
  }

  @Test
  public void testNewAttributesExcel2007() {
    testNewAttributes(completeFilenameNewAttributesInExcel2007, 4);
  }

  @Test
  public void testChangedAttributesExcel2003() {
    testNewAttributes(completeFilenameChangedAttributeInExcel2003, 4);
  }

  @Test
  public void testChangedAttributesExcel2007() {
    testNewAttributes(completeFilenameChangedAttributeInExcel2007, 4);
  }

  /**
   * Tests the import of an excel file that consists of a number of enumeration attributes that previously weren't
   *  in the metamodel, but no other sheets. The expected behaviour is that these attributes are identified as new
   *  and that there are no other changes (such as deletion of omitted standard attributes).
   * 
   * @param completeFileName
   */
  private void testNewAttributes(String completeFileName, int expectedNumberOfChanges) {
    IteraplanMapping referenceMapping = metamodelLoader.loadConceptualMetamodelMapping();
    Metamodel metamodel = referenceMapping.getMetamodel();

    Workbook workbook = ExcelUtils.openExcelFile(completeFileName);
    assertNotNull("Couldn't read file " + completeFileName, workbook);

    ExcelImporter importer = new ExcelImporter(workbook);
    importer.importExcel();

    Metamodel importerMetaModel = importer.getMetamodel();

    List<MMChange<?>> diffList = MMetamodelComparator.diff(metamodel, importerMetaModel);

    LOGGER.info("### Metamodel Diffs: " + diffList.size());
    for (MMChange<?> diff : diffList) {
      LOGGER.info("Diff: {0}: {1}", diff.getClass().getName(), diff.toString());
    }

    assertEquals(diffList.size(), expectedNumberOfChanges);

    for (MMChange<?> change : diffList) {
      assertEquals(change.getChangeKind(), MMChangeKind.ADD);
    }

  }

}
