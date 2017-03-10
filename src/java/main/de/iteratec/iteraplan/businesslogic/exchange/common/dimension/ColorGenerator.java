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
package de.iteratec.iteraplan.businesslogic.exchange.common.dimension;

import java.awt.Color;
import java.math.BigDecimal;

import de.iteratec.iteraplan.common.Constants;


/**
 * Generates Colors for {@link BigDecimal} values. Two bounds and their colors will be used in this class.
 * For each value in these bounds, a color will be calculated.
 */
public class ColorGenerator {

  private Color   defaultColor        = Color.decode("#" + Constants.DEFAULT_GRAPHICAL_EXOPORT_COLOR);
  private Color   lowerBoundColor     = getDefaultColor();
  private Color   upperBoundColor     = getDefaultColor();

  private boolean useOutOfBoundsColor = false;
  private Color   outOfBoundsColor    = getDefaultColor();

  private float   lowerBound          = 0;
  private float   upperBound          = 0;

  /**
   * Instantiates a new color generator, which behaves in accordance with the provided parameters as follows:
   * <li>If enableOutOfBoundsColor is <b>false</b>: In this case the maximal and minimal values which represent the
   * upper and lower bound color accordingly are determined on the basis of the provided values.
   * I.e. in this case the range of the attribute is assumed to be defined by the provided collection of values.
   * Any value which is below the minValue will be colored with the lower bound color. Any value above the maxValue
   * will be colored with the upper bound color.</li>
   * <li>If enableOutOfBoundsColor is <b>true</b>: In this case, the min and max values are again determined on the basis
   * of the provided collection of values. The lower and upper bounds can furthermore be explicitly set through
   * corresponding setters of this class. When a value is not between the (calculated or set) min and max values,
   * the out of bounds color is returned. By default, this is the unspecified color. The outOfBounds color can
   * be adjusted through a corresponding setter. </li>
   * @param lowerBoundColor
   * @param upperBoundColor
   * @param enableOutOfBoundsColor
   * @param values
   */
  public ColorGenerator(String lowerBoundColor, String upperBoundColor, boolean enableOutOfBoundsColor, Iterable<BigDecimal> values) {
    setLowerBoundColor(lowerBoundColor);
    setUpperBoundColor(upperBoundColor);
    this.useOutOfBoundsColor = enableOutOfBoundsColor;
    findBounds(values);
  }

  public Color generateColor(BigDecimal key) {
    if (key == null) {
      return getDefaultColor();
    }

    float value = key.floatValue();

    if (useOutOfBoundsColor && (value < getLowerBound() || value > getUpperBound())) {
      return getOutOfBoundsColor();
    }

    float factor = (value - getLowerBound()) / (getUpperBound() - getLowerBound());

    if (factor < 0) {
      factor = 0;
    }
    else if (factor > 1) {
      factor = 1;
    }

    // convert colors to hsv
    float[] lowerHSV = toHSV(getLowerBoundColor());
    float lh = lowerHSV[0];
    float ls = lowerHSV[1];
    float lv = lowerHSV[2];
    float[] upperHSV = toHSV(getUpperBoundColor());
    float uh = upperHSV[0];
    float us = upperHSV[1];
    float uv = upperHSV[2];

    // h goes in a circle, so we can add 1 and the color does not change
    // this will modify the h values, so that the length on the circle border becomes minimal
    if (Math.abs((lh + 1) - uh) < Math.abs(lh - uh)) {
      lh++;
    }
    else if (Math.abs(lh - (uh + 1)) < Math.abs(lh - uh)) {
      uh++;
    }

    float h = calculateNewColor(lh, uh, factor);
    float s = calculateNewColor(ls, us, factor);
    float v = calculateNewColor(lv, uv, factor);
    return Color.getHSBColor(h, s, v);
  }

  private float[] toHSV(Color c) {
    return Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), new float[3]);
  }

  private float calculateNewColor(float lowerBoundColorValue, float upperBoundColorValue, float factor) {
    return upperBoundColorValue * factor + lowerBoundColorValue * (1 - factor);
  }

  public final void findBounds(Iterable<BigDecimal> values) {
    float min = Float.MAX_VALUE;
    float max = Float.MIN_VALUE;
    for (BigDecimal bigDecimalValue : values) {
      float value = bigDecimalValue.floatValue();
      min = Math.min(value, min);
      max = Math.max(value, max);
    }
    if (min < max) {
      setLowerBound(min);
      setUpperBound(max);
    }
    else {
      setLowerBound(0);
      setUpperBound(0);
    }
  }

  public Color getOutOfBoundsColor() {
    return this.outOfBoundsColor;
  }

  public void setOutOfBoundsColor(Color outOfBoundsColor) {
    this.outOfBoundsColor = outOfBoundsColor;
  }

  public void setOutOfBoundsColor(String outOfBoundsColor) {
    setOutOfBoundsColor(Color.decode("#" + outOfBoundsColor));
  }

  public Color getDefaultColor() {
    return defaultColor;
  }

  public void setDefaultColor(Color defaultColor) {
    this.defaultColor = defaultColor;
  }

  public void setDefaultColor(String defaultColor) {
    setDefaultColor(Color.decode("#" + defaultColor));
  }

  public Color getLowerBoundColor() {
    return lowerBoundColor;
  }

  public final void setLowerBoundColor(Color lowerBoundColor) {
    this.lowerBoundColor = lowerBoundColor;
  }

  public final void setLowerBoundColor(String lowerBoundColor) {
    setLowerBoundColor(Color.decode("#" + lowerBoundColor));
  }

  public Color getUpperBoundColor() {
    return upperBoundColor;
  }

  public final void setUpperBoundColor(Color upperBoundColor) {
    this.upperBoundColor = upperBoundColor;
  }

  public final void setUpperBoundColor(String upperBoundColor) {
    setUpperBoundColor(Color.decode("#" + upperBoundColor));
  }

  public float getLowerBound() {
    return lowerBound;
  }

  public final void setLowerBound(float lowerBound) {
    this.lowerBound = lowerBound;
  }

  public float getUpperBound() {
    return upperBound;
  }

  public final void setUpperBound(float upperBound) {
    this.upperBound = upperBound;
  }

}
