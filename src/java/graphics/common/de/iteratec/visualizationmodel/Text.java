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

import org.apache.commons.lang.StringEscapeUtils;

import de.iteratec.visualizationmodel.renderer.AVisualizationObjectVisitor;


/**
 * Primitive representing a textual element.
 * 
 * @author Christian M. Schweda
 *
 * @version 6.0
 */
public class Text extends APlanarSymbol {

  private String              contents;
  private String              tooltipText;
  private Color               textColor = Color.BLACK;
  private final boolean[]     textStyle;
  private String              fontname;
  private int                 textSize;
  private HorizontalAlignment horizontalAlignment;
  private VerticalAlignment   verticalAlignment;
  private boolean             vertical;

  /**
   * Instantiates a new text.
   */
  public Text() {
    this.textStyle = new boolean[3];
    this.horizontalAlignment = HorizontalAlignment.CENTER;
    this.verticalAlignment = VerticalAlignment.MIDDLE;
  }

  public Text(String contents) {
    this();
    this.contents = contents;
  }

  @Override
  public void visit(AVisualizationObjectVisitor visitor) {
    visitor.visit(this);
    visitor.endPrimitive(this);
  }

  /**
   * Sets the text.
   * 
   * @param newText the new text
   */
  public void setText(String newText) {
    contents = newText;
  }

  /**
   * Sets the text color.
   * 
   * @param newColor the new text color
   */
  public void setTextColor(Color newColor) {
    textColor = newColor;
  }

  /**
   * Sets the text style.
   * 
   * @param newFontStyle the new text style
   */
  public void setTextStyle(boolean[] newFontStyle) {
    assert (newFontStyle.length == 3);
    System.arraycopy(newFontStyle, 0, this.textStyle, 0, 3);
  }

  /**
   * Gets the text.
   * 
   * @return the text
   */
  public String getText() {
    return contents;
  }

  /**
   * Gets the text color.
   * 
   * @return the text color
   */
  public Color getTextColor() {
    return textColor;
  }

  /**
   * Gets the text style.
   * 
   * @return the text style
   */
  public boolean[] getTextStyle() {
    boolean[] result = new boolean[3];
    System.arraycopy(this.textStyle, 0, result, 0, 3);
    return result;
  }

  /**
   * Sets the text size.
   * 
   * @param newSize the new text size
   */
  public void setTextSize(int newSize) {
    textSize = newSize;
  }

  /**
   * Gets the text size.
   * 
   * @return the text size
   */
  public int getTextSize() {
    return textSize;
  }

  /**
   * Sets the font name.
   * 
   * @param newName the fontname to set
   */
  public void setFontName(String newName) {
    fontname = newName;
  }

  /**
   * Gets the font name.
   * 
   * @return the fontname
   */
  public String getFontName() {
    return fontname;
  }

  /**
   * Gets the horizontal alignment.
   * 
   * @return the horizontalAlignment
   */
  public HorizontalAlignment getHorizontalAlignment() {
    return horizontalAlignment;
  }

  /**
   * Sets the horizontal alignment.
   * 
   * @param horizontalAlignment the horizontalAlignment to set
   */
  public void setHorizontalAlignment(HorizontalAlignment horizontalAlignment) {
    this.horizontalAlignment = horizontalAlignment;
  }

  /**
   * Gets the vertical alignment.
   * 
   * @return the verticalAlignment
   */
  public VerticalAlignment getVerticalAlignment() {
    return verticalAlignment;
  }

  /**
   * Sets the vertical alignment.
   * 
   * @param verticalAlignment the verticalAlignment to set
   */
  public void setVerticalAlignment(VerticalAlignment verticalAlignment) {
    this.verticalAlignment = verticalAlignment;
  }

  /**
   * Sets the tooltip for the text.
   * 
   * @param tooltipText new text of tooltip
   */
  public void setTooltipText(String tooltipText) {
    this.tooltipText = StringEscapeUtils.escapeXml(tooltipText);
  }

  /**
   * Gets the tooltip for the text.
   * 
   * @return the tooltipText
   */
  public String getTooltipText() {
    return tooltipText;
  }

  /**
   * @return vertical the vertical
   */
  public boolean isVertical() {
    return vertical;
  }

  public void setVertical(boolean vertical) {
    this.vertical = vertical;
  }
}
