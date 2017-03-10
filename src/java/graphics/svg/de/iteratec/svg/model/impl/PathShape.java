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

import org.w3c.dom.Node;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.svg.model.BasicShape;


/**
 * Represents an SVG path.
 */
public class PathShape extends AbstractBasicShapeImpl {

  private static final Logger        LOGGER = Logger.getIteraplanLogger(PathShape.class);

  private double                     beginX = 0;
  private double                     beginY = 0;

  private boolean                    startPositionSet;

  private boolean                    closePath;

  private final List<SvgPathElement> pathDescr;

  public PathShape(String shapeId, DocumentImpl document) {

    super(BasicShape.SVG_BASIC_SHAPES.PATH, shapeId, document);
    super.setBasicShape(true);

    this.closePath = false;
    this.startPositionSet = false;
    this.pathDescr = new ArrayList<SvgPathElement>();
  }

  /**
   * Moves the drawing pointer to the specified coordinates.
   * 
   * @param xCor
   * @param yCor
   */
  public void moveTo(double xCor, double yCor) {

    if (!startPositionSet) {
      setStartPos(xCor, yCor);
    }
  }

  private void setStartPos(double xCor, double yCor) {
    double[] adjustedCors = adjustCoordinates(xCor, yCor);
    beginX = adjustedCors[0];
    beginY = adjustedCors[1];
    startPositionSet = true;
    setPosition(beginX, beginY);
  }

  /**
   * Adjusts the Coordinates to make them relative to the parent shape,
   * if there is one.
   * @param xCor
   * @param yCor
   * @return array with the adjusted coordinates: [0]=x; [1]=y
   */
  private double[] adjustCoordinates(double xCor, double yCor) {
    double[] adjustedCors = { xCor, yCor };
    if (getParentShape() != null) {
      adjustedCors[0] -= getParentShape().getPinX();
      adjustedCors[1] -= getParentShape().getPinY();
    }
    return adjustedCors;
  }

  /**
   * Sets a line to be drawn from the current pointer to the coordinates given.
   * 
   * @param xCor
   *          The X coordinate of the end point of the line.
   * @param yCor
   *          The Y coordinate of the end point of the line.
   */
  public void lineTo(double xCor, double yCor) {

    double[] adjustedCors = adjustCoordinates(xCor, yCor);
    pathDescr.add(new SvgLinePathElement(adjustedCors[0], adjustedCors[1]));

    if (!startPositionSet) {
      setStartPos(xCor, yCor);
    }

  }

  /**
   * Draws an elliptical arc determined by the following parameters. See the SVG Specifications for
   * further details.
   * 
   * @param radiusX
   *          The X-Radius of the ellipse.
   * @param radiusY
   *          The Y-Radius of the ellipse.
   * @param rotationAngle
   *          The angle of rotation from the current coordinate system.
   * @param largeArcFlag
   *          If set to one, the larger arc (the one that covers more than 180 degrees) will be
   *          drawn. Otherwise the shorter one will be drawn.
   * @param sweepFlag
   *          Determines the direction of the drawing. Setting this flag to one would draw in the
   *          positive (in mathematical sense) direction. Setting to zero would force drawing
   *          clockwise.
   * @param endX
   *          The X-Coordinate of the end point of the arc.
   * @param endY
   *          The Y-Coordinate of the end point of the arc.
   */
  public void ellipticalArcTo(double radiusX, double radiusY, double rotationAngle, int largeArcFlag, int sweepFlag, double endX, double endY) {

    double[] adjustedEndCors = adjustCoordinates(endX, endY);
    pathDescr.add(new SvgEllipticalArcPathElement(radiusX, radiusY, rotationAngle, largeArcFlag, sweepFlag, adjustedEndCors[0], adjustedEndCors[1]));

    if (!startPositionSet) {
      beginX = 0;
      beginY = 0;
      startPositionSet = true;
      setPosition(beginX, beginY);
    }

  }

  /**
   * This method draws a cubic bezier curve from the current point to the specifier end point,
   * obeying the two control points.
   * 
   * @param x1
   *          The x-coordinate of the first control point.
   * @param y1
   *          The y-coordinate of the first control point.
   * @param x2
   *          The x-coordinate of the second control point.
   * @param y2
   *          The y-coordinate of the second control point.
   * @param endX
   *          The x-coordinate of the end point.
   * @param endY
   *          The y-coordinate of the end point.
   */
  public void cubicBezierCurveTo(double x1, double y1, double x2, double y2, double endX, double endY) {
    double[] firstCors = adjustCoordinates(x1, y1);
    double[] secondCors = adjustCoordinates(x2, y2);
    double[] endCors = adjustCoordinates(endX, endY);

    pathDescr.add(new SvgCubicBezierElement(firstCors[0], firstCors[1], secondCors[0], secondCors[1], endCors[0], endCors[1]));
    if (!startPositionSet) {
      beginX = x1;
      beginY = y1;
      startPositionSet = true;
      setPosition(beginX, beginY);
    }

  }

  public void setClosePath(boolean closePath) {
    this.closePath = closePath;
  }

  /**
   * Rebuilds a path string to be inserted into the svg path node.
   * 
   * @return The string.
   */
  protected String compilePath() {

    // Coordinates are relative to the parent shape (if exists)
    // so we adjust them.
    if (getParentShape() != null) {
      beginX = beginX + getParentShape().getPinX();
      beginY = beginY + getParentShape().getPinY();
      for (SvgPathElement point : pathDescr) {
        point.setXpos(point.getXpos() + getParentShape().getPinX());
        point.setYpos(point.getYpos() + getParentShape().getPinY());
      }
    }

    // We also need to adjust all coordinates so that the path is defined from (0,0) and then
    // translated (via the translation node of the shape) to its actual position. We need this as
    // otherwise rotations are being applied with center point (0,0) which is not consistent with
    // the rotations of all other shapes. By switching the path to (0,0) and then translating it to
    // the actual position we allow the rotation center to be the first point of the path. If this
    // is not specified the rotation point will be (0,0) anyway.

    // Scale all points' coordinates accordingly to the first point of the path.
    for (SvgPathElement pathEl : pathDescr) {
      pathEl.setXpos(pathEl.getXpos() - beginX);
      pathEl.setYpos(pathEl.getYpos() - beginY);
    }

    // Ally transformation for the shape to the position of the very first point of the path.
    // setPosition(beginX, beginY);

    StringBuffer result = new StringBuffer();

    // Append start position
    if (startPositionSet) {
      result.append(SvgPathElement.generateMoveToPathElement(0, 0));
      // result.append(SvgPathElement.generateMoveToPathElement(beginX, beginY));
    }

    // Append content
    for (SvgPathElement pathElement : pathDescr) {
      result.append(pathElement.compilePathElementString());
    }

    // Close path
    if (closePath) {
      result.append(" z");
    }

    return result.toString();
  }

  @Override
  protected String buildTransformationString() {
    StringBuffer result = new StringBuffer(30);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Building transformation string for shape with Id: " + this.getID());
    }

    result.append("matrix(1,0,0,1,");
    // Insert new X coordinate
    result.append(beginX);
    // Adjust
    result.append(',');
    // Insert the new Y coordinate
    result.append(beginY);
    // Close the matrix
    result.append(')');
    // Insert rotation
    if (getAngle() != 0) {
      result.append(" rotate(");
      result.append(getAngle());
      result.append(')');
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Successfully created transformation string for shape with Id " + this.getID());
    }
    return result.toString();
  }

  @Override
  protected void applySettingsToDom(Node domShape) {

    LOGGER.debug("Applying specific shape settings for PathShape to the DOM tree.");

    Node shapePropsNode = SvgDomUtils.getNodeWithId(domShape, DocumentImpl.ATTRIBUTE_VALUE_SHAPE_PROPS);
    SvgDomUtils.setOrCreateAttributeForNode(shapePropsNode, "d", this.compilePath());
  }

}
