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
package de.iteratec.iteraplan.businesslogic.service.legacyExcel;

import java.util.List;
import java.util.Locale;

import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExcelAdditionalQueryData;
import de.iteratec.iteraplan.businesslogic.exchange.legacyExcel.exporter.ExportWorkbook;
import de.iteratec.iteraplan.businesslogic.exchange.templates.TemplateType;
import de.iteratec.iteraplan.model.BuildingBlock;
import de.iteratec.iteraplan.model.Sequence;
import de.iteratec.iteraplan.model.TypeOfBuildingBlock;
import de.iteratec.iteraplan.model.dto.ReleaseSuccessorDTO.SuccessionContainer;


/**
 * This interface provides methods for creation of excel reports for different types of
 * {@link BuildingBlock}s.
 */
public interface ExcelExportService {

  /**
   * Creates an excel report for information system predecessors relation.
   * 
   * @param locale the locale for the Strings in the report
   * @param releaseSuccession the list of releases that should be exported
   * @param releaseType the Building block type of the releases that shall be exported. This required to put them on the appropriate worksheets.
   * @param serverURL the server url to be used for creation of links within the current report
   * @return {@link ExportWorkbook} that contains the required report and therefore could be written to an output stream
   */
  ExportWorkbook getReleaseSuccessionReport(Locale locale, List<SuccessionContainer<? extends Sequence<?>>> releaseSuccession,
                                            TypeOfBuildingBlock releaseType, String serverURL, TemplateType templateType);

  /**
   * Creates an excel report for any of the user-visible building blocks types. The default template for the template type is used to create the report.
   * 
   * @param <T> the type of the list elements which shall be included in the report.
   * @param locale the locale for the Strings in the report
   * @param queryData contains some information of the query
   * @param allBbs the building block to include in the report
   * @param tob the building block type descriptor that reflects the contents of <code>allBbs</code>.
   *          This value MUST correspond to the (class) type of the list elements.
   * @param serverUrl the server url to be used for creation of links within the current report
   * @param templateType the name of the excel template type
   * @return {@link ExportWorkbook} that contains the required report and therefore could be written to an output stream
   */
  <T extends BuildingBlock> ExportWorkbook getBuildingBlockReport(Locale locale, ExcelAdditionalQueryData queryData, List<T> allBbs,
                                                                  TypeOfBuildingBlock tob, String serverUrl, TemplateType templateType);

/**
 * Creates an excel report for any of the user-visible building blocks types.
 * 
 * @param <T> the type of the list elements which shall be included in the report.
 * @param locale the locale for the Strings in the report
 * @param queryData contains some information of the query
 * @param allBbs the building block to include in the report
 * @param tob the building block type descriptor that reflects the contents of <code>allBbs</code>.
 *          This value MUST correspond to the (class) type of the list elements.
 * @param serverUrl the server url to be used for creation of links within the current report
 * @param templateType the name of the excel template type
 * @param templateFileName is the name of the template file to be used. Empty string means default template.
 * @return {@link ExportWorkbook} that contains the required report and therefore could be written to an output stream
 */
<T extends BuildingBlock> ExportWorkbook getBuildingBlockReport(Locale locale, ExcelAdditionalQueryData queryData, List<T> allBbs,
                                                                TypeOfBuildingBlock tob, String serverUrl, TemplateType templateType, String templateFileName);
}
