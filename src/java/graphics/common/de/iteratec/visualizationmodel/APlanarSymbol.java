/*
 * Copyright 2011 Christian M. Schweda & iteratec
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.iteratec.visualizationmodel;

public abstract class APlanarSymbol extends APrimitiveSymbol {

  private float     width;
  private float     height;

  private float     posx;
  private float     posy;
  private Color     fillColor;
  private Color     borderColor;
  private float     borderWidth;
  private LineStyle lineStyle;

  /* (non-Javadoc)
   * @see de.tum.sebis.visualization.visualizationmodel.APrimitiveSymbol_#setWidth(float)
   */
  @Override
  public void setWidth(float width) {
    this.width = width;
  }

  /* (non-Javadoc)
   * @see de.tum.sebis.visualization.visualizationmodel.APrimitiveSymbol_#setHeight(float)
   */
  @Override
  public void setHeight(float height) {
    this.height = height;
  }

  /* (non-Javadoc)
   * @see de.tum.sebis.visualization.visualizationmodel.APrimitiveSymbol_#setXpos(float)
   */
  @Override
  public void setXpos(float newXpos) {
    posx = newXpos;
  }

  /* (non-Javadoc)
   * @see de.tum.sebis.visualization.visualizationmodel.APrimitiveSymbol_#setYpos(float)
   */
  @Override
  public void setYpos(float newYpos) {
    posy = newYpos;
  }

  /**
   * Sets the border color.
   * 
   * @param newBorderColor the border color to set
   */
  public void setBorderColor(Color newBorderColor) {
    borderColor = newBorderColor;
  }

  /**
   * Sets the border width.
   * 
   * @param newWidth The border width to set.
   */
  public void setBorderWidth(float newWidth) {
    borderWidth = newWidth;
  }

  /**
   * Sets the fill color.
   * 
   * @param newFillColor the fill color to set
   */
  public void setFillColor(Color newFillColor) {
    fillColor = newFillColor;
  }

  /**
   * Sets the line style.
   * 
   * @param newLineStyle the line style to set
   */
  public void setLineStyle(LineStyle newLineStyle) {
    lineStyle = newLineStyle;
  }

  /* (non-Javadoc)
   * @see de.tum.sebis.visualization.visualizationmodel.APrimitiveSymbol_#getXpos()
   */
  @Override
  public float getXpos() {
    return posx;
  }

  /* (non-Javadoc)
   * @see de.tum.sebis.visualization.visualizationmodel.APrimitiveSymbol_#getYpos()
   */
  @Override
  public float getYpos() {
    return posy;
  }

  /**
   * Gets the border color.
   * 
   * @return the primitive's border color
   */
  public Color getBorderColor() {
    return borderColor;
  }

  /**
   * Gets the border width.
   * 
   * @return The primitive's border width.
   */
  public float getBorderWidth() {
    return borderWidth;
  }

  /**
   * Gets the fill color.
   * 
   * @return the primitive's fill color
   */
  public Color getFillColor() {
    return fillColor;
  }

  /**
   * Gets the line style.
   * 
   * @return the primitive's line style
   */
  public LineStyle getLineStyle() {
    return lineStyle;
  }

  /* (non-Javadoc)
   * @see de.tum.sebis.visualization.visualizationmodel.APrimitiveSymbol_#getWidth()
   */
  @Override
  public float getWidth() {
    return this.width;
  }

  /* (non-Javadoc)
   * @see de.tum.sebis.visualization.visualizationmodel.APrimitiveSymbol_#getHeight()
   */
  @Override
  public float getHeight() {
    return this.height;
  }
}
