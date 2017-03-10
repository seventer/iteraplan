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
package de.iteratec.iteraplan.businesslogic.exchange.visio.legend;

import java.util.Locale;

import de.iteratec.iteraplan.businesslogic.exchange.common.Coordinates;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.ColorRangeDimension;
import de.iteratec.iteraplan.businesslogic.exchange.common.dimension.Dimension;
import de.iteratec.iteraplan.businesslogic.reports.query.options.GraphicalReporting.Dimension.DimensionOptionsBean;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.visio.model.Page;
import de.iteratec.visio.model.Shape;
import de.iteratec.visio.model.exceptions.MasterNotFoundException;


public abstract class VisioAttributeLegend<T> {

  public static final String    VISIO_SHAPE_NAME_LEGEND_GROUP_CONTAINER = "Visio-Legend";
  // 11 * 0.0139 -> ptToInch
  protected static final double TXT_SIZE_11_PTS                         = 0.1529;

  private double                labelOffset;
  private double                pinX;
  private double                pinY;

  private Shape                 visioLegendContainer;
  private Dimension<T>          dim;
  private Locale                locale;
  private double                legendEntryHeight;
  private double                legendHeightInInch;

  protected abstract String getHeaderShapeName();

  /**
   * Creates a legend entry according to the specified parameters
   * @param shapeName
   *          Name of the legend entry's mastershape
   * @param key
   *          key for this legend entry
   * @param label
   *          label for this legend entry (in most cases the same as key)
   * @throws MasterNotFoundException
   */
  protected abstract void createLegendEntry(String shapeName, String key, String label) throws MasterNotFoundException;

  protected double calculateLabelOffset(Shape headerShape) {
    return 0.25;
  }

  protected double calculatePinX(Shape headerShape) {
    return 0;
  }

  protected double calculatePinY(Shape headerShape, int numberOfLegendEntries) {
    return headerShape.getHeight() * (numberOfLegendEntries - 1);
  }

  protected double getLabelOffset() {
    return labelOffset;
  }

  protected void setLabelOffset(double labelOffset) {
    this.labelOffset = labelOffset;
  }

  protected double getPinX() {
    return pinX;
  }

  protected void setPinX(double pinX) {
    this.pinX = pinX;
  }

  protected double getPinY() {
    return pinY;
  }

  protected void setPinY(double pinY) {
    this.pinY = pinY;
  }

  private int estimateLegendHeight(Dimension<?> dimension) {
    int numberOfLegendEntries = dimension.getValues().size() + 1;
    if (dimension.hasUnspecificValue()) {
      numberOfLegendEntries++;
    }
    return numberOfLegendEntries;
  }

  /**
   * Initializes a new legend for a given configuration and page.
   * @param page
   *          Page object where we add the new legend
   * @param dimension
   *          Dimension object with information about attribute values and assigned legend entries
   * @param headline
   * @param currentLocale
   * @throws MasterNotFoundException
   */
  public void initializeLegend(Page page, Dimension<T> dimension, String headline, Locale currentLocale) throws MasterNotFoundException {

    this.dim = dimension;
    this.locale = currentLocale;
    if (!dimension.getValues().isEmpty()) {

      int numberOfLegendEntries = estimateLegendHeight(dimension);

      // Create the container
      visioLegendContainer = page.createNewShape(VISIO_SHAPE_NAME_LEGEND_GROUP_CONTAINER);

      // Initialize the header entry
      Shape legendHeader = getVisioLegendContainer().createNewInnerShape(getHeaderShapeName());
      legendHeader.setFieldValue(headline);
      legendHeader.setCharSize(TXT_SIZE_11_PTS);

      // Retrieve original coordinates of the legend box shape
      double legendBoxOriginalX = legendHeader.getPinX();
      double legendBoxOriginalY = legendHeader.getPinY();

      // Estimate the container height and width
      legendHeightInInch = legendHeader.getHeight() * numberOfLegendEntries;
      legendEntryHeight = legendHeader.getHeight();
      getVisioLegendContainer().setSize(legendHeader.getWidth(), legendHeightInInch);

      // Estimate the coordinates for the first entry relative to the container
      setPinY(calculatePinY(legendHeader, numberOfLegendEntries));
      setPinX(calculatePinX(legendHeader));
      setLabelOffset(calculateLabelOffset(legendHeader));

      // Set position for the first entry
      legendHeader.setPosition(0, pinY);

      setPosition(new Coordinates(legendBoxOriginalX, legendBoxOriginalY));
    }
  }

  /**
   * Creates the legend entries for this legend
   * @param shapeName
   *          Name of the legend entrie's master shape
   * @throws MasterNotFoundException 
   */
  public void createLegendEntries(String shapeName) throws MasterNotFoundException {
    if (getVisioLegendContainer() == null) {
      return;
    }
    if (getDim() != null) {
      for (String key : getDim().getValues()) {
        String prefix = getPrefix(getDim(), key);
        createLegendEntry(shapeName, key, prefix + key);
        pinY = pinY - getLegendEntryHeight();
      }
      if (getDim().hasUnspecificValue()) {
        // create label for unspecified values
        String unspecifiedValue = MessageAccess.getStringOrNull(DimensionOptionsBean.DEFAULT_VALUE, locale);
        createLegendEntry(shapeName, unspecifiedValue, unspecifiedValue);
      }
    }
  }

  private String getPrefix(Dimension<?> dim, String val) {
    String prefix = "";
    if (dim instanceof ColorRangeDimension) {
      prefix = MessageAccess.getStringOrNull(((ColorRangeDimension) dim).getLegendPrefixKeyFor(val), locale);
      prefix += ": ";
    }
    return prefix;
  }

  /**
   * Sets the position of the legend
   * @param position
   *          Coordinates
   */
  public void setPosition(final Coordinates position) {
    if (getVisioLegendContainer() != null && position != null) {
      getVisioLegendContainer().setPosition(position.getX(), position.getY() - getLegendHeightInInch());
    }
  }

  public Coordinates getPosition() {
    if (getVisioLegendContainer() != null) {
      return new Coordinates(getVisioLegendContainer().getPinX(), getVisioLegendContainer().getPinY() + getLegendHeightInInch());
    }
    return new Coordinates(0, 0);
  }

  public double getLegendHeightInInch() {
    return legendHeightInInch;
  }

  protected Shape getVisioLegendContainer() {
    return visioLegendContainer;
  }

  protected double getLegendEntryHeight() {
    return legendEntryHeight;
  }

  protected Dimension<T> getDim() {
    return dim;
  }
}
