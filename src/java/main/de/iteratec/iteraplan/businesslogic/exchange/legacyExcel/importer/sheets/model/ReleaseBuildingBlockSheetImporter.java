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

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ExcelImportUtilities;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.SheetImporter;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public abstract class ReleaseBuildingBlockSheetImporter<T extends BuildingBlock & Sequence<T>> extends SheetImporter<T> {

  protected ReleaseBuildingBlockSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
    super(locale, tob, processingLog);
  }

  /**
   * gets if a Release's version is valid. May be blank
   */
  protected boolean isReleaseVersionValid(String name) {
    return name != null;
  }

  /**
   * release names and version names must not contain "#"
   * 
   * @param name
   * @return true if valid
   */
  protected boolean isReleaseNameValid(String name) {
    if (name == null) {
      return false;
    }
    if (name.contains(Constants.VERSIONSEP.trim())) {
      return false;
    }
    return true;
  }

  /**
   * Return the main name for a TCR / ISR.
   */
  protected String getNameMain(String nameCombined) {

    return GeneralHelper.getPartsOfReleaseName(nameCombined)[0];
  }

  /**
   * Return the release version/name for TCR / ISR BBTs.
   */
  protected String getNameRelease(String nameCombined) {
    String version = GeneralHelper.getPartsOfReleaseName(nameCombined)[1];
    return StringUtils.defaultString(version);
  }

  @Override
  protected boolean isValidRow(Map<String, Cell> buildingBlockRowData) {
    Cell nameCell = buildingBlockRowData.get(getConstant(getTypeOfBuildingBlockOnSheet().getPluralValue()));

    if (doesEntityExistByNameWithDifferentID(nameCell, getId(buildingBlockRowData))) {
      return false;
    }

    String nameCombined = getName(buildingBlockRowData);
    String nameMain = getNameMain(nameCombined);
    String nameRelease = getNameRelease(nameCombined);
    if ((!isNameSet(nameMain)) || (!isReleaseVersionValid(nameRelease))) {
      /* for later use, if we want to make the imported excel file downloadable. Don't remove.
      BuildingBlockHolder errorHolder = new BuildingBlockHolder(null, nameCell, null);
      errorHolder.addNameProblem(ProblemMarker.ERROR, "Empty building block name: Cannot import this line");
      landscapeData.addBuildingBlock(errorHolder);
       */

      // check whether there is *any* non-empty data in this row; if yes, log warning and abort
      for (Cell cell : buildingBlockRowData.values()) {
        if (! ExcelImportUtilities.isEmpty(cell)) {
          getProcessingLog().warn(SKIPPING_ROW + "; Name must not be empty", getCellRow(buildingBlockRowData));
          return false;
        }
      }
      // fail silently, as this row contains no data anyway
      return false;
    }

    if (!isReleaseNameValid(nameMain) || !isReleaseNameValid(nameRelease) || !isNameValid(nameMain) || !isNameValid(nameRelease)) {
      /* for later use, if we want to make the imported excel file downloadable. Don't remove.
      BuildingBlockHolder errorHolder = new BuildingBlockHolder(null, nameCell, null);
      errorHolder.addNameProblem(ProblemMarker.ERROR, "Empty building block name: Cannot import this line");
      landscapeData.addBuildingBlock(errorHolder);
       */

      getProcessingLog().warn(SKIPPING_ROW + "; Buildingblock names must not contain \"#\" or \":\" characters: {1} in cell [{2}]",
          getCellRow(buildingBlockRowData), nameCombined, ExcelImportUtilities.getCellRef(nameCell));
      return false;
    }
    return true;
  }
}
