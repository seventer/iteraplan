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
package de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util;

import de.iteratec.iteraplan.businesslogic.common.URLBuilder;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.ViewpointConfiguration;
import de.iteratec.iteraplan.businesslogic.exchange.common.vbb.impl.util.VisualVariableHelper.VisualVariable;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.error.IteraplanTechnicalException;
import de.iteratec.iteraplan.elasticeam.metamodel.EditableMetamodel;
import de.iteratec.iteraplan.elasticeam.metamodel.UniversalTypeExpression;
import de.iteratec.iteraplan.elasticeam.metamodel.builtin.MixinTypeNamed;
import de.iteratec.iteraplan.elasticeam.model.Model;
import de.iteratec.iteraplan.elasticeam.model.UniversalModelExpression;
import de.iteratec.visualizationmodel.ALabeledVisualizationObject;
import de.iteratec.visualizationmodel.Color;
import de.iteratec.visualizationmodel.HorizontalAlignment;
import de.iteratec.visualizationmodel.Rectangle;
import de.iteratec.visualizationmodel.Text;
import de.iteratec.visualizationmodel.VerticalAlignment;


/**
 * Inner VBB creating a planar symbol that further carries a labeling text.
 * @param <T> the type of labeled planar symbol to create.
 */
public class CreateLabeledPlanarSymbol<T extends ALabeledVisualizationObject> extends CreatePlanarSymbol<T> {

  private static final Logger LOGGER = Logger.getLogger(CreateLabeledPlanarSymbol.class);

  private Color               textColor;
  private final boolean[]     textStyle;
  private String              fontName;
  private int                 textSize;
  private HorizontalAlignment horizontalAlignment;
  private VerticalAlignment   verticalAlignment;

  /**
   * Default constructor.
   */
  public CreateLabeledPlanarSymbol() {
    super();
    this.textStyle = new boolean[3];
    this.fontName = "Arial";
    this.textSize = 9;
    this.textColor = Color.BLACK;
  }

  /**{@inheritDoc}**/
  @Override
  protected void computeMyAbstractViewmodel(EditableMetamodel viewmodel, ViewpointConfiguration vpConfig, String prefix) {
    super.computeMyAbstractViewmodel(viewmodel, vpConfig, prefix);
  }

  private Text createTextObject(UniversalModelExpression object) {
    String label = "";
    if(object != null){
      label = getTextForAttribute(object);
    }

    Text text = new Text();
    text.setFillColor(this.textColor);
    text.setFontName(this.fontName);
    text.setTextSize(this.textSize);
    text.setTextStyle(this.textStyle);
    text.setText(label);
    text.setTooltipText(label);
    return text;
  }

  protected Text createText() {
    Text text = new Text();
    text.setTextColor(textColor);
    text.setFontName(fontName);
    text.setTextSize(textSize);
    text.setTextStyle(getTextStyle());
    text.setText("");
    text.setTooltipText("");
    text.setHorizontalAlignment(horizontalAlignment);
    text.setVerticalAlignment(verticalAlignment);
    return text;
  }

  private String getTextForAttribute(UniversalModelExpression instance) {
    return (String) instance.getValue(MixinTypeNamed.NAME_PROPERTY);
  }

  @SuppressWarnings("unchecked")
  public T create() {
    Class<? extends T> symbol = this.getVObjectClass();
    if (symbol == null) {
      this.setVObjectClass((Class<? extends T>) Rectangle.class);
    }
    try {
      T result = symbol.newInstance();
      result.setText(createText());
      return result;
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
    T visObj = super.transform(instance, model, config);
    if (visObj != null) {
      visObj.setText(createTextObject(instance));

      if (horizontalAlignment != null) {
        visObj.getText().setHorizontalAlignment(horizontalAlignment);
      }

      if (verticalAlignment != null) {
        visObj.getText().setVerticalAlignment(verticalAlignment);
      }

      if (getBaseUrl() != null && instance != null) {
        visObj.setLink(URLBuilder.getEntityURL(instance, (UniversalTypeExpression) config.getMappingFor(getAvmClass()), getBaseUrl()));
      }
    }

    return visObj;
  }

  /**
   * @return the color of the labeling text.
   */
  public Color getTextColor() {
    return textColor;
  }

  /**
   * Sets the color of the labeling text.
   * @param textColor the color of the labeling text.
   */
  public void setTextColor(Color textColor) {
    this.textColor = textColor;
  }

  /**
   * @return the style of the labeling text in {boldface, italic, underlined}
   */
  public boolean[] getTextStyle() {
    return this.textStyle.clone();
  }

  /**
   * Sets the style of the labeling text.
   * @param textStyle array with three flags for text styles in {boldface, italic, underlined}.
   */
  public void setTextStyle(boolean[] textStyle) {
    for (int i = 0; i < Math.min(this.textStyle.length, textStyle.length); i++) {
      this.textStyle[i] = textStyle[i];
    }
  }

  /**
   * @return the name of the labeling text's font.
   */
  @VisualVariable
  public String getFontName() {
    return fontName;
  }

  /**
   * Sets the name of the labeling text's font.
   * @param fontName the name of the labeling text's font.
   */
  public void setFontName(String fontName) {
    this.fontName = fontName;
  }

  /**
   * @return the font size of the labeling text.
   */
  @VisualVariable
  public int getTextSize() {
    return textSize;
  }

  /**
   * Sets the font size of the labeling text.
   * @param textSize the font size of the labeling text.
   */
  public void setTextSize(int textSize) {
    this.textSize = textSize;
  }

  /**
   * @return the horizontal alignment of the labeling text in relationship to the surrounding symbol.
   */
  @VisualVariable
  public HorizontalAlignment getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * Sets the horizontal alignment of the labeling text.
   * @param horizontalAlignment the horizontal alignment of the labeling text in relationship to the surrounding symbol.
   */
  public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
    this.horizontalAlignment = horizontalAlignment;
  }

  /**
   * @return the vertical alignment of the labeling text in relationship to the surrounding symbol.
   */
  @VisualVariable
  public VerticalAlignment getVerticalAlignment() {
    return verticalAlignment;
  }

  /**
   * Sets the vertical alignment of the labeling text.
   * @param verticalAlignment the vertical alignment of the labeling text in relationship to the surrounding symbol.
   */
  public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
    this.verticalAlignment = verticalAlignment;
  }
}
