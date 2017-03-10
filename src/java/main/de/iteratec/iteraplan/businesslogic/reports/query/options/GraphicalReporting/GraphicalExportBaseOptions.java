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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.common.collect.ImmutableList;

import de.iteratec.iteraplan.businesslogic.reports.query.options.ManageReportBeanBase;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.ColorDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.LineDimensionOptionsBean;
import de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting.ExportOption;
import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.queries.SavedQueryEntityInfo;
import de.iteratec.iteraplan.model.sorting.ResourceBundleKeyComparator;


public abstract class GraphicalExportBaseOptions implements Serializable, IGraphicalExportBaseOptions {

  /** Serialization version. */
  private static final long                  serialVersionUID        = 7657657303394535692L;
  public static final int                    STATUS_SELECTED         = 0;
  public static final int                    NOTHING_SELECTED        = -1;
  public static final int                    DESCRIPTION_SELECTED    = -4;
  public static final int                    SEAL_SELECTED           = -11;

  /** {@link #getQueryResultNames()} */
  private static final ImmutableList<String> MAIN_QUERIES            = ImmutableList.of(ManageReportBeanBase.MAIN_QUERY);

  /** {@link #getDialogStep()} */
  private int                                dialogStep              = 1;

  private final ColorDimensionOptionsBean    colorOptionsBean        = new ColorDimensionOptionsBean();
  private final LineDimensionOptionsBean     lineOptionsBean         = new LineDimensionOptionsBean();

  private String                             selectedGraphicFormat   = Constants.REPORTS_EXPORT_GRAPHICAL_VISIO;
  private List<ExportOption>                 availableGraphicFormats = ExportOption.getGraphicalExportOptions(true);

  /** The user-selected building block type (string key). */
  private String                             selectedBbType          = Constants.BB_INFORMATIONSYSTEMRELEASE_PLURAL;

  /** The List of all building block type keys (in plural). */
  private List<String>                       availableBbTypes;

  /**
   * The URL of the server.
   */
  private String                             serverUrl               = " ";

  /**
   * info about the saved query
   */
  private SavedQueryEntityInfo               savedQueryInfo          = null;
  private boolean                            showSavedQueryInfo      = true;

  /**
   * Determines whether a exported graphic is to be exported completely (false, the default case),
   * or nakedly. Naked means that no title or legend will be created, only the content-graphic
   * itself. Note that naked export mode is not implicitly implemented for every graphic
   * implementation. If an implementation does not provide the extra functionality, an ordinary
   * export will be created.
   */
  private boolean                            nakedExport             = false;
  private boolean                            legend                  = true;

  /**
   * Specifies whether a names legend should be used.
   */
  private boolean                            useNamesLegend          = true;
  
  /**
   * Specifies a static size for the image
   */
  private Double                             height;
  private Double                             width;

  public GraphicalExportBaseOptions() {
    availableBbTypes = Constants.ALL_TYPES_FOR_DISPLAY;
    Collections.sort(availableBbTypes, new ResourceBundleKeyComparator());
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public String getSelectedBbType() {
    List<String> permittedAvailableTypes = getAvailableBbTypes();
    if (selectedBbType == null) {
      return null;
    }
    else if (!permittedAvailableTypes.contains(selectedBbType)) {
      if (permittedAvailableTypes.isEmpty()) {
        selectedBbType = null;
      }
      else {
        selectedBbType = permittedAvailableTypes.get(0);
      }
    }
    return selectedBbType;
  }

  public void setSelectedBbType(String selectedBbType) {
    this.selectedBbType = selectedBbType;
  }

  public List<String> getAvailableBbTypes() {

    List<String> permittedBbTypes = new ArrayList<String>();

    for (String bBTypeInList : availableBbTypes) {
      if (UserContext.getCurrentPerms().getUserHasBbTypeFunctionalPermission(bBTypeInList)) {
        permittedBbTypes.add(bBTypeInList);
      }
    }
    return permittedBbTypes;
  }

  public void switchDimensionOptionsToPresentationMode() {
    colorOptionsBean.switchToPresentationMode();
    lineOptionsBean.switchToPresentationMode();
  }

  public void switchDimensionOptionsToGenerationMode() {
    colorOptionsBean.switchToGenerationMode();
    lineOptionsBean.switchToGenerationMode();
  }

  public int getDialogStep() {
    return dialogStep;
  }

  public void setDialogStep(int dialogStep) {
    this.dialogStep = dialogStep;
  }

  public void validate() {
    // Nothings to to!
  }

  public String getServerUrl() {
    return serverUrl;
  }

  public void setServerUrl(String serverUrl) {
    this.serverUrl = serverUrl;
  }

  public void setSavedQueryInfo(SavedQueryEntityInfo savedQueryInfo) {
    this.savedQueryInfo = savedQueryInfo;
  }

  public SavedQueryEntityInfo getSavedQueryInfo() {
    return savedQueryInfo;
  }

  public void setShowSavedQueryInfo(boolean showSavedQueryInfo) {
    this.showSavedQueryInfo = showSavedQueryInfo;
  }

  public boolean isShowSavedQueryInfo() {
    return showSavedQueryInfo;
  }

  public ColorDimensionOptionsBean getColorOptionsBean() {
    return this.colorOptionsBean;
  }

  public LineDimensionOptionsBean getLineOptionsBean() {
    return this.lineOptionsBean;
  }

  public boolean isNakedExport() {
    return nakedExport;
  }

  public void setNakedExport(boolean nakedExport) {
    this.nakedExport = nakedExport;
  }
  
  public boolean isLegend() {
    return legend;
  }

  public void setLegend(boolean legend) {
    this.legend = legend;
  }

  public List<ExportOption> getAvailableGraphicFormats() {
    return availableGraphicFormats;
  }

  public void setAvailableGraphicFormats(List<ExportOption> availableGraphicFormats) {
    this.availableGraphicFormats = availableGraphicFormats;
  }

  public String getSelectedGraphicFormat() {
    return selectedGraphicFormat;
  }

  public void setSelectedGraphicFormat(String selectedGraphicFormat) {
    this.selectedGraphicFormat = selectedGraphicFormat;
  }

  public void setAvailableBbTypes(List<String> availableBbTypes) {
    this.availableBbTypes = availableBbTypes;
  }

  public boolean isUseNamesLegend() {
    return useNamesLegend;
  }

  public void setUseNamesLegend(boolean useNamesLegend) {
    this.useNamesLegend = useNamesLegend;
  }

  public List<String> getQueryResultNames() {
    return MAIN_QUERIES;
  }

  public Double getHeight() {
    return height;
  }

  public void setHeight(Double height) {
    this.height = height;
  }

  public Double getWidth() {
    return width;
  }

  public void setWidth(Double width) {
    this.width = width;
  }
}
