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

import static de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.AbstractSheetGenerator.DESCRIPTION_ROW_NO;
import static de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.AbstractSheetGenerator.SHEET_TYPE_ROW_NO;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.DataExportIntroSheetGenerator;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.IteraExcelStyle;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.TypeExpressionReader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.SheetContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.emfimpl.EMFMetamodel;
import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.NamedExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.ModelFactory;


/**
 * Excel import: Read Excel Workbook, and create an "Elastic" metamodel and model.
 */
public class ExcelImporter {

  private static final Logger     LOGGER             = Logger.getIteraplanLogger(ExcelImporter.class);

  /** pattern to read the name cell in the title row. */
  private static final Pattern    NAME_PATTERN       = Pattern.compile("(.*)( \\(.*\\))");

  private final EditableMetamodel metamodel;
  private final Model             model;
  private final WorkbookContext   wbContext;
  private final Workbook          workbook;
  private final List<Sheet>       relationshipSheets = new ArrayList<Sheet>();

  private final List<String>      errorMessages      = new ArrayList<String>();

  /**
   * Constructor.
   * 
   * @param workbook the workbook to import.
   */
  public ExcelImporter(Workbook workbook) {
    this.workbook = workbook;
    wbContext = new WorkbookContext(workbook, new HashMap<IteraExcelStyle, CellStyle>());
    metamodel = new EMFMetamodel("conceptual", true);
    model = ModelFactory.INSTANCE.createModel(metamodel);
  }

  public Model getModel() {
    return model;
  }

  public Metamodel getMetamodel() {
    return metamodel;
  }

  /** returns the list of error messages. May be empty (i.e. success), but never null. */
  public List<String> getErrorMessages() {
    return errorMessages;
  }

  /**
   * do the import of the given Excel Workbook.
   * 
   * @return true if successful, false if errors occurred.
   */
  public boolean importExcel() {

    importMetamodel();

    if (errorMessages.isEmpty()) {
      importModel();
    }

    if (!errorMessages.isEmpty()) {
      LOGGER.error("Errors: " + errorMessages.size());
    }

    LOGGER.info("    ==== Import: Done. =====");

    return errorMessages.isEmpty();
  }

  /**
   * import meta model (i.e.: data schema)
   */
  private void importMetamodel() {
    LOGGER.info("    ==== First pass =====");
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      Sheet sheet = workbook.getSheetAt(i);
      try {
        importMetamodelTypes(sheet);
      } catch (ElasticeamException e) {
        logError("Sheet {0}: Import error: {1}", sheet.getSheetName(), e.getMessage());
      }
    }
    for (SheetContext sheetContext : wbContext.getSheetContexts()) {
      importEnumSheet(sheetContext);
    }

    LOGGER.info("    ==== Second pass =====");
    for (SheetContext sheetContext : wbContext.getSheetContexts()) {
      importMetamodelFeatures(sheetContext);
    }
    for (Sheet sheet : relationshipSheets) {
      importRelationshipSheet(sheet);
    }
  }

  /**
   * import model (i.e.: data itself)
   */
  private void importModel() {
    LOGGER.info("    ==== Third pass =====");
    EntityDataImporter edi = new EntityDataImporter(metamodel, model);
    for (SheetContext sheetContext : wbContext.getSheetContexts()) {
      edi.importEntityData(sheetContext, EntityDataImporter.Pass.PRIMITIVES);
    }

    LOGGER.info("    ==== Fourth pass =====");
    for (SheetContext sheetContext : wbContext.getSheetContexts()) {
      edi.importEntityData(sheetContext, EntityDataImporter.Pass.RELATIONS);
    }
    RelationDataImporter rdi = new RelationDataImporter(metamodel, model);
    for (Sheet sheet : relationshipSheets) {
      rdi.importRelationData(sheet);
    }

    errorMessages.addAll(edi.getErrorMessages());
    errorMessages.addAll(rdi.getErrorMessages());
  }

  /**
   * first pass: only create types.
   * 
   * Read the given sheet, and analyze the type defined on this sheet.
   *
   * @param sheet the Excel sheet.
   */
  private void importMetamodelTypes(Sheet sheet) {

    if (sheet.getSheetName().equals(DataExportIntroSheetGenerator.INTRO_SHEET_NAME)) {
      LOGGER.info("Intro");
      return;
    }

    String typeString = null;
    if (sheet.getRow(SHEET_TYPE_ROW_NO) != null && sheet.getRow(SHEET_TYPE_ROW_NO).getCell(0) != null) {
      typeString = ExcelUtils.getStringCellValue(sheet.getRow(SHEET_TYPE_ROW_NO).getCell(0));
    }
    else {
      logError("Sheet {0}: Sheet Type not defined", sheet.getSheetName());
      return;
    }

    TypeExpressionReader ter = new TypeExpressionReader(typeString);

    LOGGER.info("Sheet {0}: Type: {1}, Metatype {2}", sheet.getSheetName(), ter.getTypeName(), ter.getMetaTypeName());

    NamedExpression expr = null;
    if (ter.getMetaTypeName().equals("SubstantialTypeExpression")) {
      expr = metamodel.createSubstantialType(ter.getTypeName());
    }
    else if (ter.getMetaTypeName().equals("RelationshipTypeExpression")) {
      expr = metamodel.createRelationshipType(ter.getTypeName());
    }
    else if (ter.getMetaTypeName().equals("EnumerationExpression")) {
      expr = metamodel.createEnumeration(ter.getTypeName());
    }
    else if (ter.getMetaTypeName().equals("RelationshipExpression")) {
      relationshipSheets.add(sheet);
    }
    else {
      logError("Sheet {0}: cannot deal with metatype {1}", sheet.getSheetName(), ter.getMetaTypeName());
      return;
    }

    if (expr != null) {
      String nameString;
      if (expr instanceof EnumerationExpression) {
        //it doesn't make sense to use an abbreviation on an EnumerationExpression,
        //so it takes the name as it is from the cell, without calling readNameString()
        //REVIEW <ITERAPLAN-1612>: TODO Abbreviations for enumeration types should be supported in later versions, when that happens, this piece of code must be reviewed again.

        nameString = ExcelUtils.getStringCellValue(sheet.getRow(0).getCell(0));
      }
      else {
        // strip the type abbreviation from the name
        nameString = readNameString(sheet);
      }
      expr.setName(nameString);
      expr.setAbbreviation(ter.getAbbreviation());
      expr.setDescription(readDescription(sheet));
      SheetContext sheetContext = new SheetContext(sheet, expr);
      wbContext.addSheetContext(sheetContext);
    }
  }

  /**
   * @param sheet
   * @return the name as given in the title row, without the abbreviation (if given).
   */
  private String readNameString(Sheet sheet) {
    String cellValue = ExcelUtils.getStringCellValue(sheet.getRow(0).getCell(0));
    Matcher m = NAME_PATTERN.matcher(cellValue);

    String result;
    if (m.matches()) {
      result = m.group(1);
    }
    else {
      result = cellValue;
    }

    return result;
  }

  /**
   * read description string from the sheet.
   * 
   * @param sheet
   */
  private String readDescription(Sheet sheet) {
    String description = null;

    Row row = sheet.getRow(DESCRIPTION_ROW_NO);
    if (row != null) {
      Cell descrCell = row.getCell(0);

      if (descrCell != null) {
        description = ExcelUtils.getStringCellValue(descrCell);
      }
    }
    return description;
  }

  /**
   * also first pass: Import enum literals.
   * 
   * @param sheetContext the sheet meta info. The sheet will only be imported if it is an
   *        {@link EnumerationExpression} sheet.
   */
  private void importEnumSheet(SheetContext sheetContext) {
    String typeString = ExcelUtils.getStringCellValue(sheetContext.getSheet().getRow(SHEET_TYPE_ROW_NO).getCell(0));
    TypeExpressionReader ter = new TypeExpressionReader(typeString);

    if (ter.getMetaTypeName().equals("EnumerationExpression")) {
      try {
        new EnumSheetImporter(metamodel).importEnumerationExpression(sheetContext);
      } catch (ElasticeamException e) {
        logError("Sheet {0}: Import error: {1}", sheetContext.getSheetName(), e.getMessage());
      }
    }
  }

  /**
   * second pass: fill types with attributes.
   * 
   * @param sheetContext the sheet context; the sheet will only be imported if it is a
   *        {@link SubstantialTypeExpression} or a {@link RelationshipTypeExpression}.
   */
  private void importMetamodelFeatures(SheetContext sheetContext) {

    NamedExpression expr = sheetContext.getExpression();

    if (expr == null) {
      logError("Sheet {0}: Error: No expression found", sheetContext.getSheetName());
      return;
    }

    if (expr instanceof SubstantialTypeExpression || expr instanceof RelationshipTypeExpression) {
      try {
        new UniversaltypeSheetImporter(metamodel).importMetamodelTypeSpecification(sheetContext);
      } catch (ElasticeamException e) {
        logError("Sheet {0}: Import error: {1}", sheetContext.getSheetName(), e.getMessage());
      }
    }
    else if (!(expr instanceof EnumerationExpression)) {
      logError("Sheet {0}: Import error: Cannot deal with expression {1} of type {2}", sheetContext.getSheetName(), expr.getPersistentName(), expr
          .getClass().getName());
    }
  }

  /**
   * also second pass: import relationships.
   * 
   * @param sheet the excel sheet; this sheet must have a relationship expression.
   */
  private void importRelationshipSheet(Sheet sheet) {
    try {
      new RelationSheetImporter(metamodel).importMetamodelRelationTypeSpecification(sheet);
    } catch (ElasticeamException e) {
      logError("Sheet {0}: Import error: {1}", sheet.getSheetName(), e.getMessage());
    }
  }

  private void logError(String format, Object... params) {
    String message = MessageFormat.format(format, params);
    LOGGER.error(message);
    errorMessages.add(message);
  }
}
