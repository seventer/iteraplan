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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.iteratec.svg.model.SvgExportException;


public class SvgCssStyling extends SvgBaseStyling {

  private String cssClassName;

  public SvgCssStyling() {
    super();
  }

  public SvgCssStyling(String cssClassName) throws SvgExportException {
    super();
    if (cssClassName == null || cssClassName.matches("\\s*")) {
      throw new SvgExportException("The name of a CSS class has to be specified.");
    }
    this.cssClassName = cssClassName;
  }

  public String getCssClassName() {
    return cssClassName;
  }

  public void setCssClassName(String className) {
    this.cssClassName = className;
  }

  /**
   * Generates the CSS class string as it is to be inserted into the CDATA section of the DOM.
   * 
   * @return The CSS class String
   */
  public String generateCSSClassString() {
    StringBuffer result = new StringBuffer();
    result.append('.');
    result.append(cssClassName);
    result.append(" {");
    result.append(generateStylingString());
    result.append('}');
    return result.toString();
  }

  /**
   * Creates a new CSSStyling from a string.
   * 
   * @param inputString
   *          The string to be decoded.
   * @return A new fully initialized CSSStyling generated from the input string.
   * @throws SvgExportException
   *           if the input string is not decodable.
   */
  public static SvgCssStyling createCssClassFromString(String inputString) throws SvgExportException {

    SvgCssStyling result = new SvgCssStyling();

    String[] particles = inputString.split("\\Q{\\E");

    if (particles.length < 2) {
      throw new SvgExportException("CSS Stylesheet template loader encounterd a problem.");
    }

    String newCssClassName = particles[0].split("\\Q.\\E")[1];
    result.setCssClassName(newCssClassName.trim());
    result.addAttributes(particles[1].split("\\Q}\\E")[0]);

    return result;
  }

  public SvgCssStyling deepCopy() {
    SvgCssStyling copy = new SvgCssStyling();
    copy.setCssClassName(cssClassName);
    for (String attr : getStylingAttributes().keySet()) {
      copy.setAttribute(attr, getStylingAttributes().get(attr));
    }
    return copy;
  }

  /**
   * This method decodes the list of CSS classes defined in the CDATA section of the SVG template.
   * 
   * @param sourceString
   *          The String as it is read in the CDATA section.
   * @return A list of strings representing the single classes.
   */
  public static List<String> decodeCSSClassesFromString(String sourceString) {

    List<String> resultList = new ArrayList<String>();
    int indexCount = 0;
    String classEntry = "";
    // TODO comment the meaning of this regex!
    Pattern p = Pattern.compile("\\s*\\Q.\\E\\S+\\s*\\Q{\\E.*\\Q}\\E");
    Matcher m;
    String styleDefinition = sourceString;

    while (indexCount < styleDefinition.length()) {

      classEntry = classEntry + styleDefinition.charAt(indexCount);

      m = p.matcher(classEntry);

      if (m.matches()) {
        resultList.add(classEntry);
        indexCount++;
        styleDefinition = styleDefinition.substring(indexCount);
        indexCount = 0;
        classEntry = "";
      }
      else {
        indexCount++;
      }

    }
    return resultList;
  }

  /**
   * This method rebuilds the list of CSS classes to be inserted into the "classes" attribute of a
   * certain SVG node.
   * 
   * @param classes
   *          A list of class names.
   * @return The concatenated list as is to be inserted into the DOM.
   */
  public static String buildCSSClassesString(List<String> classes) {
    StringBuffer result = new StringBuffer();
    for (String str : classes) {
      result.append(str);
      result.append(' ');
    }
    return result.toString();
  }

}
