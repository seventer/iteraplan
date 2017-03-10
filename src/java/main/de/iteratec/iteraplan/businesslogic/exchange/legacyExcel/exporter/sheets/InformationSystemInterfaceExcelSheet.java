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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelHelper;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.GeneralHelper;
import de.iteratec.iteraplan.model.BuildingBlockType;
import de.iteratec.iteraplan.model.InformationSystemInterface;
import de.iteratec.iteraplan.model.Transport;
import de.iteratec.iteraplan.model.attribute.AttributeType;


/**
 * This class represents an excel sheet with contents for {@link InformationSystemInterface}s.
 */
public class InformationSystemInterfaceExcelSheet extends CommonSheetContent<InformationSystemInterface> {

  private static final String SHEET_KEY            = Constants.BB_INFORMATIONSYSTEMINTERFACE_PLURAL;

  // constants used for grouping of certain elements while concatenating them to one value
  private static final String LOCAL_UNIT_SEPARATOR = " ";

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
  public InformationSystemInterfaceExcelSheet(Set<InformationSystemInterface> contents, ExportWorkbook context,
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
   * Concatenates technical informations and {@link de.iteratec.iteraplan.model.BusinessObject BusinessObject}s for all
   * {@link Transport}s connected with the given <code>isi</code>. All tupels are sorted before concatenation in one
   * final string.
   * 
   * @param isi
   *          information system interface
   * @return all associated information or an empty String if no transports exist
   */
  private String concatTransportInfoAndBO(InformationSystemInterface isi) {
    Set<Transport> transports = isi.getTransports();
    if (transports == null) {
      return "";
    }
    List<String> allEntities = new ArrayList<String>();
    for (Transport transport : transports) {
      StringBuffer line = new StringBuffer();
      String directionBO = "'" + transport.getTransportInfo().getTextRepresentation();
      line.append(directionBO);
      line.append(LOCAL_UNIT_SEPARATOR);
      line.append(transport.getBusinessObject().getName());
      allEntities.add(line.toString());
    }

    // for better visualization the retrieved list is sorted before the final concatenation
    Collections.sort(allEntities);
    return GeneralHelper.makeConcatenatedStringWithSeparator(allEntities, IN_LINE_SEPARATOR);
  }

  /**
   * Adds headers specific to {@link InformationSystemInterface}s.
   */
  @Override
  protected void addCommonHeaders(List<Header> headers) {
    headers.add(getHeader(Constants.BB_INTERFACE_INFORMATIONSYSTEMRELEASE_A));
    headers.add(getHeader(Constants.BB_INTERFACE_INFORMATIONSYSTEMRELEASE_B));

    headers.add(getHeader(Constants.ATTRIBUTE_NAME, ExportWorkbook.getColumnWidthWide()));
    headers.add(getHeader(Constants.ATTRIBUTE_TRANSPORT, ExportWorkbook.getColumnWidthWide()));
    headers.add(getHeader(Constants.ATTRIBUTE_DESCRIPTION, ExportWorkbook.getColumnWidthWide()));

    headers.add(getHeader(Constants.ATTRIBUTE_LAST_USER));
    headers.add(getHeader(Constants.ATTRIBUTE_LAST_MODIFICATION_DATE));
    headers.add(getHeader(Constants.SUBSCRIBED_USERS));

    headers.add(getHeader("reporting.excel.header.interface.businessObjects"));

    headers.add(getHeader(Constants.BB_TECHNICALCOMPONENTRELEASE_PLURAL));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificHeaders(List<Header> headers) {
    // nothing to do; all done in addCommonHeaders
  }

  /**
   * Adds contents specific to {@link InformationSystemInterface}s.
   * direction describes the interface's transport. It is independent of the business objects transports.
   */
  @Override
  protected void addCommonLineContents(InformationSystemInterface isi, List<Object> contents) {
    if (isi == null) {
      return;
    }
    contents.add(isi.getInformationSystemReleaseA().getHierarchicalName());
    contents.add(isi.getInformationSystemReleaseB().getHierarchicalName());

    String name = isi.getName();
    contents.add(name);

    String direction = isi.getInterfaceDirection().getValue();
    // escape character apostrophe needed for avoiding sum function in excel
    String excelDirection = "'";

    excelDirection = excelDirection + direction;
    contents.add(excelDirection);

    String description = isi.getDescription();
    contents.add(description);

    contents.add(isi.getLastModificationUser());
    contents.add(isi.getLastModificationTime());
    contents.add(ExcelHelper.concatMultipleUsers(isi.getSubscribedUsers(), IN_LINE_SEPARATOR));

    contents.add(this.concatTransportInfoAndBO(isi));
    contents.add(ExcelHelper.concatMultipleReleaseNames(isi.getTechnicalComponentReleases(), IN_LINE_SEPARATOR));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificLineContents(InformationSystemInterface isi, List<Object> lineContents) {
    // nothing to do; all contents are added in addCommonLineContents
  }
}
