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

import org.apache.commons.lang.StringUtils;


/**
 * Class representing a color from the visualization model.
 * 
 * @author Christian M. Schweda
 *
 * @version 5.0
 */
public class Color {

  public static final int   REDFROMBIT   = 16;
  public static final int   GREENFROMBIT = 8;
  public static final int   BASE         = 16;

  public static final Color BLACK        = new Color(0, 0, 0);
  public static final Color WHITE        = new Color(255, 255, 255);

  /**
   * Attribute containing the red value of this color.
   */
  private int               red;

  /**
   * Attribute containing the green value of this color.
   */
  private int               green;

  /**
   * Attribute containing the blue value of this color.
   */
  private int               blue;

  /**
   * Default constructor for a black color instance.
   */
  public Color() {
    //Creates a new instance of color black
  }

  /**
   * Default constructor for a color instance with the given red, green, and blue values.
   * 
   * @param red The red value of the color.
   * @param green The green value of the color.
   * @param blue The blue value of the color.
   */
  public Color(int red, int green, int blue) {
    this();
    this.red = red;
    this.green = green;
    this.blue = blue;
  }

  public Color(String serialized) {
    if (serialized == null) {
      return;
    }
    if (serialized.charAt(0) == '#') {
      if (serialized.length() == 3) {
        this.red = Integer.parseInt(serialized.substring(1, 2), BASE) * 17;
        this.green = Integer.parseInt(serialized.substring(2, 3), BASE) * 17;
        this.blue = Integer.parseInt(serialized.substring(3, 4), BASE) * 17;
      }
      else {
        this.red = Integer.parseInt(serialized.substring(1, 3), BASE);
        this.green = Integer.parseInt(serialized.substring(3, 5), BASE);
        this.blue = Integer.parseInt(serialized.substring(5, 7), BASE);
      }
    }
    else if (StringUtils.startsWith(serialized, "rgb(")) {
      String[] vals = serialized.substring(4, serialized.length() - 1).split(",");
      this.red = Integer.parseInt(vals[0].trim());
      this.green = Integer.parseInt(vals[1].trim());
      this.blue = Integer.parseInt(vals[2].trim());
    }
  }

  /**
   * Auxiliary method for ensuring that the string returned contains exactly
   * two digits. If the string does not have the right length, a preceding
   * &quot;0&quot; is added or only the first two chars are returned.
   * 
   * @param hexstring The string, of which the two digits should be ensured.
   * 
   * @return The two digit version of the string.
   */
  private static String ensure2Digits(String hexstring) {
    if (hexstring.length() == 1) {
      return "0" + hexstring;
    }
    else if (hexstring.length() > 2) {
      return hexstring.substring(0, 2);
    }
    else {
      return hexstring;
    }
  }

  /**
   * Method for reading the red value of the color.
   * 
   * @return The red value of the color.
   */
  public int getRed() {
    return this.red;
  }

  /**
   * Method for setting the red value of the color.
   * 
   * @param red The new red value of the color.
   */
  public void setRed(int red) {
    this.red = red;
  }

  /**
   * Method for reading the green value of the color.
   * 
   * @return The green value of the color.
   */
  public int getGreen() {
    return this.green;
  }

  /**
   * Method for setting the green value of the color.
   * 
   * @param green The new green value of the color.
   */
  public void setGreen(int green) {
    this.green = green;
  }

  /**
   * Method for reading the blue value of the color.
   * 
   * @return The blue value of the color.
   */
  public int getBlue() {
    return this.blue;
  }

  /**
   * Method for setting the blue value of the color.
   * 
   * @param blue The new blue value of the color.
   */
  public void setBlue(int blue) {
    this.blue = blue;
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Color) {
      return this.red == ((Color) o).red && this.green == ((Color) o).green && this.blue == ((Color) o).blue;
    }
    else {
      return false;
    }
  }

  @Override
  public int hashCode() {
    return (this.red << REDFROMBIT) | (this.green << GREENFROMBIT) | this.blue;
  }

  @Override
  public String toString() {
    StringBuffer result = new StringBuffer();
    result.append('#');
    result.append(ensure2Digits(Integer.toHexString(this.red)));
    result.append(ensure2Digits(Integer.toHexString(this.green)));
    result.append(ensure2Digits(Integer.toHexString(this.blue)));
    return result.toString();
  }
}