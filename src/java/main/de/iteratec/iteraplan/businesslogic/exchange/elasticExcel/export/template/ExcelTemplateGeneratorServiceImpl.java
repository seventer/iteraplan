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
package de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.Resource;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelGeneratorUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ToOneRelationFilter;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.SheetContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.WorkbookContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanErrorMessages;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.metamodel.EnumerationExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.Metamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpressionOrder;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpressionOrderFix;


/**
 * A default implementation of the {@link ExcelTemplateGeneratorService} for Excel template 
 * generation.
 */
public class ExcelTemplateGeneratorServiceImpl implements ExcelTemplateGeneratorService {

  private static final Logger LOGGER = Logger.getIteraplanLogger(ExcelTemplateGeneratorServiceImpl.class);

  /** The empty Excel template containing only the macros. */
  private Resource            excel2003Template;
  private Resource            excel2007Template;
  private Resource            logoImage;

  /**
   * Constructor.
   * 
   * @param excel2003Template the Excel 2003 template
   * @param excel2007Template the Excel 2007 template
   */
  public ExcelTemplateGeneratorServiceImpl(Resource excel2003Template, Resource excel2007Template, Resource logoImage) {
    this.excel2003Template = excel2003Template;
    this.excel2007Template = excel2007Template;
    this.logoImage = logoImage;
  }

  /**{@inheritDoc}**/
  public WorkbookContext generateTemplateExcel2003(Metamodel metamodel) {
    LOGGER.debug("Starting Excel 2003 Template generation.");
    return generateTemplate(metamodel, this.excel2003Template);
  }

  /**{@inheritDoc}**/
  public WorkbookContext generateTemplateExcel2007(Metamodel metamodel) {
    LOGGER.debug("Starting Excel 2007 Template generation.");
    return generateTemplate(metamodel, this.excel2007Template);
  }

  /**
   * @param metamodel The meta model to export
   * @param excelTemplate The Template-File to use
   * @return the workbook to be downloaded by the user
   */
  private WorkbookContext generateTemplate(Metamodel metamodel, Resource excelTemplate) {
    Workbook workbook = null;
    try {
      workbook = ExcelUtils.openExcelFile(excelTemplate.getInputStream());
    } catch (IOException iex) {
      LOGGER.debug(iex);
      throw new IteraplanTechnicalException(IteraplanErrorMessages.INVALID_EXCEL_TEMPLATE, "The Excel Template file could not be read.", iex);
    }

    Map<IteraExcelStyle, CellStyle> styles = ExcelStylesCreator.createStyles(workbook);
    WorkbookContext wbContext = new WorkbookContext(workbook, styles);

    generateSheets(metamodel, wbContext);
    LOGGER.debug("Excel-Template was generated.");

    DataExportIntroSheetGenerator introGen = new DataExportIntroSheetGenerator(wbContext, logoImage);
    introGen.generateIntroduction();

    return wbContext;
  }

  /**
   * Generate sheets for the {@link SubstantialTypeExpression}s and {@link RelationshipTypeExpression}s.
   *
   * @param metamodel the metamodel
   * @param wbContext the workbook context
   */
  private void generateSheets(Metamodel metamodel, WorkbookContext wbContext) {
    List<UniversalTypeExpression> universalTypes = Lists.newArrayList();
    universalTypes.addAll(metamodel.getSubstantialTypes());
    universalTypes.addAll(metamodel.getRelationshipTypes());

    Collections.sort(universalTypes, new Comparator<UniversalTypeExpression>() {
      private UniversalTypeExpressionOrder order = new UniversalTypeExpressionOrderFix();

      public int compare(UniversalTypeExpression type1, UniversalTypeExpression type2) {
        return order.compareNames(type1.getPersistentName(), type2.getPersistentName());
      }
    });

    List<TypeSheetGenerator> generators = Lists.newArrayList();
    for (UniversalTypeExpression ute : universalTypes) {
      LOGGER.debug("Generating sheet for type: {0}", ute);
      TypeSheetGenerator generator = new TypeSheetGenerator(wbContext, ute);
      generators.add(generator);
      SheetContext sheetContext = generator.generateSheet();
      wbContext.addSheetContext(sheetContext);
    }

    for (TypeSheetGenerator generator : generators) {
      LOGGER.debug("Adding dropdowns to sheet for type: {0}", generator.getTypeExpression());
      generator.addDropdowns();
    }

    List<RelationshipExpression> relationships = metamodel.getRelationships();
    for (RelationshipExpression re : relationships) {
      List<RelationshipEndExpression> relationshipEnds = re.getRelationshipEnds();
      if (Iterables.any(relationshipEnds, ToOneRelationFilter.INSTANCE)) {
        continue;
      }

      LOGGER.debug("Generating sheet for relation: {0}", re);
      RelationshipSheetGenerator relationSheetGenerator = new RelationshipSheetGenerator(wbContext, re);
      SheetContext sheetContext = relationSheetGenerator.generateSheet();
      wbContext.addSheetContext(sheetContext);
    }

    List<EnumerationExpression> enums = metamodel.getEnumerationTypes();
    for (EnumerationExpression ete : enums) {
      LOGGER.debug("Generating sheet for enum: {0}: {1} ", ete, ete.getLiterals().toString());
      if (ete.getPersistentName() != null) {
        EnumSheetGenerator enumSheetGenerator = new EnumSheetGenerator(wbContext, ete);
        SheetContext sheetContext = enumSheetGenerator.generateSheet();
        wbContext.addSheetContext(sheetContext);
      }
    }

    adjustSheetColumnWidths(wbContext);
  }

  private void adjustSheetColumnWidths(WorkbookContext wbContext) {
    Map<String, Integer> widthsMap = Maps.newHashMap();
    widthsMap.put("id", Integer.valueOf(1200));
    widthsMap.put("name", Integer.valueOf(7000));
    widthsMap.put("description", Integer.valueOf(9000));

    for (SheetContext sheetContext : wbContext.getSheetContexts()) {
      ExcelGeneratorUtils.adjustColumnWidths(sheetContext, widthsMap);
    }
  }
}
