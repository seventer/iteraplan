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
package de.iteratec.svg.model.impl;

import java.util.ArrayList;
import java.util.List;

import de.iteratec.svg.styling.SvgBaseStyling;


/**
 * This class is supposed to be used as a bean to store the properties of the master shape which are
 * then to be applied to every shape created from this master. As we prefer not to work with the DOM
 * we read all the available properties only once and then apply them to the newly created shapes.
 */
public class MasterShapeProperties {

  private List<String>   shapeStyles;
  private List<String>   textStyles;
  private SvgBaseStyling defaultStyle;
  private SvgBaseStyling defaultTextStyle;

  private double         shapeHeight;
  private double         shapeWidth;
  private double         shapeX;
  private double         shapeY;

  /**
   * We use the constructor to set the default values of some shape properties, so that they can be
   * applied even if the master shape itself does not provide any information. This gives us the
   * security that all values are defined and initialized during the creation of every shape. On the
   * other hand it does not affect the "undefined" shapes, because those properties have some effect
   * if and only if the corresponding element in the DOM is defined.
   */
  public MasterShapeProperties() {

    this.shapeStyles = new ArrayList<String>();
    this.textStyles = new ArrayList<String>();

    this.defaultStyle = new SvgBaseStyling();
    defaultStyle.setAttribute(SvgBaseStyling.FILL_COLOR, "ffffff");
    defaultStyle.setAttribute(SvgBaseStyling.STROKE_COLOR, "000000");
    defaultStyle.setAttribute(SvgBaseStyling.STROKE_LINE_PATTERN, "none");
    defaultStyle.setAttribute(SvgBaseStyling.STROKE_WITDH, "0.4");

    this.defaultTextStyle = new SvgBaseStyling();
    defaultTextStyle.setAttribute(SvgBaseStyling.FILL_COLOR, "000000");
    defaultTextStyle.setAttribute(SvgBaseStyling.FONT_SIZE, "6");
    defaultTextStyle.setAttribute(SvgBaseStyling.FONT_ALIGN, "start");
    defaultTextStyle.setAttribute(SvgBaseStyling.FONT_FAMILY, "Arial");

  }

  public List<String> getShapeStyles() {
    return shapeStyles;
  }

  public void setShapeStyles(List<String> shapeStyles) {
    this.shapeStyles = shapeStyles;
  }

  public List<String> getTextStyles() {
    return textStyles;
  }

  public void setTextStyles(List<String> textStyles) {
    this.textStyles = textStyles;
  }

  public SvgBaseStyling getDefaultStyle() {
    return defaultStyle;
  }

  public void setDefaultStyle(SvgBaseStyling defaultStyle) {
    this.defaultStyle = defaultStyle;
  }

  public SvgBaseStyling getDefaultTextStyle() {
    return defaultTextStyle;
  }

  public void setDefaultTextStyle(SvgBaseStyling defaultTextStyle) {
    this.defaultTextStyle = defaultTextStyle;
  }

  public double getShapeHeight() {
    return shapeHeight;
  }

  public void setShapeHeight(double shapeHeight) {
    this.shapeHeight = shapeHeight;
  }

  public double getShapeWidth() {
    return shapeWidth;
  }

  public void setShapeWidth(double shapeWidth) {
    this.shapeWidth = shapeWidth;
  }

  public double getShapeX() {
    return shapeX;
  }

  public void setShapeX(double shapeX) {
    this.shapeX = shapeX;
  }

  public double getShapeY() {
    return shapeY;
  }

  public void setShapeY(double shapeY) {
    this.shapeY = shapeY;
  }

}
