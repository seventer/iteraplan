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

import org.apache.commons.lang.StringUtils;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.ExcelConstants;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelHelper;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.InformationSystemRelease;
import de.iteratec.iteraplan.model.Seal;
import de.iteratec.iteraplan.model.attribute.AttributeType;


/**
 * This class represents an excel sheet with contents for {@link InformationSystemRelease}s.
 */
public class InformationSystemReleaseExcelSheet extends CommonSheetContent<InformationSystemRelease> {

  private static final String SHEET_KEY = Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL;

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
  public InformationSystemReleaseExcelSheet(Set<InformationSystemRelease> contents, ExportWorkbook context,
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
   * Retrieves all {@link InformationSystemRelease}s connected with the given <code>isr</code> via
   * {@link InformationSystemInterface}s.
   * 
   * @param isr
   *          information system release
   * @return the set with all connected {@link InformationSystemRelease}s or an empty set, if no
   *         interfaces are defined.
   */
  private Set<InformationSystemRelease> retrieveConnectedISRs(InformationSystemRelease isr) {
    Set<InformationSystemRelease> connectedISRs = new HashSet<InformationSystemRelease>();
    if (isr.getAllConnections() == null) {
      return connectedISRs;
    }
    for (InformationSystemInterface isi : isr.getAllConnections()) {
      isi.setReferenceRelease(isr);
      connectedISRs.add(isi.getOtherRelease());

    }
    return connectedISRs;
  }

  /**
   * Adds common headers specific to {@link InformationSystemRelease}s.
   */
  @Override
  protected void addCommonHeaders(List<Header> headers) {
    headers.add(getHeader(SHEET_KEY, ExportWorkbook.getColumnWidthForSheetKey()));

    StringBuffer sheetKeyExt = new StringBuffer();
    sheetKeyExt.append(getString(SHEET_KEY));
    sheetKeyExt.append(' ');
    sheetKeyExt.append(getString(Constants.ATTRIBUTE_HIERARCHICAL));
    headers.add(new Header(sheetKeyExt.toString(), ExportWorkbook.getColumnWidthForSheetKey()));
    
    headers.add(getHeader(Constants.ASSOC_PREDECESSORS));
    headers.add(getHeader(Constants.ASSOC_SUCCESSORS));
    
    headers.add(getHeader(Constants.ASSOC_USES));
    headers.add(getHeader(Constants.ASSOC_USEDBY));
    
    
    headers.add(getHeader(Constants.ATTRIBUTE_DESCRIPTION, ExportWorkbook.getColumnWidthWide()));

    headers.add(getHeader(Constants.TIMESPAN_PRODUCTIVE_FROM));

    headers.add(getHeader(Constants.TIMESPAN_PRODUCTIVE_TO));

    headers.add(getHeader(Constants.ATTRIBUTE_TYPEOFSTATUS));

    headers.add(getHeader(Constants.ATTRIBUTE_LAST_USER));
    headers.add(getHeader(Constants.ATTRIBUTE_LAST_MODIFICATION_DATE));
    headers.add(getHeader(Constants.SUBSCRIBED_USERS));

    headers.add(getHeader("seal.state"));
    headers.add(getHeader("seal.verification.date"));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificHeaders(List<Header> headers) {
    headers.add(getHeader(Constants.BB_BUSINESSFUNCTION_PLURAL));
    String bm = getString(Constants.BB_BUSINESSMAPPING_PLURAL);
    String process = getString(Constants.BB_BUSINESSPROCESS);
    String bu = getString(Constants.BB_BUSINESSUNIT);
    String product = getString(Constants.BB_PRODUCT);
    String coverageHeaderNameExtended = bm + UNIT_OPENER + process + UNIT_SEPARATOR + product + UNIT_SEPARATOR + bu + UNIT_CLOSER;
    headers.add(new Header(coverageHeaderNameExtended));

    headers.add(getHeader(Constants.BB_BUSINESSOBJECT_PLURAL));

    headers.add(getHeader(Constants.BB_INFORMATIONSYSTEMDOMAIN_PLURAL));
    headers.add(getHeader(ExcelConstants.HEADER_INFORMATIONSYSTEMRELEASE_SHEET_INTERFACES_COLUMN));

    headers.add(getHeader(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL));
    headers.add(getHeader(Constants.BB_INFRASTRUCTUREELEMENT_PLURAL));

    headers.add(getHeader(Constants.BB_PROJECT_PLURAL));
  }

  /**
   * Adds common contents specific to {@link InformationSystemRelease}s.
   */
  @Override
  protected void addCommonLineContents(InformationSystemRelease isr, List<Object> contents) {
    if (isr == null) {
      return;
    }
    contents.add(isr.getNonHierarchicalName());
    contents.add(isr.getHierarchicalName());

    contents.add(ExcelHelper.concatMultipleHierarchicalNames(isr.getPredecessors(), IN_LINE_SEPARATOR));
    contents.add(ExcelHelper.concatMultipleHierarchicalNames(isr.getSuccessors(), IN_LINE_SEPARATOR));

    contents.add(ExcelHelper.concatMultipleReleaseNames(isr.getBaseComponents(), IN_LINE_SEPARATOR));
    contents.add(ExcelHelper.concatMultipleReleaseNames(isr.getParentComponents(), IN_LINE_SEPARATOR));
   
    
    String description = isr.getDescription();
    contents.add(description);

    contents.add(isr.runtimeStartsAt());
    contents.add(isr.runtimeEndsAt());

    contents.add(getString(isr.getTypeOfStatusAsString()));

    contents.add(isr.getLastModificationUser());
    contents.add(isr.getLastModificationTime());
    contents.add(ExcelHelper.concatMultipleUsers(isr.getSubscribedUsers(), IN_LINE_SEPARATOR));

    contents.add(getString(isr.getSealState().getValue()));
    Seal lastSeal = isr.getLastSeal();
    if (lastSeal != null) {
      contents.add(lastSeal.getDate());
    }
    else {
      contents.add(StringUtils.EMPTY);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificLineContents(InformationSystemRelease isr, List<Object> lineContents) {
    if (isr == null) {
      return;
    }
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(isr.getBusinessFunctions(), IN_LINE_SEPARATOR));
    lineContents.add(ExcelHelper.concatBPandProdandBU(isr.getBusinessMappings(), UNIT_OPENER, UNIT_SEPARATOR, UNIT_CLOSER, IN_LINE_SEPARATOR));
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(isr.getBusinessObjects(), IN_LINE_SEPARATOR));

     
    lineContents.add(ExcelHelper.concatMultipleNames(isr.getInformationSystemDomains(), IN_LINE_SEPARATOR));
    Set<InformationSystemRelease> connIsrList = this.retrieveConnectedISRs(isr);
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(connIsrList, IN_LINE_SEPARATOR));

    lineContents.add(ExcelHelper.concatMultipleReleaseNames(isr.getTechnicalComponentReleases(), IN_LINE_SEPARATOR));
    lineContents.add(ExcelHelper.concatMultipleHierarchicalNames(isr.getInfrastructureElements(), IN_LINE_SEPARATOR));

    lineContents.add(ExcelHelper.concatMultipleNames(isr.getProjects(), IN_LINE_SEPARATOR));
  }
}
