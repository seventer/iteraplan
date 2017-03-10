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
import de.iteratec.svg.model.Shape;
import de.iteratec.svg.model.SvgExportException;
import de.iteratec.svg.styling.SvgBaseStyling;
import de.iteratec.svg.styling.SvgCssStyling;


public class ShapeImpl extends AbstractBasicShapeImpl implements Shape {

  private static final Logger LOGGER = Logger.getIteraplanLogger(ShapeImpl.class);

  private List<String>        textStyles;
  private SvgBaseStyling      defaultTextStyle;

  private String              shapeText;
  private boolean             resizeTextWithShape;
  private double              textPinX;
  private double              textPinY;
  private double              textAngle;
  private boolean             textBoundingEnabled;
  private double              lineSpacing;
  private boolean             scaleInnerShapes;
  private double              textXPlacement;
  private String              imageXLink;
  private double              imageWidth;
  private double              imageHeight;

  // CSS Styling properties
  private boolean             shapeTextCSSStylingEnabled;

  public ShapeImpl(String masterShapeName, String shapeId, DocumentImpl document) {

    super(masterShapeName, shapeId, document);

    this.resizeTextWithShape = true;
    this.textAngle = 0;
    this.textPinX = 0;
    this.textPinY = 0;
    this.textXPlacement = 0.5;
    this.textBoundingEnabled = false;
    this.shapeText = "";
    this.setBasicShape(false);
    this.scaleInnerShapes = true;

    // Init CSS
    textStyles = new ArrayList<String>();
    this.shapeTextCSSStylingEnabled = true;
  }

  public void setTextFieldValue(String text) {
    shapeText = text;

  }

  public String getTextFieldValue() {
    return shapeText;
  }

  public void setBoundTextInShape(boolean enable) {
    this.textBoundingEnabled = enable;
  }

  public boolean isBoundTextInShape() {
    return textBoundingEnabled;
  }

  public void setLineSpacing(double lineSpacing) {
    this.lineSpacing = lineSpacing;
  }

  public double getLineSpacing() {
    return lineSpacing;
  }

  public List<String> getShapeTextCSSClassNames() {
    return textStyles;
  }

  public void addTextCSSClass(String uniqueClassName) throws SvgExportException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Adding text CSS class " + uniqueClassName + " to shape with Id: " + this.getID());
    }

    boolean found = false;
    for (SvgCssStyling styleClass : getDocument().getCSSStyleClasses()) {
      if (styleClass.getCssClassName().matches(".*" + uniqueClassName + ".*")) {
        if (!textStyles.contains(styleClass.getCssClassName())) {
          textStyles.add(styleClass.getCssClassName());
        }
        found = true;
        break;
      }
    }
    if (!found) {
      LOGGER.error("The CSS class " + uniqueClassName + " is undefined.");
      throw new SvgExportException("The CSS class with the name " + uniqueClassName + " is undefined.");
    }

  }

  public boolean isCSSTextStylingEnabled() {
    return shapeTextCSSStylingEnabled;
  }

  public void setTextCSSEnabled(boolean status) {
    shapeTextCSSStylingEnabled = status;
  }

  public void setResizeTextWithShape(boolean enable) {
    this.resizeTextWithShape = enable;
  }

  public boolean isResizeTextWithShape() {
    return resizeTextWithShape;
  }

  public double getTextPinX() {
    return textPinX;
  }

  public double getTextPinY() {
    return textPinY;
  }

  public double getTextXPlacement() {
    return textXPlacement;
  }

  public void setTextXPlacement(double textXPlacement) {
    this.textXPlacement = textXPlacement;
  }

  public void setTextPosition(double x, double y) {
    this.textPinX = x;
    this.textPinY = y;
  }

  public double getTextAngle() {
    return textAngle;
  }

  public void setTextAngle(double textAngle) {
    this.textAngle = textAngle;
  }

  public void setTextStyles(List<String> textStyles) {
    this.textStyles = textStyles;
  }

  public SvgBaseStyling getDefaultTextStyle() {
    return defaultTextStyle;
  }

  public void setDefaultTextStyle(SvgBaseStyling defaultTextStyle) {
    this.defaultTextStyle = defaultTextStyle;
  }

  public boolean isScaleInnerShapesWithShape() {
    return this.scaleInnerShapes;
  }

  public void setScaleInnerShapesWithShape(boolean scaleInnerShapes) {
    this.scaleInnerShapes = scaleInnerShapes;
  }

  @Override
  protected void applySettingsToDom(Node domShape) {
    // TODO can we have some generic impl here already?

  }

  @Override
  protected String buildTransformationString() {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Building transformation string for shape with Id: " + this.getID());
    }

    adjustShapePositionForTransformation();

    // create the resulting transformation string
    // TODO maybe it's more concise to use a format string here? (java.lang.String#format)
    StringBuffer result = new StringBuffer(50);

    result.append("matrix(");
    // Insert the new width

    result.append(getWidth() / getDocument().getMasterShapePropertiesMap().get(getMasterName()).getShapeWidth());
    // Adjust
    result.append(",0,0,");
    // Insert the new Height
    result.append(getHeight() / getDocument().getMasterShapePropertiesMap().get(getMasterName()).getShapeHeight());
    // Adjust
    result.append(',');
    // Insert new X coordinate
    result.append(getPinX());
    // Adjust
    result.append(',');
    // Insert the new Y coordinate
    result.append(getPinY());
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

  public void setImageXLink(String xLink) {
    this.imageXLink = xLink;
  }

  public String getImageLink() {
    return imageXLink;
  }

  public void setImageSize(double width, double height) {
    this.imageWidth = width;
    this.imageHeight = height;
  }

  public double getImageWidth() {
    return this.imageWidth;
  }

  public double getImageHeight() {
    return this.imageHeight;
  }

}
