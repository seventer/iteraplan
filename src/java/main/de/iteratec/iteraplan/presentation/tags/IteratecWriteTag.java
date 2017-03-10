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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.jsp.JspException;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

import de.iteratec.iteraplan.businesslogic.service.wiki.IteraplanSyntaxParserService;
import de.iteratec.iteraplan.businesslogic.service.wiki.WikiParserService;
import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;


/**
 * This tag provides basically the same functionality as the JSTL c:out tag, but performs an
 * additional evaluation round. Values in the name and property attributes are treated as EL
 * expressions, even if standard EL expansion was performed already. Expression in the property
 * attribute may use a few more constructs than pure EL, as they are evaluated by Apache Commons
 * BeanUtils. Most prominently, it is possible to invoke methods on a bean and pass parameters.
 * Furthermore, it can convert line breaks to HTML &lt;br/&gt; elements, spaces to &amp;nbsp;
 * elements and links in wiki notation to HTML links.
 */
public class IteratecWriteTag extends RequestContextAwareTag {

  /** Serialization version. */
  private static final long     serialVersionUID                   = 2454566522924232298L;

  private static final Logger   LOGGER                             = Logger.getIteraplanLogger(IteratecWriteTag.class);

  /**
   * The key to search default format string for java.util.Date in resources.
   */
  protected static final String DATE_FORMAT_KEY                    = "format.date";

  /**
   * The key to search default format string for int (byte, short, etc.) in resources.
   */
  protected static final String INT_FORMAT_KEY                     = "format.int";

  /**
   * The key to search default format string for float (double, BigDecimal) in resources.
   */
  protected static final String FLOAT_FORMAT_KEY                   = "format.float";

  /**
   * Number of characters after which the description will be truncated with "...".
   */
  private static final int      DEFAULT_DESCRIPTION_TRUNCATE_AFTER = 300;

  /**
   * If set to true, line breaks will be converted to &lt;br/&gt; and spaces will be converted to
   * &amp;nbsp; elements.
   */
  private boolean               breaksAndSpaces                    = false;

  /**
   * If set to true, links in wiki notation will be converted to HTML links. The notation is
   * [&lt;link&gt; &lt;title&gt;]. For example, [http://www.example.com/ Example Webpage] will
   * render as a link with title "Example Webpage".
   */
  private boolean               wikiText                           = false;

  /**
   * If set to true, wiki notation and special iteraplan macros will be converted to HTML.
   */
  private boolean               dashboardText                      = false;

  /**
   * If set to true, links in wiki notation will be converted to plaintext.
   */
  private boolean               plainText                          = false;

  /**
   * If set to true, the returned text will be truncated after 300 characters
   */
  private boolean               truncateText                       = false;

  /**
   * If set to true, links in wiki notation will be converted to HTML links using HTML escaping. The
   * notation is [&lt;link&gt; &lt;title&gt;]. For example, [http://www.example.com/ Example
   * Webpage] will render as a link with title "Example Webpage".
   */
  private boolean               links                              = false;

  /**
   * Name of the bean that contains the data we will be rendering.
   */
  private String                name                               = null;

  /**
   * Name of the property to be accessed on the specified bean.
   */
  private String                property                           = null;

  /**
   * The scope to be searched to retrieve the specified bean.
   */
  private String                scope                              = null;

  /**
   * Filter the rendered output for characters that are sensitive in HTML? Defaults to true to prevent code injection.
   */
  private boolean               escapeXml                          = true;

  /**
   * Should we ignore missing beans and simply output nothing?
   */
  private boolean               ignore                             = false;

  private String                userAgent                          = null;

  public String getUserAgent() {
    return userAgent;
  }

  public void setUserAgent(String userAgent) {
    this.userAgent = userAgent;
  }

  public boolean isDashboardText() {
    return dashboardText;
  }

  public void setDashboardText(boolean dashboardText) {
    this.dashboardText = dashboardText;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
    try {
      this.name = TagUtils.evalString("name", name, this, pageContext);
    } catch (JspException e) {
      LOGGER.error(e);
    }
  }

  public String getProperty() {
    return this.property;
  }

  public void setProperty(String property) {
    this.property = property;
    try {
      this.property = TagUtils.evalString("property", property, this, pageContext);
    } catch (JspException e) {
      LOGGER.error(e);
    }
  }

  public String getScope() {
    return this.scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public boolean isEscapeXml() {
    return this.escapeXml;
  }

  public void setEscapeXml(boolean shouldEscape) {
    this.escapeXml = shouldEscape;
  }

  public boolean isIgnore() {
    return this.ignore;
  }

  public void setIgnore(boolean ignore) {
    this.ignore = ignore;
  }

  /**
   * @return Returns the breaksAndSpaces.
   */
  public boolean isBreaksAndSpaces() {
    return breaksAndSpaces;
  }

  /**
   * @param breaksAndSpaces
   *          The breaksAndSpaces to set.
   */
  public void setBreaksAndSpaces(boolean breaksAndSpaces) {
    this.breaksAndSpaces = breaksAndSpaces;
  }

  /**
   * @return the wikiText
   */
  public boolean isWikiText() {
    return wikiText;
  }

  /**
   * @param wikiText
   *          the wikiText to set
   */
  public void setWikiText(boolean wikiText) {
    this.wikiText = wikiText;
  }

  /**
   * @return the plainText
   */
  public boolean isPlainText() {
    return plainText;
  }

  /**
   * @param plainText
   *          the plainText to set
   */
  public void setPlainText(boolean plainText) {
    this.plainText = plainText;
  }

  /**
   * @return truncateText
   */
  public boolean isTruncateText() {
    return truncateText;
  }

  /**
   * @param truncateText
   *          if true, the output will be truncated
   */
  public void setTruncateText(boolean truncateText) {
    this.truncateText = truncateText;
  }

  /**
   * @return Returns the wikiLinks.
   */
  public boolean isLinks() {
    return this.links;
  }

  /**
   * @param links
   *          The links to set.
   */
  public void setLinks(boolean links) {
    this.links = links;
  }

  /**
   * Process the start tag.
   * 
   * @exception JspException
   *              if a JSP exception has occurred
   */
  @Override
  public int doStartTagInternal() throws JspException {

    // Look up the requested bean (if necessary)
    if (ignore && TagUtils.lookup(pageContext, name, scope) == null) {
      return SKIP_BODY; // Nothing to output
    }

    // Look up the requested property value
    Object value = TagUtils.lookup(pageContext, name, property, scope);
    if (value == null) {
      return SKIP_BODY; // Nothing to output
    }

    // Convert value to the String with some formatting
    String output = formatValue(value);

    if (this.dashboardText) {
      IteraplanSyntaxParserService syntaxParserService = getRequestContext().getWebApplicationContext().getBean(IteraplanSyntaxParserService.class);
      output = syntaxParserService.convertIteraplanSyntax(output, userAgent);
    }
    if (this.wikiText) {
      WikiParserService wikiParserService = getRequestContext().getWebApplicationContext().getBean(WikiParserService.class);
      output = wikiParserService.convertWikiText(output);
    }
    if (this.plainText) {
      WikiParserService wikiParserService = getRequestContext().getWebApplicationContext().getBean(WikiParserService.class);
      output = wikiParserService.convertWikiTextToPlainText(output);
    }
    if (this.escapeXml) {
      output = TagUtils.filter(output);
    }
    if (this.breaksAndSpaces) {
      output = TagUtils.breaksAndSpaces(output);
    }
    if (this.truncateText) {
      output = StringUtils.abbreviate(output, DEFAULT_DESCRIPTION_TRUNCATE_AFTER);
    }
    TagUtils.write(pageContext, output);

    // Continue processing this page
    return SKIP_BODY;

  }

  /**
   * Format value according to specified format string (as tag attribute or as string from message
   * resources) or to current user locale.
   * 
   * @param valueToFormat
   *          value to process and convert to String
   * @exception JspException
   *              if a JSP exception has occurred
   */
  protected String formatValue(Object valueToFormat) throws JspException {
    Format format = null;
    Object value = valueToFormat;
    Locale locale = UserContext.getCurrentLocale();
    boolean formatStrFromResources = false;
    String formatString = null;

    // Return String object as is.
    if (value instanceof String) {
      return (String) value;
    }
    else {

      // Prepare format object for numeric values.
      if (value instanceof Number) {

        if ((value instanceof Byte) || (value instanceof Short) || (value instanceof Integer) || (value instanceof Long)
            || (value instanceof BigInteger)) {
          formatString = MessageAccess.getStringOrNull(INT_FORMAT_KEY, locale);
        }
        else if ((value instanceof Float) || (value instanceof Double) || (value instanceof BigDecimal)) {
          formatString = MessageAccess.getStringOrNull(FLOAT_FORMAT_KEY, locale);
        }

        if (formatString != null) {
          formatStrFromResources = true;

          try {
            format = NumberFormat.getNumberInstance(locale);
            if (formatStrFromResources) {
              ((DecimalFormat) format).applyLocalizedPattern(formatString);
            }
            else {
              ((DecimalFormat) format).applyPattern(formatString);
            }
          } catch (IllegalArgumentException _e) {
            JspException e = new JspException("Wrong format string: " + formatString, _e);
            throw e;
          }
        }

      }
      else if (value instanceof Date) {

        formatString = MessageAccess.getStringOrNull(DATE_FORMAT_KEY, locale);

        if (formatString != null) {
          formatStrFromResources = true;

          if (formatStrFromResources) {
            format = new SimpleDateFormat(formatString, locale);
          }
          else {
            format = new SimpleDateFormat(formatString); // NOPMD this is the last fallback, so
                                                         // locale-less is intended
          }
        }

      }
    }

    if (format != null) {
      return format.format(value);
    }
    else {
      return value.toString();
    }
  }

  /**
   * Release all allocated resources.
   */
  @Override
  public void release() {

    super.release();
    breaksAndSpaces = false;
    links = false;
    wikiText = false;
    escapeXml = true;
    ignore = false;
    name = null;
    property = null;
    scope = null;
  }
}
