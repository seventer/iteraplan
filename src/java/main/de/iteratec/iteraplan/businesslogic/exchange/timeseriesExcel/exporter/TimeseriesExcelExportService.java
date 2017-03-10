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
package de.iteratec.iteraplan.businesslogic.exchange.timeseriesExcel.exporter;

import org.apache.poi.ss.usermodel.Workbook;


public interface TimeseriesExcelExportService {

  /**
   * Generates a XLS template for timeseries data, containing a sheet for each
   * buildingblock-type/timeseries attribute type combination.
   * The sheets will be ordered, with the introduction sheet being the first.
   * The other sheets are ordered first by building block type (in order of their appearance in the EA data menu),
   * second by attribute type name. 
   * @return Empty timeseries template as Excel-{@link Workbook}
   */
  Workbook generateTemplateExcel2003();

  /**
   * Generates a XLSX template for timeseries data, containing a sheet for each
   * buildingblock-type/timeseries attribute type combination.
   * The sheets will be ordered, with the introduction sheet being the first.
   * The other sheets are ordered first by building block type (in order of their appearance in the EA data menu),
   * second by attribute type name. 
   * @return Empty timeseries template as Excel-{@link Workbook}
   */
  Workbook generateTemplateExcel2007();

  /**
   * Exports timeseries data as XLS, containing a sheet for each
   * buildingblock-type/timeseries attribute type combination.
   * The sheets will be ordered, with the introduction sheet being the first.
   * The other sheets are ordered first by building block type (in order of their appearance in the EA data menu),
   * second by attribute type name. 
   * @return Timeseries data in a {@link org.apache.poi.hssf.usermodel.HSSFWorkbook HSSFWorkbook}
   */
  Workbook exportTimeseriesDataExcel2003();

  /**
   * Exports timeseries data as XLSX, containing a sheet for each
   * buildingblock-type/timeseries attribute type combination.
   * The sheets will be ordered, with the introduction sheet being the first.
   * The other sheets are ordered first by building block type (in order of their appearance in the EA data menu),
   * second by attribute type name. 
   * @return Timeseries data in a {@link org.apache.poi.xssf.usermodel.XSSFWorkbook XSSFWorkbook}
   */
  Workbook exportTimeseriesDataExcel2007();
}
