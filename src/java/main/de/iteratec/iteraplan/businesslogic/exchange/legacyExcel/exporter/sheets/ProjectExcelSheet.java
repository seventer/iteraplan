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
import de.iteratec.iteraplan.model.Project;
import de.iteratec.iteraplan.model.attribute.AttributeType;


/**
 * This class represents an excel sheet with contents for {@link Project}s.
 */
public class ProjectExcelSheet extends CommonSheetContent<Project> {

  private static final String SHEET_KEY = Constants.BB_PROJECT_PLURAL;

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
  public ProjectExcelSheet(Set<Project> contents, ExportWorkbook context, List<AttributeType> activatedAttributeTypes,
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
   * Adds common headers specific to {@link Project}s.
   */
  @Override
  protected void addCommonHeaders(List<Header> headers) {
    headers.add(getHeader(SHEET_KEY, ExportWorkbook.getColumnWidthForSheetKey()));

    StringBuffer sheetKeyExt = new StringBuffer();
    sheetKeyExt.append(getString(SHEET_KEY));
    sheetKeyExt.append(' ');
    sheetKeyExt.append(getString(Constants.ATTRIBUTE_HIERARCHICAL));
    headers.add(new Header(sheetKeyExt.toString(), ExportWorkbook.getColumnWidthForSheetKey()));

    headers.add(getHeader(Constants.ATTRIBUTE_DESCRIPTION, ExportWorkbook.getColumnWidthWide()));
    headers.add(getHeader(Constants.TIMESPAN_LIFETIME_FROM));

    headers.add(getHeader(Constants.TIMESPAN_LIFETIME_TO));

    headers.add(getHeader(Constants.ATTRIBUTE_LAST_USER));
    headers.add(getHeader(Constants.ATTRIBUTE_LAST_MODIFICATION_DATE));
    headers.add(getHeader(Constants.SUBSCRIBED_USERS));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificHeaders(List<Header> headers) {
    headers.add(getHeader(Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL));
  }

  /**
   * Adds common contents specific to {@link Project}s.
   */
  @Override
  protected void addCommonLineContents(Project project, List<Object> contents) {
    if (project == null) {
      return;
    }
    contents.add(project.getNonHierarchicalName());

    contents.add(project.getIdentityString());

    String description = project.getDescription();
    contents.add(description);

    contents.add(project.runtimeStartsAt());

    contents.add(project.runtimeEndsAt());

    contents.add(project.getLastModificationUser());
    contents.add(project.getLastModificationTime());
    contents.add(ExcelHelper.concatMultipleUsers(project.getSubscribedUsers(), IN_LINE_SEPARATOR));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void addSpecificLineContents(Project project, List<Object> lineContents) {
    if (project == null) {
      return;
    }
    String isrNames = ExcelHelper.concatMultipleHierarchicalNames(project.getInformationSystemReleases(), IN_LINE_SEPARATOR);
    lineContents.add(isrNames);
  }
}
