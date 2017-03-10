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
package de.iteratec.svg.styling;

import java.util.HashMap;
import java.util.Map;


public class SvgBaseStyling {

  /**
   * Standard styling attribute names. Other attributes are also allowed.
   */
  public static final String  FILL_COLOR          = "fill";
  public static final String  FILL_OPACITY        = "fill-opacity";
  public static final String  STROKE_OPACITY      = "stroke-opacity";
  public static final String  STROKE_COLOR        = "stroke";
  public static final String  STROKE_WITDH        = "stroke-width";
  public static final String  STROKE_LINE_PATTERN = "stroke-dasharray";
  public static final String  FONT_SIZE           = "font-size";
  public static final String  FONT_ALIGN          = "text-anchor";
  public static final String  FONT_FAMILY         = "font-family";
  public static final String  FONT_WEIGHT         = "font-weight";

  private Map<String, String> stylingAttributes;

  public SvgBaseStyling() {
    stylingAttributes = new HashMap<String, String>();
  }

  public String getStringAttributeValue(String attributeName) {
    return stylingAttributes.get(attributeName);
  }

  public double getDoubleAttributeValue(String attributeName) {
    return Double.parseDouble(stylingAttributes.get(attributeName));
  }

  public void setAttribute(String attributeName, String attributeValue) {
    if (attributeName != null && !attributeName.matches("//s*") && attributeValue != null && !attributeValue.matches("//s*")) {
      stylingAttributes.put(attributeName, attributeValue);
    }
  }

  public String removeAttribute(String attributeName) {
    return stylingAttributes.remove(attributeName);
  }

  /**
   * Generates the styling string as it is to be built into the DOM
   * 
   * @return The styles String
   */
  public String generateStylingString() {
    StringBuffer resultString = new StringBuffer();
    for (String key : stylingAttributes.keySet()) {
      resultString.append(key);
      resultString.append(':');

      if (key.equals(SvgBaseStyling.FILL_COLOR) || key.equals(SvgBaseStyling.STROKE_COLOR)) {
        // The case of a color -> insert a # if the given value is not a reference to a pattern
        if (!stylingAttributes.get(key).matches("#.*") && !stylingAttributes.get(key).matches("url.*")) {
          resultString.append('#');
          resultString.append(stylingAttributes.get(key));
        }
        else {
          resultString.append(stylingAttributes.get(key));
        }
      }
      else if (key.equals(SvgBaseStyling.FONT_SIZE)) {
        // Text sizes get a "pt" ending
        resultString.append(stylingAttributes.get(key));
        resultString.append("pt");
      }
      else {
        resultString.append(stylingAttributes.get(key));
      }
      resultString.append(';');
    }
    return resultString.toString();
  }

  /**
   * Decodes a standardized string of attributes and adds them to those of the current style.
   * Unknown attributes are being ignored at this point.
   * 
   * @param attributeString
   *          An attribute string as defined in the SVG(style)/CSS (class definition).
   */
  public void addAttributes(String attributeString) {

    String[] properties = attributeString.split(";", 20);

    for (String property : properties) {

      String[] prop = property.split(":", 2);

      // Read the fill property
      if (prop[0].equalsIgnoreCase(FILL_COLOR)) {
        stylingAttributes.put(FILL_COLOR, decodeColor(prop[1]));
      }
      // Read the stroke property
      if (prop[0].equalsIgnoreCase(STROKE_COLOR)) {
        stylingAttributes.put(STROKE_COLOR, decodeColor(prop[1]));
      }
      // Read the line pattern
      if (prop[0].equalsIgnoreCase(STROKE_LINE_PATTERN)) {
        stylingAttributes.put(STROKE_LINE_PATTERN, prop[1]);
      }
      // Read the line weight
      if (prop[0].equalsIgnoreCase(STROKE_WITDH)) {
        stylingAttributes.put(STROKE_WITDH, decodeSizeAttribute(prop[1]));
      }
      // Read the font size property
      if (prop[0].equalsIgnoreCase(FONT_SIZE)) {
        stylingAttributes.put(FONT_SIZE, decodeSizeAttribute(prop[1]));
      }
      // Read text alignment property
      if (prop[0].equalsIgnoreCase(FONT_ALIGN)) {
        stylingAttributes.put(FONT_ALIGN, prop[1]);
      }
      // Read the font family
      if (prop[0].equalsIgnoreCase(FONT_FAMILY)) {
        stylingAttributes.put(FONT_FAMILY, prop[1]);
      }
      // Read the font weight
      if (prop[0].equalsIgnoreCase(FONT_WEIGHT)) {
        stylingAttributes.put(FONT_WEIGHT, prop[1]);
      }
      // Read the fill opacity
      if (prop[0].equalsIgnoreCase(FILL_OPACITY)) {
        stylingAttributes.put(FILL_OPACITY, prop[1]);
      }
      // Read the stroke opacity
      if (prop[0].equalsIgnoreCase(STROKE_OPACITY)) {
        stylingAttributes.put(STROKE_OPACITY, prop[1]);
      }
    }
  }

  /**
   * Creates a deep copy of this styling.
   * 
   * @return The created styling.
   */
  public SvgBaseStyling deepCopy() {
    SvgBaseStyling copy = new SvgBaseStyling();
    for (String attr : stylingAttributes.keySet()) {
      copy.setAttribute(attr, stylingAttributes.get(attr));
    }
    return copy;
  }

  private String decodeColor(String inputString) {
    String[] res = inputString.split("#");
    return res[1];
  }

  public static String decodeSizeAttribute(String inputString) {
    String[] res = inputString.split("pt|px");
    return res[0];
  }

  protected Map<String, String> getStylingAttributes() {
    return stylingAttributes;
  }

}
