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
package de.iteratec.iteraplan.businesslogic.reports.query.options.TabularReporting;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import de.iteratec.iteraplan.common.Constants;
import de.iteratec.iteraplan.common.util.EqualsUtils;


/**
 * Holds one possible export option for the current report. Used by the GUI for rendering a drop
 * down box.
 */
public class ExportOption implements Serializable {

  /** Serialization version. */
  private static final long serialVersionUID = 3878128846873669735L;

  private String            presentationKey;

  private boolean           visible          = true;

  /**
   * @param presentationKey
   * @param visible
   */
  public ExportOption(String presentationKey, boolean visible) {
    super();
    this.presentationKey = presentationKey;
    this.visible = visible;
  }

  /**
   * Initializes an Export Option with the given presentation key and visibility = true.
   * 
   * @param presentationKey
   */
  public ExportOption(String presentationKey) {
    this(presentationKey, true);
  }

  public static List<ExportOption> getGraphicalExportOptions(boolean visioSupported) {
    List<ExportOption> graphicalExportOptions = new ArrayList<ExportOption>();

    if (visioSupported) {
      graphicalExportOptions.add(new ExportOption(Constants.REPORTS_EXPORT_GRAPHICAL_VISIO));
    }
    graphicalExportOptions.add(new ExportOption(Constants.REPORTS_EXPORT_GRAPHICAL_SVG));
    graphicalExportOptions.add(new ExportOption(Constants.REPORTS_EXPORT_GRAPHICAL_JPEG));
    graphicalExportOptions.add(new ExportOption(Constants.REPORTS_EXPORT_GRAPHICAL_PNG));
    graphicalExportOptions.add(new ExportOption(Constants.REPORTS_EXPORT_GRAPHICAL_PDF));

    return graphicalExportOptions;
  }

  /**
   * Depending on whether the export option is available, true or false is returned.
   * 
   * @return true if export option is visible.
   */
  public boolean isVisible() {
    return visible;
  }

  /**
   * @see #isVisible()
   * @param visible
   *          set to true if the export option is available.
   */
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  /**
   * The I18N key used by the GUI to display the export option.
   * 
   * @return an I18N key.
   */
  public String getPresentationKey() {
    return presentationKey;
  }

  @Override
  public String toString() {
    ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    builder.append("presentationKey", presentationKey);
    builder.append("visible", visible);

    return builder.toString();
  }

  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj.getClass().equals(this.getClass())) {
      ExportOption other = (ExportOption) obj;
      return EqualsUtils.areEqual(this.presentationKey, other.presentationKey) && (this.visible == other.visible);
    }
    return false;
  }

  public int hashCode() {
    int hashCode = Boolean.valueOf(visible).hashCode();
    if (presentationKey != null) {
      hashCode *= presentationKey.hashCode();
    }
    return hashCode;
  }

}
