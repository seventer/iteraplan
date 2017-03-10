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

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.LandscapeRowData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.ProcessingLog;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.importer.sheets.SheetImporter;
import de.iteratec.iteraplan.businesslogic.service.EntityService;
import de.iteratec.iteraplan.businesslogic.service.SpringServiceFactory;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BuildingBlockFactory;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;


public class BusinessMappingSheetImporter extends SheetImporter<BusinessMapping> {

  public BusinessMappingSheetImporter(Locale locale, TypeOfBuildingBlock tob, ProcessingLog processingLog) {
    super(locale, tob, processingLog);
  }

  // does not follow the structure of the other building blocks due to the fact that they
  // must be treated partly as a BuildingBlock and as a relation
  @Override
  protected boolean importRowIntoBB(LandscapeRowData rowData, LandscapeData landscapeData) {
    BusinessMapping bm = createBuildingBlockInstance();
    landscapeData.addRelation(bm, rowData.getBuildingBlocks(), rowData.getAttributes());
    return true;
  }

  @Override
  protected boolean isValidRow(Map<String, Cell> buildingBlockRowData) {
    // each entity needs a unique name
    String bpName = getCellByKey(getConstant(Constants.BB_BUSINESSPROCESS_PLURAL), buildingBlockRowData);
    String productName = getCellByKey(getConstant(Constants.BB_PRODUCT_PLURAL), buildingBlockRowData);
    String buName = getCellByKey(getConstant(Constants.BB_BUSINESSUNIT_PLURAL), buildingBlockRowData);
    String isrName = getCellByKey(getConstant(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL), buildingBlockRowData);

    if ((!isNameSet(bpName) && !isNameSet(buName) && !isNameSet(productName)) || !isNameSet(isrName)) {
      getProcessingLog().warn("At least one Building Block specified in the Business Mapping has an invalid name. " + SKIPPING_ROW,
          getCellRow(buildingBlockRowData));
      return false;
    }
    return true;
  }

  @Override
  protected BusinessMapping createBuildingBlockInstance() {
    return BuildingBlockFactory.createBusinessMapping();
  }

  @Override
  @SuppressWarnings("PMD.NoSpringFactory")
  protected EntityService<BusinessMapping, Integer> getService() {
    return SpringServiceFactory.getBusinessMappingService();
  }

}
