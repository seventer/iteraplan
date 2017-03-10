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
package de.iteratec.iteraplan.common.util;

/**
 * Utility class for simple conversion from any unit to inches and the other way round.
 */
public final class InchConverter {

  private InchConverter() {
    // private constructor to prevent instantiation
  }

  /**
   * Converts milimeters to inches.
   * 
   * @param mm
   *          The value in milimeters.
   * @return The value in inches.
   */
  public static double mmToInches(double mm) {

    return mm * 0.0393700787;
  }

  /**
   * Converts centimeters to inches.
   * 
   * @param cm
   *          The value in centimeters.
   * @return The value in inches.
   */
  public static double cmToInches(double cm) {

    return cm * 0.393700787;
  }

  /**
   * Converts points to inches.
   * 
   * @param pt
   *          The value in points.
   * @param resolution
   *          The resolution (e.g. 72 pt per inch).
   * @return The value in inches.
   */
  public static double ptToInches(double pt, int resolution) {

    return (1.0 / resolution) * pt;
  }

  /**
   * Converts inches to milimeters.
   * 
   * @param inches
   *          The value in inches.
   * @return The value in millimeters.
   */
  public static double inchesToMm(double inches) {

    return inches / 0.0393700787;
  }

  /**
   * Converts inches to centimeters.
   * 
   * @param inches
   *          The value in inches.
   * @return The value in centimeters.
   */
  public static double inchesToCm(double inches) {

    return inches / 0.393700787;
  }

  /**
   * Converts points to inches.
   * 
   * @param inches
   *          The value in inches.
   * @param resolution
   *          The resolution (e.g. 72 pt per inch).
   * @return The value in points.
   */
  public static double inchesToPt(double inches, int resolution) {

    return resolution * inches;
  }

  /**
   * Converts pixel to inches
   * @param pixel
   *           The value in pixel
   * @param dpi
   *           The dpi that is used by the system
   * @return The value in inches
   */
  public static double pixelToInches(double pixel, int dpi) {

    return (pixel / dpi) * (Math.sqrt(2) / 2);
  }
}
