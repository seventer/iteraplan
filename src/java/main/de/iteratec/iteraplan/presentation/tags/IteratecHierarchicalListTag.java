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

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.google.common.collect.Lists;

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.model.interfaces.HierarchicalEntity;
import de.iteratec.iteraplan.model.sorting.HierarchyHelper;


/**
 * Creates a list of all entities in the hierarchie for this entity. The first element will be the root, and the last 
 * element will be the element itself. If the entity is a non hierarchical entity, the list will only contain itself.
 */
public class IteratecHierarchicalListTag extends BodyTagSupport {

  private static final Logger LOGGER   = Logger.getIteraplanLogger(IteratecHierarchicalListTag.class);

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

  /**
   * Check if we need to evaluate the body of the tag
   * 
   * @exception JspException
   *              if a JSP exception has occurred
   */
  @Override
  public int doStartTag() throws JspException {
    return SKIP_BODY;
  }

  /**
   * Retrieve the required property and expose it as a scripting variable.
   *
   * @exception JspException if a JSP exception has occurred
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public int doEndTag() throws JspException {

    // Retrieve the required property value
    Object value = TagUtils.lookup(pageContext, name, property, scope);

    List hierarchicalList;
    if (value instanceof HierarchicalEntity) {
      hierarchicalList = HierarchyHelper.<HierarchicalEntity> createHierarchyList((HierarchicalEntity) value);
    }
    else {
      hierarchicalList = Lists.newArrayList(value);
    }

    // Expose this value as a scripting variable
    int inScope = PageContext.PAGE_SCOPE;
    if (toScope != null) {
      inScope = TagUtils.getScope(toScope);
    }

    pageContext.setAttribute(id, hierarchicalList, inScope);

    // Continue processing this page
    return EVAL_PAGE;
  }

  /**
   * Release all allocated resources.
   */
  @Override
  public void release() {
    super.release();
    name = null;
    property = null;
    scope = null;
    toScope = "page";
  }

}
