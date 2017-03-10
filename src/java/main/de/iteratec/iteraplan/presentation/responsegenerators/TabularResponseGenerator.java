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
package de.iteratec.iteraplan.presentation.responsegenerators;

import de.iteratec.iteraplan.common.Constants;


public class TabularResponseGenerator {

  public static enum TabularResultFormat {
    HTML("Html", Constants.REPORTS_EXPORT_HTML, null), EXCEL_2007("Excel 2007", Constants.REPORTS_EXPORT_EXCEL_2007, ExcelResponseGenerator.class), EXCEL(
        "Excel 2003", Constants.REPORTS_EXPORT_EXCEL_2003, ExcelResponseGenerator.class), CSV("Csv", Constants.REPORTS_EXPORT_CSV,
        CsvResponseGenerator.class), MSPDI("Mspdi", Constants.REPORTS_EXPORT_MSPROJECT_MSPDI, MsProjectResponseGenerator.class), MPX("Mpx",
        Constants.REPORTS_EXPORT_MSPROJECT_MPX, MsProjectResponseGenerator.class), MSPDI_SUBS("Mspdi_subs",
        Constants.REPORTS_EXPORT_MSPROJECT_MSPDI_INCLUDING_SUBS, MsProjectResponseGenerator.class), MPX_SUBS("Mpx_subs",
        Constants.REPORTS_EXPORT_MSPROJECT_MPX_INCLUDING_SUBS, MsProjectResponseGenerator.class), XMI("Xmi", Constants.REPORTS_EXPORT_XMI,
        XMIResponseGeneratorImpl.class), UNKNOWN("", "", null);

    private String   format;
    private String   resultFormat;
    // TODO does it make sense to create a hierarchy for ResponseGenerators here?
    private Class<?> clazz;

    private TabularResultFormat(String format, String resultFormat, Class<?> clazz) {
      this.format = format;
      this.resultFormat = resultFormat;
      this.clazz = clazz;
    }

    /**
     * @return the simple string representation of the result format (i.e. Png)
     */
    public String getFormat() {
      return this.format;
    }

    /**
     * @return one of the result formats defined in the Constants (i.e.
     *         Constants.REPORTS_EXPORT_CSV) or empty string if unknown
     */
    public String getResultFormat() {
      return this.resultFormat;
    }

    public Class<?> getGeneratorClass() {
      return this.clazz;
    }

    public static TabularResultFormat fromString(String value) {
      for (TabularResultFormat format : TabularResultFormat.values()) {
        if (format.getFormat().equalsIgnoreCase(value)) {
          return format;
        }
      }
      return UNKNOWN;
    }

    public static TabularResultFormat fromResultFormatString(String value) {
      for (TabularResultFormat format : TabularResultFormat.values()) {
        if (format.getResultFormat().equalsIgnoreCase(value)) {
          return format;
        }
      }
      return UNKNOWN;
    }

  }

}
