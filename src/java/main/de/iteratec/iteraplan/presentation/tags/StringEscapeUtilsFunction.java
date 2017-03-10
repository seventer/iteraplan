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
package de.iteratec.iteraplan.presentation.tags;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;

import org.apache.commons.lang.UnhandledException;
import org.apache.commons.lang.exception.NestableRuntimeException;
import org.apache.commons.lang.text.StrBuilder;


/**
 *Simplification and adjustment of the class {@link org.apache.commons.lang.StringEscapeUtils StringEscapeUtils}
 */
public final class StringEscapeUtilsFunction {

  private StringEscapeUtilsFunction() {
    //prevent instantiation
  }

  /**
   * <p>Escapes the characters in a <code>String</code> using JavaScript String rules.</p>
   * <p>Escapes any values it finds into their JavaScript String form.
   * Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
   * Doesn't escape characters with an ASCII value over 127
   *
   * <p>So a tab becomes the characters <code>'\\'</code> and
   * <code>'t'</code>.</p>
   *
   * <p>The only difference between Java strings and JavaScript strings
   * is that in JavaScript, a single quote must be escaped.</p>
   *
   * <p>Example:
   * <pre>
   * input string: He didn't say, "Stop!"
   * output string: He didn\'t say, \"Stop!\"
   * </pre>
   * </p>
   *
   * @param str  String to escape values in, may be null
   * @return String with escaped values, <code>null</code> if null string input
   */
  public static String escapeJavaScript(String str) {
    return escapeJavaStyleString(str, true, true);
  }

  private static String escapeJavaStyleString(String str, boolean escapeSingleQuotes, boolean escapeForwardSlash) {
    if (str == null) {
      return null;
    }
    try {
      StringWriter writer = new StringWriter(str.length() * 2);
      escapeJavaStyleString(writer, str, escapeSingleQuotes, escapeForwardSlash);
      return writer.toString();
    } catch (IOException ioe) {
      // this should never ever happen while writing to a StringWriter
      throw new UnhandledException(ioe);
    }
  }

  private static void escapeJavaStyleString(Writer out, String str, boolean escapeSingleQuote, boolean escapeForwardSlash) throws IOException {
    if (out == null) {
      throw new IllegalArgumentException("The Writer must not be null");
    }
    if (str == null) {
      return;
    }
    int sz;
    sz = str.length();
    for (int i = 0; i < sz; i++) {
      char ch = str.charAt(i);

      // handle unicode
      if (ch > 0x7f) {
        out.write(ch); //these characters will not cause any harm, must not be escaped
      }
      else if (ch < 32) {
        switch (ch) {
          case '\b':
            out.write('\\');
            out.write('b');
            break;
          case '\n':
            out.write('\\');
            out.write('n');
            break;
          case '\t':
            out.write('\\');
            out.write('t');
            break;
          case '\f':
            out.write('\\');
            out.write('f');
            break;
          case '\r':
            out.write('\\');
            out.write('r');
            break;
          default:
            //controll characters should be escaped
            if (ch > 0xf) {
              out.write("\\u00" + hex(ch));
            }
            else {
              out.write("\\u000" + hex(ch));
            }
            break;
        }
      }
      else {
        switch (ch) {
          case '\'':
            if (escapeSingleQuote) {
              out.write('\\');
            }
            out.write('\'');
            break;
          case '"':
            out.write('\\');
            out.write('"');
            break;
          case '\\':
            out.write('\\');
            out.write('\\');
            break;
          case '/':
            if (escapeForwardSlash) {
              out.write('\\');
            }
            out.write('/');
            break;
          default:
            out.write(ch);
            break;
        }
      }
    }
  }

  private static String hex(char ch) {
    return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
  }

  public static String unescapeJava(String str) {
    if (str == null) {
      return null;
    }
    try {
      StringWriter writer = new StringWriter(str.length());
      unescapeJava(writer, str);
      return writer.toString();
    } catch (IOException ioe) {
      throw new UnhandledException(ioe);
    }
  }

  public static void unescapeJava(Writer out, String str) throws IOException {
    if (out == null) {
      throw new IllegalArgumentException("The Writer must not be null");
    }
    if (str == null) {
      return;
    }
    int sz = str.length();
    StrBuilder unicode = new StrBuilder(4);
    boolean hadSlash = false;
    boolean inUnicode = false;
    for (int i = 0; i < sz; i++) {
      char ch = str.charAt(i);
      if (inUnicode) {
        // if in unicode, then we're reading unicode
        // values in somehow
        unicode.append(ch);
        if (unicode.length() == 4) {
          // unicode now contains the four hex digits
          // which represents our unicode character
          try {
            int value = Integer.parseInt(unicode.toString(), 16);
            out.write((char) value);
            unicode.setLength(0);
            inUnicode = false;
            hadSlash = false;
          } catch (NumberFormatException nfe) {
            throw new NestableRuntimeException("Unable to parse unicode value: " + unicode, nfe);
          }
        }
        continue;
      }
      if (hadSlash) {
        hadSlash = false;
        switch (ch) {
          case '\\':
            out.write('\\');
            break;
          case '\'':
            out.write('\'');
            break;
          case '\"':
            out.write('"');
            break;
          case 'r':
            out.write('\r');
            break;
          case 'f':
            out.write('\f');
            break;
          case 't':
            out.write('\t');
            break;
          case 'n':
            out.write('\n');
            break;
          case 'b':
            out.write('\b');
            break;
          case 'u': {
            inUnicode = true;
            break;
          }
          default:
            out.write(ch);
            break;
        }
        continue;
      }
      else if (ch == '\\') {
        hadSlash = true;
        continue;
      }
      out.write(ch);
    }
    if (hadSlash) {
      out.write('\\');
    }
  }

  public static String unescapeJavaScript(String str) {
    return unescapeJava(str);
  }

  public static void unescapeJavaScript(Writer out, String str) throws IOException {
    unescapeJava(out, str);
  }

}
