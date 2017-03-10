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
import de.iteratec.iteraplan.businesslogic.service.EntityService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.InformationSystem;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.InformationSystemRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.email.SubscriptionsUtil;


public class InformationSystemReleaseSheetImporter extends ReleaseBuildingBlockSheetImporter<InformationSystemRelease> {

  public InformationSystemReleaseSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
    super(locale, tob, processingLog);
  }

  @Override
  protected boolean importRowIntoBB(LandscapeRowData rowData, LandscapeData landscapeData) {
    Map<String, Cell> attributes = rowData.getAttributes();
    Map<String, Cell> buildingBlockRowData = rowData.getBuildingBlocks();

    String nameCombined = getName(buildingBlockRowData);
    String nameIs = getNameMain(nameCombined);
    String nameIsr = getNameRelease(nameCombined);

    //Get cell coordinates for IS name
    Cell nameCell = buildingBlockRowData.get(getConstant(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL));

    Cell descriptionCell = getDescriptionCell(buildingBlockRowData);

    InformationSystemRelease isr = createOrLoad(buildingBlockRowData);
    BuildingBlock clone = SubscriptionsUtil.prepareForSubscriptions(isr);

    if (isr != null) {
      if (!UserContext.getCurrentPerms().userHasBbInstanceWritePermission(isr)) {
        getProcessingLog().error("The building block with id={0} has write permissions restricted to users/user groups. No Changes will be applied.",
            isr.getId());
        return false;
      }

      InformationSystem is = isr.getInformationSystem();

      // Set up IS
      is.setName(nameIs.trim());

      // Set up ISR
      isr.setVersion(nameIsr.trim());
      String description = getDescription(buildingBlockRowData);
      if (description != null) {
        isr.setDescription(description.trim());
      }

      String elementName = "Information System \"" + nameIs + "\"";

      // Status; cell may be null, so called methods must be able to handle this
      Cell statusCell = buildingBlockRowData.get(getConstant(Constants.ATTRIBUTE_TYPEOFSTATUS));
      if (statusCell != null) {
        String statusCellCoords = ExcelImportUtilities.getCellRef(statusCell);
        String statusValue = ExcelImportUtilities.contentAsString(statusCell, getProcessingLog());
        TypeOfStatus status = getStatusFromString(statusValue, elementName, statusCellCoords);
        isr.setTypeOfStatus(status);
      }

      // Date range
      CellValueHolder dateFrom = new CellValueHolder(buildingBlockRowData.get(getConstant(Constants.TIMESPAN_PRODUCTIVE_FROM)));
      CellValueHolder dateTo = new CellValueHolder(buildingBlockRowData.get(getConstant(Constants.TIMESPAN_PRODUCTIVE_TO)));

      RuntimePeriod runtimePeriod = getRuntimePeriod(dateFrom, dateTo, elementName, isr.getRuntimePeriodNullSafe());
      if (runtimePeriod != null) {
        isr.setRuntimePeriodNullSafe(runtimePeriod);
      }

      BuildingBlockHolder buildingBlockHolder = new BuildingBlockHolder(isr, nameCell, descriptionCell);
      buildingBlockHolder.setClone(clone);

      // Save
      landscapeData.addBuildingBlock(buildingBlockHolder);
      landscapeData.addAttributes(isr, attributes);

      landscapeData.addRelation(isr, buildingBlockRowData, Maps.<String, Cell> newHashMap());
      return true;
    }
    else {
      return false;
    }
  }

  private TypeOfStatus getStatusFromString(String stringStatus, String elementName, String cellCoords) {
    TypeOfStatus status = null;

    if (stringStatus.equalsIgnoreCase(getConstant(TypeOfStatus.CURRENT.toString()))) {
      status = TypeOfStatus.CURRENT;
    }
    else if (stringStatus.equalsIgnoreCase(getConstant(TypeOfStatus.PLANNED.toString()))) {
      status = TypeOfStatus.PLANNED;
    }
    else if (stringStatus.equalsIgnoreCase(getConstant(TypeOfStatus.TARGET.toString()))) {
      status = TypeOfStatus.TARGET;
    }
    else if (stringStatus.equalsIgnoreCase(getConstant(TypeOfStatus.INACTIVE.toString()))) {
      status = TypeOfStatus.INACTIVE;
    }
    else if ("".equals(stringStatus)) {
      status = TypeOfStatus.INACTIVE;
      getProcessingLog().warn("Status must be set! Set to default (INACTIVE): {0} in cell [{1}]", elementName, cellCoords);
    }
    else {
      // Status is a Not NULL-value
      status = TypeOfStatus.INACTIVE;
      getProcessingLog().warn("Undefined Status value! Set to default (INACTIVE): {0} --> {1} in cell [{2}]", elementName, stringStatus, cellCoords);
    }

    return status;
  }

  @Override
  protected InformationSystemRelease createBuildingBlockInstance() {
    InformationSystem is = BuildingBlockFactory.createInformationSystem();
    InformationSystemRelease isr = BuildingBlockFactory.createInformationSystemRelease();
    is.addRelease(isr);

    return isr;
  }

  @Override
  @SuppressWarnings("PMD.NoSpringFactory")
  protected EntityService<InformationSystemRelease, Integer> getService() {
    return SpringServiceFactory.getInformationSystemReleaseService();
  }

}
