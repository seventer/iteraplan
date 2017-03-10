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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb2.vbb.impl.util;

import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.VisualVariableHelper.VisualVariable;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.visualizationmodel.APlanarSymbol;
import de.iteratec.visualizationmodel.Color;
import de.iteratec.visualizationmodel.LineStyle;
import de.iteratec.visualizationmodel.Rectangle;


/**
 * Inner VBB for creating instances of a given planar symbol.
 * @param <T> The type of the symbol to create.
 */
public class CreatePlanarSymbol<T extends APlanarSymbol> extends CreateVisualizationObject<T> {

  private static final Logger LOGGER = Logger.getLogger(CreatePlanarSymbol.class);

  private Color               fillColor;
  private Color               borderColor;

  private float               borderWidth;
  private LineStyle           lineStyle;

  private float               width;

  private float               height;

  /**
   * Default constructor.
   */
  @SuppressWarnings("unchecked")
  public CreatePlanarSymbol() {
    super();
    setVObjectClass((Class<T>) Rectangle.class);
    this.width = 80f;
    this.height = 30f;
  }

  @SuppressWarnings("unchecked")
  public T create() {
    Class<? extends T> symbol = this.getVObjectClass();
    if (symbol == null) {
      this.setVObjectClass((Class<? extends T>) Rectangle.class);
    }
    try {
      return symbol.newInstance();
    } catch (InstantiationException e) {
      LOGGER.error("Could not instantiate Symbol.", e);
      throw new IteraplanTechnicalException(e);
    } catch (IllegalAccessException e) {
      LOGGER.error("Could not instantiate Symbol.", e);
      throw new IteraplanTechnicalException(e);
    }
  }

  /**{@inheritDoc}**/
  @Override
  public T transform(UniversalModelExpression instance, Model model, ViewpointConfiguration config) {
    T visObject = super.transform(instance, model, config);
    if (visObject != null) {
      if (fillColor != null) {
        visObject.setFillColor(fillColor);
      }
      if (borderColor != null) {
        visObject.setBorderColor(borderColor);
      }
      if (borderWidth != 0) {
        visObject.setBorderWidth(borderWidth);
      }
      if (lineStyle != null) {
        visObject.setLineStyle(lineStyle);
      }
      if (width != 0) {
        visObject.setWidth(width);
      }
      if (height != 0) {
        visObject.setHeight(height);
      }
    }

    return visObject;
  }

  /**
   * @return the width of the symbol's border.
   */
  @VisualVariable
  public float getBorderWidth() {
    return borderWidth;
  }

  /**
   * Sets the width of the symbol's border.
   * @param borderWidth the width of the symbol's border.
   */
  public void setBorderWidth(float borderWidth) {
    this.borderWidth = borderWidth;
  }

  /**
   * @return the width of the symbol.
   */
  @VisualVariable
  public float getWidth() {
    return width;
  }

  /**
   * Sets the width of the symbol.
   * @param width the width of the symbol.
   */
  public void setWidth(float width) {
    this.width = width;
  }

  /**
   * @return the height of the symbol.
   */
  @VisualVariable
  public float getHeight() {
    return height;
  }

  /**
   * Sets the height of the symbol.
   * @param height the height of the symbol.
   */
  public void setHeight(float height) {
    this.height = height;
  }

  /**
   * @return the fill color of the symbol.
   */
  @VisualVariable
  public Color getFillColor() {
    return fillColor;
  }

  /**
   * Sets the fill color of the symbol.
   * @param fillColor the fill color of the symbol.
   */
  public void setFillColor(Color fillColor) {
    this.fillColor = fillColor;
  }

  /**
   * @return the border color of the symbol.
   */
  @VisualVariable
  public Color getBorderColor() {
    return borderColor;
  }

  /**
   * Sets the border color of the symbol.
   * @param borderColor the border color of the symbol.
   */
  public void setBorderColor(Color borderColor) {
    this.borderColor = borderColor;
  }

  /**
   * @return the style of the symbol's border line
   */
  @VisualVariable
  public LineStyle getLineStyle() {
    return lineStyle;
  }

  /**
   * Sets the style of the symbol's border line.
   * @param lineStyle the style of the symbol's border line.
   */
  public void setLineStyle(LineStyle lineStyle) {
    this.lineStyle = lineStyle;
  }

}
