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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import de.iteratec.iteraplan.common.Logger;


/**
 * Provides basically a functionality similar to the JSTL c:set tag, but performs an additional
 * evaluation round. Values in the name and property attributes are treated as EL expressions, even
 * if standard EL expansion was performed already. Expressions in the property attribute may use a
 * few more constructs than pure EL, as they are evaluated by Apache Commons BeanUtils. Most
 * prominently, it is possible to invoke methods on a bean and pass parameters.
 * <p>
 * This tag borrows concepts (and code) from the Struts bean:define tag, but is slightly more
 * permissive: If the name (and optionally property) attribute is set and points to a location that
 * has the value null, no bean is set. The original DefineTag would throw an exception at this
 * point.
 * </p>
 */
public class IteratecDefineTag extends BodyTagSupport {

  private static final Logger LOGGER   = Logger.getIteraplanLogger(IteratecDefineTag.class);

  /**
   * The body content of this tag (if any).
   */
  private String              body     = null;
  /**
   * The name of the bean owning the property to be exposed.
   */
  private String              name     = null;
  /**
   * The name of the property to be retrieved.
   */
  private String              property = null;
  /**
   * The scope within which to search for the specified bean.
   */
  private String              scope    = null;
  /**
   * The scope within which the newly defined bean will be created.
   */
  private String              toScope  = null;
  /**
   * The (String) value to which the defined bean will be set.
   */
  private String              value    = null;

  public void setName(String name) {
    this.name = name;
    try {
      this.name = TagUtils.evalString("name", name, this, pageContext);
    } catch (JspException e) {
      LOGGER.error(e);
    }
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

  public String getToScope() {
    return (this.toScope);
  }

  public void setToScope(String toScope) {
    this.toScope = toScope;
  }

  public String getValue() {
    return this.value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  /**
   * Check if we need to evaluate the body of the tag
   * 
   * @exception JspException
   *              if a JSP exception has occurred
   */
  @Override
  public int doStartTag() throws JspException {
    return (EVAL_BODY_BUFFERED);
  }

  /**
   * Retrieve the required property and expose it as a scripting variable.
   *
   * @exception JspException if a JSP exception has occurred
   */
  @Override
  public int doEndTag() throws JspException {

    // Enforce restriction on ways to declare the new value
    int n = 0;
    if (this.body != null) {
      n++;
    }
    if (this.name != null) {
      n++;
    }
    if (this.value != null) {
      n++;
    }
    if (n != 1) {
      throw new JspException("Define tag can contain only one of name attribute, value attribute, or body content");
    }

    // Retrieve the required property value
    Object valueToSet = this.value;
    if ((valueToSet == null) && (name != null)) {
      valueToSet = TagUtils.lookup(pageContext, name, property, scope);
    }
    if ((valueToSet == null) && (body != null)) {
      valueToSet = body;
    }
    // value == null is valid in this tag, don't test

    // Expose this value as a scripting variable
    int inScope = PageContext.PAGE_SCOPE;
    if (toScope != null) {
      inScope = TagUtils.getScope(toScope);
    }

    pageContext.setAttribute(id, valueToSet, inScope);

    // Continue processing this page
    return EVAL_PAGE;
  }

  /**
   * Save the body content of this tag (if any), or throw a JspException if the value was already
   * defined.
   * 
   * @exception JspException
   *              if value was defined by an attribute
   */
  @Override
  public int doAfterBody() throws JspException {

    if (bodyContent != null) {
      body = bodyContent.getString();
      if (body != null) {
        body = body.trim();
        if (body.length() < 1) {
          body = null;
        }
      }
    }
    return (SKIP_BODY);

  }

  /**
   * Release all allocated resources.
   */
  @Override
  public void release() {
    super.release();
    body = null;
    name = null;
    property = null;
    scope = null;
    toScope = "page";
    value = null;
  }

}
