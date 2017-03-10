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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting;

import java.util.List;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.model.queries.SavedQueryEntityInfo;


public interface IGraphicalExportBaseOptions {

  String getSelectedBbType();

  void setSelectedBbType(String selectedBbType);

  /**
   * The step of the diagram configuration page
   * 
   * @return Either 1 or 2
   */
  int getDialogStep();

  void setDialogStep(int dialogStep);

  ColorDimensionOptionsBean getColorOptionsBean();

  LineDimensionOptionsBean getLineOptionsBean();

  void switchDimensionOptionsToPresentationMode();

  void switchDimensionOptionsToGenerationMode();

  /**
   * Returns a list of names for the possible {@link de.iteratec.iteraplan.businesslogic.reports.query.options.QueryResult QueryResult}s of the report.
   * Override if necessary for the specific diagram type.
   * @return List of Strings serving as keys for
   *         {@link de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase#queryResults ManageReportBeanBase#queryResults}.
   */
  List<String> getQueryResultNames();

  /**
   * Validates whether the user completed the necessary settings for creating a diagram
   */
  void validate();

  void setServerUrl(String serverUrl);

  String getServerUrl();

  void setSavedQueryInfo(SavedQueryEntityInfo savedQueryInfo);

  SavedQueryEntityInfo getSavedQueryInfo();

  void setShowSavedQueryInfo(boolean showSavedQueryInfo);

  boolean isShowSavedQueryInfo();

  String getSelectedGraphicFormat();

  void setSelectedGraphicFormat(String selectedGraphicFormat);

  void setNakedExport(boolean nakedExport);

  boolean isNakedExport();
  
  void setLegend(boolean legend);

  boolean isLegend();

  void setUseNamesLegend(boolean useNamesLegend);

  boolean isUseNamesLegend();
  
  void setWidth(Double width);
  
  Double getWidth();
  
  void setHeight(Double height);
  
  Double getHeight();
}
