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
package de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.impl;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.read.LocalizedIteraplanMessage;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.AttributeTypeReader;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.BuildingBlockTypeNameMatcher;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.VirtualAttributeType;
import de.iteratec.iteraplan.businesslogic.exchange.elasticmi.write.metamodel.util.VirtualAttributeTypeCreator;
import de.iteratec.iteraplan.elasticmi.io.mapper.xls.XlsMappingUtil;
import de.iteratec.iteraplan.elasticmi.messages.Message.Severity;
import de.iteratec.iteraplan.elasticmi.messages.MessageListener;
import de.iteratec.iteraplan.elasticmi.util.NamedUtil;
import de.iteratec.iteraplan.model.Direction;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.attribute.EnumAT;


public final class ExcelAttributeTypeReader extends AttributeTypeReader {

  private static final int      DEFAULT_PERSISTENT_NAME_COLUMN = 0;

  private final XlsCoordinates  structuredTypeSheetIdentifier  = new XlsCoordinates("TypeExpression", 2, 0);
  private final XlsCoordinates  featureIdentifier              = new XlsCoordinates(".*:\\[[0-9,\\*].*[0-9,\\*]\\]:.*", 3, 0);
  private final XlsCoordinates  enumerationIdentifier          = new XlsCoordinates("EnumerationExpression", 2, 0);
  private final XlsCoordinates  enumerationAVIdentifier        = new XlsCoordinates("EnumerationExpression", 7, 0);

  private final MessageListener messageListener;

  private final Workbook        workbook;

  public ExcelAttributeTypeReader(Workbook workbook, MessageListener messageListener) {
    this.workbook = workbook;
    this.messageListener = messageListener;
  }

  /**{@inheritDoc}**/
  @Override
  public List<VirtualAttributeType> readVirtualAttributes() {
    Map<String, VirtualAttributeType> virtualAttributeTypes = Maps.newLinkedHashMap();
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      Sheet currentSheet = workbook.getSheetAt(i);
      if (isValidStructuredTypeSheet(currentSheet)) {
        readAttributes(currentSheet, virtualAttributeTypes);
      }
    }
    for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
      Sheet currentSheet = workbook.getSheetAt(i);
      if (isEnumerationSheet(currentSheet)) {
        readEnumerations(currentSheet, virtualAttributeTypes);
      }
    }
    return Lists.newArrayList(virtualAttributeTypes.values());
  }

  private void readAttributes(Sheet fromSheet, Map<String, VirtualAttributeType> vats) {
    Row featureRow = fromSheet.getRow(featureIdentifier.getRow());
    int i = 0;
    while (true) {
      Cell currentCell = featureRow.getCell(i);
      if (currentCell == null) {
        return;
      }
      i++;
      if (!currentCell.toString().trim().isEmpty()) {
        String atName = getAttributeName(currentCell.toString());
        VirtualAttributeType virtualAttributeType = vats.get(atName);
        if (virtualAttributeType == null) {
          virtualAttributeType = VirtualAttributeTypeCreator.createAttributeType(currentCell.toString());
        }
        if (virtualAttributeType != null) {
          vats.put(atName, virtualAttributeType);
          addTypeOfBuildingBlock(virtualAttributeType, fromSheet);
        }
      }
    }
  }

  private void addTypeOfBuildingBlock(VirtualAttributeType virtualAttributeType, Sheet currentSheet) {
    String tobbName = getStructuredTypeName(currentSheet.getRow(structuredTypeSheetIdentifier.getRow())
        .getCell(structuredTypeSheetIdentifier.getColumn()).toString());

    TypeOfBuildingBlock discoveredTobb = BuildingBlockTypeNameMatcher.getTypeOfBuildingBlockForPersistentName(tobbName);

    if (!virtualAttributeType.getAssociatedToBB().contains(discoveredTobb)) {
      virtualAttributeType.addAssociatedToBB(discoveredTobb);
    }
  }

  private String getAttributeName(String cellValue) {
    return XlsMappingUtil.INSTANCE.extractPersistentNameFromTechnicalFeatureString(cellValue);
  }

  private String getStructuredTypeName(String cellValue) {
    return cellValue.substring(0, cellValue.indexOf('{'));
  }

  private void readEnumerations(Sheet fromSheet, Map<String, VirtualAttributeType> vats) {
    String enumName = extractName(fromSheet);
    if (enumName == null) {
      return;
    }

    VirtualAttributeType attributeType = vats.get(enumName);
    if (attributeType == null) {
      //should only occur when EnumAT has not been assigned to any BBT within the corresponding sheets => no cardinality information available
      attributeType = new VirtualAttributeType(EnumAT.class, enumName, false, false);
      vats.put(enumName, attributeType);
    }

    int currentRow = enumerationAVIdentifier.getRow();
    Row row = fromSheet.getRow(currentRow);
    while (row != null && hasValues(row)) {
      attributeType.addEnumAV(row.getCell(DEFAULT_PERSISTENT_NAME_COLUMN).toString());
      currentRow++;
      row = fromSheet.getRow(currentRow);
    }
  }

  private String extractName(Sheet sheet) {
    String tempName = sheet.getRow(enumerationIdentifier.getRow()).getCell(enumerationIdentifier.getColumn()).toString();
    tempName = XlsMappingUtil.INSTANCE.extractPersistentNameFromTechnicalNameString(tempName);
    tempName = tempName.replaceAll(EnumAT.class.getName() + "\\.", "");
    if (tempName.endsWith(TypeOfStatus.class.getName())
        || tempName.endsWith(de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus.class.getName())
        || tempName.endsWith(Direction.class.getName())) {
      tempName = null;
    }
    return tempName;
  }

  private boolean hasValues(Row row) {
    if (row.getCell(DEFAULT_PERSISTENT_NAME_COLUMN) != null && !row.getCell(DEFAULT_PERSISTENT_NAME_COLUMN).toString().equals("")) {
      return true;
    }
    return false;
  }

  private boolean isValidStructuredTypeSheet(Sheet sheet) {
    Cell cell = sheet.getRow(structuredTypeSheetIdentifier.getRow()).getCell(structuredTypeSheetIdentifier.getColumn());
    boolean matchesStName = (cell != null && (cell.toString().endsWith(structuredTypeSheetIdentifier.getIdentifier()) || cell.toString().endsWith(
        ":RelationshipTypeExpression")));
    if (!matchesStName) {
      return false;
    }
    String stName = getStructuredTypeName(cell.getStringCellValue());
    if (!isValidStructuredType(stName)) {
      if (!NamedUtil.areSame("InformationFlow", stName)) {
        this.messageListener.onMessage(new LocalizedIteraplanMessage(Severity.WARNING, MESSAGE_KEY_UNSUPPOERTED_STRUCTURED_TYPE, stName));
      }
      return false;
    }
    return true;
  }

  private boolean isEnumerationSheet(Sheet sheet) {
    Cell cell = sheet.getRow(enumerationIdentifier.getRow()).getCell(enumerationIdentifier.getColumn());
    return (cell != null && cell.toString().endsWith(enumerationIdentifier.getIdentifier()));
  }

  private static final class XlsCoordinates {

    private String identifier;
    private int    row;
    private int    column;

    public XlsCoordinates(String identifier, int row, int column) {
      super();
      this.identifier = identifier;
      this.row = row;
      this.column = column;
    }

    String getIdentifier() {
      return identifier;
    }

    int getRow() {
      return row;
    }

    int getColumn() {
      return column;
    }
  }

}
