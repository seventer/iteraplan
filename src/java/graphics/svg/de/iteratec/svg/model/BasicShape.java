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

import java.awt.geom.Rectangle2D;
import java.util.List;

import de.iteratec.svg.styling.SvgBaseStyling;


/**
 * This interface specifies the functionality that covers all shapes - those with and those without
 * a master. For the shapes that have no master (the basic shapes) a master is simulated through the
 * {@link SVG_BASIC_SHAPES}.
 */
public interface BasicShape {

  /**
   * Creates a new Shape as inner shape of this one. Note that the inner shapes' position is then
   * relative to this of its parent shape and the inner shape is then being retranslated and
   * rescaled with the parent.
   * 
   * @param masterName
   *          The unique name of the master to use.
   * @return The new inner shape.
   * @throws MasterNotFoundException
   *           Iff the master can not be found.
   */
  Shape createNewInnerShape(String masterName) throws SvgExportException;

  /**
   * Creates a new Shape as inner shape of this one. Note that the inner shapes' position is then
   * relative to this of its parent shape and the inner shape is then being retranslated and
   * rescaled with the parent.
   * 
   * @param masterName
   *          The unique name of the master to use.
   * @param layer
   *          The internal layer of the new inner shape.
   * @return The new inner shape.
   * @throws MasterNotFoundException
   *           Iff the master can not be found.
   */
  Shape createNewInnerShape(String masterName, int layer) throws SvgExportException;

  /**
   * Creates a new basic inner shape in this shape. The coordinates of the inner shape are
   * accordingly relative to the coordinates of this shape.
   * 
   * @param shapeType
   *          The {@link SVG_BASIC_SHAPES} type of the new inner shape.
   * @return The new basic shape instance.
   * @throws SvgExportException
   *           If some error occurs while instantiating the new shape.
   */
  BasicShape createNewBasicInnerShape(String shapeType) throws SvgExportException;

  /**
   * Creates a new basic inner shape in this shape. The coordinates of the inner shape are
   * accordingly relative to the coordinates of this shape.
   * 
   * @param shapeType
   *          The {@link SVG_BASIC_SHAPES} type of the new inner shape.
   * @param layer
   *          The internal layer of the new inner shape.
   * @return The new basic shape instance.
   * @throws SvgExportException
   *           If some error occurs while instantiating the new shape.
   */
  BasicShape createNewBasicInnerShape(String shapeType, int layer) throws SvgExportException;

  /**
   * Retrieves the master this shape is based upon. The master is a collection of shapes that was
   * used to create this shape. Note that the master is not a shape itself, to get the shape this
   * shape is based upon call getMasterShape().
   * 
   * @return The object representing the master.
   * @throws MasterNotFoundException
   *           Iff no master was found for the shape.
   */
  String getMasterName();

  /**
   * Places the shape at the given location. Note that the SVG (0,0) is the upper left corner with Y
   * increasing downwards and X increasing rightwards.
   * 
   * @param x
   *          The new horizontal position.
   * @param y
   *          The new vertical position.
   */
  void setPosition(double x, double y);

  /**
   * Sets the rotation angle of the shape.
   * 
   * @param angle
   *          The angle to set the shape to, in degrees, zero is default, counterclockwise.
   */
  void setAngle(double angle);

  /**
   * Resizes the shape to the given dimensions.
   * 
   * @param width
   *          The new width of the shape.
   * @param height
   *          The new height of the shape.
   */
  void setSize(double width, double height);

  /**
   * The width of the shape. This is either the width defined on the shape or the width of the
   * master shape if no there is no explicit width on this shape.
   * 
   * @return The width of the shape.
   * @throws IllegalStateException
   *           iff the shape has no width attached or it can not be parsed
   */
  double getWidth() throws IllegalStateException;

  /**
   * The height of the shape.
   * 
   * @return The height of the shape.
   * @throws IllegalStateException
   *           iff the shape has no height attached or it cannot be parsed
   */
  double getHeight();

  /**
   * The horizontal position of the shape. This position is the position of the pin point, which is
   * at the left upper corner of the rectangle bounding the shape.
   * 
   * @return The horizontal position of the shape.
   * @throws IllegalStateException
   *           iff the shape has no position attached or it cannot be parsed
   */
  double getPinX();

  /**
   * The vertical position of the shape. This position is the position of the pin point, which is at
   * the left upper corner of the rectangle bounding the shape.
   * 
   * @return The vertical position of the shape.
   * @throws IllegalStateException
   *           iff the shape has no position attached or it cannot be parsed
   */
  double getPinY();

  /**
   * Retrieves the absolute coordinates of the shape. Will be equal to getPinX() if the shape is a
   * top level shape. If the shape is an inner shape, it's absolute X coordinate will be determined.
   * 
   * @return The absolute X coordinate of the shape.
   */
  double getAbsolutePinX();

  /**
   * Retrieves the absolute coordinates of the shape. Will be equal to getPinY() if the shape is a
   * top level shape. If the shape is an inner shape, it's absolute Y coordinate will be determined.
   * 
   * @return The absolute Y coordinate of the shape.
   */
  double getAbsolutePinY();

  /**
   * Retrieves the identifier for the shape. Each shape unique name which is returned here.
   * 
   * @return The identifier for the Shape. Will not be null.
   */
  String getID();

  /**
   * Calculates the bounding box of the shape. Currently not supported.
   * 
   * @return the bounding box of the underlying shape in SVG's coordinate space
   * @throws IllegalStateException
   *           iff the shape has not been placed and sized yet
   */
  Rectangle2D getBoundingBox() throws IllegalStateException;

  /**
   * Adds the CSS class with this unique name to the list of classes to be applied to the shape.
   * 
   * @param className
   *          - The name of the css class.
   */
  void addCSSClass(String className) throws SvgExportException;

  /**
   * Disables the use of CSS styles for the current shape. Overrides the global setting that can be
   * set in the document.
   * 
   * @param enable
   */
  void setShapeCSSEnabled(boolean enable);

  /**
   * Retrieves whether CSS styling is enabled for the current shape.
   * 
   * @return true if CSS is on, false otherwise.
   */
  boolean isCSSStylingEnabled();

  /**
   * Retrieves the currently selected CSS classes that are to be applied to the shape style.
   * 
   * @return The list of class names.
   */
  List<String> getShapeCSSClassNames();

  /**
   * Sets a list with names of CSS classes to be applied to the shape.
   * 
   * @param shapeStyles
   *          The list with classes.
   */
  void setShapeStyles(List<String> shapeStyles);

  /**
   * Retrieves the current default styling settings.
   * 
   * @return The styling.
   */
  SvgBaseStyling getDefaultStyle();

  /**
   * Sets the default style to be applied to the shape.
   * 
   * @param defaultStyle
   *          The style.
   */
  void setDefaultStyle(SvgBaseStyling defaultStyle);

  /**
   * Makes the shape a link to the specified address.
   * 
   * @param url
   *          The address to be linked.
   */
  void setXLink(String url);

  /**
   * Retrieves the address this shape should link to.
   * 
   * @return The address as string.
   */
  String getXLink();

  /**
   * Sets the layer of the shape. Note that this concerns the global layer of the shape. If the
   * method is called for a lower level shape, the global layer in the document of its top parent
   * will be adjusted. For setting the internal layer on the level of this shape, use
   * setInternalLayer().
   * 
   * @param layer
   *          The layer in the document to which the top level parent of this shape should be
   *          inserted.
   */
  void setShapeLayer(int layer) throws SvgExportException;

  /**
   * Retrieves the layer of this shape. Note that this is the global layer, so the setting only
   * concerns top level shapes (whose parent is null). If the method is called on an inner shape,
   * the layer of its top level parent will be returned. For the layer of an inner shape on its
   * level, use getInternalLayper().
   * 
   * @return The layer of the top level parent of this shape in the document.
   */
  int getShapeLayer();

  /**
   * Sets the internal layer of this shape between all inner shapes on this level. For the global
   * setting use setShapeLayer().
   * 
   * @param layer
   *          The internal layer to which the shape should belong.
   */
  void setInternalLayer(int layer) throws SvgExportException;

  /**
   * Retrieves the internal layer of this shape. This is relative only to other child shapes of the
   * same parent that have the same internal depth (for example, all are first level children).
   * 
   * @return The internal layer number.
   */
  int getInternalLayer();

  /**
   * Specifies masters for the possible basic shapes that also occur as implementations of the
   * {@link BasicShape} interface.
   */
  class SVG_BASIC_SHAPES {
    public static final String CIRCLE    = "CircleBasicShapeRoot";
    public static final String RECTANGLE = "RectangleBasicShapeRoot";
    public static final String TEXT      = "TextBasicShapeRoot";
    public static final String PATH      = "PathBasicShapeRoot";
    public static final String PATTERN   = "PatternBasicShapeRoot";
  }

}
