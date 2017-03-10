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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import com.google.common.collect.Sets;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets.ExcelSheet;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.SheetImporter;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.model.BusinessMappingSheetImporter;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.model.HierarchicalBuildingBlockSheetImporter;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.model.InformationSystemInterfaceSheetImporter;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.model.InformationSystemReleaseSheetImporter;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.model.ProjectSheetImporter;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.model.TechnicalComponentSheetImporter;
import de.iteratec.iteraplan.businesslogic.service.EntityService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.model.ArchitecturalDomain;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemDomain;
import de.iteratec.iteraplan.model.InfrastructureElement;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.user.User;


/**
 * A {@link ImportWorkbook} extension for importing landscape data out of the specified
 * Excel file. Each file must contain sheet, where configuration details are specified.
 * This config sheet must be named as {@value de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.ExcelWorkbook#DEFAULT_SHEET_KEY}.
 * 
 */
public class LandscapeDataWorkbook extends ImportWorkbook {

  private final Map<String, SheetImporter<?>>    sheetImporters     = new HashMap<String, SheetImporter<?>>();

  private final Map<String, TypeOfBuildingBlock> sheetToTypeMapping = new HashMap<String, TypeOfBuildingBlock>();

  public LandscapeDataWorkbook(ProcessingLog userLog) {
    super(userLog);
  }

  /**
   * Reads the specified {@code excelInputStream} and returns the landscape data
   * as {@link LandscapeData} object, containing the building blocks, relations and
   * attributes. If the import failes, or no data could be found, this method
   * returns an empty landscape data object.
   * 
   * @param excelInputStream the Excel file input stream
   * @return the {@link LandscapeData} object containing building blocks, relations and attributes
   */
  public LandscapeData doImport(InputStream excelInputStream) {
    loadWorkbookFromInputStream(excelInputStream);
    final LandscapeData landscapeData = new LandscapeData();

    if (!readInConfigSheet()) {
      getProcessingLog().error("Could not read in configuration sheet. Aborting.");
      landscapeData.setLocale(getLocale());
      return landscapeData;
    }

    // we can only initialize the sheet importers *after* the locale was read in from the config sheet
    initSheetImporters();

    calculateAllFormulas();

    for (int i = 0; i < getWb().getNumberOfSheets(); i++) {
      Sheet sheet = getWb().getSheetAt(i);
      String sheetName = sheet.getSheetName();

      getProcessingLog().insertDummyRow("");

      getProcessingLog().debug("Current Sheet: " + (sheetName == null ? "null" : sheetName));

      if (isSheetSupported(sheetName)) {
        final String idKey = MessageAccess.getStringOrNull("global.id", getLocale());
        int contentPosition = findSheetContentPosition(sheet, idKey, 0);
        if (contentPosition != -1) {
          importSheet(sheet, contentPosition, landscapeData);
        }
        else {
          getProcessingLog().warn("Invalid structure of Sheet '" + sheetName + "', skipping");

        }
        int contentPositionSubscribed = findSheetContentPosition(sheet, MessageAccess.getStringOrNull(Constants.SUBSCRIBED_USERS, getLocale()), 1);
        Set<User> users = Sets.newHashSet();
        if (contentPositionSubscribed != -1) {
          Row row = sheet.getRow(contentPositionSubscribed - 1);
          if (row.getPhysicalNumberOfCells() > 1) {
            String subUsers = row.getCell(2, Row.CREATE_NULL_AS_BLANK).getStringCellValue();
            if (StringUtils.isNotEmpty(subUsers)) {
              Set<String> logins = Sets.newHashSet(ExcelImportUtilities.getSplittedArray(subUsers, ExcelSheet.IN_LINE_SEPARATOR.trim()));
              for (String login : logins) {
                User user = new User();
                user.setLoginName(login);
                users.add(user);
              }
            }
          }
        }
        TypeOfBuildingBlock type = sheetToTypeMapping.get(sheetName);
        landscapeData.getUsers().put(type, users);
      }
      else if (!DEFAULT_SHEET_KEY.equals(sheetName)) {
        getProcessingLog().warn("Unknown Sheet Name '" + sheetName + "'(different Locale?). Skipping.");
      }
    }

    landscapeData.setLocale(getLocale());

    return landscapeData;
  }

  private void initSheetToTypeMapping() {
    TypeOfBuildingBlock[] blocks = new TypeOfBuildingBlock[] { TypeOfBuildingBlock.ARCHITECTURALDOMAIN, TypeOfBuildingBlock.BUSINESSDOMAIN,
        TypeOfBuildingBlock.BUSINESSFUNCTION, TypeOfBuildingBlock.BUSINESSOBJECT, TypeOfBuildingBlock.BUSINESSPROCESS,
        TypeOfBuildingBlock.BUSINESSUNIT, TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN, TypeOfBuildingBlock.INFRASTRUCTUREELEMENT,
        TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, TypeOfBuildingBlock.BUSINESSMAPPING,
        TypeOfBuildingBlock.PRODUCT, TypeOfBuildingBlock.PROJECT, TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE };
    for (TypeOfBuildingBlock type : blocks) {
      sheetToTypeMapping.put(findExcelSheetNameFor(type), type);
    }
  }

  private void initSheetImporters() {
    initSheetToTypeMapping();
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.ARCHITECTURALDOMAIN), new ArchitectrualDomainHierarchicalBuildingBlockSheetImporter(
        getLocale(), TypeOfBuildingBlock.ARCHITECTURALDOMAIN, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.BUSINESSDOMAIN), new BusinessDomainHierarchicalBuildingBlockSheetImporter(
        getLocale(), TypeOfBuildingBlock.BUSINESSDOMAIN, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.BUSINESSFUNCTION), new BusinessFunctionHierarchicalBuildingBlockSheetImporter(
        getLocale(), TypeOfBuildingBlock.BUSINESSFUNCTION, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.BUSINESSOBJECT), new BusinessObjectHierarchicalBuildingBlockSheetImporter(
        getLocale(), TypeOfBuildingBlock.BUSINESSOBJECT, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.BUSINESSPROCESS), new BusinessProcessHierarchicalBuildingBlockSheetImporter(
        getLocale(), TypeOfBuildingBlock.BUSINESSPROCESS, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.BUSINESSUNIT), new BusinessUnitHierarchicalBuildingBlockSheetImporter(getLocale(),
        TypeOfBuildingBlock.BUSINESSUNIT, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN),
        new InformationSystemDomainHierarchicalBuildingBlockSheetImporter(getLocale(), TypeOfBuildingBlock.INFORMATIONSYSTEMDOMAIN,
            getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE), new InformationSystemReleaseSheetImporter(getLocale(),
        TypeOfBuildingBlock.INFORMATIONSYSTEMRELEASE, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.INFRASTRUCTUREELEMENT),
        new InfrastructureElementHierarchicalBuildingBlockSheetImporter(getLocale(), TypeOfBuildingBlock.INFRASTRUCTUREELEMENT, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE), new InformationSystemInterfaceSheetImporter(
        getLocale(), TypeOfBuildingBlock.INFORMATIONSYSTEMINTERFACE, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.PRODUCT), new ProductHierarchicalBuildingBlockSheetImporter(getLocale(),
        TypeOfBuildingBlock.PRODUCT, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.BUSINESSMAPPING), new BusinessMappingSheetImporter(getLocale(),
        TypeOfBuildingBlock.BUSINESSMAPPING, getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.PROJECT), new ProjectSheetImporter(getLocale(), TypeOfBuildingBlock.PROJECT,
        getProcessingLog()));
    sheetImporters.put(findExcelSheetNameFor(TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE), new TechnicalComponentSheetImporter(getLocale(),
        TypeOfBuildingBlock.TECHNICALCOMPONENTRELEASE, getProcessingLog()));
  }

  /**
   * Creates appropriate specific class for that sheetName/Type, which automatically starts
   * importing its data
   */
  private void importSheet(Sheet sheet, int contentRowOffset, LandscapeData landscapeData) {
    String sheetName = sheet.getSheetName();
    getProcessingLog().debug("Importing Sheet: " + sheetName);

    SheetImporter<?> sheetImporter = sheetImporters.get(sheetName);
    if (sheetImporter == null) {
      getProcessingLog().error(" Sheet Name '" + sheetName + "' not supported, skipping");
      return;
    }

    Permissions permissions = UserContext.getCurrentPerms();
    TypeOfBuildingBlock tobb = sheetImporter.getTypeOfBuildingBlockOnSheet();
    if (!permissions.userHasBbTypeUpdatePermission(tobb) || !permissions.userHasBbTypeCreatePermission(tobb)) {
      String tobbStr = MessageAccess.getStringOrNull(tobb.getPluralValue(), getLocale());
      getProcessingLog().error("You have no update and create permissions for the building block {0}, skipping sheet {1}.", tobbStr, sheetName);
      return;
    }

    final int headRowIndex = contentRowOffset - 1;
    sheetImporter.doImport(sheet, contentRowOffset, headRowIndex, landscapeData);
  }

  /**
   * Returns {@code true} if the specified {@code sheetName} is supported
   * by the configured sheet importers. Otherwise returns {@code false}.
   * 
   * @param sheetName the Excel sheet name
   * @return {@code true} if the specified {@code sheetName} is supported
   *    by the configured sheet importers
   */
  private boolean isSheetSupported(String sheetName) {
    return (sheetName != null) && (sheetImporters.get(sheetName) != null);
  }

  private static final class ArchitectrualDomainHierarchicalBuildingBlockSheetImporter extends
      HierarchicalBuildingBlockSheetImporter<ArchitecturalDomain> {
    public ArchitectrualDomainHierarchicalBuildingBlockSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
      super(locale, tob, processingLog);
    }

    @Override
    protected ArchitecturalDomain createBuildingBlockInstance() {
      return BuildingBlockFactory.createArchitecturalDomain();
    }

    @Override
    @SuppressWarnings("PMD.NoSpringFactory")
    protected EntityService<ArchitecturalDomain, Integer> getService() {
      return SpringServiceFactory.getArchitecturalDomainService();
    }
  }

  private static final class BusinessDomainHierarchicalBuildingBlockSheetImporter extends HierarchicalBuildingBlockSheetImporter<BusinessDomain> {
    public BusinessDomainHierarchicalBuildingBlockSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
      super(locale, tob, processingLog);
    }

    @Override
    protected BusinessDomain createBuildingBlockInstance() {
      return BuildingBlockFactory.createBusinessDomain();
    }

    @Override
    @SuppressWarnings("PMD.NoSpringFactory")
    protected EntityService<BusinessDomain, Integer> getService() {
      return SpringServiceFactory.getBusinessDomainService();
    }
  }

  private static final class BusinessFunctionHierarchicalBuildingBlockSheetImporter extends HierarchicalBuildingBlockSheetImporter<BusinessFunction> {
    public BusinessFunctionHierarchicalBuildingBlockSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
      super(locale, tob, processingLog);
    }

    @Override
    protected BusinessFunction createBuildingBlockInstance() {
      return BuildingBlockFactory.createBusinessFunction();
    }

    @Override
    @SuppressWarnings("PMD.NoSpringFactory")
    protected EntityService<BusinessFunction, Integer> getService() {
      return SpringServiceFactory.getBusinessFunctionService();
    }
  }

  private static final class BusinessObjectHierarchicalBuildingBlockSheetImporter extends HierarchicalBuildingBlockSheetImporter<BusinessObject> {
    public BusinessObjectHierarchicalBuildingBlockSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
      super(locale, tob, processingLog);
    }

    @Override
    protected BusinessObject createBuildingBlockInstance() {
      return BuildingBlockFactory.createBusinessObject();
    }

    @Override
    @SuppressWarnings("PMD.NoSpringFactory")
    protected EntityService<BusinessObject, Integer> getService() {
      return SpringServiceFactory.getBusinessObjectService();
    }
  }

  private static final class BusinessProcessHierarchicalBuildingBlockSheetImporter extends HierarchicalBuildingBlockSheetImporter<BusinessProcess> {
    public BusinessProcessHierarchicalBuildingBlockSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
      super(locale, tob, processingLog);
    }

    @Override
    protected BusinessProcess createBuildingBlockInstance() {
      return BuildingBlockFactory.createBusinessProcess();
    }

    @Override
    @SuppressWarnings("PMD.NoSpringFactory")
    protected EntityService<BusinessProcess, Integer> getService() {
      return SpringServiceFactory.getBusinessProcessService();
    }
  }

  private static final class BusinessUnitHierarchicalBuildingBlockSheetImporter extends HierarchicalBuildingBlockSheetImporter<BusinessUnit> {
    public BusinessUnitHierarchicalBuildingBlockSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
      super(locale, tob, processingLog);
    }

    @Override
    protected BusinessUnit createBuildingBlockInstance() {
      return BuildingBlockFactory.createBusinessUnit();
    }

    @Override
    @SuppressWarnings("PMD.NoSpringFactory")
    protected EntityService<BusinessUnit, Integer> getService() {
      return SpringServiceFactory.getBusinessUnitService();
    }
  }

  private static final class InformationSystemDomainHierarchicalBuildingBlockSheetImporter extends
      HierarchicalBuildingBlockSheetImporter<InformationSystemDomain> {
    public InformationSystemDomainHierarchicalBuildingBlockSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
      super(locale, tob, processingLog);
    }

    @Override
    protected InformationSystemDomain createBuildingBlockInstance() {
      return BuildingBlockFactory.createInformationSystemDomain();
    }

    @Override
    @SuppressWarnings("PMD.NoSpringFactory")
    protected EntityService<InformationSystemDomain, Integer> getService() {
      return SpringServiceFactory.getInformationSystemDomainService();
    }
  }

  private static final class InfrastructureElementHierarchicalBuildingBlockSheetImporter extends
      HierarchicalBuildingBlockSheetImporter<InfrastructureElement> {
    public InfrastructureElementHierarchicalBuildingBlockSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
      super(locale, tob, processingLog);
    }

    @Override
    protected InfrastructureElement createBuildingBlockInstance() {
      return BuildingBlockFactory.createInfrastructureElement();
    }

    @Override
    @SuppressWarnings("PMD.NoSpringFactory")
    protected EntityService<InfrastructureElement, Integer> getService() {
      return SpringServiceFactory.getInfrastructureElementService();
    }
  }

  private static final class ProductHierarchicalBuildingBlockSheetImporter extends HierarchicalBuildingBlockSheetImporter<Product> {
    public ProductHierarchicalBuildingBlockSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
      super(locale, tob, processingLog);
    }

    @Override
    protected Product createBuildingBlockInstance() {
      return BuildingBlockFactory.createProduct();
    }

    @Override
    @SuppressWarnings("PMD.NoSpringFactory")
    protected EntityService<Product, Integer> getService() {
      return SpringServiceFactory.getProductService();
    }
  }

}
