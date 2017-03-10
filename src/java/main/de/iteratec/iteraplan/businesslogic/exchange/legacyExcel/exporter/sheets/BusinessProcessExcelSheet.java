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
package de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.sheets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelHelper;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessProcess;
import de.iteratec.iteraplan.model.BusinessUnit;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Product;
import de.iteratec.iteraplan.model.attribute.AttributeType;


/**
 * This class represents an excel sheet with contents for {@link BusinessProcess}s.
 */
public class BusinessProcessExcelSheet extends CommonSheetContent<BusinessProcess> {

  private static final String SHEET_KEY = Constants.BB_BUSINESSPROCESS_PLURAL;

  /**
   * Constructor.
   * 
   * @param contents
   *          the actual contents to be added.
   * @param context
   *          the excel context to use for storage of the created contents.
   * @param activatedAttributeTypes
   *          the types to be used for this sheet
   * @param queryData
   *          additional query data; is ignored if <code>null</code>
   * @param bbt
   *          the type of building block this sheet should be created for; this information can be
   *          used to create different views for different {@link BuildingBlockType}s.
   * @param serverURL
   *          the server url to be used for creation of links within the current sheet
   */
  public BusinessProcessExcelSheet(Set<BusinessProcess> contents, ExportWorkbook context, List<AttributeType> activatedAttributeTypes,
      ExcelAdditionalQueryData queryData, BuildingBlockType bbt, String serverURL) {
    this.init(contents, context, activatedAttributeTypes, queryData, bbt, serverURL);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getSheetKey() {
    return SHEET_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitleKey() {
    return SHEET_KEY;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificHeaders(List<Header> headers) {
    headers.add(getHeader(Constants.BB_BUSINESSDOMAIN_PLURAL));
    headers.add(getHeader(Constants.BB_PRODUCT_PLURAL));
    headers.add(getHeader(Constants.BB_BUSINESSUNIT_PLURAL));

    String isr = getString(Constants.BB_INFORMATIONSYSTEMRELEASE);
    String bu = getString(Constants.BB_BUSINESSUNIT);
    String product = getString(Constants.BB_PRODUCT);
    String bm = getString(Constants.BB_BUSINESSMAPPING_PLURAL);
    String coverageHeaderNameExtended = bm + UNIT_OPENER + product + UNIT_SEPARATOR + bu + UNIT_SEPARATOR + isr + UNIT_CLOSER;
    headers.add(new Header(coverageHeaderNameExtended));

    headers.add(getHeader(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificLineContents(BusinessProcess bp, List<Object> lineContents) {
    if (bp == null) {
      return;
    }
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(bp.getBusinessDomains(), IN_LINE_SEPARATOR));

    Set<BusinessUnit> buSet = new HashSet<BusinessUnit>();
    Set<Product> productSet = new HashSet<Product>();
    Set<InformationSystemRelease> isrSet = new HashSet<InformationSystemRelease>();

    ExcelHelper.extractContentsFromBMs(bp.getBusinessMappings(), null, buSet, isrSet, productSet);
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(productSet, IN_LINE_SEPARATOR));
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(buSet, IN_LINE_SEPARATOR));
    lineContents.add(ExcelHelper.concatProdandBuandISR(bp.getBusinessMappings(), UNIT_OPENER, UNIT_SEPARATOR, UNIT_CLOSER, IN_LINE_SEPARATOR));
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(isrSet, IN_LINE_SEPARATOR));

  }
}