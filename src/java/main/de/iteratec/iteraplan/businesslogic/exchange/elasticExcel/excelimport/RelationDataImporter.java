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

import static de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.AbstractSheetGenerator.FEATURE_TYPE_ROW_NO;
import static de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.AbstractSheetGenerator.FIRST_DATA_ROW_NO;
import static de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.AbstractSheetGenerator.SHEET_TYPE_ROW_NO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.FeatureExpressionReader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.TypeExpressionReader;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;


/**
 * Import RelationshipExpressions and add them to the <b>model</b>.
 */
public class RelationDataImporter extends AbstractDataImporter {

  /** Logger. */
  private static final Logger LOGGER = Logger.getIteraplanLogger(RelationDataImporter.class);

  private EditableMetamodel   metamodel;
  private Model               model;

  /**
   * Constructor.
   * 
   * @param metamodel
   * @param model
   */
  public RelationDataImporter(EditableMetamodel metamodel, Model model) {
    this.model = model;
    this.metamodel = metamodel;
  }

  /**
   * do the import of the relations on the given sheet.
   * 
   * @param sheet
   */
  public void importRelationData(Sheet sheet) {
    LOGGER.info("    == Pass 4: Sheet {0}", sheet.getSheetName());

    String typeString = ExcelUtils.getStringCellValue(sheet.getRow(SHEET_TYPE_ROW_NO).getCell(0));
    TypeExpressionReader ter = new TypeExpressionReader(typeString);

    if (!"RelationshipExpression".equals(ter.getMetaTypeName())) { // TODO tse: hard coded name ok?
      logError("Unexpected sheet {0} with expression: {1}", sheet.getSheetName(), ter.getMetaTypeName());
      return;
    }

    String fromString = ExcelUtils.getStringCellValue(sheet.getRow(FEATURE_TYPE_ROW_NO).getCell(0));
    String toString = ExcelUtils.getStringCellValue(sheet.getRow(FEATURE_TYPE_ROW_NO).getCell(1));

    FeatureExpressionReader fromFER = new FeatureExpressionReader(fromString);
    FeatureExpressionReader toFER = new FeatureExpressionReader(toString);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Create Relation: From {0} to {1}", fromFER.getTypeName(), toFER.getTypeName());
    }

    SubstantialTypeExpression fromType = (SubstantialTypeExpression) metamodel.findTypeByPersistentName(fromFER.getTypeName());
    SubstantialTypeExpression toType = (SubstantialTypeExpression) metamodel.findTypeByPersistentName(toFER.getTypeName());

    if (fromType != null && toType != null) {
      RelationshipEndExpression expr = (RelationshipEndExpression) fromType.findFeatureByPersistentName(toFER.getPersistentName());

      if (expr == null) {
        RelationshipExpression rel = metamodel.createRelationship(ter.getTypeName(), //
            toType, fromFER.getPersistentName(), fromFER.getLowerBound(), fromFER.getUpperBound(), //
            fromType, toFER.getPersistentName(), toFER.getLowerBound(), toFER.getUpperBound());
        expr = rel.findRelationshipEndByPersistentName(toFER.getPersistentName());
      }

      if (expr != null) {
        boolean hasMore = true;
        for (int rowNum = FIRST_DATA_ROW_NO; hasMore; rowNum++) {
          Row row = sheet.getRow(rowNum);
          hasMore = importRelation(row, fromType, toType, expr);
        }
      }
    }
    else {
      String missingType = (fromType == null) ? fromFER.getPersistentName() : toFER.getPersistentName();
      logError("Sheet \"{0}\": Could not import Relation \"{1}-{2}\". Type for {3} doesn't exist.", sheet.getSheetName(),
          fromFER.getPersistentName(), toFER.getPersistentName(), missingType);
    }
  }

  /**
   * @param row
   * @param ferFrom
   * @param ferTo
   * 
   * @return true if this method did an import, false if it hit an empty row.
   */
  private boolean importRelation(Row row, SubstantialTypeExpression fromType, SubstantialTypeExpression toType, RelationshipEndExpression expr) {
    if (row == null || row.getCell(0) == null || ExcelUtils.isEmptyCell(row.getCell(0))) {
      return false;
    }

    String fromName = getName(row.getCell(0), fromType);
    String toName = getName(row.getCell(1), toType);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Relation: From {0}[{1}] to {2}[{3}] via {4}[{5}]", fromType.getPersistentName(), fromName, //
          toType.getPersistentName(), toName, //
          expr.getType().getPersistentName(), expr.getName());
    }

    UniversalModelExpression fromInstance = model.findByName(fromType, fromName);
    UniversalModelExpression toInstance = model.findByName(toType, toName);

    if (fromInstance == null || toInstance == null) {
      logError("Sheet \"{0}\", row {1}: fromInstance {2} or toInstance {3} is null!", row.getSheet().getSheetName(),
          Integer.valueOf(row.getRowNum() + 1), fromInstance, toInstance);
    }
    else {
      LOGGER.debug("About to create relation: from {0} to {1}", fromInstance, toInstance);
      fromInstance.connect(expr, toInstance);
    }

    return true;
  }

  private String getName(Cell cell, SubstantialTypeExpression type) {
    if (isReleaseName(type, MixinTypeNamed.NAME_PROPERTY)) {
      return getNormalizedReleaseName(cell);
    }
    else {
      return ExcelUtils.getStringCellValue(cell);
    }
  }

  private void logError(String format, Object... params) {
    logError(LOGGER, format, params);
  }
}
