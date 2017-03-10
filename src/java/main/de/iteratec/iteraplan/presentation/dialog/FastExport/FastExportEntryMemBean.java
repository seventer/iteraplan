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
package de.iteratec.iteraplan.presentation.dialog.FastExport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.iteratec.iteraplan.common.Constants;


/**
 * This class is a placeholder to store the preferences of a certain fast export. We need this as we
 * want to support a list of fast exports. It is a kind of a simplified ReportMemoryBean.
 */
public class FastExportEntryMemBean implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID            = -3683329922150369031L;

  private boolean           previewMode                 = false;

  private String            selectedExportFormat        = Constants.REPORTS_EXPORT_GRAPHICAL_SVG;
  private List<String>      availableExportFormats      = new ArrayList<String>();

  private Integer           selectedColorAttribute      = Integer.valueOf(0);
  private List<String>      availableColorAttributes    = new ArrayList<String>();

  private Integer           selectedLineTypeAttribute   = Integer.valueOf(-1);
  private List<String>      availableLineTypeAttributes = new ArrayList<String>();

  private String            outputMode;

  public FastExportEntryMemBean() {
    // default value
    this.outputMode = "inline";
  }

  public boolean isPreviewMode() {
    return previewMode;
  }

  public void setPreviewMode(boolean previewMode) {
    this.previewMode = previewMode;
  }

  public String getSelectedExportFormat() {
    return selectedExportFormat;
  }

  public void setSelectedExportFormat(String selectedExportFormat) {
    this.selectedExportFormat = selectedExportFormat;
  }

  public Integer getSelectedColorAttribute() {
    return selectedColorAttribute;
  }

  public void setSelectedColorAttribute(Integer selectedColorAttribute) {
    this.selectedColorAttribute = selectedColorAttribute;
  }

  public Integer getSelectedLineTypeAttribute() {
    return selectedLineTypeAttribute;
  }

  public void setSelectedLineTypeAttribute(Integer selectedLineTypeAttribute) {
    this.selectedLineTypeAttribute = selectedLineTypeAttribute;
  }

  public List<String> getAvailableExportFormats() {
    return availableExportFormats;
  }

  public void setAvailableExportFormats(List<String> availableExportFormats) {
    this.availableExportFormats = availableExportFormats;
  }

  public List<String> getAvailableColorAttributes() {
    return availableColorAttributes;
  }

  public void setAvailableColorAttributes(List<String> availableColorAttributes) {
    this.availableColorAttributes = availableColorAttributes;
  }

  public List<String> getAvailableLineTypeAttributes() {
    return availableLineTypeAttributes;
  }

  public void setAvailableLineTypeAttributes(List<String> availableLineTypeAttributes) {
    this.availableLineTypeAttributes = availableLineTypeAttributes;
  }

  public void setOutputMode(String outputMode) {
    this.outputMode = outputMode;
  }

  public String getOutputMode() {
    return outputMode;
  }

}
