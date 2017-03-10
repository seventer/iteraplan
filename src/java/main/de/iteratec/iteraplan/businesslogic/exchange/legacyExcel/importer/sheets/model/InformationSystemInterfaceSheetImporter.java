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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.model;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellReference;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.BuildingBlockHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ExcelImportUtilities;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeRowData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.SheetImporter;
import de.iteratec.iteraplan.businesslogic.service.EntityService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.email.SubscriptionsUtil;


/**
 * A {@link SheetImporter} for importing the {@link InformationSystemInterface} properties.
 */
public class InformationSystemInterfaceSheetImporter extends SheetImporter<InformationSystemInterface> {
  private static final Pattern DIRECTION_PATTERN = Pattern.compile("^\\s*'?(<->|->|<-|-)\\s*$");

  public InformationSystemInterfaceSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
    super(locale, tob, processingLog);
  }

  /**
   * {@inheritDoc}
   * <p>
   *  does not follow the structure of the other building blocks due to the fact that
   *  information system interfaces must be treated partly as a BuildingBlock and as a relation
   * </p>
   */
  @Override
  protected boolean importRowIntoBB(LandscapeRowData rowData, LandscapeData landscapeData) {
    Map<String, Cell> buildingBlockRowData = rowData.getBuildingBlocks();
    // each interface contains now a name and a direction
    String name = getCellByKeyOrNull(getConstant(Constants.ATTRIBUTE_NAME), buildingBlockRowData);
    String direction = getCellByKeyOrNull(getConstant(Constants.ATTRIBUTE_TRANSPORT), buildingBlockRowData);

    Cell nameCell = buildingBlockRowData.get(getConstant(Constants.ATTRIBUTE_NAME));
    Cell descriptionCell = getDescriptionCell(buildingBlockRowData);

    InformationSystemInterface isi = createOrLoad(buildingBlockRowData);
    BuildingBlock clone = SubscriptionsUtil.prepareForSubscriptions(isi);

    if (!UserContext.getCurrentPerms().userHasBbInstanceWritePermission(isi)) {
      getProcessingLog().error("The building block with id={0} has write permissions restricted to users/user groups. No Changes will be applied.",
          isi.getId());
      return false;
    }

    if (isi != null) {

      if (name != null) {
        isi.setName(name);
      }
      if (direction != null) {
        Matcher matcher = DIRECTION_PATTERN.matcher(direction);
        if (matcher.find()) {
          isi.setDirection(matcher.group(1));
        }
        else {
          String directionCoordinates = ExcelImportUtilities.getCellRef(buildingBlockRowData.get(getConstant(Constants.ATTRIBUTE_TRANSPORT)));
          getProcessingLog().warn("Direction {0} in cell [{1}] not recognized", direction, directionCoordinates);
        }
      }
      String description = getDescription(buildingBlockRowData);
      if (description != null) {
        isi.setDescription(description.trim());
      }

      BuildingBlockHolder buildingBlockHolder = new BuildingBlockHolder(isi, nameCell, descriptionCell);
      buildingBlockHolder.setClone(clone);
      landscapeData.addBuildingBlock(buildingBlockHolder);

      landscapeData.addRelation(isi, buildingBlockRowData, rowData.getAttributes());
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  protected boolean isValidRow(Map<String, Cell> buildingBlockRowData) {
    String releaseA = getCellByKey(getConstant(Constants.BB_INTERFACE_INFORMATIONSYSTEMRELEASE_A), buildingBlockRowData);
    String releaseB = getCellByKey(getConstant(Constants.BB_INTERFACE_INFORMATIONSYSTEMRELEASE_B), buildingBlockRowData);

    Cell releaseANameCell = buildingBlockRowData.get(getConstant(Constants.BB_INTERFACE_INFORMATIONSYSTEMRELEASE_A));
    String releaseANameCellCoords = ExcelImportUtilities.getCellRef(releaseANameCell);

    Cell releaseBNameCell = buildingBlockRowData.get(getConstant(Constants.BB_INTERFACE_INFORMATIONSYSTEMRELEASE_B));
    String releaseBNameCellCoords = ExcelImportUtilities.getCellRef(releaseBNameCell);

    if (releaseANameCell == null && releaseBNameCell != null) {
      CellReference cellRelA = new CellReference(releaseBNameCell.getRowIndex(), releaseBNameCell.getColumnIndex() - 1);
      releaseANameCellCoords = cellRelA.formatAsString();
    }

    if (releaseBNameCell == null && releaseANameCell != null) {
      CellReference cellRelB = new CellReference(releaseANameCell.getRowIndex(), releaseANameCell.getColumnIndex() + 1);
      releaseBNameCellCoords = cellRelB.formatAsString();
    }

    if (!isNameSet(releaseA)) {
      getProcessingLog().warn("Release A in cell [{0}] is empty: not importing", releaseANameCellCoords);
      return false;
    }
    if (!isNameSet(releaseB)) {
      getProcessingLog().warn("Release B in cell [{0}] is empty: not importing", releaseBNameCellCoords);
      return false;
    }
    return true;
  }

  @Override
  protected InformationSystemInterface createBuildingBlockInstance() {
    return BuildingBlockFactory.createInformationSystemInterface();
  }

  @Override
  @SuppressWarnings("PMD.NoSpringFactory")
  protected EntityService<InformationSystemInterface, Integer> getService() {
    return SpringServiceFactory.getInformationSystemInterfaceService();
  }

}
