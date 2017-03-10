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

import java.text.MessageFormat;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.FeatureExpressionReader;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.exception.MetamodelException;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;


/**
 * Import RelationshipExpressions and add them to the <b>meta model</b>.
 */
public class RelationSheetImporter {
  /** Logger. */
  private static final Logger LOGGER = Logger.getIteraplanLogger(RelationSheetImporter.class);

  private EditableMetamodel   metamodel;

  /**
   * Constructor.
   * 
   * @param metamodel metamodel to set up. This metamodel will be changed directly.
   */
  /*package*/RelationSheetImporter(EditableMetamodel metamodel) {
    this.metamodel = metamodel;
  }

  /**
   * import the relationship expression and add it to the meta model.
   * 
   * If there are errors, these errors are logged, but the import will continue.
   * 
   * @param sheetContext sheet to import. The expression type of this sheet <b>must</b> be 
   *        a {@link RelationshipExpression}.
   */
  /* package*/void importMetamodelRelationTypeSpecification(Sheet sheet) {
    Row featureTypeRow = sheet.getRow(FEATURE_TYPE_ROW_NO);
    Cell fromCell = featureTypeRow.getCell(0);
    Cell toCell = featureTypeRow.getCell(1);

    FeatureExpressionReader fromFER = new FeatureExpressionReader(ExcelUtils.getStringCellValue(fromCell));
    FeatureExpressionReader toFER = new FeatureExpressionReader(ExcelUtils.getStringCellValue(toCell));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Sheet {0}: Create relationship: FROM: {1} --> TO: {2}", sheet.getSheetName(), fromFER.toString(), toFER.toString());
    }

    String relationshipName = createRelationshipName(fromFER, toFER);
    UniversalTypeExpression fromType = metamodel.findUniversalTypeByPersistentName(fromFER.getTypeName());
    UniversalTypeExpression toType = metamodel.findUniversalTypeByPersistentName(toFER.getTypeName());

    try {
      RelationshipExpression rel = metamodel.createRelationship(relationshipName, //
          fromType, toFER.getPersistentName(), toFER.getLowerBound(), toFER.getUpperBound(), //
          toType, fromFER.getPersistentName(), fromFER.getLowerBound(), fromFER.getUpperBound());

      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("created RelationshipExpression: {0}", rel.toString());
      }
    } catch (MetamodelException e) {
      LOGGER.error("caught Metamodel Exception:", e);
      String message = e.getMessage();
      if (fromType == null || toType == null) {
        String missingType = (fromType == null) ? fromFER.getPersistentName() : toFER.getPersistentName();
        message = MessageFormat.format("Sheet \"{0}\": Could not import Relation \"{1}-{2}\". Type for {3} doesn't exist.", sheet.getSheetName(),
            fromFER.getPersistentName(), toFER.getPersistentName(), missingType);
      }
      throw new MetamodelException(ElasticeamException.GENERAL_ERROR, message, e);
    }
  }

  /**
   * @param fromFER
   * @param toFER
   * @return unique name for the relationship
   */
  private String createRelationshipName(FeatureExpressionReader fromFER, FeatureExpressionReader toFER) {
    if (fromFER.getPersistentName().compareTo(toFER.getPersistentName()) < 0) {
      return fromFER.getPersistentName() + "-" + toFER.getPersistentName();
    }
    else {
      return toFER.getPersistentName() + "-" + fromFER.getPersistentName();
    }
  }
}
