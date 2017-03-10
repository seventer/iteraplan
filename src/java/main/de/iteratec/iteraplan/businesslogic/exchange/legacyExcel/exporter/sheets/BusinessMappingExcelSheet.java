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

import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessMapping;
import de.iteratec.iteraplan.model.attribute.AttributeType;


/**
 * This class represents an excel sheet with contents for {@link BusinessMapping}s.
 */
public class BusinessMappingExcelSheet extends CommonSheetContent<BusinessMapping> {

  private static final String SHEET_KEY      = Constants.BB_BUSINESSMAPPING_PLURAL;

  private static final String MISSING_ENTITY = "-";

  /**
   * Constructor.
   * 
   * @param contents
   *          the actual contents to be added
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
  public BusinessMappingExcelSheet(Set<BusinessMapping> contents, ExportWorkbook context, List<AttributeType> activatedAttributeTypes,
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
   * Adds specific common headers to {@link BusinessMapping}.
   */
  @Override
  protected void addCommonHeaders(List<Header> headers) {

    //headers.add(getString("global.lastModificationUser"));
    //headers.add(getString("global.lastModificationTime"));

    headers.add(getHeader(Constants.BB_BUSINESSPROCESS_PLURAL));
    headers.add(getHeader(Constants.BB_PRODUCT_PLURAL));
    headers.add(getHeader(Constants.BB_BUSINESSUNIT_PLURAL));
    headers.add(getHeader(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL));
  }

  /**
   * Adds specific common contents to {@link BusinessMapping}.
   */
  @Override
  protected void addCommonLineContents(BusinessMapping bm, List<Object> contents) {
    if (bm == null) {
      return;
    }

    contents.add(this.retrieveHierarchicalName(bm.getBusinessProcess()));
    contents.add(this.retrieveHierarchicalName(bm.getProduct()));
    contents.add(this.retrieveHierarchicalName(bm.getBusinessUnit()));

    contents.add(bm.getInformationSystemRelease().getHierarchicalName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificHeaders(List<Header> headers) {
    // nothing to do; all headers has been added by overriding addCommonHeaders
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificLineContents(BusinessMapping bb, List<Object> lineContents) {
    // nothing to do; all contents has been added by overriding addCommonLineContents
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getDescription(BusinessMapping bb) {
    // this method should not be called, because no description
    // for business mappings is required. Therefore is the return value irrelevant.
    return "";
  }

  /**
   * At the moment the id of the corresponding information system is used due to the fact that no
   * dialog (for usage with the hyperlink) is defined for business mappings. If such dialog will be
   * created later on, these method can be omitted.
   */
  @Override
  protected Integer getIdForBB(BusinessMapping bm) {
    return bm.getInformationSystemRelease().getId();
  }

  /**
   * Return the header for the id-column with suffix concerning the connection to the corresponding
   * {@link de.iteratec.iteraplan.model.InformationSystemRelease InformationSystemRelease}.
   * 
   * @see #getIdForBB(BuildingBlock)
   */
  @Override
  protected Header getIdHeader() {
    return new Header(getString(Constants.ATTRIBUTE_ID) + "(IS)", ExportWorkbook.getColumnWidthForId());
  }

  /**
   * Returns the connected {@link de.iteratec.iteraplan.model.InformationSystemRelease InformationSystemRelease}.
   */
  @Override
  protected BuildingBlock getBBForIdHyperlink(BusinessMapping bm) {
    return bm.getInformationSystemRelease();
  }

  /**
   * @param bb
   * @return the hierarchical name of the given {@link BuildingBlock} or {@link MISSING_ENTITY} if
   *         bb is <code>null</code>.
   */
  private String retrieveHierarchicalName(BuildingBlock bb) {
    if (bb == null) {
      return MISSING_ENTITY;
    }
    return bb.getHierarchicalName();
  }
}
