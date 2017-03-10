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
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.rt.fmt.MessageTag;
import org.apache.tiles.jsp.taglib.InsertTemplateTag;

import de.iteratec.iteraplan.common.MessageAccess;
import de.iteratec.iteraplan.common.UserContext;


public final class TagUtils {

  /**
   * Maps lowercase JSP scope names to their PageContext integer constant values.
   */
  private static final Map<String, Integer> SCOPES = new HashMap<String, Integer>();

  private static final Pattern  FILE_PATTERN_REGEX                 = Pattern.compile("<a href=\\\"(file:[^\\\"]*)\\\">\\s*([^<]+)</a>",
      Pattern.CASE_INSENSITIVE);
  

  /**
   * A I18n key to a message, warning the user that file links cannot be opened directly, but must
   * be copied to the file explorer manually. This will appear as a JavaScript popup.
   */
  private static final String   WIKI_LINKS_WARNING_KEY             = "global.wikiLinksWarningKey";

  /**
   * Initialize the scope names map and the encode variable with the Java 1.4 method if available.
   */
  static {
    SCOPES.put("page", Integer.valueOf(PageContext.PAGE_SCOPE));
    SCOPES.put("request", Integer.valueOf(PageContext.REQUEST_SCOPE));
    SCOPES.put("session", Integer.valueOf(PageContext.SESSION_SCOPE));
    SCOPES.put("application", Integer.valueOf(PageContext.APPLICATION_SCOPE));
  }

  /**
   * No instances, the class server as module with static methods.
   */
  private TagUtils() {
    super();
  }

  public static void insertImage(PageContext ctx, String image, int width, int height) throws IOException {
    JspWriter w = ctx.getOut();
    String alt = "";

    w.print("<img src=\"");
    w.print(ctx.getServletContext().getContextPath());
    w.print(image);
    w.println("\" style=\"float: right;\" width=\"" + width + "\" height=\"" + height + "\" alt=\"" + alt + "\" />");
  }

  public static void insertTile(PageContext ctx, String page) throws JspException, IOException {

    InsertTemplateTag insertTag = new InsertTemplateTag();
    insertTag.setJspContext(ctx);
    insertTag.setTemplate(page);
    insertTag.doTag();
  }

  /**
   * This method will print the literal (if given) or else the value of the key
   * 
   * @param ctx
   * @param literal
   * @param key
   * @throws JspException
   * @throws IOException
   */
  public static void insertMessage(PageContext ctx, String literal, String key) throws JspException, IOException {
    JspWriter w = ctx.getOut();

    if (literal != null && literal.length() != 0) {
      w.println(literal);
    }

    MessageTag messageTag = new MessageTag();
    messageTag.doStartTag();
    messageTag.setPageContext(ctx);
    messageTag.setKey(key);
    messageTag.doEndTag();
  }
  
  public static String breaksAndSpaces(String source) {
    String output = TagUtils.convertLineBreaks(source);
    output = TagUtils.convertSpaces(output);
    return output;
  }
  
  /**
   * @param original
   *          The string containing line breaks.
   * @return The original string with line breaks replaced by HTML line breaks.
   */
  public static String convertLineBreaks(String original) {
    return original.replaceAll("\r\n", "<br/>");
  }
  
  public static String convertSpaces(String original) {
    return original.replaceAll("  ", " &nbsp;");
  }
  
  /**
   * Every File-Link will be decorated with a javascript alert message.
   * 
   * @param original
   * @return string that contains decorated file links
   */
  public static String decorateFileLinks(String original) {
    Matcher matcherFile = FILE_PATTERN_REGEX.matcher(original);
    String message = MessageAccess.getString(WIKI_LINKS_WARNING_KEY, UserContext.getCurrentLocale());

    return matcherFile.replaceAll("<a onclick=\"javascript:return alert(&#39;" + message + "&#39;);\" href=\"$1\">$2</a>");
  }

  /**
   * Convenience method for evaluating an EL expression to a String (assuming that you expect to
   * receive a String)
   * 
   * @param attrName
   * @param attrValue
   * @param tagObject
   * @param pageContext
   * @return the String that the given expression evaluates to
   * @throws JspException
   *           if the evaluation engine encounters an error
   */
  public static String evalString(String attrName, String attrValue, Tag tagObject, PageContext pageContext) throws JspException {
    if (attrValue != null) {
      return (String) ExpressionEvaluatorManager.evaluate(attrName, attrValue, String.class, tagObject, pageContext);
    }
    else {
      return null;
    }

  }

  /**
   * Write the specified text as the response to the writer associated with this page.
   * 
   * @param pageContext
   *          The PageContext object for this page
   * @param text
   *          The text to be written
   * @throws JspException
   *           if an input/output error occurs
   */
  public static void write(PageContext pageContext, String text) throws JspException {
    JspWriter writer = pageContext.getOut();

    try {
      writer.print(text);
    } catch (IOException e) {
      throw new JspException("An I/O error ocurred: ", e);
    }
  }

  /**
   * Converts the scope name into its corresponding PageContext constant value.
   * 
   * @param scopeName
   *          Can be "page", "request", "session", or "application" in any case.
   * @return The constant representing the scope (ie. PageContext.REQUEST_SCOPE).
   * @throws JspException
   *           if the scopeName is not a valid name.
   */
  public static int getScope(String scopeName) throws JspException {

    Integer scope = SCOPES.get(scopeName.toLowerCase());
    if (scope == null) {
      throw new JspException("An invalid scope identifier was encountered: " + scopeName);
    }

    return scope.intValue();
  }

  /**
   * Locate and return the specified bean, from an optionally specified scope, in the specified page
   * context. If no such bean is found, return <code>null</code> instead.
   * 
   * @param pageContext
   *          Page context to be searched
   * @param name
   *          Name of the bean to be retrieved
   * @param scopeName
   *          Scope to be searched (page, request, session, application) or <code>null</code> to use
   *          <code>findAttribute()</code> instead
   * @return JavaBean in the specified page context
   * @throws JspException
   *           if an invalid scope name is requested
   */
  public static Object lookup(PageContext pageContext, String name, String scopeName) throws JspException {

    if (scopeName == null) {
      return pageContext.findAttribute(name);
    }

    return pageContext.getAttribute(name, getScope(scopeName));
  }

  /**
   * Locate and return the specified property of the specified bean, from an optionally specified
   * scope, in the specified page context.
   * 
   * @param pageContext
   *          Page context to be searched
   * @param name
   *          Name of the bean to be retrieved
   * @param property
   *          Name of the property to be retrieved, or <code>null</code> to retrieve the bean itself
   * @param scope
   *          Scope to be searched (page, request, session, application) or <code>null</code> to use
   *          <code>findAttribute()</code> instead
   * @return property of specified JavaBean
   * @throws JspException
   *           if an invalid scope name is requested
   * @throws JspException
   *           if the specified bean is not found
   * @throws JspException
   *           if accessing this property causes an IllegalAccessException,
   *           IllegalArgumentException, InvocationTargetException, or NoSuchMethodException
   */
  public static Object lookup(PageContext pageContext, String name, String property, String scope) throws JspException {

    // Look up the requested bean, and return if requested
    Object bean = lookup(pageContext, name, scope);

    if (bean == null) {
      if (scope == null) {
        throw new JspException("Could not find bean '" + name + "' in any scope.");
      }
      else {
        throw new JspException("Could not find bean '" + name + "' in scope '" + scope + "'.");
      }
    }

    if (property == null) {
      return bean;
    }

    // Locate and return the specified property
    try {
      return PropertyUtils.getProperty(bean, property);
    } catch (IllegalAccessException e) {
      throw new JspException(String.format("Invalid access looking up property '%s' of bean %s", property, name), e);
    } catch (IllegalArgumentException e) {
      throw new JspException(String.format("Invalid argument looking up property '%s' of bean %s", property, name), e);
    } catch (InvocationTargetException e) {
      throw new JspException(String.format("Exception thrown by getter for property '%s' of bean %s", property, name), e);
    } catch (NoSuchMethodException e) {

      throw new JspException(String.format("No getter method for property '%s' of bean %s", property, name), e);
    }

  }

  /**
   * Filter the specified string for characters that are sensitive to HTML interpreters, returning
   * the string with these characters replaced by the corresponding character entities.
   * 
   * @param value
   *          The string to be filtered and returned
   */
  public static String filter(String value) {

    if (value == null) {
      return (null);
    }

    char content[] = new char[value.length()];
    value.getChars(0, value.length(), content, 0);
    StringBuffer result = new StringBuffer(content.length + 50);
    for (int i = 0; i < content.length; i++) {
      switch (content[i]) {
        case '<':
          result.append("&lt;");
          break;
        case '>':
          result.append("&gt;");
          break;
        case '&':
          result.append("&amp;");
          break;
        case '"':
          result.append("&quot;");
          break;
        case '\'':
          result.append("&#39;");
          break;
        default:
          result.append(content[i]);
      }
    }
    return (result.toString());
  }
}
