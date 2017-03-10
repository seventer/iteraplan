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
package de.iteratec.svg.model;

import java.util.List;

import de.iteratec.svg.styling.SvgBaseStyling;


/**
 * This interface provides the additional functionality for the "advanced" shapes. These are the
 * shapes that have a master.
 */
public interface Shape extends BasicShape {

  /**
   * Set text displayed in the shapes' text field.
   */
  void setTextFieldValue(String text);

  /**
   * Retrieves the text of the shapes' text field (if exists).
   * 
   * @return The text string.
   */
  String getTextFieldValue();

  /**
   * Simulates a text box with the dimensions of the shape for the text. Default is disabled. Note
   * that if using CSS Styling, the text font size has either to be set manually in the Java source
   * code or in the template, as the method uses it internally to calculate the bounds of the text
   * box.
   * 
   * @param enable
   */
  void setBoundTextInShape(boolean enable);

  /**
   * Retrieves whether the text box simulation is enabled or disabled for the current shape.
   * 
   * @return true if text box simulation is on, else otherwise.
   */
  boolean isBoundTextInShape();

  /**
   * Sets the line spacing of the text box in points. The method has effect only is the text box is
   * enabled.
   * 
   * @param lineSpacing
   */
  void setLineSpacing(double lineSpacing);

  /**
   * Retrieves the current line spacing of the text box.
   * 
   * @return the current value of the line spacing in pt.
   */
  double getLineSpacing();

  /**
   * Adds the CSS class with this name to the list of classes to be applied to the shapes' text.
   * 
   * @param className
   *          - The name of the css class.
   */
  void addTextCSSClass(String className) throws SvgExportException;

  /**
   * Disables the use of CSS styles for the text of the current shape. Overrides the global setting
   * that can be set in the document.
   * 
   * @param enable
   */
  void setTextCSSEnabled(boolean enable);

  /**
   * Retrieves whether CSS text styling is enabled for the current shape.
   * 
   * @return true is text CSS is on, false otherwise.
   */
  boolean isCSSTextStylingEnabled();

  /**
   * Sets whether the text (if set) of a shape should be resized with the shape. Note: If disabled,
   * the text also occurs on top of any inner shapes that might be inserted.
   * 
   * @param enable
   *          Enables/Disables resizing. Default is enabled.
   */
  void setResizeTextWithShape(boolean enable);

  /**
   * Retrieves whether the text set to be resized with the shape.
   * 
   * @return true if resizing is enabled (which is the case by default) and false otherwise.
   */
  boolean isResizeTextWithShape();

  /**
   * Retrieves the x coordinate of the text.
   * 
   * @return The coordinate as double.
   */
  double getTextPinX();

  /**
   * Retrieves the y coordinate of the text.
   * 
   * @return The coordinate as double.
   */
  double getTextPinY();

  /**
   * Sets the position of the text. Note that this only works if the text is not being resized with
   * the shape in means of {@link #setResizeTextWithShape(boolean)}.
   * 
   * @param x
   *          The x coordinate.
   * @param y
   *          The y coordinate.
   */
  void setTextPosition(double x, double y);

  /**
   * @return horizontal placement of the text anchor relative to its shape. 0: left border of the shape, 1: right border
   */
  double getTextXPlacement();

  /**
   * Sets the horizontal placement of the text anchor relative to its shape.
   * @param ratio
   *           horizontal placement of the text anchor: 0: left border of the shape, 1: right border
   */
  void setTextXPlacement(double ratio);

  /**
   * Retrieves the current rotation angle of the text.
   * 
   * @return The angle in degrees. Default is 0, which is horizontal, left-to-right.
   */
  double getTextAngle();

  /**
   * Sets the angle at which the text should be rotated. Note that this only works if the text is
   * not being resized with the shape in means of {@link #setResizeTextWithShape(boolean)}.
   * 
   * @param textAngle
   *          The angle of rotation in degrees.
   */
  void setTextAngle(double textAngle);

  /**
   * Retrieves the currently selected CSS classes that are to be applied to the shape text style.
   * 
   * @return The list of class names.
   */
  List<String> getShapeTextCSSClassNames();

  /**
   * Sets a list with names of CSS classes to be applied to the shapes' text.
   * 
   * @param textStyles
   *          The list with classes.
   */
  void setTextStyles(List<String> textStyles);

  /**
   * Retrieves the current default text styling settings.
   * 
   * @return The styling.
   */
  SvgBaseStyling getDefaultTextStyle();

  /**
   * Sets the default style to be applied to the shapes' text.
   * 
   * @param defaultTextStyle
   *          The style.
   */
  void setDefaultTextStyle(SvgBaseStyling defaultTextStyle);

  /**
   * Sets the XLink for the image shape
   * @param xLink
   */
  void setImageXLink(String xLink);

  /**
   * @return the XLink for the image shape
   */
  String getImageLink();

  /**
   * Sets the size of the image-shape
   * @param width
   * @param height
   */
  void setImageSize(double width, double height);

  /**
   * @return width of the image-shape
   */
  double getImageWidth();

  /**
   * @return height of the image-shape
   */
  double getImageHeight();

  /**
   * Retrieves whether this shapes' inner shapes are to be rescaled with this one.
   * 
   * @return True if the inner shapes are to be rescales (default value). False otherwise.
   */
  boolean isScaleInnerShapesWithShape();

  /**
   * Sets whether inner shapes are to be scaled with this one. Default is true.
   */
  void setScaleInnerShapesWithShape(boolean scaleInnerShapes);
}