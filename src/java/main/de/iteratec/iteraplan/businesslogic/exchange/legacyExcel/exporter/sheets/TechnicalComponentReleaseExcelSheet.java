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

import java.util.Date;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.ExcelConstants;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelHelper;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.TechnicalComponent;
import de.iteratec.iteraplan.model.TechnicalComponentRelease;
import de.iteratec.iteraplan.model.attribute.AttributeType;


/**
 * This class represents an excel sheet with contents for {@link TechnicalComponentRelease}s.
 */
public class TechnicalComponentReleaseExcelSheet extends CommonSheetContent<TechnicalComponentRelease> {

  private static final String SHEET_KEY = Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL;

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
  public TechnicalComponentReleaseExcelSheet(Set<TechnicalComponentRelease> contents, ExportWorkbook context,
      List<AttributeType> activatedAttributeTypes, ExcelAdditionalQueryData queryData, BuildingBlockType bbt, String serverURL) {
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
   * Adds common header specific to {@link TechnicalComponentRelease}s.
   */
  @Override
  protected void addCommonHeaders(List<Header> headers) {
    headers.add(getHeader(SHEET_KEY, ExportWorkbook.getColumnWidthForSheetKey()));
    headers.add(getHeader(Constants.ASSOC_PREDECESSORS));
    headers.add(getHeader(Constants.ASSOC_SUCCESSORS));
    
    headers.add(getHeader(Constants.ASSOC_USES));
    headers.add(getHeader(Constants.ASSOC_USEDBY));
    
    
    headers.add(getHeader(Constants.ATTRIBUTE_DESCRIPTION, ExportWorkbook.getColumnWidthWide()));

    headers.add(getHeader(Constants.TIMESPAN_PRODUCTIVE_FROM));

    headers.add(getHeader(Constants.TIMESPAN_PRODUCTIVE_TO));

    headers.add(getHeader(Constants.ATTRIBUTE_TYPEOFSTATUS));

    headers.add(getHeader("technicalComponentRelease.availableForInterfaces"));

    headers.add(getHeader(Constants.ATTRIBUTE_LAST_USER));
    headers.add(getHeader(Constants.ATTRIBUTE_LAST_MODIFICATION_DATE));
    headers.add(getHeader(Constants.SUBSCRIBED_USERS));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificHeaders(List<Header> headers) {
    headers.add(getHeader(Constants.BB_ARCHITECTURALDOMAIN_PLURAL));

    headers.add(getHeader(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL));

    headers.add(getHeader(ExcelConstants.HEADER_TECHNICALCOMPONENTRELEASE_SHEET_INTERFACES_COLUMN));

    headers.add(getHeader(Constants.BB_INFRASTRUCTUREELEMENT_PLURAL));
  }

  /**
   * Adds contents specific to {@link TechnicalComponentRelease}s.
   */
  @Override
  protected void addCommonLineContents(TechnicalComponentRelease tcr, List<Object> contents) {
    if (tcr == null) {
      return;
    }
    contents.add(tcr.getIdentityString());

    contents.add(ExcelHelper.concatMultipleReleaseNames(tcr.getPredecessors(), IN_LINE_SEPARATOR));
    contents.add(ExcelHelper.concatMultipleReleaseNames(tcr.getSuccessors(), IN_LINE_SEPARATOR));

    contents.add(ExcelHelper.concatMultipleReleaseNames(tcr.getBaseComponents(), IN_LINE_SEPARATOR));
    contents.add(ExcelHelper.concatMultipleReleaseNames(tcr.getParentComponents(), IN_LINE_SEPARATOR));
    
    
    String descr = tcr.getDescription();
    contents.add(descr);

    Date startDate = tcr.runtimeStartsAt();
    contents.add(startDate);

    Date endDate = tcr.runtimeEndsAt();
    contents.add(endDate);

    contents.add(getString(tcr.getTypeOfStatus().toString()));

    TechnicalComponent tc = tcr.getTechnicalComponent();
    if ((tc != null) && tc.isAvailableForInterfaces()) {
      contents.add(getString("global.yes"));
    }
    else {
      contents.add(getString("global.no"));
    }

    contents.add(tcr.getLastModificationUser());

    Date lastMod = tcr.getLastModificationTime();
    contents.add(lastMod);

    contents.add(ExcelHelper.concatMultipleUsers(tcr.getSubscribedUsers(), IN_LINE_SEPARATOR));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificLineContents(TechnicalComponentRelease tcr, List<Object> lineContents) {
    if (tcr == null) {
      return;
    }
    
    lineContents.add(ExcelHelper.concatMultipleNames(tcr.getArchitecturalDomains(), IN_LINE_SEPARATOR));

    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(tcr.getInformationSystemReleases(), IN_LINE_SEPARATOR));
    lineContents.add(ExcelHelper.concatISINamesBothWays(tcr.getInformationSystemInterfaces(), INTERFACE_SEPARATOR, IN_LINE_SEPARATOR));

    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(tcr.getInfrastructureElements(), IN_LINE_SEPARATOR));
  }
}
