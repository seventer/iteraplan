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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.reports;

import java.util.HashSet;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.model.BusinessDomain;
import de.iteratec.iteraplan.model.BusinessFunction;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.persistence.dao.AttributeTypeDAO;
import de.iteratec.iteraplan.persistence.dao.BuildingBlockTypeDAO;


/**
 * This class is responsible for creation of an excel report for {@link BusinessFunction}s.
 */
public class BusinessFunctionExcelReport extends ExcelReport {

  private Set<BusinessFunction> bfList;

  /**
   * Constructor.
   * 
   * @param context
   *          the context which should contain the created report.
   * @param attributeTypeDAO
   *          required for handling of attribute types
   * @param bfList
   *          the {@link BusinessFunction}s for the report
   * @param queryData
   *          additional query data, can be <code>null</code>.
   * @param serverURL
   *          the server url to be used for creation of links within the current report
   * @throws de.iteratec.iteraplan.common.error.IteraplanBusinessException
   *           if the current user has no permission to create this excel report
   */
  public BusinessFunctionExcelReport(ExportWorkbook context, BuildingBlockTypeDAO bbtDao, AttributeTypeDAO attributeTypeDAO,
      Set<BusinessFunction> bfList, ExcelAdditionalQueryData queryData, String serverURL) {
    this.init(context, bbtDao, attributeTypeDAO, queryData, TypeOfBuildingBlock.BUSINESSFUNCTION, serverURL);
    this.bfList = bfList;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ExportWorkbook createReport() {

    this.addBFSheet(this.bfList);

    // in order to eliminate double entries in the result list, all necessary entities are added
    // to a HashSet first.
    Set<BusinessDomain> allBDs = new HashSet<BusinessDomain>();
    Set<BusinessObject> allBOs = new HashSet<BusinessObject>();
    Set<InformationSystemRelease> allISRs = new HashSet<InformationSystemRelease>();
    // for efficiency reasons only one iteration over the given list of elements is made
    for (BusinessFunction bf : this.bfList) {
      allISRs.addAll(bf.getInformationSystems());
      allBDs.addAll(bf.getBusinessDomains());
      allBOs.addAll(bf.getBusinessObjects());
    }

    this.addBDSheet(allBDs);
    this.addBOSheet(allBOs);
    this.addISRSheet(allISRs);

    return this.getContext();
  }

}
