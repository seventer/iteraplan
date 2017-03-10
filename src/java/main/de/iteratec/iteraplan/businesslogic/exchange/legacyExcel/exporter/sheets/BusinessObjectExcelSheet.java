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
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelHelper;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.BusinessObject;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.attribute.AttributeType;


/**
 * This class represents an excel sheet with contents for {@link BusinessObject}s.
 */
public class BusinessObjectExcelSheet extends CommonSheetContent<BusinessObject> {

  private static final String SHEET_KEY = Constants.BB_BUSINESSOBJECT_PLURAL;

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
  public BusinessObjectExcelSheet(Set<BusinessObject> contents, ExportWorkbook context, List<AttributeType> activatedAttributeTypes,
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
   * Adds common headers specific to {@link BusinessObject}s.
   */
  @Override
  protected void addCommonHeaders(List<Header> headers) {
    headers.add(getHeader(SHEET_KEY, ExportWorkbook.getColumnWidthForSheetKey()));

    StringBuilder sheetKeyExt = new StringBuilder();
    sheetKeyExt.append(getString(SHEET_KEY));
    sheetKeyExt.append(' ');
    sheetKeyExt.append(getString(Constants.ATTRIBUTE_HIERARCHICAL));
    headers.add(new Header(sheetKeyExt.toString(), ExportWorkbook.getColumnWidthForSheetKey()));

    headers.add(getHeader(Constants.ATTRIBUTE_DESCRIPTION, ExportWorkbook.getColumnWidthWide()));

    // This header is different to the common headers -> override is necessary
    headers.add(getHeader(Constants.ASSOC_SPECIALISATION));
    headers.add(getHeader(Constants.ASSOC_GENERALIZATION));

    headers.add(getHeader(Constants.ATTRIBUTE_LAST_USER));
    headers.add(getHeader(Constants.ATTRIBUTE_LAST_MODIFICATION_DATE));
    headers.add(getHeader(Constants.SUBSCRIBED_USERS));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificHeaders(List<Header> headers) {
    headers.add(getHeader(Constants.BB_BUSINESSDOMAIN_PLURAL));
    headers.add(getHeader(Constants.BB_BUSINESSFUNCTION_PLURAL));
    headers.add(getHeader(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL));
    headers.add(getHeader(Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL));
  }

  /**
   * Adds common contents specific to {@link BusinessObject}s.
   */
  @Override
  protected void addCommonLineContents(BusinessObject bo, List<Object> contents) {
    if (bo == null) {
      return;
    }
    contents.add(bo.getNonHierarchicalName());
    contents.add(bo.getIdentityString());

    String description = bo.getDescription();
    contents.add(description);

    contents.add(ExcelHelper.concatMultipleHierarchicalNames(bo.getSpecialisations(), IN_LINE_SEPARATOR));
    contents.add((bo.getGeneralisation() != null) ? bo.getGeneralisation().getName() : null);

    contents.add(bo.getLastModificationUser());
    contents.add(bo.getLastModificationTime());
    contents.add(ExcelHelper.concatMultipleUsers(bo.getSubscribedUsers(), IN_LINE_SEPARATOR));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificLineContents(BusinessObject bo, List<Object> lineContents) {
    if (bo == null) {
      return;
    }
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(bo.getBusinessDomains(), IN_LINE_SEPARATOR));
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(bo.getBusinessFunctions(), IN_LINE_SEPARATOR));

    String isrNames = ExcelHelper.concatMultipleHierarchicalNames(bo.getInformationSystemReleases(), IN_LINE_SEPARATOR);
    lineContents.add(isrNames);

    Set<InformationSystemInterface> allISIs = ExcelHelper.retrieveInformationSystemInterfaces(bo.getTransports());
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(allISIs, IN_LINE_SEPARATOR));

  }
}
