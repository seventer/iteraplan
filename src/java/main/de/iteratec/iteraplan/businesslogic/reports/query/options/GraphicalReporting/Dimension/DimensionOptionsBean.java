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
package de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.GraphicalExportBaseOptions;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.PieBar.PieBarDiagramOptionsBean;


/**
 * This abstract class summarizes the different kinds of dimension options (e.g. color, line
 * pattern) available in the different graphics. The class should hide the differences between the
 * states of the dimensions during presentation(configuration) and generation time from the rest of
 * the application. We need this as the ability to additionally edit the unspecified value leads to
 * complications. Namely, we need to add the value to the list of values at the proper position
 * during presentation (configuration), but need to remove it for the generation as the default
 * value is treated differently at that point. Then, as the user might want to generate again, we
 * have to add the values back (otherwise strange things occur in the drop down menus and eventually
 * the application crashes). Thus, the need to differentiate. Alternatively, one could do all the
 * changes locally in all frontend services etc. but this doesn't make things any simpler or
 * clearer.
 */
public abstract class DimensionOptionsBean implements Serializable {

  public static final String   DEFAULT_VALUE        = "graphicalReport.unspecified";

  /** Serialization version */
  private static final long    serialVersionUID     = 5120786809137812823L;

  /**
   * The current mode in which the dimension is. Subclasses should take care of verifying a correct
   * invariant for all possible states.
   */
  private DimensionOptionsMode currentMode          = DimensionOptionsMode.PRESENTATION;

  /**
   * The Id of the attribute, which is represented by this dimension.
   */
  private Integer              dimensionAttributeId = Integer.valueOf(-1);

  /**
   * Specifies whether the current dimension is to be refreshed or not. It is set to true whenever
   * a new dimension attribute id is set, and set to false whenever {@link #refreshDimensionOptions(List)}
   * is successfully executed. Has to be set to true with every action which should make the
   * dimension refresh, like done in the {@link PieBarDiagramOptionsBean}.
   */
  private boolean              toRefresh            = true;

  /**
   * Switch the dimension to presentation-correct mode. This should be handled in the subclasses.
   */
  public abstract void switchToPresentationMode();

  /**
   * Switch the dimension to generation-correct mode. This should be handled in the subclasses.
   */
  public abstract void switchToGenerationMode();

  /**
   * Refreshes the dimension options. Also takes care that the unspecified value does not occur more
   * than once in the selection.
   */
  public void refreshDimensionOptions(List<String> dimensionAttributeValues) {
    if (isToRefresh()) {
      refresh(dimensionAttributeValues);
      setToRefresh(false);
    }
  }

  /**
   * Refreshes the dimension options. Also takes care that the unspecified value does not occur more
   * than once in the selection. Only executed if {@link #toRefresh} is true.
   */
  protected abstract void refresh(List<String> dimensionAttributeValues);

  /**
   * This method is called when loading a saved query to match the saved parameters to the current
   * database state.
   * 
   * @param savedDimensionAttributeValues
   *          The attribute values for the dimension loaded from the saved query.
   * @param savedSelectedValues
   *          The selected values for the dimension from the saved query.
   */
  public abstract void matchValuesFromSavedQuery(List<String> savedDimensionAttributeValues, List<String> savedSelectedValues);

  /**
   * Sets the attribute values of the dimension.
   * 
   * @param attributeValues
   *          The list of attribute values
   */
  protected abstract void setAttributeValues(List<String> attributeValues);

  /**
   * Retrieves the list of attribute values.
   * 
   * @return The list of strings representing the attribute values of the selection.
   */
  public abstract List<String> getAttributeValues();

  /**
   * Retrieves the current state of the dimension.
   * 
   * @return The current state.
   */
  public DimensionOptionsMode getCurrentDimensionOptionsMode() {
    return currentMode;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  public void setDimensionAttributeId(Integer attributeId) {
    if (!this.dimensionAttributeId.equals(attributeId)) {
      this.dimensionAttributeId = attributeId;
      this.toRefresh = true;
    }
  }

  public Integer getDimensionAttributeId() {
    return this.dimensionAttributeId;
  }

  /**
   * {@link #toRefresh}
   */
  public void setToRefresh(boolean toRefresh) {
    this.toRefresh = toRefresh;
  }

  /**
   * {@link #toRefresh}
   */
  public boolean isToRefresh() {
    return this.toRefresh;
  }

  protected void setCurrentMode(DimensionOptionsMode currentMode) {
    this.currentMode = currentMode;
  }

  protected DimensionOptionsMode getCurrentMode() {
    return currentMode;
  }

  /**
   * Returns {@code true} if the specified dimension attribute id is a custom "attribute". Those 
   * attributes are not really attribute, but custom made. For example the ISR status or Seal.
   * 
   * @param attributeId the dimension attribute id
   * @return {@code true} if the specified dimension attribute id is a custom "attribute"
   */
  protected boolean isCustomAttribute(int attributeId) {
    return attributeId == GraphicalExportBaseOptions.STATUS_SELECTED || attributeId == GraphicalExportBaseOptions.SEAL_SELECTED;
  }

  /**
   * Describes the possible states for a dimension.
   */
  public enum DimensionOptionsMode {
    PRESENTATION, GENERATION;
  }

}
