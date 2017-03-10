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
import static de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.export.template.AbstractSheetGenerator.OPPOSITE_TYPE_ROW_NO;

import java.math.BigInteger;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.ExcelUtils;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.util.FeatureExpressionReader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.ColumnContext;
import de.iteratec.iteraplan.businesslogic.exchange.elasticExcel.workbookdata.SheetContext;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.elasticeam.exception.ElasticeamException;
import de.iteratec.iteraplan.elasticeam.exception.MetamodelException;
import de.iteratec.iteraplan.elasticeam.metamodel.DataTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.FeatureExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipEndExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.RelationshipExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.SubstantialTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.TypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;


/**
 * Import SubstantialTypes and RelationshipTypes and build the <b>meta model</b>.
 */
public class UniversaltypeSheetImporter {

  /** Logger. */
  private static final Logger LOGGER = Logger.getIteraplanLogger(UniversaltypeSheetImporter.class);

  private EditableMetamodel   metamodel;

  /**
   * Constructor.
   * 
   * @param metamodel metamodel to set up. This metamodel will be changed directly in 
   *                  {@link #importMetamodelTypeSpecification(SheetContext)}.
   */
  /* package */UniversaltypeSheetImporter(EditableMetamodel metamodel) {
    this.metamodel = metamodel;
  }

  /**
   * import the type specification and add it to the meta model.
   * 
   * If there are errors, these errors are logged, but the import will continue.
   * 
   * @param sheetContext sheet to import. The expression type of this sheet <b>must</b> be 
   *        a {@link UniversalTypeExpression}. 
   */
  /* package */void importMetamodelTypeSpecification(SheetContext sheetContext) {
    Sheet sheet = sheetContext.getSheet();
    UniversalTypeExpression entityType = (UniversalTypeExpression) sheetContext.getExpression();

    Row featureTypeRow = sheet.getRow(FEATURE_TYPE_ROW_NO);
    Row oppositeTypeRow = sheet.getRow(OPPOSITE_TYPE_ROW_NO);
    // not interested in preHeaderRow (used for time spans), and in header row (display name)

    for (int colNo = 0; true; colNo++) {
      Cell featureTypeCell = featureTypeRow.getCell(colNo);
      if (featureTypeCell == null || featureTypeCell.getCellType() != Cell.CELL_TYPE_STRING) {
        break;
      }
      Cell oppositeTypeCell = oppositeTypeRow.getCell(colNo);

      try {
        FeatureExpression<?> feature = importFeature(entityType, featureTypeCell, oppositeTypeCell);
        ColumnContext sheetColumn = new ColumnContext(feature, featureTypeRow.getCell(colNo));
        sheetContext.addColumn(sheetColumn);
      } catch (MetamodelException e) {
        String message = MessageFormat.format("Metamodel exception in sheet {0}, near cell {1}: {2}  ", sheetContext.getSheetName(),
            ExcelUtils.getFullCellName(featureTypeCell), e.getMessage());
        throw new MetamodelException(ElasticeamException.GENERAL_ERROR, message, e);
      }
    }
  }

  /**
   * @param entityType
   * @param featureTypeCell
   * @param oppositeTypeCell
   * @return the feature defined in the given feature type cell.
   */
  private FeatureExpression<?> importFeature(UniversalTypeExpression entityType, Cell featureTypeCell, Cell oppositeTypeCell) {
    String featureTypeString = ExcelUtils.getStringCellValue(featureTypeCell);
    FeatureExpressionReader fer = new FeatureExpressionReader(featureTypeString);

    String oppositeTypeString = ExcelUtils.getStringCellValue(oppositeTypeCell);
    FeatureExpressionReader oppositeFER = null;
    if (!StringUtils.isEmpty(oppositeTypeString)) {
      oppositeFER = new FeatureExpressionReader(oppositeTypeString);
    }

    TypeExpression featureType = findOrCreateTypeExpression(fer.getTypeName());
    FeatureExpression<?> feature = null;

    if (isDefaultProperty(fer.getPersistentName())) {
      feature = entityType.findPropertyByName(fer.getPersistentName());
    }
    else if (featureType instanceof SubstantialTypeExpression) {
      // => relation from entity to feature
      feature = createRelationshipEnd(entityType, (SubstantialTypeExpression) featureType, fer, oppositeFER);
    }
    else {
      feature = entityType.findFeatureByName(fer.getPersistentName());
      if (feature == null) {
        feature = metamodel.createProperty(entityType, fer.getPersistentName(), fer.getLowerBound(), fer.getUpperBound(),
            (DataTypeExpression) featureType);
      }
    }
    return feature;
  }

  /**
   * @param qualifiedTypeName
   * @return the {@link TypeExpression} representing the given qualified name.
   */
  private TypeExpression findOrCreateTypeExpression(String qualifiedTypeName) {
    String qn = qualifiedTypeName;
    if ("boolean".equals(qualifiedTypeName)) {
      qn = "java.lang.Boolean";
    }
    else if (Integer.class.getName().equals(qualifiedTypeName)) {
      qn = BigInteger.class.getName();
    }

    TypeExpression attributeType = metamodel.findTypeByPersistentName(qn);
    if (attributeType == null) {

      throw new ElasticeamException(ElasticeamException.GENERAL_ERROR, "No type with the persistent name \"" + qn + "\" exists in the metamodel.");
    }

    return attributeType;
  }

  /** ignore default properties id, name, description. */
  private boolean isDefaultProperty(String propertyName) {
    return (MixinTypeNamed.DESCRIPTION_PROPERTY.getPersistentName().equals(propertyName) || //
        MixinTypeNamed.NAME_PROPERTY.getPersistentName().equals(propertyName) || //
    UniversalTypeExpression.ID_PROPERTY.getPersistentName().equals(propertyName));
  }

  /**
   * create relation from entity to feature
   * 
   * @param entityType
   * @param featureType
   * @param fer
   * @param oppositeFER
   * @return the created relation.
   */
  private RelationshipEndExpression createRelationshipEnd(UniversalTypeExpression entityType, SubstantialTypeExpression featureType,
                                                          FeatureExpressionReader fer, FeatureExpressionReader oppositeFER) {
    RelationshipEndExpression relEnd = null;
    String relationshipName = createRelationshipName(entityType.getPersistentName(), featureType.getPersistentName());

    if (oppositeFER == null) {
      LOGGER.error("Missing opposite definition for relationship from {0} to {1}", entityType.getPersistentName(), featureType.getPersistentName());
    }
    else {
      RelationshipExpression rel = metamodel.createRelationship(relationshipName, //
          entityType, fer.getPersistentName(), fer.getLowerBound(), fer.getUpperBound(), //
          featureType, oppositeFER.getPersistentName(), oppositeFER.getLowerBound(), oppositeFER.getUpperBound());
      relEnd = rel.findRelationshipEndByPersistentName(fer.getPersistentName());
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("created relationship: {0}: {1}[{2}]", relationshipName, featureType.getName(), relEnd.getPersistentName());
    }
    return relEnd;
  }

  /**
   * @param fromName
   * @param toName
   * @return unique name for the relationship 
   */
  private String createRelationshipName(String fromName, String toName) {
    if (fromName.compareTo(toName) < 0) {
      return fromName + "-" + toName;
    }
    else {
      return toName + "-" + fromName;
    }
  }

}
