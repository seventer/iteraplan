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
package de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.exporter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.core.io.Resource;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.ExcelStylesCreator;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.IteraExcelStyle;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.importer.TimeseriesExcelImporter;
import de.iteratec.iteraplan.businesslogic.service.AttributeTypeService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.common.UserContext.Permissions;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpressionOrderFix;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.AttributeType;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;
import de.iteratec.iteraplan.model.attribute.TimeseriesType;
import de.iteratec.iteraplan.model.user.TypeOfFunctionalPermission;


public class TimeseriesExcelTemplateGenerator {

  private static final Logger        LOGGER = Logger.getIteraplanLogger(TimeseriesExcelTemplateGenerator.class);

  private final Resource             logoImage;
  private final AttributeTypeService atService;

  public TimeseriesExcelTemplateGenerator(Resource logoImage, AttributeTypeService atService) {
    this.logoImage = logoImage;
    this.atService = atService;
  }

  public TimeseriesWorkbookContext generateTimeseriesTemplate(Resource excelTemplate) {
    LOGGER.debug("Starting Excel-Template generation.");

    Workbook workbook = null;
    try {
      workbook = ExcelUtils.openExcelFile(excelTemplate.getInputStream());
    } catch (IOException iex) {
      LOGGER.debug(iex);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INVALID_EXCEL_TEMPLATE, "The Excel Template file could not be read.", iex);
    }
    TimeseriesWorkbookContext wbContext = new TimeseriesWorkbookContext(workbook);

    wbContext.setCellStyles(ExcelStylesCreator.createStyles(workbook));

    List<AttributeType> atList = atService.loadElementList();
    Multimap<TypeOfBuildingBlock, AttributeType> tobbToAt = HashMultimap.create();

    for (AttributeType at : atList) {
      if (at instanceof TimeseriesType && ((TimeseriesType) at).isTimeseries()) {
        for (BuildingBlockType bbt : at.getBuildingBlockTypes()) {
          tobbToAt.put(bbt.getTypeOfBuildingBlock(), at);
        }
      }
    }

    generateSheets(wbContext, tobbToAt);

    return wbContext;
  }

  private void generateSheets(TimeseriesWorkbookContext wbContext, Multimap<TypeOfBuildingBlock, AttributeType> tobbToAt) {
    List<TypeOfBuildingBlock> tobbs = Lists.newArrayList(tobbToAt.keySet());
    Collections.sort(tobbs, getTobbOrdering());

    for (TypeOfBuildingBlock tobb : tobbs) {
      List<AttributeType> atList = Lists.newArrayList(tobbToAt.get(tobb));
      Collections.sort(atList, getAtOrdering());
      for (AttributeType at : atList) {
        generateSheet(wbContext, tobb, at);
      }
    }
    TimeseriesIntroSheetGenerator introSheetGenerator = new TimeseriesIntroSheetGenerator(wbContext, logoImage);
    introSheetGenerator.generateIntroduction();
  }

  private Ordering<TypeOfBuildingBlock> getTobbOrdering() {
    return Ordering.from(new UniversalTypeExpressionOrderFix()).onResultOf(new Function<TypeOfBuildingBlock, String>() {

      public String apply(TypeOfBuildingBlock input) {
        return input.getAssociatedClass().getSimpleName();
      }

    });
  }

  private Ordering<AttributeType> getAtOrdering() {
    return Ordering.natural().onResultOf(new Function<AttributeType, String>() {

      public String apply(AttributeType input) {
        return input.getName();
      }

    });
  }

  private void generateSheet(TimeseriesWorkbookContext wbContext, TypeOfBuildingBlock tobb, AttributeType at) {
    if (!hasPermissions(tobb, at)) {
      return;
    }

    Workbook workbook = wbContext.getWorkbook();

    String sheetName = MessageAccess.getStringOrNull(tobb.getAbbreviationValue()) + "-" + at.getName();
    sheetName = ExcelUtils.makeSheetNameValid(sheetName, workbook);
    Sheet sheet = workbook.createSheet(sheetName);

    TimeseriesSheetContext sheetContext = new TimeseriesSheetContext(sheet, tobb, at);
    wbContext.getSheetContexts().put(sheetName, sheetContext);

    preformatCells(sheet, wbContext.getCellStyles());

    addSheetTitle(sheetContext, wbContext.getCellStyles());

    addBuildingBlockType(sheetContext, wbContext.getCellStyles());

    addAttributeType(sheetContext, wbContext.getCellStyles());

    addDataHeader(sheetContext, wbContext.getCellStyles());
  }

  private boolean hasPermissions(TypeOfBuildingBlock tobb, AttributeType at) {
    Permissions currentPerms = UserContext.getCurrentPerms();

    boolean hasAtPerm = currentPerms.userHasAttrTypeGroupPermission(at.getAttributeTypeGroup(), AttributeTypeGroupPermissionEnum.READ);

    TypeOfFunctionalPermission tobbPerm = currentPerms.getFunctionalPermissionForBuildingBlock(tobb);
    boolean hasTobbPerm = currentPerms.userHasFunctionalPermission(tobbPerm);

    return hasTobbPerm && hasAtPerm;
  }

  private void preformatCells(Sheet sheet, Map<IteraExcelStyle, CellStyle> styles) {
    sheet.setDefaultColumnStyle(TimeseriesExcelImporter.BB_COL_NO, styles.get(IteraExcelStyle.DATA));
    sheet.setDefaultColumnStyle(TimeseriesExcelImporter.DATE_COL_NO, styles.get(IteraExcelStyle.DATA_DATE));
    sheet.setDefaultColumnStyle(TimeseriesExcelImporter.VALUE_COL_NO, styles.get(IteraExcelStyle.DATA));

    CellStyle workbookDefaultStyle = sheet.getWorkbook().getCellStyleAt((short) 0);
    for (int rowNum = 0; rowNum < TimeseriesExcelImporter.FIRST_DATA_ROW_NO; rowNum++) {
      Row row = sheet.createRow(rowNum);
      for (int colNum = 0; colNum < 3; colNum++) {
        row.createCell(colNum).setCellStyle(workbookDefaultStyle);
      }
    }
  }

  private void addSheetTitle(TimeseriesSheetContext sheetContext, Map<IteraExcelStyle, CellStyle> styles) {
    Sheet sheet = sheetContext.getSheet();
    String sheetTitle = MessageAccess.getStringOrNull(sheetContext.getTobb().getPluralValue()) + " - " + sheetContext.getAt().getName();

    Cell sheetHeaderCell = sheet.getRow(0).getCell(0);
    sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 2));

    sheetHeaderCell.setCellStyle(styles.get(IteraExcelStyle.HEADER));
    sheetHeaderCell.setCellValue(sheetTitle);
  }

  private void addBuildingBlockType(TimeseriesSheetContext sheetContext, Map<IteraExcelStyle, CellStyle> styles) {
    Sheet sheet = sheetContext.getSheet();

    Row bbTypeRow = sheet.getRow(TimeseriesExcelImporter.BB_TYPE_CELL_REF.getRow());
    bbTypeRow.setHeight((short) 0);
    Cell bbTypeCell = bbTypeRow.getCell(TimeseriesExcelImporter.BB_TYPE_CELL_REF.getCol());
    bbTypeCell.setCellStyle(styles.get(IteraExcelStyle.DATA_HIDDEN));
    bbTypeCell.setCellValue(sheetContext.getTobb().getAssociatedClass().getSimpleName());
  }

  private void addAttributeType(TimeseriesSheetContext sheetContext, Map<IteraExcelStyle, CellStyle> styles) {
    Sheet sheet = sheetContext.getSheet();

    Row atRow = sheet.getRow(TimeseriesExcelImporter.AT_CELL_REF.getRow());
    atRow.setHeight((short) 0);
    Cell atCell = atRow.getCell(TimeseriesExcelImporter.AT_CELL_REF.getCol());
    atCell.setCellStyle(styles.get(IteraExcelStyle.DATA_HIDDEN));
    atCell.setCellValue(sheetContext.getAt().getName());
  }

  private void addDataHeader(TimeseriesSheetContext sheetContext, Map<IteraExcelStyle, CellStyle> styles) {
    Row headerRow = sheetContext.getSheet().getRow(TimeseriesExcelImporter.FIRST_DATA_ROW_NO - 1);

    Cell bbHeaderCell = headerRow.getCell(TimeseriesExcelImporter.BB_COL_NO);
    bbHeaderCell.setCellStyle(styles.get(IteraExcelStyle.HEADER));
    bbHeaderCell.setCellValue(MessageAccess.getStringOrNull(sheetContext.getTobb().getValue()) + " " + MessageAccess.getStringOrNull("global.name"));

    Cell dateHeaderCell = headerRow.getCell(TimeseriesExcelImporter.DATE_COL_NO);
    dateHeaderCell.setCellStyle(styles.get(IteraExcelStyle.HEADER));
    dateHeaderCell.setCellValue(MessageAccess.getStringOrNull("global.date"));

    Cell valueHeaderCell = headerRow.getCell(TimeseriesExcelImporter.VALUE_COL_NO);
    valueHeaderCell.setCellStyle(styles.get(IteraExcelStyle.HEADER));
    valueHeaderCell.setCellValue(sheetContext.getAt().getName() + " " + MessageAccess.getStringOrNull("global.attributevalue"));
  }

}
