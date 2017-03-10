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

import de.iteratec.iteraplan.common.Logger;
import de.iteratec.iteraplan.common.UserContext;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroup;
import de.iteratec.iteraplan.model.attribute.AttributeTypeGroupPermissionEnum;


/**
 * Tag for checking read and write permissions for an attribute type group. 
 * 
 * This tag is used by a JSP to determine whether an attribute type group is
 * visible and whether it can be edited. The tag expects the attribute type group,
 * the UserContext and the type of permission to check. The result is stored in 
 * the specified page-scoped bean and contains either true or false.
 */
public class CheckAttrTypeGroupPermission extends AbstractCheckPermissionTag {
  private static final Logger LOGGER         = Logger.getIteraplanLogger(CheckAttrTypeGroupPermission.class);

  /** Must be either 'read' or 'write' in order to check for read or for write permissions. * */
  private String              permissionType = null;

  public String getPermissionType() {
    return permissionType;
  }

  public void setPermissionType(String permissionType) {
    this.permissionType = permissionType;
    try {
      this.permissionType = TagUtils.evalString("permissionType", this.permissionType, this, pageContext);
    } catch (JspException e) {
      LOGGER.error(e);
    }
  }

  @Override
  public int doStartTag() throws JspException {

    // Look up the user context.
    UserContext uc = (UserContext) pageContext.findAttribute(getUserContext());
    if (uc == null) {
      throw new JspException("Could not find bean: " + getUserContext());
    }

    // Look up the attribute type group.
    Object atg = pageContext.findAttribute(getName());
    if (atg == null) {
      throw new JspException("Bean " + getName() + " was not found.");
    }
    if (!(atg instanceof AttributeTypeGroup)) {
      atg = TagUtils.lookup(pageContext, getName(), getProperty(), null);
    }
    if (!(atg instanceof AttributeTypeGroup)) {
      throw new JspException("Bean " + getName() + " with property " + getProperty() + "does not point to a AttributeTypeGroup.");
    }

    Boolean permissionGranted;
    if ("read".equals(permissionType)) {
      permissionGranted = Boolean.valueOf(uc.getPerms().userHasAttrTypeGroupPermission((AttributeTypeGroup) atg,
          AttributeTypeGroupPermissionEnum.READ));
    }
    else if ("write".equals(permissionType)) {
      permissionGranted = Boolean.valueOf(uc.getPerms().userHasAttrTypeGroupPermission((AttributeTypeGroup) atg,
          AttributeTypeGroupPermissionEnum.READ_WRITE));
    }
    else {
      throw new JspException("The value of permissionType must be either read or write.");
    }

    pageContext.setAttribute(getResult(), permissionGranted, PageContext.PAGE_SCOPE);

    return super.doStartTag();
  }

}
