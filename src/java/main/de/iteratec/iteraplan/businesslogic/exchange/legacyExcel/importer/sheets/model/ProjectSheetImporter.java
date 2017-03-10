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

import org.apache.poi.ss.usermodel.Cell;

import com.google.common.collect.Maps;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.BuildingBlockHolder;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.CellValueHolder;
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
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.email.SubscriptionsUtil;


public class ProjectSheetImporter extends SheetImporter<Project> {

  public ProjectSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
    super(locale, tob, processingLog);
  }

  @Override
  protected boolean importRowIntoBB(LandscapeRowData rowData, LandscapeData landscapeData) {
    Map<String, Cell> attributes = rowData.getAttributes();
    Map<String, Cell> buildingBlockRowData = rowData.getBuildingBlocks();

    Cell nameCell = buildingBlockRowData.get(getConstant(Constants.BB_PROJECT_PLURAL));
    Cell descriptionCell = getDescriptionCell(buildingBlockRowData);

    // each entity needs a unique name
    String name = getName(buildingBlockRowData);

    Project entity = createOrLoad(buildingBlockRowData);
    BuildingBlock clone = SubscriptionsUtil.prepareForSubscriptions(entity);

    if (!UserContext.getCurrentPerms().userHasBbInstanceWritePermission(entity)) {
      getProcessingLog().error("The building block with id={0} has write permissions restricted to users/user groups. No Changes will be applied.",
          entity.getId());
      return false;
    }

    if (entity != null) {
      entity.setName(name.trim());
      String description = getDescription(buildingBlockRowData);
      if (description != null) {
        entity.setDescription(description.trim());
      }

      CellValueHolder dateFrom = new CellValueHolder(buildingBlockRowData.get(getConstant(Constants.TIMESPAN_LIFETIME_FROM)));
      CellValueHolder dateTo = new CellValueHolder(buildingBlockRowData.get(getConstant(Constants.TIMESPAN_LIFETIME_TO)));

      String elementName = "Project \"" + name + "\"";

      RuntimePeriod runtimePeriod = getRuntimePeriod(dateFrom, dateTo, elementName, entity.getRuntimePeriod());
      if (runtimePeriod != null) {
        entity.setRuntimePeriodNullSafe(runtimePeriod);
      }

      BuildingBlockHolder buildingBlockHolder = new BuildingBlockHolder(entity, nameCell, descriptionCell);
      buildingBlockHolder.setClone(clone);

      landscapeData.addBuildingBlock(buildingBlockHolder);
      landscapeData.addAttributes(entity, attributes);

      landscapeData.addRelation(entity, buildingBlockRowData, Maps.<String, Cell> newHashMap());
      getService().saveOrUpdate(entity); // save here already to avoid problems as in ITERAPLAN-2208
      return true;
    }
    else {
      return false;
    }
  }

  @Override
  protected boolean isValidRow(Map<String, Cell> buildingBlockRowData) {
    Cell nameCell = buildingBlockRowData.get(getConstant(getTypeOfBuildingBlockOnSheet().getPluralValue()));

    if (doesEntityExistByNameWithDifferentID(nameCell, getId(buildingBlockRowData))) {
      return false;
    }

    String name = getName(buildingBlockRowData);
    if (!isNameSet(name)) {
      /* for later use, if we want to make the imported excel file downloadable. Don't remove.
      BuildingBlockHolder errorHolder = new BuildingBlockHolder(null, nameCell, null);
      errorHolder.addNameProblem(ProblemMarker.ERROR, "Empty building block name: Cannot import this line");
      landscapeData.addBuildingBlock(errorHolder);
       */

      // check whether there is *any* non-empty data in this row; if yes, log warning and abort
      for (Cell cell : buildingBlockRowData.values()) {
        if (!ExcelImportUtilities.isEmpty(cell)) {
          getProcessingLog().warn(SKIPPING_ROW + "; Name must not be empty", getCellRow(buildingBlockRowData));
          return false;
        }
      }
      // fail silently, as this row contains no data anyway
      return false;
    }

    // just like hierarchical entities, projects may not contain ":" characters
    if (!isNameValid(name)) {
      /* for later use, if we want to make the imported excel file downloadable. Don't remove.
      BuildingBlockHolder errorHolder = new BuildingBlockHolder(null, nameCell, null);
      errorHolder.addNameProblem(ProblemMarker.ERROR, "Empty building block name: Cannot import this line");
      landscapeData.addBuildingBlock(errorHolder);
       */

      getProcessingLog().warn(SKIPPING_ROW + "; Buildingblock names must not contain the \":\" character: {1} in cell [{2}]",
          getCellRow(buildingBlockRowData), name, ExcelImportUtilities.getCellRef(nameCell));
      return false;
    }
    return true;
  }

  @Override
  protected Project createBuildingBlockInstance() {
    return BuildingBlockFactory.createProject();
  }

  @Override
  @SuppressWarnings("PMD.NoSpringFactory")
  protected EntityService<Project, Integer> getService() {
    return SpringServiceFactory.getProjectService();
  }

}
