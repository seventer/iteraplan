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
package de.iteratec.iteraplan.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A utility class for string escaping.
 */
@SuppressWarnings("boxing")
public final class StringUtil {

  private static final Map<Character, String> VISIO_XML_ENCODING_MAP = new HashMap<Character, String>();

  private static final List<Character>        LEGAL_XMLCHARS_LT0X20  = new ArrayList<Character>();
  static {
    VISIO_XML_ENCODING_MAP.put(Character.valueOf('\u0002'), "");
    LEGAL_XMLCHARS_LT0X20.addAll(Arrays.asList(new Character[] { '\t', '\n', '\r' }));
  }

  private StringUtil() {
    // only static method, no instance needed
  }

  /**
   * Removes new lines from a given string.
   * 
   * @param text
   *          The input string
   * @return The input string where all new lines have been replaced by a space (" "). If the string
   *         is null, an empty string is returned.
   */
  public static String removeNewLines(String text) {
    if (text == null) {
      return "";
    }
    // return text.replaceAll("(\r)*(\n)+(\r)*", " ");
    return text.replaceAll("(\r)(\n)|(\n)|(\r)", " ");
  }

  /**
   * Remove illegal characters like 0x11 (vertical tab) and others that are not allowed in xml from
   * the input string.
   * <p>
   * Characters removed are chars that are below #x20, except #x9, #xA, #xD. Also see
   * http://www.w3.org/TR/REC-xml/#charsets
   * </p>
   * 
   * @param in
   *          String to parse
   * @return String without illegal xml characters
   */
  public static String removeIllegalXMLChars(String in) {

    if (in == null) {
      return null;
    }

    StringBuilder out = new StringBuilder(in.length());
    for (int i = 0; i < in.length(); i++) {
      char c = in.charAt(i);
      if (c < 0x20) { // check only chars < 0x20
        Character val = Character.valueOf(in.charAt(i));
        if (LEGAL_XMLCHARS_LT0X20.contains(val)) {
          out.append(c);
        }
//        else { // NOPMD
//          // do nothing, character is removed
//        }
      }
      else {
        out.append(c);
      }
    }

    return out.toString();
  }

  /**
   * Encode string for usage in Visio XML document. Strips &#2; from a string. Checks only non ascii
   * characters.
   * 
   * @param in
   *          String to parse
   * @return String without '&#2;'
   */
  public static String encodeVisioXml(String in) {

    // ExcelImporter can cause null values for some attributes in the DB which must be checked
    // especially
    if (in == null) {
      return "";
    }

    StringBuilder out = new StringBuilder(in.length());
    for (int i = 0; i < in.length(); i++) {
      char c = in.charAt(i);
      if (c < 0x1F || c >= 0x80) { // check only non acsii
        Character val = Character.valueOf(in.charAt(i));
        if (VISIO_XML_ENCODING_MAP.containsKey(val)) {
          out.append(VISIO_XML_ENCODING_MAP.get(val));
        }
        else {
          out.append(c);
        }
      }
      else {
        out.append(c);
      }
    }

    return out.toString();
  }

  /**
   * Note: Unix and Windows use the same path separator ('/') for this.
   * 
   * @param str
   *          an file:-uri contained in a string like:
   *          sometext...file:///C:/Programme/apache-tomcat-6.0.26/bin/iteraplanSerialization.xmi
   *          moretext...
   * @return the anonymized information, without the full path
   */
  public static String removePathFromFileURIString(String str) {
    if (str == null) {
      return null;
    }
    String result;
    String regexFile = "file:.*/(\\S*)";
    Pattern patternFile = Pattern.compile(regexFile, Pattern.CASE_INSENSITIVE);
    Matcher matcherFile = patternFile.matcher(str);

    result = matcherFile.replaceAll("$1");
    return result;
  }
  
  /**
   * Replaces the substring between start and end position of this string with the given replacement. 
   * 
   * @param source
   * @param posStart
   * @param posEnd
   * @param replace
   * @return The resulting String
   */
  public static String replaceStringAt(String source, int posStart, int posEnd, String replace) {
    int start = posStart;
    int end = posEnd;

    if (source == null) {
      return null;
    }
    
    if (start < 0 || end < 0 || start > source.length() - 1 || end > source.length() - 1) {
      return source;
    }

    if (start > end) {
      int x = start;
      start = end;
      end = x;
    }

    return source.substring(0, start) + replace + source.substring(end + 1);
  }
}