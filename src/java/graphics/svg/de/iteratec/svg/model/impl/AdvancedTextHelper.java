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
import java.util.Arrays;
import java.util.List;


public final class AdvancedTextHelper {

  public static final double POINT_TO_UNIT_CONSTANT               = 0.625;

  public static final double TEXT_BOX_WIDTH_TO_SHAPE_WIDTH_FACTOR = 0.9;

  private AdvancedTextHelper() {
    // private constructor, to avoid instantiation of a static methods-only class
  }

  public static List<String> buildTextBoxLines(String sourceString, double textCharSize, double pointToUnitConstant, double shapeWidth) {

    List<String> resultList = new ArrayList<String>();

    double textBoxWidth = shapeWidth * TEXT_BOX_WIDTH_TO_SHAPE_WIDTH_FACTOR;

    // Simulate textBox
    StringBuilder textBox = new StringBuilder();

    // Separate the text into lines that fit in the shape width
    // 1. Cover the case of having just one line
    if (getTextWidth(sourceString.length(), textCharSize, pointToUnitConstant) < textBoxWidth) {
      resultList.add(sourceString);
    }
    // 2. Cover the case where line separation is indeed needed
    else {
      // Retrieve the words, and convert to a modifiable List
      List<String> staticList = Arrays.asList(sourceString.split("\\s+"));
      List<String> wordsList = new ArrayList<String>(staticList);

      while (wordsList.size() != 0) {
        // Only the rest string is longer than one line
        if (getTextWidth(textBox.length(), textCharSize, pointToUnitConstant) > textBoxWidth) {
          textBox.insert(0, ' ');
          resultList.add(textBox.toString());

          textBox.setLength(0); // reset string builder
        }
        // The rest string with the next word coming is longer than a line
        if (getTextWidth(textBox.length() + wordsList.get(0).length(), textCharSize, pointToUnitConstant) > textBoxWidth) {
          textBox.append(' ').append(wordsList.remove(0));
          resultList.add(textBox.toString());

          textBox.setLength(0); // reset string builder
          if (wordsList.size() == 0) {
            break;
          }
        }
        // The current string is not longer than a line
        while (getTextWidth(textBox.length(), textCharSize, pointToUnitConstant) < textBoxWidth) {
          textBox.append(' ').append(wordsList.remove(0));
          if (wordsList.size() == 0) {
            break;
          }
        }

        // Split again and add all but the last word to the resultList
        // The last word is then left in the temp string and added either
        // to the next line or separately as a line iff the wordsList is empty.
        String[] t = textBox.toString().split("\\s+");
        StringBuilder sb = new StringBuilder(t.length);

        for (int i = 0; i < t.length - 1; i++) {
          sb.append(' ').append(t[i]);
        }
        if (!sb.toString().matches("\\s*")) {
          resultList.add(sb.toString());
        }
        textBox = new StringBuilder(t[t.length - 1]);
      }
    }

    if (!textBox.toString().matches("\\s*")) {
      if (getTextWidth(resultList.get(resultList.size() - 1).length() + textBox.length(), textCharSize, pointToUnitConstant) < textBoxWidth) {
        textBox.insert(0, resultList.remove(resultList.size() - 1));
        textBox.insert(0, ' ');
        resultList.add(textBox.toString());
      }
      else {
        resultList.add(textBox.toString());
      }
    }

    return resultList;
  }

  public static double getTextWidth(int textLength, double textSizePt, double pointToUnitConstant) {
    return textLength * (textSizePt) * pointToUnitConstant;
  }

}
