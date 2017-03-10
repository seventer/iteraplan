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
import de.iteratec.iteraplan.model.RuntimePeriod;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.TechnicalComponentRelease.TypeOfStatus;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.presentation.email.SubscriptionsUtil;


public class TechnicalComponentSheetImporter extends ReleaseBuildingBlockSheetImporter<TechnicalComponentRelease> {

  public TechnicalComponentSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
    super(locale, tob, processingLog);
  }

  @Override
  protected boolean importRowIntoBB(LandscapeRowData rowData, LandscapeData landscapeData) {
    Map<String, Cell> attributes = rowData.getAttributes();
    Map<String, Cell> buildingBlockRowData = rowData.getBuildingBlocks();

    //Get cell coordinates for TC name
    Cell nameCell = buildingBlockRowData.get(getConstant(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL));

    Cell descriptionCell = getDescriptionCell(buildingBlockRowData);

    String nameCombined = getName(buildingBlockRowData);
    String nameTc = getNameMain(nameCombined);
    String nameTcr = getNameRelease(nameCombined);

    TechnicalComponentRelease tcr = createOrLoad(buildingBlockRowData);
    BuildingBlock clone = SubscriptionsUtil.prepareForSubscriptions(tcr);

    if (!UserContext.getCurrentPerms().userHasBbInstanceWritePermission(tcr)) {
      getProcessingLog().error("The building block with id={0} has write permissions restricted to users/user groups. No Changes will be applied.",
          tcr.getId());
      return false;
    }

    if (tcr != null) {
      TechnicalComponent tc = tcr.getTechnicalComponent();

      tc.setName(nameTc.trim());
      tcr.setVersion(nameTcr.trim());
      String description = getDescription(buildingBlockRowData);
      if (description != null) {
        tcr.setDescription(description.trim());
      }

      String elementName = "Technical Component \"" + nameTc + "\"";

      // Status; cell may be null, so called methods must be able to handle this
      Cell statusCell = buildingBlockRowData.get(getConstant(Constants.ATTRIBUTE_TYPEOFSTATUS));
      if (statusCell != null) {
        String statusCellCoords = ExcelImportUtilities.getCellRef(statusCell);
        String statusValue = ExcelImportUtilities.contentAsString(statusCell, getProcessingLog());
        TypeOfStatus status = getStatusFromString(statusValue, elementName, statusCellCoords);
        tcr.setTypeOfStatus(status);
      }

      // Suitability for Interfaces; cell may be null, so called methods must be able to handle this
      Cell availableForISICell = buildingBlockRowData.get(getConstant(Constants.MU_AVAILABLE_FOR_CONNECTIONS));
      if (availableForISICell != null) {
        String availableForISICellCoords = ExcelImportUtilities.getCellRef(availableForISICell);
        String suitableForInterfaces = ExcelImportUtilities.contentAsString(
            buildingBlockRowData.get(getConstant(Constants.MU_AVAILABLE_FOR_CONNECTIONS)), getProcessingLog());
        tc.setAvailableForInterfaces(getSuitability(suitableForInterfaces, elementName, availableForISICellCoords));
      }

      // Date range
      CellValueHolder dateFrom = new CellValueHolder(buildingBlockRowData.get(getConstant(Constants.TIMESPAN_PRODUCTIVE_FROM)));
      CellValueHolder dateTo = new CellValueHolder(buildingBlockRowData.get(getConstant(Constants.TIMESPAN_PRODUCTIVE_TO)));

      RuntimePeriod runtimePeriod = getRuntimePeriod(dateFrom, dateTo, elementName, tcr.getRuntimePeriod());
      if (runtimePeriod != null) {
        tcr.setRuntimePeriodNullSafe(runtimePeriod);
      }

      BuildingBlockHolder buildingBlockHolder = new BuildingBlockHolder(tcr, nameCell, descriptionCell);
      buildingBlockHolder.setClone(clone);

      // Save
      landscapeData.addBuildingBlock(buildingBlockHolder);
      landscapeData.addAttributes(tcr, attributes);

      Map<String, Cell> contContent = buildingBlockRowData;
      Map<String, Cell> emptyAttributes = Maps.newHashMap();
      landscapeData.addRelation(tcr, contContent, emptyAttributes);
      return true;
    }
    else {
      return false;
    }
  }

  private TypeOfStatus getStatusFromString(String suitableForInterfaces, String happyName, String statusCellCoords) {

    if (StringUtils.isEmpty(suitableForInterfaces)) {
      return TypeOfStatus.INACTIVE;
    }
    else if (suitableForInterfaces.equalsIgnoreCase(getConstant(TypeOfStatus.CURRENT.toString()))) {
      return TypeOfStatus.CURRENT;
    }
    else if (suitableForInterfaces.equalsIgnoreCase(getConstant(TypeOfStatus.PLANNED.toString()))) {
      return TypeOfStatus.PLANNED;
    }
    else if (suitableForInterfaces.equalsIgnoreCase(getConstant(TypeOfStatus.TARGET.toString()))) {
      return TypeOfStatus.TARGET;
    }
    else if (suitableForInterfaces.equalsIgnoreCase(getConstant(TypeOfStatus.INACTIVE.toString()))) {
      return TypeOfStatus.INACTIVE;
    }
    else {
      // Status is a Not NULL-value
      getProcessingLog().warn("Undefined Status value! Set to default (INACTIVE): {0} --> {1} in cell [{2}]", happyName, suitableForInterfaces,
          statusCellCoords);
      return TypeOfStatus.INACTIVE;
    }
  }

  private boolean getSuitability(String value, String happyName, String availableForISICellCoords) {
    if (getConstant("global.true").equalsIgnoreCase(value)) {
      return true;
    }
    else if (getConstant("global.false").equalsIgnoreCase(value)) {
      return false;
    }
    else {

      getProcessingLog().warn("Undefined 'Available for Interfaces' value! Set to default (NO): {0} --> {1} in cell [{2}]", happyName, value,
          availableForISICellCoords);
      return false;
    }
  }

  @Override
  protected TechnicalComponentRelease createBuildingBlockInstance() {
    TechnicalComponent tc = BuildingBlockFactory.createTechnicalComponent();
    TechnicalComponentRelease tcr = BuildingBlockFactory.createTechnicalComponentRelease();
    tc.addRelease(tcr);

    return tcr;
  }

  @Override
  @SuppressWarnings("PMD.NoSpringFactory")
  protected EntityService<TechnicalComponentRelease, Integer> getService() {
    return SpringServiceFactory.getTechnicalComponentReleaseService();
  }

}
